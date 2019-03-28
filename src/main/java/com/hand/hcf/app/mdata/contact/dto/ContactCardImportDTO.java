package com.hand.hcf.app.mdata.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class ContactCardImportDTO {

    private UUID userOid;

    private String employeeId;      //工号

    private String cardType;       //证件类型

    private String cardNo;          //证件号码

    private String firstName;       //姓

    private String lastName;        //名

    private String nationality;     //国籍

    private String cardExpiredTime;       //证件过期时间

    private String errorDetail;     //错误描述

    private Integer rowNum;

    public ContactCardImportDTO(String employeeId, String cardType, String cardNo, String firstName, String lastName, String nationality, String cardExpiredTime) {
        this.employeeId = employeeId;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.cardExpiredTime = cardExpiredTime;
    }
}
