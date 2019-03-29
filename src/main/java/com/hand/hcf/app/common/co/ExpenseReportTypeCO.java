package com.hand.hcf.app.common.co;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/25 10:45
 */
@Data
public class ExpenseReportTypeCO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //报账单类型代码
    private String reportTypeCode;
    //报账单类型名称
    private String reportTypeName;
}
