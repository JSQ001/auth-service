package com.hand.hcf.app.workflow.approval.util;

import lombok.Data;

/**
 * 工作流动作执行结果
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowResult {
    /** 动作执行的对象（实例/节点/任务） */
    private Object entity;

    /** 动作执行的结果 */
    private String status;

    /** 下一个动作/动作集合 */
    private Object next;
}
