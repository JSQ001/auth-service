package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeCompany;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeCompanyService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/26
 */
@Api(tags = "报账单类型关联公司控制器")
@RestController
@RequestMapping("/api/expense/report/type/company")
public class ExpenseReportTypeCompanyController {
    private final ExpenseReportTypeCompanyService expenseReportTypeCompanyService;

    public ExpenseReportTypeCompanyController(ExpenseReportTypeCompanyService expenseReportTypeCompanyService){
        this.expenseReportTypeCompanyService = expenseReportTypeCompanyService;
    }

    /**
     * 批量新增 报账单类型关联公司表
     *
     * @param list
     * @return
     */

    @PostMapping("/batch")
    @ApiOperation(value = "批量新增 报账单类型关联公司表", notes = "批量新增 报账单类型关联公司表 开发:xue.han")
    public ResponseEntity<List<ExpenseReportTypeCompany>> createExpenseReportTypeCompanyBatch(@ApiParam(value = "账套ID") @RequestBody List<ExpenseReportTypeCompany> list){
        return ResponseEntity.ok(expenseReportTypeCompanyService.createExpenseReportTypeCompanyBatch(list));
    }

    /**
     * 单个修改 报账单类型关联公司表
     *
     * @param expenseReportTypeCompany
     * @return
     */

    @PutMapping()
    @ApiOperation(value = "批量修改报账单类型关联公司", notes = "批量修改报账单类型关联公司 开发:xue.han")
    public ResponseEntity<ExpenseReportTypeCompany> updateExpenseReportTypeCompany(@ApiParam(value = "报账单类型关联公司表") @RequestBody ExpenseReportTypeCompany expenseReportTypeCompany){
        return ResponseEntity.ok(expenseReportTypeCompanyService.updateExpenseReportTypeCompany(expenseReportTypeCompany));
    }

    /**
     * 根据报账单类型ID->reportTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param reportTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/query")
    @ApiOperation(value = "【报账单类型关联公司】分页查询", notes = "【报账单类型关联公司】分页查询 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportTypeCompany>> getExpenseReportTypeCompanyByCond(
            @ApiParam(value = "报账类型ID") @RequestParam(value = "reportTypeId") Long reportTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportTypeCompany> list = expenseReportTypeCompanyService.getExpenseReportTypeCompanyByCond(reportTypeId,page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param reportTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/filter")
    @ApiOperation(value = "分配页面的公司筛选查询", notes = "分配页面的公司筛选查询 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CompanyCO>> assignCompanyQuery(@ApiParam(value = "报账单类型ID") @RequestParam Long reportTypeId,
                                             @ApiParam(value = "公司代码") @RequestParam(required = false) String companyCode,
                                             @ApiParam(value = "公司名称") @RequestParam(required = false) String companyName,
                                             @ApiParam(value = "公司代码从") @RequestParam(required = false) String companyCodeFrom,
                                             @ApiParam(value = "公司代码到") @RequestParam(required = false) String companyCodeTo,
                                                              @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = expenseReportTypeCompanyService.assignCompanyQuery(reportTypeId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);
        HttpHeaders headers = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
