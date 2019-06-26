package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 计提明细信息导入临时类
 * @date 2019/6/25 10:18
 */
@Data
@TableName("exp_tax_calculation_temp")
public class ExpTaxCalculationTempDomain extends Domain {

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
     * 币种代码
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 税种代码--业务小类代码
     */
    @TableField(value = "tax_category_code")
    private String taxCategoryCode;

    /**
     * 税种名称--业务小类名称
     */
    //@TableField(exist = false)
    @TableField(value = "tax_category_name")
    private String taxCategoryName;

    /**
     * 计提金额
     */
    @TableField(value = "request_amount")
    private String requestAmount;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 计提期间
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
