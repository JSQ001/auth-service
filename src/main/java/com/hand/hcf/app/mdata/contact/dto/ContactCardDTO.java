package com.hand.hcf.app.mdata.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by yangqi on 2017/1/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class ContactCardDTO {

    private UUID contactCardOid;

    private UUID userOid;           //用户Oid

    private String employeeId;      //工号

    private Integer cardType;       //证件类型

    private String cardTypeName;

    private String cardNo;          //证件号码
    /**
     * 证件号码（导出证件信息用）
     */
    private String cardNoStr;

    private String firstName;       //姓

    private String lastName;        //名

    private String nationality;     //国籍

    /**
     * 国籍编码
     */
    private String nationalityCode;

    private String originalCardNo;

    private ZonedDateTime cardExpiredTime;       //证件过期时间
    /**
     * 证件过期时间（导出证件信息用）
     */
    private String cardExpiredTimeStr;

    private Boolean primary = false;         //是否默认
    /**
     * 是否默认（导出证件信息用）
     */
    private String primaryStr;

    private Boolean enabled = true;             //是否可用
    /**
     * 是否启用（导出证件信息用）
     */
    private String enabledStr;

}
