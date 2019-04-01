package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @Description: 预付款单据头行ID
 * @Date: Created in 10:37 2018/7/4
 * @Modified by
 */
@Data
public class PrepaymentDocumentIdsCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;

    private List<Long> lineIds;
}
