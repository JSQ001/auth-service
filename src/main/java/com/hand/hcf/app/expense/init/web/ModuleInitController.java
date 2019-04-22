package com.hand.hcf.app.expense.init.web;

import com.hand.hcf.app.expense.application.service.ApplicationTypeService;
import com.hand.hcf.app.expense.init.dto.*;
import com.hand.hcf.app.expense.policy.service.ExpensePolicyService;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.service.ExpenseTypeAssignCompanyService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeAssignUserService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private ApplicationTypeService applicationTypeService;

    @Autowired
    private TravelApplicationTypeService travelApplicationTypeService;

    @Autowired
    private ExpenseTypeAssignCompanyService expenseTypeAssignCompanyService;
    @Autowired
    private ExpenseTypeAssignUserService expenseTypeAssignUserService;

    @Autowired
    private ExpensePolicyService expensePolicyService;
    /**
     * 申请类型/费用类型导入
     * @param expenseTypeInitDTOS
     * @return
     */
    @PostMapping(value = "/expenseType", produces = "application/json")
    @ApiOperation(value = "申请类型/费用类型导入", notes = "申请类型/费用类型导入 开发:赵柱")
    public ResponseEntity initExpenseType(@RequestBody List<ExpenseTypeInitDTO> expenseTypeInitDTOS) {
        return ResponseEntity.ok(expenseTypeService.initExpenseType(expenseTypeInitDTOS));
    }

    /**
     * 申请类型/费用类型分配公司导入
     * @param expenseTypeAssignCompanyInitDTOS
     * @return
     */
    @PostMapping(value = "/expenseTypeAssignCompany", produces = "application/json")
    @ApiOperation(value = "申请类型/费用类型分配公司导入", notes = "申请类型/费用类型分配公司导入 开发:赵柱")
    public ResponseEntity initExpenseTypeAssignCompany(@RequestBody List<ExpenseTypeAssignCompanyInitDTO> expenseTypeAssignCompanyInitDTOS) {
        return ResponseEntity.ok(expenseTypeAssignCompanyService.initExpenseTypeAssignCompany(expenseTypeAssignCompanyInitDTOS));
    }

    /**
     * 费用政策导入
     * @param expensePolicyInitDTOS
     * @return
     */
    @PostMapping(value = "/expensePolicy", produces = "application/json")
    @ApiOperation(value = "费用政策导入导入", notes = "费用政策导入导入 开发:赵柱")
    public ResponseEntity initExpensePolicy(@RequestBody List<ExpensePolicyInitDTO> expensePolicyInitDTOS) {
        return ResponseEntity.ok(expensePolicyService.initExpensePolicy(expensePolicyInitDTOS));
    }

    /**
     * 申请类型/费用类型适用人员导入
     * @param expenseTypeAssignUserInitDTOS
     * @return
     */
    @PostMapping(value = "/expenseTypeAssignUser", produces = "application/json")
    @ApiOperation(value = "申请类型/费用类型适用人员导入", notes = "申请类型/费用类型适用人员导入 开发:赵柱")
    public ResponseEntity initExpenseTypeAssignUser(@RequestBody List<ExpenseTypeAssignUserInitDTO> expenseTypeAssignUserInitDTOS) {
        return ResponseEntity.ok(expenseTypeAssignUserService.initExpenseTypeAssignUser(expenseTypeAssignUserInitDTOS));
    }

    @PutMapping("/expenseReportTypeExpenseType")
    @ApiOperation(value = "报账单类型关联费用类型", notes = "报账单类型关联费用类型 开发:20855")
    public String expExpenseReportTypeExpenseType(@ApiParam(value = "报账单关联费用类型") @RequestBody List<ModuleInitDTO> moduleInitDTOList) {
        return expenseReportTypeService.expExpenseReportTypeExpenseType(moduleInitDTOList);
    }

    @ApiOperation(value = "费用申请单关联申请类型", notes = "费用申请单关联申请类型 开发:20855")
    @PutMapping("/expenseApplicationTypeApplicationType")
    public String expApplicationTypeApplicationType(@ApiParam(value = "费用申请单关联申请类型") @RequestBody List<ModuleInitDTO> moduleInitDTOList) {
        return applicationTypeService.expApplicationTypeApplicationType(moduleInitDTOList);
    }

    @ApiOperation(value = "差旅申请单关联申请类型", notes = "差旅申请单关联申请类型 开发:20855")
    @PutMapping("/travelApplicationTypeApplicationType")
    public String expTravelApplicationTypeApplicationType(@ApiParam(value = "差旅申请单关联申请类型") @RequestBody List<ModuleInitDTO> moduleInitDTOList) {
        return travelApplicationTypeService.expTravelApplicationTypeApplicationType(moduleInitDTOList);
    }


}
