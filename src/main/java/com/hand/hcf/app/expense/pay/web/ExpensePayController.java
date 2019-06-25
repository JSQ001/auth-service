package com.hand.hcf.app.expense.pay.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.income.domain.ExpenseIncomeAssign;
import com.hand.hcf.app.expense.income.domain.ExpenseTypeAssignDepartment;
import com.hand.hcf.app.expense.income.dto.ExpenseReportIncomeRequestDTO;
import com.hand.hcf.app.expense.income.service.ExpenseIncomeService;
import com.hand.hcf.app.expense.pay.domain.ExpensePayAssign;
import com.hand.hcf.app.expense.pay.domain.ExpensePayAssignDepartment;
import com.hand.hcf.app.expense.pay.dto.ExpenseReportPayRequestDTO;
import com.hand.hcf.app.expense.pay.service.ExpensePayService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportPay;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: dazhaung.xie
 * @Date: 2019/6/19
 */
@Api(tags = "费用付款类型")
@RestController
@RequestMapping("/api/expense/pay")
public class ExpensePayController {
    @Autowired
    private ExpensePayService expenseIncomeService;

    /**
     * 自定义条件查询 报账单类型(分页)
     *
     * @param setOfBooksId
     * @param reportTypeCode
     * @param reportTypeName
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    @ApiOperation(value = "自定义条件分页查询 报账单类型", notes = "自定义条件分页查询 报账单类型 开发:dazhuang.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpensePayAssign>> getExpenseReportTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "报账单类型代码") @RequestParam(value = "reportTypeCode", required = false) String reportTypeCode,
            @ApiParam(value = "报账单类型名称") @RequestParam(value = "reportTypeName", required = false) String reportTypeName,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpensePayAssign> list = expenseIncomeService.getExpenseReportTypeByCond(setOfBooksId,reportTypeCode,reportTypeName,enabled,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据ID查询 报账单类型
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询 报账单类型", notes = "根据ID查询 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpenseReportPayRequestDTO> getExpenseReportPay(@PathVariable Long id){
        return ResponseEntity.ok(expenseIncomeService.getExpenseReportPay(id));
    }

    /**
     * 新增 报账单类型
     *
     * @param expenseReportPayRequestDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增 报账单类型", notes = "新增 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpensePayAssign> createExpenseReportPay(@ApiParam(value = "账套ID") @Valid @RequestBody @NotNull ExpenseReportPayRequestDTO expenseReportPayRequestDTO){
        return ResponseEntity.ok(expenseIncomeService.createExpenseReportPay(expenseReportPayRequestDTO));
    }

    /**
     * 编辑 报账单类型
     *
     * @param expenseReportPayRequestDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "编辑 报账单类型", notes = "编辑 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpensePayAssign> updateExpenseReportPay(@ApiParam(value = "账套ID") @Valid @RequestBody @NotNull ExpenseReportPayRequestDTO expenseReportPayRequestDTO){
        return ResponseEntity.ok(expenseIncomeService.updateExpenseReportPay(expenseReportPayRequestDTO));
    }


    @GetMapping("/get/department/info")
    public ResponseEntity<List<ExpensePayAssignDepartment>> getDepartmentInfo(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiIgnore Pageable pageable
    )throws URISyntaxException {
        return expenseIncomeService.getExpenseReportTypeDepartmentByCond(reportTypeId,pageable);
    }

    @GetMapping("/get/department/filter")
    public ResponseEntity<List<ExpensePayAssignDepartment>> getDepartmentfilter(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiParam(value = "部门代码") @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @ApiParam(value = "部门名称") @RequestParam(value = "name",required = false) String name,
            @ApiParam(value = "部门代码从") @RequestParam(value = "departmentFrom",required = false) String departmentFrom,
            @ApiParam(value = "部门代码至") @RequestParam(value = "departmentTo",required = false) String departmentTo,
            @ApiIgnore Pageable pageable
    )throws URISyntaxException {
        return expenseIncomeService.getExpenseDepartmentFilter(reportTypeId,departmentCode,name,departmentFrom,departmentTo,pageable);
    }

    @PostMapping("/distribution/department")
    @ApiOperation(value = "批量分配部门", notes = "批量分配部门 dazhuang.xie")
    public void createExpenseReportTypeCompanyBatch(
            @ApiParam(value = "部门实体") @RequestBody List<ExpensePayAssignDepartment> list){
        expenseIncomeService.distributionDepartment(list);
    }


    @PostMapping("/change/department/status")
    @ApiOperation(value = "更改部门状态", notes = "更改部门状态 dazhuang.xie")
    public void changeDepartmentStatus(
            @ApiParam(value = "部门实体") @RequestBody ExpensePayAssignDepartment expenseTypeAssignDepartment){
        expenseIncomeService.changeDepartmentStatus(expenseTypeAssignDepartment);
    }
}
