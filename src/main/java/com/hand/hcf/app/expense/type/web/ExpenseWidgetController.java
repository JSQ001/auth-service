package com.hand.hcf.app.expense.type.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.type.domain.ExpenseWidget;
import com.hand.hcf.app.expense.type.service.ExpenseWidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *     申请/费用类别前端控件控制器
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@RestController
@RequestMapping("/api/expense/widgets")
public class ExpenseWidgetController {
    @Autowired
    private ExpenseWidgetService service;

    @GetMapping
    public ResponseEntity<List<ExpenseWidget>> queryAll(){

        return ResponseEntity.ok(service.selectList(new EntityWrapper<ExpenseWidget>().eq("enabled", true)));
    }
}
