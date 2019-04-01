package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Created by 韩雪 on 2017/12/8.
 */
@Data
public class ContractDocumentRelation {
    //主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //合同头ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeadId;

    //合同行ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractLineId;

    //单据类型
    @NotNull
    private String documentType;

    //单据头ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeadId;

    //单据行ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;

    //关联金额
    @NotNull
    private Double amount;

    //币种
    private String currencyCode;

    //汇率
    private Double exchangeRate;

    //本币金额
    private Double functionAmount;

    //创建日期
    private ZonedDateTime createdDate;

    //创建用户ID
    @NotNull
    private Long createdBy;

    private Boolean deleted;
}
