package com.hand.hcf.app.mdata.responsibilityCenter.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *    责任中心lov
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/10
 */
@Data
@ApiModel("责任中心lLov对象")
@AllArgsConstructor
@NoArgsConstructor
public class ResponsibilityLov implements Serializable {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("责任中心代码")
    private String code;
    @ApiModelProperty("责任中心名称")
    private String name;
    @ApiModelProperty("责任中心代码-名称")
    private String codeName;
}
