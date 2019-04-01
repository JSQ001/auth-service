package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.UUID;

/**
 * @author mh.z
 * @date 2019/03/25
 * @description 审批结果通知
 */
@Data
public class ApprovalNotificationCO {
    // 单据id
    private Long documentId;

    // 单据oid
    private UUID documentOid;

    // 单据大类
    private Integer documentCategory;

    // 单据状态
    private Integer documentStatus;
}
