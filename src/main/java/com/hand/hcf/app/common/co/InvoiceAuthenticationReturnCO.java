package com.hand.hcf.app.common.co;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description
 * @Version: 1.0
 * @author: LIg
 * @date: 2019/4/18 16:39
 */
@ApiModel("发票认证接口返回信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceAuthenticationReturnCO {
    /**
     * 发票认证接口返回信息 Dto，
     * returnCode  返回代码
     * returnMessage 返回信息
     */
    private String returnCode;
    private String returnMessage;

}
