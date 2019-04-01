package com.hand.hcf.app.expense.travel.web.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;
@Data
public class TravelApplicationHeaderWebDTO  extends TravelApplicationHeader {

    /**
     * 维度信息
     */
    private List<ExpenseDimension> dimensions;

    private List<String> attachmentOidList;

    private List<AttachmentCO> attachments;

    private String companyName;
    private String departmentName;
    private String employeeName;
    private String typeName;
    private String createdName;
    private String orderName;


    /**
     * 出发地
     */
    private List<TravelPlaceDTO>  travelFromPlaceDTOS;

    /**
     * 目的地
     */
    private List<TravelPlaceDTO>  travelToPlaceDTOS;

    /**
     * 出行人员
     */
    private List<TravelPeopleDTO>  travelPeopleDTOList;
}
