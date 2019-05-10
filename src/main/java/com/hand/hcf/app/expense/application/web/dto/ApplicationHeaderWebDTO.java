package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
@ApiModel(description = "申请单头表")
@Data
public class ApplicationHeaderWebDTO extends ApplicationHeader {

    /**
     * 维度信息
     */
    @ApiModelProperty(value = "维度信息")
    private List<ExpenseDimension> dimensions;
    @ApiModelProperty(value = "附件Oid列表")
    private List<String> attachmentOidList;
    @ApiModelProperty(value = "附件")
    private List<AttachmentCO> attachments;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "员工名称")
    private String employeeName;
    @ApiModelProperty(value = "员工编码")
    private String employeeCode;
    @ApiModelProperty(value = "类型名称")
    private String typeName;
    @ApiModelProperty(value = "创建者名称")
    private String createdName;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractNumber;
    @ApiModelProperty(value = "报告合计")
    private BigDecimal reportAmount;
    @ApiModelProperty(value = "报告允许合计")
    private BigDecimal reportAbleAmount;
    /**
     * 可关闭金额
     */
    @ApiModelProperty(value = "可关闭金额")
    private BigDecimal canCloseAmount;

    /**
     * 关联金额
     */
    @ApiModelProperty(value = "关联金额")
    private  BigDecimal releaseAmount;
    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型")
    private  Long expenseType;
    @ApiModelProperty(value = "费用类型名称")
    private  String expenseTypeName;

    //是否可同时发起预付款标志（true：可发起，false：不可发起）
    @ApiModelProperty(value = "是否可同时发起预付款标志（true：可发起，false：不可发起")
    private Boolean prePaymentFlag;
}
