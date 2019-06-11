package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.report.domain.ExpenseReportLine;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/11 09:11
 * @remark
 */
@ApiModel(description = "费用行")
@Data
public class ExpenseReportLineDTO extends ExpenseReportLine{

    /**
     * 费用体系
     */
    @Valid
    @ApiModelProperty(value = "费用体系")
    private List<ExpenseFieldDTO> fields;
    /**
     * 关联发票信息
     */
    @ApiModelProperty(value = "关联发票信息")
    private List<InvoiceHead> invoiceHeads;

    /**
     * 分摊行数据
     */
    @Valid
    @ApiModelProperty(value = "分摊行数据")
    private List<ExpenseReportDistDTO> expenseReportDistList;

}
