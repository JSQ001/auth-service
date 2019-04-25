package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.PaymentCompanyConfig;
import lombok.Data;

/**
 * Created by 刘亮 on 2017/9/29.
 */
@Data
public class PaymentCompanyConfigDTO extends PaymentCompanyConfig {

    private String companyCode;//单据公司code

    private String companyName;//单据公司名称

    private String ducumentType;//单据类型

    private String paymentCompanyCode;//付款公司代码

    private String paymentCompanyName;//付款公司名称

}
