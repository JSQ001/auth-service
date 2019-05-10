package com.hand.hcf.app.expense.travel.web.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@ApiModel(description = "差旅申请单头")
@Data
public class TravelApplicationHeaderWebDTO  extends TravelApplicationHeader {

    /**
     * 维度信息
     */
    @ApiModelProperty(value = "维度")
    private List<ExpenseDimension> dimensions;

    @ApiModelProperty(value = "附件OID")
    private List<String> attachmentOidList;

    @ApiModelProperty(value = "附件")
    private List<AttachmentCO> attachments;

    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "员工名称")
    private String employeeName;
    @ApiModelProperty(value = "类型名称")
    private String typeName;
    @ApiModelProperty(value = "创建者名称")
    private String createdName;
    @ApiModelProperty(value = "订票名称")
    private String orderName;


    /**
     * 出发地
     */
    @ApiModelProperty(value = "出发地")
    private List<TravelPlaceDTO>  travelFromPlaceDTOS;

    /**
     * 目的地
     */
    @ApiModelProperty(value = "目的地")
    private List<TravelPlaceDTO>  travelToPlaceDTOS;

    /**
     * 出行人员
     */
    @ApiModelProperty(value = "出行人员")
    private List<TravelPeopleDTO>  travelPeopleDTOList;
}
