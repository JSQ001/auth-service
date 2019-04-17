package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DepartmentPositionCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.ApprovalFormPropertyConstants;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.dto.*;
import com.hand.hcf.app.workflow.enums.ApprovalFormEnum;
import com.hand.hcf.app.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.persistence.ApprovalFormMapper;
import com.hand.hcf.app.workflow.util.ExceptionCode;
import com.hand.hcf.app.workflow.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApprovalFormService extends BaseService<ApprovalFormMapper, ApprovalForm> {


    @Autowired
    private BaseClient baseClient;
    @Autowired
    private ApprovalFormPropertyService approvalFormPropertyService;

    @Autowired
    private BrmsService brmsService;

    @Autowired
    private MapperFacade mapper;

    /**
     * 查询单个表单
     *
     * @param approvalFormQO
     * @return
     */
    public ApprovalFormDTO getDTOByQO(ApprovalFormQO approvalFormQO) {

        //基础查询
        List<ApprovalFormDTO> customFormList = baseMapper.listDTOByQO(approvalFormQO);
        if (CollectionUtils.isEmpty(customFormList)) {
            return null;
        }
        return customFormList.get(0);

    }

    public ApprovalForm getByOid(UUID formOid) {

        return selectOne(new EntityWrapper<ApprovalForm>()
                .eq("form_oid", formOid));

    }

    public ApprovalForm getById(Long formId) {

        return selectById(formId);

    }

    public List<ApprovalForm> listByOids(List<UUID> formOids) {

        return selectList(new EntityWrapper<ApprovalForm>()
                .in("form_oid", formOids));

    }

    public ApprovalFormDTO getCustomFormDetailForRule(UUID formOid) {

        if (formOid == null) {
            return null;
        }

        ApprovalFormDTO approvalFormDTO = getDTOByQO(ApprovalFormQO.builder().formOid(formOid).build());

        if (approvalFormDTO == null) {
            throw new BizException("SYS_FORM_Oid_NOT_EXIST");
        }
        consumateBrmsField(approvalFormDTO);
        /*if(approvalFormDTO!=null&& CollectionUtils.isNotEmpty(approvalFormDTO.getCustomFormFields()))
            approvalFormDTO.getCustomFormFields().forEach(x->{
                if(x != null && x.getFieldOid()!=null&&RuleConstants.CUSTOM_ROLE_DTO_MAP != null && RuleConstants.CUSTOM_ROLE_DTO_MAP.containsKey(x.getFieldOid()))
                    x.setFieldName(RuleConstants.CUSTOM_ROLE_DTO_MAP.get(x.getFieldOid()).stream().filter(y->y.getLanguage().equals(language)).findFirst().get().getFieldName());
            });*/
        return approvalFormDTO;
    }


    private void consumateBrmsField(ApprovalFormDTO approvalFormDTO) {
        List<FormFieldDTO> formFieldDTOs = new ArrayList<>();
        formFieldDTOs.add(RuleConstants.DEFAULT_APPLICANT_COMPANY_FIELD);// 申请人公司
        formFieldDTOs.add(RuleConstants.DEFAULT_DEPARTMENT_FIELD);//申请人部门
        formFieldDTOs.add(RuleConstants.DEFAULT_DOCUMENT_COMPANY_FIELD);//单据公司
        formFieldDTOs.add(RuleConstants.DEFAULT_DOCUMENT_DEPARTMENT_FIELD);// 单据部门
        formFieldDTOs.add(RuleConstants.DEFAULT_CURRENCY_FIELD);
        formFieldDTOs.add(RuleConstants.DEFAULT_AMOUNT_FIELD);
        formFieldDTOs.add(RuleConstants.DEFAULT_FUNCTION_AMOUNT_FIELD);
        formFieldDTOs.add(RuleConstants.DEFAULT_REMARK_FIELD);

        formFieldDTOs.add(RuleConstants.DEFAULT_DEPARTMENT_LEVEL_FIELD);
        formFieldDTOs.add(RuleConstants.DEFAULT_DEPARTMENT_PATH_FIELD);
        //部门角色多语言
        FormFieldDTO formFieldDTO = RuleConstants.DEFAULT_DEPARTMENT_ROLE_FIELD;
        try {
            List<DepartmentPositionCO> result = baseClient.listDepartmentPosition(approvalFormDTO.getTenantId());
            List<Long> ids = result.stream().map(departmentPosition -> departmentPosition.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(result) && result.size() > 0) {
                com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
                result.forEach(x -> {
                    jsonArray.add(new com.alibaba.fastjson.JSONObject() {{
                        put("id", x.getPositionCode());
                        put("name", x.getPositionName());
                    }});
                });
                formFieldDTO.setFieldContent(jsonArray.toString());
            }
        } catch (Exception ex) {
            log.info("工作流还不支持角色多语言");
        }
        formFieldDTOs.add(formFieldDTO);
        /**
         * 报销单增加默认字段,金额,费用类型
         * 申请单 包含预算时增加金额字段
         */
        approvalFormDTO.setFormFieldList(formFieldDTOs);
        log.info("customFormFieldDTOs size :" + formFieldDTOs);
    }

    /**
     * get One
     *
     * @param formOid
     * @return
     */
    public ApprovalFormDTO getCustomForm(UUID formOid) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Locale locale = new Locale(language);
        ApprovalFormDTO approvalFormDTO = null;
        if (formOid != null) {

            ApprovalForm form = getByOid(formOid);
            if (form != null) {
                approvalFormDTO = approvalFormToFormDTO(form);
            }
        }
        return approvalFormDTO;
    }


    //切换公司防止表单Oid套用
    public ApprovalFormDTO getCustomForm(Long companyId, UUID formOid) {
        if (formOid == null) {
            return null;
        }

        return getDTOByQO(ApprovalFormQO.builder().formOid(formOid)
                .companyId(companyId).build());
    }


    /**
     * 查询公司所有申请单和报销单(包含禁用)
     *
     * @param companyId
     * @return
     */
    public List<ApprovalForm> listByCompanyId(Long companyId) {
        List<ApprovalForm> approvalFormList = selectList(new EntityWrapper<ApprovalForm>()
                .eq("company_id", companyId)
                .eq("from_type", ApprovalFormEnum.CUSTOMER_FROM_COMPANY.getId())
                .orderBy("id"));
        return approvalFormList;
    }

    public List<ApprovalFormDTO> listByTenantId(Long tenantId) {
        List<ApprovalForm> list = selectList(new EntityWrapper<ApprovalForm>()
                .where("form_type_id IN (1001,1002,2001,2002,2003,2004,2005,3001, 3002, 3003)")
                .eq("tenant_id", tenantId));
        list.sort(Comparator.comparing(ApprovalForm::getCreatedDate).thenComparing(ApprovalForm::getId));
        //list = baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(list, ApprovalForm.class);
        List<ApprovalFormDTO> formDTOList = list.stream().map(u -> approvalFormToFormDTO(u)).collect(Collectors.toList());
        return formDTOList;
    }

    /**
     * 查询租户创建的表单
     *
     * @param tenantId
     * @return
     */
    public List<ApprovalForm> listDTOByTenantAndCondition(Long tenantId, Long formTypeId, String formName, String remark, Boolean valid, Page page) {
        return selectPage(page, new EntityWrapper<ApprovalForm>()
                .eq("tenant_id", tenantId)
                .eq("from_type", ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId())
                .eq(formTypeId != null, "form_type_id", formTypeId)
                .like(StringUtils.isNotEmpty(formName), "form_name", formName)
                .like(StringUtils.isNotEmpty(remark), "remark", remark)
                .eq(valid != null, "valid", valid)
                .orderBy("id")).getRecords();

    }

    /**
     * 查询租户创建的表单
     *
     * @return
     */
    public List<ApprovalForm> listByCondition(List<Long> formTypeIds, String formName, String remark, Page page) {
        return selectPage(page, new EntityWrapper<ApprovalForm>()
                .eq("from_type", ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId())
                .in(formTypeIds != null && formTypeIds.size() > 0, "form_type_id", formTypeIds)
                .like(StringUtils.isNotEmpty(formName), "form_name", formName)
                .like(StringUtils.isNotEmpty(remark), "remark", remark)
                .eq("valid", RuleApprovalEnum.VALID.getId())
                .orderBy("id")).getRecords();

    }





    /**
     * create
     *
     * @param approvalFormDTO
     * @return
     */
    @Transactional
    public ApprovalFormDTO createCustomForm(ApprovalFormDTO approvalFormDTO, UUID userOid, Long tenantId) {

        if (StringUtils.isEmpty(approvalFormDTO.getFormName())) {
            throw new BizException(ExceptionCode.SYS_PARAM_CANT_BE_NULL, new Object[]{"formName"});
        }
        if (StringUtils.isEmpty(approvalFormDTO.getRemark())) {
            throw new BizException(ExceptionCode.SYS_PARAM_CANT_BE_NULL, new Object[]{"remark"});
        }
        ApprovalForm approvalForm = approvalFormDTOToForm(approvalFormDTO);


        approvalForm.setFormOid(UUID.randomUUID());
        approvalForm.setAsSystem(Boolean.FALSE);
        approvalForm.setTenantId(tenantId);
        approvalForm.setFromType(approvalFormDTO.getFromType() == null ? ApprovalFormEnum.CUSTOMER_FROM_COMPANY.getId() : approvalFormDTO.getFromType());

        ApprovalForm associateForm = new ApprovalForm();

        approvalForm.setId(null);

        approvalForm.setApprovalMode(ApprovalMode.CUSTOM.getId());

        try {
            insert(approvalForm);//保存customForm和formI18n
        } catch (DuplicateKeyException e) {
            throw new BizException(ExceptionCode.CUSTOM_FORM_NAME_EXIST);
        }


        additionalOperation(approvalForm.getFormOid());

        //对所有单据生效
        List<UUID> uuids = new ArrayList<>();
        uuids.add(approvalForm.getFormOid());
        if (associateForm.getFormOid() != null) {
            uuids.add(associateForm.getFormOid());
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(ApprovalFormPropertyConstants.COUNTERSIGN_TYPE, ApprovalFormPropertyInitDTO.COUNTERSIGN_TYPE_PROPERTY_VALUE);
        map.put(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN, Boolean.TRUE.toString());
        map.put(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN_FOR_SUBMITTER, Boolean.FALSE.toString());
        approvalFormPropertyService.saveList(
                new ApprovalFormPropertyInitDTO(map).getApprovalFormPropertyList()
                , uuids);
        ApprovalFormDTO results = approvalFormToFormDTO(approvalForm);
        return results;
    }


    /**
     * 新建表单额外的操作
     *
     * @param formOid
     */

    @Transactional
    public void additionalOperation(UUID formOid) {
        //1.change approval mode
        //do not checkData for distributed transaction
        RuleApprovalChainDTO ruleApprovalChainDTO = brmsService.changeApprovalMode(RuleApprovalChainDTO.builder().approvalMode(ApprovalMode.CUSTOM.getId()).formOid(formOid).checkData(Boolean.FALSE).build());
        //2.add an end node
        brmsService.addAnEndNode(RuleApprovalNodeDTO.builder().printFlag(Boolean.TRUE).name("结束").remark("结束").typeNumber(RuleApprovalEnum.NODE_TYPE_EED.getId()).ruleApprovalChainOid(ruleApprovalChainDTO.getRuleApprovalChainOid()).build());
        //表单更新approvalMode

    }


    @Transactional
    public void synchronizeApprovalMode(UUID formOid, Integer approvalMode, Long tenantId) {
        ApprovalForm approvalForm = getByOid(formOid);
        if (approvalForm != null) {
            if (approvalForm.getTenantId().equals(tenantId)) {
                approvalForm.setApprovalMode(approvalMode);
                updateById(approvalForm);
            } else {
                log.error("updateApprovalMode,authority is error");
                throw new BizException("authority is error");
            }
        } else {
            log.error("updateApprovalMode,approvalForm is null");
            throw new BizException("approvalForm is null");
        }

    }

    /**
     * 查询公司所有表单 （包含禁用）
     *
     * @return
     */
    public List<ApprovalFormDTO> listDTOByUserOid(UUID userOid) {
        return listDTO();
    }

    public List<ApprovalFormDTO> listDTO() {


        return baseMapper.listDTOByQO(ApprovalFormQO.builder().build());
    }


    public List<ApprovalFormDTO> listTenantForms(Long tenantId, Long formTypeId, String formName) {
        List<ApprovalForm> list = null;
        list = selectList(new EntityWrapper<ApprovalForm>()
                .eq("tenant_id", tenantId)
                .like(!StringUtil.isNullOrEmpty(formName), "form_name", formName)
                .eq(formTypeId != null, "form_type_id", formTypeId));
        //list = baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(list, ApprovalForm.class);

        if (CollectionUtils.isNotEmpty(list)) {
            List<ApprovalFormDTO> approvalFormDTOList = list.stream().map(c -> approvalFormToFormDTO(c)).collect(Collectors.toList());
            return approvalFormDTOList;
        }
        return new ArrayList<>();
    }

    /**
     * 根据表单编码和租户id获取表单信息
     *
     * @param formCode：表单编码
     * @param tenantId：租户id
     * @return
     */
    public ApprovalFormDTO getDTOByFormCode(String formCode, Long tenantId) {
        return getDTOByQO(ApprovalFormQO.builder().formCode(formCode)
                .tenantId(tenantId).build());
    }

    public List<ApprovalFormDTO> listDTOByCompanyOidAndFormType(UUID companyOid, Integer formType) {
        if (companyOid == null && formType == null) {
            return null;
        }

        return baseMapper.listDTOByQO(ApprovalFormQO.builder().companyOid(companyOid)
                .formTypeId(formType).build());
    }


    /**
     * 根据账套id和表单类型查询租户创建的表单
     *
     * @return
     */
    public List<ApprovalForm> listTenantCustomByType() {

        List<ApprovalForm> allList = selectList(new EntityWrapper<ApprovalForm>()
                .eq("from_type", ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId())
                .eq("valid", RuleApprovalEnum.VALID.getId()));
        if (CollectionUtils.isNotEmpty(allList)) {
            List<ApprovalForm> list = new ArrayList<>();
            allList.forEach(c -> {
                    list.add(c);
            });
            return list;
        }
        return allList;
    }


    //申请表单列表
    public List<ApprovalFormDTO> listCustomForms() {
        List<ApprovalForm> list = listTenantCustomByType();
        List<ApprovalFormDTO> result = list.stream().map(c -> approvalFormToFormDTO(c)).collect(Collectors.toList());
        return result;
    }

    /**
     * 用户查询可用表单（新费用申请查看查询表单接口）
     *
     * @return
     */
    public List<ApprovalFormSummaryDTO> listSummaryForm() {

        List<ApprovalFormDTO> dtos = this.listCustomForms();
        List<ApprovalFormSummaryDTO> result = dtos.stream().map(c -> approvalFormDTOToSummaryDTO(c)).collect(Collectors.toList());
        return result;
    }

    /**
     * app 端查询表单字段
     *
     * @param formOid
     * @return
     */
    public ApprovalFormDTO getDTOByOid(UUID formOid) {

        if (formOid == null) {
            return null;
        }
        ApprovalForm approvalForm = getByOid(formOid);
        if (approvalForm == null) {
            return null;
        }
        ApprovalFormDTO result = approvalFormToFormDTO(approvalForm);

        return result;
    }

    /**
     * 给 表单 提供，表单 新增更新关联申请单类型
     * 获取某个 表单 下，当前账套下 已分配的、未分配的 申请单类型
     *
     * @param forOtherRequestDTO
     * @param page
     * @return
     */
    public Page<ApprovalFormDTO> pageByRangeForForm(ApprovalFormForOtherRequestDTO forOtherRequestDTO, Page page) {
        List<ApprovalFormDTO> list = new ArrayList<>();
        //全部：all、已选：selected、未选：notChoose
        if (forOtherRequestDTO.getRange().equals("selected")) {
            if (forOtherRequestDTO.getIdList().size() == 0) {
                page.setRecords(list);
                return page;
            } else {
                List<ApprovalForm> returns = selectPage(page,
                        new EntityWrapper<ApprovalForm>()
                                .eq("valid", true)
                                .eq("company_id", forOtherRequestDTO.getCompanyId())
                                .eq("form_type_id", 2002)
                                .in("id", forOtherRequestDTO.getIdList())
                                .like(forOtherRequestDTO.getFormCode() != null, "form_code", forOtherRequestDTO.getFormCode())
                                .like(forOtherRequestDTO.getFormName() != null, "form_name", forOtherRequestDTO.getFormName())
                                .orderBy("form_code")
                ).getRecords();
                returns.stream().forEach(customForm -> {
                    ApprovalFormDTO approvalFormDTO = new ApprovalFormDTO();
                    mapper.map(customForm, approvalFormDTO);
                    approvalFormDTO.setAssigned(true);
                    list.add(approvalFormDTO);
                });
            }
        } else if (forOtherRequestDTO.getRange().equals("notChoose")) {
            List<ApprovalForm> returns = selectPage(page,
                    new EntityWrapper<ApprovalForm>()
                            .eq("valid", true)
                            .eq("company_id", forOtherRequestDTO.getCompanyId())
                            .eq("form_type_id", 2002)
                            .notIn("id", forOtherRequestDTO.getIdList())
                            .like(forOtherRequestDTO.getFormCode() != null, "form_code", forOtherRequestDTO.getFormCode())
                            .like(forOtherRequestDTO.getFormName() != null, "form_name", forOtherRequestDTO.getFormName())
                            .orderBy("form_code")
            ).getRecords();
            returns.stream().forEach(customForm -> {
                ApprovalFormDTO approvalFormDTO = new ApprovalFormDTO();
                mapper.map(customForm, approvalFormDTO);
                approvalFormDTO.setAssigned(false);
                list.add(approvalFormDTO);
            });
        } else if (forOtherRequestDTO.getRange().equals("all")) {
            List<ApprovalFormDTO> temp = new ArrayList<>();
            if (forOtherRequestDTO.getIdList().size() > 0) {
                List<ApprovalForm> list1 = this.selectList(
                        new EntityWrapper<ApprovalForm>()
                                .eq("valid", true)
                                .eq("company_id", forOtherRequestDTO.getCompanyId())
                                .eq("form_type_id", 2002)
                                .in("id", forOtherRequestDTO.getIdList())
                                .like(forOtherRequestDTO.getFormCode() != null, "form_code", forOtherRequestDTO.getFormCode())
                                .like(forOtherRequestDTO.getFormName() != null, "form_name", forOtherRequestDTO.getFormName())
                                .orderBy("form_code")
                );
                list1.stream().forEach(customForm -> {
                    ApprovalFormDTO approvalFormDTO = new ApprovalFormDTO();
                    mapper.map(customForm, approvalFormDTO);
                    approvalFormDTO.setAssigned(true);
                    temp.add(approvalFormDTO);
                });
            }
            List<ApprovalForm> list2 = this.selectList(
                    new EntityWrapper<ApprovalForm>()
                            .eq("valid", true)
                            .eq("form_type_id", 2002)
                            .eq("from_type", 2)  // 1代表数据公司 2代表数据账套
                            .notIn("id", forOtherRequestDTO.getIdList())
                            .like(forOtherRequestDTO.getFormCode() != null, "form_code", forOtherRequestDTO.getFormCode())
                            .like(forOtherRequestDTO.getFormName() != null, "form_name", forOtherRequestDTO.getFormName())
                            .orderBy("form_code")
            );
            list2.stream().forEach(customForm -> {
                ApprovalFormDTO approvalFormDTO = new ApprovalFormDTO();
                mapper.map(customForm, approvalFormDTO);
                approvalFormDTO.setAssigned(false);
                temp.add(approvalFormDTO);
            });
            page.setTotal(temp.size());
            if (temp.size() < page.getSize() * page.getCurrent()) {
                for (int i = (page.getCurrent() - 1) * page.getSize(); i < temp.size(); i++) {
                    list.add(temp.get(i));
                }
            } else {
                for (int i = (page.getCurrent() - 1) * page.getSize(); i < page.getCurrent() * page.getSize(); i++) {
                    list.add(temp.get(i));
                }
            }
        }

        page.setRecords(list);
        return page;
    }

    /**
     * update,
     *
     * @param dto
     * @return
     */
    public ApprovalForm updateApprovalForm(ApprovalForm dto, UUID userOid) {
        //oid校验
        if (dto.getFormOid() == null) {
            throw new ValidationException(new ValidationError("formOid", "formOid is missing"));
        }

        ApprovalForm persistent = getByOid(dto.getFormOid());
        //是否存在
        if (persistent == null) {
            throw new ObjectNotFoundException(ApprovalForm.class, dto.getFormOid());
        }

        // 可修改的字段
        if (dto.getValid() != null) {
            persistent.setValid(dto.getValid());
        }
        if (dto.getAsSystem() != null) {
            persistent.setAsSystem(dto.getAsSystem());
        }
        //表单名称
        if (!StringUtils.isEmpty(dto.getFormName())) {
            persistent.setFormName(dto.getFormName());
        }
        //图标
        if (!StringUtils.isEmpty(dto.getIconName())) {
            persistent.setIconName(dto.getIconName());
        }
        //name
        if (!StringUtils.isEmpty(dto.getMessageKey())) {
            persistent.setMessageKey(dto.getMessageKey());
        }
        //备注
        if (!StringUtils.isEmpty(dto.getRemark())) {
            persistent.setRemark(dto.getRemark());
        }
        persistent.setReferenceOid(dto.getReferenceOid());

        //表单更新
        persistent.setI18n(dto.getI18n());
        try {
            updateById(persistent);
        } catch (DuplicateKeyException e) {
            throw new BizException(ExceptionCode.CUSTOM_FORM_NAME_EXIST);
        }


        return persistent;
    }

    /**
     * 更新表单--合并service
     *
     * @param customFormMergeDTO
     * @param userOID
     * @return
     */
    public ApprovalFormAllDTO updateApprovalForm(ApprovalFormAllDTO customFormMergeDTO, UUID userOID) {
        ApprovalForm approvalFormDTO = null;
        //1.更新表单
        if (customFormMergeDTO.getApprovalFormDTO() != null) {
            approvalFormDTO = this.updateApprovalForm(customFormMergeDTO.getApprovalFormDTO(), userOID);
        }


        return ApprovalFormAllDTO.builder().approvalFormDTO(approvalFormDTO).build();
    }


    public List<ApprovalFormSummaryDTO> listByFormType(Long tenantId, Integer formTypeId) {
        List<ApprovalFormSummaryDTO> result = new ArrayList<>();
        result = this.selectList(
                new EntityWrapper<ApprovalForm>()
                        .eq("valid", 1)
                        .eq("as_system", 0)
                        .eq(formTypeId != null, "form_type_id", formTypeId)
                        .eq("tenant_id", tenantId)
        ).stream().map(approvalForm -> {
            ApprovalFormSummaryDTO approvalFormSummaryDTO = approvalFormToSummaryDTO(approvalForm);
            return approvalFormSummaryDTO;
        }).collect(Collectors.toList());
        return result;
    }


    public static ApprovalFormDTO approvalFormToFormDTO(ApprovalForm approvalForm) {
        if (approvalForm == null) {
            return null;
        }
        ApprovalFormDTO result = new ApprovalFormDTO();
        result.setId(approvalForm.getId());
        result.setFormOid(approvalForm.getFormOid());
        result.setFormName(approvalForm.getFormName());
        result.setIconName(approvalForm.getIconName());
        result.setFormType(approvalForm.getFormTypeId());
        result.setMessageKey(approvalForm.getMessageKey());
        result.setValid(approvalForm.getValid());
        result.setAsSystem(approvalForm.getAsSystem());
        result.setCreatedDate(approvalForm.getCreatedDate());
        result.setParentId(approvalForm.getParentId());
        result.setRemark(approvalForm.getRemark());
        result.setReferenceOid(approvalForm.getReferenceOid());
        result.setFormCode(approvalForm.getFormCode());
        result.setTenantId(approvalForm.getTenantId());
        result.setIconUrl(approvalForm.getIconUrl());
        result.setFromType(approvalForm.getFromType());
        result.setApprovalMode(approvalForm.getApprovalMode());
        result.setI18n(approvalForm.getI18n());

        return result;
    }

    public static ApprovalForm approvalFormDTOToForm(ApprovalFormDTO approvalFormDTO) {
        if (approvalFormDTO == null) {
            return null;
        }
        ApprovalForm approvalForm = new ApprovalForm();
        approvalForm.setAsSystem(approvalFormDTO.getAsSystem());
        approvalForm.setFormName(approvalFormDTO.getFormName());
        approvalForm.setValid(approvalFormDTO.getValid());
        approvalForm.setCreatedDate(approvalFormDTO.getCreatedDate());
        approvalForm.setFormOid(approvalFormDTO.getFormOid());
        approvalForm.setIconName(approvalFormDTO.getIconName());
        approvalForm.setMessageKey(approvalFormDTO.getMessageKey());
        approvalForm.setFormTypeId(approvalFormDTO.getFormType());
        approvalForm.setParentId(approvalFormDTO.getParentId());
        approvalForm.setRemark(approvalFormDTO.getRemark());
        approvalForm.setReferenceOid(approvalFormDTO.getReferenceOid());
        approvalForm.setFormCode(approvalFormDTO.getFormCode());
        approvalForm.setFromType(approvalFormDTO.getFromType());
        approvalForm.setI18n(approvalFormDTO.getI18n());
        approvalForm.setFormOid(approvalFormDTO.getFormOid());
        approvalForm.setId(approvalFormDTO.getId());
        approvalForm.setSubmitFlag(approvalFormDTO.getSubmitFlag());
        approvalForm.setApprovalMode(approvalFormDTO.getApprovalMode());

        return approvalForm;
    }


    public static ApprovalFormSummaryDTO approvalFormDTOToSummaryDTO(ApprovalFormDTO dto) {
        if (dto == null) {
            return null;
        }
        ApprovalFormSummaryDTO result = new ApprovalFormSummaryDTO();
        result.setFormOid(dto.getFormOid());
        result.setFormName(dto.getFormName());
        result.setFormType(dto.getFormType());
        result.setIconName(dto.getIconName());
        result.setMessageKey(dto.getMessageKey());
        result.setFormId(dto.getId());

        return result;
    }

    public static ApprovalFormSummaryDTO approvalFormToSummaryDTO(ApprovalForm approvalForm) {
        if (approvalForm == null) {
            return null;
        }
        ApprovalFormSummaryDTO result = new ApprovalFormSummaryDTO();
        result.setFormId(approvalForm.getId());
        result.setFormOid(approvalForm.getFormOid());
        result.setFormName(approvalForm.getFormName());
        result.setIconName(approvalForm.getIconName());
        result.setMessageKey(approvalForm.getMessageKey());
        result.setFormType(approvalForm.getFromType());
        return result;
    }

}