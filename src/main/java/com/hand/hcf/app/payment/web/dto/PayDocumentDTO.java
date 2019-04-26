package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 付款单据DTO
 * Created by 刘亮 on 2017/12/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayDocumentDTO {

    //单据编号
    private String documentCode;

    //单据类型
    private String documentTypeCode;
    private String documentTypeName;

    //币种
    private String currency;

    //单据总金额
    private BigDecimal documentTotalAmount;

    //单据申请人
    private String documentApplicant;

    //单据ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;

    //单据日期
    private ZonedDateTime documentDate;

    private String documentCategory;

}
