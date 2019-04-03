package com.hand.hcf.app.demo.book.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.demo.book.domain.ExpenseBookDemo;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.demo.book.service.ExpenseBookDemoService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jiancheng.li@hand-china.com
 * @description  我的账本
 * @date 2019/2/21 14:17
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/api/demo/book")
public class ExpenseBookControllerDemo {

    @Autowired
    private ExpenseBookDemoService expenseBookDemoService;

    @GetMapping("/query")
    public ResponseEntity pageExpenseBookByCond(@RequestParam(value = "expenseTypeId",required = false) Long expenseTypeId,
                                                @RequestParam(value = "dateFrom",required = false) String dateFrom,
                                                @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                                @RequestParam(value = "page",defaultValue = "0") int page,
                                                @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ExpenseBookDemo> expenseBookList = expenseBookDemoService.pageExpenseBookByCond(expenseTypeId, dateFrom, dateTo, amountFrom,amountTo,currencyCode,null,null,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(expenseBookList,httpHeaders, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity insertExpenseBook(@RequestBody ExpenseBookDemo expenseBook){
        return ResponseEntity.ok(expenseBookDemoService.insertExpenseBook(expenseBook));
    }


    @PutMapping
    public ResponseEntity updateExpenseBook(@RequestBody ExpenseBookDemo expenseBook){
        return ResponseEntity.ok(expenseBookDemoService.updateExpenseBook(expenseBook));
    }

    @DeleteMapping
    public void deleteExpenseBook(@RequestParam(value = "expenseBookId",required = false) Long expenseBookId){
        expenseBookDemoService.deleteExpenseBook(expenseBookId);

    }

}
