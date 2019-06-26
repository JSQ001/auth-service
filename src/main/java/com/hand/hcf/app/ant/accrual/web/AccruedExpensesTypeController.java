package com.hand.hcf.app.ant.accrual.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrual.service.AccrualExpenseTypeService;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/accrual/type")
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

    /**
     * 根据单据类型id查询有该单据权限的用户
     * @param expAccrualTypeId
     * @param userCode
     * @param userName
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity listUsersByApplicationType(@RequestParam(value = "accruedTypeId") Long expAccrualTypeId,
                                                     @RequestParam(value = "userCode", required = false) String userCode,
                                                     @RequestParam(value = "userName", required = false) String userName,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = expenseAccrualTypeService.listUsersByAccuralType(expAccrualTypeId, userCode, userName, queryPage);

        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

}
