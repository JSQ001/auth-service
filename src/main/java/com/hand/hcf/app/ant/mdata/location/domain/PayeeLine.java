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
     * 收款方账户名
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
     * 联系人
     */
    @TableField(value = "link_man")
    @ApiModelProperty(value = "联系人",dataType = "String")
    private String linkMan;


    /**
     * 联系电话
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "联系电话",dataType = "String")
    private String phone;


    /**
     * 联系地址
     */
    @TableField(value = "account_address")
    @ApiModelProperty(value = "联系地址",dataType = "String")
    private String account_address;

    /**
     * 占用状态
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "占用状态",dataType = "String")
    private String status;

    /**
     * bsb code
     */
    @TableField(value = "bsb_code")
    @ApiModelProperty(value = "bsb code",dataType = "String")
    private String bsbCode;

    /**
     * sort code
     */
    @TableField(value = "sort_code")
    @ApiModelProperty(value = "sort code",dataType = "String")
    private String sortCode;

    /**
     * IBAN
     */
    @TableField(value = "iban")
    @ApiModelProperty(value = "IBAN",dataType = "String")
    private String iban;

    /**
     * Beneficiary Name
     */
    @TableField(value = "beneficiary_name")
    @ApiModelProperty(value = "Beneficiary Name",dataType = "String")
    private String beneficiaryName;

    /**
     * SWIFT No.
     */
    @TableField(value = "swift_no")
    @ApiModelProperty(value = "SWIFT No.",dataType = "String")
    private String swiftNo;

    /**
     * Beneficiary Account No.
     */
    @TableField(value = "beneficiary_account_no")
    @ApiModelProperty(value = "Beneficiary Account No.",dataType = "String")
    private String beneficiaryAccountNo;

    /**
     * Routing No./SWIFT No.
     */
    @TableField(value = "routing_swift_no")
    @ApiModelProperty(value = "Routing No./SWIFT No.",dataType = "String")
    private String routingSwiftNo;

    /**
     * Statement Letter (财资准备)
     */
    @TableField(value = "statement_letter")
    @ApiModelProperty(value = "Statement Letter (财资准备)",dataType = "String")
    private String statementLetter;

    /**
     * Supporting documents
     */
    @TableField(value = "supporting_documents")
    @ApiModelProperty(value = "Supporting documents",dataType = "String")
    private String supportingDocuments;

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
