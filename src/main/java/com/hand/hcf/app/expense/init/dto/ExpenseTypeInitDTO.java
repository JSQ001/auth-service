package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 * 申请类型/费用类型初始化DTO
 */
@ApiModel(description = "申请类型/费用类型初始化DTO")
@Data
public class ExpenseTypeInitDTO{
    /**
     * 校验信息
     */
    @ApiModelProperty(value = "校验信息")
    private Map<String, List<String>> resultMap;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 图标名称
     */
    @ApiModelProperty(value = "图标名称")
    private String iconName;
    /**
     * 代码
     */
    @ApiModelProperty(value = "代码")
    private String code;
    /**
     * 图标url
     */
    @ApiModelProperty(value = "图标url")
    private String iconUrl;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private Long tenantId;
    /**
     * 账套id
     */
    @ApiModelProperty(value = "账套id")
    private Long setOfBooksId;
    /**
     * 账套code
     */
    @ApiModelProperty(value = "账套code")
    private String setOfBooksCode;
    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号")
    private Integer sequence = 0;
    /**
     * 所属大类id
     */
    @ApiModelProperty(value = "所属大类id")
    private Long typeCategoryId;
    /**
     * 所属大类code
     */
    @ApiModelProperty(value = "所属大类code")
    private String typeCategoryName;
    /**
     * 类型 0-申请类型 1-费用类型
     */
    @ApiModelProperty(value = "类型 0-申请类型 1-费用类型")
    private Integer typeFlag;
    /**
     * 金额录入模式 N-总金额 Y-单价*数量
     */
    @ApiModelProperty(value = "金额录入模式 N-总金额 Y-单价*数量")
    private String entryModeStr;
    /**
     * 金额录入模式 false-总金额 true-单价*数量
     */
    @ApiModelProperty(value = "金额录入模式 false-总金额 true-单价*数量")
    private Boolean entryMode;
    /**
     * 附件
     */
    @ApiModelProperty(value = "附件")
    private Integer attachmentFlag;
    /**
     * 申请类型ID，费用类型才有该字段
     */
    @ApiModelProperty(value = "申请类型ID，费用类型才有该字段")
    private Long sourceTypeId;
    /**
     * 申请类型code，费用类型才有该字段
     */
    @ApiModelProperty(value = "申请类型code，费用类型才有该字段")
    private String sourceTypeCode;

    /**
     * 关联申请模式
     */
    @ApiModelProperty(value = "关联申请模式")
    private String applicationModel;

    /**
     * 对比符号
     */
    @ApiModelProperty(value = "对比符号")
    private String contrastSign;

    /**
     * 金额条件
     */
    @ApiModelProperty(value = "金额条件")
    private BigDecimal amount;

    /**
     *  单位 当录入金额为 单价*数量 时有用
     */
    @ApiModelProperty(value = "单位 当录入金额为 单价*数量 时有用")
    private String priceUnit;

    /**
     * 差旅类型代码
     */
    @ApiModelProperty(value = "差旅类型代码")
    private String travelTypeCode;

    /**
     * 启用标识
     */
    @ApiModelProperty(value = "启用标识")
    private String enabledStr;

    /**
     * 启用标识
     */
    @ApiModelProperty(value = "启用标识")
    private Boolean enabled;
}
