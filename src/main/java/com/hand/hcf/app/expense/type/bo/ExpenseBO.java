package com.hand.hcf.app.expense.type.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 *  费用体系查询对象
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseBO {
    private Integer typeFlag;
    private Long setOfBooksId;
    private Long companyId;
    private Long employeeId;
    private Long departmentId;
    private List<Long> assignTypeIds;
    private Long categoryId;
    private String expenseTypeName;
    private Boolean allFlag;
}
