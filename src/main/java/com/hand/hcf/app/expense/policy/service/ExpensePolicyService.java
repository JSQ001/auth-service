package com.hand.hcf.app.expense.policy.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.service.ApplicationTypeService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.PolicyCheckConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.policy.domain.*;
import com.hand.hcf.app.expense.policy.dto.DynamicFieldDTO;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyDTO;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyMatchDimensionDTO;
import com.hand.hcf.app.expense.policy.dto.PolicyCheckResultDTO;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.DepartmentControllerImpl;
import com.hand.hcf.app.mdata.implement.web.SobControllerImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import com.hand.hcf.app.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 11:53
 */
@Service
@Transactional
public class ExpensePolicyService extends BaseService<ExpensePolicyMapper, ExpensePolicy> {
    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private SobControllerImpl sobClient;
    @Autowired
    private DepartmentControllerImpl departmentClient;
    @Autowired
    private ExpensePolicyControlDimensionService controlDimensionService;
    @Autowired
    private ExpensePolicyRelatedCompanyService relatedCompanyService;
    @Autowired
    private ExpensePolicyFieldPropertyService fieldPropertyService;
    @Autowired
    private ExpensePolicyDynamicFieldService dynamicFieldService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private ApplicationTypeService applicationTypeService;
    @Autowired
    private ExpenseFieldService expenseFieldService;

    /**
     * 新增费用政策
     *
     * @param expensePolicyDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpensePolicyDTO insertExpensePolicy(ExpensePolicyDTO expensePolicyDTO) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        if (expensePolicyDTO.getId() != null) {
            //创建数据不允许有ID
            throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
        }
        ExpenseType expenseType = expenseTypeService.getTypeById(expensePolicyDTO.getExpenseTypeId());
        expensePolicyDTO.setExpenseTypeFlag(expenseType.getTypeFlag());
        //同一账套下优先级不能重复
        List<ExpensePolicy> expensePolicies = baseMapper.selectList(new EntityWrapper<ExpensePolicy>()
                .eq("expense_type_flag", expensePolicyDTO.getExpenseTypeFlag())
                .eq("set_of_books_id", expensePolicyDTO.getSetOfBooksId())
                .eq("priority", expensePolicyDTO.getPriority()));
        if (CollectionUtils.isNotEmpty(expensePolicies)) {
            throw new BizException(RespCode.EXPENSE_PLOICY_PRIORITY_REPEAT);
        }
        expensePolicyDTO.setTenantId(tenantId);

        baseMapper.insert(expensePolicyDTO);
        //插入费用政策控制维度值表
        List<ExpensePolicyControlDimension> controlDimensions = expensePolicyDTO.getControlDimensions();
        if (CollectionUtils.isNotEmpty(controlDimensions)) {
            controlDimensions.stream().forEach(entity -> {
                entity.setTenantId(entity.getTenantId() != null ? entity.getTenantId() : OrgInformationUtil.getCurrentTenantId());
                entity.setSetOfBooksId(entity.getSetOfBooksId() != null ? entity.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                entity.setExpExpensePolicyId(expensePolicyDTO.getId());
                entity.setId(null);
            });
            controlDimensionService.insertBatch(controlDimensions);
        }
        // 插入费用政策关联公司
        if (expensePolicyDTO.getAllCompanyFlag()) {
            expensePolicyDTO.setRelatedCompanies(new ArrayList<>());
        } else {
            // 插入费用政策控制维度值表
            List<ExpensePolicyRelatedCompany> relatedCompanies = expensePolicyDTO.getRelatedCompanies();
            if (CollectionUtils.isNotEmpty(relatedCompanies)) {
                relatedCompanies.stream().forEach(entity -> {
                    entity.setTenantId(entity.getTenantId() != null ? entity.getTenantId() : OrgInformationUtil.getCurrentTenantId());
                    entity.setSetOfBooksId(entity.getSetOfBooksId() != null ? entity.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                    entity.setExpExpensePolicyId(expensePolicyDTO.getId());
                    entity.setCompanyId(entity.getCompanyId());
                });
                relatedCompanyService.insertBatch(relatedCompanies);
            }
        }
        //插入动态字段表
        List<ExpensePolicyDynamicField> dynamicFields = expensePolicyDTO.getDynamicFields();
        if(CollectionUtils.isNotEmpty(dynamicFields)){
            dynamicFields.stream().forEach(entity -> {
                entity.setTenantId(entity.getTenantId() != null ? entity.getTenantId() : OrgInformationUtil.getCurrentTenantId());
                entity.setSetOfBooksId(entity.getSetOfBooksId() != null ? entity.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                entity.setExpExpensePolicyId(expensePolicyDTO.getId());
            });
            dynamicFieldService.insertBatch(dynamicFields);
            dynamicFields.stream().forEach(entity -> {
                ExpensePolicyFieldProperty fieldProperty = entity.getExpensePolicyFieldProperty();
                if(fieldProperty != null) {
                    fieldProperty.setId(entity.getId());
                    fieldPropertyService.insert(fieldProperty);
                }
            });
        }
        return expensePolicyDTO;
    }

    /**
     * 更新费用政策
     * @param expensePolicyDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpensePolicyDTO updateExpensePolicy(ExpensePolicyDTO expensePolicyDTO) {
        PrincipalLite user = OrgInformationUtil.getUser();
        if (expensePolicyDTO.getId() == null) {
            // 更新数据ID必填
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        ExpensePolicy oldExpensePolicy = baseMapper.selectById(expensePolicyDTO.getId());
        if(!oldExpensePolicy.getPriority().equals(expensePolicyDTO.getPriority())) {
            //同一账套下优先级不能重复
            List<ExpensePolicy> expensePolicies = baseMapper.selectList(new EntityWrapper<ExpensePolicy>()
                    .eq("set_of_books_id", expensePolicyDTO.getSetOfBooksId())
                    .eq("priority", expensePolicyDTO.getPriority())
                    .eq("expense_type_flag",oldExpensePolicy.getExpenseTypeFlag()));
            if (CollectionUtils.isNotEmpty(expensePolicies)) {
                throw new BizException(RespCode.EXPENSE_PLOICY_PRIORITY_REPEAT);
            }
        }
        baseMapper.updateById(expensePolicyDTO);
        // 更新费用政策控制维度值表
        // 需先清除原控制维度值
        List<Long> controlDimensionIds = controlDimensionService.getControlDimensionByPolicyId(expensePolicyDTO.getId()).stream().map(ExpensePolicyControlDimension::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(controlDimensionIds)) {
            controlDimensionService.deleteBatchIds(controlDimensionIds);
        }
        List<ExpensePolicyControlDimension> controlDimensions = expensePolicyDTO.getControlDimensions();
        if (CollectionUtils.isNotEmpty(controlDimensions)) {
            for (ExpensePolicyControlDimension controlDimension : controlDimensions) {
                if(controlDimension.getId() == null){
                    controlDimension.setExpExpensePolicyId(expensePolicyDTO.getId());
                    controlDimension.setTenantId(controlDimension.getTenantId() != null ? controlDimension.getTenantId() : user.getTenantId());
                    controlDimension.setSetOfBooksId(controlDimension.getSetOfBooksId() != null ? controlDimension.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                }
                controlDimensionService.insertOrUpdate(controlDimension);
            }
        }
        // 更新动态字段表,需先清除原动态字段
        List<Long> dynamicFieldIds = dynamicFieldService.getDynamicFieldByPolicyId(expensePolicyDTO.getId()).stream().map(ExpensePolicyDynamicField::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dynamicFieldIds)) {
            dynamicFieldService.deleteBatchIds(dynamicFieldIds);
        }
        List<ExpensePolicyDynamicField> dynamicFields = expensePolicyDTO.getDynamicFields();
        if(CollectionUtils.isNotEmpty(dynamicFields)) {
            for (ExpensePolicyDynamicField dynamicField : dynamicFields) {
                if (dynamicField.getId() == null) {
                    dynamicField.setExpExpensePolicyId(expensePolicyDTO.getId());
                    dynamicField.setTenantId(dynamicField.getTenantId() != null ? dynamicField.getTenantId() : user.getTenantId());
                    dynamicField.setSetOfBooksId(dynamicField.getSetOfBooksId() != null ? dynamicField.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                }
            }
            dynamicFieldService.insertBatch(dynamicFields);
            dynamicFields.stream().forEach(entity -> {
                ExpensePolicyFieldProperty fieldProperty = entity.getExpensePolicyFieldProperty();
                if (fieldProperty != null) {
                    fieldProperty.setId(entity.getId());
                    fieldPropertyService.insert(fieldProperty);
                }
            });
        }
        // 更新费用政策关联公司表
        if (expensePolicyDTO.getAllCompanyFlag()) {
            List<Long> relatedCompanyIds = relatedCompanyService.getRelatedCompanyByPolicyId(expensePolicyDTO.getId()).stream().map(ExpensePolicyRelatedCompany::getId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(relatedCompanyIds)) {
                relatedCompanyService.deleteBatchIds(relatedCompanyIds);
            }
            expensePolicyDTO.setRelatedCompanies(new ArrayList<>());
        } else {
            // 插入费用政策关联公司表
            List<Long> relatedCompanyIds = relatedCompanyService.getRelatedCompanyByPolicyId(expensePolicyDTO.getId()).stream().map(ExpensePolicyRelatedCompany::getId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(relatedCompanyIds)) {
                relatedCompanyService.deleteBatchIds(relatedCompanyIds);
            }
            List<ExpensePolicyRelatedCompany> relatedCompanies = expensePolicyDTO.getRelatedCompanies();
            if (CollectionUtils.isNotEmpty(relatedCompanies)) {
                 relatedCompanies.stream().forEach(relatedCompany -> {
                     if (relatedCompany.getId() == null) {
                         relatedCompany.setExpExpensePolicyId(expensePolicyDTO.getId());
                         relatedCompany.setTenantId(relatedCompany.getTenantId() != null ? relatedCompany.getTenantId() : user.getTenantId());
                         relatedCompany.setSetOfBooksId(relatedCompany.getSetOfBooksId() != null ? relatedCompany.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                     }
                    relatedCompanyService.insertOrUpdate(relatedCompany);
                });
            }
        }
        return expensePolicyDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteExpensePolicyById(Long id){
        baseMapper.deleteById(id);
        List<Long> controlDimensionIds = controlDimensionService.getControlDimensionByPolicyId(id).stream().map(ExpensePolicyControlDimension::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(controlDimensionIds)) {
            controlDimensionService.deleteBatchIds(controlDimensionIds);
        }
        List<Long> dynamicFieldIds = dynamicFieldService.getDynamicFieldByPolicyId(id).stream().map(ExpensePolicyDynamicField::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dynamicFieldIds)) {
            dynamicFieldService.deleteBatchIds(dynamicFieldIds);
            fieldPropertyService.deleteBatchIds(dynamicFieldIds);
        }
        List<Long> relatedCompanyIds = relatedCompanyService.getRelatedCompanyByPolicyId(id).stream().map(ExpensePolicyRelatedCompany::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(relatedCompanyIds)) {
            relatedCompanyService.deleteBatchIds(relatedCompanyIds);
        }
    }

    /**
     * 根据id获得费用政策信息
     *
     * @param expExpensePolicyId
     * @return
     */
    public ExpensePolicyDTO getExpensePolicyById(Long expExpensePolicyId) {
        ExpensePolicyDTO expensePolicyDTO = new ExpensePolicyDTO();
        expensePolicyDTO = toDTO(baseMapper.selectById(expExpensePolicyId));
        List<ExpensePolicyControlDimension> controlDimensions = controlDimensionService.getControlDimensionByPolicyId(expExpensePolicyId);
        expensePolicyDTO.setControlDimensions(controlDimensions);
        List<ExpensePolicyDynamicField> dynamicFields = dynamicFieldService.getDynamicFieldByPolicyId(expExpensePolicyId);
        if(CollectionUtils.isNotEmpty(dynamicFields)) {
            dynamicFields.stream().forEach(item -> {
                ExpenseField expenseField = expenseFieldService.selectById(item.getFieldId());
                if (expenseField != null) {
                    item.setName(expenseField.getName());
                }
            });
            expensePolicyDTO.setDynamicFields(dynamicFields);
        }
        List<ExpensePolicyRelatedCompany> relatedCompanies = relatedCompanyService.getRelatedCompanyByPolicyId(expExpensePolicyId);
        if (CollectionUtils.isEmpty(relatedCompanies)){
            expensePolicyDTO.setAllCompanyFlag(Boolean.TRUE);
            expensePolicyDTO.setRelatedCompanies(new ArrayList<>());
        }else{
            expensePolicyDTO.setAllCompanyFlag(Boolean.FALSE);
            Set<Long> companyIds = relatedCompanies.stream().map(ExpensePolicyRelatedCompany::getCompanyId).collect(Collectors.toSet());
            List<CompanyCO> companySumDTOS = organizationService.listCompaniesByIds(new ArrayList<>(companyIds));
            Map<Long, String> map = companySumDTOS
                    .stream()
                    .collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
            relatedCompanies.stream().forEach(e -> {
                if (map.containsKey(e.getCompanyId())){
                    e.setCompanyName(map.get(e.getCompanyId()));
                }
            });
            expensePolicyDTO.setRelatedCompanies(relatedCompanies);
        }
        List<ExpenseTypeWebDTO> expenseTypeInfos = applicationTypeService.queryExpenseTypeBySetOfBooksId(expensePolicyDTO.getSetOfBooksId(),expensePolicyDTO.getExpenseTypeId(),null,null,expensePolicyDTO.getExpenseTypeFlag(),new Page(0,10));
        if(CollectionUtils.isNotEmpty(expenseTypeInfos)) {
            expensePolicyDTO.setExpenseTypeInfo(expenseTypeInfos.get(0));
        }
        return expensePolicyDTO;
    }


    /**
     * 按条件分页查询
     *
     * @param setOfBooksId
     * @param expenseTypeId
     * @param dutyType
     * @param companyLevelId
     * @param page
     * @return
     */
    public List<ExpensePolicyDTO> pageExpensePolicyByCond(Long setOfBooksId, Long expenseTypeId, String dutyType, Long companyLevelId,Integer typeFlag, Page page) {
        List<ExpensePolicy> expensePolicys = baseMapper.selectPage(page, new EntityWrapper<ExpensePolicy>()
                .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                .eq(expenseTypeId != null, "expense_type_id", expenseTypeId)
                .eq(dutyType != null, "duty_type", dutyType)
                .eq(companyLevelId != null, "company_level_id", companyLevelId)
                .eq("expense_type_flag",typeFlag)
                .orderBy("enabled",false)
                .orderBy("priority",true));
        return toDTO(expensePolicys);
    }

    /**
     * domain转dto
     *
     * @param domain
     * @return
     */
    public ExpensePolicyDTO toDTO(ExpensePolicy domain) {
        // 设置相同的属性
        ExpensePolicyDTO dto = mapperFacade.map(domain, ExpensePolicyDTO.class);
        // 转化其他属性
        SetOfBooksInfoCO setOfBooksInfoCO = sobClient.getSetOfBooksById(domain.getSetOfBooksId());
        if (setOfBooksInfoCO != null) {
            dto.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
        }
        ExpenseType expenseType =expenseTypeService.getTypeById(domain.getExpenseTypeId());
        if(expenseType != null){
            dto.setExpenseTypeName(expenseType.getName());
        }
        if(dto.getCompanyLevelId() != null){
            List<CompanyLevelCO> companyLevelCO = organizationService.listCompanyLevel(dto.getCompanyLevelId(),null);
            if(CollectionUtils.isNotEmpty(companyLevelCO)) {
                dto.setCompanyLevelName(companyLevelCO.get(0).getDescription());
            }
        }
        if(domain.getDutyType()!=null){
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("1002", domain.getDutyType());
            if(sysCodeValueCO != null) {
                dto.setDutyTypeName(sysCodeValueCO.getName());
            }
        }
        if(domain.getStaffLevel()!=null){
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("1008", domain.getStaffLevel());
            if(sysCodeValueCO != null) {
                dto.setStaffLevelName(sysCodeValueCO.getName());
            }
        }
        if(domain.getDepartmentId() != null) {
            DepartmentCO departmentCO = departmentClient.getDepartmentById(domain.getDepartmentId());
            if (departmentCO != null) {
                dto.setDepartmentName(departmentCO.getName());
            }
        }
        CurrencyRateCO currencyRateCO = organizationService.getForeignCurrencyByCode("CNY",domain.getCurrencyCode(), domain.getSetOfBooksId());
        if (currencyRateCO != null){
            dto.setCurrencyName(currencyRateCO.getCurrencyName());
        }
        SysCodeValueCO controlStrategy = organizationService.getSysCodeValueByCodeAndValue("CONTROL_STRATEGY",domain.getControlStrategyCode());
        if(controlStrategy != null){
            dto.setControlStrategyName(controlStrategy.getName());
        }

        return dto;
    }

    /**
     * domains转换dtos
     *
     * @param domains
     * @return
     */
    public List<ExpensePolicyDTO> toDTO(List<ExpensePolicy> domains) {
        List<ExpensePolicyDTO> expensePolicyDTOS = new ArrayList<ExpensePolicyDTO>();
        for (ExpensePolicy domain : domains) {
            expensePolicyDTOS.add(toDTO(domain));
        }
        return expensePolicyDTOS;
    }

    /**
     * 根据匹配维度获取匹配的费用政策
     * @param matchDimensionDTO
     * @return
     */
    public PolicyCheckResultDTO checkExpensePolicy(ExpensePolicyMatchDimensionDTO matchDimensionDTO){
        List<ExpensePolicy> expensePolicys = baseMapper.selectList(new EntityWrapper<ExpensePolicy>()
                .eq("set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                .eq("expense_type_flag", matchDimensionDTO.getExpenseTypeFlag())
                .eq("enabled", true)
                .eq("deleted", false)
                .orderBy("priority",false));

        //匹配固定字段
        //查询申请人信息
        ContactCO contactCO = Optional.ofNullable(organizationService.getUserById(matchDimensionDTO.getApplicationId())).orElse(new ContactCO());
        CompanyCO companyCO = Optional.ofNullable(organizationService.getCompanyById(contactCO.getCompanyId())).orElse(new CompanyCO());
        DepartmentCO departmentCO = Optional.ofNullable(organizationService.getDepartementCOByUserOid(contactCO.getUserOid())).orElse(new DepartmentCO());

        expensePolicys = expensePolicys.stream().filter(p -> {
            //匹配申请项目
            if (p.getExpenseTypeId() == null || !p.getExpenseTypeId().equals(matchDimensionDTO.getExpenseTypeId())) {
                return false;
            }
            //匹配申请人公司级别
            if (p.getCompanyLevelId() != null && !p.getCompanyLevelId().equals(companyCO.getCompanyLevelId())) {
                return false;
            }
            //匹配申请人职务
            if (p.getDutyType() != null && !p.getDutyType().equals(contactCO.getDutyCode())) {
                return false;
            }
            //匹配申请人员工级别
            if (p.getStaffLevel() != null && !p.getStaffLevel().equals(contactCO.getRankCode())) {
                return false;
            }
            //匹配申请人部门
            if (p.getDepartmentId() != null && !p.getDepartmentId().equals(departmentCO.getId())) {
                return false;
            }
            //匹配币种
            if (p.getCurrencyCode() != null && !p.getCurrencyCode().equals(matchDimensionDTO.getCurrencyCode())) {
                return false;
            }
            //匹配公司
            if (!p.getAllCompanyFlag()) {
                if (relatedCompanyService.selectCount(new EntityWrapper<ExpensePolicyRelatedCompany>().eq("exp_expense_policy_id", p.getId()).eq("company_id", matchDimensionDTO.getCompanyId())) <= 0) {
                    return false;
                }
            }
            //匹配有效日期
            if (p.getStartDate() != null && p.getStartDate().compareTo(ZonedDateTime.now()) > 0) {
                return false;
            }
            if (p.getEndDate() != null && p.getEndDate().compareTo(ZonedDateTime.now()) < 0) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        //匹配动态字段
        Map<Long, DynamicFieldDTO> fieldMap = matchDimensionDTO.getDynamicFields().stream().collect(Collectors.toMap(DynamicFieldDTO::getFieldId, e -> e, (e1, e2) -> e1));

        expensePolicys = expensePolicys.stream().filter(p -> {
            List<ExpensePolicyDynamicField> dynamicFields = dynamicFieldService.selectList(new EntityWrapper<ExpensePolicyDynamicField>().eq("exp_expense_policy_id", p.getId()));
            for (ExpensePolicyDynamicField dynamicField : dynamicFields) {
                if (!fieldMap.containsKey(dynamicField.getFieldId()) || !fieldMap.get(dynamicField.getFieldId()).equals(dynamicField.getValue())) {
                    return false;
                }
                //校验动态字段相关属性
                DynamicFieldDTO fieldDTO = fieldMap.get(dynamicField.getFieldId());
                switch (fieldDTO.getFieldName()) {
                    case PolicyCheckConstant.SYSTEM_CONTROL_COUNTERPART:
                    case PolicyCheckConstant.SYSTEM_CONTROL_PARTICIPANT:
                        ExpensePolicyFieldProperty property = Optional.ofNullable(fieldPropertyService.selectById(dynamicField.getId())).orElse(new ExpensePolicyFieldProperty());

                        Long userId;
                        try {
                            userId = Long.valueOf(fieldDTO.getFieldValue());
                        } catch (Exception e){
                            throw new BizException(RespCode.SYS_DATA_FORMAT_ERROR);
                        }
                        ContactCO tempContact = Optional.ofNullable(organizationService.getUserById(userId)).orElse(new ContactCO());
                        DepartmentCO tempDepartment = Optional.ofNullable(organizationService.getDepartementCOByUserOid(tempContact.getUserOid())).orElse(new DepartmentCO());

                        //匹配职务
                        if (property.getDutyType() != null && !property.getDutyType().equals(tempContact.getDutyCode())) {
                            return false;
                        }
                        //匹配员工级别
                        if (property.getStaffLevel() != null && !property.getStaffLevel().equals(tempContact.getRankCode())) {
                            return false;
                        }
                        //匹配部门
                        if (property.getDepartmentId() != null && !property.getDepartmentId().equals(tempDepartment.getId())) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }
            }
            return true;
        }).collect(Collectors.toList());

        return this.checkExpensePolicyDimension(expensePolicys, matchDimensionDTO);
    }

    /**
     * 控制维度校验
     * @param expensePolicyList
     * @param matchDimensionDTO
     * @return
     */
    public PolicyCheckResultDTO checkExpensePolicyDimension(List<ExpensePolicy> expensePolicyList, ExpensePolicyMatchDimensionDTO matchDimensionDTO){
        Boolean checkSuccess;
        for (ExpensePolicy policy: expensePolicyList) {
            Map<String, DynamicFieldDTO> fieldMap = matchDimensionDTO.getDynamicFields().stream().collect(Collectors.toMap(DynamicFieldDTO::getFieldName, e -> e, (e1, e2) -> e1));

            List<String> valueList = controlDimensionService.selectList(new EntityWrapper<ExpensePolicyControlDimension>().eq("exp_expense_policy_id", policy.getId())).stream().map(ExpensePolicyControlDimension::getValue).collect(Collectors.toList());
            switch (policy.getControlDimensionType()) {
                case PolicyCheckConstant.CONTROL_DIMENSION_TYPE_AMOUNT:
                    checkSuccess = this.checkExpensePolicyDimensionFieldValue(policy.getJudgementSymbol(), PolicyCheckConstant.VALUE_TYPE_LONG, matchDimensionDTO.getAmount().toString(), valueList);
                    break;
                case PolicyCheckConstant.CONTROL_DIMENSION_TYPE_PRICE:
                    checkSuccess = this.checkExpensePolicyDimensionFieldValue(policy.getJudgementSymbol(), PolicyCheckConstant.VALUE_TYPE_LONG, matchDimensionDTO.getPrice().toString(), valueList);
                    break;
                case PolicyCheckConstant.CONTROL_DIMENSION_TYPE_QUANTITY:
                    checkSuccess = this.checkExpensePolicyDimensionFieldValue(policy.getJudgementSymbol(), PolicyCheckConstant.VALUE_TYPE_LONG, matchDimensionDTO.getQuantity().toString(), valueList);
                    break;
                default:
                    DynamicFieldDTO fieldDTO = fieldMap.get(policy.getControlDimensionType());
                    if (fieldDTO == null) {
                        checkSuccess = false;
                        break;
                    }
                    checkSuccess = this.checkExpensePolicyDimensionFieldValue(policy.getJudgementSymbol(), fieldDTO.getFieldDataType(), fieldDTO.getFieldValue(), valueList);
                    break;
            }
            if (!checkSuccess) {
                SysCodeValueCO controlStrategy = Optional.ofNullable(organizationService.getSysCodeValueByCodeAndValue("EXPENSE_POLICY_MESSAGE",policy.getMessageCode())).orElse(new SysCodeValueCO());
                return PolicyCheckResultDTO.error(policy.getControlStrategyCode(), controlStrategy.getName());
            }
        }
        return PolicyCheckResultDTO.ok();
    }

    /**
     * 判断维度字段是否满足费用政策
     * @param judgementSymbol 判断符号
     * @param value 用于判断的值
     * @param valueList 动态字段值集合
     * @return
     */
    public Boolean checkExpensePolicyDimensionFieldValue(String judgementSymbol, String valueType, String value, List<String> valueList){
        if (value == null || valueList.size() == 0) {
            return false;
        }
        switch (judgementSymbol) {
            case PolicyCheckConstant.JUDGEMENT_SYMBOL_LESS_THEN:
                if (valueType.equals(PolicyCheckConstant.VALUE_TYPE_LONG)) {
                    return Double.valueOf(value) < Double.valueOf(valueList.get(0));
                } else if (valueType.equals(PolicyCheckConstant.VALUE_TYPE_DATE)){
                    return ZonedDateTime.parse(value).compareTo(ZonedDateTime.parse(valueList.get(0))) < 0;
                }
            case PolicyCheckConstant.JUDGEMENT_SYMBOL_LESS_EQUAL:
                if (valueType.equals(PolicyCheckConstant.VALUE_TYPE_LONG)) {
                    return Double.valueOf(value) <= Double.valueOf(valueList.get(0));
                } else if (valueType.equals(PolicyCheckConstant.VALUE_TYPE_DATE)){
                    return ZonedDateTime.parse(value).compareTo(ZonedDateTime.parse(valueList.get(0))) <= 0;
                }
            case PolicyCheckConstant.JUDGEMENT_SYMBOL_BELONG:
                return valueList.contains(value);
            case PolicyCheckConstant.JUDGEMENT_SYMBOL_NOT_BELONG:
                return !valueList.contains(value);
            default:
                return false;
        }
    }

}
