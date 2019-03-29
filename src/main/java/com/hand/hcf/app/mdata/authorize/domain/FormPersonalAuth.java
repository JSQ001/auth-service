package com.hand.hcf.app.mdata.authorize.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 单据个人授权
 * @author shouting.cheng
 * @date 2019/1/18
 */
@Data
@TableName("sys_form_personal_auth")
public class FormPersonalAuth extends Domain {

    /**
     * 单据大类（代码）
     */
    private String documentCategory;
    /**
     * 单据类型ID
     */
    private Long formId;
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
     * 有效日期从
     */
    private ZonedDateTime startDate;
    /**
     * 有效日期至
     */
    private ZonedDateTime endDate;

}
