package com.hand.hcf.app.ant.accrualExpense.web;

import com.hand.hcf.app.ant.accrualExpense.service.AccrualExpenseTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@RestController
@RequestMapping("/api/expense/accrual/type")
public class AccruedExpensesTypeController {

    private AccrualExpenseTypeService expenseAccrualTypeService;

    /**
     *  获取用户有权限创建的预提单类型
     * @param
     * @return
     */
//    @GetMapping("/owner/all")
//    public ResponseEntity<List<ExpenseReportType>> getCurrentUserExpenseReportType(
//            @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag){
//        List<ExpenseReportType> result = ExpenseAccrualTypeService.getCurrentUserExpenseReportType(authFlag);
//        return ResponseEntity.ok(result);
//    }

//    @GetMapping("/users")
//    @ApiOperation(value = "根据单据类型id查询有该单据权限的用户", notes = "根据单据类型id查询有该单据权限的用户 修改： 成寿庭")
//    public ResponseEntity listUsersByApplicationType(@ApiParam(value = "报账类型ID") @RequestParam(value = "expenseReportTypeId") Long expenseReportTypeId,
//                                                     @ApiParam(value = "用户编码") @RequestParam(value = "userCode", required = false) String userCode,
//                                                     @ApiParam(value = "用户名称") @RequestParam(value = "userName", required = false) String userName,
//                                                     @ApiParam(value = "当前页") @RequestParam(defaultValue = "0") int page,
//                                                     @ApiParam(value = "每页多少条") @RequestParam(defaultValue = "10") int size){
//        Page queryPage = PageUtil.getPage(page, size);
//        List<ContactCO> result = expenseReportTypeService.listUsersByExpenseReportType(expenseReportTypeId, userCode, userName, queryPage);
//
//        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
//        return new ResponseEntity<>(result, headers, HttpStatus.OK);
//    }
}
