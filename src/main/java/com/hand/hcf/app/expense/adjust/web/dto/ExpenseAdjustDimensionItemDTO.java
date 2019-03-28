package com.hand.hcf.app.expense.adjust.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/10
 */
@Data
public class ExpenseAdjustDimensionItemDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;

    private String itemName;
}
