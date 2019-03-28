package com.hand.hcf.app.expense.report.implement.web;

import com.hand.hcf.app.apply.expense.ExpenseReportTypeInterface;
import com.hand.hcf.app.apply.expense.dto.ExpenseReportTypeCO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/25 11:12
 */
@RestController
public class ExpenseReportTypControllerImpl implements ExpenseReportTypeInterface {

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private MapperFacade mapperFacade;

    /**
     * 根据id获取报账单类型
     * @param id
     * @return
     */
    @Override
    public ExpenseReportTypeCO getExpenseReportTypeById(Long id) {
        ExpenseReportType reportType = expenseReportTypeService.selectById(id);
        return mapperFacade.map(reportType,ExpenseReportTypeCO.class);
    }
}
