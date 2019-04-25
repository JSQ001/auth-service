package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by 刘亮 on 2017/10/17.
 */
@Data
public class CompanyBankPaymentDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentMethodId;

    private String paymentMethodCategory;//付款方式类型--线上线下落地文件

    private String paymentMethodCategoryName;

    private String paymentMethodCode;//付款方式代码

    private String description;//付款方式名称
}
