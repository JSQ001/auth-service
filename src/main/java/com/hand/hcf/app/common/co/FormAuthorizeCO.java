package com.hand.hcf.app.common.co;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FormAuthorizeCO {

    /**
     * 单据大类（代码）
     */
    private String documentCategory;
    /**
     * 单据类型ID
     */
    private Long formId;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 部门ID
     */
    private Long unitId;
    /**
     * 委托人ID
     */
    private Long mandatorId;
    /**
     * 受托人ID
     */
    private Long baileeId;
    /**
     * 有效日期从
     */
    private ZonedDateTime startDate;
    /**
     * 有效日期至
     */
    private ZonedDateTime endDate;

}
