package com.hand.hcf.app.expense.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 单据退回DTO
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/8
 */
@ApiModel(description = "单据退回DTO")
@Data
public class rejectSignReportsDTO {

    @ApiModelProperty(value = "单据退回方式")
    private String rejectType;

    @ApiModelProperty(value = "单据退回原因")
    private String rejectReason;

    @ApiModelProperty(value = "报账单头id集合")
    private List<Long> expenseReportHeaderIdList;
}
