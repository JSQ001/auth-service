package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报信息类
 * @date 2019/5/29 10:18
 */
@Data
@TableName("exp_tax_report")
public class ExpTaxReport extends Domain {

    /**
     * id-唯一识别号
     */
    @TableField(value = "id")
    private Long Id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    private Long companyId;


    /**
     * 预算部门id
     */
    @TableField(value = "budget_department_id")
    private Long budgetDepartmentId;


    /**
     * 收益部门id
     */
    @TableField(value = "benefit_department_id")
    private Long benefitDepartmentId;

    /**
     * 币种代码
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 勾兑状态
     */
    @TableField(value = "blend_status",strategy = FieldStrategy.NOT_NULL)
    private Boolean blendStatus;

    /**
     * 税种代码
     */
    @TableField(value = "tax_category_code")
    private String taxCategoryCode;

    /**
     * 税种名称
     */
    //@TableField(exist = false)
    @TableField(value = "tax_category_name")
    private String taxCategoryName;

    /**
     * 科目代码
     */
    @TableField(value = "business_subcategory_code")
    private String businessSubcategoryCode;

    /**
     * 会计科目
     */
    @TableField(value = "business_subcategory_name")
    private String businessSubcategoryName;

    /**
     * 申报金额
     */
    @TableField(value = "request_amount")
    private BigDecimal requestAmount;

    /**
     * 申报期间
     */
    @TableField(value = "request_period")
    private String requestPeriod;

    /**
     * 报账状态
     */
    @TableField(value ="status",strategy = FieldStrategy.NOT_NULL)
    private Boolean status;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 国内税金缴纳报账单头Id
     */
    @TableField(value = "exp_reimburse_header_id")
    private Long expReimburseHeaderId;


    /**
     * 以下字段表中不存在，用于显示在页面上
     */
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;


    //币种名称
    @TableField(exist = false)
    private String currencyName;

}
