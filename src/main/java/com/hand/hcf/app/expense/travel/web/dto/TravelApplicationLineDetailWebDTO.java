package com.hand.hcf.app.expense.travel.web.dto;

import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import lombok.Data;

@Data
public class TravelApplicationLineDetailWebDTO extends TravelApplicationLineDetail {
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

    private String expenseTypeName;

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
}
