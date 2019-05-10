package com.hand.hcf.app.expense.book.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.service.ExpenseBookService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description  我的账本
 * @date 2019/2/21 14:17
 * @version: 1.0.0
 */
@Api(tags = "我的账本")
@RestController
@RequestMapping("/api/expense/book")
public class ExpenseBookController {

    @Autowired
    private ExpenseBookService expenseBookService;

    @PostMapping
    @ApiOperation(value = "新增我的账本", notes = "新增我的账本 开发:shaofeng.zheng")
    public ResponseEntity insertExpenseBook(@ApiParam(value = "账本明细表") @RequestBody ExpenseBook expenseBook){
        return ResponseEntity.ok(expenseBookService.insertExpenseBook(expenseBook));
    }
    

    @PutMapping
    @ApiOperation(value = "修改我的账本", notes = "修改我的账本 开发:shaofeng.zheng")
    public ResponseEntity updateExpenseBook(@ApiParam(value = "账本明细表") @RequestBody ExpenseBook expenseBook){
        return ResponseEntity.ok(expenseBookService.updateExpenseBook(expenseBook));
    }

    @GetMapping("/query")
    @ApiOperation(value = "分页查询我的账本", notes = "分页查询我的账本 开发:shaofeng.zheng")
    public ResponseEntity pageExpenseBookByCond(@ApiParam(value = "费用类型ID") @RequestParam(value = "expenseTypeId",required = false) Long expenseTypeId,
                                                @ApiParam(value = "日期从") @RequestParam(value = "dateFrom",required = false) String dateFrom,
                                                @ApiParam(value = "日期到") @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                                @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ExpenseBook> expenseBookList = expenseBookService.pageExpenseBookByCond(expenseTypeId, dateFrom, dateTo, amountFrom,amountTo,currencyCode,null,null,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(expenseBookList,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取我的账本", notes = "获取我的账本 开发:shaofeng.zheng")
    public ResponseEntity getExpenseBookById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(expenseBookService.selectById(id));
    }



    @DeleteMapping
    @ApiOperation(value = "删除账本关联", notes = "删除账本关联 开发:shaofeng.zheng")
    public ResponseEntity deleteExpenseBook(@ApiParam(value = "账本ID") @RequestParam(value = "expenseBookId",required = false) Long expenseBookId,
                                            @ApiParam(value = "发票头ID") @RequestParam("invoiceHeadId") Long invoiceHeadId,
                                            @ApiParam(value = "发票行ID") @RequestParam("invoiceLineId") Long invoiceLineId){
        return ResponseEntity.ok(expenseBookService.deleteExpenseBook(expenseBookId, invoiceHeadId, invoiceLineId ));
    }


    @GetMapping("/release")
    @ApiOperation(value = "从账本导入费用", notes = "从账本导入费用 开发:shaofeng.zheng")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getOwnExpenseBooks(@ApiParam(value = "报账单类型ID") @RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                             @ApiParam(value = "币种") @RequestParam("currencyCode") String currencyCode,
                                             @ApiParam(value = "费用类型ID") @RequestParam(required = false) Long expenseTypeId,
                                             @ApiParam(value = "发生日期从") @RequestParam(required = false) String requisitionDateFrom,
                                             @ApiParam(value = "发生日期到") @RequestParam(required = false) String requisitionDateTo,
                                             @ApiParam(value = "备注") @RequestParam(required = false) String remarks,
                                             @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                             @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                             @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseBook> expenseBookList = expenseBookService.pageExpenseBookByCond(expenseTypeId, requisitionDateFrom, requisitionDateTo, amountFrom,amountTo,currencyCode,remarks,expenseReportTypeId,page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseBookList,totalHeader,HttpStatus.OK);
    }

    /**
     * 删除账本信息
     * @param expenseBookId
     * @return
     */
    @ApiOperation(value = "删除账本信息", notes = "删除账本信息 开发:kai.zhang")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteExpenseBook(@ApiParam(value = "账本ID") @PathVariable(value = "id") Long expenseBookId){
        expenseBookService.deleteExpenseBook(expenseBookId);
        return ResponseEntity.ok().build();
    }
}
