package com.hand.hcf.app.expense.accrual.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssign;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssignDepartment;
import com.hand.hcf.app.expense.accrual.dto.ExpenseReportAccrualRequestDTO;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualService;
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
@Api(tags = "费用收入类型")
@RestController
@RequestMapping("/api/expense/accrual")
public class ExpenseAccrualController {
    @Autowired
    private ExpenseAccrualService expenseAccrualService;

    /**
     * 自定义条件查询 预提报账单类型(分页)
     *
     * @param setOfBooksId
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    @ApiOperation(value = "自定义条件分页查询 预提报账单类型", notes = "自定义条件分页查询 报账单类型 开发:dazhuang.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseAccrualAssign>> getExpenseReportTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
            @ApiParam(value = "申请单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "申请单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
            @ApiParam(value = "是否启用")  @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiIgnore Pageable pageable
    ) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
       // List<ExpenseAccrualAssign> list = expenseAccrualService.getExpenseReportTypeByCond(setOfBooksId,reportTypeCode,reportTypeName,enabled,page);

        List<ExpenseAccrualAssign> list = expenseAccrualService.getExpenseReportTypeByCond(setOfBooksId, typeCode, typeName,enabled, page,true);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }




    /**
     * 根据ID查询 报账单类型
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询 报账单类型", notes = "根据ID查询 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpenseReportAccrualRequestDTO> getExpenseReportType(@PathVariable Long id){
        return ResponseEntity.ok(expenseAccrualService.getExpenseReportAccrual(id));
    }

    /**
     * 新增 报账单类型
     *
     * @param expenseReportAccrualRequestDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增 报账单类型", notes = "新增 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpenseAccrualAssign> createExpenseReportAccrual(@ApiParam(value = "账套ID") @Valid @RequestBody ExpenseReportAccrualRequestDTO expenseReportAccrualRequestDTO){
        return ResponseEntity.ok(expenseAccrualService.createExpenseReportAccrual(expenseReportAccrualRequestDTO));
    }

    /**
     * 编辑 报账单类型
     *
     * @param expenseReportAccrualRequestDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "编辑 报账单类型", notes = "编辑 报账单类型 开发:dazhuang.xie")
    public ResponseEntity<ExpenseAccrualAssign> updateExpenseReportIncome(@ApiParam(value = "账套ID") @Valid @RequestBody @NotNull ExpenseReportAccrualRequestDTO expenseReportAccrualRequestDTO){
        return ResponseEntity.ok(expenseAccrualService.updateExpenseReportAccrual(expenseReportAccrualRequestDTO));
    }


    @GetMapping("/get/department/info")
    public ResponseEntity<List<ExpenseAccrualAssignDepartment>> getDepartmentInfo(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiIgnore Pageable pageable
    )throws URISyntaxException {
        return expenseAccrualService.getExpenseReportTypeDepartmentByCond(reportTypeId,pageable);
    }

    @GetMapping("/get/department/filter")
    public ResponseEntity<List<ExpenseAccrualAssignDepartment>> getDepartmentfilter(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiParam(value = "部门代码") @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @ApiParam(value = "部门名称") @RequestParam(value = "name",required = false) String name,
            @ApiParam(value = "部门代码从") @RequestParam(value = "departmentFrom",required = false) String departmentFrom,
            @ApiParam(value = "部门代码至") @RequestParam(value = "departmentTo",required = false) String departmentTo,
            @ApiIgnore Pageable pageable
    )throws URISyntaxException {
        return expenseAccrualService.getExpenseDepartmentFilter(reportTypeId,departmentCode,name,departmentFrom,departmentTo,pageable);
    }

    @PostMapping("/distribution/department")
    @ApiOperation(value = "批量分配部门", notes = "批量分配部门 xiedazhuang")
    public void createExpenseReportTypeCompanyBatch(
            @ApiParam(value = "部门实体") @RequestBody List<ExpenseAccrualAssignDepartment> list){
        expenseAccrualService.distributionDepartment(list);
    }


    @PostMapping("/change/department/status")
    @ApiOperation(value = "更改部门状态", notes = "更改部门状态 xiedazhuang")
    public void changeDepartmentStatus(
            @ApiParam(value = "部门实体") @RequestBody ExpenseAccrualAssignDepartment expenseTypeAssignDepartment){
        expenseAccrualService.changeDepartmentStatus(expenseTypeAssignDepartment);
    }
}
