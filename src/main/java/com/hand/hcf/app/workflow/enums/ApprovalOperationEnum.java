package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * @author elvis.xu
 * @since 2016-07-10 17:24
 * action定义
 */
public enum ApprovalOperationEnum implements SysEnum {
    SUBMIT_FOR_APPROVAL(1001 ,"approval.history.submit") // 提交审批
    , WITHDRAW(1002 ,"approval.history.withDraw") // 撤回
    , CHANGE(1003 ,"approval.history.change") //申请单更改
    , APPROVAL(1004)//审批中
    , APPROVAL_PASS(2001 ,"approval.history.pass") // 审批通过
    , APPROVAL_REJECT(2002 ,"approval.history.reject") // 审批驳回
    , APPROVAL_INVOICE_REJECT(2003 ,"approval.history.invoice.reject") // 审批驳回费用
    , AUDIT_PASS(3001 ,"approval.history.audit.pass") // 审核通过
    , AUDIT_REJECT(3002 ,"approval.history.audit.reject") // 审核驳回
    , AUDIT_RECEIVE(3003 ,"approval.history.finance.receive") // 财务已收单
    , AUDIT_PRE_CHECK(3004 ,"approval.history.finance.preCheck") //财务预检
    , AUDIT_BACK(3005 ,"approval.history.finance.back")  //退单
    , AUDIT_NOTICE(3006 ,"approval.history.finance.notice") //审核通知
    , AUDIT_SEND(3007 ,"approval.history.finance.send")  //寄出
    , PAYMENT_IN_PROCESS(4000 ,"approval.history.finance.payment.process")//付款中确认
    , FINANCE_LOANED(4001 ,"approval.history.finance.loaned")//财务已付款
    , FINANCE_FAILED(4002 ,"approval.history.finance.loanFailed")//财务付款失败
    , RECEIPT_PASS(4011 ,"approval.history.receipted.pass") // 财务已开票
    , RECEIPT_REJECT(4012 ,"approval.history.receipted.reject") // 开票驳回
    ,REPAYMENT_T_SUBMIT(5000 ,"approval.history.repayment.t.submit") //APP还款提交
    ,REPAYMENT_C_SUBMIT(5001 ,"approval.history.repayment.c.submit") //现金还款提交
    ,REPAYMENT_PASS(5002 ,"approval.history.repayment.pass") //财务收款通过
    ,REPAYMENT_REJECT(5003 ,"approval.history.repayment.reject") //财务收款驳回
    ,REPAYMENT_F_SUBMIT(5004 ,"approval.history.repayment.f.submit") //报销单还款提交
    ,APPLICATION_CLOSE(5005 ,"approval.history.application.close") //申请单关闭
    ,PARTICIPANT_CLOSE(5006 ,"approval.history.participant.close") //用户停用申请单
    ,PARTICIPANT_RESTART(5007 ,"approval.history.participant.restart") //用户重启申请单
    ,INTEGRATION_CLOSE(5008 ,"approval.history.intergration.close") //中间件停用申请单
    , ADD_COUNTERSIGN(5009 ,"approval.history.add.countersign") //添加会签

    , CONFIRM_SUBMIT(6001 ,"approval.history.confirm.submit") //订票专员发起机票信息确认
    , CONFIRM_PASS(6002 ,"approval.history.confirm.pass") //用户确认信息合适
    , CONFIRM_REJECT(6003 ,"approval.history.confirm.reject") //用户确认机票不合适
    , PRICE_AUDIT_SUBMIT(6004 ,"approval.history.priceAudit.submit") //订票专员发起机票价格审核
    , PRICE_AUDIT_PASS(6005 ,"approval.history.priceAudit.pass") //价格审核通过
    , PRICE_AUDIT_REJECT(6006 ,"approval.history.priceAudit.reject") //价格审核驳回
    , ENDORSE_SUBMIT(6007 ,"approval.history.endorse.submit") //发起改签
    , REFUND_SUBMIT(6008 ,"approval.history.refund.submit") //发起退票
    , ENDORSED(6009 ,"approval.history.endorsed") //完成改签
    , REFUNDED(6010 ,"approval.history.refunded") //完成退票
    , BOOKED(6011 ,"approval.history.booked") //完成订票
    , APPROVAL_PASS_NEED_PRICE_AUDIT(6012 ,"approval.history.pass.need.priceAudit") // 审批通过需要价格审核

    ,REVIEWED_AMOUNT(7001 ,"approval.history.reviewed.amount"),//核定金额
    REVIEWED_RATE(7002 ,"approval.history.reviewed.rate"),//核定汇率
    REVIEWED_AMOUNT_RATE(7003 ,"approval.history.reviewed.amount.rate")//核定金额&核定汇率

//    20180227 RDC4161 财务员工交互
    , STAFF_REPLY(8001 ,"approval.history.staff.reply")
   /* , STAFF_REPLY(8001)
    // 20180520 支付模块日志记录
    ,PAYMENT(9001)// 支付
    ,RETURN(9002)// 退款
    ,REFUND(9003)// 退票
    ,RESERVED(9004)// 反冲*/
    //财务附件上传
   , FIN_UPLOAD_EXPENSE_ATTACHMENT(8002 ,"approval.history.finance.upload.expense.attachment")
    //财务附件删除
    , FIN_DELETE_EXPENSE_ATTACHMENT(8003 ,"approval.history.finance.delete.expense.attachment")


;
    private Integer id;

    private String messageKey;

    ApprovalOperationEnum(Integer id) {
        this.id = id;
    }

    ApprovalOperationEnum(Integer id, String messageKey) {
        this.id = id;
        this.messageKey = messageKey;
    }

    public static ApprovalOperationEnum parse(Integer id) {
        for (ApprovalOperationEnum fieldType : ApprovalOperationEnum.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public String getMessageKey(){
        return this.messageKey;
    }

    public static String getMessageKeyByID(Integer id){
        String messageKey = null;
        ApprovalOperationEnum approvalOperationEnum = parse(id);
        if(approvalOperationEnum != null){
            messageKey = approvalOperationEnum.getMessageKey();
        }
        return messageKey;
    }
}
