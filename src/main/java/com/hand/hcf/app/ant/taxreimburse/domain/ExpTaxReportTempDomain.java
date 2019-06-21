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
 * @description: 税金申报信导入临时domain
 * @date 2019/5/29 10:18
 */
@Data
@TableName("exp_tax_report_temp")
public class ExpTaxReportTempDomain extends Domain {

    /**
     * id-唯一识别号
     */
    @TableField(value = "id")
    private Long Id;


    /**
     * 导入数据的行号
     */
    @TableField(value = "row_number")
    private String rowNumber;


    /**
     * 公司id
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 公司代码code
     */
    @TableField(value = "company_code")
    private String companyCode;


    /**
     * 预算部门code
     */
    @TableField("budget_department_code")
    private String budgetDepartmentCode;


    /**
     * 受益部门code
     */
    @TableField("benefit_department_code")
    private String benefitDepartmentCode;


    /**
     * 税种代码
     */
    @TableField(value = "tax_category_code")
    private String taxCategoryCode;

    /**
     * 税种名称name
     */
    @TableField(value = "tax_category_name")
    private String taxCategoryName;

    /**
     * 业务小类代码--科目代码
     */
    @TableField(value = "business_subcategory_code")
    private String businessSubcategoryCode;

    /**
     * 业务小类名称-会计科目名称
     */
    @TableField(value = "business_subcategory_name")
    private String businessSubcategoryName;

    /**
     * 地方代码code
     */
    @TableField(value = "location_code")
    private String locationCode;

    /**
     * 申报金额
     */
    @TableField(value = "request_amount")
    private String requestAmount;

    /**
     * 申报期间
     */
    @TableField(value = "request_period")
    private String requestPeriod;

    /**
     * 临时表字段--批次号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 错误明细
     */
    @TableField(value = "error_detail")
    private String errorDetail;

    /**
     * 错误标记
     */
    @TableField(value = "error_flag")
    private Boolean errorFlag;


}
