package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/20
 */
@Data
public class ApplicationTypeDimensionDTO extends ApplicationType {

    private List<ExpenseDimension> dimensions;
}
