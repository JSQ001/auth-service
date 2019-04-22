package com.hand.hcf.app.expense.init.web;

import com.hand.hcf.app.expense.init.dto.ExpenseTypeAssignCompanyInitDTO;
import com.hand.hcf.app.expense.init.dto.ExpenseTypeInitDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 */
@RestController
@RequestMapping("/api/expense/init")
@Api(tags = "初始化数据控制器")
public class ModuleInitController {

    @Autowired
    private ExpenseTypeService expenseTypeService;

    private ExpenseTypeAssignCompany expenseTypeAssignCompany;
    /**
     * 申请类型/费用类型导入
     * @param expenseTypeInitDTOS
     * @return
     */
    @PostMapping("/expensetype")
    public ResponseEntity initExpenseType(@RequestBody List<ExpenseTypeInitDTO> expenseTypeInitDTOS) {
        return ResponseEntity.ok(expenseTypeService.initExpenseType(expenseTypeInitDTOS));
    }

    /**
     * 申请类型/费用类型分配公司导入
     * @param expenseTypeAssignCompanyInitDTOS
     * @return
     */
    @PostMapping("/expensetype/assigncompany")
    public ResponseEntity initExpenseTypeAssignCompany(@RequestBody List<ExpenseTypeAssignCompanyInitDTO> expenseTypeAssignCompanyInitDTOS) {
        //return ResponseEntity.ok(expenseTypeAssignCompany.initExpenseTypeAssignCompany(expenseTypeAssignCompanyInitDTOS));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
