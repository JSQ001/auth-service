package com.hand.hcf.app.mdata.authorize.dto;

import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 单据个人授权DTO
 * @author shouting.cheng
 * @date 2019/1/22
 */
@Data
public class FormPersonalAuthDTO extends DomainObjectDTO {
    /**
     * 单据大类
     */
    private String documentCategory;
    /**
     * 单据大类（描述）
     */
    private String documentCategoryDesc;
    /**
     * 单据类型ID
     */
    private Long formId;
    /**
     * 单据类型名称
     */
    private String formName;
    /**
     * 委托人ID
     */
    private Long mandatorId;
    /**
     * 委托人工号
     */
    private String mandatorCode;
    /**
     * 委托人姓名
     */
    private String mandatorName;
    /**
     * 受托人ID
     */
    private Long baileeId;
    /**
     * 受托人工号
     */
    private String baileeCode;
    /**
     * 受托人姓名
     */
    private String baileeName;
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
