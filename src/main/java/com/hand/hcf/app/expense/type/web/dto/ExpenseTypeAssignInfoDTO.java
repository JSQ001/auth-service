package com.hand.hcf.app.expense.type.web.dto;

import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@Data
public class ExpenseTypeAssignInfoDTO implements Serializable {

    private List<ExpenseTypeAssignUser> assignUsers;

    private List<ExpenseTypeAssignCompany> assignCompanies;

    private Boolean allCompanyFlag;

    private Integer applyType;
}
