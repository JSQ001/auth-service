package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2018/3/8.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFormForOtherDTO {
    //预付款单类型中的申请单类型：取申请单下的费用申请单

    //id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //申请单类型代码
    private String formCode;

    //申请单类型名称
    private String formName;

    //是否被分配
    private Boolean assigned;

    //公司name
    private String companyName;
}
