package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  费用体系类型
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/19
 */
@Data
public class ExpenseTypeCO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String code;

    private String name;

    private String typeCode;
}
