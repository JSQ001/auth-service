package com.hand.hcf.app.expense.invoice.domain.temp;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @description:
 * @version: 1.0
 * @author: shuqiang.luo@hand-china.com
 * @date: 2019/4/12
 */
@ApiModel(description = "发票费用映射规则临时表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("inv_exp_type_rules_temp")
public class InvoiceExpenseTypeRulesTemp extends DomainEnable {

    @ApiModelProperty(value = "版本批次号")
    private String batchNumber;

    @ApiModelProperty(value = "账套id")
    private Long setOfBooksId;

    @ApiModelProperty(value = "错误标识")
    private Boolean errorFlag;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "行序号")
    private String rowNumber;

    @ApiModelProperty(value = "货物名称")
    private String goodsName;

    @ApiModelProperty(value = "费用类型代码")
    private String expenseTypeCode;

    @ApiModelProperty(value = "状态")
    private String enabledStr;

    @ApiModelProperty(value = "有效日期从")
    @TableField("start_date")
    private ZonedDateTime startDateTime;

    @ApiModelProperty(value = "有效日期至")
    @TableField("end_date")
    private ZonedDateTime endDateTime;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;


    /**
     * 有效日期从
     */
    @TableField(exist = false)
    private String startDate;

    /**
     * 有效日期至
     */
    @TableField(exist = false)
    private String endDate;
}
