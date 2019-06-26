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
 * @description: 银行流水信息临时Domain类
 * @date 2019/06/19 10:18
 */
@Data
@TableName("exp_bank_flow_temp")
public class ExpBankFlowTempDomain extends Domain {

    /**
     * id-唯一识别号
     */
    @TableField(value = "id")
    private Long Id;


    /**
     * 导入数据的行号
     */
    @TableField(value = "row_number")
    private String rowNumber;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 公司代码code
     */
    @TableField(value = "company_code")
    private String companyCode;

    /**
     * 支付日期--银行交易日期
     */
    @TableField(value = "pay_date")
    private String payDate;

    /**
     * 资金流水号
     */
    @TableField(value = "fund_flow_number")
    private String fundFlowNumber;

    /**
     * 对方银行户名
     */
    @TableField(value = "bank_account_name")
    private String bankAccountName;


    /**
     * 借方金额
     */
    @TableField(value = "flow_amount_lender")
    private String flowAmountLender;

    /**
     * 贷方金额
     */
    @TableField(value = "flow_amount_debit")
    private String flowAmountDebit;


    /**
     * 币种代码
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 银行备注
     */
    @TableField(value = "bank_remark")
    private String bankRemark;


    /**
     * 临时表字段--批次号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 错误明细
     */
    @TableField(value = "error_detail")
    private String errorDetail;

    /**
     * 错误标记
     */
    @TableField(value = "error_flag")
    private Boolean errorFlag;

}
