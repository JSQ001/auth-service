package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.AttachmentCO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by 刘亮 on 2017/12/15.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentRequisitionHeaderCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 租户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 公司id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    private String companyName;
    /**
     * 部门id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;
    private String UnitName;
    /**
     * 上级部门
     */
    private String path;
    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    /**
     * 预付款单编号
     */

    private String requisitionNumber;
    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 预付款单类型id
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentReqTypeId;

    private String typeName;

    //未核销金额
    @TableField(exist = false)
    private BigDecimal noWritedAmount;
    //本位币币种
    private String currency;
    //核销金额（推送支付平台的单据才有）
    private BigDecimal writedAmount;
    /**
     * 说明
     */
    private String description;
    /**
     * 附件数
     */
    private Long attachmentNum;
    /**
     * 审批状态
     */
    private int status;
//    private String statusName;
    /**
     * 审批日期
     */
    private ZonedDateTime approvalDate;
    /**
     * 审批人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long approvedBy;
    /**
     * 审核标志
     */
    private Boolean auditFlag;
    /**
     * 审核日期
     */
    private ZonedDateTime auditDate;

    /**
     *  预付款金额
     */
    private BigDecimal advancePaymentAmount;


    private Integer versionNumber;


    private ZonedDateTime createdDate;

    private Long createdBy;
    private String createByName;
    private String createdByCode;


    private ZonedDateTime lastUpdatedDate;

    private Long lastUpdatedBy;

    private String attachmentOid;
    private List<String> attachmentOids;
    private List<AttachmentCO> attachments;


    /*申请人oid*/
    private String applicationOid;

    /*表单oid*/
    private String formOid;

    /*部门oid*/
    private String unitOid;

    /*员工oid*/
    private String empOid;

    /*单据oid*/
    private String documentOid;

    /*单据类型*/
    private Integer documentType;

    //审批备注
    private String approvalRemark;

    /*是否走工作流：true:走工作流*/
    @NotNull
    private Boolean ifWorkflow;

    //预付款单类型付款方式
    private String paymentMethod;
    private String paymentMethodCode;

    //预付款单是否关联申请单
    private Boolean ifApplication;

    //提交日期
    private ZonedDateTime submitDate;

    //字符串提交日期
    private String StringSubmitDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long checkBy;

    private Boolean currentFlag;

    private Page page;
}
