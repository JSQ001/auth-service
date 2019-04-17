package com.hand.hcf.app.expense.report.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDistRange;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeDistRangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/3
 */
@Service
@Transactional
public class ExpenseReportTypeDistRangeService extends BaseService<ExpenseReportTypeDistRangeMapper,ExpenseReportTypeDistRange> {

    @Autowired
    private ExpenseReportTypeDistRangeMapper expenseReportTypeDistRangeMapper;


}
