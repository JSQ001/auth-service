package com.hand.hcf.app.expense.adjust.implement.web;

import com.hand.hcf.app.apply.mdata.ApplyAdjustInterface;
import com.hand.hcf.app.apply.expense.ExpenseAdjustInterface;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *     费用调整单对外接口实现
 * </p>
 *
 * @Author: shouting.cheng
 * @Date: 2019/02/21
 */
@RestController
public class AdjustControllerImpl implements ExpenseAdjustInterface, ApplyAdjustInterface {


    @Autowired
    private ExpenseAdjustTypeService adjustTypeService;

    @Override
    public String getFormTypeNameByFormTypeId(@RequestParam("id") Long id) {
        ExpenseAdjustType expenseAdjustType = adjustTypeService.selectById(id);
        return expenseAdjustType != null ? expenseAdjustType.getExpAdjustTypeName() : null;
    }
}
