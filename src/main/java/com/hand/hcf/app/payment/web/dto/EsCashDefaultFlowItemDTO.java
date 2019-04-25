package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsCashDefaultFlowItemDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transactionClassId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;
    private Boolean defaultFlag;
    private Boolean enabled;
    private Boolean deleted;
}
