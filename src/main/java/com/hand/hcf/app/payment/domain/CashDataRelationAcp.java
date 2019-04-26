package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:02 2018/4/25
 * @Modified by
 */
@Data
@TableName("csh_data_relation_acp")
public class CashDataRelationAcp extends Domain {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //报账单头ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "report_head_id")
    private Long reportHeadId;

    //报账单计划付款行ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "report_line_id")
    private Long reportLineId;

    //单据类型
    @NotNull
    @TableField(value = "document_type")
    private String documentType;

    //单据头ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "document_head_id")
    private Long documentHeadId;

    //单据行ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "document_line_id")
    private Long documentLineId;

    //关联金额
    @NotNull
    @TableField(value = "amount")
    private BigDecimal amount;

    //币种
    @TableField(value = "currency_code")
    private String currencyCode;

}
