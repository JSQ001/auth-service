package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationLine;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
@ApiModel(description = "申请单行表")
@Data
public class ApplicationLineWebDTO extends ApplicationLine {

    /**
     * 维度信息
     */
    @ApiModelProperty(value = "维度信息")
    private List<ExpenseDimension> dimensions;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "责任中心编码名称")
    private String responsibilityCenterCodeName;
    @ApiModelProperty(value = "作用域")
    private List<ExpenseFieldDTO> fields;
    @ApiModelProperty(value = "费用类型名称")
    private String expenseTypeName;
    @ApiModelProperty(value = "指标")
    private Integer index;
    @ApiModelProperty(value = "维度1")
    private String dimension1Name;
    @ApiModelProperty(value = "维度2")
    private String dimension2Name;
    @ApiModelProperty(value = "维度3")
    private String dimension3Name;
    @ApiModelProperty(value = "维度4")
    private String dimension4Name;
    @ApiModelProperty(value = "维度5")
    private String dimension5Name;
    @ApiModelProperty(value = "维度6")
    private String dimension6Name;
    @ApiModelProperty(value = "维度7")
    private String dimension7Name;
    @ApiModelProperty(value = "维度8")
    private String dimension8Name;
    @ApiModelProperty(value = "维度9")
    private String dimension9Name;
    @ApiModelProperty(value = "维度10")
    private String dimension10Name;
    @ApiModelProperty(value = "维度11")
    private String dimension11Name;
    @ApiModelProperty(value = "维度12")
    private String dimension12Name;
    @ApiModelProperty(value = "维度13")
    private String dimension13Name;
    @ApiModelProperty(value = "维度14")
    private String dimension14Name;
    @ApiModelProperty(value = "维度15")
    private String dimension15Name;
    @ApiModelProperty(value = "维度16")
    private String dimension16Name;
    @ApiModelProperty(value = "维度17")
    private String dimension17Name;
    @ApiModelProperty(value = "维度18")
    private String dimension18Name;
    @ApiModelProperty(value = "维度19")
    private String dimension19Name;
    @ApiModelProperty(value = "维度20")
    private String dimension20Name;

    /**
     * 金额录入模式 false-总金额 true-单价*数量
     */
    @ApiModelProperty(value = "金额录入模式")
    private Boolean entryMode;
    @ApiModelProperty(value = "可关闭金额")
    private BigDecimal canCloseAmount;
    @ApiModelProperty(value = "图标路径")
    private String iconUrl;
}
