package com.hand.hcf.app.payment.domain.enumeration;


import com.hand.hcf.app.core.enums.SysEnum;

/**
 * @description: 单据操作状态
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2018/1/24 14:43
 */
public enum PaymentDocumentOperationEnum implements SysEnum {
    GENERATE(1001)//编辑中
    , APPROVAL(1002)//审批中
    , WITHDRAW(1003) // 撤回
    , APPROVAL_PASS(1004) // 审批通过
    , APPROVAL_REJECT(1005) // 审批驳回

    //对公报销单 2001
    //预付款申请单 3001
    //付款申请单 4001
    //预算日记账 5001
    //合同  6001

    ,HOLD(6001)//暂挂中
    ,CANCEL(6002)//已取消
    ,FINISH(6003)//已完成


    ,PAYMENT(9001)// 支付
    ,RETURN(9002)// 退款
    ,REFUND(9003)// 退票
    ,RESERVED(9004)// 反冲


    ;
    private Integer id;

    PaymentDocumentOperationEnum(Integer id) {
        this.id = id;
    }

    public static PaymentDocumentOperationEnum parse(Integer id) {
        for (PaymentDocumentOperationEnum fieldType : PaymentDocumentOperationEnum.values()) {
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

}
