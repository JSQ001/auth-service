package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 韩雪 on 2017/9/6.
 */
@ApiModel(description = "现金流量项domain")
@Data
@TableName("csh_cash_flow_item")
public class CashFlowItem extends DomainLogicEnable {
    @ApiModelProperty(value = "帐套ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_book_id")
    private Long setOfBookId;//帐套ID

    @ApiModelProperty(value = "现金流量项代码")
    @TableField(value = "flow_code")
    private String flowCode;//现金流量项代码

    @ApiModelProperty(value = "现金流量项描述")
    @TableField(value = "description")
    private String description;//现金流量项描述
}
