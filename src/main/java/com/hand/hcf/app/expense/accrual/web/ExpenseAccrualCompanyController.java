package com.hand.hcf.app.expense.accrual.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualCompany;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualCompanyService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description: 费用预提单分配公司
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Api(tags = "费用预提单分配公司")
@RestController
@RequestMapping("/api/expense/accrual/type/assign/companies")
public class ExpenseAccrualCompanyController {

    @Autowired
    private ExpenseAccrualCompanyService expenseAccrualCompanyService;

    /**
     * 批量新增 费用预提单类型关联的公司表
     *
     * @param list
     * @return
     */
    @ApiOperation(value = "批量新增 费用预提单类型关联的公司表", notes = "批量新增 费用预提单类型关联的公司表 开发:liguo.zhao")
    @PostMapping(value = "/batch")
    public ResponseEntity<List<ExpenseAccrualCompany>> createExpenseAccrualTypeAssignCompanyBatch(
            @ApiParam(value = "费用预提类型关联公司") @RequestBody List<ExpenseAccrualCompany> list){
        return ResponseEntity.ok(expenseAccrualCompanyService.createExpenseAccrualTypeAssignCompanyBatch(list));
    }

    /**
     * 批量更新 费用预提单类型关联的公司表
     *
     * @param list
     * @return
     */
    @ApiOperation(value = "批量更新 费用预提单类型关联的公司表", notes = "批量更新 费用预提单类型关联的公司表 开发:liguo.zhao")
    @PutMapping(value = "/batch")
    public ResponseEntity<Boolean> updateExpenseAccrualTypeAssignCompanyBatch(
            @ApiParam(value = "费用预提类型关联公司") @RequestBody List<ExpenseAccrualCompany> list){
        return ResponseEntity.ok(expenseAccrualCompanyService.updateCompanyEnbaled(list));
    }
    /**
     * 根据费用预提单类型ID->expAccrualTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param expAccrualTypeId
     * @param enableFlag
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "查询关联公司", notes = "查询关联公司 开发:liguo.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping(value = "/query")
    public ResponseEntity<List<ExpenseAccrualCompany>> getExpenseAccrualTypeAssignCompanyByCond(
            @ApiParam(value = "费用预提类型ID") @RequestParam(value = "expAccrualTypeId") Long expAccrualTypeId,
            @ApiParam(value = "启用标志") @RequestParam(value = "enableFlag",required = false) Boolean enableFlag,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAccrualCompany> list = expenseAccrualCompanyService
                .getExpenseAccrualTypeAssignCompanyByCond(expAccrualTypeId,enableFlag,page);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link","/api/expense/accrual/type/assign/companies/query");
        return  new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param expAccrualTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "分配页面的公司筛选查询", notes = "分配页面的公司筛选查询 开发:liguo.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping(value = "/filter")
    public ResponseEntity<List<CompanyCO>> assignCompanyQuery(
            @ApiParam(value = "费用预提类型ID") @RequestParam Long expAccrualTypeId,
            @ApiParam(value = "公司编号") @RequestParam(required = false) String companyCode,
            @ApiParam(value = "公司名称") @RequestParam(required = false) String companyName,
            @ApiParam(value = "公司编号从") @RequestParam(required = false) String companyCodeFrom,
            @ApiParam(value = "公司编号到") @RequestParam(required = false) String companyCodeTo,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> list = expenseAccrualCompanyService.assignCompanyQuery(expAccrualTypeId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link","/api/expense/accrual/type/assign/companies/filter");
        return  new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }
}
