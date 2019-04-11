package com.hand.hcf.app.workflow.approval.util;

import lombok.Data;

/**
 * 工作流执行结果
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowResult {
    /** 执行的结果 */
    private String status;

    /** 执行的对象 */
    private Object entity;

    /** 下一个动作 */
    private WorkflowAction next;
}
