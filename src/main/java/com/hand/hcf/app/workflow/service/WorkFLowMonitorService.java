package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowMoveNodeAction;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowMainService;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.brms.service.RuleConditionService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.constant.LocaleMessageConstants;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.chain.UserApprovalDTO;
import com.hand.hcf.app.workflow.dto.monitor.MonitorNode;
import com.hand.hcf.app.workflow.dto.monitor.MonitorReturnNodeDTO;
import com.hand.hcf.app.workflow.dto.monitor.WorkFlowMonitorDTO;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.persistence.WorkFlowDocumentRefMapper;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkFLowMonitorService extends BaseService<WorkFlowDocumentRefMapper, WorkFlowDocumentRef> {

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private WorkFlowDocumentRefMapper workFlowDocumentRefMapper;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;


    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleConditionService ruleConditionService;
    /**
     *
     * @version 1.0
     * @author ly
     * @date 2019/04/28
     *
     * @param booksID 账套ID
     * @param documentCategory 单据大类
     * @param createdBy 创建人
     * @param status    单据状态
     * @param startDate 申请日期从
     * @param endDate   申请日期到
     * @param lastApproverOid  当前审批人oid
     * @param formName  审批流名称
     * @param mybaitsPage
     * @return
     */
    public List<WorkFlowMonitorDTO> pageWorkflowMonitorByCond(Long booksID,
                                                              Integer documentCategory,
                                                              Long createdBy,
                                                              Integer status,
                                                              String documentNumber,
                                                              ZonedDateTime startDate,
                                                              ZonedDateTime endDate,
                                                              UUID   lastApproverOid,
                                                              String formName,
                                                              Page mybaitsPage){
        Wrapper  wrapper = new EntityWrapper<WorkFlowDocumentRef>();
        if(formName != null){
            formName = '%' + formName + '%';
        }

        List<WorkFlowDocumentRef> workFlowDocumentRefs = workFlowDocumentRefMapper.pageWorkflowMonitorByCond(booksID, documentCategory, createdBy, status, documentNumber, startDate, endDate, lastApproverOid, formName, mybaitsPage);
        return toDTO(workFlowDocumentRefs);
    }
    /**
     *  转换封装数据 DTO处理类
     * @param
     * @return
     */
    public List<WorkFlowMonitorDTO> toDTO(List<WorkFlowDocumentRef> workFlowDocumentRefsList) {
        List<WorkFlowMonitorDTO> workflowWorkFlowDocumentRefDTOS = mapper.mapAsList(workFlowDocumentRefsList,WorkFlowMonitorDTO.class);
        workflowWorkFlowDocumentRefDTOS.stream().forEach(workFlowMonitorDTO ->{
            //设置审批流名称
            UUID formOid = workFlowMonitorDTO.getFormOid();
            if(formOid != null){
                ApprovalForm approvalForm = approvalFormService.getByOid(formOid);
                if(approvalForm != null){
                    workFlowMonitorDTO.setFormName(approvalForm.getFormName());
                }
            }
            //单据大类
            Integer documentCategory = workFlowMonitorDTO.getDocumentCategory();

            if(documentCategory != null ){
                SysCodeValueCO sysCodeValueCO = baseClient.getSysCodeValueByCodeAndValue("SYS_APPROVAL_FORM_TYPE",String.valueOf(documentCategory));
                if(sysCodeValueCO != null){
                    workFlowMonitorDTO.setDocumentCategoryName(sysCodeValueCO.getName());
                }
            }
            //审批人
            UUID documentOid = workFlowMonitorDTO.getDocumentOid();

            if(documentCategory!= null && documentOid != null){
                List<ApprovalChain> approvalChainList = approvalChainService.listCurrrentByEntityTypeAndEntityOid(documentCategory, documentOid);

                if(approvalChainList != null && approvalChainList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (ApprovalChain approvalChain : approvalChainList) {
                        UserApprovalDTO userDTO = baseClient.getUserByUserOid(approvalChain.getApproverOid());
                        if (userDTO != null) {
                            sb.append(userDTO.getFullName() + ",");
                        }
                    }
                    workFlowMonitorDTO.setApproverName(sb.toString());
                }
            }
            //最后审批人
            UUID lastAppover = workFlowMonitorDTO.getLastApproverOid();
            if(lastAppover != null){
                UserApprovalDTO userByUserOid = baseClient.getUserByUserOid(lastAppover);
                if(userByUserOid != null){
                    workFlowMonitorDTO.setLastAppover(userByUserOid.getFullName());
                }
            }

            //创建人
            Long createdBy = workFlowMonitorDTO.getCreatedBy();
            if(createdBy != null){
                ContactCO contactCO = baseClient.getUserById(createdBy);
                if(contactCO != null){
                    workFlowMonitorDTO.setCreatedByName(contactCO.getFullName());
                }
            }
        });


        return workflowWorkFlowDocumentRefDTOS;
    }
  

    /**
     * @version 1.0
     * @author ly
     * @date 2019/04/28
     * @param ruleApprovalNodeOid
     * @param entityOid
     * @param entityType
     */
    @Transactional(rollbackFor = {Exception.class})
    public ApprovalResDTO workFlowJump(UUID ruleApprovalNodeOid, UUID entityOid, Integer entityType){
        Assert.notNull(ruleApprovalNodeOid, "ruleApprovalNodeOid null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(entityType, "entityType null");
        UUID currentUserId = OrgInformationUtil.getCurrentUserOid();

        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        //获取单据
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid , entityType);
        //获取实例
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        UUID lastNodeOid = instance.getLastNodeOid();
        if(lastNodeOid.equals(ruleApprovalNodeOid)){
            //节点重复
            throw new BizException(LocaleMessageConstants.WORKFLOW_RULEAPPROVALNODE_NOT_EXIST);
        }

        //跳转的节点
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);

        WorkflowNode workflowNode = new WorkflowNode(ruleApprovalNode, instance);

        Integer nodeType = workflowNode.getType();
        //不可跳转机器人节点
        if(WorkflowNode.TYPE_ROBOT.equals(nodeType)){
            throw new BizException(LocaleMessageConstants.WORKFLOW_RULEAPPROVALNODE_NOT_TYPE_ROBOT);
        }

        WorkflowMoveNodeAction action = new WorkflowMoveNodeAction(workflowActionService, instance, workflowNode);
        workflowMainService.runWorkflow(instance, action);
        // 累加成功数
        approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);


        WorkflowUser user =new WorkflowUser(currentUserId);
        UserApprovalDTO userByUser = baseClient.getUserByUserOid(currentUserId);
        WorkflowTask task = workflowBaseService.findTask(new WorkflowInstance(workFlowDocumentRef), user);
        //保存历史
        String remark = messageService.getMessageDetailByCode(MessageConstants.JUMP_REMARK, userByUser.getEmployeeCode(), userByUser.getFullName());
        workflowBaseService.saveHistory(task, ApprovalOperationEnum.APPROVAL_JUMP.getId().toString(), remark);
        return approvalResDTO;
    }

    /**
     * 审批控件跳转节点
     *
     * @param entityOid
     * @author polus
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public MonitorReturnNodeDTO listApprovalNode(Integer entityType, UUID entityOid) {
        List<MonitorNode> list = new ArrayList<>();
        // 获取节点
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(
                UUID.fromString(workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType)
                        .getApprovalNodeOid())
        );
        //获取审批链
        UUID ruleApprovalChainOid = ruleApprovalNode.getRuleApprovalChainOid();
        List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listByRuleApprovalChainOid(ruleApprovalChainOid);

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid , entityType);
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        UUID lastNodeOid = instance.getLastNodeOid();

        ruleApprovalNodes.forEach(e -> {
            MonitorNode monitorNode = new MonitorNode();
            monitorNode.setRemark(e.getRemark());
            if(WorkflowNode.TYPE_ROBOT.equals(e.getTypeNumber())){
                monitorNode.setJump(Boolean.FALSE);
            }else if(lastNodeOid.equals(e.getRuleApprovalNodeOid())){
                monitorNode.setJump(Boolean.FALSE);
                monitorNode.setIsApprovalNode(Boolean.TRUE);
            }else if(WorkflowNode.TYPE_END.equals(e.getTypeNumber())){
                monitorNode.setJump(Boolean.FALSE);
            }else{
                monitorNode.setJump(Boolean.TRUE);
            }
            monitorNode.setRuleApprovalNodeOid(e.getRuleApprovalNodeOid());
            list.add(monitorNode);
        });
        MonitorReturnNodeDTO monitorReturnNodeDTO = new MonitorReturnNodeDTO();
        monitorReturnNodeDTO.setApprovalNodeDTOList(list);
        return monitorReturnNodeDTO;
    }



    /**
     * @version 1.0
     * @author ly
     * @date 2019/04/30
     * 审批流预览
     */
    public void workflowPreview(){

    }
}
