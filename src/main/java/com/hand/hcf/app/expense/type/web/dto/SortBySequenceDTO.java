package com.hand.hcf.app.expense.type.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/25
 */
@ApiModel(description = "bin.xie")
@Data
public class SortBySequenceDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "ађСа")
    private Integer sequence;
}
