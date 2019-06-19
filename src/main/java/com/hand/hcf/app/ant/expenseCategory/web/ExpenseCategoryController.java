package com.hand.hcf.app.ant.expenseCategory.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.expenseCategory.dto.ExpenseCategory;
import com.hand.hcf.app.ant.expenseCategory.service.ExpenseCategoryService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "费用小类")
@RestController
@RequestMapping("/api/expense/category")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @GetMapping("/query/page")
    @ApiOperation(value = "费用小类分页查询", notes = "费用小类分页查询（当前帐套下） 开发:jsq")
    ResponseEntity<List<ExpenseCategory>> queryPages(Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseCategory> list = expenseCategoryService.queryPages(page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/category/query/page");
        return new ResponseEntity<List<ExpenseCategory>>(list, httpHeaders, HttpStatus.OK);
    }
}
