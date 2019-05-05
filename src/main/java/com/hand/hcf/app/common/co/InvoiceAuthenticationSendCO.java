package com.hand.hcf.app.common.co;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description
 * @Version: 1.0
 * @author: LIg
 * @date: 2019/4/18 16:39
 */
@ApiModel("发票认证接口发送信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceAuthenticationSendCO {
    /**
     * 发票认证接口信息 Dto，
     * batchNo  批次号
     * buyerTaxNo 购方税号
     * contentRows 总条数
     * invoices 发票信息
     */

    private String batchNo;
    private String buyerTaxNo;
    private Integer contentRows;
    private List<InvoiceAuthenticationSendLineCO> invoices;

}
