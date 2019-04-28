package com.hand.hcf.app.workflow.approval.util;

/**
 * 工作流动作
 * @author mh.z
 * @date 2019/04/07
 */
public interface WorkflowAction {

    /**
     * 返回动作名称
     *
     * @return
     */
    String getName();

    /**
     * 执行动作
     *
     * @return
     */
    WorkflowResult execute();
}
