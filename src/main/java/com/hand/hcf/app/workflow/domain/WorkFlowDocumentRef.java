package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/6.
 * 单据关联工作流表
 */
@Data
@TableName("sys_wfl_document_ref")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkFlowDocumentRef extends Domain {
    @TableField("document_number")
    private String documentNumber;// 单据编号
    @TableField("document_oid")
    private UUID documentOid;//单据Oid
    @TableField("document_category")
    private Integer documentCategory;//单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单 801007 费用反冲 801008 核算工单  801009 费用申请单
    @TableField("document_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentTypeId;//单据类型ID
    @TableField("form_oid")
    private UUID formOid;//表单Oid
    @TableField("currency_code")
    private String currencyCode;//单据币种
    @TableField("amount")
    private BigDecimal amount;//原币金额
    @TableField("function_amount")
    private BigDecimal functionAmount;//本币金额
    @TableField("unit_oid")
    private UUID unitOid;//单据部门Oid
    @TableField("company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;//公司ID
    @TableField("status")
    private Integer status;//单据状态
    @TableField("last_approver_oid")
    private UUID lastApproverOid;//最后审批人Oid
    @TableField("user_oid")
    private UUID userOid;//用户Oid
    @TableField("applicant_oid")
    private UUID applicantOid;//申请人Oid
    @TableField("remark")
    private String remark;//备注
    /**
     * 根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤
     */
    @TableField("filter_flag")
    private Boolean filterFlag;
    /**
     * 历史驳回类型 RejectTypeEnum 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回 1004-开票驳回
     */
    @TableField("last_reject_type")
    private String lastRejectType;

    @TableField("reject_type")
    private String rejectType;

    @TableField("reject_reason")
    private String rejectReason;
    @TableField("event_id")
    private String eventId;
    @TableField("event_confirm_status")
    private Boolean eventConfirmStatus;
    //审批节点Oid
    @TableField(value = "approval_node_oid")
    private String  approvalNodeOid;
    //审批节点名称
    @TableField(value="approval_node_name")
    private String approvalNodeName;
    @TableField(exist = false)
    private List<UUID> countersignApproverOids;

    @TableField("company_name")
    private String companyName;//公司名称
    @TableField("company_oid")
    private UUID companyOid;//公司Oid
    @TableField("company_code")
    private String companyCode;//公司代码
    @TableField("unit_name")
    private String unitName;//部门名称
    @TableField("unit_code")
    private String unitCode;//部门代码
    @TableField("applicant_name")
    private String applicantName;//申请人名称
    @TableField("applicant_code")
    private String applicantCode;//申请人编号
    @TableField("document_type_name")
    private String documentTypeName;//单据类型名称
    @TableField("document_type_code")
    private String documentTypeCode;//单据类型代码
    @TableField("contract_name")
    private String contractName; //合同名称
    @TableField("submit_date")
    private ZonedDateTime submitDate;//提交日期
    @TableField("applicant_date")
    private ZonedDateTime applicantDate;//申请日期
    @TableField("document_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;// 单据的ID
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //表示服务注册到Eureka中的名称(如：prepayment:预付款，contract:合同, budget:预算)，这样能保证每次只对具体的服务发布消息
    @TableField("destination_service")
    private String destinationService;

    @TableField(exist = false)
    private List<UUID> currentApproverOids;//审批人Oid集合
    @TableField(exist = false)
    private List<WorkFlowApprovers> currentApproverList;//审批信息集合：审批节点Oid,审批人Oid
    @TableField("submitted_by")
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID submittedBy; //提交人Oid

}
