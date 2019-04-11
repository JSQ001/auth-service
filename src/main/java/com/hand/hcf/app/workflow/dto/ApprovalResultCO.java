package com.hand.hcf.app.workflow.dto;

import lombok.Data;

/**
 * @author mh.z
 * @date 2019/03/22
 * @description 审批结果
 */
@Data
public class ApprovalResultCO {
    // 成功标志
    private Boolean success;

    // 单据状态
    private Integer status;

    // 错误信息
    private String error;
}
