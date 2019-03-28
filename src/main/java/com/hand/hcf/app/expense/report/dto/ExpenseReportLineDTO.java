package com.hand.hcf.app.expense.report.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.report.domain.ExpenseReportLine;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.core.serializer.CollectionToStringSerializer;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/11 09:11
 * @remark
 */
@Data
public class ExpenseReportLineDTO extends ExpenseReportLine{

    /**
     * 费用体系
     */
    @Valid
    private List<ExpenseFieldDTO> fields;
    /**
     * 关联发票信息(发票头ID为空时表示手工录入信息)
     */
    @Valid
    private List<InvoiceHead> invoiceHeads;

    /**
     * 分摊行数据
     */
    @Valid
    private List<ExpenseReportDistDTO> expenseReportDistList;

}
