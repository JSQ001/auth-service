package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.reflection.Reflector;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/8 11:01
 * @remark
 */
@ApiModel(description = "费用报告头")
@Data
public class ExpenseReportHeaderDTO extends ExpenseReportHeader{

    /**
     * 单据维度布局
     */
    @ApiModelProperty(value = "单据维度布局")
    private List<ExpenseDimension> expenseDimensions;
    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码")
    private String companyCode;
    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    /**
     * 部门代码
     */
    @ApiModelProperty(value = "部门代码")
    private String departmentCode;
    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String createdName;
    /**
     * 创建人编码
     */
    @ApiModelProperty(value = "创建人编码")
    private String createdCode;
    /**
     * 申请人名称
     */
    @ApiModelProperty(value = "申请人名称")
    private String applicantName;
    /**
     * 申请人编码
     */
    @ApiModelProperty(value = "申请人编码")
    private String applicantCode;
    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractNumber;
    /**
     * 收款方代码
     */
    @ApiModelProperty(value = "收款方代码")
    private String payCode;
    /**
     * 收款方代码
     */
    @ApiModelProperty(value = "收款方代码")
    private String payName;
    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称")
    private String currencyName;
    /**
     * 税金分摊方式
     */
    @ApiModelProperty(value = "税金分摊方式")
    private String expTaxDist;

    /**
     * 已付金额
     */
    @ApiModelProperty(value = "已付金额")
    private BigDecimal paidAmount;

    /**
     * 核销金额
     */
    @ApiModelProperty(value = "核销金额")
    private  Double writeOffAmount;

    /**
     * 反冲标志
     */
    @ApiModelProperty(value = "反冲标志")
    private  String reversedFlag;

    /**
     * 付款总金额
     */
    @ApiModelProperty(value = "付款总金额")
    private  BigDecimal paymentTotalAmount;
}
