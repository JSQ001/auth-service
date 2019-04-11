package com.hand.hcf.app.expense.application.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.FormTypeEnum;
import com.hand.hcf.app.expense.application.domain.*;
import com.hand.hcf.app.expense.application.persistence.ApplicationTypeMapper;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeAndUserGroupDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.enums.AssignUserEnum;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;


/**
 * <p>
 *     申请单类型服务类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@Service
public class ApplicationTypeService extends BaseService<ApplicationTypeMapper, ApplicationType> {
    @Autowired
    private ApplicationTypeAssignUserService assignUserService;

    @Autowired
    private ApplicationTypeAssignTypeService assignTypeService;

    @Autowired
    private ApplicationTypeAssignCompanyService assignCompanyService;

    @Autowired
    private ApplicationTypeDimensionService dimensionService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AuthorizeControllerImpl authorizeClient;
    /**
     *  创建一个申请单类型
     * @param typeDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApplicationTypeDTO createApplicationType(ApplicationTypeDTO typeDTO) {

        ApplicationType applicationType = typeDTO.getApplicationType();
        List<ApplicationTypeAssignUser> userInfos = typeDTO.getUserInfos();
        List<ApplicationTypeAssignType> expenseTypeInfos = typeDTO.getExpenseTypeInfos();
        // 逻辑校验和逻辑赋值
        checkUnique(applicationType, userInfos, expenseTypeInfos, true);
        // 保存类型
        applicationType.setId(null);
        this.insert(applicationType);
        // 适用人员 和申请类型
        insertAssignUser(userInfos, applicationType,expenseTypeInfos);
        return typeDTO;
    }


    /**
     * 唯一性校验
     * @param applicationType
     * @param userInfos
     * @param expenseTypeInfos
     */
    private void checkUnique(ApplicationType applicationType,
                             List<ApplicationTypeAssignUser> userInfos,
                             List<ApplicationTypeAssignType> expenseTypeInfos,
                             Boolean isNew){

        // 1、如果关联申请类型不是全部关联，则必须要有关联的申请
        if (applicationType.getAllFlag() == null || !applicationType.getAllFlag()){
            if (CollectionUtils.isEmpty(expenseTypeInfos)){
                throw new BizException(RespCode.EXPENSE_ASSOICATE_TYPE_NOT_EXISTS);
            }
        }
        // 2、如果适用人员不是全部类型，则必须要有关联的人员
        if (!AssignUserEnum.USER_ALL.getKey().equals(applicationType.getApplyEmployee())){
            if (CollectionUtils.isEmpty(userInfos)){
                throw new BizException(RespCode.EXPENSE_ASSOICATE_USER_NOT_EXISTS1);
            }

        }
        if (isNew) {
            ApplicationType queryData = selectOne(new EntityWrapper<ApplicationType>()
                    .eq("type_code",applicationType.getTypeCode())
                    .eq("set_of_books_id",applicationType.getSetOfBooksId())
            );

            if (null != queryData && queryData.getId() != null) {
                throw new BizException(RespCode.EXPENSE_APPLICATION_TYPE_CODE_EXISTS);
            }
        }
        // 不能关联合同则合同为非必输
        if (applicationType.getAssociateContract() == null){
            applicationType.setAssociateContract(false);
        }
        if (!applicationType.getAssociateContract()){
            applicationType.setRequireInput(false);
        }
        if (null == applicationType.getBudgetFlag()){
            applicationType.setBudgetFlag(false);
        }
        // 租户为当前登陆人的租户
        applicationType.setTenantId(OrgInformationUtil.getCurrentTenantId());
    }


    /**
     * 更新费用类型
     * @param typeDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApplicationTypeDTO updateApplicationType(ApplicationTypeDTO typeDTO) {

        ApplicationType applicationType = typeDTO.getApplicationType();
        List<ApplicationTypeAssignUser> userInfos = typeDTO.getUserInfos();
        List<ApplicationTypeAssignType> expenseTypeInfos = typeDTO.getExpenseTypeInfos();
        if (applicationType.getId() == null){
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        // 逻辑校验和逻辑赋值
        checkUnique(applicationType, userInfos, expenseTypeInfos, false);
        // 删除分配的
        assignTypeService.delete(new EntityWrapper<ApplicationTypeAssignType>().eq("application_type_id",applicationType.getId()));

        assignUserService.delete(new EntityWrapper<ApplicationTypeAssignUser>().eq("application_type_id",applicationType.getId()));
        insertAssignUser(userInfos, applicationType,expenseTypeInfos);

        // 保存类型
        this.updateById(applicationType);
        return typeDTO;
    }

    private void insertAssignUser(List<ApplicationTypeAssignUser> userInfos, ApplicationType applicationType, List<ApplicationTypeAssignType> expenseTypeInfos){
        // 1、如果关联申请类型不是全部关联，则必须要有关联的申请
        if (applicationType.getAllFlag() == null || !applicationType.getAllFlag()){
            expenseTypeInfos.forEach(e -> {
                if (e.getExpenseTypeId() == null) {
                    throw new BizException(RespCode.EXPENSE_ASSOICATE_TYPE_NOT_EXISTS);
                }
                e.setApplicationTypeId(applicationType.getId());
            });
            assignTypeService.insertBatch(expenseTypeInfos);
        }
        // 2、如果适用人员不是全部类型，则必须要有关联的人员
        if (!AssignUserEnum.USER_ALL.getKey().equals(applicationType.getApplyEmployee())){
            userInfos.forEach(e -> {
                if (e.getUserTypeId() == null){
                    throw new BizException(RespCode.EXPENSE_ASSIGN_USER_IS_NULL);
                }
                e.setApplicationTypeId(applicationType.getId());
                e.setApplyType(applicationType.getApplyEmployee());
            });
            assignUserService.insertBatch(userInfos);
        }
    }

    /**
     * 添加查询申请单类型
     * @param setOfBooksId
     * @param typeCode
     * @param typeName
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public List<ApplicationType> queryByCondition(Long setOfBooksId, String typeCode, String typeName, Boolean enabled, Page page) {
        ApplicationType queryParams = new ApplicationType(typeCode, setOfBooksId);
        queryParams.setTypeName(typeName);
        queryParams.setEnabled(enabled);

        List<ApplicationType> list = baseMapper.selectPage(page, new EntityWrapper<>(queryParams)
                .orderBy("enabled", false)
                .orderBy("type_code", true));

        if (!CollectionUtils.isEmpty(list)){
            SetOfBooksInfoCO setOfBooksInfoDTO = organizationService.getSetOfBooksInfoCOById(setOfBooksId, false);
            list.forEach(e -> {
                e.setSetOfBooksName(setOfBooksInfoDTO.getSetOfBooksCode() + "-" + setOfBooksInfoDTO.getSetOfBooksName());
                if(!StringUtil.isNullOrEmpty(e.getFormOid())) {
                    ApprovalFormCO approvalFormCO = organizationService.getApprovalFormByOid(e.getFormOid());
                    if (null != approvalFormCO) {
                        e.setFormName(approvalFormCO.getFormName());
                    }
                }
            });
        }
        return list;
    }

    /**
     *  分配公司
     * @param companies
     * @param applicationTypeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignCompanies(List<Long> companies,
                                   Long applicationTypeId) {
        ApplicationType applicationType = this.selectById(applicationTypeId);
        if (applicationType == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(companies)){
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        List<ApplicationTypeAssignCompany> list = new ArrayList<>();
        companies.forEach(u -> {
            ApplicationTypeAssignCompany e = new ApplicationTypeAssignCompany();
            e.setApplicationTypeId(applicationTypeId);
            e.setEnabled(true);
            e.setCompanyId(u);
            list.add(e);
        });
        return assignCompanyService.insertBatch(list);
    }


    /**
     * 查询已经分配的公司
     * @param applicationTypeId
     * @param page
     * @return
     */
    public Page<ApplicationTypeAssignCompany> queryAssignCompanies(Long applicationTypeId, Page<ApplicationTypeAssignCompany> page) {

        Page<ApplicationTypeAssignCompany> selectPage = assignCompanyService.selectPage(page,
                new EntityWrapper<ApplicationTypeAssignCompany>()
                        .eq("application_type_id", applicationTypeId)
                        .orderBy("enabled", false)
                        .orderBy("company_id", true));
        if (CollectionUtils.isEmpty(selectPage.getRecords())){
            return selectPage;
        }else{
            // 查出集合则去查公司名称
            Set<Long> idSet = selectPage.getRecords().stream().map(ApplicationTypeAssignCompany::getCompanyId).collect(Collectors.toSet());
            List<CompanyCO> companySumDTOS = organizationService.listCompaniesByIds(new ArrayList<>(idSet));
            if (CollectionUtils.isEmpty(companySumDTOS)){
                return selectPage;
            }else{
                Map<Long, CompanyCO> companySumDTOMap = companySumDTOS
                        .stream()
                        .collect(Collectors.toMap(CompanyCO::getId, e -> e, (k1, k2) -> k1));
                selectPage.getRecords().forEach(e -> {
                    if (companySumDTOMap.containsKey(e.getCompanyId())){
                        e.setCompanyCode(companySumDTOMap.get(e.getCompanyId()).getCompanyCode());
                        e.setCompanyName(companySumDTOMap.get(e.getCompanyId()).getName());
                        e.setCompanyTypeName(companySumDTOMap.get(e.getCompanyId()).getCompanyTypeName());
                    }
                });
                selectPage.getRecords().sort(Comparator.comparing(ApplicationTypeAssignCompany::getCompanyCode));
            }
        }
        return selectPage;
    }

    /**
     * @Author: bin.xie
     * @Description: 查询当前项目下未被添加的公司
     * @param: setOfBooksId 账套ID
     * @param: acpReqTypesId 借款申请单类型ID
     * @param: companyCode 机构代码
     * @param: companyName 机构名称
     * @param: companyCodeFrom 机构代码从
     * @param: companyCodeTo 机构代码至
     * @param: page 分页
     * @return: java.util.List<com.hand.hcf.app.apply.payment.OrganizationStandardDto>
     * @Date: Created in 2018/1/23 14:03
     * @Modified by
     */
    public Page<CompanyCO> getCompanyByConditionFilter(Long applicationTypeId, String companyCode,
                                                           String companyName, String companyCodeFrom, String companyCodeTo,
                                                           Page page) {
        ApplicationType applicationType = this.selectById(applicationTypeId);
        if (null == applicationType){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<Long> ids = assignCompanyService.selectList(new EntityWrapper<ApplicationTypeAssignCompany>()
                .eq("application_type_id", applicationTypeId))
                .stream()
                .map(ApplicationTypeAssignCompany::getCompanyId)
                .collect(Collectors.toList());

        Page<CompanyCO> result = organizationService.pageCompanyByCond(applicationType.getSetOfBooksId(), companyCode,
                companyName, companyCodeFrom, companyCodeTo, ids, page);

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCompanyEnabled(ApplicationTypeAssignCompany company) {
        if (company.getId() == null){
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }

        ApplicationTypeAssignCompany assignCompany = assignCompanyService.selectById(company.getId());
        if (assignCompany == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        assignCompany.setEnabled(company.getEnabled());
        assignCompany.setVersionNumber(company.getVersionNumber());
        return assignCompanyService.updateById(assignCompany);
    }

    /**
     * 申请单类型分配维度
     * @param applicationTypeId
     * @param dimensions
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ApplicationTypeDimension> assignDimensions(Long applicationTypeId, List<ApplicationTypeDimension> dimensions) {

        if (CollectionUtils.isEmpty(dimensions)){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_IS_NULL);
        }
        // 查询类型是否存在
        ApplicationType applicationType = this.selectById(applicationTypeId);
        if (null == applicationType) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        dimensions.forEach(e -> {
            e.setTypeId(applicationTypeId);
            if (e.getHeaderFlag() == null){
                e.setHeaderFlag(false);
            }
            //维度的默认值允许为空，去掉该校验
//            if (e.getDefaultValue() == null){
//                throw new BizException(RespCode.EXPENSE_DIMENSION_DEFAULT_VALUE_IS_NULL);
//            }
        });
        try{
            dimensionService.insertOrUpdateBatch(dimensions);
        }catch (DataAccessException e){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_DUPLICATE);
        }
        return dimensions;
    }

    public List<ApplicationTypeDimension> queryDimension(Long applicationTypeId, Page<ApplicationTypeDimension> page) {

        List<ApplicationTypeDimension> records = dimensionService.selectPage(page,
                new EntityWrapper<ApplicationTypeDimension>().eq("type_id", applicationTypeId)
                        .orderBy("sequence"))
                .getRecords();

        if (!CollectionUtils.isEmpty(records)){
            // 设置默认值的名称
            List<Long> valueIds = records
                    .stream()
                    .map(ApplicationTypeDimension::getDefaultValue)
                    .collect(Collectors.toList());
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(valueIds);

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            List<Long> dimensionIds = records
                    .stream()
                    .map(ApplicationTypeDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionCO> centerDTOS = organizationService.listDimensionsByIds(dimensionIds);
            Map<Long, String> dimensionNameMap = centerDTOS.
                    stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            records.forEach(e -> {
                if (valueMap.containsKey(e.getDefaultValue())){
                    e.setValueName(valueMap.get(e.getDefaultValue()));
                }

                if (dimensionNameMap.containsKey(e.getDimensionId())){
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }
        return records;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDimension(Long id) {
        dimensionService.deleteById(id);
        return true;
    }

    public List<ApplicationType> queryAllType(Long setOfBooksId,Boolean enabled) {

        List<Long> typeIdList = assignCompanyService.selectList(
                new EntityWrapper<ApplicationTypeAssignCompany>().eq("company_id", OrgInformationUtil.getCurrentCompanyId())
                .eq("enabled",true)
        ).stream().map(ApplicationTypeAssignCompany::getApplicationTypeId).collect(Collectors.toList());
        if (typeIdList.size() == 0) {
            return new ArrayList<>();
        }

        List<ApplicationType> applicationTypes = this.selectList(
                new EntityWrapper<ApplicationType>()
                        .in("id", typeIdList)
                        .eq("set_of_books_id", setOfBooksId == null ? OrgInformationUtil.getCurrentSetOfBookId() : setOfBooksId)
                        .eq(enabled != null,"enabled",enabled));

        //根据当前用户权限筛选单据类型
        applicationTypes = applicationTypes.stream().filter(applicationType -> {
            boolean flag = true;

            List<ApplicationTypeAssignUser> assignUsers = null;
            List<Long> ids = null;
            // 如果不是全部人员就去查询分配的部门或者人员组
            if (!AssignUserEnum.USER_ALL.getKey().equals(applicationType.getApplyEmployee())){
                assignUsers = assignUserService.selectList(
                        new EntityWrapper<ApplicationTypeAssignUser>().eq("application_type_id", applicationType.getId()));
                ids = assignUsers.stream().map(ApplicationTypeAssignUser::getUserTypeId).collect(Collectors.toList());
            } else {
                return true;
            }
            // 部门
            if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(applicationType.getApplyEmployee())){
                if (!CollectionUtils.isEmpty(ids)) {
                    System.out.println(OrgInformationUtil.getCurrentUserOid());
                    DepartmentCO departmentCO = organizationService.getDepartementCOByUserOid(OrgInformationUtil.getCurrentUserOid().toString());
                    if (ids.contains(departmentCO.getId())) {
                        flag = true;
                    } else {
                        flag = false;
                    }

                }
            }
            // 人员组
            if (AssignUserEnum.USER_GROUP.getKey().equals(applicationType.getApplyEmployee())){
                if (!CollectionUtils.isEmpty(ids)) {
                    JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(ids).userId(OrgInformationUtil.getCurrentUserId()).build();
                    if (organizationService.judgeUserInUserGroups(judgeUserCO)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
            }

            return flag;
        }).collect(Collectors.toList());

        applicationTypes.addAll(listAuthorizedApplicationType());
        //根据ID去重
        applicationTypes = applicationTypes.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(ApplicationType::getId))), ArrayList::new)
        );

        return applicationTypes;
    }

    /**
     * 获取当前用户被授权的单据类型
     * @return
     */
    public List<ApplicationType> listAuthorizedApplicationType(){
        List<ApplicationType> applicationTypeList = new ArrayList<>();

        List<FormAuthorizeCO> formAuthorizeCOList = authorizeClient.listFormAuthorizeByDocumentCategoryAndUserId(FormTypeEnum.EXPENSE_REQUISITION.getCode(), OrgInformationUtil.getCurrentUserId());

        for(FormAuthorizeCO item : formAuthorizeCOList) {
            OrganizationUserCO contactCO = new OrganizationUserCO();
            if (item.getMandatorId() != null) {
                contactCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
            }
            List<Long> typeIdList = assignCompanyService.selectList(
                    new EntityWrapper<ApplicationTypeAssignCompany>()
                            .eq(item.getCompanyId() != null, "company_id", item.getCompanyId())
                            .eq(contactCO.getCompanyId() != null, "company_id", contactCO.getCompanyId())
                            .eq("enabled",true)
            ).stream().map(ApplicationTypeAssignCompany::getApplicationTypeId).collect(Collectors.toList());
            if (typeIdList.size() == 0) {
                continue;
            }
            List<ApplicationType> applicationTypes = this.selectList(
                    new EntityWrapper<ApplicationType>()
                            .in(typeIdList.size() != 0, "id", typeIdList)
                            .eq(item.getFormId() != null, "id", item.getFormId())
                            .eq("enabled", true));

            applicationTypes = applicationTypes.stream().filter(applicationType -> {
                List<ApplicationTypeAssignUser> assignUsers;
                List<Long> ids;
                // 如果不是全部人员就去查询分配的部门或者人员组
                if (!AssignUserEnum.USER_ALL.getKey().equals(applicationType.getApplyEmployee())){
                    assignUsers = assignUserService.selectList(
                            new EntityWrapper<ApplicationTypeAssignUser>().eq("application_type_id", applicationType.getId()));
                    ids = assignUsers.stream().map(ApplicationTypeAssignUser::getUserTypeId).collect(Collectors.toList());
                } else {
                    return true;
                }
                // 部门
                if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(applicationType.getApplyEmployee())){
                    if (!CollectionUtils.isEmpty(ids)) {

                        if (item.getMandatorId() != null) {
                            OrganizationUserCO userCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
                            if (!ids.contains(userCO.getDepartmentId())){
                                return false;
                            }
                        }

                        if (item.getUnitId() != null && !ids.contains(item.getUnitId())) {
                            return false;
                        }
                    }
                }
                // 人员组
                if (AssignUserEnum.USER_GROUP.getKey().equals(applicationType.getApplyEmployee())){
                    if (!CollectionUtils.isEmpty(ids)) {

                        if (item.getMandatorId() != null) {
                            JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(ids).userId(item.getMandatorId()).build();
                            if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                return false;
                            }
                        }

                        if (item.getUnitId() != null){
                            List<Long> userIds = organizationService.listUsersByDepartmentId(item.getUnitId()).stream().map(ContactCO::getId).collect(Collectors.toList());
                            for(Long e : userIds){
                                JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(ids).userId(e).build();
                                if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toList());

            applicationTypeList.addAll(applicationTypes);
        }
        return applicationTypeList;
    }

    public List<ApplicationType> queryCreatedType(Long setOfBooksId,Boolean enabled) {

        return baseMapper.queryCreatedType(setOfBooksId, enabled);
    }

    public List<ApplicationType> queryByUser() {
        //获取当前员工ID
        UUID userId = OrgInformationUtil.getCurrentUserOid();

        DepartmentCO department = organizationService.getDepartementCOByUserOid(userId.toString());
        Long departmentId = -1L;
        if (department != null){
            departmentId = department.getId();
        }
        Long companyId = OrgInformationUtil.getCurrentCompanyId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        // 先查询权限为全部人员和当前用户所属的部门的单据类型
        List<ApplicationType> applicationTypes = baseMapper.selectByUser(departmentId, companyId, setOfBooksId);

        // 然后查询权限为人员组的单据类型
        List<ApplicationTypeAndUserGroupDTO> list = baseMapper.selectByUserGroup(setOfBooksId, companyId);
        if (!CollectionUtils.isEmpty(list)) {
            for (ApplicationTypeAndUserGroupDTO type : list) {
                JudgeUserCO judgeUserCO = new JudgeUserCO();
                judgeUserCO.setIdList(type.getUserGroupIds());
                judgeUserCO.setUserId(OrgInformationUtil.getCurrentUserId());
                Boolean isExists = organizationService.judgeUserInUserGroups(judgeUserCO);
                if (isExists) {
                    applicationTypes.add(type);
                }
            }
        }
        applicationTypes.addAll(listAuthorizedApplicationType());
        //根据ID去重
        applicationTypes = applicationTypes.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(ApplicationType::getId))), ArrayList::new)
        );
        return applicationTypes;
    }

    public ApplicationTypeDTO getTypeForUpdate(Long id) {
        ApplicationTypeDTO dto = new ApplicationTypeDTO();
        ApplicationType applicationType = this.selectById(id);
        if (applicationType == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        //账套名称
        SetOfBooksInfoCO setOfBooksInfoDTO = organizationService.getSetOfBooksInfoCOById(applicationType.getSetOfBooksId(), true);
        applicationType.setSetOfBooksName(setOfBooksInfoDTO.getSetOfBooksCode() + "-" + setOfBooksInfoDTO.getSetOfBooksName());
        List<ApplicationTypeAssignUser> assignUsers = null;
        List<Long> ids = null;
        // 如果不是全部人员就去查询分配的部门或者人员组
        if (!AssignUserEnum.USER_ALL.getKey().equals(applicationType.getApplyEmployee())){
            assignUsers = assignUserService.selectList(
                    new EntityWrapper<ApplicationTypeAssignUser>().eq("application_type_id", applicationType.getId()));
            ids = assignUsers.stream().map(ApplicationTypeAssignUser::getUserTypeId).collect(Collectors.toList());
        }else{
            dto.setUserInfos(new ArrayList<>());
        }
        // 部门
        if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(applicationType.getApplyEmployee())){
            if (!CollectionUtils.isEmpty(ids)) {
                // 获取所有的部门
                List<DepartmentCO> departmentInfoDTOS = organizationService.listDepartmentsByIds(ids);

                Map<Long, String> collect = departmentInfoDTOS
                        .stream()
                        .collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));

                assignUsers.forEach(e -> {
                    if (collect.containsKey(e.getUserTypeId())) {
                        e.setPathOrName(collect.get(e.getUserTypeId()));
                    }
                });
            }
        }
        // 人员组
        if (AssignUserEnum.USER_GROUP.getKey().equals(applicationType.getApplyEmployee())){
            if (!CollectionUtils.isEmpty(ids)) {
                // 获取所有的人员组
                List<UserGroupCO> userGroupDTOS = organizationService.listUserGroupsByIds(ids);

                Map<Long, String> collect = userGroupDTOS
                        .stream()
                        .collect(Collectors.toMap(UserGroupCO::getId, UserGroupCO::getName, (k1, k2) -> k1));

                assignUsers.forEach(e -> {
                    if (collect.containsKey(e.getUserTypeId())) {
                        e.setPathOrName(collect.get(e.getUserTypeId()));
                    }
                });
            }
        }
        if (applicationType.getAllFlag() == null || !applicationType.getAllFlag()){
            List<ApplicationTypeAssignType> assignTypes = assignTypeService.listByApplicationTypeId(applicationType.getId());
            dto.setExpenseTypeInfos(assignTypes);
        }else{
            dto.setExpenseTypeInfos(new ArrayList<>());
        }
        dto.setApplicationType(applicationType);
        dto.setUserInfos(assignUsers);
        return dto;
    }

    public ApplicationTypeDimensionDTO queryTypeAndDimensionById(Long id, boolean isHeader) {
        ApplicationType applicationType = this.selectById(id);
        if (applicationType == null){
            throw new BizException(RespCode.EXPENSE_APPLICATION_TYPE_IS_NUTT);
        }
        List<ApplicationTypeDimension> typeDimensions = dimensionService.selectList(
                new EntityWrapper<ApplicationTypeDimension>()
                        .eq("type_id", applicationType.getId())
                        .orderBy("sequence", true));

        ApplicationTypeDimensionDTO dto = new ApplicationTypeDimensionDTO();
        BeanUtils.copyProperties(applicationType, dto);
        List<ExpenseDimension> dimensions = new ArrayList<>();
        // 根据维度ID查询相关维度信息
        if (!CollectionUtils.isEmpty(typeDimensions)){
            List<Long> ids = typeDimensions.stream().map(ApplicationTypeDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionDetailCO> dimensionDetails = organizationService.listDetailCOByDimensionIdsAndCompany(
                    OrgInformationUtil.getCurrentCompanyId(),
                    OrgInformationUtil.getCurrentDepartmentId(),
                    OrgInformationUtil.getCurrentUserId(),
                    Boolean.TRUE,
                    ids);
            Map<Long, DimensionDetailCO> dimensionDetailMap = dimensionDetails
                    .stream()
                    .collect(Collectors.toMap(DimensionDetailCO::getId, e -> e, (k1, k2) -> k1));

            for (int i = 1; i <= typeDimensions.size(); i++) {
                ApplicationTypeDimension typeDimension = typeDimensions.get(i -1);
                // 查询所有的维度，只有当是位置是头的时候才返回，以便确认 dimension*Id
                ExpenseDimension expenseDimension = new ExpenseDimension();
                expenseDimension.setDimensionId(typeDimension.getDimensionId());
                expenseDimension.setHeaderFlag(typeDimension.getHeaderFlag());
                expenseDimension.setSequence(typeDimension.getSequence());
                expenseDimension.setRequiredFlag(typeDimension.getRequiredFlag());
                if (dimensionDetailMap.containsKey(typeDimension.getDimensionId())) {
                    DimensionDetailCO detailCO = dimensionDetailMap.get(typeDimension.getDimensionId());
                    expenseDimension.setDimensionField("dimension" + detailCO.getDimensionSequence() + "Id");
                    Map<Long, DimensionItemCO> collect = detailCO
                            .getSubDimensionItemCOS()
                            .stream()
                            .collect(Collectors.toMap(DimensionItemCO::getId, e -> e));
                    expenseDimension.setName(detailCO.getDimensionName());
                    expenseDimension.setOptions(detailCO.getSubDimensionItemCOS());
                    if (collect.containsKey(typeDimension.getDefaultValue())) {
                        DimensionItemCO itemCO = collect.get(typeDimension.getDefaultValue());
                        expenseDimension.setValue(itemCO.getId());
                        expenseDimension.setValueName(itemCO.getDimensionItemName());
                    }else{
                        expenseDimension.setValue(null);
                        expenseDimension.setValueName(null);
                    }
                }else{
                    expenseDimension.setOptions(new ArrayList<>());
                }
                if (expenseDimension.getHeaderFlag() && isHeader) {
                    dimensions.add(expenseDimension);
                }
                if (!isHeader){
                    dimensions.add(expenseDimension);
                }
            }
            dto.setDimensions(dimensions);
        }
        return dto;
    }

    public List<ExpenseTypeWebDTO> queryExpenseTypeByApplicationTypeId(Long applicationTypeId,
                                                                       Long categoryId,
                                                                       String expenseTypeName,
                                                                       Page page) {
        ApplicationType applicationType = this.selectById(applicationTypeId);

        List<ExpenseTypeWebDTO> expenseTypeWebDTOS = assignTypeService.queryExpenseTypeByApplicationTypeId(applicationType, categoryId, expenseTypeName, page);

        expenseTypeWebDTOS.forEach(field -> {
            if (CollectionUtils.isEmpty(field.getFieldList())){
                field.setFields(new ArrayList<>());
            }else{
                List<ExpenseFieldDTO> fieldDTOS = field.getFieldList().stream().map(e -> {
                    ExpenseFieldDTO fieldDTO = ExpenseFieldDTO.builder()
                            .commonField(e.getCommonField())
                            .customEnumerationOid(e.getCustomEnumerationOid())
                            .defaultValueConfigurable(e.getDefaultValueConfigurable())
                            .defaultValueKey(e.getDefaultValueKey())
                            .defaultValueMode(e.getDefaultValueMode())
                            .fieldDataType(e.getFieldDataType())
                            .fieldOid(e.getFieldOid())
                            .fieldType(e.getFieldType())
                            .i18n(e.getI18n())
                            .mappedColumnId(e.getMappedColumnId())
                            .messageKey(e.getMessageKey())
                            .name(e.getName())
                            .id(e.getId())
                            .printHide(e.getPrintHide())
                            .reportKey(e.getReportKey())
                            .required(e.getRequired())
                            .sequence(e.getSequence())
                            .value(null)
                            .editable(e.getEditable())
                            .showOnList(e.getShowOnList())
                            .showValue(null)
                            .build();
                    if (FieldType.CUSTOM_ENUMERATION.getId().equals(e.getFieldTypeId())){
                        // 为值列表，则设置值列表的相关值
                        List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(e.getCustomEnumerationOid());

                        List<OptionDTO> options = sysCodeValueCOS.stream().map(OptionDTO::createOption).collect(toList());
                        fieldDTO.setOptions(options);
                    }
                    return fieldDTO;
                }).collect(Collectors.toList());
                field.setFields(fieldDTOS);
            }
        });

        return expenseTypeWebDTOS;
    }

    //typeflag标识0为申请单,1为报销单
    public List<ExpenseTypeWebDTO> queryExpenseTypeBySetOfBooksId(Long setOfBooksId, Long id,Long typeCategoryId, String expenseTypeName,Integer typeFlag, Page page) {
        List<ExpenseTypeWebDTO> expenseTypeWebDTOS = assignTypeService.queryExpenseTypeBySetOfBooksIdAndId(setOfBooksId,id,typeCategoryId,expenseTypeName,typeFlag,page);

        expenseTypeWebDTOS.forEach(field -> {
            if (CollectionUtils.isEmpty(field.getFieldList())){
                field.setFields(new ArrayList<>());
            }else{
                List<ExpenseFieldDTO> fieldDTOS = field.getFieldList().stream().map(e -> {
                    ExpenseFieldDTO fieldDTO = ExpenseFieldDTO.builder()
                            .commonField(e.getCommonField())
                            .customEnumerationOid(e.getCustomEnumerationOid())
                            .defaultValueConfigurable(e.getDefaultValueConfigurable())
                            .defaultValueKey(e.getDefaultValueKey())
                            .defaultValueMode(e.getDefaultValueMode())
                            .fieldDataType(e.getFieldDataType())
                            .fieldOid(e.getFieldOid())
                            .fieldType(e.getFieldType())
                            .i18n(e.getI18n())
                            .mappedColumnId(e.getMappedColumnId())
                            .messageKey(e.getMessageKey())
                            .name(e.getName())
                            .id(e.getId())
                            .printHide(e.getPrintHide())
                            .reportKey(e.getReportKey())
                            .required(e.getRequired())
                            .sequence(e.getSequence())
                            .value(null)
                            .editable(e.getEditable())
                            .showOnList(e.getShowOnList())
                            .showValue(null)
                            .build();
                    if (FieldType.CUSTOM_ENUMERATION.getId().equals(e.getFieldTypeId())){
                        // 为值列表，则设置值列表的相关值
                        List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(e.getCustomEnumerationOid());
                        List<OptionDTO> options = sysCodeValueCOS.stream().map(OptionDTO::createOption).collect(toList());
                        fieldDTO.setOptions(options);
                    }
                    return fieldDTO;
                }).collect(Collectors.toList());
                field.setFields(fieldDTOS);
            }
        });

        return expenseTypeWebDTOS;
    }


    /**
     * 报账单表单关联申请单类型LOV查询
     * @param setOfBooksId
     * @param range
     * @param typeName
     * @param ids
     * @param page
     * @return
     */
    public List<ApplicationType> listTypesByReportCondition(Long setOfBooksId, String range, String typeName, List<Long> ids, Page page) {

        return baseMapper.listTypesByReportCondition(setOfBooksId, range, typeName, ids, page);
    }

    /**
     * 根据ID查询 费用申请单类型数据
     *
     * @param typeId
     * @return
     */
    public ApplicationType getApplicationTypeById(Long typeId){
        //返回 费用申请单类型数据
        ApplicationType applicationType = baseMapper.selectById(typeId);
        if (applicationType == null){
            throw new BizException(RespCode.EXPENSE_APPLICATION_TYPE_IS_NUTT);
        }
        return applicationType;
    }

    public List<ApplicationTypeDimension> queryDimensionByTypeIdAndCompanyId(Long applicationTypeId, Long companyId) {

        if (assignCompanyService.selectList(
                new EntityWrapper<ApplicationTypeAssignCompany>().eq("application_type_id", applicationTypeId).eq("company_id", companyId)
        ).size() == 0) {
            return new ArrayList<>();
        }
        List<DimensionCO> dimensionCOList = organizationService.listDimensionsByCompanyId(companyId);
        if (dimensionCOList.size() == 0) {
            return new ArrayList<>();
        }
        List<ApplicationTypeDimension> typeDimensionList = dimensionService.selectList(
                new EntityWrapper<ApplicationTypeDimension>().eq("type_id", applicationTypeId).in("dimension_id",dimensionCOList.stream().map(DimensionCO::getId).collect(Collectors.toList()))
        );
        if (!CollectionUtils.isEmpty(typeDimensionList)){
            // 设置默认值的名称
            List<Long> valueIds = typeDimensionList
                    .stream()
                    .map(ApplicationTypeDimension::getDefaultValue)
                    .collect(Collectors.toList());
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(valueIds);

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            Map<Long, String> dimensionNameMap = dimensionCOList.
                    stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            typeDimensionList.stream().forEach(e -> {
                if (valueMap.containsKey(e.getDefaultValue())){
                    e.setValueName(valueMap.get(e.getDefaultValue()));
                }

                if (dimensionNameMap.containsKey(e.getDimensionId())){
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }
        return typeDimensionList;
    }

    public List<DimensionCO> listDimensionByConditionFilter(Long applicationTypeId,
                                                            Long setOfBooksId,
                                                            String dimensionCode,
                                                            String dimensionName,
                                                            Boolean enabled){
        List<Long> ids = dimensionService.selectList(new EntityWrapper<ApplicationTypeDimension>()
                .eq("type_id", applicationTypeId))
                .stream()
                .map(ApplicationTypeDimension::getDimensionId)
                .collect(Collectors.toList());
        List<DimensionCO> result = organizationService.listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, dimensionCode, dimensionName, enabled, ids);
        return result;
    }

    public List<ContactCO> listUsersByApplicationType(Long id, String userCode, String userName, Page queryPage) {
        List<ContactCO> userCOList = new ArrayList<>();

        ApplicationType applicationType = this.getApplicationTypeById(id);
        if (applicationType == null){
            return userCOList;
        }

        //查出有该单据权限的所有公司的id
        List<Long> companyIdList = assignCompanyService.selectList(
                new EntityWrapper<ApplicationTypeAssignCompany>()
                        .eq("enabled", true)
                        .eq("application_type_id", id)
        ).stream().map(ApplicationTypeAssignCompany::getCompanyId).collect(toList());

        if (companyIdList.size() == 0){
            return userCOList;
        }

        List<Long> departmentIdList = null;
        List<Long> userGroupIdList = null;

        // 部门
        if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(applicationType.getApplyEmployee())){
            departmentIdList = assignUserService.selectList(
                    new EntityWrapper<ApplicationTypeAssignUser>()
                            .eq("application_type_id", applicationType.getId())
            ).stream().map(ApplicationTypeAssignUser::getUserTypeId).collect(Collectors.toList());
        }
        // 人员组
        if (AssignUserEnum.USER_GROUP.getKey().equals(applicationType.getApplyEmployee())){
            userGroupIdList = assignUserService.selectList(
                    new EntityWrapper<ApplicationTypeAssignUser>()
                            .eq("application_type_id", applicationType.getId())
            ).stream().map(ApplicationTypeAssignUser::getUserTypeId).collect(Collectors.toList());
        }

        AuthorizeQueryCO queryCO = AuthorizeQueryCO
                .builder()
                .documentCategory(FormTypeEnum.EXPENSE_REQUISITION.getCode())
                .formTypeId(id)
                .companyIdList(companyIdList)
                .departmentIdList(departmentIdList)
                .userGroupIdList(userGroupIdList)
                .currentUserId(OrgInformationUtil.getCurrentUserId())
                .build();
        Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage);
        queryPage.setTotal(contactCOPage.getTotal());
        userCOList = contactCOPage.getRecords();

        return userCOList;
    }

    public Page<ApplicationTypeCO> queryApplicationTypeByCond(ApplicationTypeForOtherCO applicationTypeForOtherCO,Page page){
        Page<ApplicationTypeCO> result = new Page<>();
        List<ApplicationTypeCO> applicationTypeCOS = new ArrayList<>();

        List<ApplicationType> applicationTypeList = baseMapper.queryApplicationTypeByCond(
                applicationTypeForOtherCO.getSetOfBooksId(),
                applicationTypeForOtherCO.getRange(),
                applicationTypeForOtherCO.getCode(),
                applicationTypeForOtherCO.getName(),
                applicationTypeForOtherCO.getEnabled(),
                applicationTypeForOtherCO.getIdList(),
                page
        );

        if (!CollectionUtils.isEmpty(applicationTypeList)){
            applicationTypeList.forEach(applicationType -> {
                ApplicationTypeCO applicationTypeCO = ApplicationTypeCO.builder()
                        .id(applicationType.getId())
                        .typeCode(applicationType.getTypeCode())
                        .typeName(applicationType.getTypeName())
                        .enabled(applicationType.getEnabled())
                        .build();
                applicationTypeCOS.add(applicationTypeCO);
            });
        }
        result.setRecords(applicationTypeCOS);
        result.setTotal(page.getTotal());
        return result;
    }
}
