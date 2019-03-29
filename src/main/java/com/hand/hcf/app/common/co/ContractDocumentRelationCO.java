package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class ContractDocumentRelationCO {
        //主键ID
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;

        //合同头ID
        @JsonSerialize(using = ToStringSerializer.class)
        private Long contractHeadId;

        //合同行ID
        @JsonSerialize(using = ToStringSerializer.class)
        private Long contractLineId;

        //单据类型
        private String documentType;

        //单据头ID
        @JsonSerialize(using = ToStringSerializer.class)
        private Long documentHeadId;

        //单据行ID
        @JsonSerialize(using = ToStringSerializer.class)
        private Long documentLineId;

        //关联金额
        private BigDecimal amount;

        //币种
        private String currencyCode;

        //汇率
        private Double exchangeRate;

        //本币金额
        private BigDecimal functionAmount;

        //创建日期
        private ZonedDateTime createdDate;

        //创建用户ID
        private Long createdBy;

        private Boolean isDeleted;

}
