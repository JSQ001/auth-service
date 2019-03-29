package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by yangqi on 2017/1/11.
 */
@TableName( "sys_contact_card_info")
@Data
public class ContactCard extends DomainLogicEnable {


    @TableField( "contact_card_oid")
    @NotNull
    private UUID contactCardOid;
    @NotNull
    @TableField( "user_oid")
    private UUID userOid;
    private Integer cardType;       //证件类型
    private String cardNo;          //证件号码
    private String firstName;       //名
    private String lastName;        //姓
    @TableField(strategy = FieldStrategy.IGNORED)
    private String nationality;     //国籍
    private ZonedDateTime cardExpiredTime;       //证件过期时间
    private Boolean primary = false;         //是否默认

    /**
     * 国籍编码
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private String nationalityCode;
}
