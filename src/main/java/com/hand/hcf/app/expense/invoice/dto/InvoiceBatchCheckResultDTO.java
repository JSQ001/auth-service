package com.hand.hcf.app.expense.invoice.dto;

import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ClassName InvoiceBatchCheckResultDTO
 *
 * @description 发票批量查验返回结果
 */
@Data
@AllArgsConstructor
@Builder
@ApiModel(description = "发票信息")
public class InvoiceBatchCheckResultDTO {

    @ApiModelProperty(value = "发票批量查验发票总数",dataType = "Long")
    private Long invoiceBatchNum;

    @ApiModelProperty(value = "发票批量查验发票成功总数",dataType = "Long")
    private Long successCheckNum;

    @ApiModelProperty(value = "发票批量查验发票失败总数",dataType = "Long")
    private Long errorCheckNum;

    @ApiModelProperty(value = "发票头信息",dataType = "InvoiceHead")
    private List<InvoiceHead> invoiceHeadList;

    public InvoiceBatchCheckResultDTO() {
        this.invoiceBatchNum = 0L;
        this.successCheckNum = 0L;
        this.errorCheckNum = 0L;
    }

}
