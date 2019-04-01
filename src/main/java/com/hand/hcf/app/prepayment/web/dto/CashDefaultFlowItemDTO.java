package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by 韩雪 on 2018/1/9.
 */
@Data
public class CashDefaultFlowItemDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transactionClassId;//现金事务分类ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;//现金流量项ID

    private Boolean defaultFlag;//默认现金流量项

    //现金事务分类code
    private String transactionClassCode;

    //现金事务分类name
    private String transactionClassName;

    //现金流量项code
    private String cashFlowItemCode;

    //现金流量项name
    private String cashFlowItemName;
}
