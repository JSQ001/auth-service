package com.hand.hcf.app.workflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/10.
 * 工作流发布的事件，主要作用是根据消息信息，修改单据的状态（一般是工作流结束时触发该事件）
 */
@Component
public class WorkFlowEventPublishService {
    private static final Logger logger = LoggerFactory.getLogger(WorkFlowEventPublishService.class);

    private String applicationName;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkFlowEventLogsService workflowEventLogsService;
    // added by mh.z 20190322 目前没有好的方式判断是不是提交操作
    private static final ThreadLocal<Boolean> publishEnabled = new ThreadLocal<Boolean>();

    public static void enablePublish(boolean enabled) {
        publishEnabled.set(enabled);
    }

    /**
     *  这里发布时，event会自动生产一个全局唯一UUID ：event.getId()
     *  workflowMessage 有以下字段： entityOid:单据Oid,entityTyp: 单据类型 801003:表示预付款单,801004: 表示合同单据...,
     *  status: 需要修改的单据状态  如：1001,  remark:备注说明
     *  destinationService：表示服务注册到Eureka中的名称(如：prepayment:预付款，contract:合同, budget:预算)，这样能保证每次只对具体的服务发布消息
     */
    /*public void publishEvent(WorkFlowDocumentRef workFlowDocumentRef) {
        // added by mh.z 20190322 目前没有好的方式判断是不是提交操作
        if (Boolean.FALSE.equals(publishEnabled.get())) {
            workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
            return;
        }

        // added by mh.z 20190311 解决BUG，机器人不在第一个节点并且机器人通过/驳回情况下出现发送多个矛盾的消息，
        // 第一个消息发送的状态是“审批中”或“驳回”，第二个消息却是“审批中”，结果是单据审批通过了但单据的状态还是审批中
        if (DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
            UUID documentOid = workFlowDocumentRef.getDocumentOid();
            Integer documentCategory = workFlowDocumentRef.getDocumentCategory();
            WorkFlowDocumentRef existWorkFlowDocumentRef  = workFlowDocumentRefService
                    .getByDocumentOidAndDocumentCategory(documentOid, documentCategory);
            boolean approveEnd = false;

            if (existWorkFlowDocumentRef != null) {
                Integer status = existWorkFlowDocumentRef.getStatus();

                if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(status)
                        || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)) {
                    approveEnd = true;
                }
            }

            // 如果单据在这之前已经审批结束了则不应该再修改单据关联工作流表，
            // 否则更新单据关联工作流表
            if (!approveEnd) {
                workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
            }

            // 不需要发送“审批中”消息
            return;
        }
        // END added by mh.z

        //如果目标服务为空或者与源服务名一致，则不发布消息
        if(workFlowDocumentRef.getDestinationService() == null || (workFlowDocumentRef.getDestinationService().equalsIgnoreCase(applicationName))){
            return;
        }
        WorkflowMessageCO workflowMessage = new WorkflowMessageCO();
        workflowMessage.setUserBean(OrgInformationUtil.getUser());
        if(workFlowDocumentRef != null){
            workflowMessage.setEntityOid(workFlowDocumentRef.getDocumentOid()); // 单据Oid
            workflowMessage.setEntityType(workFlowDocumentRef.getDocumentCategory().toString());// 单据大类
            workflowMessage.setStatus(workFlowDocumentRef.getStatus());  // 状态
            workflowMessage.setUserId(workFlowDocumentRef.getCreatedBy()); // 创建人ID
            workflowMessage.setDocumentId(workFlowDocumentRef.getDocumentId()); // 单据ID
            workflowMessage.setApprovalText(workFlowDocumentRef.getRejectReason());//审批意见
            workflowMessage.setRemark("单据编号:" + workFlowDocumentRef.getDocumentNumber());
        }
        WorkflowCustomRemoteEvent event = new WorkflowCustomRemoteEvent(this,applicationName+":**", workFlowDocumentRef.getDestinationService(), workflowMessage);
        logger.info("[发布工作流事件消息]：" + event);
        workFlowDocumentRef.setEventId(event.getId());
        workFlowDocumentRef.setEventConfirmStatus(false);
        // 记录到事件日志表
        WorkFlowEventLogs eventLogs = new WorkFlowEventLogs();
        eventLogs.setEventId(event.getId());
        eventLogs.setDocumentOid(workFlowDocumentRef.getDocumentOid());
        eventLogs.setDocumentCategory(workFlowDocumentRef.getDocumentCategory());
        eventLogs.setDestinationService(workFlowDocumentRef.getDestinationService());
        eventLogs.setEventConfirmStatus(false);
        workflowEventLogsService.createSysWorkflowEventLogs(eventLogs);
        workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
        applicationEventPublisher.publishEvent(event);
    }
    *//**
     * @param event 监听 对应的消费端是否正常消费了该条消息，并将结果更新到工作流关联的单据表上去
     *//*
    @EventListener(AckRemoteApplicationEvent.class)
    public void ackConsumerConfirm(AckRemoteApplicationEvent event){
        // 相当于回调函数，在消息成功被消费时调用，event里能取得actId: event.getAckId 和 消息的ID：event.getId()
        logger.info("[发布工作流事件消息]消费者端: "+event.getOriginService()+", 服务消费确认 ackId :" + event.getAckId());
        WorkFlowEventLogs eventLogs = workflowEventLogsService.getSysWorkflowEventLogsByEventId(event.getAckId());
        if(eventLogs != null){
            eventLogs.setEventConfirmStatus(true);  // 更新消息事件的确认状态
            workflowEventLogsService.updateById(eventLogs);
        }
        // 根据事件ID，取得单据关联工作流消息
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByEventId(event.getAckId());
        if(workFlowDocumentRef != null){
            workFlowDocumentRef.setEventConfirmStatus(true);
            workFlowDocumentRefService.updateById(workFlowDocumentRef);
            if(workFlowDocumentRef.getStatus().equals(DocumentOperationEnum.APPROVAL_REJECT.getId()) || workFlowDocumentRef.getStatus().equals(DocumentOperationEnum.WITHDRAW.getId())){
            }
        }
    }*/
}
