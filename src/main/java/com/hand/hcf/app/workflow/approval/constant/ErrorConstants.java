package com.hand.hcf.app.workflow.approval.constant;

/**
 * @author mh.z
 * @date 2019/04/07
 */
public class ErrorConstants {

    /** 不能提交审批中和已通过的实例 */
    public static final String INSTANCE_STATUS_CANNOT_SUBMIT = "INSTANCE_STATUS_CANNOT_SUBMIT";

    /**  只能撤回审批中的实例 */
    public static final String INSTANCE_STATUS_CANNOT_WITHDRAW = "INSTANCE_STATUS_CANNOT_WITHDRAW";

    /** 空节点且不能跳过 */
    public static final String NODE_EMPTY_NOT_SKIP = "NODE_EMPTY_NOT_SKIP";

    /** 整条审批流没有审批任务 */
    public static final String CHAIN_NOT_EXISTS_TASK = "CHAIN_NOT_EXISTS_TASK";

    /** 找不到任务 */
    public static final String NOT_FIND_THE_TASK = "NOT_FIND_THE_TASK";

    /** 找不到实例 */
    public static final String NOT_FIND_THE_INSTANCE = "NOT_FIND_THE_INSTANCE";
}
