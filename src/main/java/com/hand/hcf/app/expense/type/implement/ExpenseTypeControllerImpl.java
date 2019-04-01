package com.hand.hcf.app.expense.type.implement;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.ExpenseTypeCO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/19
 */
@RestController
public class ExpenseTypeControllerImpl {
    @Autowired
    private ExpenseTypeService expenseTypeService;

    public ExpenseTypeCO getById(@PathVariable("id") Long id){
        ExpenseType expenseType = expenseTypeService.selectById(id);
        return expenseType2ExpenseTypeCO(expenseType);
    }

    public List<ExpenseTypeCO> listByIds(@RequestBody List<Long> ids){
        List<ExpenseType> list = expenseTypeService.selectBatchIds(ids);
        return expenseType2ExpenseTypeCO(list);
    }

    public ExpenseTypeCO getByCodeAndTypeFlagAndSetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                             @RequestParam("typeFlag") Integer typeFlag,
                                                             @RequestParam("code") String code,
                                                             @RequestParam(value = "enabled",required = false) Boolean enabled){
        ExpenseType expenseType = expenseTypeService.selectOne(new EntityWrapper<ExpenseType>()
                .eq("set_of_books_id", setOfBooksId)
                .eq("type_flag", typeFlag)
                .eq("code", code)
                .eq(enabled != null, "enabled", enabled));
        return expenseType2ExpenseTypeCO(expenseType);
    }

    public List<ExpenseTypeCO> listBySetOfBooksIdConditionByEnabled(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                    @RequestParam(value = "enabled",required = false) Boolean enabled){
        List<ExpenseType> list = expenseTypeService.selectList(new EntityWrapper<ExpenseType>()
                .eq("set_of_books_id", setOfBooksId)
                .eq(enabled != null, "enabled", enabled));
        return expenseType2ExpenseTypeCO(list);
    }

    private List<ExpenseTypeCO> expenseType2ExpenseTypeCO(List<ExpenseType> list){
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<ExpenseTypeCO> coList = list.stream().map(e -> {
            ExpenseTypeCO typeCO = new ExpenseTypeCO();
            typeCO.setId(e.getId());
            typeCO.setCode(e.getCode());
            typeCO.setName(e.getName());
            typeCO.setTypeCode(Integer.valueOf(1).equals(e.getTypeFlag()) ? "EXPENSE_TYPE" : "APPLICATION_TYPE");
            return typeCO;
        }).collect(Collectors.toList());
        return coList;
    }

    private ExpenseTypeCO expenseType2ExpenseTypeCO(ExpenseType expenseType){
        if (expenseType == null){
            return null;
        }
        ExpenseTypeCO typeCO = new ExpenseTypeCO();
        typeCO.setId(expenseType.getId());
        typeCO.setCode(expenseType.getCode());
        typeCO.setName(expenseType.getName());
        typeCO.setTypeCode(Integer.valueOf(1).equals(expenseType.getTypeFlag()) ? "EXPENSE_TYPE" : "APPLICATION_TYPE");
        return typeCO;
    }
}
