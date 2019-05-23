package com.hand.hcf.app.ant.accrual.web;

import com.hand.hcf.app.ant.accrual.service.AccrualExpenseTypeService;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@RestController
@RequestMapping("/api/expense/accrual/type")
public class AccruedExpensesTypeController {

    @Autowired
    private AccrualExpenseTypeService expenseAccrualTypeService;

    /**
     *  获取用户有权限创建的预提单类型
     * @param
     * @return
     */
    @GetMapping("/owner/all")
    public ResponseEntity<List<ExpenseAccrualType>> getCurrentUserExpenseReportType(
            @RequestParam(required = false, defaultValue = "true") Boolean authFlag){
        List<ExpenseAccrualType> result = expenseAccrualTypeService.getCurrentUserExpenseAccrualType(authFlag);
        return ResponseEntity.ok(result);
    }
}
