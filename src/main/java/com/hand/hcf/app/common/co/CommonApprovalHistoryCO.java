package com.hand.hcf.app.common.co;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * A ApprovalHistory.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonApprovalHistoryCO {

    private Integer entityType;//单据类型 801004

    private UUID entityOid;//单据Oid

    private Integer operation;//操作类型(单据类型)

    private UUID operatorOid;//操作人Oid

    private String operationDetail;//处理意见

}
