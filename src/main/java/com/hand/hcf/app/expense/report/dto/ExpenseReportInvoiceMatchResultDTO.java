package com.hand.hcf.app.expense.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/4/30 16:36
 * @remark 发票匹配费用类型返回信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "发票生成报账单返回信息")
public class ExpenseReportInvoiceMatchResultDTO {

    /**
     * 报账单类型ID
     */
    @ApiModelProperty(value = "报账单类型ID",dataType = "Long")
    private Long expenseReportId;

    /**
     * 匹配状态
     */
    @ApiModelProperty(value = "匹配状态，1001:成功；1002:发票重复；1003:发票连号；1004:查验失败",dataType = "Integer")
    private Integer status;

    /**
     * 校验返回信息
     */
    @ApiModelProperty(value = "校验返回信息",dataType = "String")
    private String resultMessage;

    /**
     * 发票信息
     */
    @ApiModelProperty(value = "发票信息",dataType = "List<InvoiceDTO>")
    private List<InvoiceDTO> invoiceDTOS;

    /**
     * 账本集合
     */
    @JsonIgnore
    @ApiModelProperty(value = "账本集合",dataType = "List<ExpenseBook>", hidden = true)
    private List<ExpenseBook> expenseBooks;
}
