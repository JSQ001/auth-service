package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:09 2018/1/24
 * @Modified by
 */
@Data
@TableName("csh_acp_requisition_lns")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequisitionLine extends Domain {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;  //主键ID

    @TableField(value = "header_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId; //头表ID

    @TableField(value = "csh_transaction_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionId; //来源待付数据ID

    @TableField(value = "ref_document_type")
    private String refDocumentType; //关联单据类型

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "ref_document_id")
    private Long refDocumentId; //关联单据头ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "ref_document_line_id")
    private Long refDocumentLineId; //关联单据行ID

    @TableField(value = "company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "机构ID不能为空")
    private Long companyId; //机构ID

    @TableField(value = "partner_category")
    private String partnerCategory; //收款对象

    @TableField(value = "partner_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId; //收款对象ID

    @TableField(value = "csh_transaction_type_code")
    private String cshTransactionTypeCode; //现金事务类型代码

    @TableField(value = "csh_transaction_class_id")
    private String cshTransactionClassId;//现金事务分类ID

    @TableField(value = "cash_flow_item_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;//现金流量项ID

    @TableField(value = "currency_code")
    private String currencyCode;//币种

    @TableField(value = "exchange_rate")
    private Double exchangeRate;//汇率

    @TableField(value = "amount")
    private BigDecimal amount;//原币金额

    @TableField(value = "function_amount")
    private BigDecimal functionAmount;//本位币金额

    @TableField(value = "line_description")
    private String lineDescription;//行描述

    @TableField(value = "account_name")
    private String accountName; //银行户名

    @TableField(value = "account_number")
    private String accountNumber;//银行户名

    @TableField(value = "bank_location_code")
    private String bankLocationCode;//分行代码

    @TableField(value = "bank_location_name")
    private String bankLocationName;//分行名称

    @TableField(value = "province_code")
    private String provinceCode; //省份代码

    @TableField(value = "province")
    private String province;//省份名称

    @TableField(value = "city_code")
    private String cityCode;//城市代码

    @TableField(value = "city_name")
    private String cityName;//城市名称

    @TableField(value ="payment_method_category")
    private String paymentMethodCategory;//付款方式类型

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "contract_header_id")
    private Long contractHeaderId;//关联合同ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "payment_schedule_line_id")
    private Long paymentScheduleLineId;//资金计划行ID

    @TableField(value = "schedule_payment_date")
    private ZonedDateTime schedulePaymentDate;//计划付款日期

}
