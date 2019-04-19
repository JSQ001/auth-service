package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 申请单查询
 * @date 2019/4/17 11:58
 * @version: 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseApportionQueryCO {
    /**
     * 单据编号
     */
    private String requisitionNumber;
    /**
     * 单据大类
     */
    private String documentCategory;
    /**
     * 单据头ID
     */
    private Long documentHeaderId;
    /**
     * 单据行ID
     */
    private Long documentLineId;

    /**
     * 单据类型
     */
    private String documentTypeId;

    /**
     * 单据类型名称
     */
    private String documentTypeName;

    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 审核状态
     */
    private String auditFlag;

    /**
     * 备注
     */
    private String description;
}
