package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 银行流水信息类
 * @date 2019/5/29 10:18
 */
@Data
@TableName("exp_bank_flow")
public class ExpBankFlow extends Domain {

    /**
     * id-唯一识别号
     */
    @TableField(value = "id")
    private Long Id;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 资金流水号
     */
    @TableField(value = "fund_flow_number")
    private Long fundFlowNumber;

    /**
     * 对方户名
     */
    @TableField(value = "bank_account_name")
    private String bankAccountName;

    /**
     * 对方户号
     */
    @TableField(value = "bank_account_number")
    private String bankAccountNumber;

    /**
     * 银行备注
     */
    @TableField(value = "bank_remark")
    private String bankRemark;

    /**
     * 币种代码
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 支付日期
     */
    @TableField(value = "pay_date")
    private ZonedDateTime payDate;

    /**
     * 流水金额
     */
    @TableField("flow_amount")
    private BigDecimal flowAmount;

    /**
     * 勾兑状态
     */
    @TableField(value = "blend_status", strategy = FieldStrategy.NOT_NULL)
    private Boolean blendStatus;


    /**
     * 报账状态
     */
    @TableField(value = "status", strategy = FieldStrategy.NOT_NULL)
    private Boolean status;

    /**
     * 以下字段表中不存在，用于显示在页面上
     */
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;

    //币种名称
    @TableField(exist = false)
    private String currencyName;

    /*    *//**
     * 勾兑状态描述
     *//*
    @TableField(exist = false)
    private String blendStatus;*/


}
