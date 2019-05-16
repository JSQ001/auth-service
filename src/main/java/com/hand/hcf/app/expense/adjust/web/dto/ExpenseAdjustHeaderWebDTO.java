package com.hand.hcf.app.expense.adjust.web.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5
 */
@ApiModel(description = "费用调整单头")
@Data
public class ExpenseAdjustHeaderWebDTO extends ExpenseAdjustHeader {

    @ApiModelProperty(value = "附件OID")
    protected List<String> attachmentOidList;

    @ApiModelProperty(value = "附件")
    private List<AttachmentCO> attachments;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "部门名称")
    private String unitName;

    @ApiModelProperty(value = "申请人名称")
    private String employeeName;

    @ApiModelProperty(value = "创建用户名称")
    private String createdByName;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "实体类型")
    private Integer entityType;

    /**
     * 维度信息
     */
    @ApiModelProperty(value = "维度信息")
    private List<ExpenseDimension> dimensions;

    /**
     * 账套代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套代码",dataType = "String")
    private String setOfBooksCode;
    /**
     * 账套名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称",dataType = "String")
    private String setOfBooksName;
}