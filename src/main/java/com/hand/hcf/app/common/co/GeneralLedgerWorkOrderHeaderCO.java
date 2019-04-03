package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by cbc on 2018/07/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralLedgerWorkOrderHeaderCO {

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
    @NotNull
    private Long companyId;
    private String companyName;
    /**
     * 部门id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long unitId;
    private String unitName;
    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;
    private String employeeName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 汇率
     */
    private Double exchangeRate;


    /**
     * 核算工单编号
     */


    private String workOrderNumber;
    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 核算工单类型id
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workOrderTypeId;

    private String typeName;

    /**
     * 说明
     */
    @NotNull
    private String remark;
    /**
     * 附件数
     */
    private Long attachmentNum;
    /**
     * 审批状态
     */
    private int status;
    private String statusName;
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


    private Integer versionNumber;

    private Boolean enabled;

    private Boolean isDeleted;

    private ZonedDateTime createdDate;

    private Long createdBy;
    private String createByName;

    private ZonedDateTime lastUpdatedDate;

    private Long lastUpdatedBy;

    /**
     * 附件oid
     */
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

    /*是否走工作流：true:走工作流*/
    @NotNull
    private Boolean ifWorkflow;

    //提交日期
    private ZonedDateTime submitDate;

    //字符串提交日期
    private String StringSubmitDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long checkBy;

    private BigDecimal amount;


    private Boolean accountFlag;   //凭证标志

    private Page page;
    /**
     *  根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤
     */
    private Boolean filterFlag;// true表示跳过,false表示不跳
    /**
     * 历史驳回类型 RejectTypeEnum 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回 1004-开票驳回
     */
    private String lastRejectType;
    private String rejectType;
    private String rejectReason;
}
