package com.hand.hcf.app.workflow.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleNextApproverResult;
import com.hand.hcf.app.workflow.brms.enums.ApprovalMode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.LocaleMessageConstants;
import com.hand.hcf.app.workflow.constant.ValueConstants;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.AssembleBrmsParamsRespDTO;
import com.hand.hcf.app.workflow.dto.chain.UserApprovalDTO;
import com.hand.hcf.app.workflow.dto.form.FormValueDTO;
import com.hand.hcf.app.workflow.enums.ApprovalPathModeEnum;
import com.hand.hcf.app.workflow.enums.form.BusinessColumnMessageKey;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.StringUtil;
import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("defaultWorkflowIntegrationServiceImpl")
public class DefaultWorkflowIntegrationServiceImpl {

    private final Logger log = LoggerFactory.getLogger(DefaultWorkflowIntegrationServiceImpl.class);

    @Autowired
    WorkflowRulesSettingService workflowRulesSettingService;

    @Autowired
    BrmsService brmsService;

    @Autowired
    private BaseClient baseClient;




    public List<String> getWorkflowApprovalPath(UUID userOid, UUID entityOid) {
        CompanyCO company = baseClient.getCompanyByUserOid(userOid);
        CompanyConfigurationCO companyConfiguration = baseClient.getCompanyConfigByCompanyOid(company.getCompanyOid());
        if (companyConfiguration==null) {//公司配置不存在
            throw new ValidationException(new ValidationError("CompanyConfiguration.not.exist", "CompanyConfiguration.not.exist"));
        }

        return this.getWorkflowApprovalPath(userOid, entityOid,
                companyConfiguration.getApprovalMode(),
                companyConfiguration.getDepartmentLevel());
    }

    public List<String> getWorkflowApprovalPath(UUID userOid, UUID entityOid, int approvalMode, int departmentLevel) {
        CompanyCO company = baseClient.getCompanyByUserOid(userOid);
        List<String> result = new ArrayList<>();
        String approvalOids = "";
        UUID chooseDepartment = null;// 获取单据 部门OID
        UUID chooseCostCenterItem = null;
        boolean isUserPick = approvalMode == ApprovalMode.USER_PICK.getId();


        if (approvalMode == ApprovalMode.DEPARTMENT.getId()) {//选部门审批
            result=baseClient.getDepartmentPath(userOid,chooseDepartment,departmentLevel);

        } else if (approvalMode == ApprovalMode.USER_PICK.getId()) {//选人审批
            if (StringUtils.isNotBlank(approvalOids)) {
                List<String> userPick = new ArrayList<>(Arrays.asList(approvalOids.split(ValueConstants.WORKFLOW_APPROVAL_SPLIT)));
                result = userPick;
            }
        }

        if (CollectionUtils.isEmpty(result)) {//从审批规则获取

                result = workflowRulesSettingService.getWorkflowRulesSettingRoleListNoCondition(company.getCompanyOid(), chooseDepartment, chooseCostCenterItem, userOid, StringUtil.getStringValue(approvalOids), 1001);
        }


        if (CollectionUtils.isEmpty(result)) {
            log.info("====userOid:" + userOid + "  " + " entityOid:" + entityOid + "=======workflowRulesSettingRoleList.empty");
            throw new BizException(LocaleMessageConstants.SYS_APPROVAL_CHAIN_IS_NULL);
        } else {

            /**
             * 替换申请人
             */
            String applicantOidStirng = userOid.toString();
            if (result.contains(applicantOidStirng)) {
                //根据profile获取自审批规则
                int approvalRuleSelfSkip = baseClient.getApprovalRuleSelfSkip(company.getCompanyOid());
                /**
                 * 选人模式 默认可以自审批
                 * 其他 默认 不可自审批
                 */
                if (isUserPick) {
                    if (approvalRuleSelfSkip==0){
                        approvalRuleSelfSkip=RuleApprovalEnum.RULE_SELFAPPROVAL_NOT_SKIP.getId();
                    }
                } else {
                    if (approvalRuleSelfSkip==0){
                        approvalRuleSelfSkip=RuleApprovalEnum.RULE_SELFAPPROVAL_SUPERIOR_MANAGER.getId();
                    }
                }
                switch (RuleApprovalEnum.parse(approvalRuleSelfSkip)) {
                    case RULE_SELFAPPROVAL_SKIP:
                        result.remove(applicantOidStirng);
                        break;
                    case RULE_SELFAPPROVAL_NOT_SKIP:
                        break;
                    case RULE_SELFAPPROVAL_SUPERIOR_MANAGER:
                        UUID managerOid = baseClient.getLastDepartmentManagerByApplicantOid(userOid);
                        if (managerOid == null) {
                            log.info("SELFAPPROVAL ,replace approver error ,applicatOid : {} ", applicantOidStirng);
                            throw new BizException(LocaleMessageConstants.SYS_APPROVAL_CHANGE_APPLICANT_ERROR);
                        }
                        result.set(result.indexOf(applicantOidStirng), managerOid.toString());
                        break;
                    case RULE_SELFAPPROVAL_CHARGE_MANAGER:
                        //TODO

                        break;
                }
            }
        }
        return result;
    }



    public List<String> getWorkflowNextApprovalPath(UUID userOid, UUID entityOid, UUID lastApproval) {


        List<String> list = this.getWorkflowApprovalPath(userOid, entityOid).stream().collect(Collectors.toList());
        if (lastApproval == null) {//第一次获取审批人
            return Arrays.asList(list.get(0));
        } else {
            LinkedList<String> linkedList = new LinkedList();
            HashSet<String> hashSet = new HashSet();//处理重复的人
            for (String user : list) {
                if (hashSet.add(user)) {
                    linkedList.add(user);
                }
            }
            int sequence = linkedList.indexOf(lastApproval.toString());
            if (sequence != -1 && sequence + 1 < linkedList.size()) {
                return Arrays.asList(linkedList.get(sequence + 1));
            } else if (sequence + 1 == linkedList.size()) {//最后一个审批
                return new ArrayList<>();
            } else {
                return Arrays.asList(linkedList.get(0));//审批规则改变从头获取
            }
        }
    }


    public ApprovalPathModeEnum getCompanyApprovalPathMode(UUID companyOid) {
        CompanyConfigurationCO companyConfiguration = baseClient.getCompanyConfigByCompanyOid(companyOid);
        return ApprovalPathModeEnum.parse(companyConfiguration.getApprovalPathMode());
    }

    private void getRuleApproverUserOids(RuleApprovalNodeDTO ruleApprovalNodeDTO, DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        //查找具体审批人
        log.info("DefaultWorkflowIntegrationServiceImpl->getRuleApproverUserOids->request:ruleApprovalNodeDTO:{};droolsRuleApprovalNodeDTO:{}", JSONObject.toJSONString(ruleApprovalNodeDTO), JSONObject.toJSONString(droolsRuleApprovalNodeDTO));
        List<RuleApproverDTO> ruleApproverDTOS = ruleApprovalNodeDTO.getRuleApprovers();
        droolsRuleApprovalNodeDTO.setRuleApproverDTOs(ruleApproverDTOS);
        droolsRuleApprovalNodeDTO.setRuleApprovalNodeDTO(ruleApprovalNodeDTO);

        Map<String, Set<UUID>> ruleApproverMap = getApproverUserOids(droolsRuleApprovalNodeDTO.getRuleApproverDTOs(), droolsRuleApprovalNodeDTO.getFormValues(), droolsRuleApprovalNodeDTO.getApplicantOid(),  droolsRuleApprovalNodeDTO);
        Set<UUID> countersignApprover = new HashSet<>(buildApproverByRule(ruleApprovalNodeDTO, ruleApproverMap, droolsRuleApprovalNodeDTO.getApplicantOid(), new ArrayList<>(ruleApproverMap.get(ValueConstants.COUNTERSIGN_APPROVER))));
        Set<UUID> approver = new HashSet<>(buildApproverByRule(ruleApprovalNodeDTO, ruleApproverMap, droolsRuleApprovalNodeDTO.getApplicantOid(), new ArrayList<>(ruleApproverMap.get(ValueConstants.APPROVER))));
        //没有找到审批人逻辑处理
        if (RuleConstants.RULE_NULLABLE_THROW.equals(ruleApprovalNodeDTO.getNullableRule()) ) {
            throw new BizException(LocaleMessageConstants.SYS_APPROVAL_NO_APPROVER);
        }
        ruleApproverMap.put(ValueConstants.COUNTERSIGN_APPROVER, countersignApprover);
        ruleApproverMap.put(ValueConstants.APPROVER, approver);
        log.info("DefaultWorkflowIntegrationServiceImpl->getRuleApproverUserOids->ruleApproverMap:" + JSONObject.toJSONString(ruleApproverMap));
        ruleApprovalNodeDTO.setRuleApproverMap(ruleApproverMap);
    }

    public List<UUID> buildApproverByRule(RuleApprovalNodeDTO ruleApprovalNodeDTO, Map<String, Set<UUID>> ruleApproverMap, UUID applicantOid, List<UUID> approvers) {
        if (approvers != null) {
            approvers.remove(null);
        }
        //审批人包含自己
        if (ruleApproverMap != null
                && CollectionUtils.isNotEmpty(approvers)
                && approvers.contains(applicantOid)) {
            switch (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getSelfApprovalRule())) {
                case RULE_SELFAPPROVAL_SKIP:
                    approvers.remove(applicantOid);
                    ruleApprovalNodeDTO.setContainsSelfApprovalSkip(true);
                    break;
                case RULE_SELFAPPROVAL_NOT_SKIP:
                    break;
                case RULE_SELFAPPROVAL_SUPERIOR_MANAGER:
                    UUID managerOid = baseClient.getLastDepartmentManagerByApplicantOid(applicantOid, Boolean.FALSE);
                    if (managerOid == null) {
                        log.info("SELFAPPROVAL ,replace approver error ,applicantOid : {} ", applicantOid);
                        throw new BizException(LocaleMessageConstants.SYS_APPROVAL_CHANGE_APPLICANT_ERROR);
                    }
                    approvers.set(approvers.indexOf(applicantOid), managerOid);
                    log.info("SELFAPPROVAL ,replace approver from {} to {} ", applicantOid, managerOid);
                    break;
                case RULE_SELFAPPROVAL_PEER_MANAGER:
                    UUID peerManagerOid = baseClient.getLastDepartmentManagerByApplicantOid(applicantOid, Boolean.TRUE);
                    if (peerManagerOid == null) {
                        log.info("SELFAPPROVAL ,replace approver error ,applicantOid : {} ", applicantOid);
                        throw new BizException(LocaleMessageConstants.SYS_APPROVAL_CHANGE_APPLICANT_ERROR);
                    }
                    approvers.set(approvers.indexOf(applicantOid), peerManagerOid);
                    log.info("SELFAPPROVAL ,replace approver from {} to {} ", applicantOid, peerManagerOid);
                    break;
                case RULE_SELFAPPROVAL_CHARGE_MANAGER:
                    //TODO
                    break;
                default:
                    throw new BizException(LocaleMessageConstants.SYS_APPROVAL_CHANGE_APPLICANT_ERROR);
            }
        }
        if (approvers != null) {
            approvers.remove(null);
        }
        return approvers;
    }

    /**
     * 拼接部门角色属性
     * 申请人所担任的部门角色,多个时用分隔符|
     *
     * @param departmentId
     * @param applicantOid
     * @return
     */
    public String getDepartmentRoleStringByDepartmentPosition(Long departmentId, UUID applicantOid) {
        StringBuilder departmentRoleString = new StringBuilder();
        List<DepartmentPositionCO> departmentPositions = baseClient.getDepartmentPositionByUserAndDepartment(departmentId, applicantOid);
        if (CollectionUtils.isNotEmpty(departmentPositions)) {
            for (DepartmentPositionCO departmentPosition : departmentPositions) {
                departmentRoleString.append(departmentPosition.getPositionCode()).append(ValueConstants.WORKFLOW_APPROVAL_SPLIT);
            }
        }
        return departmentRoleString.length() > 0 ? departmentRoleString.substring(0, departmentRoleString.length() - 1) : "";
    }



    /**
     * 根据审批者定义RuleConstants 查出用户列表
     *
     * @param ruleApproverDTOs
     * @param customFormValueDTOs
     * @param applicantOid
     * @return
     */
    public Map<String, Set<UUID>> getApproverUserOids(List<RuleApproverDTO> ruleApproverDTOs, List<FormValueDTO> customFormValueDTOs, UUID applicantOid, DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        HashMap<String, Set<UUID>> map = new HashMap<>();
        map.put("countersignApprover", new HashSet<>());  //会签审批人
        map.put("approver", new HashSet<>());
        UserApprovalDTO applicant = baseClient.getUserByUserOid(applicantOid);
        if (applicant == null) {
            throw new ObjectNotFoundException(UserApprovalDTO.class, applicantOid);
        }
        //用户所在部门
        DepartmentCO userDepartment = baseClient.getDepartmentByUserOid(applicantOid);
        //用户所在部门Oid
        UUID userDepartmentOid = null;
        if (userDepartment != null) {
            userDepartmentOid = userDepartment.getDepartmentOid();
        }
        //获取自定义表单中的部门字段

        FormValueDTO departmentField = null;
        if (CollectionUtils.isNotEmpty(getCustomFormValueByCode(customFormValueDTOs, BusinessColumnMessageKey.SELECT_DEPARTMENT.getKey()))) {
            departmentField = getCustomFormValueByCode(customFormValueDTOs, BusinessColumnMessageKey.SELECT_DEPARTMENT.getKey()).get(0);
        }

        UUID departmentOid = null;
        if (departmentField == null) {
            departmentOid = userDepartmentOid;
        } else {
            if (departmentField.getFieldOid() != null) {
                departmentOid = departmentField.getFieldOid();
            } else {
                departmentOid = UUID.fromString(departmentField.getValue());
            }
        }
        Map<String, Object> entityData = droolsRuleApprovalNodeDTO.getEntityData();
        //获取实体对应的分摊的部门 及成本中心
        List<UUID> apportionmentDepartmentOids = null;
        if (entityData != null) {
            apportionmentDepartmentOids = (List<UUID>) entityData.get(ValueConstants.APPORTIONMENT_DEPARTMENTS);
        }
        // 结束
        if (ruleApproverDTOs != null && ruleApproverDTOs.size() > 0) {
            for (RuleApproverDTO ruleApproverDTO : ruleApproverDTOs) {
                getApproverOidsByRuleApproverDTO(ruleApproverDTO, userDepartmentOid, departmentOid, apportionmentDepartmentOids, map, applicantOid);
            }
        }
        return map;
    }




    //根据单条审批规则获取审批人Oid
    private void getApproverOidsByRuleApproverDTO(RuleApproverDTO ruleApproverDTO, UUID userDepartmentOid, UUID departmentOid,
                                                  List<UUID> apportionmentDepartmentOids,
                                                   HashMap<String, Set<UUID>> map, UUID applicantOid) {
        Set<UUID> approverUserOids = new HashSet<>();
        Set<UUID> apportionApprovalUserOids = new HashSet<>();
        Integer approverType = ruleApproverDTO.getApproverType();
        Integer departmentType = ruleApproverDTO.getDepartmentType();
        DepartmentCO department = null;
        Boolean withApportionment = Boolean.FALSE;

        switch (approverType) {
            //按人审批，直接返回审批规则中配置的审批人Oid
            case RuleConstants.APPROVAL_TYPE_USER:
                approverUserOids.add(ruleApproverDTO.getApproverEntityOid());
                break;
            case RuleConstants.APPROVAL_TYPE_DEPARTMENT_ROBOT: {
                log.info("debug->getApproverOidsByRuleApproverDTO->APPROVAL_TYPE_DEPARTMENT_ROBOT：{};getApproverEntityOid:{}", JSONObject.toJSONString(approverType), JSONObject.toJSONString(ruleApproverDTO.getApproverEntityOid()));
                approverUserOids.add(ruleApproverDTO.getApproverEntityOid());
                break;
            }
            //用户组 ，查找审批规则中配置的用户组的所有用户
            case RuleConstants.APPROVAL_TYPE_USERGROUP:
                approverUserOids.addAll(baseClient.listUserByUserGroupOid(ruleApproverDTO.getApproverEntityOid())
                        .stream().map(UserApprovalDTO::getUserOid).collect(Collectors.toList()));
                log.info("Look up by APPROVAL_TYPE_USERGROUP,approvers size:{},detail:{}", approverUserOids.size(), approverUserOids);
                break;

            //申请人的直属领导,带层级
            case RuleConstants.APPROVAL_TYPE_APPLICANT_DIRECT_MANAGER:
                UUID directManagerOid = baseClient.getDirectManager(applicantOid, ruleApproverDTO.getLevelNumber());
                if (directManagerOid != null) {
                    approverUserOids.add(directManagerOid);
                }
                break;
            default:
                //提交人所在的组织架构审批
                if (departmentType != null && departmentType.equals(RuleConstants.DEPARTMENT_TYPE_BY_APPLICANT)) {
                    //通过提交人所在的部门、审批规则中的部门层级，来获取对应的部门及其角色的用户的Oid
                    department = baseClient.getDepartmentByDepartmentOidAndLevel(userDepartmentOid, ruleApproverDTO.getLevelNumber());
                    if (department != null) {
                        UserApprovalDTO user = baseClient.getUserByDeparmentOidAndPosition(department.getDepartmentOid(), approverType.toString());
                        if (user != null) {
                            approverUserOids.add(user.getUserOid());
                        }
                    }
                } else if (departmentType != null && departmentType.equals(RuleConstants.DEPARTMENT_TYPE_BY_BILLS)) {
                    //通过单据所选分摊的部门、审批规则中的部门层级，来获取对应的部门及其角色的用户的Oid
                    if (withApportionment) {
                        if (CollectionUtils.isNotEmpty(apportionmentDepartmentOids) && ruleApproverDTO.getContainsApportionmentDepartmentManager() != null
                                && ruleApproverDTO.getContainsApportionmentDepartmentManager()) {
                            List<DepartmentCO> departmentList = baseClient.listDepartmentByDepartmentOidsAndLevel(apportionmentDepartmentOids, ruleApproverDTO.getLevelNumber());
                            if (CollectionUtils.isNotEmpty(departmentList)) {
                                for (DepartmentCO existDepartment : departmentList) {
                                    UserApprovalDTO user = baseClient.getUserByDeparmentOidAndPosition(existDepartment.getDepartmentOid(), approverType.toString());
                                    if (user != null) {
                                        apportionApprovalUserOids.add(user.getUserOid());
                                    }
                                }
                            }
                        }
                    }
                    //通过单据所选的部门、审批规则中的部门层级，来获取对应的部门及其角色的用户的Oid
                    department = baseClient.getDepartmentByDepartmentOidAndLevel(departmentOid, ruleApproverDTO.getLevelNumber());
                    if (department != null) {
                        UserApprovalDTO user = baseClient.getUserByDeparmentOidAndPosition(department.getDepartmentOid(), approverType.toString());
                        if (user != null) {
                            approverUserOids.add(user.getUserOid());
                        }
                    }
                }
        }
        if (CollectionUtils.isNotEmpty(approverUserOids)) {
            approverUserOids.remove(null);
        }
        if (CollectionUtils.isNotEmpty(apportionApprovalUserOids)) {
            apportionApprovalUserOids.remove(null);
        }
        map.get(ValueConstants.COUNTERSIGN_APPROVER).addAll(approverUserOids);  //会签审批人
        map.get(ValueConstants.APPROVER).addAll(apportionApprovalUserOids); //不受会签规则影响
    }




    //根据fieldCode ，获取自定义表单中对应的字段(先用messageKey做匹配)
    public List<FormValueDTO> getCustomFormValueByCode(List<FormValueDTO> customFormValueDTOs, String code) {
       return customFormValueDTOs.stream()
                .filter(customFormValue ->
                        code.equals(customFormValue.getMessageKey()) && !StringUtils.isEmpty(customFormValue.getValue()) && customFormValue.getValue() != "null")
                .collect(Collectors.toList());
    }

    /**
     * 修改为标准工作流的方式
     *
     * @return
     */
    public AssembleBrmsParamsRespDTO assembleNewBrmsParams(WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        AssembleBrmsParamsRespDTO assembleBrmsParamsRespDTO = new AssembleBrmsParamsRespDTO();
        List<FormValueDTO> customFormValueDTOs = null;
        Map<String, Object> entityData = new HashMap<>();
        UUID formOid = sysWorkFlowDocumentRef.getFormOid();
        UUID applicantOid = sysWorkFlowDocumentRef.getApplicantOid();
        customFormValueDTOs = new ArrayList<>();
        //组装单据部门数据
        FormValueDTO customFormValueDTO = RuleConstants.DEFAULT_DOCUMENT_DEPARTMENT_VALUE;
        customFormValueDTO.setValue(String.valueOf(sysWorkFlowDocumentRef.getUnitOid()));
        customFormValueDTO.setFormOid(formOid);
        customFormValueDTO.setFieldOid(RuleConstants.DEFAULT_DOCUMENT_DEPARTMENT_FIELD_OID);
        customFormValueDTOs.add(customFormValueDTO);

        // 申请人部门
        DepartmentCO department = baseClient.getDepartmentByUserOid(sysWorkFlowDocumentRef.getApplicantOid());
        FormValueDTO customFormValueUnitDTO = RuleConstants.DEFAULT_DEPARTMENT_VALUE;
        customFormValueUnitDTO.setValue(String.valueOf(department.getDepartmentOid()));
        customFormValueUnitDTO.setFormOid(formOid);
        customFormValueUnitDTO.setFieldOid(RuleConstants.DEFAULT_DEPARTMENT_OID);
        customFormValueDTOs.add(customFormValueUnitDTO);

        //单据公司数据
        FormValueDTO customFormValueCompanyDTO = RuleConstants.DEFAULT_DOCUMENT_COMPANY_VALUE;
        customFormValueCompanyDTO.setValue(String.valueOf(sysWorkFlowDocumentRef.getCompanyOid()));
        customFormValueCompanyDTO.setFormOid(formOid);
        customFormValueCompanyDTO.setFieldOid(RuleConstants.DEFAULT_DOCUMENT_COMPANY_FIELD_OID);
        customFormValueDTOs.add(customFormValueCompanyDTO);

        // 申请人公司
        CompanyCO company = baseClient.getCompanyByUserOid(sysWorkFlowDocumentRef.getApplicantOid());
        FormValueDTO customFormValueUseCompanyDTO = RuleConstants.DEFAULT_APPLICANT_COMPANY_VALUE;
        customFormValueUseCompanyDTO.setValue(String.valueOf(company.getCompanyOid()));
        customFormValueUseCompanyDTO.setFormOid(formOid);
        customFormValueUseCompanyDTO.setFieldOid(RuleConstants.DEFAULT_APPLICANT_COMPANY_OID);
        customFormValueDTOs.add(customFormValueUseCompanyDTO);

        //组装单据备注数据
        FormValueDTO customFormValueRemarkDTO = RuleConstants.DEFAULT_REMARK_VALUE;
        customFormValueRemarkDTO.setValue(sysWorkFlowDocumentRef.getRemark());
        customFormValueRemarkDTO.setFormOid(formOid);
        customFormValueDTOs.add(customFormValueRemarkDTO);

        //组装单据币种数据
        FormValueDTO customFormValueCurrencyDTO = RuleConstants.DEFAULT_CURRENCY_VALUE;
        customFormValueCurrencyDTO.setValue(sysWorkFlowDocumentRef.getCurrencyCode());
        customFormValueCurrencyDTO.setFormOid(formOid);
        customFormValueCurrencyDTO.setFieldOid(RuleConstants.DEFAULT_CURRENCY_FIELD_OID);
        customFormValueDTOs.add(customFormValueCurrencyDTO);

        //设置默认金额
        FormValueDTO budgetValueDTO = RuleConstants.DEFAULT_AMOUNT_VALUE;
        BigDecimal amount = sysWorkFlowDocumentRef.getAmount();
        String amountStr = amount != null ? amount.toString() : "0";
        budgetValueDTO.setValue(amountStr);
        budgetValueDTO.setFormOid(formOid);
        customFormValueDTOs.add(budgetValueDTO);

        //设置默认本币金额
        FormValueDTO customFormValueFunctionAmountDTO = RuleConstants.DEFAULT_FUNCTION_AMOUNT_VALUE;
        BigDecimal functionAmount = sysWorkFlowDocumentRef.getFunctionAmount();
        String functionAmountStr = functionAmount != null ? functionAmount.toString() : "0";
        customFormValueFunctionAmountDTO.setValue(functionAmountStr);
        customFormValueFunctionAmountDTO.setFormOid(formOid);
        customFormValueDTOs.add(customFormValueFunctionAmountDTO);

        int level = 1;
        if (StringUtils.isNotEmpty(department.getPath()) && department.getPath().contains(Constants.DEPARTMENT_SPLIT)) {
            level = department.getPath().split("\\|").length;
        }

        FormValueDTO departmentLevelValueDTO = RuleConstants.DEFAULT_DEPARTMENT_LEVEL_VALUE;
        departmentLevelValueDTO.setValue(level + "");
        departmentLevelValueDTO.setFormOid(formOid);
        customFormValueDTOs.add(departmentLevelValueDTO);

        FormValueDTO departmentPathValueDTO = RuleConstants.DEFAULT_DEPARTMENT_PATH_VALUE;
        // modify by mh.z 20190115 drool规则里写的是根据部门OID判断
        departmentPathValueDTO.setValue(department.getDepartmentOid() + "|");
        departmentPathValueDTO.setFormOid(formOid);
        customFormValueDTOs.add(departmentPathValueDTO);

        FormValueDTO departmentRoleValueDTO = RuleConstants.DEFAULT_DEPARTMENT_ROLE_VALUE;
        departmentRoleValueDTO.setValue(getDepartmentRoleStringByDepartmentPosition(department.getId(), applicantOid));
        departmentRoleValueDTO.setFormOid(formOid);
        customFormValueDTOs.add(departmentRoleValueDTO);

        assembleBrmsParamsRespDTO.setFormValueDTOS(customFormValueDTOs);
        assembleBrmsParamsRespDTO.setEntityData(entityData);
        assembleBrmsParamsRespDTO.setFormOid(formOid);
        return assembleBrmsParamsRespDTO;
    }




    //调用brms获取下一个
    public RuleNextApproverResult getRuleNextApproverResult(List<FormValueDTO> customFormValueDTOs, UUID formOid, UUID lastApprovalNodeOid, UUID applicantOid, Integer entityType, UUID entityOid, Map<String, Object> entityData) {
        RuleNextApproverResult ruleNextApproverResult = brmsService.getNextApprovalNode(customFormValueDTOs, formOid, lastApprovalNodeOid, applicantOid, entityType, entityOid, entityData);
        customFormValueDTOs = customFormValueDTOs.stream().filter(x -> x.getFieldOid() != null).collect(Collectors.toList());
        DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO = DroolsRuleApprovalNodeDTO.builder()
                .ruleApprovalNodeOid(lastApprovalNodeOid)
                .formValues(customFormValueDTOs)
                .applicantOid(applicantOid)
                .formOid(formOid)
                .entityType(entityType)
                .entityOid(entityOid)
                .entityData(entityData)
                .build();

        if (ruleNextApproverResult != null && ruleNextApproverResult.getDroolsApprovalNode() != null && RuleConstants.RULE_NULLABLE_THROW.equals(ruleNextApproverResult.getDroolsApprovalNode().getNullableRule()) && CollectionUtils.isEmpty(ruleNextApproverResult.getDroolsApprovalNode().getRuleApprovers())) {
            throw new BizException(LocaleMessageConstants.SYS_APPROVAL_NO_APPROVER);
        }
        if (ruleNextApproverResult != null && ruleNextApproverResult.getDroolsApprovalNode() != null && CollectionUtils.isNotEmpty(ruleNextApproverResult.getDroolsApprovalNode().getRuleApprovers())) {
            getRuleApproverUserOids(ruleNextApproverResult.getDroolsApprovalNode(), droolsRuleApprovalNodeDTO);
        }
        return ruleNextApproverResult;
    }
}
