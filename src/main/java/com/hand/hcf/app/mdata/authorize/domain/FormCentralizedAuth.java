package com.hand.hcf.app.mdata.authorize.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 单据集中授权
 * @author shouting.cheng
 * @date 2019/1/18
 */
@Data
@TableName("sys_form_centralized_auth")
public class FormCentralizedAuth extends Domain {

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
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 有效日期从
     */
    private ZonedDateTime startDate;
    /**
     * 有效日期至
     */
    private ZonedDateTime endDate;

}
