package com.hand.hcf.app.expense.type.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.init.dto.ExpenseTypeInitDTO;
import com.hand.hcf.app.expense.type.bo.ExpenseBO;
import com.hand.hcf.app.expense.type.domain.*;
import com.hand.hcf.app.expense.type.domain.enums.AssignUserEnum;
import com.hand.hcf.app.expense.type.domain.enums.FieldDataColumn;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.domain.enums.TypeEnum;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeCategoryMapper;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeMapper;
import com.hand.hcf.app.expense.type.web.dto.*;
import com.hand.hcf.app.expense.type.web.mapper.ExpenseFieldMapper;
import com.hand.hcf.app.expense.type.web.mapper.FieldMappedColumn;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 *     申请类型Service
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
@Service
public class ExpenseTypeService extends BaseService<ExpenseTypeMapper, ExpenseType> {

    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private ExpenseFieldService expenseFieldService;
    @Autowired
    private ExpenseTypeAssignCompanyService assignCompanyService;
    @Autowired
    private ExpenseTypeAssignUserService assignUserService;
    @Autowired
    private OrganizationService organizationService;
    //jiu.zhao 预算
    /*@Autowired
    private BudgetService budgetService;*/

    @Autowired
    private ExpenseTypeCategoryMapper expenseTypeCategoryMapper;
    @Autowired
    private ExpenseTypeIconService expenseTypeIconService;

    @Transactional(rollbackFor = Exception.class)
    public ExpenseType createType(ExpenseType dto) {
        dto.setTenantId(OrgInformationUtil.getCurrentTenantId());
        checkType(dto);
        // 校验唯一性 账套下的代码不能重复
        List<ExpenseType> list = baseMapper.selectList(new EntityWrapper<ExpenseType>()
                .eq("code", dto.getCode())
                .eq("set_of_books_id", dto.getSetOfBooksId())
                .eq("type_flag", dto.getTypeFlag()));
        if (!CollectionUtils.isEmpty(list)){
            throw new BizException(dto.getTypeFlag().compareTo(0) == 0 ?
                    RespCode.EXPENSE_TYPE_APPLICATION_CODE_IS_EXISTS : RespCode.EXPENSE_TYPE_EXPENSE_CODE_IS_EXISTS);
        }
        dto.setDeleted(Boolean.FALSE);
        this.insert(dto);
        return dto;
    }

    private void checkType(ExpenseType dto){
        // 判断是不是申请类型
        if (null == dto.getTypeFlag() || TypeEnum.APPLICATION_TYPE.getKey().compareTo(dto.getTypeFlag()) == 0){
            dto.setSourceTypeId(null);
            dto.setTypeFlag(TypeEnum.APPLICATION_TYPE.getKey());
            dto.setAttachmentFlag(null);
        }else{
            dto.setTypeFlag(TypeEnum.COST_TYPE.getKey());
        }
        // 如果金额录入模式 为总金额
        if (dto.getEntryMode() == null || !dto.getEntryMode()){
            dto.setPriceUnit(null);
        }else{
            if (dto.getPriceUnit() == null){
                throw new BizException(RespCode.EXPENSE_TYPE_PRICE_UNIT_IS_NULL);
            }
        }
    }
    /**
     * 更新类型
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseType updateType(ExpenseType dto) {
        if (dto.getId() == null){
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        dto.setTenantId(OrgInformationUtil.getCurrentTenantId());

        ExpenseType expenseType = this.selectById(dto.getId());
        // 校验数据存不存在
        if (null == expenseType || expenseType.getDeleted()){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        // 如果类型不同不允许更新
        if (!expenseType.getTypeFlag().equals(dto.getTypeFlag())){
            throw new BizException(RespCode.EXPENSE_TYPE_ERROR);
        }
        dto.setVersionNumber(expenseType.getVersionNumber());
        checkType(dto);
        dto.setDeleted(Boolean.FALSE);
        this.updateById(dto);
        return dto;
    }

    public ExpenseType getTypeById(Long id) {
        ExpenseType expenseType = baseMapper.getTypeById(id);
        Map<String, List<Map<String, String>>> i18nMap = baseI18nService.getI18nMap(ExpenseType.class, id);
        expenseType.setI18n(i18nMap);
        SetOfBooksInfoCO setOfBooksInfoDTO = organizationService.getSetOfBooksInfoCOById(expenseType.getSetOfBooksId(), true);
        expenseType.setSetOfBooksName(setOfBooksInfoDTO.getSetOfBooksName());
        if(expenseType.getSourceTypeId() != null) {
            ExpenseType type = this.selectById(expenseType.getSourceTypeId());
            expenseType.setSourceTypeName(type.getName());
            //jiu.zhao 预算
            /*BudgetItemCO budgetItemCO = budgetService.getBudgetItemByExpenseTypeId(expenseType.getSourceTypeId());
            if (budgetItemCO != null){
                expenseType.setBudgetItemName(budgetItemCO.getItemName());
            }*/
        }
        if(expenseType.getApplicationModel() != null){
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("APPLICATION_MODEL",expenseType.getApplicationModel());
            if(sysCodeValueCO != null){
                expenseType.setApplicationModelName(sysCodeValueCO.getName());
            }
        }
        return expenseType;
    }

    /**
     * 保存费用类型字段
     * @param expenseTypeId
     * @param fieldDTOS
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveExpenseTypeFields(Long expenseTypeId, List<ExpenseFieldDTO> fieldDTOS){
        // 先查询该类别
        ExpenseType expenseType = baseMapper.selectById(expenseTypeId);
        if (null == expenseType){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        // 查询该类别已经保存的 field
        List<ExpenseField> expenseFields = expenseFieldService.selectByFieldId(expenseTypeId);
        // toSet
        Set<ExpenseField> fields = expenseFields.stream().collect(Collectors.toSet());

        Set<ExpenseFieldDTO> fieldDTOSet = fieldDTOS.stream().collect(Collectors.toSet());
        // 将前台传过来的filedDTO转换为 field
        Set<ExpenseField> tmpFields = adaptExpenseFields(fieldDTOSet);

        //修改的fields fieldOID != null
        Map<UUID,ExpenseField> toUpdateFieldsMap = tmpFields.stream().filter(u->u.getFieldOid()!= null).collect(Collectors.toMap(u->u.getFieldOid(), u->u));
        //新增的fields  fieldOID == null
        List<ExpenseField> toAddFields = tmpFields.stream().filter(u->u.getFieldOid()==null).collect(Collectors.toList());
        //待删除的费用字段
        List<ExpenseField> deleteExpenseFields = new ArrayList<>();
        //设置已存在的字段的属性值
        for (ExpenseField expenseField : fields) {
            //包含的字段做修改
            if (toUpdateFieldsMap.containsKey(expenseField.getFieldOid())){
                ExpenseField dto = toUpdateFieldsMap.get(expenseField.getFieldOid());
                expenseField.setFieldType(dto.getFieldType());
                expenseField.setName(dto.getName());
                expenseField.setReportKey(dto.getReportKey());
                expenseField.setMessageKey(dto.getMessageKey());
                expenseField.setSequence(dto.getSequence());
                if(dto.getRequired() != null){
                    expenseField.setRequired(dto.getRequired());
                }
                if(dto.getPrintHide() != null){
                    expenseField.setPrintHide(dto.getPrintHide());
                }
                if(dto.getShowOnList() != null){
                    expenseField.setShowOnList(dto.getShowOnList());
                }
                expenseField.setEditable(null!=dto.getEditable()?dto.getEditable():true);
                expenseField.setCustomEnumerationOid(dto.getCustomEnumerationOid());
                if(!StringUtils.isEmpty(dto.getDefaultValueMode())){
                    expenseField.setDefaultValueMode(dto.getDefaultValueMode());
                }
                expenseField.setDefaultValueKey(dto.getDefaultValueKey());
                expenseField.setI18n(dto.getI18n());
            } else {//删除的字段
                deleteExpenseFields.add(expenseField);
            }
        }

        //新增的费用字段
        if (CollectionUtils.isNotEmpty(toAddFields)) {
            for (ExpenseField toAddField : toAddFields) {
                //初始化默认值
                toAddField.setExpenseTypeId(expenseTypeId);
                toAddField.setCommonField(false);
                toAddField.setFieldDataType(toAddField.getFieldType().getFieldDataTypeEnum().getKey());
                toAddField.setFieldOid(UUID.randomUUID());
                toAddField.setId(null);
                if(toAddField.getShowOnList() ==  null){
                    toAddField.setShowOnList(true);
                }
            }
            fields.addAll(toAddFields);
        }
        //删除字段
        if(CollectionUtils.isNotEmpty(deleteExpenseFields)){
            fields.removeAll(deleteExpenseFields);
            expenseFieldService.deleteBatchIds(deleteExpenseFields.stream().map(u->u.getId()).collect(Collectors.toList()));
        }
        //循环更新字段
        for (ExpenseField expenseField : fields) {
            if (FieldType.CUSTOM_ENUMERATION.getId().equals(expenseField.getFieldTypeId())){
                // 如果是值列表就保存列列表的相关值信息
                List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(expenseField.getCustomEnumerationOid());
                if (CollectionUtils.isEmpty(sysCodeValueCOS)){
                    throw new BizException(RespCode.EXPENSE_TYPE_SYS_CODE_IS_NULL);
                }
            }
            if(expenseField.getId() == null){
                expenseFieldService.insert(expenseField);
            }else {
                expenseFieldService.updateById(expenseField);
            }
        }
    }


    /**
     * 费用类型字段映射匹配
     * @param fieldDTOs
     * @return
     */
    public Set<ExpenseField> adaptExpenseFields(Set<ExpenseFieldDTO> fieldDTOs) {
        Set<ExpenseField> fields = new HashSet<>();
        List<FieldDataColumn> fieldDataColumns = new ArrayList<>();
        //获得所有已映射的columnId
        for (ExpenseFieldDTO expenseFieldDTO : fieldDTOs) {
            FieldDataColumn invoiceDataColumn = FieldDataColumn.parse(expenseFieldDTO.getMappedColumnId());
            if (expenseFieldDTO.getMappedColumnId() != null && invoiceDataColumn != null) {
                fieldDataColumns.add(invoiceDataColumn);
            }
        }
        //匹配当前可映射的columnId
        FieldMappedColumn fieldMappedColumn = new FieldMappedColumn(fieldDataColumns);
        for (ExpenseFieldDTO expenseFieldDTO : fieldDTOs) {
            ExpenseField field = ExpenseFieldMapper.expenseFieldDTOToExpenseField(expenseFieldDTO);
            fields.add(field);
            if (field.getMappedColumnId() == null) {
                //参与人组件特殊，数据库类型TEXT,且只能配置一个
                if (FieldType.PARTICIPANTS.getId().equals(field.getFieldType().getId())) {
                    field.setMappedColumnId(FieldDataColumn.PARTICIPANTS_COL_1.getId());
                } else {
                    field.setMappedColumnId(fieldMappedColumn.getNextColumnId(field.paresFieldDataTypeEnum()));
                }
            }
        }
        return fields;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFieldByOid(Long expenseTypeId, UUID fieldOid) {
        ExpenseField deleteWrapper = new ExpenseField(expenseTypeId, fieldOid);

        expenseFieldService.delete(new EntityWrapper<>(deleteWrapper));
    }


    /**
     * 费用/申请类型适用权限
     * @param infoDTO
     * @param expenseTypeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseTypeAssignInfoDTO saveAssignInfo(ExpenseTypeAssignInfoDTO infoDTO, Long expenseTypeId) {
        // 先删除 后新增
        assignCompanyService.delete(new EntityWrapper<ExpenseTypeAssignCompany>().eq("expense_type_id",expenseTypeId));
        assignUserService.delete(new EntityWrapper<ExpenseTypeAssignUser>().eq("expense_type_id",expenseTypeId));
        List<ExpenseTypeAssignCompany> companies = infoDTO.getAssignCompanies();
        List<ExpenseTypeAssignUser> users = infoDTO.getAssignUsers();
        // 如果不是全部公司
        if (!infoDTO.getAllCompanyFlag()){
            if (CollectionUtils.isEmpty(companies)){
                throw new BizException(RespCode.EXPENSE_ASSIGN_COMPANY_IS_NULL);
            }
            companies.stream().map(e -> {
                e.setExpenseTypeId(expenseTypeId);
                e.setId(null);
                return e;
            }).collect(Collectors.toList());
            assignCompanyService.insertBatch(companies);
        }else{
            infoDTO.setAssignCompanies(new ArrayList<>());
        }
        // 如果不是全部人员
        if (!AssignUserEnum.USER_ALL.getKey().equals(infoDTO.getApplyType())){
            if (CollectionUtils.isEmpty(users)){
                throw new BizException(RespCode.EXPENSE_ASSIGN_USER_IS_NULL);
            }
            users.stream().map(e -> {
                e.setExpenseTypeId(expenseTypeId);
                e.setApplyType(infoDTO.getApplyType());
                e.setId(null);
                return e;
            }).collect(Collectors.toList());
            assignUserService.insertBatch(users);
        }else{
            infoDTO.setAssignUsers(new ArrayList<>());
        }
        return infoDTO;
    }

    public ExpenseTypeAssignInfoDTO queryAssign(Long expenseTypeId) {
        List<ExpenseTypeAssignCompany> assignCompanies = assignCompanyService.selectList(new EntityWrapper<ExpenseTypeAssignCompany>().eq("expense_type_id", expenseTypeId));
        List<ExpenseTypeAssignUser> assignUsers = assignUserService.selectList(new EntityWrapper<ExpenseTypeAssignUser>().eq("expense_type_id", expenseTypeId));
        ExpenseTypeAssignInfoDTO infoDTO = new ExpenseTypeAssignInfoDTO();
        if (CollectionUtils.isEmpty(assignCompanies)){
            infoDTO.setAllCompanyFlag(Boolean.TRUE);
            infoDTO.setAssignCompanies(new ArrayList<>());
        }else{
            infoDTO.setAllCompanyFlag(Boolean.FALSE);
            Set<Long> companyIds = assignCompanies.stream().map(ExpenseTypeAssignCompany::getCompanyId).collect(Collectors.toSet());
            List<CompanyCO> companySumDTOS = organizationService.listCompaniesByIds(new ArrayList<>(companyIds));
            Map<Long, String> map = companySumDTOS
                    .stream()
                    .collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
            assignCompanies.stream().forEach(e -> {
                if (map.containsKey(e.getCompanyId())){
                    e.setCompanyName(map.get(e.getCompanyId()));
                }
            });
            infoDTO.setAssignCompanies(assignCompanies);
        }

        if (CollectionUtils.isEmpty(assignUsers)) {
            infoDTO.setApplyType(AssignUserEnum.USER_ALL.getKey());
            infoDTO.setAssignUsers(assignUsers);
        }else{
            infoDTO.setApplyType(assignUsers.get(0).getApplyType());
            // 部门
            if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(infoDTO.getApplyType())){
                Set<Long> departmentIds = assignUsers.stream().map(ExpenseTypeAssignUser::getUserTypeId).collect(Collectors.toSet());
                List<DepartmentCO> departmentInfoDTOS = organizationService.listDepartmentsByIds(new ArrayList<>(departmentIds));
                Map<Long, String> map = departmentInfoDTOS
                        .stream()
                        .collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));
                assignUsers.stream().forEach(e -> {
                    if (map.containsKey(e.getUserTypeId())){
                        e.setName(map.get(e.getUserTypeId()));
                    }
                });
            }else{
                // 人员组
                Set<Long> userGroupIds = assignUsers.stream().map(ExpenseTypeAssignUser::getUserTypeId).collect(Collectors.toSet());
                List<UserGroupCO> userGroupDTOS = organizationService.listUserGroupsByIds(new ArrayList<>(userGroupIds));
                Map<Long, String> map = userGroupDTOS
                        .stream()
                        .collect(Collectors.toMap(UserGroupCO::getId, UserGroupCO::getName, (k1, k2) -> k1));
                assignUsers.stream().forEach(e -> {
                    if (map.containsKey(e.getUserTypeId())){
                        e.setName(map.get(e.getUserTypeId()));
                    }
                });
            }
            infoDTO.setAssignUsers(assignUsers);
        }

        return infoDTO;
    }

    public List<ExpenseFieldDTO> queryFields(Long expenseTypeId) {

        List<ExpenseField> fields = expenseFieldService.selectList(
                new EntityWrapper<ExpenseField>()
                        .eq("expense_type_id", expenseTypeId)
                        .orderBy("sequence",true));

        fields = baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(fields, ExpenseField.class);

        List<ExpenseFieldDTO> fieldDTOS = fields.stream().map(e -> {
            ExpenseFieldDTO fieldDTO = ExpenseFieldDTO.builder()
                    .codeName(null)
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
                List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(e.getCustomEnumerationOid());
                // 为值列表，则设置值列表的相关值
                List<OptionDTO> options = sysCodeValueCOS.stream().map(OptionDTO::createOption).collect(Collectors.toList());
                fieldDTO.setOptions(options);
            }
            return fieldDTO;
        }).collect(Collectors.toList());
        return fieldDTOS;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTypeById(Long id) {
        // 删除field
        expenseFieldService.delete(new EntityWrapper<ExpenseField>().eq("expense_type_id", id));
        // 分配公司
        assignCompanyService.delete(new EntityWrapper<ExpenseTypeAssignCompany>().eq("expense_type_id", id));
        // 分配人员
        assignUserService.delete(new EntityWrapper<ExpenseTypeAssignUser>().eq("expense_type_id", id));
        // 删除自己
        this.deleteById(id);
        return true;
    }

    public List<ExpenseType> queryByCondition(Page page, Long setOfBooksId, String code, String name, Long typeCategoryId, Integer typeFlag,Boolean enabled) {
        EntityWrapper<ExpenseType> wrapper = new EntityWrapper<>();
        wrapper
                .eq("et.set_of_books_id", setOfBooksId)
                .eq("et.type_flag", typeFlag)
                .eq(typeCategoryId != null, "type_category_id", typeCategoryId)
                .eq(enabled != null,"et.enabled", enabled)
                .eq("et.deleted", false)
                .like(StringUtils.hasText(code), "et.code", code)
                .like(StringUtils.hasText(name), "et.name", name)
                .orderBy(typeCategoryId != null, "et.sequence",true)
                .orderBy(typeCategoryId == null, "et.id ",true);

        /**
         * 将控件信息一并返回给前端
         *   --2019.02.27 zsf
         */
        List<ExpenseType> expenseTypeList = baseMapper.queryByCondition(page, wrapper);
        expenseTypeList.stream().forEach(expenseType -> {
            expenseType.setFields(this.queryFields(expenseType.getId()));
        });
        return expenseTypeList;
    }

    public List<ExpenseType> queryByCategoryId(Long typeCategoryId, int typeFlag) {
        List<ExpenseType> list = this.selectList(new EntityWrapper<ExpenseType>()
                .eq("type_flag", typeFlag)
                .eq("enabled", true)
                .eq("deleted", false)
                .eq(typeCategoryId != null, "type_category_id", typeCategoryId)
                .orderBy("sequence", true));
        if (typeFlag == 0){
            List<Long> ids = list.stream().map(ExpenseType::getId).collect(Collectors.toList());
            //jiu.zhao 预算
            /*List<BudgetItemCO> budgetItemCOS = budgetService.listBudgetItemByExpenseTypeIds(ids);
            Map<Long, String> itemMap = budgetItemCOS.stream().collect(Collectors.toMap(BudgetItemCO::getSourceItemId, BudgetItemCO::getItemName, (k1, k2) -> k1));
            list.stream().forEach(e -> {
                if (itemMap.containsKey(e.getId())){
                    e.setBudgetItemName(itemMap.get(e.getId()));
                }
            });*/
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean sort(List<SortBySequenceDTO> list) {
        Map<Long, Integer> map = list.stream().collect(Collectors.toMap(SortBySequenceDTO::getId, SortBySequenceDTO::getSequence, (k1, k2) -> k1));
        List<ExpenseType> expenseTypes = this.selectBatchIds(list.stream().map(SortBySequenceDTO::getId).collect(Collectors.toList()));
        expenseTypes.stream().forEach(e -> {
            if (map.containsKey(e.getId())){
                e.setSequence(map.get(e.getId()));
            }
        });
        return this.updateBatchById(expenseTypes);
    }

    public List<ExpenseType> queryLovByDocumentTypeAssign(Long setOfBooksId,
                                                          String range,
                                                          Integer documentType,
                                                          Long documentTypeId,
                                                          String code,
                                                          String name,
                                                          Long typeCategoryId,
                                                          Integer typeFlag,
                                                          Page page) {
        if (documentTypeId == null) {
            range = "all";
        }
        List<ExpenseType> expenseTypes;
        if(page == null){
            expenseTypes = baseMapper.queryLovByDocumentTypeAssign(setOfBooksId, range, documentType, documentTypeId, code, name, typeCategoryId, typeFlag);
        }else{
            expenseTypes = baseMapper.queryLovByDocumentTypeAssign(setOfBooksId, range, documentType, documentTypeId, code, name, typeCategoryId, typeFlag, page);
        }

        if (expenseTypes.size() > 0){
            expenseTypes.stream().forEach(expenseType -> {
                ExpenseTypeCategory expenseTypeCategory = expenseTypeCategoryMapper.selectById(expenseType.getTypeCategoryId());
                if (expenseTypeCategory != null){
                    expenseType.setTypeCategoryName(expenseTypeCategory.getName());
                }
            });
        }

        return expenseTypes;
    }

    public List<BasicCO> listByExpenseTypesAndCond(Long tenantId, Long setOfBooksId, boolean enabled, String code, String name, Page page) {
        return baseMapper.listByExpenseTypesAndCond(tenantId, setOfBooksId, enabled, code, name, page);
    }


    public List<ExpenseTypeWebDTO> lovExpenseType(ExpenseBO expenseBO,
                                                  Page<ExpenseTypeWebDTO> page){
        List<ExpenseTypeWebDTO> expenseTypeWebDTOS = baseMapper.listByDocumentLov(expenseBO);
        if (CollectionUtils.isEmpty(expenseTypeWebDTOS)){
            return new ArrayList<>();
        }

        List<ExpenseTypeWebDTO> collect = expenseTypeWebDTOS
                .stream()
                .filter(e -> !e.getUserGroupFlag()).collect(Collectors.toList());
        List<ExpenseTypeWebDTO> userGroupType = expenseTypeWebDTOS
                .stream()
                .filter(ExpenseTypeWebDTO::getUserGroupFlag)
                .filter(e -> {
                    if (expenseBO.getEmployeeId() == null || CollectionUtils.isEmpty(e.getUserGroupIds())){
                        return false;
                    }
                    JudgeUserCO judgeUserCO = new JudgeUserCO(expenseBO.getEmployeeId(), e.getUserGroupIds());
                    Boolean exists = organizationService.judgeUserInUserGroups(judgeUserCO);
                    return exists == null ? Boolean.FALSE : exists;
                }).collect(Collectors.toList());
        collect.addAll(userGroupType);
        Collections.sort(collect);
        List<ExpenseTypeWebDTO> list = PageUtil.pageHandler(page, collect);
        String currentLanguage = StringUtils.hasText(LoginInformationUtil.getCurrentLanguage()) ?
                LoginInformationUtil.getCurrentLanguage() : LanguageEnum.ZH_CN.getKey();
        list.forEach(e -> e.setFieldList(expenseFieldService.listFieldByTypeId(e.getId(), currentLanguage)));
        return setFields(list);
    }

    private List<ExpenseTypeWebDTO> setFields(List<ExpenseTypeWebDTO> expenseTypeWebDTOS){
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
                        List<SysCodeValueCO> sysCodeValueCOS = organizationService
                                .listSysCodeValueCOByOid(e.getCustomEnumerationOid());
                        List<OptionDTO> options = sysCodeValueCOS
                                .stream()
                                .map(OptionDTO::createOption).collect(Collectors.toList());
                        fieldDTO.setOptions(options);
                    }
                    return fieldDTO;
                }).collect(Collectors.toList());
                field.setFields(fieldDTOS);
            }
        });
        return expenseTypeWebDTOS;
    }

    public void checkExpenseTypeInitData(ExpenseTypeInitDTO dto){
        StringBuilder errorMessage = new StringBuilder();
        if(TypeConversionUtils.isEmpty(dto.getCode()) || TypeConversionUtils.isEmpty(dto.getName())){
            errorMessage.append(dto.getCode()+":必输字段为空！");
        }else {
            //默认当前租户
            dto.setTenantId(OrgInformationUtil.getCurrentTenantId());
            //todo 应该根据账套code去查询id,暂时没有这样的三方接口
            Long setOfBooksId =  OrgInformationUtil.getCurrentSetOfBookId();
            dto.setSetOfBooksId(setOfBooksId);
            ExpenseTypeIcon expenseTypeIcon = expenseTypeIconService.selectOne(
                    new EntityWrapper<ExpenseTypeIcon>()
                            .eq("icon_name",dto.getIconName())
                            .eq("enabled",true)
            );
            if(expenseTypeIcon != null) {
                dto.setIconUrl(expenseTypeIcon.getIconURL());
            }else{
                errorMessage.append(dto.getCode()+":图标名称未找到！");
            }
            String y = "Y";
            String n = "N";
            if (y.equals(dto.getEnabledStr())) {
                dto.setEnabled(Boolean.TRUE);
            } else if (n.equals(dto.getEnabledStr())) {
                dto.setEnabled(Boolean.FALSE);
            }
            List<ExpenseTypeCategory> categoryList = expenseTypeCategoryMapper.selectList(
                    new EntityWrapper<ExpenseTypeCategory>()
                        .eq("set_of_books_id",setOfBooksId)
                        .eq("name",dto.getTypeCategoryName())
            );
            if(categoryList.size() == 0){
                errorMessage.append(dto.getCode()+":分类名称未找到！");
            }else if(categoryList.size() == 1){
                dto.setTypeCategoryId(categoryList.get(0).getId());
            }else{
                errorMessage.append(dto.getCode()+":分类名称存在多个！");
            }
            if(y.equals(dto.getEntryModeStr())){
                dto.setEntryMode(Boolean.TRUE);
            }else if(n.equals(dto.getEntryModeStr())){
                dto.setEntryMode(Boolean.FALSE);
            }
            // 判断是不是申请类型
            if (null == dto.getTypeFlag() || TypeEnum.APPLICATION_TYPE.getKey().compareTo(dto.getTypeFlag()) == 0){
                dto.setSourceTypeId(null);
                dto.setTypeFlag(TypeEnum.APPLICATION_TYPE.getKey());
                dto.setAttachmentFlag(null);
            }else{
                dto.setTypeFlag(TypeEnum.COST_TYPE.getKey());
            }
            // 如果金额录入模式 为总金额
            if (dto.getEntryMode() == null || !dto.getEntryMode()){
                dto.setPriceUnit(null);
            }else{
                if (dto.getPriceUnit() == null){
                    errorMessage.append(dto.getCode()+":金额录入模式为单价*数量，请输入单位！");
                }
            }
            if(Integer.valueOf(1).equals(dto.getTypeFlag())){
                List<ExpenseType> list = baseMapper.selectList(new EntityWrapper<ExpenseType>()
                        .eq("code", dto.getSourceTypeCode())
                        .eq("set_of_books_id",dto.getSetOfBooksId())
                        .eq("type_flag", Integer.valueOf(0)));
                if(list.size() == 0){
                    errorMessage.append(dto.getCode()+":费用类型的申请类型代码找不到！");
                }else if(list.size() == 1){
                    dto.setSourceTypeId(list.get(0).getId());
                }else if(list.size() > 1){
                    errorMessage.append(dto.getCode()+":费用类型的申请类型代码存在多个！");
                }

            }
        }
        dto.setErrorMessage(errorMessage);
    }

    public String initExpenseType(List<ExpenseTypeInitDTO> expenseTypeInitDTOS){
        StringBuilder errorMessage = new StringBuilder();
        expenseTypeInitDTOS.forEach(item -> {
            checkExpenseTypeInitData(item);
            if("".equals(item.getErrorMessage().toString())){
                List<ExpenseType> temp = baseMapper.selectList(new EntityWrapper<ExpenseType>()
                        .eq("code", item.getCode())
                        .eq("set_of_books_id",item.getSetOfBooksId())
                        .eq("type_flag", item.getTypeFlag()));

                if (temp.size() == 0) {
                    ExpenseType expenseType = new ExpenseType();
                    BeanUtils.copyProperties(item,expenseType);
                    baseMapper.insert(expenseType);
                }else  if (temp.size() == 1) {
                    ExpenseType expenseTypeTemp = temp.get(0);
                    StringBuilder updateErrorMessage = new StringBuilder();
                    // 校验数据存不存在
                    if (null == expenseTypeTemp || expenseTypeTemp.getDeleted()){
                        updateErrorMessage.append(item.getCode()+":校验数据存不存在或者已删除！");
                    }
                    if("".equals(updateErrorMessage.toString())){
                        BeanUtils.copyProperties(item,expenseTypeTemp);
                        baseMapper.updateById(expenseTypeTemp);
                    }else{
                        errorMessage.append(updateErrorMessage);
                    }
                }
            }else{
                errorMessage.append(item.getErrorMessage());
            }
        });
        if ("".equals(errorMessage.toString())) {
            return "导入成功!";
        } else {
            return errorMessage.toString();
        }
    }
}
