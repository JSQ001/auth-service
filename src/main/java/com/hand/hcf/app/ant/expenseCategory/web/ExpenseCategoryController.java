package com.hand.hcf.app.ant.expenseCategory.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.expenseCategory.dto.ExpenseCategory;
import com.hand.hcf.app.ant.expenseCategory.service.ExpenseCategoryService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "费用小类")
@RestController
@RequestMapping("/api/expense/category")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @GetMapping("/query/page")
    @ApiOperation(value = "费用小类分页查询", notes = "费用小类分页查询（当前帐套下） 开发:jsq")
    ResponseEntity<List<ExpenseCategory>> queryPages(
            @RequestParam String categoryType,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name, Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseCategory> list = expenseCategoryService.queryPages(code,name,categoryType,page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/category/query/page");
        return new ResponseEntity<List<ExpenseCategory>>(list, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新建费用小类", notes = "新建费用小类 开发:jsq")
    ResponseEntity<ExpenseCategory> create(@ApiParam(value = "费用类小类信息") @RequestBody ExpenseCategory expenseCategory){
        return ResponseEntity.ok(expenseCategoryService.insertOrUpdateExpenseCategory(expenseCategory));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询", notes = "根据ID查询 开发:jsq")
    ResponseEntity<ExpenseCategory> getExpenseCategoryById(@PathVariable Long id){
        return ResponseEntity.ok(expenseCategoryService.selectById(id));
    }


    @GetMapping("/delete/{id}")
    @ApiOperation(value = "根据ID查询", notes = "根据ID查询 开发:jsq")
    ResponseEntity<Boolean> deleteExpenseCategory(@PathVariable Long id){
        return ResponseEntity.ok(expenseCategoryService.deleteById(id));
    }
/**/
}
