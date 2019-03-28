package com.hand.hcf.app.prepayment.web.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by lichao on 17/6/26.
 * 审批列表返回对象，保持和旧的数据结构一致，重新定义返回字段
 */
@Getter
@Setter
public class ApprovalEntityDTO {
    private UUID entityOid;
    private Integer entityType;
    private PrepaymentApprovalDTO prepaymentApprovalView;
}
