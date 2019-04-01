package com.hand.hcf.app.expense.adjust.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseAdjustTypeDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String code;

    private String name;
    @JsonIgnore
    private List<Long> userGroupIdList;
}
