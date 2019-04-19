package com.hand.hcf.app.expense.travel.web.dto;

import com.hand.hcf.app.expense.travel.domain.TravelApplicationLine;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
@Data
public class TravelApplicationLineWebDTO extends TravelApplicationLine {

    /**
     * 维度信息
     */
    private List<ExpenseDimension> dimensions;

    private String companyName;
    private String departmentName;
    /**
     * 订票人名称
     */
    private String bookerName;
    /**
     * 出行人
     */
    private String travelPeopleStr;

    /**
     * 责任中心名称
     */
    private String responsibilityCenterCodeName;

    private List<ExpenseFieldDTO> fields;

    private String expenseTypeName;
    private Integer index;
    private String dimension1Name;
    private String dimension2Name;
    private String dimension3Name;
    private String dimension4Name;
    private String dimension5Name;
    private String dimension6Name;
    private String dimension7Name;
    private String dimension8Name;
    private String dimension9Name;
    private String dimension10Name;
    private String dimension11Name;
    private String dimension12Name;
    private String dimension13Name;
    private String dimension14Name;
    private String dimension15Name;
    private String dimension16Name;
    private String dimension17Name;
    private String dimension18Name;
    private String dimension19Name;
    private String dimension20Name;

    /**
     * 出行人员
     */
    private List<TravelPeopleDTO>  travelPeopleDTOList;
    private List<TravelApplicationLineDetailWebDTO> children;
}
