package com.hand.hcf.app.expense.type.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/25
 */
@Data
public class SortBySequenceDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Integer sequence;
}
