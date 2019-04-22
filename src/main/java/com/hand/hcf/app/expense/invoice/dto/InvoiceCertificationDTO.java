package com.hand.hcf.app.expense.invoice.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票认证
 * @date 2019/4/19 9:28
 * @version: 1.0.0
 */
@ApiModel("发票认证")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCertificationDTO {
    private Long id;
    //发票类型ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发票类型ID")
    private Long invoiceTypeId;

    @ApiModelProperty(value = "发票类型名称")
    private String invoiceTypeName;

    //发票号码
    @NotNull
    @ApiModelProperty(value = "发票号码")
    private String invoiceNo;

    //发票代码
    @NotNull
    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    //开票日期
    @ApiModelProperty(value = "开票日期")
    private ZonedDateTime invoiceDate;

    //金额合计
    @ApiModelProperty(value = "金额合计")
    private BigDecimal invoiceAmount;

    //创建方式
    @NotNull
    @ApiModelProperty(value = "创建方式")
    private String createdMethod;

    @ApiModelProperty(value = "创建方式描述")
    private String CreatedMethodName;

    //认证状态
    @ApiModelProperty(value = "认证状态")
    private String certificationStatus;

    @ApiModelProperty(value = "认证状态描述")
    private String certificationStatusName;

    @ApiModelProperty(value = "认证日期")
    private ZonedDateTime certificationDate;

    @ApiModelProperty(value = "认证失败原因")
    private String certificationReason;
}
