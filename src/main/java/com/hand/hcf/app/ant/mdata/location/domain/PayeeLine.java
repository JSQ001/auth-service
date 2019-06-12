package com.hand.hcf.app.ant.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zihao.yang on 2019-6-11 19:52:16
 */
@Data
@TableName("sys_payee_header")
@ApiModel(description = "收款方表头信息")
public class PayeeLine extends Domain {
    /**
     * 收款方头id
     */
    @TableField(value = "header_id")
    @ApiModelProperty(value = "收款方头id",dataType = "Long")
    private Long headerId;

    /**
     * 收款方银行账号
     */
    @TableField(value = "account_name")
    @ApiModelProperty(value = "收款方银行账号",dataType = "String")
    private String accountName;

    /**
     * 收款方银行账号
     */
    @TableField(value = "account_number")
    @ApiModelProperty(value = "收款方银行账号",dataType = "String")
    private String accountNumber;

    /**
     * 开户支行编码
     */
    @TableField(value = "bank_code")
    @ApiModelProperty(value = "开户支行编码",dataType = "String")
    private String bankCode;

    /**
     * 开户城市
     */
    @TableField(value = "bank_city_code")
    @ApiModelProperty(value = "开户城市",dataType = "String")
    private String bankCityCode;

    /**
     * 开户省
     */
    @TableField(value = "bank_province_code")
    @ApiModelProperty(value = "开户省",dataType = "String")
    private String bankProvinceCode;

    /**
     * 联系人
     */
    @TableField(value = "link_man")
    @ApiModelProperty(value = "联系人",dataType = "String")
    private String linkMan;


    /**
     * 联系电话
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "联系人",dataType = "String")
    private String phone;

    /**
     * 联系地址
     */
    @TableField(value = "account_address")
    @ApiModelProperty(value = "联系地址",dataType = "String")
    private String account_address;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute1")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute1;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute2")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute2;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute3")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute3;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute4")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute4;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute5")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute5;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute6")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute6;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute7")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute7;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute8")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute8;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute9")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute9;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute10")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute10;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute11")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute11;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute12")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute12;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute13")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute13;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute14")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute14;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute15")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute15;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute16")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute16;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute17")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute17;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute18")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute18;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute19")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute19;

    /**
     * 扩展字段
     */
    @TableField(value = "attribute20")
    @ApiModelProperty(value = "扩展字段",dataType = "String")
    private String attribute20;
}
