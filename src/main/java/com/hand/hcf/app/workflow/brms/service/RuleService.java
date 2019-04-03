package com.hand.hcf.app.workflow.brms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.common.co.DepartmentPositionCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalChain;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.domain.RuleApprover;
import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.domain.RuleConditionRelation;
import com.hand.hcf.app.workflow.brms.domain.RuleScene;
import com.hand.hcf.app.workflow.brms.dto.CustomFormApprovalModeDTO;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.NotifyInfo;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverUserOidsDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleEnumDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleNextApproverResult;
import com.hand.hcf.app.workflow.brms.enums.OperationEntityTypeEnum;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.ApprovalFormApprovalModeMapper;
import com.hand.hcf.app.workflow.brms.util.cache.CacheNames;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormQO;
import com.hand.hcf.app.workflow.dto.FormFieldDTO;
import com.hand.hcf.app.workflow.enums.ApprovalFormEnum;
import com.hand.hcf.app.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.enums.FieldType;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.ApprovalFormPropertyService;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RuleService {

    private final Logger log = LoggerFactory.getLogger(RuleService.class);

    @Inject
    ApprovalFormService approvalFormService;
    @Autowired
    ApprovalFormPropertyService approvalFormPropertyService;
    @Autowired
    BaseClient baseClient;
    @Inject
    RuleApprovalChainService ruleApprovalChainService;
    @Inject
    RuleSceneService ruleSceneService;
    @Inject
    RuleConditionRelationService ruleConditionRelationService;
    @Inject
    RuleConditionService ruleConditionService;
    @Inject
    RuleApprovalNodeService ruleApprovalNodeService;
    @Inject
    RuleApproverService ruleApproverService;

    @Inject
    private DroolsService droolsService;

    @Autowired
    private ApprovalFormApprovalModeMapper approvalFormApprovalModeMapper;

    @Autowired
    private com.hand.hcf.app.workflow.service.WorkFlowApprovalService workFlowApprovalService;
    @Autowired
    private MapperFacade mapper;


    /**
     * 审批链相关接口 ***********************
     */
    public RuleApprovalChainDTO createRuleApprovalChain(RuleApprovalChainDTO ruleApprovalChainDTO) {

        if (ruleApprovalChainDTO.getFormOid() == null) {
            throw new BizException("approvalRule.createRuleApprovalChain", "formOid is null");
        }
        if (ruleApprovalChainDTO.getApprovalMode() == null) {
            throw new BizException("approvalRule.createRuleApprovalChain", "aprovalMode is null");
        }


        if (ruleApprovalChainDTO.getCheckData() == null || ruleApprovalChainDTO.getCheckData()) {
            //check customForm
            ApprovalFormDTO formDTO = approvalFormService.getCustomFormDetailForRule(ruleApprovalChainDTO.getFormOid());
            if (formDTO == null) {
                throw new BizException("approvalRule.createRuleApprovalChain", "form not exist , formOid: " + ruleApprovalChainDTO.getFormOid());

            }
        }
        RuleCondition existRuleCondition = ruleConditionService.getFormRuleCondition(ruleApprovalChainDTO.getFormOid());
        //TODO 先不允许多审批链
        if (existRuleCondition != null) {
            throw new BizException("approvalRule.createRuleApprovalChain", "conditioin exist , formOid: " + ruleApprovalChainDTO.getFormOid());
        }

        //创建场景
        RuleScene ruleScene = ruleSceneService.save(
                RuleScene.builder()
                        .sequenceNumber(RuleConstants.RULE_SEQUENCE_DEFAULT)
                        .build()
        );

        //创建审批链
        RuleApprovalChain ruleApprovalChain = mapper.map(ruleApprovalChainDTO, RuleApprovalChain.class);
        ruleApprovalChain.setRuleApprovalChainOid(null);
        ruleApprovalChain.setRuleSceneOid(ruleScene.getRuleSceneOid());
        ruleApprovalChain = ruleApprovalChainService.save(ruleApprovalChain);

        //创建条件
        RuleCondition ruleCondition = ruleConditionService.save(
                RuleCondition.builder()
                        .batchCode(RuleConstants.RULE_BATCH_CODE_DEFAULT)
                        .typeNumber(RuleConstants.CONDITION_TYPE_FORM)
                        .symbol(RuleConstants.SYMBOL_EQ)
                        .ruleValue(ruleApprovalChainDTO.getFormOid().toString())
                        .build()
        );

        //审批链关联场景
        ruleConditionRelationService.save(
                RuleConditionRelation.builder()
                        .ruleConditionOid(ruleCondition.getRuleConditionOid())
                        .entityType(RuleApprovalEnum.CONDITION_RELATION_TYPE_SCENE.getId())
                        .entityOid(ruleScene.getRuleSceneOid())
                        .build()
        );
        this.saveLog(ruleApprovalChain, "新增审批链:" + ruleApprovalChain.getRuleApprovalChainOid(), OperationEntityTypeEnum.APPROVAL_CHAIN.getKey());
        return mapper.map(ruleApprovalChain, RuleApprovalChainDTO.class);
    }

    public RuleApprovalChainDTO getApprovalChainByFormOid(UUID formOid, UUID userOid, boolean cascadeApprovalNode, boolean cascadeApprover, boolean cascadeCondition) {
        if (formOid == null) {
            return null;
        }
        RuleCondition ruleCondition = ruleConditionService.getFormRuleCondition(formOid);
        if (ruleCondition == null) {
            return null;
        }

        RuleConditionRelation ruleConditionRelation = ruleConditionRelationService.findByRuleConditionOidAndEntityType(ruleCondition.getRuleConditionOid(), RuleApprovalEnum.CONDITION_RELATION_TYPE_SCENE.getId());
        if (ruleConditionRelation == null) {
            return null;
        }

        RuleApprovalChain ruleApprovalChain = ruleApprovalChainService.getByRuleSceneOid(ruleConditionRelation.getEntityOid());
        if (ruleApprovalChain == null) {
            return null;
        }
        RuleApprovalChainDTO result = mapper.map(ruleApprovalChain, RuleApprovalChainDTO.class);
        result.setFormOid(formOid);
        consummateRuleApprovalChain(result, cascadeApprovalNode, cascadeApprover, cascadeCondition);
        return result;
    }

    public RuleApprovalChainDTO findRuleApprovalChain(UUID ruleApprovalChainOid, UUID userOid, boolean cascadeApprovalNode, boolean cascadeApprover, boolean cascadeCondition) {
        RuleApprovalChain ruleApprovalChain = ruleApprovalChainService.getByOid(ruleApprovalChainOid);
        if (ruleApprovalChain == null) {
            return null;
        }
        RuleApprovalChainDTO result = mapper.map(ruleApprovalChain, RuleApprovalChainDTO.class);
        consummateRuleApprovalChain(result, cascadeApprovalNode, cascadeApprover, cascadeCondition);
        return result;
    }

    public RuleApprovalChainDTO updateRuleApprovalChain(RuleApprovalChainDTO ruleApprovalChainDTO, UUID userOid) {
        if (ruleApprovalChainDTO.getRuleApprovalChainOid() != null) {
            throw new BizException("approvalRule.updateRuleApprovalChain", "ruleApprovalChainOid is null");
        }
        if (ruleApprovalChainDTO.getApprovalMode() == null) {
            throw new BizException("approvalRule.updateRuleApprovalChain", "aprovalMode is null");
        }
        if (ruleApprovalChainDTO.getFormOid() == null) {
            throw new BizException("approvalRule.updateRuleApprovalChain", "formOid is null");
        }
        RuleApprovalChain existRuleApprovalChain = ruleApprovalChainService.getByOid(ruleApprovalChainDTO.getRuleApprovalChainOid());
        if (existRuleApprovalChain == null) {
            throw new BizException("approvalRule.updateRuleApprovalChain", "ruleApprovalChain not exist , ruleApprovalChainOid : " + ruleApprovalChainDTO.getRuleApprovalChainOid());
        }
        RuleApprovalChain oldRuleApprovalChain = new RuleApprovalChain();
        RuleApprovalChain newRuleApprovalChain = new RuleApprovalChain();
        BeanUtils.copyProperties(existRuleApprovalChain, oldRuleApprovalChain);
        //如果修改了审批模式，修改表单表的approvalMode字段
        if (!String.valueOf(ruleApprovalChainDTO.getApprovalMode()).equals(String.valueOf(existRuleApprovalChain.getApprovalMode()))) {
            Long tenantId = OrgInformationUtil.getCurrentTenantId();
            if (null == tenantId) {
                throw new BizException("approvalRule.updateRuleApprovalChain", "tenantId is null");
            }
            log.info("updateRuleApprovalChain:formOid" + ruleApprovalChainDTO.getFormOid() + ",ruleApprovalChainOid:" + ruleApprovalChainDTO.getRuleApprovalChainOid() + ",approvalMode:" + ruleApprovalChainDTO.getApprovalMode() + ",tenantId:" + tenantId);
            approvalFormService.synchronizeApprovalMode(ruleApprovalChainDTO.getFormOid(), ruleApprovalChainDTO.getApprovalMode(), tenantId);
        }
        existRuleApprovalChain.setCode(ruleApprovalChainDTO.getCode());
        existRuleApprovalChain.setName(ruleApprovalChainDTO.getName());
        existRuleApprovalChain.setRemark(ruleApprovalChainDTO.getRemark());
        existRuleApprovalChain.setApprovalMode(ruleApprovalChainDTO.getApprovalMode());
        existRuleApprovalChain.setLevelNumber(ruleApprovalChainDTO.getLevel());

        existRuleApprovalChain = ruleApprovalChainService.save(existRuleApprovalChain);
        mapper.map(existRuleApprovalChain, newRuleApprovalChain);
        this.updateLog(oldRuleApprovalChain, newRuleApprovalChain, OperationEntityTypeEnum.APPROVAL_CHAIN.getKey());
        return mapper.map(existRuleApprovalChain, RuleApprovalChainDTO.class);
    }


    /**
     * 复制审批链
     * copy审批链
     * copy节点
     * copy审批者
     * copy条件
     *
     * @param sourceFormOid
     * @param targetFormOid
     * @return
     */
    public RuleApprovalChainDTO copyApprovalChain(UUID sourceFormOid, UUID targetFormOid) {
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        RuleApprovalChainDTO sourceRuleApprovalChainDTO = getApprovalChainByFormOid(sourceFormOid, userOid, true, true, true);
        if (sourceRuleApprovalChainDTO == null) {
            return null;
        }
        if (targetFormOid == null) {
            throw new BizException("approvalRule.copyApprovalChain", "targetFormOid is null");
        }
        //check customForm
        ApprovalFormDTO formDTO = approvalFormService.getDTOByQO(ApprovalFormQO.builder().formOid(targetFormOid).build());
        if (formDTO == null) {
            throw new BizException("approvalRule.copyApprovalChain", "targetForm not exist , formOid: " + targetFormOid);
        }

        RuleApprovalChainDTO targetRuleApprovalChainDTO = getApprovalChainByFormOid(targetFormOid, userOid, true, true, true);

        RuleApprovalChain targetRuleApprovalChain = mapper.map(targetRuleApprovalChainDTO, RuleApprovalChain.class);
        //copy 表单配置
        approvalFormPropertyService.synchronizeCustomFormProperty(sourceFormOid, targetFormOid);
        //创建目标表单审批链
        if (targetRuleApprovalChainDTO == null) {
            //创建场景
            RuleScene ruleScene = ruleSceneService.save(
                    RuleScene.builder()
                            .sequenceNumber(RuleConstants.RULE_SEQUENCE_DEFAULT)
                            .build()
            );
            //创建审批链
            RuleApprovalChain ruleApprovalChain = mapper.map(sourceRuleApprovalChainDTO, RuleApprovalChain.class);
            ruleApprovalChain.setRuleApprovalChainOid(null);
            ruleApprovalChain.setRuleSceneOid(ruleScene.getRuleSceneOid());
            targetRuleApprovalChain = ruleApprovalChainService.save(ruleApprovalChain);
            //创建条件
            RuleCondition ruleCondition = ruleConditionService.save(
                    RuleCondition.builder()
                            .batchCode(RuleConstants.RULE_BATCH_CODE_DEFAULT)
                            //.field(RuleApprovalEnum.CONDITION_TYPE_FORM.getId().toString())
                            .typeNumber(RuleConstants.CONDITION_TYPE_FORM)
                            .symbol(RuleConstants.SYMBOL_EQ)
                            .ruleValue(targetFormOid.toString())
                            .build()
            );

            //审批链关联场景
            ruleConditionRelationService.save(
                    RuleConditionRelation.builder()
                            .ruleConditionOid(ruleCondition.getRuleConditionOid())
                            .entityType(RuleApprovalEnum.CONDITION_RELATION_TYPE_SCENE.getId())
                            .entityOid(ruleScene.getRuleSceneOid())
                            .build()
            );
            targetRuleApprovalChainDTO = mapper.map(targetRuleApprovalChain, RuleApprovalChainDTO.class);
        } else {
            if (!targetRuleApprovalChain.getApprovalMode().equals(sourceRuleApprovalChainDTO.getApprovalMode())) {
                targetRuleApprovalChain = ruleApprovalChainService.getByOid(targetRuleApprovalChain.getRuleApprovalChainOid());
                targetRuleApprovalChain.setApprovalMode(sourceRuleApprovalChainDTO.getApprovalMode());
                targetRuleApprovalChain.setCode(sourceRuleApprovalChainDTO.getCode());
                targetRuleApprovalChain.setName(sourceRuleApprovalChainDTO.getName());
                targetRuleApprovalChain.setRemark(sourceRuleApprovalChainDTO.getRemark());
                targetRuleApprovalChain.setLevelNumber(sourceRuleApprovalChainDTO.getLevel());
                targetRuleApprovalChain = ruleApprovalChainService.save(targetRuleApprovalChain);
            }
        }
        List<RuleApprovalNodeDTO> sourceRuleApprovalNodeDTOs = sourceRuleApprovalChainDTO.getRuleApprovalNodes();

        List<RuleApprovalNode> targetRuleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOid(targetRuleApprovalChainDTO.getRuleApprovalChainOid());
        if (!CollectionUtils.isEmpty(targetRuleApprovalNodes)) {
            //删除目标表单已存在审批节点
            List<UUID> targetRuleApprovalNodeOids = targetRuleApprovalNodes.stream().map(RuleApprovalNode::getRuleApprovalNodeOid).collect(Collectors.toList());
            deleteRuleApprovalNode(targetRuleApprovalNodeOids, userOid, true, true);
        }
        UUID targetRuleApprovalChainOid = targetRuleApprovalChain.getRuleApprovalChainOid();
        //copy节点
        if (CollectionUtils.isEmpty(sourceRuleApprovalNodeDTOs)) {
            return targetRuleApprovalChainDTO;
        }
        sourceRuleApprovalNodeDTOs.forEach(sourceRuleApprovalNodeDTO -> {
            RuleApprovalNode targetRuleApprovalNode = mapper.map(sourceRuleApprovalNodeDTO, RuleApprovalNode.class);
            targetRuleApprovalNode.setRuleApprovalNodeOid(null);
            targetRuleApprovalNode.setRuleApprovalChainOid(targetRuleApprovalChainOid);
            targetRuleApprovalNode.setCreatedDate(ZonedDateTime.now());
            try {
                targetRuleApprovalNode.setNotifyInfo(new ObjectMapper().writeValueAsString(sourceRuleApprovalNodeDTO.getNotifyInfo()).toString());
            } catch (JsonProcessingException e) {
                throw new BizException("approvalRule.createRuleApprovalNode", "序列化失败");
            }
            if (nodeFilter(targetRuleApprovalNode, formDTO)) {
                return;
            }
            UUID targetRuleApprovalNodeOid = ruleApprovalNodeService.save(targetRuleApprovalNode).getRuleApprovalNodeOid();

            List<RuleApproverDTO> sourceRuleApproverDTOs = sourceRuleApprovalNodeDTO.getRuleApprovers();
            //copy审批者
            if (sourceRuleApproverDTOs != null) {
                sourceRuleApproverDTOs.forEach(sourceRuleApproverDTO -> {
                    RuleApprover targetRuleApprover = mapper.map(sourceRuleApproverDTO, RuleApprover.class);
                    targetRuleApprover.setRuleApproverOid(null);
                    targetRuleApprover.setRuleApprovalNodeOid(targetRuleApprovalNodeOid);
                    UUID targetRuleApproverOid = ruleApproverService.save(targetRuleApprover).getRuleApproverOid();
                    //copy条件
                    //TODO nick 2017/04/27
                    /**
                     * 条件属性设置字段Oid,无法直接copy
                     */

                    /*List<RuleConditionDTO> sourceRuleConditionDTOs = sourceRuleApproverDTO.getRuleConditionList();
                    if (!StringUtils.isEmpty(sourceRuleConditionDTOs)) {
                        sourceRuleConditionDTOs.forEach(sourceRuleConditionDTO -> {
                            RuleCondition targetRuleCondition = mapper.map(sourceRuleConditionDTO, RuleCondition.class);
                            StringWriter sw = new StringWriter();
                            ObjectMapper persistence = new ObjectMapper();
                            try {
                                persistence.writeValue(sw, sourceRuleConditionDTO.getValueDetail());
                                targetRuleCondition.setValueDetail(sw.toString());
                                sw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            targetRuleCondition.setRuleConditionOid(null);
                            targetRuleCondition = ruleConditionService.save(targetRuleCondition);
                            ///关联
                            ruleConditionRelationService.save(
                                RuleConditionRelation.builder()
                                    .ruleConditionOid(targetRuleCondition.getRuleConditionOid())
                                    .entityType(RuleApprovalEnum.CONDITION_RELATION_TYPE_APPROVER.getId())
                                    .entityOid(targetRuleApproverOid)
                                    .build()
                            );

                            //创建DroolsRule相关
                            DroolsRuleDetail droolsRuleDetail = new DroolsRuleDetail();
                            droolsRuleDetail.setRuleCondition(targetRuleCondition);
                            droolsRuleDetail.setDroolsRuleDetailOid(UUID.randomUUID());
                            droolsRuleDetail.setRuleConditionOid(targetRuleCondition.getRuleConditionOid());
                            droolsRuleDetail.setRuleConditionApproverOid(targetRuleApprover.getRuleApproverOid());
                            droolsRuleDetail = droolsService.save(droolsRuleDetail, targetRuleApprover, userOid, sourceRuleConditionDTO);
                        });
                    }*/
                });
            }

        });
        return targetRuleApprovalChainDTO;
    }

    /**
     * 节点过滤
     *
     * @param approvalFormDTO
     * @param ruleApprovalNode
     * @return
     */
    private Boolean nodeFilter(RuleApprovalNode ruleApprovalNode, ApprovalFormDTO approvalFormDTO) {
        Boolean resp = Boolean.FALSE;
        if (approvalFormDTO != null) {
            //判断修改核定金额
            if (RuleConstants.getNotUpdateCustomFormType.stream().anyMatch(c -> c.equals(approvalFormDTO.getFormType()))) {
                ruleApprovalNode.setInvoiceAllowUpdateType(null);
            }
            if (RuleConstants.NODE_TYPE_PRINT.equals(ruleApprovalNode.getTypeNumber())) {
                //判断打印节点
                return RuleConstants.getNotPrintCustomFormType.stream().anyMatch(c -> c.equals(approvalFormDTO.getFormType()));
            }
        }
        return resp;
    }

    public int updateApprovalChainStatus(UUID ruleApprovalChainOid, boolean enabled, UUID userOid) {
        RuleApprovalChain ruleApprovalChain = ruleApprovalChainService.getByOid(ruleApprovalChainOid);
        if (ruleApprovalChain == null) {
            return 0;
        }
        ruleApprovalChain.setStatus(enabled ? RuleApprovalEnum.VALID.getId() : RuleApprovalEnum.INVALID.getId());
        ruleApprovalChainService.save(ruleApprovalChain);
        return 1;
    }

    /**
     * 根据 typeNumber 创建审批几点 1：审批；2：知会；3：机器人; 4 打印接单, 5 结束节点
     *
     * @param ruleApprovalNodeDTO
     * @param userOid
     * @return
     */
    public RuleApprovalNodeDTO createRuleApprovalNodeMapping(RuleApprovalNodeDTO ruleApprovalNodeDTO, UUID userOid) {

        if (ruleApprovalNodeDTO.getTypeNumber() == null) {

            throw new BizException("approvalRule.createRuleApprovalNode", "typeNumber is null");
        }
        if (!RuleApprovalEnum.getNodeType().contains(RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber()))) {
            throw new BizException("approvalRule.createRuleApprovalNode", "Node typeNumber not exist");
        }
        RuleApprovalNodeDTO ruleApprovalNodeDTOResult = new RuleApprovalNodeDTO();

        switch (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber())) {
            case NODE_TYPE_APPROVAL:

            case NODE_TYPE_PRINT:
            case NODE_TYPE_EED:
                ruleApprovalNodeDTOResult = createRuleApprovalNode(ruleApprovalNodeDTO, userOid);
                break;
            case NODE_TYPE_ROBOT:
                ruleApprovalNodeDTOResult = createRobotRuleApprovalNode(ruleApprovalNodeDTO, userOid);
                break;
            case NODE_TYPE_NOTICE:
                ruleApprovalNodeDTOResult = createNoticeRuleApprovalNode(ruleApprovalNodeDTO, userOid);
        }
        this.saveLog(ruleApprovalNodeDTOResult, "新增审批节点:" + ruleApprovalNodeDTOResult.getRuleApprovalChainOid(), OperationEntityTypeEnum.APPROVAL_NODE.getKey());
        return ruleApprovalNodeDTOResult;
    }

    /**
     * 创建机器人审批节点
     * 节点类型  8001：通过；8002：驳回
     *
     * @param ruleApprovalNodeDTO
     * @param userOid
     * @return
     */
    public RuleApprovalNodeDTO createRobotRuleApprovalNode(RuleApprovalNodeDTO ruleApprovalNodeDTO, UUID userOid) {
        RuleApprovalNodeDTO ruleApprovalNodeDTOResult = new RuleApprovalNodeDTO();

        //整理机器人数据
        /*ruleApprovalNodeDTO.getRuleApprovers().forEach(approver->{
            approver.setApproverType(RuleApprovalEnum.NODE_TYPE_ROBOT.getId());
            approver.setLevel(1);
            approver.setName(APPROVER_TYPE_ROBOT_NAME);
            approver.setRuleApprovalNodeOid(ruleApprovalNodeDTO.getRuleApprovalNodeOid());
            approver.setRuleApproverOid(UUID.fromString(RuleConstants.APPROVER_TYPE_ROBOT_OID));
            approver.setStatus(1);

        });*/
        if (StringUtils.isEmpty(ruleApprovalNodeDTO.getApprovalActions())) {
            ruleApprovalNodeDTO.setApprovalActions(String.valueOf(RuleConstants.ACTION_APPROVAL_PASS));
        }
        ruleApprovalNodeDTOResult = createRuleApprovalNode(ruleApprovalNodeDTO, userOid);
        if (CollectionUtils.isEmpty(ruleApprovalNodeDTO.getRuleApprovers())) {
            List<RuleApproverDTO> list = new ArrayList<RuleApproverDTO>();
            RuleApproverDTO ruleApproverDTO = new RuleApproverDTO();
            ruleApproverDTO.setName(RuleConstants.APPROVER_TYPE_ROBOT_NAME);
            ruleApproverDTO.setRemark(RuleConstants.APPROVER_TYPE_ROBOT_NAME);
            ruleApproverDTO.setLevelNumber(1);
            ruleApproverDTO.setApproverType(RuleApprovalEnum.NODE_TYPE_ROBOT.getId());
            ruleApproverDTO.setRuleApprovalNodeOid(ruleApprovalNodeDTOResult.getRuleApprovalNodeOid());
            ruleApproverDTO.setRuleApproverOid(UUID.randomUUID());
            ruleApproverDTO.setApproverEntityOid(UUID.fromString(RuleConstants.APPROVER_TYPE_ROBOT_OID));
            ruleApproverDTO.setStatus(RuleApprovalEnum.VALID.getId());
            list.add(ruleApproverDTO);
            ruleApprovalNodeDTO.setRuleApprovers(list);
        }
        ruleApprovalNodeDTOResult.getRuleApprovers().add(createRuleApprover(ruleApprovalNodeDTO.getRuleApprovers().get(0), userOid));
        return ruleApprovalNodeDTOResult;
    }

    /**
     * 创建知会审批节点
     *
     * @param ruleApprovalNodeDTO
     * @param userOid
     * @return
     */
    public RuleApprovalNodeDTO createNoticeRuleApprovalNode(RuleApprovalNodeDTO ruleApprovalNodeDTO, UUID userOid) {
        if (ruleApprovalNodeDTO.getRuleApprovalChainOid() == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "ruleApprovalChain is null");
        }
        if (ruleApprovalNodeDTO.getTypeNumber() == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "typeNumber is null");
        }
        RuleApprovalChain ruleApprovalChain = ruleApprovalChainService.getByOid(ruleApprovalNodeDTO.getRuleApprovalChainOid());
        if (ruleApprovalChain == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "ruleApprovalChain not exist , ruleApprovalChainOid : " + ruleApprovalNodeDTO.getRuleApprovalChainOid());
        }
        if (!ruleApprovalChain.getApprovalMode().equals(ApprovalMode.CUSTOM.getId())) {
            throw new BizException("approvalRule.createRuleApprovalNode", "approvalMode error , ruleApprovalChainOid : " + ruleApprovalNodeDTO.getRuleApprovalChainOid());
        }
        Integer newSequence = RuleConstants.RULE_SEQUENCE_DEFAULT;
        List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOid(ruleApprovalNodeDTO.getRuleApprovalChainOid());
        if (!CollectionUtils.isEmpty(ruleApprovalNodes)) {
            UUID nextRuleApprovalNodeOid = ruleApprovalNodeDTO.getNextRuleApprovalNodeOid();
            /**
             * nextRuleApprovalNodeOid:后一个节点的Oid
             * 当nextRuleApprovalNodeOid为空,节点为末节点
             * 当nextRuleApprovalNodeOid不为空 ,新节点使用nextRuleApprovalNodeOid对应的sequence
             * nextRuleApprovalNodeOid和之后节点 sequence都增加
             */

            if (nextRuleApprovalNodeOid == null) {
                RuleApprovalNode lastRuleApprovalNode = ruleApprovalNodes.get(ruleApprovalNodes.size() - 1);
                newSequence = lastRuleApprovalNode.getSequenceNumber() + RuleConstants.RULE_SEQUENCE_INCREMENT;
            } else {
                //调整所有之后的sequence
                boolean found = false;
                Integer startSequence = 0;
                for (RuleApprovalNode ruleApprovalNode : ruleApprovalNodes) {
                    if (found) {
                        startSequence += RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    } else if (ruleApprovalNode.getRuleApprovalNodeOid().equals(nextRuleApprovalNodeOid)) {
                        newSequence = ruleApprovalNode.getSequenceNumber();
                        found = true;
                        startSequence = newSequence + RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    }
                }
                if (!found) {
                    throw new BizException("approvalRule.createRuleApprovalNode", "nextRuleApprovalNode not exist , nextRuleApprovalNodeOid : " + nextRuleApprovalNodeOid);
                }
            }
        }
        //序列化知会配置 该审批节点为知会节点同时知会配置不为空则进行序列化
        String notifyInfo = null;
        if (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber()).equals(RuleApprovalEnum.NODE_TYPE_NOTICE) && ruleApprovalNodeDTO.getNotifyInfo() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                //判断是否app推送，是否微信企业号消息，是否网页端消息，是否勾选姓名 ，是否勾选总金额，是否勾选单据上的【事由】
                if (ruleApprovalNodeDTO.getNotifyInfo().getWeChat() == null || ruleApprovalNodeDTO.getNotifyInfo().getWeChat() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setWeChat(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getApp() == null || ruleApprovalNodeDTO.getNotifyInfo().getApp() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setApp(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getWeb() == null || ruleApprovalNodeDTO.getNotifyInfo().getWeb() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setWeb(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getName() == null || ruleApprovalNodeDTO.getNotifyInfo().getName() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setName(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getReason() == null || ruleApprovalNodeDTO.getNotifyInfo().getReason() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setReason(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getMoney() == null || ruleApprovalNodeDTO.getNotifyInfo().getMoney() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setMoney(false);
                }

                notifyInfo = mapper.writeValueAsString(ruleApprovalNodeDTO.getNotifyInfo());
            } catch (Exception e) {
                throw new BizException("approvalRule.createRuleApprovalNode", "序列化失败");
            }
        }
        RuleApprovalNode newRuleApprovalNode = mapper.map(ruleApprovalNodeDTO, RuleApprovalNode.class);
        newRuleApprovalNode.setSequenceNumber(newSequence);
        newRuleApprovalNode.setRuleApprovalNodeOid(null);
        //设置知会配置信息
        newRuleApprovalNode.setNotifyInfo(notifyInfo);
        newRuleApprovalNode = ruleApprovalNodeService.save(newRuleApprovalNode);
        RuleApprovalNodeDTO ruleApprovalNodeDTOResult = mapper.map(newRuleApprovalNode, RuleApprovalNodeDTO.class);
        ruleApprovalNodeDTOResult.setNotifyInfo(ruleApprovalNodeDTO.getNotifyInfo());
        return ruleApprovalNodeDTOResult;
    }

    /**
     * 审批节点相关接口 ***********************
     */
    public RuleApprovalNodeDTO createRuleApprovalNode(RuleApprovalNodeDTO ruleApprovalNodeDTO, UUID userOid) {
        if (ruleApprovalNodeDTO.getRuleApprovalChainOid() == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "ruleApprovalChain is null");
        }
        if (ruleApprovalNodeDTO.getTypeNumber() == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "typeNumber is null");
        }
        RuleApprovalChain ruleApprovalChain = ruleApprovalChainService.getByOid(ruleApprovalNodeDTO.getRuleApprovalChainOid());
        if (ruleApprovalChain == null) {
            throw new BizException("approvalRule.createRuleApprovalNode", "ruleApprovalChain not exist , ruleApprovalChainOid : " + ruleApprovalNodeDTO.getRuleApprovalChainOid());
        }

        if (!ruleApprovalChain.getApprovalMode().equals(ApprovalMode.CUSTOM.getId())) {
            throw new BizException("approvalRule.createRuleApprovalNode", "approvalMode error , ruleApprovalChainOid : " + ruleApprovalNodeDTO.getRuleApprovalChainOid());
        }
        Integer newSequence = RuleConstants.RULE_SEQUENCE_DEFAULT;
        List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOid(ruleApprovalNodeDTO.getRuleApprovalChainOid());
        if (!CollectionUtils.isEmpty(ruleApprovalNodes)) {
            UUID nextRuleApprovalNodeOid = ruleApprovalNodeDTO.getNextRuleApprovalNodeOid();
            /**
             * nextRuleApprovalNodeOid:后一个节点的Oid
             * 当nextRuleApprovalNodeOid为空,节点为末节点
             * 当nextRuleApprovalNodeOid不为空 ,新节点使用nextRuleApprovalNodeOid对应的sequence
             * nextRuleApprovalNodeOid和之后节点 sequence都增加
             */

            if (nextRuleApprovalNodeOid == null) {
                RuleApprovalNode lastRuleApprovalNode = ruleApprovalNodes.get(ruleApprovalNodes.size() - 1);
                newSequence = lastRuleApprovalNode.getSequenceNumber() + RuleConstants.RULE_SEQUENCE_INCREMENT;
            } else {
                //调整所有之后的sequence
                boolean found = false;
                Integer startSequence = 0;
                for (RuleApprovalNode ruleApprovalNode : ruleApprovalNodes) {
                    if (found) {
                        startSequence += RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNode.setCreatedDate(ZonedDateTime.now());
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    } else if (ruleApprovalNode.getRuleApprovalNodeOid().equals(nextRuleApprovalNodeOid)) {
                        newSequence = ruleApprovalNode.getSequenceNumber();
                        found = true;
                        startSequence = newSequence + RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNode.setCreatedDate(ZonedDateTime.now());
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    }
                }
                if (!found) {
                    throw new BizException("approvalRule.createRuleApprovalNode", "nextRuleApprovalNode not exist , nextRuleApprovalNodeOid : " + nextRuleApprovalNodeOid);
                }
            }
        }
        RuleApprovalNode newRuleApprovalNode = mapper.map(ruleApprovalNodeDTO, RuleApprovalNode.class);
        newRuleApprovalNode.setSequenceNumber(newSequence);
        newRuleApprovalNode.setRuleApprovalNodeOid(null);
        newRuleApprovalNode.setCreatedDate(ZonedDateTime.now());
        newRuleApprovalNode.setLastUpdatedDate(ZonedDateTime.now());
        newRuleApprovalNode = ruleApprovalNodeService.save(newRuleApprovalNode);

        return mapper.map(newRuleApprovalNode, RuleApprovalNodeDTO.class);
    }

    public RuleApprovalNodeDTO getRuleApprovalNode(UUID ruleApprovalNodeOid, UUID userOid, boolean cascadeApprover, boolean cascadeCondition) {
        if (ruleApprovalNodeOid == null) {
            throw new BizException("approvalRule.getRuleApprovalNode", "ruleApprovalNodeOid is null");
        }
        RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
        if (existRuleApprovalNode == null) {
            throw new BizException("approvalRule.getRuleApprovalNode", "ruleApprovalChain not exist , ruleApprovalNodeOid : " + ruleApprovalNodeOid);
        }
        //如果 notifyInfo 不为空和改审批节点类型为知会节点做反序列化知会内容
        NotifyInfo notifyInfo = new NotifyInfo();
        if (RuleApprovalEnum.parse(existRuleApprovalNode.getTypeNumber()).equals(RuleApprovalEnum.NODE_TYPE_NOTICE) && !StringUtils.isEmpty(existRuleApprovalNode.getNotifyInfo())) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                notifyInfo = mapper.readValue(existRuleApprovalNode.getNotifyInfo(), NotifyInfo.class);
            } catch (Exception e) {
                throw new BizException("approvalRule.getRuleApprovalNode", "反序列化失败");
            }
        }

        RuleApprovalNodeDTO ruleApprovalNodeDTO = mapper.map(existRuleApprovalNode, RuleApprovalNodeDTO.class);
        consummateRuleApprovalNode(ruleApprovalNodeDTO, cascadeApprover, cascadeCondition);
        // 添加序列化知会内容
        ruleApprovalNodeDTO.setNotifyInfo(notifyInfo);
        return ruleApprovalNodeDTO;
    }

    public int deleteRuleApprovalNode(UUID ruleApprovalNodeOid, UUID userOid) {
        return deleteRuleApprovalNode(Arrays.asList(ruleApprovalNodeOid), userOid, true, true);
    }

    public int deleteRuleApprovalNode(List<UUID> ruleApprovalNodeOids, UUID userOid, boolean cascadeApprover, boolean cascadeCondition) {
        if (CollectionUtils.isEmpty(ruleApprovalNodeOids)) {
            return 0;
        }
        if (cascadeApprover) {
            List<RuleApprover> ruleApprovers = ruleApproverService.findByRuleApprovalNodeOidsIn(ruleApprovalNodeOids);
            if (!CollectionUtils.isEmpty(ruleApprovers)) {
                List<UUID> ruleApproverOids = ruleApprovers.stream().map(RuleApprover::getRuleApproverOid).collect(Collectors.toList());
                deleteRuleApprover(ruleApproverOids, userOid, cascadeCondition);
            }
        }
        return ruleApprovalNodeOids.stream().map(ruleApprovalNodeOid -> ruleApprovalNodeService.delete(ruleApprovalNodeOid)).reduce(0, Integer::sum);
    }

    public RuleApprovalNodeDTO updateRuleApprovalNode(RuleApprovalNodeDTO ruleApprovalNodeDTO, UUID userOid) {
        if (ruleApprovalNodeDTO.getRuleApprovalNodeOid() == null) {
            throw new BizException("approvalRule.updateRuleApprovalNode", "ruleApprovalNodeOid is null");
        }
        if (ruleApprovalNodeDTO.getTypeNumber() == null) {
            throw new BizException("approvalRule.updateRuleApprovalNode", "typeNumber is null");
        }
        RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeDTO.getRuleApprovalNodeOid());
        if (existRuleApprovalNode == null) {
            throw new BizException("approvalRule.updateRuleApprovalNode", "ruleApprovalChain not exist , ruleApprovalNodeOid : " + ruleApprovalNodeDTO.getRuleApprovalNodeOid());
        }
        RuleApprovalNode oldRuleApprovalNode = new RuleApprovalNode();
        RuleApprovalNode newRuleApprovalNode = new RuleApprovalNode();
        BeanUtils.copyProperties(existRuleApprovalNode, oldRuleApprovalNode);
        //TODO 公司数据隔离

        existRuleApprovalNode.setCode(ruleApprovalNodeDTO.getCode());
        existRuleApprovalNode.setApprovalActions(ruleApprovalNodeDTO.getApprovalActions());
        existRuleApprovalNode.setName(ruleApprovalNodeDTO.getName());
        existRuleApprovalNode.setRemark(ruleApprovalNodeDTO.getRemark());
        existRuleApprovalNode.setTypeNumber(ruleApprovalNodeDTO.getTypeNumber());
        existRuleApprovalNode.setNullableRule(ruleApprovalNodeDTO.getNullableRule());
        existRuleApprovalNode.setCountersignRule(ruleApprovalNodeDTO.getCountersignRule());
        existRuleApprovalNode.setRepeatRule(ruleApprovalNodeDTO.getRepeatRule());
        existRuleApprovalNode.setSelfApprovalRule(ruleApprovalNodeDTO.getSelfApprovalRule());
        existRuleApprovalNode.setPrintFlag(ruleApprovalNodeDTO.getPrintFlag());
        //将知会配置转成字节，该审批节点为知会节点，同时知会配置不为空，做序列化处理
        String notifyInfo = null;
        if (RuleApprovalEnum.parse(ruleApprovalNodeDTO.getTypeNumber()).equals(RuleApprovalEnum.NODE_TYPE_NOTICE) && ruleApprovalNodeDTO.getNotifyInfo() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                //判断是否app推送，是否微信企业号消息，是否网页端消息，是否勾选姓名 ，是否勾选总金额，是否勾选单据上的【事由】
                if (ruleApprovalNodeDTO.getNotifyInfo().getApp() == null || ruleApprovalNodeDTO.getNotifyInfo().getApp() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setApp(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getWeChat() == null || ruleApprovalNodeDTO.getNotifyInfo().getWeChat() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setWeChat(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getWeb() == null || ruleApprovalNodeDTO.getNotifyInfo().getWeb() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setWeb(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getName() == null || ruleApprovalNodeDTO.getNotifyInfo().getName() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setName(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getMoney() == null || ruleApprovalNodeDTO.getNotifyInfo().getMoney() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setMoney(false);
                }
                if (ruleApprovalNodeDTO.getNotifyInfo().getReason() == null || ruleApprovalNodeDTO.getNotifyInfo().getReason() == null) {
                    ruleApprovalNodeDTO.getNotifyInfo().setReason(false);
                }
                notifyInfo = mapper.writeValueAsString(ruleApprovalNodeDTO.getNotifyInfo());
            } catch (Exception e) {
                throw new BizException("approvalRule.updateRuleApprovalNode", "序列化失败");
            }
        }
        existRuleApprovalNode.setNotifyInfo(notifyInfo);
        existRuleApprovalNode.setComments(ruleApprovalNodeDTO.getComments());
        existRuleApprovalNode.setInvoiceAllowUpdateType(ruleApprovalNodeDTO.getInvoiceAllowUpdateType());
        //TODO 增加可修改字段时需增加
        existRuleApprovalNode = ruleApprovalNodeService.save(existRuleApprovalNode);
        BeanUtils.copyProperties(existRuleApprovalNode, newRuleApprovalNode);
        RuleApprovalNodeDTO ruleApprovalNodeDTOResult = mapper.map(existRuleApprovalNode, RuleApprovalNodeDTO.class);
        ruleApprovalNodeDTOResult.setNotifyInfo(ruleApprovalNodeDTO.getNotifyInfo());
        this.updateLog(oldRuleApprovalNode, newRuleApprovalNode, OperationEntityTypeEnum.APPROVAL_NODE.getKey());
        return ruleApprovalNodeDTOResult;
    }

    public RuleApprovalNodeDTO moveRuleApprovalNode(UUID ruleApprovalNodeOid, UUID nextRuleApprovalNodeOid, UUID userOid) {
        if (ruleApprovalNodeOid == null) {
            throw new BizException("approvalRule.moveRuleApprovalNode", "ruleApprovalNodeOid is null");
        }
        RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
        if (existRuleApprovalNode == null) {
            throw new BizException("approvalRule.moveRuleApprovalNode", "ruleApprovalChain not exist , ruleApprovalNodeOid : " + ruleApprovalNodeOid);
        }
        //TODO 公司数据隔离
        if (ruleApprovalNodeOid.equals(nextRuleApprovalNodeOid)) {
            return mapper.map(existRuleApprovalNode, RuleApprovalNodeDTO.class);
        }
        List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOid(existRuleApprovalNode.getRuleApprovalChainOid());
        if (!CollectionUtils.isEmpty(ruleApprovalNodes)) {
            Integer newSequence = RuleConstants.RULE_SEQUENCE_DEFAULT;
            if (nextRuleApprovalNodeOid == null) {
                RuleApprovalNode lastRuleApprovalNode = ruleApprovalNodes.get(ruleApprovalNodes.size() - 1);
                newSequence = lastRuleApprovalNode.getSequenceNumber() + RuleConstants.RULE_SEQUENCE_INCREMENT;
            } else {
                //调整所有之后的sequence
                boolean found = false;
                Integer startSequence = 0;

                for (RuleApprovalNode ruleApprovalNode : ruleApprovalNodes) {
                    if (ruleApprovalNode.getRuleApprovalNodeOid().equals(ruleApprovalNodeOid)) {
                        continue;
                    }
                    if (found) {
                        startSequence += RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    } else if (ruleApprovalNode.getRuleApprovalNodeOid().equals(nextRuleApprovalNodeOid)) {
                        newSequence = ruleApprovalNode.getSequenceNumber();
                        found = true;
                        startSequence = newSequence + RuleConstants.RULE_SEQUENCE_INCREMENT;
                        ruleApprovalNode.setSequenceNumber(startSequence);
                        ruleApprovalNodeService.save(ruleApprovalNode);
                    }
                }
                if (!found) {
                    throw new BizException("approvalRule.moveRuleApprovalNode", "nextRuleApprovalNode not exist , nextRuleApprovalNodeOid : " + nextRuleApprovalNodeOid);
                }
            }
            existRuleApprovalNode.setSequenceNumber(newSequence);
            existRuleApprovalNode = ruleApprovalNodeService.save(existRuleApprovalNode);
        }

        return mapper.map(existRuleApprovalNode, RuleApprovalNodeDTO.class);
    }

    /**
     * 审批者相关接口 ***********************
     */
    public RuleApproverDTO createRuleApprover(RuleApproverDTO ruleApproverDTO, UUID userOid) {
        if (ruleApproverDTO.getRuleApprovalNodeOid() == null) {
            throw new BizException("approvalRule.createRuleApprover", "ruleApprovalNodeOid is null");
        }
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApproverDTO.getRuleApprovalNodeOid());
        if (ruleApprovalNode == null) {
            throw new BizException("approvalRule.createRuleApprover", "ruleApprovalNode not exist , ruleApprovalNodeOid : " + ruleApproverDTO.getRuleApprovalNodeOid());
        }
        //TODO 公司数据隔离
        RuleApprover newRuleApprover = mapper.map(ruleApproverDTO, RuleApprover.class);
        newRuleApprover = ruleApproverService.save(newRuleApprover);
        this.saveLog(newRuleApprover, "新增审批者:" + newRuleApprover.getRuleApproverOid(), OperationEntityTypeEnum.APPROVAL_APPROVER.getKey());
        return mapper.map(newRuleApprover, RuleApproverDTO.class);
    }

    /**
     * 审批者相关接口 ***********************
     */
    public List<RuleApproverDTO> createRuleApprover(List<RuleApproverDTO> ruleApproverDTOList, UUID userOid) {
        List<RuleApproverDTO> newRuleApproverDTOList = new ArrayList<>();
        for (RuleApproverDTO ruleApproverDTO : ruleApproverDTOList) {
            newRuleApproverDTOList.add(createRuleApprover(ruleApproverDTO, userOid));
        }
        return newRuleApproverDTOList;
    }

    public RuleApproverDTO updateRuleApprover(RuleApproverDTO ruleApproverDTO, UUID userOid) {
        if (ruleApproverDTO.getRuleApproverOid() == null) {
            throw new BizException("approvalRule.updateRuleApprover", "ruleApproverOid is null");
        }
        RuleApprover existRuleApprover = ruleApproverService.getRuleApprover(ruleApproverDTO.getRuleApproverOid());
        if (existRuleApprover == null) {
            throw new BizException("approvalRule.updateRuleApprover", "ruleApprover not exist , ruleApproverOid : " + ruleApproverDTO.getRuleApproverOid());
        }
        RuleApprover oldruleApprover = new RuleApprover();
        RuleApprover newruleApprover = new RuleApprover();
        BeanUtils.copyProperties(existRuleApprover, oldruleApprover);
        //TODO 公司数据隔离
        existRuleApprover.setApproverEntityOid(ruleApproverDTO.getApproverEntityOid());
        existRuleApprover.setApproverType(ruleApproverDTO.getApproverType());
        existRuleApprover.setCode(ruleApproverDTO.getCode());
        existRuleApprover.setName(ruleApproverDTO.getName());
        existRuleApprover.setRemark(ruleApproverDTO.getRemark());
        existRuleApprover.setLevelNumber(ruleApproverDTO.getLevelNumber());
        existRuleApprover.setContainsApportionmentCostCenterManager(ruleApproverDTO.getContainsApportionmentCostCenterManager());
        existRuleApprover.setContainsApportionmentDepartmentManager(ruleApproverDTO.getContainsApportionmentDepartmentManager());
        existRuleApprover.setContainsApportionmentCostCenterPrimaryDepartmentManager(ruleApproverDTO.getContainsApportionmentCostCenterPrimaryDepartmentManager());
        //TODO 增加可修改字段时需增加
        existRuleApprover = ruleApproverService.save(existRuleApprover);
        BeanUtils.copyProperties(existRuleApprover, newruleApprover);
        this.updateLog(oldruleApprover, newruleApprover, OperationEntityTypeEnum.APPROVAL_APPROVER.getKey());
        return mapper.map(existRuleApprover, RuleApproverDTO.class);
    }

    public List<RuleApproverDTO> updateRuleApprover(List<RuleApproverDTO> ruleApproverDTOList, UUID userOid) {
        List<RuleApproverDTO> newRuleApproverDTOList = new ArrayList<>();
        for (RuleApproverDTO ruleApproverDTO : ruleApproverDTOList) {
            newRuleApproverDTOList.add(updateRuleApprover(ruleApproverDTO, userOid));
        }
        return newRuleApproverDTOList;
    }

    public RuleApproverDTO getRuleApprover(UUID ruleApproverOid, UUID userOid, boolean cascadeCondition) {
        if (ruleApproverOid == null) {
            throw new BizException("approvalRule.getRuleApprover", "ruleApproverOid is null");
        }
        //TODO 公司数据隔离
        RuleApprover existRuleApprover = ruleApproverService.getRuleApprover(ruleApproverOid);
        RuleApproverDTO ruleApproverDTO = mapper.map(existRuleApprover, RuleApproverDTO.class);
        consummateRuleApprover(ruleApproverDTO, cascadeCondition);
        return ruleApproverDTO;
    }

    public int deleteRuleApprover(UUID ruleApproverOid, UUID userOid) {
        if (ruleApproverOid == null) {
            return 0;
        }
        return deleteRuleApprover(Arrays.asList(ruleApproverOid), userOid, true);
    }

    public int deleteRuleApprover(List<UUID> ruleApproverOids, UUID userOid, boolean cascadeCondition) {
        if (CollectionUtils.isEmpty(ruleApproverOids)) {
            return 0;
        }
        //TODO 公司数据隔离
        if (cascadeCondition) {
            List<RuleConditionRelation> ruleConditionRelations = ruleConditionRelationService.findEntityOidIn(ruleApproverOids);
            if (!CollectionUtils.isEmpty(ruleConditionRelations)) {
                List<UUID> ruleConditionOids = ruleConditionRelations.stream().map(RuleConditionRelation::getRuleConditionOid).collect(Collectors.toList());
                ruleConditionRelationService.deleteByEntityOid(ruleApproverOids);
                ruleConditionService.deleteRuleCondition(ruleConditionOids, userOid);
            }
        }
        return ruleApproverService.delete(ruleApproverOids);
    }





    /**
     * 表单
     */
    public List<ApprovalFormDTO> getAllCustomForm(UUID userOid, boolean cascadeApprovalChain) {
        List<ApprovalFormDTO> approvalFormDTOS = null;
        try {
            approvalFormDTOS = approvalFormService.listDTO();
            approvalFormDTOS = approvalFormDTOS.stream().filter(v -> !RuleConstants.excludeFormTypes.contains(v.getFormType())).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        consummateCustomForm(approvalFormDTOS, cascadeApprovalChain);
        return approvalFormDTOS;
    }

    public RuleService() {
    }


    /**
     * consumate转换相关接口 ***********************
     */
    public void consummateCustomForm(List<ApprovalFormDTO> approvalFormDTOS, boolean cascadeApprovalChain) {

        if (!cascadeApprovalChain || CollectionUtils.isEmpty(approvalFormDTOS)) {
            return;
        }
        List<UUID> customFormOids = approvalFormDTOS.stream().map(ApprovalFormDTO::getFormOid).collect(Collectors.toList());
        List<RuleCondition> ruleConditions = ruleConditionService.getFormRuleCondition(customFormOids);
        if (CollectionUtils.isEmpty(ruleConditions)) {
            return;
        }
        List<UUID> ruleConditionOids = ruleConditions.stream().map(RuleCondition::getRuleConditionOid).collect(Collectors.toList());
        Map<UUID, UUID> conditionOidFormOidMap = ruleConditions.stream().collect(Collectors.toMap(p -> p.getRuleConditionOid(), p -> UUID.fromString(p.getRuleValue())));
        List<RuleConditionRelation> ruleConditionRelations = ruleConditionRelationService.findByRuleConditionOidsAndEntityType(ruleConditionOids, RuleApprovalEnum.CONDITION_RELATION_TYPE_SCENE.getId());
        if (CollectionUtils.isEmpty(ruleConditionRelations)) {
            return;
        }
        List<UUID> ruleConditionRelationEntityOids = ruleConditionRelations.stream().map(RuleConditionRelation::getEntityOid).collect(Collectors.toList());
        Map<UUID, UUID> entityOidConditionOidMap = ruleConditionRelations.stream().collect(Collectors.toMap(p -> p.getEntityOid(), p -> p.getRuleConditionOid()));
        List<RuleApprovalChain> ruleApprovalChains = ruleApprovalChainService.getByRuleSceneOids(ruleConditionRelationEntityOids);
        if (CollectionUtils.isEmpty(ruleApprovalChains)) {
            return;
        }
        List<RuleApprovalChainDTO> ruleApprovalChainDTOs = mapper.mapAsList(ruleApprovalChains, RuleApprovalChainDTO.class);
        consummateRuleApprovalChain(ruleApprovalChainDTOs, true, false, false);
        Map<UUID, RuleApprovalChainDTO> formOidApprovaChainMap = ruleApprovalChainDTOs.stream().collect(
                Collectors.toMap(
                        p -> conditionOidFormOidMap.get(entityOidConditionOidMap.get(p.getRuleSceneOid())),
                        p -> p)
        );

        approvalFormDTOS.stream().forEach(customFormDTO -> {
            customFormDTO.setRuleApprovalChain(formOidApprovaChainMap.get(customFormDTO.getFormOid()));
        });
//        customFormI18nMapping(approvalFormDTOS);
    }

    public void consummateRuleApprovalChain(RuleApprovalChainDTO ruleApprovalChainDTO, boolean cascadeApprovalNode, boolean cascadeApprover, boolean cascadeCondition) {
        if (ruleApprovalChainDTO == null) {
            return;
        }
        consummateRuleApprovalChain(Arrays.asList(ruleApprovalChainDTO), cascadeApprovalNode, cascadeApprover, cascadeCondition);
    }

    public void consummateRuleApprovalChain(List<RuleApprovalChainDTO> ruleApprovalChainDTOs, boolean cascadeApprovalNode, boolean cascadeApprover, boolean cascadeCondition) {
        if (!cascadeApprovalNode || CollectionUtils.isEmpty(ruleApprovalChainDTOs)) {
            return;
        }
        List<UUID> ruleApprovalChainOids = ruleApprovalChainDTOs.stream().filter(r -> r.getApprovalMode().equals(ApprovalMode.CUSTOM.getId())).map(RuleApprovalChainDTO::getRuleApprovalChainOid).collect(Collectors.toList());

        List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOidsIn(ruleApprovalChainOids);
        if (CollectionUtils.isEmpty(ruleApprovalNodes)) {

            return;
        }
        Map<UUID, List<RuleApprovalNode>> ruleApprovalNodeMap = ruleApprovalNodes.stream().collect(Collectors.groupingBy(RuleApprovalNode::getRuleApprovalChainOid));
        ruleApprovalChainDTOs.stream().forEach(ruleApprovalChainDTO -> {
            List<RuleApprovalNode> ruleApprovalNodes1 = ruleApprovalNodeMap.get(ruleApprovalChainDTO.getRuleApprovalChainOid());
            if (!CollectionUtils.isEmpty(ruleApprovalNodes1)) {
                List<RuleApprovalNodeDTO> ruleApprovalNodeDTOs = new ArrayList<>();
                ruleApprovalNodes1.stream().forEach(existRuleApprovalNode ->
                {// 如果该审批节点为知会节点同时，知会配置不为空，做反序列化处理
                    NotifyInfo notifyInfo = null;
                    if (RuleApprovalEnum.NODE_TYPE_NOTICE.equals(RuleApprovalEnum.parse(existRuleApprovalNode.getTypeNumber())) && !StringUtils.isEmpty(existRuleApprovalNode.getNotifyInfo())) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            notifyInfo = mapper.readValue(existRuleApprovalNode.getNotifyInfo(), NotifyInfo.class);
                        } catch (Exception e) {
                            throw new BizException("approvalRule.getRuleApprovalNode", "反序列化失败"+e.getMessage());
                        }
                    }
                    RuleApprovalNodeDTO ruleApprovalNodeDTOResult = mapper.map(existRuleApprovalNode, RuleApprovalNodeDTO.class);
                    ruleApprovalNodeDTOResult.setNotifyInfo(notifyInfo);
                    ruleApprovalNodeDTOs.add(ruleApprovalNodeDTOResult);
                });

                if (cascadeApprover) {
                    consummateRuleApprovalNode(ruleApprovalNodeDTOs, cascadeApprover, cascadeCondition);
                }
                ruleApprovalChainDTO.setRuleApprovalNodes(ruleApprovalNodeDTOs);
            }
        });


    }

    public void consummateRuleApprovalNode(RuleApprovalNodeDTO ruleApprovalNodeDTO, boolean cascadeApprover, boolean cascadeCondition) {
        if (ruleApprovalNodeDTO == null) {
            return;
        }
        consummateRuleApprovalNode(Arrays.asList(ruleApprovalNodeDTO), cascadeApprover, cascadeCondition);
    }

    public void consummateRuleApprovalNode(List<RuleApprovalNodeDTO> ruleApprovalNodeDTOs, boolean cascadeApprover, boolean cascadeCondition) {
        if (!cascadeApprover || CollectionUtils.isEmpty(ruleApprovalNodeDTOs)) {
            return;
        }
        List<UUID> ruleApprovalNodeOids = ruleApprovalNodeDTOs.stream().map(RuleApprovalNodeDTO::getRuleApprovalNodeOid).collect(Collectors.toList());
        List<RuleApprover> ruleApprovers = ruleApproverService.findByRuleApprovalNodeOidsIn(ruleApprovalNodeOids);
        if (CollectionUtils.isEmpty(ruleApprovers)) {
            return;
        }
        Map<UUID, List<RuleApprover>> ruleApproverMap = ruleApprovers.stream().collect(Collectors.groupingBy(RuleApprover::getRuleApprovalNodeOid));
        ruleApprovalNodeDTOs.stream().forEach(ruleApprovalNodeDTO -> {
            List<RuleApprover> ruleApprover1 = ruleApproverMap.get(ruleApprovalNodeDTO.getRuleApprovalNodeOid());
            if (!CollectionUtils.isEmpty(ruleApprover1)) {
                List<RuleApproverDTO> ruleApproverDTOs = mapper.mapAsList(ruleApprover1, RuleApproverDTO.class);
                if (cascadeCondition) {
                    consummateRuleApprover(ruleApproverDTOs, cascadeCondition);
                }
                ruleApprovalNodeDTO.setRuleApprovers(ruleApproverDTOs);
            }
        });
    }

    public void consummateRuleApprover(RuleApproverDTO ruleApproverDTO, boolean cascadeCondition) {
        if (ruleApproverDTO == null) {
            return;
        }
        consummateRuleApprover(Arrays.asList(ruleApproverDTO), cascadeCondition);
    }

    public void consummateRuleApprover(List<RuleApproverDTO> ruleApproverDTOs, boolean cascadeCondition) {
        if (!cascadeCondition || CollectionUtils.isEmpty(ruleApproverDTOs)) {
            return;
        }
        List<UUID> ruleApproverOids = ruleApproverDTOs.stream().map(RuleApproverDTO::getRuleApproverOid).collect(Collectors.toList());
        List<RuleConditionRelation> ruleConditionRelations = ruleConditionRelationService.findEntityOidIn(ruleApproverOids);
        if (CollectionUtils.isEmpty(ruleConditionRelations)) {
            return;
        }
        List<UUID> ruleConditionOids = ruleConditionRelations.stream().map(RuleConditionRelation::getRuleConditionOid).collect(Collectors.toList());
        Map<UUID, List<RuleConditionRelation>> ruleConditionRelationMap = ruleConditionRelations.stream().collect(Collectors.groupingBy(RuleConditionRelation::getEntityOid));

        List<RuleCondition> ruleConditions = ruleConditionService.findByRuleConditionOidIn(ruleConditionOids);
        Map<UUID, RuleCondition> ruleConditionOidMap = ruleConditions.stream().collect(Collectors.toMap((k) -> k.getRuleConditionOid(), (v) -> v));

        Map<UUID, List<RuleCondition>> dataMap = new HashMap<>();
        ruleConditionRelationMap.forEach((ruleApproverOid, ruleConditionRelationList) -> {
            dataMap.put(ruleApproverOid,
                    ruleConditionRelationList.stream()
                            .filter(v -> ruleConditionOidMap.containsKey(v.getRuleConditionOid()))
                            .map(v -> ruleConditionOidMap.get(v.getRuleConditionOid()))
                            .collect(Collectors.toList())
            );
        });

        ruleApproverDTOs.stream().forEach(ruleApproverDTO -> {
            List<RuleCondition> ruleConditionList = dataMap.get(ruleApproverDTO.getRuleApproverOid());
            if (!CollectionUtils.isEmpty(ruleConditionList)) {
                //List<RuleConditionDTO> ruleConditionDTOs = ConvertMapper.convertList(ruleCondition1, RuleConditionDTO.class);
                List<RuleConditionDTO> ruleConditionDTOs = new ArrayList<>();
                for (RuleCondition ruleCondition : ruleConditionList) {
                    RuleConditionDTO ruleConditionDTO =ruleConditionService.toDTO(ruleCondition);

                    ruleConditionDTOs.add(ruleConditionDTO);
                }
                ruleApproverDTO.setRuleConditionList(ruleConditionDTOs);
                ruleApproverDTO.setRuleConditions(ruleConditionDTOs.stream().collect(Collectors.groupingBy(RuleConditionDTO::getBatchCode)));
            }
        });
    }

    public RuleNextApproverResult getNextApprovalNode(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        try {
            //验证参数
            if (droolsRuleApprovalNodeDTO == null
                    || CollectionUtils.isEmpty(droolsRuleApprovalNodeDTO.getFormValues())
                    || droolsRuleApprovalNodeDTO.getFormOid() == null
                    || droolsRuleApprovalNodeDTO.getApplicantOid() == null
            ) {
                //throw new ValidationException(new ValidationError("getNextApprovalNode", "CustFormValues is null"));
                log.error("invoke getNextApprovalNode param error , code : {} , msg : {} ", RespCode.SYS_APPROVAL_CHAIN_GET_ERROR);
                return RuleNextApproverResult.builder()
                        .returnCode(RespCode.SYS_APPROVAL_CHAIN_GET_ERROR)
                        .returnMsg("param is null ")
                        .build();
            }
            if (droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid() == null) {
                //获取第一个审批者
                return getFirstApprovalNode(droolsRuleApprovalNodeDTO);
            } else {
                //获取下一个审批者
                RuleNextApproverResult result = RuleNextApproverResult.builder().approvalMode(ApprovalMode.CUSTOM.getId()).build();
                RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
                if (ruleApprovalNode == null) {
                    return getFirstApprovalNode(droolsRuleApprovalNodeDTO);
                }
                //获取下一个节点
                RuleApprovalNode nextRuleApprovalNode = ruleApprovalNodeService.getNextByRuleApprovalChainOid(ruleApprovalNode.getRuleApprovalChainOid(), ruleApprovalNode.getSequenceNumber());
                if (nextRuleApprovalNode != null) {
                    droolsRuleApprovalNodeDTO.setRuleApprovalNodeOid(nextRuleApprovalNode.getRuleApprovalNodeOid());
                    //计算符合的审批者
                    RuleApprovalNodeDTO droolsrResult = droolsService.invokeDroolsRuleForApprovalNode(droolsRuleApprovalNodeDTO, droolsRuleApprovalNodeDTO.getApplicantOid());
                    RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
                    //将包含提交人审批规则放入RuleApprovalNode
                    droolsrResult.setSelfApprovalRule(existRuleApprovalNode.getSelfApprovalRule());
                    result.setDroolsApprovalNode(droolsrResult);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("invoke getNextApprovalNode error , code : {} , msg : {}, {} ", RespCode.SYS_APPROVAL_CHAIN_GET_ERROR, e.getMessage(), e);
            return RuleNextApproverResult.builder()
                    .returnCode(RespCode.SYS_APPROVAL_CHAIN_GET_ERROR)
                    .returnMsg(e.getMessage())
                    .build();
        }
    }

    /**
     * 计算第一个审批者
     *
     * @param droolsRuleApprovalNodeDTO
     * @return
     */
    private RuleNextApproverResult getFirstApprovalNode(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        RuleApprovalChainDTO ruleApprovalChainDTO = getApprovalChainByFormOid(droolsRuleApprovalNodeDTO.getFormOid(), droolsRuleApprovalNodeDTO.getApplicantOid(), false, false, false);
        /*if (ruleApprovalChainDTO == null) {
            throw new ValidationException(new ValidationError("getNextApprovalNode", "ruleApprovalChain not exist"));
        }*/
        RuleNextApproverResult result = RuleNextApproverResult.builder()
                .approvalMode(ruleApprovalChainDTO.getApprovalMode())
                .build();

        //兼容选人和部门审批
        if (ApprovalMode.parse(ruleApprovalChainDTO.getApprovalMode()) == ApprovalMode.USER_PICK) {

        } else if (ApprovalMode.parse(ruleApprovalChainDTO.getApprovalMode()) == ApprovalMode.DEPARTMENT) {
            result.setLevel(ruleApprovalChainDTO.getLevel());
        } else if (ApprovalMode.parse(ruleApprovalChainDTO.getApprovalMode()) == ApprovalMode.CUSTOM) {
            RuleApprovalNode nextRuleApprovalNode = ruleApprovalNodeService.getNextByRuleApprovalChainOid(ruleApprovalChainDTO.getRuleApprovalChainOid(), 0);
            if (nextRuleApprovalNode == null) {
                return null;
            }
            droolsRuleApprovalNodeDTO.setRuleApprovalNodeOid(nextRuleApprovalNode.getRuleApprovalNodeOid());
            //计算符合的审批者
            RuleApprovalNodeDTO droolsrResult = droolsService.invokeDroolsRuleForApprovalNode(droolsRuleApprovalNodeDTO, droolsRuleApprovalNodeDTO.getApplicantOid());

            // 调试暂时去掉开始
            log.info("droolsrResult：{}", droolsrResult);
            // 结束
            RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
            //将包含提交人审批规则放入RuleApprovalNode
            droolsrResult.setSelfApprovalRule(existRuleApprovalNode.getSelfApprovalRule());
            result.setDroolsApprovalNode(droolsrResult);
        }
        return result;
    }

    /*private List<UUID> getRuleApproverUserOids(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO ){
        RuleApproverUserOidsDTO ruleApproverUserOidsDTO = userService.getRuleApproverUserOids(droolsRuleApprovalNodeDTO);
        List<UUID> approvers=ruleApproverUserOidsDTO.getRuleApproverUserOids();
        if(approvers!=null) {
            approvers.remove(null);
        }
        //审批人包含自己
        if(ruleApproverUserOidsDTO!=null
            && !CollectionUtils.isEmpty(approvers)
            && approvers.contains(droolsRuleApprovalNodeDTO.getApplicantOid())){
                RuleApprovalNode existRuleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
                switch (RuleApprovalEnum.parse(existRuleApprovalNode.getSelfApprovalRule())) {
                    case RULE_SELFAPPROVAL_SKIP:
                        approvers.remove(droolsRuleApprovalNodeDTO.getApplicantOid());
                        break;
                    case RULE_SELFAPPROVAL_NOT_SKIP:
                        break;
                    case RULE_SELFAPPROVAL_SUPERIOR_MANAGER:
                        UUID managerOid=userService.getUserDepartmentManager(droolsRuleApprovalNodeDTO.getApplicantOid());
                        if(managerOid==null){
                            log.info("SELFAPPROVAL ,replace approver error ,applicantOid : {} ",droolsRuleApprovalNodeDTO.getApplicantOid());
                            throw new RuntimeException(RespCode.RES_2003);
                        }
                        approvers.set(approvers.indexOf(droolsRuleApprovalNodeDTO.getApplicantOid()),managerOid);
                        log.info("SELFAPPROVAL ,replace approver from {} to {} ",droolsRuleApprovalNodeDTO.getApplicantOid(),managerOid);
                        break;
                    case RULE_SELFAPPROVAL_CHARGE_MANAGER:
                        //TODO

                        break;
                }

            }
        if(approvers!=null) {
            approvers.remove(null);
        }
        return approvers;
    }*/


    /**
     * 初始化公司表单
     *
     */
    public void initRule(Integer approvalType) {

        List<RuleApprovalChain> existRuleApprovalChains = ruleApprovalChainService.listAll();
        if (!CollectionUtils.isEmpty(existRuleApprovalChains)) {
            return;
        }

        List<ApprovalFormDTO> approvalFormDTOS = null;
        try {
            approvalFormDTOS = approvalFormService.listDTO();
            for (ApprovalFormDTO formDTO : approvalFormDTOS) {
                RuleApprovalChainDTO ruleApprovalChainDTO = RuleApprovalChainDTO.builder()
                        .formOid(formDTO.getFormOid())
                        .approvalMode(approvalType)
                        .build();
                this.createRuleApprovalChain(ruleApprovalChainDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 申请人所在的组织架构和单据上的组织架构 查询可选择的角色
     */
    //@Cacheable(key="#a0.toString()",cacheNames = CacheNames.BRMS_ARTEMIS_APPROVAL_ROLE)
    public Map<RuleEnumDTO, List<RuleEnumDTO>> getRuleApprovalRole(UUID companyOid) {
        //审批类型
        Map<RuleEnumDTO, List<RuleEnumDTO>> approvalTypes = new HashMap<>();
        List<DepartmentPositionCO> departmentPositionDTOs = baseClient.listDepartmentPosition(companyOid);
        //先增加申请人直属领导
        if (LanguageEnum.EN_US.getKey().equals(OrgInformationUtil.getCurrentLanguage())) {
            approvalTypes.put(new RuleEnumDTO(0, "直属领导", ""),
                    new ArrayList() {{
                        add(RuleConstants.directManagerEnumDTOEnglish);
                    }});
        } else {
            approvalTypes.put(new RuleEnumDTO(0, "直属领导", ""),
                    new ArrayList() {{
                        add(RuleConstants.directManagerEnumDTO);
                    }});
        }
        if (!CollectionUtils.isEmpty(departmentPositionDTOs)) {
            approvalTypes.put(new RuleEnumDTO(0, "按申请人所在的组织架构审批", ""), new ArrayList() {{
                        departmentPositionDTOs.stream().forEach(departmentPositionDTO -> {
                            add(new RuleEnumDTO(Integer.valueOf(departmentPositionDTO.getPositionCode()), departmentPositionDTO.getPositionName(), ""));
                        });
                    }}
            );
            approvalTypes.put(new RuleEnumDTO(0, "按单据上的组织架构审批", ""),
                    new ArrayList() {{
                        departmentPositionDTOs.stream().forEach(departmentPositionDTO -> {
                            add(new RuleEnumDTO(Integer.valueOf(departmentPositionDTO.getPositionCode()), departmentPositionDTO.getPositionName(), ""));
                        });
                    }});
        }
        approvalTypes.put(new RuleEnumDTO(0, "按单据上的成本中心主管审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(RuleConstants.APPROVAL_TYPE_COST_CENTER_MANAGER, "按单据上的成本中心主管审批", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "按单据上的成本中心的主要部门经理审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(RuleConstants.APPROVAL_TYPE_COST_CENTER_PRIMARY_DEPARTMENT_MANAGER, "按单据上的成本中心的主要部门经理审批", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "指定人员审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(RuleConstants.APPROVAL_TYPE_USER, "指定人员审批", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "指定人员组审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(RuleConstants.APPROVAL_TYPE_USERGROUP, "指定人员组审批", ""));
                }});
        return approvalTypes;
    }

    @Cacheable(key = "#a0", cacheNames = CacheNames.BRMS_CONSTANTS_RULE_APPROVAL_MODE)
    public List<RuleEnumDTO> getApprovalModesByLanguage(String language) {
        List<RuleEnumDTO> approvalModes = RuleConstants.approvalModes;
        if (Locale.ENGLISH.getLanguage().equalsIgnoreCase(language)) {
            for (RuleEnumDTO mode : approvalModes) {
                Integer key = mode.getKey();
                if (ApprovalMode.DEPARTMENT.getId().equals(key)) {
                    mode.setValue("Department Manager");
                    mode.setRemark("Approval by applicant department manager");
                }
                if (ApprovalMode.USER_PICK.getId().equals(key)) {
                    mode.setValue("Specified approver");
                    mode.setRemark("Approval by approver specified by applicant");
                }
                if (ApprovalMode.CUSTOM.getId().equals(key)) {
                    mode.setValue("Customized approver");
                    mode.setRemark("Diversify your own workflow");
                }
                if (ApprovalMode.YING_FU_USER_PICK.getId().equals(key)) {
                    mode.setValue("Yingfu specified approver");
                    mode.setRemark("Approval by approver specified by applicant");
                }
            }
        } else {
            approvalModes.clear();
            approvalModes.add(new RuleEnumDTO(ApprovalMode.DEPARTMENT.getId(), "部门经理审批", "由提交人所在部门的所有领导审批"));
            approvalModes.add(new RuleEnumDTO(ApprovalMode.USER_PICK.getId(), "选人审批", "由提交人选择人员依次进行审批"));
            approvalModes.add(new RuleEnumDTO(ApprovalMode.CUSTOM.getId(), "自定义审批", "可配置多样的审批流"));
            approvalModes.add(new RuleEnumDTO(ApprovalMode.YING_FU_USER_PICK.getId(), "英孚选人审批", "由提交人选择人员依次进行审批"));
        }
        return approvalModes;
    }

    /**
     * 表单
     */
    public List<ApprovalFormDTO> listAllForm(boolean cascadeApprovalChain, String fromType, String roleType, String formName, Long formTypeId) {
        List<ApprovalFormDTO> approvalFormDTOS = new ArrayList<>();
        try {
            if ("TENANT".equals(roleType) ) {
                approvalFormDTOS = approvalFormService.listTenantForms(LoginInformationUtil.getCurrentTenantId(),formTypeId,formName);
            } else {
                approvalFormDTOS = approvalFormService.listDTO();
                if (String.valueOf(ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId()).equals(fromType)) {
                    approvalFormDTOS = approvalFormDTOS.stream().filter(v -> ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId().equals(v.getFromType())).collect(Collectors.toList());
                } else {
                    approvalFormDTOS = approvalFormDTOS.stream().filter(v -> ApprovalFormEnum.CUSTOMER_FROM_COMPANY.getId().equals(v.getFromType())).collect(Collectors.toList());
                }
            }
            approvalFormDTOS = approvalFormDTOS.stream().filter(v -> !RuleConstants.excludeFormTypes.contains(v.getFormType())).collect(Collectors.toList());
            consummateCustomForm(approvalFormDTOS, cascadeApprovalChain);
        } catch (Exception e) {
            log.error("getAllCustomFormByTenant---------error:", e);
        }
        return approvalFormDTOS;
    }

    /**
     * 尚未初始化审批链的表单
     *
     * @param cascadeApprovalChain
     * @return
     */
    public List<ApprovalFormDTO> getAllUnInitialCustomForm(boolean cascadeApprovalChain) {
        List<ApprovalFormDTO> approvalFormDTOS = null;
        try {
            approvalFormDTOS = approvalFormService.listDTO();
            approvalFormDTOS = approvalFormDTOS.stream().filter(v -> !RuleConstants.excludeFormTypes.contains(v.getFormType())).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        consummateCustomForm(approvalFormDTOS, cascadeApprovalChain);
        approvalFormDTOS = approvalFormDTOS.stream().filter(c -> c.getRuleApprovalChain() == null).collect(Collectors.toList());
        return approvalFormDTOS;
    }


    //@Cacheable(keyGenerator = "wiselyKeyGenerator",cacheNames = CacheNames.BRMS_ARTEMIS_CUSTOMFORM_FIELD)
    public Map<Integer, List<FormFieldDTO>> getCustomFormField(UUID formOid, UUID userOid, String roleType) {
        ApprovalFormDTO approvalFormDTO = approvalFormService.getCustomFormDetailForRule(formOid);
        if (approvalFormDTO == null) {
            return null;
        }
        List<FormFieldDTO> formFieldDTOs = approvalFormDTO.getFormFieldList();
        List<FormFieldDTO> defaultCustomFieldTypeList = new ArrayList<>();
        List<FormFieldDTO> controlCustomFieldTypeList = new ArrayList<>();
        if (formFieldDTOs == null) {
            formFieldDTOs = new ArrayList<>();
        }
        /**
         * 过滤表单字段
         * 1.默认字段
         * 2.自定义字段
         * 3.管控字段
         */
        Map<Integer, List<FormFieldDTO>> result = formFieldDTOs.stream()
                .filter(p -> RuleConstants.showFieldKeys.contains(p.getMessageKey())
                        && RuleConstants.fieldTypeGroups.containsKey(p.getFieldType().getId())
                        && p.getFieldOid() != null)
                .map(p -> {
                    p.setFieldType(FieldType.parse(RuleConstants.fieldTypeGroups.get(p.getFieldType().getId())));
                    return p;
                })
                .collect(Collectors.groupingBy(p -> RuleConstants.fieldTypeGroups.get(p.getFieldType().getId())));

        if (!CollectionUtils.isEmpty(RuleConstants.defaultFieldKeys)) {
            defaultCustomFieldTypeList.addAll(
                    formFieldDTOs.stream().filter(p -> RuleConstants.defaultFieldKeys.contains(p.getMessageKey())).collect(Collectors.toList())
            );
        }
        if (!CollectionUtils.isEmpty(RuleConstants.controlFieldKeys)) {
            controlCustomFieldTypeList.addAll(
                    formFieldDTOs.stream().filter(p -> RuleConstants.controlFieldKeys.contains(p.getMessageKey())).collect(Collectors.toList())
            );
        }


        result.put(RuleConstants.CUSTOM_FILED_TYPE_ID_DEFAULT, defaultCustomFieldTypeList);
        result.put(RuleConstants.CUSTOM_FILED_TYPE_ID_CONTROL, controlCustomFieldTypeList);

       /* result.put(RuleConstants.CUSTOM_FILED_TYPE_ID_COST_CENTER_CUSTOMED_LIST, costCenterCustomFieldTypeList);
        result.put(RuleConstants.APPLICATION_COST_CENTER_CUSTOMED_LIST, applicationCostCenterCustomList);*/
        return result;
    }


    /**
     * 初始化表单审批链和增加结束节点
     *
     * @param formOids
     */
    public void additionalOperation(List<UUID> formOids) {
        formOids.stream().forEach(f -> {
            RuleApprovalChainDTO ruleApprovalChainDTO = createRuleApprovalChain(RuleApprovalChainDTO.builder().approvalMode(ApprovalMode.CUSTOM.getId()).formOid(f).checkData(Boolean.FALSE).build());
            createRuleApprovalNodeMapping(RuleApprovalNodeDTO.builder().printFlag(Boolean.TRUE).name("结束").remark("结束").typeNumber(RuleApprovalEnum.NODE_TYPE_EED.getId()).ruleApprovalChainOid(ruleApprovalChainDTO.getRuleApprovalChainOid()).build(), OrgInformationUtil.getCurrentUserOid());
            approvalFormService.synchronizeApprovalMode(f, ApprovalMode.CUSTOM.getId(), OrgInformationUtil.getCurrentTenantId());
        });
    }


    public CustomFormApprovalModeDTO getCustomFormApproverMode() {
        CustomFormApprovalModeDTO customFormApprovalModeDTO = new CustomFormApprovalModeDTO();
        customFormApprovalModeDTO.setApprovalFormOidAndApprovalModeDTOList(approvalFormApprovalModeMapper.getCustomFormApproverMode());
        return customFormApprovalModeDTO;
    }



    public RuleApproverUserOidsDTO getRuleApproverUserOIDs(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        Map<String,Set<UUID>> ruleApproverMap = new HashMap<>();
        RuleApproverUserOidsDTO ruleApproverUserOIDsDTO = new RuleApproverUserOidsDTO();
        try {
            ruleApproverMap = workFlowApprovalService.getRuleApproverUserOIDs(droolsRuleApprovalNodeDTO);
        } catch (final HttpStatusCodeException e) {
            log.error(e.getStatusCode().toString());
            log.error(e.getResponseBodyAsString());
        }
        ruleApproverUserOIDsDTO.setRuleApproverMap(ruleApproverMap);
        return ruleApproverUserOIDsDTO;
    }

    /**
     * 新增日志
     *
     * @param currentObj
     * @param name
     * @param operationEntityType
     */
    public void saveLog(Object currentObj, String name, String operationEntityType) {
//        try {
//            dataOperationService.save(OrgInformationUtil.getCurrentUserOid(),currentObj, name , operationEntityType, OperationTypeEnum.ADD.getKey(), Long.parseLong(OrgInformationUtil.getTenantId()));
//        } catch (Exception e) {
//            log.error("saveLog,error:",e);
//        }
    }

    /**
     * 保存更新日志
     *
     * @param oldObj
     * @param newObj
     * @param operationEntityType
     */
    public void updateLog(Object oldObj, Object newObj, String operationEntityType) {
        /*try {
            dataOperationService.save(OrgInformationUtil.getCurrentUserOid(), oldObj, newObj, operationEntityType, OperationTypeEnum.UPDATE.getKey(), Long.parseLong(OrgInformationUtil.getTenantId()), "");
        } catch (Exception e) {
            log.error("updateLog,error:",e);
        }*/
    }
}
