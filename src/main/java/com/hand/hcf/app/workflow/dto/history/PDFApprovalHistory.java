package com.hand.hcf.app.workflow.dto.history;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lichao on 17/5/9.
 * pdf 展示审批历史
 */
@Getter
@Setter
public class PDFApprovalHistory {
    //当前操作人
    private String operator;
    //操作人工号
    private String employeeID;
    //代理操作人
    private String proxyOperator;
    //代理操作人工号
    private String proxyEmployeeID;
    //操作人人员类型
    private String employeeType;
    //角色
    private String roleName;
    //审批日期
    private String approvalDate;
    //具体操作
    private String operation;
    //驳回备注
    private String operationDetail;
    //操作类型
    private Integer operationType;
    //备注
    private String remark;
    //职位
    private String  title;
}
