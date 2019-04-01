package com.hand.hcf.app.expense.common.dto;

import com.hand.hcf.app.expense.common.utils.BudgetCheckConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/28
 */
@Data
public class BudgetCheckResultDTO implements Serializable {
    /**
     * 通过标记
     */
    private Boolean passFlag;
    /**
     * 校验返回码
     */
    private String code;
    /**
     * 校验说明信息
     */
    private String message;


    private Boolean budgetErEnabled;

    public static BudgetCheckResultDTO ok(){
        BudgetCheckResultDTO result = new BudgetCheckResultDTO();
        result.setBudgetErEnabled(true);
        result.setCode(BudgetCheckConstant.SUCCESS);
        result.setPassFlag(true);
        return result;
    }

    public static BudgetCheckResultDTO error( String code,String message,Boolean budgetErEnabled){
        BudgetCheckResultDTO budgetCheckResultDTO = new BudgetCheckResultDTO();
        budgetCheckResultDTO.setPassFlag(false);
        budgetCheckResultDTO.setCode(code);
        budgetCheckResultDTO.setMessage(message);
        budgetCheckResultDTO.setBudgetErEnabled(budgetErEnabled);
        return budgetCheckResultDTO;
    }

}
