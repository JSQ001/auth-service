package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeRequestDTO;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
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
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/22
 */
/**
 * @apiDefine ReportTypeService 报账单类型
 */
@RestController
@RequestMapping("/api/expense/report/type")
@Api(tags = "报账单类型控制器")
public class ExpenseReportTypeController {

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    /**
     * 新增 报账单类型
     *
     * @param expenseReportTypeRequestDTO
     * @return
     */

    @PostMapping
    @ApiOperation(value = "新增 报账单类型", notes = "新增 报账单类型 开发:xue.han")
    public ResponseEntity<ExpenseReportType> createExpenseReportType(@ApiParam(value = "账套ID") @Valid @RequestBody @NotNull ExpenseReportTypeRequestDTO expenseReportTypeRequestDTO){
        return ResponseEntity.ok(expenseReportTypeService.createExpenseReportType(expenseReportTypeRequestDTO));
    }

    /**
     * 修改 报账单类型
     *
     * @param expenseReportTypeRequestDTO
     * @return
     */

    @PutMapping
    @ApiOperation(value = "修改 报账单类型", notes = "修改 报账单类型 开发:xue.han")
    public ResponseEntity<ExpenseReportType> updateExpenseReportType(@ApiParam(value = "报账单类型") @RequestBody ExpenseReportTypeRequestDTO expenseReportTypeRequestDTO){
        return ResponseEntity.ok(expenseReportTypeService.updateExpenseReportType(expenseReportTypeRequestDTO));
    }

    /**
     * 根据ID查询 报账单类型
     *
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询 报账单类型", notes = "根据ID查询 报账单类型 开发:xue.han")
    public ResponseEntity<ExpenseReportTypeRequestDTO> getExpenseReportType(@PathVariable Long id){
        return ResponseEntity.ok(expenseReportTypeService.getExpenseReportType(id));
    }

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
    @ApiOperation(value = "自定义条件分页查询 报账单类型", notes = "自定义条件分页查询 报账单类型 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "报账单类型代码") @RequestParam(value = "reportTypeCode", required = false) String reportTypeCode,
            @ApiParam(value = "报账单类型名称") @RequestParam(value = "reportTypeName", required = false) String reportTypeName,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportType> list = expenseReportTypeService.getExpenseReportTypeByCond(setOfBooksId,reportTypeCode,reportTypeName,enabled,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * 获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类)
     * @param setOfBooksId 账套ID
     * @param range 查询范围(全部：all；已选：selected；未选：notChoose)
     * @param reportTypeId 报账单类型ID
     * @param code 付款用途代码
     * @param name 付款用途名称
     * @param pageable 分页信息
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/query/transaction/class")
    @ApiOperation(value = "获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类)", notes = "获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类) 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashTransactionClassCO>> getTransactionClassForExpenseReportType(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId") Long setOfBooksId,
            @ApiParam(value = "查询范围") @RequestParam(value = "range") String range,
            @ApiParam(value = "报账单类型ID") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiParam(value = "付款用途代码") @RequestParam(value = "code",required = false) String code,
            @ApiParam(value = "付款用途名称") @RequestParam(value = "name",required = false) String name,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionClassCO> result = expenseReportTypeService.getTransactionClassForExpenseReportType(setOfBooksId,range,reportTypeId,code,name,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(),httpHeaders,HttpStatus.OK);
    }
    @ApiOperation(value = "获取用户有权限创建的报账单类型", notes = "获取用户有权限创建的报账单类型 修改： 谢宾")
    @GetMapping("/owner/all")
    public ResponseEntity<List<ExpenseReportType>> getCurrentUserExpenseReportType(
            @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag){
        List<ExpenseReportType> result = expenseReportTypeService.getCurrentUserExpenseReportType(authFlag);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据报账类型获取明细配置
     * @param expenseReportTypeId
     * @param headerId
     * @return
     */

    @GetMapping("/properties/detail")
    @ApiOperation(value = "根据报账单类型获取明细配置", notes = "根据报账单类型获取明细配置 修改： 张开")
    public ResponseEntity<ExpenseReportTypeDTO> getExpenseReportHeaderDimensions(@ApiParam(value = "报账单类型ID") @RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                                                 @ApiParam(value = "单据头ID") @RequestParam(value = "headerId",required = false) Long headerId){
        ExpenseReportTypeDTO expenseReportTypeDTO = expenseReportTypeService.getExpenseReportType(expenseReportTypeId, headerId);
        return ResponseEntity.ok(expenseReportTypeDTO);
    }

    /**
     * 获取报账单费用类型 (部分类型)
     * @param expenseReportTypeId
     * @param pageable
     * @return
     */

    @GetMapping("/section/expense/type")
    @ApiOperation(value = "根据报账单类型获取明细配置", notes = "根据报账单类型获取明细配置 修改： 张开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseType>> getExpenseTypesById(@ApiParam(value = "费用类型ID") @RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                                 @ApiParam(value = "员工ID") @RequestParam("employeeId") Long employeeId,
                                                                 @ApiParam(value = "公司ID") @RequestParam("companyId") Long companyId,
                                                                 @ApiParam(value = "部门ID") @RequestParam("departmentId") Long departmentId,
                                                                 @ApiParam(value = "单据大类ID") @RequestParam(value = "typeCategoryId",required = false) Long typeCategoryId,
                                                                 @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                                                 @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> expenseReportTypeExpenseType =
                expenseReportTypeService.getExpenseReportTypeExpenseType(expenseReportTypeId,employeeId,companyId, departmentId, typeCategoryId, name, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseReportTypeExpenseType,totalHeader,HttpStatus.OK);
    }

    /**
     * 获取部分现金事务分类信息
     * @param expenseReportTypeId
     * @param code
     * @param name
     * @param pageable
     * @return
     */

    @GetMapping("/section/cash/transaction/class")
    @ApiOperation(value = "获取部分现金事务分类信息", notes = "获取部分现金事务分类信息 修改： 张开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashTransactionClassCO>> getExpenseReportTypeCashTransactionClasses(@ApiParam(value = "费用类型ID") @RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                                                                   @ApiParam(value = "编码") @RequestParam(value = "code",required = false) String code,
                                                                                                   @ApiParam(value = "名称") @RequestParam(value = "name",required = false) String name,
                                                                                                   @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionClassCO> cashTransactionClassCOPage =
                expenseReportTypeService.getExpenseReportTypeCashTransactionClasses(expenseReportTypeId,code,name, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(cashTransactionClassCOPage,totalHeader,HttpStatus.OK);
    }

    /**
     * {GET} /api/expense/report/type/by/companyId
     * 根据公司id查询报账单类型
     * @param companyId
     * @return
     */
    @GetMapping("/by/companyId")
    @ApiOperation(value = "根据公司id查询报账单类型", notes = "根据公司id查询报账单类型 修改： 陈志鹏")
    public ResponseEntity getExpenseReprotTypeByCompanyId(@ApiParam(value = "公司ID") @RequestParam(value = "companyId") Long companyId){
        List<ExpenseReportType> reportTypes = expenseReportTypeService.getExpenseReprotTypeByCompanyId(companyId);
        return ResponseEntity.ok(reportTypes);
    }

    @GetMapping("/queryByformTypes")
    @ApiOperation(value = "根据表格类型获取费用报账类型", notes = "根据表格类型获取费用报账类型 修改： hao.yi")
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByFormTypes(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "表格类型") @RequestParam(value = "formTypes",required = false)List<Long> formTypes
            ) throws URISyntaxException {
        List<ExpenseReportType> list = expenseReportTypeService.getExpenseReportTypeByFormTypes(setOfBooksId,formTypes);

        return ResponseEntity.ok(list);
    }


    @GetMapping("/users")
    @ApiOperation(value = "根据单据类型id查询有该单据权限的用户", notes = "根据单据类型id查询有该单据权限的用户 修改： 成寿庭")
    public ResponseEntity listUsersByApplicationType(@ApiParam(value = "报账类型ID") @RequestParam(value = "expenseReportTypeId") Long expenseReportTypeId,
                                                     @ApiParam(value = "用户编码") @RequestParam(value = "userCode", required = false) String userCode,
                                                     @ApiParam(value = "用户名称") @RequestParam(value = "userName", required = false) String userName,
                                                     @ApiParam(value = "当前页") @RequestParam(defaultValue = "0") int page,
                                                     @ApiParam(value = "每页多少条") @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = expenseReportTypeService.listUsersByExpenseReportType(expenseReportTypeId, userCode, userName, queryPage);

        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
