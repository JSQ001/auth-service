package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/4/28 16:21
 * @remark 自动生成报账单所需信息
 */
@Data
@ApiModel(description = "自动生成报账单所需信息")
public class ExpenseReportAutoCreateDTO {

    /**
     * 报账单类型ID
     */
    @NotNull
    @ApiModelProperty(value = "报账单类型ID",dataType = "Long",required = true)
    private Long expenseReportTypeId;

    /**
     * 账本ID集合
     */
    @ApiModelProperty(value = "账本ID集合",dataType = "List<Long>")
    private List<Long> expenseBookIds;

    /**
     * 发票信息集合
     */
    @ApiModelProperty(value = "发票信息",dataType = "List<InvoiceDTO>")
    private List<InvoiceDTO> invoiceDTOS;

    /**
     * 忽略连号标志
     */
    @ApiModelProperty(value = "忽略连号标志",dataType = "Boolean")
    private Boolean ignoreContinuation;
}
