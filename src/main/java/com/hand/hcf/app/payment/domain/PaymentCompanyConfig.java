package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@ApiModel(description = "付款公司配置实体类")
@TableName("csh_payment_company_config")
@Data
public class PaymentCompanyConfig extends DomainLogicEnable implements Serializable{

    private static final long serialVersionUID = -3510564181192108767L;

    //账套id
    @ApiModelProperty(value = "帐套id")
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //优先级
    @ApiModelProperty(value = "优先级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer priorty;

    //单据类别
    @ApiModelProperty(value = "单据类别")
    @TableField("ducument_category")
    private String ducumentCategory;

    @ApiModelProperty(value = "公司id")
    @TableField("company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    //单据类型id
    @ApiModelProperty(value = "单据类型id")
    @TableField("ducument_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ducumentTypeId;

    @ApiModelProperty(value = "付款公司id")
    @TableField("payment_company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentCompanyId;

}
