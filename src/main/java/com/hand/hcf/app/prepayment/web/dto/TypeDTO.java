package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by 刘亮 on 2018/3/6.
 */
@Data
public class TypeDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String code;
    private String name;
}
