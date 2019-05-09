package com.hand.hcf.app.expense.application.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/2/27
 */
@ApiModel(description = "关闭")
@Data
public class ClosedDTO implements Serializable {
    /**
     * 单据头Id
     */
    @ApiModelProperty(value = "单据头Id")
    private List<Long> headerIds;
    /**
     * 原因/意见
     */
    @ApiModelProperty(value = "原因/意见")
    private String messages;
}
