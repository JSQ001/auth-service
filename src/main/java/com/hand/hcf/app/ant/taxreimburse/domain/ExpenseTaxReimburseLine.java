package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 11:32
 * @remark 国内税金缴纳报账单行表-存放
 */
@Data
@TableName("exp_tax_reimburse_line")
public class ExpenseTaxReimburseLine extends Domain{

    /**
     * 国内税金缴纳报账单头Id
     */
    @TableField(value = "exp_reimburse_header_id")
    private Long expReimburseHeaderId;

    /**
     * 租户
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 账套
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 税金申报数据Id
     */
    @NotNull
    @TableField(value = "exp_tax_report_id")
    private Long expenseTaxReportId;

    /**
     * 银行流水数据Id
     */
    @NotNull
    @TableField(value = "exp_bank_flow_id")
    private Long expenseBankFlowId;

    /**
     * 币种
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 报账总金额--税金申报总金额
     */
    @NotNull
    @TableField(value = "request_total_amount")
    private BigDecimal requestTotalAmount;

    /**
     * 备注
     */
    @TableField(value = "description")
    private String description;

    /**
     * 附件
     */
    @TableField(value = "attachment_oid")
    private String attachmentOid;


    //以下字段为辅助字段,数据库中无具体的字段对应，用于显示在页面上
    /**
     * 附件OID集合
     */
    @TableField(exist = false)
    private List<String> attachmentOidList;
    /**
     * 附件信息
     */
    @TableField(exist = false)
    private List<AttachmentCO> attachments;
    /**
     * 序号
     */
    @TableField(exist = false)
    private Integer index;

}
