package com.hand.hcf.app.payment.domain.enumeration;

/**
 * Created by kai.zhang on 2017-12-14.
 * 核销状态:N未生效;P已生效;Y:已审核 | 核销反冲状态:N拒绝;P已提交;Y:已审核
 */
public enum CashWriteOffStatus {
    N,
    P,
    Y
}
