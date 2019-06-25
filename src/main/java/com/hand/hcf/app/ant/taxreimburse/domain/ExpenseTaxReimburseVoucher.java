package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @create 2019/6/6 14:59
 * @remark 国内税金缴纳报账单凭证信息表
 */
@Data
@TableName("exp_tax_reimburse_voucher")
public class ExpenseTaxReimburseVoucher extends Domain{

    /**
     * 国内税金缴纳报账单头Id
     */
    @TableField(value = "exp_reimburse_header_id")
    private Long expReimburseHeaderId;

    /**
     * 单据编号
     */
    @TableField(value = "requisition_number")
    private String requisitionNumber;

    /**
     * 凭证编号
     * 当该凭证尚未传递至记账中心生成凭证前，置灰，默认为空值，不可维护。
     * 当凭证已传递至记账中心生成凭证后，默认回传ERP系统对应生成的凭证编号，不可修改。
     */
    @TableField(value = "voucher_number")
    private String voucherNumber;

    /**
     * 公司段
     */
    @TableField(value = "company_segment")
    private String companySegment;

    /**
     * 公司段说明
     */
    @TableField(value = "company_segment_desc")
    private String companySegmentDesc;

    /**
     * 预算部门id
     */
    @TableField(value = "budget_department_id")
    private Long budgetDepartmentId;

    /**
     * 预算部门名称
     */
    @TableField(exist = false)
    private String budgetDepartmentName;

    /**
     * 预算部门说明
     */
    @TableField(value = "benefited_depart_desc")
    private String budgetDepartmentDesc;

    /**
     * 受益部门Id
     */
    @NotNull
    @TableField(value = "benefited_depart_id")
    private Long benefitedDepartId;

    /**
     * 受益部门名称
     */
    @NotNull
    @TableField(exist = false)
    private String benefitedDepartName;

    /**
     * 受益部门说明
     */
    @TableField(value = "benefited_depart_desc")
    private String benefitedDepartDesc;

    /**
     * 区域
     */
    @NotNull
    @TableField(value = "area_id")
    private Long areaId;

    /**
     * 区域
     */
    @NotNull
    @TableField(exist = false)
    private String areaName;

    /**
     * 区域说明
     */
    @TableField(value = "area_desc")
    private String areaDesc;

    /**
     * 科目段
     */
    @TableField(value = "subject_segment")
    private String subjectSegment;

    /**
     * 科目段说明
     */
    @TableField(value = "subject_segment_desc")
    private String subjectSegmentDesc;

    /**
     * 明细段
     */
    @TableField(value = "detail_segment")
    private String detailSegment;

    /**
     * 明细段说明
     */
    @TableField(value = "detail_segment_desc")
    private String detailSegmentDesc;

    /**
     * 往来段
     */
    @TableField(value = "dealing_segment")
    private String dealingSegment;

    /**
     * 往来段说明
     */
    @TableField(value = "dealing_segment_desc")
    private String dealingSegmentDesc;

    /**
     * 项目段
     */
    @TableField(value = "project_segment")
    private String projectSegment;

    /**
     * 项目段说明
     */
    @TableField(value = "project_segment_desc")
    private String projectSegmentDesc;

    /**
     * 产品段
     */
    @TableField(value = "product_segment")
    private String productSegment;

    /**
     * 产品段说明
     */
    @TableField(value = "product_segment_desc")
    private String productSegmentDesc;

    /**
     * 行业段
     */
    @TableField(value = "industry_segment")
    private String industrySegment;

    /**
     * 行业段说明
     */
    @TableField(value = "industry_segment_desc")
    private String industrySegmentDesc;

    /**
     * 本币借方
     */
    @TableField(value = "local_currency_debit")
    private String localCurrencyDebit;

    /**
     * 本币贷方
     */
    @TableField(value = "local_currency_lender")
    private String localCurrencyLender;

    /**
     * 备用段1
     */
    @TableField(value = "attribute_segment1")
    private String attributeSegment1;

    /**
     * 备用段1说明
     */
    @TableField(value = "attribute_segment1_desc")
    private String attributeSegment1Desc;

    /**
     * 备用段2
     */
    @TableField(value = "attribute_segment2")
    private String attributeSegment2;

    /**
     * 备用段2说明
     */
    @TableField(value = "attribute_segment2_desc")
    private String attributeSegment2Desc;

    /**
     * 摘要
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 入账日期
     */
    @TableField(value = "account_entry_date")
    private ZonedDateTime accountEntryDate;

    /**
     * 状态
     * 默认为“拟定”，
     * 在凭证预览环节点击“同意”按钮时，状态更改为“已制证”，
     * 报账单点击审核按钮后，凭证传递到记账中心，传递成功并反馈凭证编号，凭证状态更改为“已入账”
     */
    @TableField(value = "status")
    private Integer status;

}
