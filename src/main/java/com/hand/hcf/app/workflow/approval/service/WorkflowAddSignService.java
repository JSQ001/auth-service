package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.enums.ApprovalOrderEnum;
import com.hand.hcf.app.workflow.approval.enums.CounterSignOrderEnum;
import com.hand.hcf.app.workflow.approval.implement.WorkflowAddSignAction;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.countersign.CounterSignDTO;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工作流加签逻辑
 * @author mh.z
 * @date 2019/04/28
 */
@Service
public class WorkflowAddSignService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private BaseClient baseClient;

    /**
     * 加签
     * @author mh.z
     * @date 2019/04/28
     *
     * @param userOid 审批人oid
     * @param counterSignDTO 加签信息
     * @return 加签结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO addSignWorkflow(UUID userOid, CounterSignDTO counterSignDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);

        Integer entityType = counterSignDTO.getEntityType();
        UUID entityOid = counterSignDTO.getEntityOid();
        List<UUID> signerOidList = counterSignDTO.getUserOids();
        Integer countersignOrder = counterSignDTO.getCounterSignOrder();
        Integer approvalOrder = counterSignDTO.getApprovalOrder();
        String approvalText = counterSignDTO.getRemark();

        // 加签
        doAddSignWorkflow(entityType, entityOid, userOid, signerOidList, countersignOrder, approvalOrder, approvalText);
        return approvalResDTO;
    }

    /**
     * 加签
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param userOid 审批人oid
     * @param signerOidList 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @param approvalText 加签理由
     */
    protected void doAddSignWorkflow(Integer entityType, UUID entityOid, UUID userOid, List<UUID> signerOidList,
                                     Integer countersignOrder, Integer approvalOrder, String approvalText) {
        CheckUtil.notNull(entityType, "entityType null");
        CheckUtil.notNull(entityOid, "entityOid null");
        CheckUtil.notNull(userOid, "userOid null");
        CheckUtil.notNull(signerOidList, "signerOidList null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");

        // 根据单据大类和单据oid查找单据
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService
                .getByDocumentOidAndDocumentCategory(entityOid, entityType);
        if (workFlowDocumentRef == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_INSTANCE);
        }

        // 获取当前用户的租户
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        // 根据oid获取加签人
        List<ContactCO> signerCOList = baseClient.listContactCOs(signerOidList);
        // 检查加签人的租户（只能加签同租户下的用户）
        checkSignersTenant(tenantId, signerCOList);

        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        List<WorkflowUser> signerList = WorkflowUser.createUsers(signerOidList);
        String remark = getOperationDetail(signerCOList, countersignOrder, approvalOrder, approvalText);
        // 创建加签动作
        WorkflowAddSignAction action = new WorkflowAddSignAction(workflowActionService, instance, user,
                signerList, countersignOrder, approvalOrder, remark);

        // 执行加签的工作流逻辑
        workflowMainService.runWorkflowAndSendMessage(instance, action);
    }

    /**
     * 检查加签人的租户（只能加签同租户下的用户）
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param tenantId 租户id
     * @param signerList 加签人
     */
    protected void checkSignersTenant(Long tenantId, List<ContactCO> signerList) {
        CheckUtil.notNull(tenantId, "tenantId null");
        CheckUtil.notNull(signerList, "signerList null");

        for (ContactCO signer : signerList) {
            // 只能加签同租户下的用户
            if (!tenantId.equals(signer.getTenantId())) {
                throw new BizException(MessageConstants.ADDSIGN_USER_TENANT_DIFFERENT);
            }
        }
    }

    /**
     * 返回操作的详情
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param signerList 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @param approvalText 加签理由
     * @return
     */
    protected String getOperationDetail(List<ContactCO> signerList, Integer countersignOrder,
                                        Integer approvalOrder, String approvalText) {
        CheckUtil.notNull(signerList, "signerList null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");

        StringBuffer signersBuffer = new StringBuffer();
        for (int i = 0; i < signerList.size(); i++) {
            ContactCO contactCO = signerList.get(i);

            if (i > 0) {
                signersBuffer.append('、');
            }

            signersBuffer.append(contactCO.getEmployeeCode());
            signersBuffer.append('-');
            signersBuffer.append(contactCO.getFullName());
        }

        // 解决没有输入加签理由但历史里显示null的问题
        if (approvalText == null) {
            approvalText = "";
        }

        // 根据加签顺序和审批顺序获取多语言信息代码
        String messageCode = getMessageCode(countersignOrder, approvalOrder);
        // 调用第三方接口获取多语言信息
        String operationDetail = baseClient.getMessageDetailByCode(
                messageCode, true, signersBuffer, approvalText);
        return operationDetail;
    }

    /**
     * 根据加签顺序和审批顺序获取多语言信息代码并返回
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @return 消息代码
     */
    protected String getMessageCode(Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");

        String messageCode = null;

        if (CounterSignOrderEnum.BEFORE.getValue().equals(countersignOrder)) {
            // 节点前加签
            //
            if (ApprovalOrderEnum.PARALLEL.getValue().equals(approvalOrder)) {
                // 平行审批
                messageCode = MessageConstants.ADDSIGN_PARALLEL_APPROVE_BEFORE;
            } else if (ApprovalOrderEnum.ORDER.getValue().equals(approvalOrder)) {
                // 按加签顺序审批
                messageCode = MessageConstants.ADDSIGN_ORDER_APPROVE_BEFORE;
            }
        } else if (CounterSignOrderEnum.AFTER.getValue().equals(countersignOrder)) {
            // 节点后加签
            //
            if (ApprovalOrderEnum.PARALLEL.getValue().equals(approvalOrder)) {
                // 平行审批
                messageCode = MessageConstants.ADDSIGN_PARALLEL_APPROVE_AFTER;
            } else if (ApprovalOrderEnum.ORDER.getValue().equals(approvalOrder)) {
                // 按加签顺序审批
                messageCode = MessageConstants.ADDSIGN_ORDER_APPROVE_AFTER;
            }
        } else if (CounterSignOrderEnum.PARALLEL.getValue().equals(countersignOrder)) {
            // 平行于节点加签
            //
            if (ApprovalOrderEnum.PARALLEL.getValue().equals(approvalOrder)) {
                // 平行审批
                messageCode = MessageConstants.ADDSIGN_PARALLEL_APPROVE_PARALLEL;
            }
        }

        return messageCode;
    }

}
