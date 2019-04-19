package com.hand.hcf.app.workflow.brms.enums;



import com.hand.hcf.app.core.enums.SysEnum;

import java.util.Arrays;
import java.util.List;

public enum RuleApprovalEnum implements SysEnum {
    VALID(1),
    INVALID(0),
    DELETED(2),
    FINISHED(3),

    //节点类型
    //节点类型-审批
    NODE_TYPE_APPROVAL(1001),//defaut

    //节点类型-通知
    NODE_TYPE_NOTICE(1002),
    //节点类型-机器人审批
    NODE_TYPE_ROBOT(1003),
    //打印节点
    NODE_TYPE_PRINT(1004),
    //结束节点
    NODE_TYPE_EED(1005),
    //为空规则
    //为空规则-跳过
    RULE_NULLABLE_SKIP(2001),//defaut
    //为空规则-报错
    RULE_NULLABLE_THROW(2002),

    //节点是否能否修改核定金额(0:不允许)
    NODE_INVOICE_ALLOW_UPDATE_TYPE_NOT_ALLOW(0),
    //节点是否能否修改核定金额(1允许)
    NODE_INVOICE_ALLOW_UPDATE_TYPE_ALLOW(1),

    //会签规则
    //会签规则-所有审批人（所有审批人审批通过则单据审批通过，任一审批人审批驳回则单据被驳回）
    RULE_CONUTERSIGN_ALL(3001),//defaut
    //会签规则-一票通过/一票否决（一位审批人审批通过则单据被审批通过，一位审批人审批驳回后则单据被驳回）
    RULE_CONUTERSIGN_ANY(3002),
    //会签规则-任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回）
    RULE_CONUTERSIGN_ALL_REJECT(3003),

    //重复规则
    //重复规则-跳过审批
    RULE_REPEAR_SKIP(4001),//defaut
    //重复规则-审批
    RULE_REPEAR_APPROVAL(4002),

    //包含提交人规则
    //跳过
    RULE_SELFAPPROVAL_SKIP(5001),
    //不跳过
    RULE_SELFAPPROVAL_NOT_SKIP(5002),
    //上级部门经理审批
    RULE_SELFAPPROVAL_SUPERIOR_MANAGER(5003),//set defaut
    //部门分管领导审批
    RULE_SELFAPPROVAL_CHARGE_MANAGER(5004),
    //部门同级经理审批
    RULE_SELFAPPROVAL_PEER_MANAGER(5005),

    //场景关联类型
    //审批链
    CONDITION_RELATION_TYPE_SCENE(7001),
    //审批者
    CONDITION_RELATION_TYPE_APPROVER(7002),
    //转交
    CONDITION_RELATION_TYPE_TRANSFER(7003),
    //通知
    CONDITION_RELATION_TYPE_NOTICE(7004);

    private Integer id;


    RuleApprovalEnum(Integer id) {
        this.id = id;
    }

    public static RuleApprovalEnum parse(Integer id) {
        for (RuleApprovalEnum applicablePersonnelType : RuleApprovalEnum.values()) {
            if (applicablePersonnelType.getId().equals(id)) {
                return applicablePersonnelType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * 返回所有节点类型
     * @return
     */
    public static List<RuleApprovalEnum> getNodeType()
    {
       return Arrays.asList(NODE_TYPE_APPROVAL,NODE_TYPE_NOTICE,NODE_TYPE_ROBOT,NODE_TYPE_PRINT,NODE_TYPE_EED);
    }
}
