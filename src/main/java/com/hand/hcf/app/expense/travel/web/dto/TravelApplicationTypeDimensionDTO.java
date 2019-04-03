package com.hand.hcf.app.expense.travel.web.dto;

import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;
@Data
public class TravelApplicationTypeDimensionDTO extends TravelApplicationType {

    private List<ExpenseDimension> dimensions;
}
