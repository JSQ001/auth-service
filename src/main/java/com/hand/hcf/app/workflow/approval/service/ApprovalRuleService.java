package com.hand.hcf.app.workflow.approval.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.NotifyInfo;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleNextApproverResult;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.ApprovalFormPropertyConstants;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.constant.SyncLockPrefix;
import com.hand.hcf.app.workflow.constant.WorkflowConstants;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.ApprovalNodeEnum;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalFormPropertyRuleDTO;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.AssembleBrmsParamsRespDTO;
import com.hand.hcf.app.workflow.dto.BuildApprovalChainResult;
import com.hand.hcf.app.workflow.dto.UserApprovalDTO;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.enums.ApprovalPathModeEnum;
import com.hand.hcf.app.workflow.enums.CounterSignOperationTypeEnum;
import com.hand.hcf.app.workflow.enums.IsAddSignEnum;
import com.hand.hcf.app.workflow.enums.RejectTypeEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalFormPropertyService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.CountersignDetailService;
import com.hand.hcf.app.workflow.service.DefaultWorkflowIntegrationServiceImpl;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.service.WorkFlowEventPublishService;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.service.BaseI18nService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hand.hcf.app.workflow.constant.RuleConstants.ACTION_APPROVAL_PASS;
import static com.hand.hcf.app.workflow.constant.RuleConstants.ADD_SIGN_APPROVER_ADD_SIGN;
import static com.hand.hcf.app.workflow.constant.RuleConstants.ALL_REPEATED_FILTER;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVAL_CHAIN_FILTER;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_ADD_SIGN;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_FILTER;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_ROBOT_PASS_Detail;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_ROBOT_PASS_Detail_ENGLISH;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_ROBOT_REJECT_Detail;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_ROBOT_REJECT_Detail_ENGLISH;
import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_TYPE_ROBOT_OID;
import static com.hand.hcf.app.workflow.constant.RuleConstants.COUNTERSIGN_APPROVER_FILTER;
import static com.hand.hcf.app.workflow.constant.RuleConstants.ROBOT_NODE_AND_APPROVAL_PASS_RESULT;
import static com.hand.hcf.app.workflow.constant.RuleConstants.ROBOT_NODE_AND_APPROVAL_REJECT_RESULT;
import static com.hand.hcf.app.workflow.constant.RuleConstants.RULE_CONUTERSIGN_ANY;
import static com.hand.hcf.app.workflow.constant.RuleConstants.RULE_SEQUENCE;


/**
 * @author houyin.zhang
 * @since 2018/12/12
 */
@Service
public class ApprovalRuleService {
    public static Logger logger = LoggerFactory.getLogger(ApprovalRuleService.class);

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalFormPropertyService approvalFormPropertyService;

    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private BrmsService brmsService;

    @Autowired
    private CountersignDetailService countersignDetailService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    BaseI18nService baseI18nService;

    @Autowired
    private WorkFlowEventPublishService workflowEventPublishService;

    @Autowired
    private ApprovalPassService approvalPassService;

    @Autowired
    private ApprovalRejectService approvalRejectService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 新工作流所有单据提交入口 20181214
     * @param approvalPathType
     * @param ruleApprovalNodeOid
     * @param sysWorkFlowDocumentRef
     * @return
     */
    public BuildApprovalChainResult buildNewApprovalChainResultByRuleOrApproverOids(Integer approvalPathType, UUID ruleApprovalNodeOid, String ruleApprovalNodeName, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        BuildApprovalChainResult buildApprovalChainResult = null;
        if (CollectionUtils.isNotEmpty(sysWorkFlowDocumentRef.getCountersignApproverOids())) {
            //构建加签审批链
            List<ApprovalChain> approvalChainList = buildApprovalChainByCountersignApproverOids(sysWorkFlowDocumentRef.getDocumentOid(), sysWorkFlowDocumentRef.getDocumentCategory(), sysWorkFlowDocumentRef.getUserOid(), sysWorkFlowDocumentRef.getCountersignApproverOids(), null, APPROVER_ADD_SIGN, null, ruleApprovalNodeName, false, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER, null, sysWorkFlowDocumentRef.getFormOid());
            //创建或更新加签详情表
            countersignDetailService.saveCountersignDetailByEntityTypeAndEntityOidAndApplicantOidAndCountersignApproverOids(sysWorkFlowDocumentRef.getDocumentCategory(), sysWorkFlowDocumentRef.getDocumentOid(), baseClient.getUserByUserOid(sysWorkFlowDocumentRef.getUserOid()).getId(), sysWorkFlowDocumentRef.getCountersignApproverOids(), null, sysWorkFlowDocumentRef.getFormOid());
            //激活审批链
            activeApprovalChain(sysWorkFlowDocumentRef.getDocumentOid(), sysWorkFlowDocumentRef.getDocumentCategory(), approvalChainList, sysWorkFlowDocumentRef.getFormOid());
        } else {
            //根据规则构建审批节点
            buildApprovalChainResult = buildNextApprovalChainByRule(approvalPathType, ruleApprovalNodeOid, true, sysWorkFlowDocumentRef);
        }
        return buildApprovalChainResult;
    }

    /*
        激活审批链 返回需要知会的审批人
     */
    private List<UUID> activeApprovalChain(UUID entityOid, Integer entityType, List<ApprovalChain> approvalChainList, UUID formOid) {
        //查询实体对应的表单
        String countersignType = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER);
        if (StringUtils.isBlank(countersignType)) {
            countersignType = RULE_SEQUENCE.toString();
        }
        List<UUID> approverOids = new ArrayList<>();
        //如果是顺序审批 只激活第一个sequence最小的加签审批链
        if (RULE_SEQUENCE.equals(Integer.valueOf(countersignType))) {
            ApprovalChain approvalChain = approvalChainList.get(0);
            approvalChain.setCurrentFlag(true);
            approverOids.add(approvalChain.getApproverOid());
        } else {
            //是非顺序审批
            approvalChainList.forEach(approvalChain -> {
                approverOids.add(approvalChain.getApproverOid());
                approvalChain.setCurrentFlag(true);
            });
        }
        approvalChainService.saveAll(approvalChainList);
        return approverOids;
    }

    /**
     * 根据加签的Oid 创建审批链
     * @param entityOid
     * @param entityType
     * @param applicantOid
     * @param countersignApproverOids
     * @param proxyStrategy
     * @param counterSignTypeTag      区分提交加签和审批加签
     * @return
     */
    protected List<ApprovalChain> buildApprovalChainByCountersignApproverOids(UUID entityOid, Integer entityType, UUID applicantOid, List<UUID> countersignApproverOids, Integer sequence, Integer addSignType, UUID ruleApprovalNodeOid, String ruleApprovalNodeName, boolean proxyStrategy, String counterSignTypeTag, Long chainId, UUID formOid) {
        ZonedDateTime now = ZonedDateTime.now();
        //查询实体对应的表单
        String countersignType = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, counterSignTypeTag);
        if (StringUtils.isBlank(countersignType)) {
            countersignType = RULE_SEQUENCE.toString();
        }
        //添加会签审批链
        List<ApprovalChain> countersignApprovelList = new ArrayList<>();
        Integer countersignFlag = Integer.valueOf(countersignType);
        //查询上次加签的sequence  invoiceAllowUpdateType
        ApprovalChain previousApprovalChain = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusOrderBySequenceDesc(
                entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
        //如果是顺序审批
        if (countersignFlag.equals(RULE_SEQUENCE)) {
            //审批人加签 sequence按顺序累加
            if (APPROVER_ADD_SIGN.equals(addSignType)) {
                int i = 1;
                for (UUID approverOid : countersignApproverOids) {
                    //创建加签审批链
                    ApprovalChain countersignApprovalChain = new ApprovalChain();
                    countersignApprovalChain.setApproverOid(approverOid);
                    countersignApprovalChain.setSequence(previousApprovalChain == null ? i : (previousApprovalChain.getSequence() + i));
                    countersignApprovalChain.setInvoiceAllowUpdateType(previousApprovalChain == null ? null : previousApprovalChain.getInvoiceAllowUpdateType());
                    countersignApprovelList.add(countersignApprovalChain);
                    i++;
                }
                //被加签人再去加签 在当前sequence累加,  另外的加签审批链依次顺延
            } else if (ADD_SIGN_APPROVER_ADD_SIGN.equals(addSignType)) {
                int i = 0;
                for (UUID approverOid : countersignApproverOids) {
                    //创建加签审批链
                    ApprovalChain countersignApprovalChain = new ApprovalChain();
                    countersignApprovalChain.setApproverOid(approverOid);
                    countersignApprovalChain.setSequence(sequence + ++i);
                    countersignApprovalChain.setInvoiceAllowUpdateType(previousApprovalChain == null ? null : previousApprovalChain.getInvoiceAllowUpdateType());
                    countersignApprovelList.add(countersignApprovalChain);
                }
                //查询在当前加签审批链sequence之后的加签审批链,更新sequence
                List<ApprovalChain> approvalChainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndCountersignTypeNotNullAndSequenceGreaterThan(
                        entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), sequence);
                if (CollectionUtils.isNotEmpty(approvalChainList)) {
                    ArrayList<ApprovalChain> newApprovalChainList = new ArrayList<>();
                    for (ApprovalChain approvalChain : approvalChainList) {
                        approvalChain.setSequence(approvalChain.getSequence() + i);
                        newApprovalChainList.add(approvalChain);
                    }
                    approvalChainService.saveAll(newApprovalChainList);
                }
            }
        } else {
            //不用顺序审批　加签审批链是同一组sequence
            countersignApproverOids.stream().distinct().forEach(approverOid -> {
                //创建加签审批链
                ApprovalChain countersignApprovalChain = new ApprovalChain();
                countersignApprovalChain.setApproverOid(approverOid);
                countersignApprovalChain.setSequence(previousApprovalChain == null ? 1 : previousApprovalChain.getSequence() + 1);
                countersignApprovalChain.setInvoiceAllowUpdateType(previousApprovalChain == null ? null : previousApprovalChain.getInvoiceAllowUpdateType());
                countersignApprovelList.add(countersignApprovalChain);
            });
        }
        countersignApprovelList.forEach(approvalChain -> {
            approvalChain.setAddSign(IsAddSignEnum.SIGN_YES.getId());
            approvalChain.setSourceApprovalChainId(chainId);
            approvalChain.setAllFinished(false);
            approvalChain.setEntityType(entityType);
            approvalChain.setEntityOid(entityOid);
            approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());
            approvalChain.setCreatedDate(ZonedDateTime.now());
            approvalChain.setLastUpdatedDate(ZonedDateTime.now());
            approvalChain.setNoticed(false);
            approvalChain.setCurrentFlag(false);
            approvalChain.setFinishFlag(false);
            approvalChain.setCountersignType(countersignFlag);
            approvalChain.setRuleApprovalNodeOid(ruleApprovalNodeOid);
            if (proxyStrategy) {
                approvalChain.setProxyFlag(proxyStrategy);
            }
        });
        //保存加签审批链
        approvalChainService.saveAll(countersignApprovelList);
        //保存加签历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);
        approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
        approvalHistory.setOperation(ApprovalOperationEnum.ADD_COUNTERSIGN.getId());
        approvalHistory.setOperatorOid(applicantOid);
        approvalHistory.setCountersignType(countersignFlag);
        approvalHistory.setApprovalNodeName(ruleApprovalNodeName);
        approvalHistory.setApprovalNodeOid(ruleApprovalNodeOid);
        StringBuffer countersignApproverFullName = new StringBuffer();
        countersignApproverOids.stream().distinct().forEach(countersignApproverOid -> {
            UserApprovalDTO countersignApprover = baseClient.getUserByUserOid(countersignApproverOid);
            if (StringUtils.isEmpty(countersignApproverFullName.toString())) {
                countersignApproverFullName.append(countersignApprover.getFullName() + " " + countersignApprover.getEmployeeCode());
            } else {
                countersignApproverFullName.append(", " + countersignApprover.getFullName() + " " + countersignApprover.getEmployeeCode());
            }
        });
        approvalHistory.setOperationDetail(countersignApproverFullName.toString());
        approvalHistory.setCreatedDate(now);
        approvalHistory.setLastUpdatedDate(now);
        approvalHistoryService.save(approvalHistory);
        return countersignApprovelList;
    }

    private void preApprovalChain(ApprovalChain approvalChain, int entityType, UUID entityOid, int start, ZonedDateTime now, UUID ruleApprovalNodeOid, Integer invoiceAllowUpdateType) {
        approvalChain.setEntityType(entityType);
        approvalChain.setEntityOid(entityOid);
        approvalChain.setSequence(1 + start);
        approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());
        approvalChain.setCreatedDate(now);
        approvalChain.setLastUpdatedDate(now);
        approvalChain.setRuleApprovalNodeOid(ruleApprovalNodeOid);
        approvalChain.setInvoiceAllowUpdateType(invoiceAllowUpdateType);

    }

    private void initApprovalChain(boolean isNotice, boolean isCurrentFlag, boolean isFinishFlag, UUID approverOid, ApprovalChain approvalChain, boolean apportionmentFlag, Integer countersignRule) {
        approvalChain.setNoticed(isNotice);
        approvalChain.setCurrentFlag(isCurrentFlag);
        approvalChain.setFinishFlag(isFinishFlag);
        approvalChain.setApproverOid(approverOid);
        approvalChain.setApportionmentFlag(apportionmentFlag);
        approvalChain.setCountersignRule(countersignRule);
    }

    private List<UUID> getNextApproversByCompute(UUID ruleApprovalNodeOid, Integer approvalPathType, WorkFlowDocumentRef workFlowDocumentRef) {
        ZonedDateTime now = ZonedDateTime.now();
        UUID formOid = workFlowDocumentRef.getFormOid();
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID companyOid = workFlowDocumentRef.getCompanyOid();
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        //启用规则计算
        RuleNextApproverResult ruleNextApproverResult = null;
        //所有待审批的用户
        List<UUID> approvalUserOids = new ArrayList<>();
        //所有机器人
        List<UUID> robotUserOids = new ArrayList<>();
        //审批链结果
        List<ApprovalChain> approvelList = new ArrayList<ApprovalChain>();
        //机器人
        List<ApprovalChain> robotList = new ArrayList<ApprovalChain>();
        RuleApprovalNodeDTO ruleApprovalNodeDTO = null;
        //是否执行了过滤方法
        boolean filterReleInvoked = false;
        //查询实体对应的表单过滤规则
        String filterRule = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.FILTER_RULE);
        String filterTypeRule = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.FILTER_TYPE_RULE);
        //封装调用brms的数据
        AssembleBrmsParamsRespDTO assembleBrmsParamsRespDTO = defaultWorkflowIntegrationService.assembleBrmsParams(entityOid, entityType, applicantOid);
        logger.info("ApprovalItemService : getNextApproversByCompute:assembleBrmsParamsRespDTO:" + com.alibaba.fastjson.JSONObject.toJSONString(assembleBrmsParamsRespDTO));
        do {
            //未找到审批者 继续执行
            ruleNextApproverResult = defaultWorkflowIntegrationService.getRuleNextApproverResult(assembleBrmsParamsRespDTO.getFormValueDTOS(), assembleBrmsParamsRespDTO.getFormOid(), ruleApprovalNodeOid, applicantOid, entityType, entityOid, assembleBrmsParamsRespDTO.getEntityData());
            logger.info("ApprovalItemService : getNextApproversByCompute:ruleNextApproverResult:" + com.alibaba.fastjson.JSONObject.toJSONString(ruleNextApproverResult));
            if (ruleNextApproverResult == null) {
                break;
            }
            if (ruleNextApproverResult.getApprovalMode() == null || ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.CUSTOM) {
                //自定义审批 根据节点
                ruleApprovalNodeDTO = ruleNextApproverResult.getDroolsApprovalNode();

                if (ruleApprovalNodeDTO == null || ruleApprovalNodeDTO.getRuleApprovalNodeOid() == null) {
                    break;
                }
                ruleApprovalNodeOid = ruleApprovalNodeDTO.getRuleApprovalNodeOid();

                List<UUID> countersignApproverOids = null;
                List<UUID> approverOids = null;
                if (ruleApprovalNodeDTO.getRuleApproverMap() != null) {
                    countersignApproverOids = new ArrayList<>(ruleApprovalNodeDTO.getRuleApproverMap().get(WorkflowConstants.COUNTERSIGN_APPROVER));
                    approverOids = new ArrayList<>(ruleApprovalNodeDTO.getRuleApproverMap().get(WorkflowConstants.APPROVER));
                    countersignApproverOids.removeAll(approverOids);
                }
                //过滤重复审批人 优先保留分摊的成本中心和部门对应的审批人   分摊审批人approverOids不受会签规则影响
                Set<UUID> allApproaverOids = new HashSet<>();
                if (countersignApproverOids != null) {
                    allApproaverOids.addAll(countersignApproverOids);
                }
                if (approverOids != null) {
                    allApproaverOids.addAll(approverOids);
                }
                logger.info("ruleApprovalNodeDTO.getRuleApproverMap() : {}", StringUtils.join(approvalUserOids.toArray()));
                // 承接已有的审批步骤
                ApprovalChain existApprovalChain = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusOrderBySequenceDesc(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
                int start = existApprovalChain == null ? 0 : existApprovalChain.getSequence();
                ApprovalChain approvalChain = null;
                if (CollectionUtils.isNotEmpty(countersignApproverOids)) {
                    countersignApproverOids.remove(null);
                }
                if (CollectionUtils.isNotEmpty(approverOids)) {
                    approverOids.remove(null);
                }
                switch (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber())) {
                    case NODE_TYPE_PRINT:
                        break;
                    case NODE_TYPE_EED:
                        break;
                    case NODE_TYPE_NOTICE:
                        break;
                    case NODE_TYPE_APPROVAL: {
                        if (CollectionUtils.isNotEmpty(countersignApproverOids)) {
                            //根据过滤规则过滤审批人
                            filterReleInvoked = filterApproverByFilterRule(countersignApproverOids, filterRule, filterReleInvoked, filterTypeRule, workFlowDocumentRef);
                            for (UUID approverOid : countersignApproverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, false, ruleApprovalNodeDTO.getCountersignRule());
                                approvelList.add(approvalChain);
                            }
                            approvalUserOids.addAll(countersignApproverOids);
                        }
                        //分摊的成本中心对应负责人不受会签规则影响
                        if (CollectionUtils.isNotEmpty(approverOids)) {
                            //根据过滤规则过滤审批人
                            filterReleInvoked = filterApproverByFilterRule(approverOids, filterRule, filterReleInvoked, filterTypeRule, workFlowDocumentRef);
                            for (UUID approverOid : approverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, true, ruleApprovalNodeDTO.getCountersignRule());
                                approvelList.add(approvalChain);
                            }
                            approvalUserOids.addAll(approverOids);
                        }
                    }
                    break;
                    case NODE_TYPE_ROBOT: {
                        if (CollectionUtils.isNotEmpty(allApproaverOids)) {
                            //保存关联单据信息
                            workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
                            for (UUID approverOid : allApproaverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, false, ruleApprovalNodeDTO.getCountersignRule());
                                robotList.add(approvalChain);
                            }
                            robotUserOids.addAll(allApproaverOids);
                        }
                    }
                    break;
                }
            } else if (ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.DEPARTMENT) {
                break;
            } else if (ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.USER_PICK || ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.YING_FU_USER_PICK) {
                break;
            }
        }
        while (CollectionUtils.isEmpty(approvalUserOids) && CollectionUtils.isEmpty(robotUserOids));

        if (CollectionUtils.isNotEmpty(approvalUserOids)) {
            return approvalUserOids;
        }
        if (CollectionUtils.isNotEmpty(robotUserOids)) {
            return robotUserOids;
        }
        return new ArrayList<UUID>();
    }

    protected BuildApprovalChainResult buildNextApprovalChainByRule(Integer approvalPathType, UUID ruleApprovalNodeOid, boolean isFirstNode, WorkFlowDocumentRef workFlowDocumentRef) {
        ZonedDateTime now = ZonedDateTime.now();
        //启用规则计算
        RuleNextApproverResult ruleNextApproverResult = null;
        //所有待通知的用户
        List<UUID> noticeUserOids = new ArrayList<>();
        //所有待审批的用户
        List<UUID> approvalUserOids = new ArrayList<>();
        //所有机器人
        List<UUID> robotUserOids = new ArrayList<>();
        //审批链结果
        List<ApprovalChain> approvelList = new ArrayList<ApprovalChain>();
        //知会人
        List<ApprovalChain> noticeList = new ArrayList<ApprovalChain>();
        //知会配置信息
        NotifyInfo notifyInfo = null;
        //机器人
        List<ApprovalChain> robotList = new ArrayList<ApprovalChain>();
        //打印节点开启打印
        boolean isEndNode = false;
        //结束节点控制 审批完后是否推送PDF邮件
        Boolean endNodePrintEnable = null;
        /**
         * 查询审批者,通知者
         */
        //是否包含自审批跳过
        boolean containsSelfApprovalSkip = false;
        //自审批
        boolean autoSelfApproval = false;
        Integer nodeTypeAndApprovalResult = 0;
        RuleApprovalNodeDTO ruleApprovalNodeDTO = null;
        //是否执行了过滤方法
        boolean filterReleInvoked = false;
        //查询实体对应的表单过滤规则
        String filterRule = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(workFlowDocumentRef.getFormOid(), ApprovalFormPropertyConstants.FILTER_RULE);
        String filterTypeRule = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(workFlowDocumentRef.getFormOid(), ApprovalFormPropertyConstants.FILTER_TYPE_RULE);
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        UUID formOid = workFlowDocumentRef.getFormOid();

        //封装调用brms的数据
        AssembleBrmsParamsRespDTO assembleBrmsParamsRespDTO = defaultWorkflowIntegrationService.assembleNewBrmsParams(workFlowDocumentRef);
        logger.info("ApprovalItemService : buildNextApprovalChainByRule:assembleBrmsParamsRespDTO:" + com.alibaba.fastjson.JSONObject.toJSONString(assembleBrmsParamsRespDTO));
        do {
            //未找到审批者 继续执行
            ruleNextApproverResult = defaultWorkflowIntegrationService.getRuleNextApproverResult(assembleBrmsParamsRespDTO.getFormValueDTOS(), assembleBrmsParamsRespDTO.getFormOid(), ruleApprovalNodeOid, applicantOid, entityType, entityOid, assembleBrmsParamsRespDTO.getEntityData());
            logger.info("ApprovalItemService : buildNextApprovalChainByRule:ruleNextApproverResult:" + com.alibaba.fastjson.JSONObject.toJSONString(ruleNextApproverResult));
            if (ruleNextApproverResult == null) {
                break;
            }
            if (ruleNextApproverResult.getApprovalMode() == null || ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.CUSTOM) {
                //自定义审批 根据节点
                ruleApprovalNodeDTO = ruleNextApproverResult.getDroolsApprovalNode();

                if (ruleApprovalNodeDTO == null || ruleApprovalNodeDTO.getRuleApprovalNodeOid() == null) {
                    break;
                }

                ruleApprovalNodeOid = ruleApprovalNodeDTO.getRuleApprovalNodeOid();
                workFlowDocumentRef.setApprovalNodeName(ruleApprovalNodeDTO.getRemark());
                workFlowDocumentRef.setApprovalNodeOid(ruleApprovalNodeDTO.getRuleApprovalNodeOid().toString());

                if (ruleApprovalNodeDTO.getContainsSelfApprovalSkip() != null
                        && ruleApprovalNodeDTO.getContainsSelfApprovalSkip()) {
                    containsSelfApprovalSkip = true;
                }
                List<UUID> countersignApproverOids = null;
                List<UUID> approverOids = null;
                if (ruleApprovalNodeDTO.getRuleApproverMap() != null) {
                    countersignApproverOids = new ArrayList<>(ruleApprovalNodeDTO.getRuleApproverMap().get(WorkflowConstants.COUNTERSIGN_APPROVER));
                    approverOids = new ArrayList<>(ruleApprovalNodeDTO.getRuleApproverMap().get(WorkflowConstants.APPROVER));
                    countersignApproverOids.removeAll(approverOids);
                }
                //过滤重复审批人 优先保留分摊的成本中心和部门对应的审批人   分摊审批人approverOids不受会签规则影响
                Set<UUID> allApproaverOids = new HashSet<>();
                if (countersignApproverOids != null) {
                    allApproaverOids.addAll(countersignApproverOids);
                }
                if (approverOids != null) {
                    allApproaverOids.addAll(approverOids);
                }
                logger.info("ruleApprovalNodeDTO.getRuleApproverMap() : {}", StringUtils.join(approvalUserOids.toArray()));
                // 承接已有的审批步骤
                ApprovalChain existApprovalChain = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusOrderBySequenceDesc(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
                int start = existApprovalChain == null ? 0 : existApprovalChain.getSequence();
                ApprovalChain approvalChain = null;
                if (CollectionUtils.isNotEmpty(countersignApproverOids)) {
                    countersignApproverOids.remove(null);
                }
                if (CollectionUtils.isNotEmpty(approverOids)) {
                    approverOids.remove(null);
                }
                switch (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber())) {
                    case NODE_TYPE_EED: {
                        isEndNode = true;
                        endNodePrintEnable = ruleApprovalNodeDTO.getPrintFlag() == null ? true : ruleApprovalNodeDTO.getPrintFlag();
                    }
                    break;
                    case NODE_TYPE_NOTICE: {
                        if (CollectionUtils.isNotEmpty(allApproaverOids)) {
                            for (UUID approverOid : allApproaverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(true, false, true, approverOid, approvalChain, false, ruleApprovalNodeDTO.getCountersignRule());
                                noticeList.add(approvalChain);
                            }
                            notifyInfo = ruleApprovalNodeDTO.getNotifyInfo();
                            noticeUserOids.addAll(allApproaverOids);
                        }
                    }
                    break;
                    case NODE_TYPE_APPROVAL: {
                        if (CollectionUtils.isNotEmpty(countersignApproverOids)) {
                            //根据过滤规则过滤审批人
                            filterReleInvoked = filterApproverByFilterRule(countersignApproverOids, filterRule, filterReleInvoked, filterTypeRule, workFlowDocumentRef);
                            for (UUID approverOid : countersignApproverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, false, ruleApprovalNodeDTO.getCountersignRule());
                                approvelList.add(approvalChain);
                            }
                            approvalUserOids.addAll(countersignApproverOids);
                        }
                        //分摊的成本中心对应负责人设置apportionmentFlag 为true
                        if (CollectionUtils.isNotEmpty(approverOids)) {
                            //根据过滤规则过滤审批人
                            filterReleInvoked = filterApproverByFilterRule(approverOids, filterRule, filterReleInvoked, filterTypeRule, workFlowDocumentRef);
                            for (UUID approverOid : approverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, true, ruleApprovalNodeDTO.getCountersignRule());
                                approvelList.add(approvalChain);
                            }
                            approvalUserOids.addAll(approverOids);
                        }
                    }
                    break;
                    case NODE_TYPE_ROBOT: {
                        if (CollectionUtils.isNotEmpty(allApproaverOids)) {
                            //保存关联单据信息
                            workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
                            for (UUID approverOid : allApproaverOids) {
                                approvalChain = new ApprovalChain();
                                preApprovalChain(approvalChain, entityType, entityOid, start, now, ruleApprovalNodeOid, ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
                                initApprovalChain(false, true, false, approverOid, approvalChain, false, ruleApprovalNodeDTO.getCountersignRule());
                                robotList.add(approvalChain);
                            }
                            robotUserOids.addAll(allApproaverOids);
                        }
                    }
                    break;
                }
            } else if (ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.DEPARTMENT) {
                List<String> approverList = defaultWorkflowIntegrationService.getWorkflowApprovalPath(applicantOid, entityOid, approvalPathType, ApprovalMode.DEPARTMENT.getId(), 1);
                if (!CollectionUtils.isEmpty(approverList)) {
                    approvalUserOids.addAll(approverList.stream().map(UUID::fromString).collect(Collectors.toList()));
                    buildApprovalChain(entityOid, entityType, approverList, false);
                }
                break;
            } else if (ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.USER_PICK || ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.YING_FU_USER_PICK) {
                List<String> approverList = defaultWorkflowIntegrationService.getWorkflowApprovalPath(applicantOid, entityOid, approvalPathType, ApprovalMode.USER_PICK.getId(), 1);
                if (!CollectionUtils.isEmpty(approverList)) {
                    approvalUserOids.addAll(approverList.stream().map(UUID::fromString).collect(Collectors.toList()));
                    //英孚选人模式  所有人一起审批
                    buildApprovalChain(entityOid, entityType, approverList, ApprovalMode.parse(ruleNextApproverResult.getApprovalMode()) == ApprovalMode.YING_FU_USER_PICK);
                }
                break;
            }
        }
        while (CollectionUtils.isEmpty(approvalUserOids) && CollectionUtils.isEmpty(robotUserOids));
        logger.info("buildNextApprovalChainByRule param entityOid : {} , entityType : {} , get approvalUserOids result : {}", entityOid, entityType, approvalUserOids);

        /**
         *  获取首个审批者为空
         *  审批链为空时
         *      若发生过 跳过申请人 : 申请人自审批通过
         *      若无 则报错
         *      filterReleInvoked 过滤过审批人
         */
        if (!filterReleInvoked && isFirstNode && CollectionUtils.isEmpty(approvalUserOids) && CollectionUtils.isEmpty(robotUserOids)) {
            // modify by mh.z 20190117 去掉自审批通过的逻辑
            /*if (containsSelfApprovalSkip) {
                //自审批
                autoSelfApproval = true;
                ApprovalChain approvalChain = new ApprovalChain();
                approvalChain.setEntityType(entityType);
                approvalChain.setEntityOid(entityOid);
                approvalChain.setSequence(1);
                approvalChain.setApproverOid(applicantOid);
                approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());
                approvalChain.setCreatedDate(now);
                approvalChain.setLastUpdatedDate(now);
                approvalChain.setRuleApprovalNodeOid(ruleApprovalNodeOid);
                approvalChain.setNoticed(false);
                approvalChain.setCurrentFlag(true);
                approvalChain.setFinishFlag(false);
                approvelList.add(approvalChain);
            } else {
                logger.error("rule approval path is null，buildNextApprovalChainByRule, entityOid :{} , entityType :{} ", entityOid, entityType);
                throw new BizException(RespCode.SYS_APPROVAL_NO_APPROVER, RuleConstants.CANNOT_FIND_CURRENT_APPROVAL);
            }*/

            logger.error("rule approval path is null，buildNextApprovalChainByRule, entityOid :{} , entityType :{} ", entityOid, entityType);
            throw new BizException(RespCode.SYS_APPROVAL_NO_APPROVER, RuleConstants.CANNOT_FIND_CURRENT_APPROVAL);
        }
        if (CollectionUtils.isNotEmpty(robotList)) {
            logger.debug("buildNextApprovalChainByRule param entityOid : {} , entityType : {} , approvalChainService save robotList count : {}", entityOid, entityType, robotList.size());
            try {
                logger.debug("debug->buildNextApprovalChainByRule->robotList：{}", objectMapper.writeValueAsString(robotList));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            approvalChainService.saveAll(robotList);
            ApprovalReqDTO approvalReqDTO = new ApprovalReqDTO();
            List<ApprovalReqDTO.Entity> entityList = new ArrayList<>();
            ApprovalReqDTO.Entity entity = new ApprovalReqDTO.Entity();
            entity.setEntityType(entityType);
            entity.setEntityOid(entityOid.toString());
            entityList.add(entity);
            approvalReqDTO.setEntities(entityList);
            if (ruleApprovalNodeDTO.getApprovalActions().contains(String.valueOf(ACTION_APPROVAL_PASS))) {
                if (StringUtils.isEmpty(ruleApprovalNodeDTO.getComments())) {
                    if (!Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                        approvalReqDTO.setApprovalTxt(APPROVER_ROBOT_PASS_Detail_ENGLISH);
                    } else {
                        approvalReqDTO.setApprovalTxt(APPROVER_ROBOT_PASS_Detail);
                    }
                } else {
                    approvalReqDTO.setApprovalTxt(ruleApprovalNodeDTO.getComments());
                }
                approvalPassService.passWorkflow(UUID.fromString(APPROVER_TYPE_ROBOT_OID), approvalReqDTO, formOid);
                nodeTypeAndApprovalResult = RuleConstants.ROBOT_NODE_AND_APPROVAL_PASS_RESULT;

            } else {
                if (StringUtils.isEmpty(ruleApprovalNodeDTO.getComments())) {
                    if (!Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                        approvalReqDTO.setApprovalTxt(APPROVER_ROBOT_REJECT_Detail_ENGLISH);
                    } else {
                        approvalReqDTO.setApprovalTxt(APPROVER_ROBOT_REJECT_Detail);
                    }
                } else {
                    approvalReqDTO.setApprovalTxt(ruleApprovalNodeDTO.getComments());
                }
                approvalRejectService.rejectWorkflow(UUID.fromString(APPROVER_TYPE_ROBOT_OID), approvalReqDTO);
                nodeTypeAndApprovalResult = RuleConstants.ROBOT_NODE_AND_APPROVAL_REJECT_RESULT;
            }
        }
        if (CollectionUtils.isNotEmpty(noticeList)) {
            logger.debug("buildNextApprovalChainByRule param entityOid : {} , entityType : {} , approvalChainService save noticeList count : {}", entityOid, entityType, noticeList.size());
            approvalChainService.saveAll(noticeList);
        }
        if (CollectionUtils.isNotEmpty(approvelList)) {
            logger.debug("buildNextApprovalChainByRule param entityOid : {} , entityType : {} , approvalChainService save approvelList count : {}", entityOid, entityType, approvelList.size());
            approvelList = approvalChainService.saveAll(approvelList);
        }
        /**
         *   如果是第一个节点且过滤过审批人且审批人为空  直接跳过审批  单据审批通过
         */
        if (isFirstNode && filterReleInvoked && CollectionUtils.isEmpty(approvelList)) {
            //todo
            // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
            workFlowDocumentRef.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
            workFlowDocumentRef.setRejectType("");
            workFlowDocumentRef.setRejectReason("");
            workFlowDocumentRef.setLastRejectType("");
            //发送事件消息，通知对应的服务修改单据的状态
            workflowEventPublishService.publishEvent(workFlowDocumentRef);
            return null;
        }
        workFlowDocumentRef.setCurrentApproverOids(approvalUserOids);
        return BuildApprovalChainResult.builder()
                .approvalChains(approvelList)
                .autoSelfApproval(autoSelfApproval).nodeTypeAndApprovalResult(nodeTypeAndApprovalResult)
                .endNodePrintEnable(endNodePrintEnable).build();
    }

    /**
     * 根据过滤规则过滤重复审批人
     * @param approvalUserOids
     * @param filterRule
     * @return
     */
    private boolean filterApproverByFilterRule(List<UUID> approvalUserOids, String filterRule, boolean filterReleInvoked, String filterTypeRule, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        Boolean billFilterRule = sysWorkFlowDocumentRef.getFilterFlag();
        Boolean isWithdraw = Boolean.FALSE;
        Integer entityType = sysWorkFlowDocumentRef.getDocumentCategory();
        UUID entityOid = sysWorkFlowDocumentRef.getDocumentOid();
        if (String.valueOf(RejectTypeEnum.WITHDRAW.getId()).equals(String.valueOf(sysWorkFlowDocumentRef.getLastRejectType()))) {
            isWithdraw = Boolean.TRUE;
        }
        //表单配置选择检验当前工作流+表单配置选择校验历史工作流但是命中特殊情况+用户撤回单据重走审批流(即校验当前工作流)
        if ((StringUtils.isNotBlank(filterTypeRule) && APPROVAL_CHAIN_FILTER.equals(Integer.valueOf(filterTypeRule))) || (billFilterRule != null && !billFilterRule) || isWithdraw) {
            //过滤和加签人重复的审批人
            if (StringUtils.isNotBlank(filterRule) && COUNTERSIGN_APPROVER_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalChain> existApprovalChainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndCountersignTypeNotNullAndIsNoticeIsFalse(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), approvalUserOids);
                if (CollectionUtils.isNotEmpty(existApprovalChainList)) {
                    //过滤重复审批人
                    filterApproverByApprovalChain(existApprovalChainList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
            //过滤和审批人重复的审批人
            if (StringUtils.isNotBlank(filterRule) && APPROVER_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalChain> existApprovalChainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndCountersignTypeIsNullAndIsNoticeIsFalse(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), approvalUserOids);
                if (CollectionUtils.isNotEmpty(existApprovalChainList)) {
                    //过滤重复审批人, 保留当前审批人的加签审批
                    filterApproverByApprovalChain(existApprovalChainList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
            //和加签人重复的审批人 或 和审批人重复的审批人都过滤
            if (StringUtils.isNotBlank(filterRule) && ALL_REPEATED_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalChain> existApprovalChainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndIsNoticeIsFalse(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), approvalUserOids);
                if (CollectionUtils.isNotEmpty(existApprovalChainList)) {
                    //过滤重复审批人, 保留当前审批人的加签审批
                    filterApproverByApprovalChain(existApprovalChainList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
        } else { //默认走过滤比对审批历史
            //过滤和加签人重复的审批人
            if (StringUtils.isNotBlank(filterRule) && COUNTERSIGN_APPROVER_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalHistory> existApprovalHistoryList = approvalHistoryService.listByEntityTypeAndEntityOidAndOperationAndCountersignTypeNotNull(
                        entityType, entityOid, ApprovalOperationEnum.APPROVAL_PASS.getId());
                if (CollectionUtils.isNotEmpty(existApprovalHistoryList)) {
                    //过滤重复审批人, 保留当前审批人的加签审批
                    filterApproverByFilterRule(existApprovalHistoryList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
            //过滤和审批人重复的审批人
            if (StringUtils.isNotBlank(filterRule) && APPROVER_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalHistory> existApprovalHistoryList = approvalHistoryService.listByEntityTypeAndEntityOidAndOperationAndCountersignTypeIsNull(
                        entityType, entityOid, ApprovalOperationEnum.APPROVAL_PASS.getId());
                if (CollectionUtils.isNotEmpty(existApprovalHistoryList)) {
                    //过滤重复审批人, 保留当前审批人的加签审批
                    filterApproverByFilterRule(existApprovalHistoryList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
            //和加签人重复的审批人 或 和审批人重复的审批人都过滤
            if (StringUtils.isNotBlank(filterRule) && ALL_REPEATED_FILTER.equals(Integer.valueOf(filterRule))) {
                List<ApprovalHistory> existApprovalHistoryList = approvalHistoryService.listByEntityTypeAndEntityOidAndOperation(
                        entityType, entityOid, ApprovalOperationEnum.APPROVAL_PASS.getId());
                logger.info("filter:filterApproverByFilterRule, existApprovalHistoryList:{},approvalUserOids:{},approvalUserOids:{},approvalUserOids:{}", existApprovalHistoryList, approvalUserOids, filterRule, filterTypeRule);
                logger.info("filter:filterApproverByFilterRule,entityType:{},entityOid:{}:", entityType, entityOid);

                if (CollectionUtils.isNotEmpty(existApprovalHistoryList)) {
                    //过滤重复审批人, 保留当前审批人的加签审批
                    filterApproverByFilterRule(existApprovalHistoryList, approvalUserOids);
                    filterReleInvoked = true;
                }
            }
        }
        return filterReleInvoked;
    }

    /**
     * remove 审批人
     * @param approvalUserOids
     */
    private void filterApproverByFilterRule(List<ApprovalHistory> approvalUserOids, List<UUID> ruleApproverUserOids) {
        List<UUID> approvers = Lists.newArrayList();
        List<UUID> newApprovers = approvalUserOids.stream().filter(p -> p.getRefApprovalChainId() != null).map(p -> approvalChainService.getApprovalChainByRefId(p.getRefApprovalChainId())).collect(Collectors.toList()).stream().filter(l -> l.getApproverOid() != null).map(l -> l.getApproverOid()).collect(Collectors.toList());
        logger.info("filter:filterApproverByFilterRule,newApprovers:{}", newApprovers);
        logger.info("filter:filterApproverByFilterRule,ruleApproverUserOids:{}", ruleApproverUserOids);
        if (CollectionUtils.isNotEmpty(newApprovers)) {
            approvers.addAll(newApprovers);
        }
        approvers.stream().forEach(a -> {
            ruleApproverUserOids.remove(a);
        });
    }

    private void filterApproverByApprovalChain(List<ApprovalChain> approvalUserOids, List<UUID> ruleApproverUserOids) {
        approvalUserOids.stream().forEach(approvalChain -> {
            ruleApproverUserOids.remove(approvalChain.getApproverOid());
        });
    }

    public void buildApprovalChain(UUID entityOid, Integer entityType, List<String> approverList, boolean emergency) {
        if (CollectionUtils.isEmpty(approverList)) {
            return;
        }

        // 承接已有的审批步骤
        ApprovalChain existApprovalChain = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusOrderBySequenceDesc(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
        int start = existApprovalChain == null ? 0 : existApprovalChain.getSequence();

        List<ApprovalChain> list = new LinkedList<>();
        for (int i = 0; i < approverList.size(); i++) {
            ApprovalChain approvalChain = new ApprovalChain();
            approvalChain.setEntityType(entityType);
            approvalChain.setEntityOid(entityOid);
            approvalChain.setSequence(i + 1 + start);

            if (approverList.get(i).indexOf(WorkflowConstants.APPROVAL_CHAIN_NOTICE_FLAG) != -1) {
                approvalChain.setNoticed(true);
                approvalChain.setApproverOid(UUID.fromString(approverList.get(i).replaceAll(WorkflowConstants.APPROVAL_CHAIN_NOTICE_FLAG, "")));
                approverList.set(i, approverList.get(i).replaceAll(WorkflowConstants.APPROVAL_CHAIN_NOTICE_FLAG, ""));
            } else {
                approvalChain.setApproverOid(UUID.fromString(approverList.get(i)));
            }
            approvalChain.setCurrentFlag(i == 0);
            if (emergency) {
                approvalChain.setCurrentFlag(true);
            }
            approvalChain.setFinishFlag(false);
            approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());
            approvalChain.setCreatedDate(ZonedDateTime.now());
            approvalChain.setLastUpdatedDate(ZonedDateTime.now());

            list.add(approvalChain);
        }
        approvalChainService.saveAll(list);
    }

    /**
     * 自审批通过
     *
     * @param approverOid
     * @param approvalTxt
     */
    public void selfApproval(UUID approverOid, String approvalTxt, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        passWorkflow(approverOid, null, approvalTxt, true, false, null, sysWorkFlowDocumentRef);
    }

    /**
     * 审批通过
     * 支持会签,一任务多节点
     *
     * @param approverOid 其实是当前登录人,可能是审批人本人,也可能是其代理人
     * @param approvalTxt
     */
    @Transactional
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, errorMessage = RespCode.SYS_REQUEST_BE_PROCESSING)
    public void passWorkflow(UUID approverOid, UUID chainApproverOid, String approvalTxt, boolean selfApproval, boolean priceAuditor, ApprovalResDTO approvalResDTO, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        ZonedDateTime now = ZonedDateTime.now();
        UUID entityOid = sysWorkFlowDocumentRef.getDocumentOid();
        Integer entityType = sysWorkFlowDocumentRef.getDocumentCategory();
        UUID formOid = sysWorkFlowDocumentRef.getFormOid();
        ApprovalChain approvalChain;
        RuleApprovalNodeDTO ruleApprovalNode = new RuleApprovalNodeDTO();
        if (chainApproverOid != null && !chainApproverOid.equals(approverOid)) {
            approvalChain = approvalChainService.getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, chainApproverOid);
        } else {
            approvalChain = approvalChainService.getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, approverOid);
        }
        if (approvalChain == null) {
            throw new BizException(RespCode.SYS_APPROVAL_NO_APPROVER, RuleConstants.CANNOT_FIND_CURRENT_APPROVAL);
            //throw new RuntimeException("Cannot find current approval step for: " + entityType + ", " + entityOid);
        }
        //获取当前审批节点
        if (approvalChain.getRuleApprovalNodeOid() != null) {
            ruleApprovalNode = brmsService.getApprovalNode(approvalChain.getRuleApprovalNodeOid(), approvalChain.getApproverOid());
        }
        // 1.当前的审批链审批完,插入操作历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);
        approvalHistory.setOperationType(selfApproval ? ApprovalOperationTypeEnum.SELF.getId() : ApprovalOperationTypeEnum.APPROVAL.getId());
        approvalHistory.setOperation(priceAuditor ? ApprovalOperationEnum.APPROVAL_PASS_NEED_PRICE_AUDIT.getId() : ApprovalOperationEnum.APPROVAL_PASS.getId());
        approvalHistory.setOperatorOid(approverOid);
        approvalHistory.setCountersignType(approvalChain.getCountersignType());
        approvalHistory.setApportionmentFlag(approvalChain.getApportionmentFlag());
        approvalHistory.setRuleApprovalNodeOid(approvalChain.getRuleApprovalNodeOid());
        //审批节点名称
        approvalHistory.setApprovalNodeName(ruleApprovalNode.getRemark());
        approvalHistory.setApprovalNodeOid(approvalChain.getRuleApprovalNodeOid());
        approvalHistory.setOperationDetail(approvalTxt);
        approvalHistory.setCreatedDate(now);
        approvalHistory.setCreatedDate(now.plusSeconds(1));
        approvalHistory.setLastUpdatedDate(now);
        //审批历史记录其关联的审批链
        approvalHistory.setRefApprovalChainId(approvalChain.getId());
        approvalHistoryService.save(approvalHistory);
        // 标记当前审批为完成
        approvalChain.setCurrentFlag(false);
        approvalChain.setFinishFlag(true);
        //不是加签人审批，并且没有审批加签 标记为true
        if (CollectionUtils.isEmpty(sysWorkFlowDocumentRef.getCountersignApproverOids()) && !IsAddSignEnum.SIGN_YES.getId().equals(approvalChain.getAddSign())) {
            approvalChain.setAllFinished(true);
        } else {
            approvalChain.setAllFinished(false);
        }
        approvalChain.setLastUpdatedDate(now);
        approvalChainService.save(approvalChain);
        List<UUID> noticeApproverList = new ArrayList<>();
        if ((RuleApprovalEnum.RULE_CONUTERSIGN_ANY.getId().equals(approvalChain.getCountersignRule())
                || RuleApprovalEnum.RULE_CONUTERSIGN_ALL_REJECT.getId().equals(approvalChain.getCountersignRule())) // added by mh.z 20190103 支持任一人会签规则
                && !approvalChain.getApportionmentFlag()) {
            //不包含分摊成本中心对应负责人（分摊对应的人 ApportionmentFlag为true） 分摊的成本中心对应负责人不受会签规则影响
            List<ApprovalChain> approvalChains = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApportionmentFlagFalseAndCountersignRule(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, approvalChain.getCountersignRule());
            if (CollectionUtils.isNotEmpty(approvalChains)) {
                for (ApprovalChain chain : approvalChains) {
                    chain.setCurrentFlag(false);
                    chain.setFinishFlag(true);
                    // added by mh.z 20190304 标记其他待审批该单据的实例无效，否则其他人（没有审批该单据）的已审批列表会有该单据
                    chain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
                    // END added by mh.z
                    chain.setLastUpdatedDate(now);
                    approvalChainService.save(chain);
                }
            }
        }
        //2.判断是否加签 如果加签就创建加签审批链并保存加签详情
        if (CollectionUtils.isNotEmpty(sysWorkFlowDocumentRef.getCountersignApproverOids())) {
            //根据加签审批人构建未激活的加签审批链 并保存加签历史
            buildApprovalChainByCountersignApproverOids(entityOid, entityType, approverOid, sysWorkFlowDocumentRef.getCountersignApproverOids(), approvalChain.getSequence()
                    , approvalChain.getCountersignType() != null ? ADD_SIGN_APPROVER_ADD_SIGN : APPROVER_ADD_SIGN, approvalChain.getRuleApprovalNodeOid(), ruleApprovalNode.getRemark(), approvalChain.getProxyFlag(), ApprovalFormPropertyConstants.COUNTERSIGN_TYPE, approvalChain.getId(), formOid);
        }

        //3.判断当前审批链是否是审批节点,如果是审批节点,审批完激活加签审批链（非加签审批链）
        if (approvalChain.getCountersignType() == null) {
            //判断同级其他未审批任务
            List<ApprovalChain> existApprovalChain = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndFinishFlagAndCountersignType(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, false, null);
            if (CollectionUtils.isEmpty(existApprovalChain)) {
                List<UUID> approverOids = new ArrayList<>();
                //判断是否有未激活的加签任务 若有，激活sequence最小的任务
                List<ApprovalChain> countersignApprovalChainList = approvalChainService.listNextSequenceApprovalChain(
                        entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), false, false);
                if (CollectionUtils.isNotEmpty(countersignApprovalChainList)) {
                    countersignApprovalChainList.stream().forEach(countersignApprovalChain -> {
                        approverOids.add(countersignApprovalChain.getApproverOid());
                        countersignApprovalChain.setCurrentFlag(true);
                    });
                    approvalChainService.saveAll(countersignApprovalChainList);
                    if (CollectionUtils.isNotEmpty(approverOids)) {
                        noticeApproverList.addAll(approverOids);
                    }
                } else {
                    //说明审批节点同级无未审批任务,并且也没有待激活的加签任务,把提交时的加签激活出来
                    if (approvalChain.getProxyFlag() == null ? false : approvalChain.getProxyFlag()) {
                        //拿提交的加签人构造出来
                        List<UUID> uuids = buildAndActiveProxyAddSignApproversChain(entityOid, entityType, approverOid, approvalChain.getSequence(), formOid, ruleApprovalNode.getRemark());
                        if (CollectionUtils.isNotEmpty(uuids)) {
                            noticeApproverList.addAll(uuids);
                        }
                    }
                }
            }
        } else {
            //4.如果当前节点是加签的审批链
            //根据会签规则失效同组的加签审批链  所有会签审批人通过或其中一个会签审批人通过即可
            if (RULE_CONUTERSIGN_ANY.equals(approvalChain.getCountersignType())) {
                // 审批步骤作废
                List<ApprovalChain> chainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndCountersignTypeAndApproverOidNotAndSequence(entityType, entityOid,
                        ApprovalChainStatusEnum.NORMAL.getId(), RULE_CONUTERSIGN_ANY, approvalChain.getApproverOid(), approvalChain.getSequence());
                if (CollectionUtils.isNotEmpty(chainList)) {
                    for (ApprovalChain chain : chainList) {
                        chain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
                        chain.setLastUpdatedDate(ZonedDateTime.now());
                    }
                    approvalChainService.saveAll(chainList);
                }
            }

            //5.如果当前的同组加签节点审批完 查询下组是否有未完成的加签节点 有就激活下一个sequence加签审批链
            List<ApprovalChain> existApprovalChain = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndFinishFlagAndCountersignTypeNotNullAndSequence(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, false, approvalChain.getSequence());
            if (CollectionUtils.isEmpty(existApprovalChain)) {
                List<UUID> approverOids = new ArrayList<>();
                //判断是否有未激活的加签任务 若有，激活sequence最小的任务
                List<ApprovalChain> countersignApprovalChainList = approvalChainService.listNextSequenceApprovalChain(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), false, false);
                if (CollectionUtils.isNotEmpty(countersignApprovalChainList)) {
                    countersignApprovalChainList.stream().forEach(countersignApprovalChain -> {
                        approverOids.add(countersignApprovalChain.getApproverOid());
                        countersignApprovalChain.setCurrentFlag(true);
                    });
                    approvalChainService.saveAll(countersignApprovalChainList);
                    if (CollectionUtils.isNotEmpty(approverOids)) {
                        noticeApproverList.addAll(approverOids);
                    }
                } else {
                    //说明加签审批节点后没有待激活的加签任务把提交时的加签激活出来
                    if (approvalChain.getProxyFlag() == null ? false : approvalChain.getProxyFlag()) {
                        //拿提交的加签人构造出来
                        List<UUID> uuids = buildAndActiveProxyAddSignApproversChain(entityOid, entityType, approverOid, approvalChain.getSequence(), formOid, ruleApprovalNode.getRemark());
                        if (CollectionUtils.isNotEmpty(uuids)) {
                            noticeApproverList.addAll(uuids);
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(sysWorkFlowDocumentRef.getCountersignApproverOids()) && IsAddSignEnum.SIGN_YES.getId().equals(approvalChain.getAddSign())) {
            checkChainSignGroupFinishStatus(approvalChain);
        }

        // modify by mh.z 20190308 机器人审批通过则单据审批通过
        //normalApprovalNode(approvalChain, now,sysWorkFlowDocumentRef);
        if (approvalChain != null && RuleConstants.APPROVER_TYPE_ROBOT_OID.equals(String.valueOf(approvalChain.getApproverOid()))) {
            // 通过单据
            passDocument(approvalChain, sysWorkFlowDocumentRef);
        } else {
            normalApprovalNode(approvalChain, now, sysWorkFlowDocumentRef);
        }
        // END modify by mh.z
    }

    /**
     * 如果加签组全部完成更新allFinished 状态为true
     * @param approvalChainSource
     */
    private void checkChainSignGroupFinishStatus(ApprovalChain approvalChainSource) {
        ApprovalChain checkApprovalChain = new ApprovalChain();
        BeanUtils.copyProperties(approvalChainSource, checkApprovalChain);
        ApprovalChain sourceApprovalChain = queryStartSignChainID(checkApprovalChain);
        if (checkSignGroupFinishStatus(sourceApprovalChain)) {
            updateSignGroupFinishTrue(sourceApprovalChain);
        }
    }

    /**
     * 查找加签源头
     * @param checkApprovalChain
     */
    private ApprovalChain queryStartSignChainID(ApprovalChain checkApprovalChain) {
        if (IsAddSignEnum.SIGN_YES.getId().equals(checkApprovalChain.getAddSign()) && checkApprovalChain.getSourceApprovalChainId() != null) {
            return queryStartSignChainID(approvalChainService.getApprovalChainById(checkApprovalChain.getSourceApprovalChainId()));
        } else {
            return checkApprovalChain;
        }
    }

    /**
     * 从加签源头检查整个加签组是否都完成
     * @param checkApprovalChain
     */
    private Boolean checkSignGroupFinishStatus(ApprovalChain checkApprovalChain) {
        List<ApprovalChain> allApprovalChain = new ArrayList<>();
        allApprovalChain = getSignGroup(checkApprovalChain, allApprovalChain);
        if (CollectionUtils.isNotEmpty(allApprovalChain)) {
            return allApprovalChain.stream().allMatch(s -> Boolean.TRUE.equals(s.getFinishFlag()));
        }
        return Boolean.TRUE;
    }

    /**
     * 获得加签组信息
     * @param checkApprovalChain
     * @return
     */
    private List<ApprovalChain> getSignGroup(ApprovalChain checkApprovalChain, List<ApprovalChain> allSignGroupApprovalChain) {
        if (checkApprovalChain != null) {
            allSignGroupApprovalChain.add(checkApprovalChain);
            List<ApprovalChain> signApprovalChainList = approvalChainService.listByEntityTypeAndEntityOidAndStatusAndSourceApprovalChainId(checkApprovalChain.getEntityType(), checkApprovalChain.getEntityOid(),
                    ApprovalChainStatusEnum.NORMAL.getId(), checkApprovalChain.getId());
            if (CollectionUtils.isNotEmpty(signApprovalChainList)) {
                for (ApprovalChain approvalChain : signApprovalChainList) {
                    getSignGroup(approvalChain, allSignGroupApprovalChain);
                }
            }
        }
        return allSignGroupApprovalChain;
    }

    /**
     * 更新整个加签组为完成
     * @param checkApprovalChain
     */
    private void updateSignGroupFinishTrue(ApprovalChain checkApprovalChain) {
        List<ApprovalChain> allApprovalChain = new ArrayList<>();
        allApprovalChain = getSignGroup(checkApprovalChain, allApprovalChain);
        if (CollectionUtils.isNotEmpty(allApprovalChain)) {
            allApprovalChain.stream().forEach(a -> approvalChainService.updateAllFinshTrueById(a.getId()));
        }
    }

    /**
     * 构造并且激活代理人提交时加签的加签人审批链
     * @param entityType
     * @param entityOid
     * @param currentSequence
     */
    private List<UUID> buildAndActiveProxyAddSignApproversChain(UUID entityOid, Integer entityType, UUID submittedBy, Integer currentSequence, UUID formOid, String approvalNodeName) {

        List<UUID> countersignApproversOidsSet = countersignDetailService.listAndInvalidateByEntityTypeAndEntityOidAndOperationType(entityType, entityOid, baseClient.getUserByUserOid(submittedBy).getId(), CounterSignOperationTypeEnum.COUNTER_SIGN_OPERATION_TYPE_CREATED_BY_SUBMIT.getValue());
        if (CollectionUtils.isNotEmpty(countersignApproversOidsSet)) {
            //构建加签审批链
            List<ApprovalChain> approvalChainList = buildApprovalChainByCountersignApproverOids(entityOid, entityType, submittedBy, countersignApproversOidsSet, currentSequence++, APPROVER_ADD_SIGN, null, approvalNodeName, false, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER, null, formOid);
            //激活审批链
            return activeApprovalChain(entityOid, entityType, approvalChainList, formOid);
        }
        return null;
    }

    /**
     * @param approvalChain
     * @param now
     */
    public void normalApprovalNode(ApprovalChain approvalChain, ZonedDateTime now, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        Integer entityType = sysWorkFlowDocumentRef.getDocumentCategory();
        UUID entityOid = sysWorkFlowDocumentRef.getDocumentOid();
        /**
         * 判断会签是否都审批完
         */
        ApprovalChain otherApproverChain = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusAndCurrentFlagOrderBySequenceDesc(entityType, entityOid,
                ApprovalChainStatusEnum.NORMAL.getId(), true);
        if (otherApproverChain == null) {
            BuildApprovalChainResult buildApprovalChainResult = getNextChain(approvalChain, sysWorkFlowDocumentRef);
            List<ApprovalChain> nextChains = buildApprovalChainResult == null ? null : buildApprovalChainResult.getApprovalChains();
            if (!CollectionUtils.isEmpty(nextChains)) {
                // 继续下一步的审批
                nextChains.stream().forEach(nextChain -> {
                    nextChain.setCurrentFlag(true);
                    nextChain.setLastUpdatedDate(now);
                    approvalChainService.save(nextChain);
                });
            } else if (buildApprovalChainResult != null && buildApprovalChainResult.getNodeTypeAndApprovalResult().equals(ROBOT_NODE_AND_APPROVAL_REJECT_RESULT)) {

            } else if (buildApprovalChainResult != null && buildApprovalChainResult.getNodeTypeAndApprovalResult().equals(ROBOT_NODE_AND_APPROVAL_PASS_RESULT)) {

            } else { // 审批完了
                passDocument(approvalChain, sysWorkFlowDocumentRef);
            }
        }
    }

    /**
     * @author mh.z
     * @date 2019/03/08
     * @description 通过单据
     *
     * @param approvalChain
     * @param workFlowDocumentRef
     */
    private void passDocument(ApprovalChain approvalChain, WorkFlowDocumentRef workFlowDocumentRef) {
        if (approvalChain == null) {
            throw new IllegalArgumentException("approvalChain null");
        }

        if (workFlowDocumentRef == null) {
            throw new IllegalArgumentException("workFlowDocumentRef null");
        }

        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID entityOid = workFlowDocumentRef.getDocumentOid();

        logger.info("审批结束 param entityOid : {} , entityType : {} , finish approval ", entityOid, entityType);
        workFlowDocumentRef.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
        workFlowDocumentRef.setRejectType("");
        workFlowDocumentRef.setRejectReason("");
        workFlowDocumentRef.setLastRejectType("");
        workFlowDocumentRef.setLastApproverOid(approvalChain.getApproverOid());
    }

    /**
     * 获取下一组审批链
     * @param last
     * @return
     */
    protected BuildApprovalChainResult getNextChain(ApprovalChain last, WorkFlowDocumentRef sysWorkFlowDocumentRef) {
        Integer entityType = sysWorkFlowDocumentRef.getDocumentCategory();
        UUID entityOid = sysWorkFlowDocumentRef.getDocumentOid();
        UUID companyOid = sysWorkFlowDocumentRef.getCompanyOid();
        //如果上一个审批链的规则审批节点Oid不为null 或者是加签的审批链走BRMS工作流
        if (last != null && (last.getRuleApprovalNodeOid() != null || last.getCountersignType() != null)) {
            //使用规则审批
            logger.debug("debug->getNextChain：");
            BuildApprovalChainResult buildApprovalChainResult = buildNextApprovalChainByRule(entityType, last.getRuleApprovalNodeOid(), false, sysWorkFlowDocumentRef);
            return buildApprovalChainResult;
        } else {
            ApprovalPathModeEnum companyApprovalPathMode = defaultWorkflowIntegrationService.getCompanyApprovalPathMode(companyOid);
            if (companyApprovalPathMode != ApprovalPathModeEnum.FULL) {
                List<String> approverList = defaultWorkflowIntegrationService.getWorkflowNextApprovalPath(sysWorkFlowDocumentRef.getUserOid(), entityOid, last == null ? null : last.getApproverOid(), entityType);
                if (CollectionUtils.isNotEmpty(approverList)) {
                    buildApprovalChain(entityOid, entityType, approverList, false);
                }
            }
            ApprovalChain next = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusAndFinishFlagIsFalseAndSequenceGreaterThanOrderBySequenceAsc(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), last == null ? 0 : last.getSequence());
            if (next == null) {
                return null;
            }
            List<UUID> noticeMessageList = new ArrayList<>();
            ApprovalChain result = dealWithNextChain(entityType, entityOid, next, noticeMessageList);
            if (result == null) {
                return null;
            }
            BuildApprovalChainResult buildApprovalChainResult = new BuildApprovalChainResult();
            buildApprovalChainResult.setApprovalChains(Arrays.asList(result));
            return buildApprovalChainResult;
        }
    }

    private ApprovalChain dealWithNextChain(Integer entityType, UUID entityOid, ApprovalChain next, List<UUID> noticeMessageList) {
        if (next != null && next.getNoticed()) {//通知
            // 消息推送知会人
            noticeMessageList.add(next.getApproverOid());
            next = approvalChainService.getTop1ByEntityTypeAndEntityOidAndStatusAndFinishFlagIsFalseAndSequenceGreaterThanOrderBySequenceAsc(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), next.getSequence());
            return dealWithNextChain(entityType, entityOid, next, noticeMessageList);
        }
        return next;
    }


    //对比单据数据 判断是否开启过滤 也就是拒绝和撤回时都不需要再保存当时单据的金额了
    public Boolean findFilterRuleNew(WorkFlowDocumentRef workFlowDocumentRef) {
        WorkFlowDocumentRef dbDoc = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(workFlowDocumentRef.getDocumentOid(), workFlowDocumentRef.getDocumentCategory());
        if (dbDoc != null) {
            BigDecimal rejectedAmount = dbDoc.getAmount();
            BigDecimal resubmittedAmount = workFlowDocumentRef.getAmount();
            if (resubmittedAmount == null) {
                resubmittedAmount = BigDecimal.ZERO;
            }
            ApprovalFormPropertyRuleDTO formRule = approvalFormPropertyService.selectByFormOid(workFlowDocumentRef.getFormOid());
            logger.info("Form config: enableAmountFilter[{}], enableExpenseTypeFilter[{}]", formRule.isEnableAmountFilter
                    (), formRule.isEnableExpenseTypeFilter());
            logger.info("Amount change: {} -> {}", rejectedAmount, resubmittedAmount);
            // 不开启审批人过滤的条件：
            // 1. 启用了『当单据金额变大时，再提交时不跳过已审批环节』，并且金额变大
            return !(formRule.isEnableAmountFilter() && resubmittedAmount.compareTo(rejectedAmount) > 0);
        } else {
            return true;
        }
    }

    /**
     * 单据提交
     */
    @Transactional
    public void submitWorkflow(WorkFlowDocumentRef workFlowDocumentRef) {
        // 设置FilterFlag
        if (workFlowDocumentRef.getDocumentOid() == null) {
            throw new BizException("提交失败，单据Oid不允许为空!");
        }
        UUID documentOid = workFlowDocumentRef.getDocumentOid();
        UUID userOid = workFlowDocumentRef.getUserOid();
        UUID formOid = workFlowDocumentRef.getFormOid();
        Boolean filterFlag = findFilterRuleNew(workFlowDocumentRef);
        workFlowDocumentRef.setFilterFlag(filterFlag);
        //保存单据信息
        workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
        //获取公司信息
        CompanyCO company = baseClient.getCompanyById(workFlowDocumentRef.getCompanyId());
        //查询审批人
        //设置审批历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setEntityType(workFlowDocumentRef.getDocumentCategory());
        approvalHistory.setEntityOid(documentOid);
        approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
        approvalHistory.setOperation(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId());
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setApprovalNodeName(ApprovalNodeEnum.SUBMIT_NODE.getName());// 第一个节点名为 提交
        approvalHistoryService.save(approvalHistory);
        try {
            // 审批流
            BuildApprovalChainResult buildApprovalChainResult = null;
            //判断是否是自定义审批流
            if (brmsService.isEnableRule( workFlowDocumentRef.getFormOid())) {
                buildApprovalChainResult = buildNewApprovalChainResultByRuleOrApproverOids(workFlowDocumentRef.getDocumentCategory(), null, ApprovalNodeEnum.SUBMIT_NODE.getName(), workFlowDocumentRef);
            } else {
                List<String> approverList = null;
                UUID companyOid = baseClient.getCompanyByUserOid(userOid).getCompanyOid();
                ApprovalPathModeEnum companyApprovalPathMode = defaultWorkflowIntegrationService.getCompanyApprovalPathMode(companyOid);
                if (companyApprovalPathMode == ApprovalPathModeEnum.FULL) {
                    approverList = defaultWorkflowIntegrationService.getWorkflowApprovalPath(userOid, documentOid, workFlowDocumentRef.getDocumentCategory());
                } else {
                    approverList = defaultWorkflowIntegrationService.getWorkflowNextApprovalPath(userOid, documentOid, null, workFlowDocumentRef.getDocumentCategory());
                }
                buildApprovalChain(documentOid, workFlowDocumentRef.getDocumentCategory(), approverList, false);
            }
            /**
             * 满足自审批要求
             * 申请人自审批
             */
            if (buildApprovalChainResult != null && buildApprovalChainResult.getAutoSelfApproval()) {
                logger.debug("submitApplication param entityOid : {} , entityType : {} , self approval ", documentOid, workFlowDocumentRef.getDocumentCategory());
                this.selfApproval(userOid, "", workFlowDocumentRef);
            }
            if (buildApprovalChainResult != null && buildApprovalChainResult.getApprovalChains().size() > 0) {
                // 修改各单据的审批状态
                workflowEventPublishService.publishEvent(workFlowDocumentRef);
            }
        } catch (ValidationException e) {
            e.printStackTrace();

            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_APPROVAL_CHAIN_IS_NULL, e.getMessage());
        }
    }
}
