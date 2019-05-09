package com.hand.hcf.app.expense.type.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeCategory;
import com.hand.hcf.app.expense.type.service.ExpenseTypeCategoryService;
import com.hand.hcf.app.expense.type.web.dto.SortBySequenceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/5
 */
@Api(tags = "费用大类控制器")
@RestController
@RequestMapping("/api/expense/types/category")
public class ExpenseTypeCategoryController {

    @Autowired
    private ExpenseTypeCategoryService service;


    @GetMapping
    @ApiOperation(value = "根据账套ID查询所有的费用大类", notes = "根据账套ID查询所有的费用大类 开发:bin.xie")
    public ResponseEntity<List<ExpenseTypeCategory>> queryBySetOfBooksId(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId){
        ExpenseTypeCategory expenseTypeCategory = new ExpenseTypeCategory(setOfBooksId);
        return ResponseEntity.ok(service.selectList(new EntityWrapper<>(expenseTypeCategory).orderBy("sequence",true)));
    }


    @GetMapping("/query")
    @ApiOperation(value = "根据账套ID费用大类(含里面申请类别或者费用类别)", notes = "根据账套ID费用大类(含里面申请类别或者费用类别) 开发:bin.xie")
    public ResponseEntity<List<ExpenseTypeCategory>> queryByCondition(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                      @ApiParam(value = "类型标志") @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag){
        return ResponseEntity.ok(service.listResult(setOfBooksId, typeFlag, false));
    }


    @GetMapping("/query/enable/dataAuth")
    @ApiOperation(value = "根据账套ID费用大类(含里面申请类别或者费用类别)", notes = "根据账套ID费用大类(含里面申请类别或者费用类别) 开发:bin.xie")
    public ResponseEntity<List<ExpenseTypeCategory>> queryByConditionEnableAuth(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                                @ApiParam(value = "类型标志") @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag){
        return ResponseEntity.ok(service.listResult(setOfBooksId, typeFlag, true));
    }



    @PostMapping
    @ApiOperation(value = "创建一个费用大类", notes = "创建一个费用大类 开发:bin.xie")
    public ResponseEntity<Boolean> createTypeCategory(@ApiParam(value = "费用大类") @RequestBody @Validated ExpenseTypeCategory expenseTypeCategory){
        return ResponseEntity.ok(service.createTypeCategory(expenseTypeCategory));
    }


    @PutMapping
    @ApiOperation(value = "更新一个费用大类的名称", notes = "更新一个费用大类的名称 开发:bin.xie")
    public ResponseEntity updateTypeCategory(@ApiParam(value = "费用大类") @RequestBody ExpenseTypeCategory expenseTypeCategory){
        return ResponseEntity.ok(service.updateTypeCategory(expenseTypeCategory));
    }


    @DeleteMapping
    @ApiOperation(value = "根据大类ID删除指定的费用大类", notes = "根据大类ID删除指定的费用大类 开发:bin.xie")
    public ResponseEntity deleteType(@ApiParam(value = "id") @RequestParam("id") Long id){
        return ResponseEntity.ok(service.deleteTypeCategory(id));
    }



    @PostMapping("/sort")
    @ApiOperation(value = "修改费用大类的排序", notes = "修改费用大类的排序 开发:bin.xie")
    public ResponseEntity<Boolean> sort(@ApiParam(value = "排序") @RequestBody List<SortBySequenceDTO> list){

        return ResponseEntity.ok(service.sort(list));
    }
}
