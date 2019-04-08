package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.expense.report.dto.DepartmentOrUserGroupDTO;
import com.hand.hcf.app.expense.travel.domain.*;
import com.hand.hcf.app.expense.travel.dto.TravelApplicationTypeDTO;
import com.hand.hcf.app.expense.travel.enums.VisibleUserScopeEnum;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationTypeMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Service
public class TravelApplicationTypeService extends BaseService<TravelApplicationTypeMapper, TravelApplicationType> {

    @Autowired
    private TravelApplicationTypeAssignTypeService assignTypeService;
    @Autowired
    private TravelApplicationTypeAssignDepartmentService assignDepartmentService;
    @Autowired
    private TravelApplicationTypeAssignUserGroupService assignUserGroupService;
    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private TravelApplicationTypeAssignDimensionService assignDimensionService;
    @Autowired
    private TravelApplicationTypeAssignCompanyService assignCompanyService;

    /**
     * 新增
     * @param typeDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationType createTravelApplicationType(TravelApplicationTypeDTO typeDTO) {
        TravelApplicationType travelApplicationType = mapperFacade.map(typeDTO, TravelApplicationType.class);
        travelApplicationType.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (travelApplicationType.getId() != null) {
            throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
        }
        if (baseMapper.selectCount(new EntityWrapper<TravelApplicationType>()
                        .eq("set_of_books_id",travelApplicationType.getSetOfBooksId())
                        .eq("code", travelApplicationType.getCode())
        ) > 0) {
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_CODE_REPEAT);
        }
        baseMapper.insert(travelApplicationType);

        //插入关联申请类型
        if (!travelApplicationType.getAllTypeFlag()) {
            List<TravelApplicationTypeAssignType> assignTypeList = new ArrayList<>();
            typeDTO.getRequisitionTypeList().stream().forEach(e ->
                assignTypeList.add(TravelApplicationTypeAssignType.builder().typeId(travelApplicationType.getId()).requisitionTypeId(e.getRequisitionTypeId()).build())
            );
            assignTypeService.insertBatch(assignTypeList);
        }

        //插入关联部门或人员组
        if (travelApplicationType.getVisibleUserScope().equals(VisibleUserScopeEnum.DEPARTMENT.getId())) {
            List<TravelApplicationTypeAssignDepartment> assignDepartmentList = new ArrayList<>();
            typeDTO.getDeptOrUserGroupList().stream().forEach(e ->
                    assignDepartmentList.add(TravelApplicationTypeAssignDepartment.builder().typeId(travelApplicationType.getId()).departmentId(e.getId()).build())
            );
            assignDepartmentService.insertBatch(assignDepartmentList);
        } else if (travelApplicationType.getVisibleUserScope().equals(VisibleUserScopeEnum.USER_GROUP.getId())) {
            List<TravelApplicationTypeAssignUserGroup> assignUserGroupList = new ArrayList<>();
            typeDTO.getDeptOrUserGroupList().stream().forEach(e ->
                    assignUserGroupList.add(TravelApplicationTypeAssignUserGroup.builder().typeId(travelApplicationType.getId()).userGroupId(e.getId()).build())
            );
            assignUserGroupService.insertBatch(assignUserGroupList);
        }

        return travelApplicationType;
    }

    /**
     * 更新
     * @param typeDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationType updateTravelApplicationType(TravelApplicationTypeDTO typeDTO) {
        TravelApplicationType travelApplicationType = mapperFacade.map(typeDTO, TravelApplicationType.class);
        if (travelApplicationType.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        if (baseMapper.selectCount(new EntityWrapper<TravelApplicationType>()
                .eq("set_of_books_id", travelApplicationType.getSetOfBooksId())
                .ne("id", travelApplicationType.getId())
                .eq("code", travelApplicationType.getCode())
        ) > 0) {
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_CODE_REPEAT);
        }
        baseMapper.updateById(travelApplicationType);

        //删除关联申请类型
        assignTypeService.delete(new EntityWrapper<TravelApplicationTypeAssignType>().eq("type_id", travelApplicationType.getId()));

        //插入关联申请类型
        if (!travelApplicationType.getAllTypeFlag()) {
            List<TravelApplicationTypeAssignType> assignTypeList = new ArrayList<>();
            typeDTO.getRequisitionTypeList().stream().forEach(e ->
                    assignTypeList.add(TravelApplicationTypeAssignType.builder().typeId(travelApplicationType.getId()).requisitionTypeId(e.getRequisitionTypeId()).build())
            );
            assignTypeService.insertBatch(assignTypeList);
        }

        //删除关联部门或人员组
        assignDepartmentService.delete(new EntityWrapper<TravelApplicationTypeAssignDepartment>().eq("type_id", travelApplicationType.getId()));
        assignUserGroupService.delete(new EntityWrapper<TravelApplicationTypeAssignUserGroup>().eq("type_id", travelApplicationType.getId()));

        //插入关联部门或人员组
        if (travelApplicationType.getVisibleUserScope().equals(VisibleUserScopeEnum.DEPARTMENT.getId())) {
            List<TravelApplicationTypeAssignDepartment> assignDepartmentList = new ArrayList<>();
            typeDTO.getDeptOrUserGroupList().stream().forEach(e ->
                    assignDepartmentList.add(TravelApplicationTypeAssignDepartment.builder().typeId(travelApplicationType.getId()).departmentId(e.getId()).build())
            );
            assignDepartmentService.insertBatch(assignDepartmentList);
        } else if (travelApplicationType.getVisibleUserScope().equals(VisibleUserScopeEnum.USER_GROUP.getId())) {
            List<TravelApplicationTypeAssignUserGroup> assignUserGroupList = new ArrayList<>();
            typeDTO.getDeptOrUserGroupList().stream().forEach(e ->
                    assignUserGroupList.add(TravelApplicationTypeAssignUserGroup.builder().typeId(travelApplicationType.getId()).userGroupId(e.getId()).build())
            );
            assignUserGroupService.insertBatch(assignUserGroupList);
        }

        return travelApplicationType;
    }

    /**
     * 查询
     * @param id
     * @return
     */
    public TravelApplicationTypeDTO getTravelApplicationTypeById(Long id) {
        TravelApplicationType travelApplicationType = baseMapper.selectById(id);
        travelApplicationType.setI18n(baseI18nService.getI18nMap(TravelApplicationType.class, id));
        return toDTO(travelApplicationType);
    }

    /**
     * 分页条件查询
     * @param setOfBooksId
     * @param travelCode
     * @param travelName
     * @param enabled
     * @param queryPage
     * @return
     */
    public List<TravelApplicationTypeDTO> pageTravelApplicationTypeByCondition(Long setOfBooksId, String travelCode, String travelName, Boolean enabled, Page queryPage) {
        List<TravelApplicationType> typeList = baseMapper.selectPage(queryPage,
                new EntityWrapper<TravelApplicationType>()
                        .eq("set_of_books_id", setOfBooksId)
                        .like(!StringUtil.isNullOrEmpty(travelCode), "code", travelCode)
                        .like(!StringUtil.isNullOrEmpty(travelName), "name", travelName)
                        .eq(enabled != null, "enabled", enabled)
        );
        return toDTOs(typeList);
    }

    private TravelApplicationTypeDTO toDTO(TravelApplicationType travelApplicationType) {
        TravelApplicationTypeDTO typeDTO = mapperFacade.map(travelApplicationType, TravelApplicationTypeDTO.class);
        SetOfBooksInfoCO setOfBooksInfoCO = organizationService.getSetOfBooksById(typeDTO.getSetOfBooksId());
        //账套
        if (setOfBooksInfoCO != null) {
            typeDTO.setSetOfBooksCode(setOfBooksInfoCO.getSetOfBooksCode());
            typeDTO.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
        }
        //表单类型
        /**
         * OrganizationService organizationService 中此方法listApprovalFormsByIds已被注释 by chenxu
         */
      /*  List<ApprovalFormCO> approvalFormCOList = organizationService.listApprovalFormsByIds(Arrays.asList(typeDTO.getFormId()));
        if (approvalFormCOList.size() > 0) {
            typeDTO.setFormName(approvalFormCOList.get(0).getFormName());
        }*/
        //关联申请类型
        if (!typeDTO.getAllTypeFlag()) {
            List<Long> idList = assignTypeService.selectList(
                    new EntityWrapper<TravelApplicationTypeAssignType>()
                            .eq("type_id", travelApplicationType.getId())
            ).stream().map(TravelApplicationTypeAssignType::getRequisitionTypeId).collect(Collectors.toList());
            Map<Long, String> expenseTypeMap = expenseTypeService.selectBatchIds(idList).stream().collect(Collectors.toMap(ExpenseType::getId, ExpenseType::getName, (e1, e2) -> e1));
            List<TravelApplicationTypeAssignType> assignTypeList = new ArrayList<>();
            idList.stream().forEach(id -> assignTypeList.add(TravelApplicationTypeAssignType.builder().requisitionTypeId(id).requisitionTypeName(expenseTypeMap.get(id)).build()));
            typeDTO.setRequisitionTypeList(assignTypeList);
        }
        //关联可见人员
        if (typeDTO.getVisibleUserScope().equals(VisibleUserScopeEnum.DEPARTMENT.getId())) {
            List<Long> idList = assignDepartmentService.selectList(
                    new EntityWrapper<TravelApplicationTypeAssignDepartment>()
                            .eq("type_id", travelApplicationType.getId())
            ).stream().map(TravelApplicationTypeAssignDepartment::getDepartmentId).collect(Collectors.toList());
            Map<Long, String> departmentMap = organizationService.listDepartmentsByIds(idList).stream().collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (e1, e2) -> e1));
            List<DepartmentOrUserGroupDTO> departmentList = new ArrayList<>();
            idList.stream().forEach(id -> departmentList.add(DepartmentOrUserGroupDTO.builder().id(id).name(departmentMap.get(id)).build()));
            typeDTO.setDeptOrUserGroupList(departmentList);
        } else if (typeDTO.getVisibleUserScope().equals(VisibleUserScopeEnum.USER_GROUP.getId())) {
            List<Long> idList = assignUserGroupService.selectList(
                    new EntityWrapper<TravelApplicationTypeAssignUserGroup>()
                            .eq("type_id", travelApplicationType.getId())
            ).stream().map(TravelApplicationTypeAssignUserGroup::getUserGroupId).collect(Collectors.toList());
            Map<Long, String> userGroupMap = organizationService.listUserGroupsByIds(idList).stream().collect(Collectors.toMap(UserGroupCO::getId, UserGroupCO::getName, (e1, e2) -> e1));
            List<DepartmentOrUserGroupDTO> userGroupList = new ArrayList<>();
            idList.stream().forEach(id -> userGroupList.add(DepartmentOrUserGroupDTO.builder().id(id).name(userGroupMap.get(id)).build()));
            typeDTO.setDeptOrUserGroupList(userGroupList);
        }
        return typeDTO;
    }

    private List<TravelApplicationTypeDTO> toDTOs(List<TravelApplicationType> typeList) {
        List<TravelApplicationTypeDTO> typeDTOList = new ArrayList<>();
        for (TravelApplicationType type: typeList) {
            typeDTOList.add(toDTO(type));
        }
        return typeDTOList;
    }

    public TravelApplicationTypeDimensionDTO queryTypeAndDimensionById(Long id, boolean isHeader) {
        TravelApplicationType travelApplicationType = this.selectById(id);
        if (travelApplicationType == null){
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_NOT_EXISTS);
        }
        List<TravelApplicationTypeAssignDimension> typeDimensions = assignDimensionService.selectList(
                new EntityWrapper<TravelApplicationTypeAssignDimension>()
                        .eq("type_id", travelApplicationType.getId())
                        .orderBy("sequence", true));

        TravelApplicationTypeDimensionDTO dto = new TravelApplicationTypeDimensionDTO();
        BeanUtils.copyProperties(travelApplicationType, dto);
        List<ExpenseDimension> dimensions = new ArrayList<>();
        // 根据维度ID查询相关维度信息
        if (!CollectionUtils.isEmpty(typeDimensions)){
            List<Long> ids = typeDimensions.stream().map(TravelApplicationTypeAssignDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionDetailCO> dimensionDetails = organizationService.listDetailCOByDimensionIdsAndCompany(OrgInformationUtil.getCurrentCompanyId(), true, ids);
            Map<Long, DimensionDetailCO> dimensionDetailMap = dimensionDetails
                    .stream()
                    .collect(Collectors.toMap(DimensionDetailCO::getId, e -> e, (k1, k2) -> k1));

            for (int i = 1; i <= typeDimensions.size(); i++) {
                TravelApplicationTypeAssignDimension typeDimension = typeDimensions.get(i -1);
                // 查询所有的维度，只有当是位置是头的时候才返回，以便确认 dimension*Id
                ExpenseDimension expenseDimension = new ExpenseDimension();
                expenseDimension.setDimensionId(typeDimension.getDimensionId());
                expenseDimension.setHeaderFlag(typeDimension.getHeaderFlag());
                expenseDimension.setSequence(typeDimension.getSequence());
                expenseDimension.setRequiredFlag(typeDimension.getRequiredFlag());
                if (dimensionDetailMap.containsKey(typeDimension.getDimensionId())) {
                    DimensionDetailCO detailCO = dimensionDetailMap.get(typeDimension.getDimensionId());
                    Map<Long, DimensionItemCO> collect = detailCO
                            .getSubDimensionItemCOS()
                            .stream()
                            .collect(Collectors.toMap(DimensionItemCO::getId, e -> e));
                    expenseDimension.setDimensionField("dimension" + detailCO.getDimensionSequence() + "Id");
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

    public List<TravelApplicationType> queryCreatedType(Long setOfBooksId, Boolean enabled) {

        List<TravelApplicationType> applicationTypes = baseMapper.queryCreatedType(setOfBooksId, enabled);

        return applicationTypes;
    }

    public List<ExpenseTypeWebDTO> queryExpenseTypeByApplicationTypeId(Long applicationTypeId, Long categoryId, String expenseTypeName, Page page) {
        TravelApplicationType applicationType = this.selectById(applicationTypeId);

        List<ExpenseTypeWebDTO> expenseTypeWebDTOS = assignTypeService.queryExpenseTypeByApplicationTypeId(applicationType, categoryId, expenseTypeName, page);

        expenseTypeWebDTOS.stream().forEach(field -> {
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

    public List<TravelApplicationType> queryAllType(Long setOfBooksId, Boolean enabled) {

        List<Long> typeIdList = assignCompanyService.selectList(
                new EntityWrapper<TravelApplicationTypeAssignCompany>().eq("company_id", OrgInformationUtil.getCurrentCompanyId())
                        .eq("enabled",true)
        ).stream().map(TravelApplicationTypeAssignCompany::getTypeId).collect(Collectors.toList());
        if (typeIdList.size() == 0) {
            return new ArrayList<>();
        }

        List<TravelApplicationType> travelTypeList = this.selectList(
                new EntityWrapper<TravelApplicationType>()
                        .in("id", typeIdList)
                        .eq("set_of_books_id", setOfBooksId == null ? OrgInformationUtil.getCurrentSetOfBookId() : setOfBooksId)
                        .eq(enabled != null,"enabled",enabled));

        //根据当前用户权限筛选单据类型
        travelTypeList = travelTypeList.stream().filter(applicationType -> {
            boolean flag = true;

            // 全部人员
            if (VisibleUserScopeEnum.ALL.getId().equals(applicationType.getVisibleUserScope())){
                return true;
            }
            // 部门
            if (VisibleUserScopeEnum.DEPARTMENT.getId().equals(applicationType.getVisibleUserScope())){
                List<Long> ids = assignDepartmentService.selectList(
                        new EntityWrapper<TravelApplicationTypeAssignDepartment>().eq("type_id", applicationType.getId())
                ).stream().map(TravelApplicationTypeAssignDepartment::getDepartmentId).collect(Collectors.toList());

                if (!org.springframework.util.CollectionUtils.isEmpty(ids)) {
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
            if (VisibleUserScopeEnum.USER_GROUP.getId().equals(applicationType.getVisibleUserScope())){
                List<Long> ids = assignUserGroupService.selectList(
                        new EntityWrapper<TravelApplicationTypeAssignUserGroup>().eq("type_id", applicationType.getId())
                ).stream().map(TravelApplicationTypeAssignUserGroup::getUserGroupId).collect(Collectors.toList());

                if (!org.springframework.util.CollectionUtils.isEmpty(ids)) {
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

        return travelTypeList;
    }
}
