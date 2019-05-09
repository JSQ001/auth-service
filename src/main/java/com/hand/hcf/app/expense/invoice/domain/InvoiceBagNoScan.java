package com.hand.hcf.app.expense.invoice.domain;

import java.time.ZonedDateTime;

import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author zhuo.zhang
 * @description 发票袋号码扫描(INVOICE_BAG_NO_SCAN)表实体类
 * @date 2019-04-29 16:08:06
 */
@ApiModel(description = "发票袋号码扫描记录")
@TableName(value = "invoice_bag_no_scan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBagNoScan extends Domain {
    /**
     * 发票袋号码
     */
    @ApiModelProperty(value = "发票袋号码")
    @TableField(value = "invoice_bag_no")
    private String invoiceBagNo;

}