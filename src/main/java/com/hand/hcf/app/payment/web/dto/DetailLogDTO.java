package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 刘亮 on 2018/4/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailLogDTO {
    /**
     * 审批动作\
     */
    private Integer operation;
    /**
     * 审批类型
     */
    private Integer operationType;
    /**
     * 审批时间
     */
    private String lastUpdatedDate;

    /**
     * 员工工号
     */
    private String employeeID;
    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 审批意见
     */
    private String operationDetail;
    /**
     * 加签规则
     */
    private Integer countersignType;
    /**
     * 审批动作描述
     */
    private String operationRemark;
}
