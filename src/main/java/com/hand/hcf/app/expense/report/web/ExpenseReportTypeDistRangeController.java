package com.hand.hcf.app.expense.report.web;

import com.hand.hcf.app.expense.report.service.ExpenseReportTypeDistRangeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/3
 */
@RestController
@RequestMapping("/api/expense/report/type/distRange")
public class ExpenseReportTypeDistRangeController {
    private final ExpenseReportTypeDistRangeService expenseReportTypeDistRangeService;

    public ExpenseReportTypeDistRangeController(ExpenseReportTypeDistRangeService expenseReportTypeDistRangeService){
        this.expenseReportTypeDistRangeService = expenseReportTypeDistRangeService;
    }
}
