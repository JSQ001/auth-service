package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/6.
 * 单据关联工作流表
 */
@Data
public class WorkFlowDocumentRefCO extends Domain {
    private String documentNumber;// 单据编号
    private UUID documentOid;//单据OID
    private String documentName;//单据名称
    private Integer documentCategory;//单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单 801007 费用反冲 801008 核算工单  801009 费用申请单
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentTypeId;//单据类型ID
    private UUID formOid;//表单OID
    private String currencyCode;//单据币种
    private BigDecimal amount;//原币金额
    private BigDecimal functionAmount;//本币金额
    private UUID unitOid;//单据部门OID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;//公司ID
    private Integer status;//单据状态
    private UUID lastApproverOid;//最后审批人OID
    private UUID userOid;//用户OID
    private UUID applicantOid;//申请人OID
    private String remark;//备注
    /**
     * 根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤
     */
    private Boolean filterFlag;
    /**
     * 历史驳回类型 RejectTypeEnum 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回 1004-开票驳回
     */
    private String lastRejectType;
    private String rejectType;
    private String rejectReason;
    private String eventId;
    private Boolean eventConfirmStatus;
    //审批节点OID
    private String approvalNodeOID;
    //审批节点名称
    private String approvalNodeName;
    @TableField(exist = false)
    private List<UUID> countersignApproverOIDs;
    private String companyName;//公司名称
    private UUID companyOid;//公司OID
    private String companyCode;//公司代码
    private String unitName;//部门名称
    private String unitCode;//部门代码
    private String applicantName;//申请人名称
    private String applicantCode;//申请人编号
    private String documentTypeName;//单据类型名称
    private String documentTypeCode;//单据类型代码
    @Deprecated
    private String contractName; //合同名称
    private ZonedDateTime submitDate;//提交日期
    private ZonedDateTime applicantDate;//申请日期
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;// 单据的ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    private List<UUID> currentApproverOids;//审批人OID集合
    private List<WorkflowApproversCO> currentApproverList;//审批信息集合：审批节点OID,审批人OID

    //表示服务注册到Eureka中的名称(如：prepayment:预付款，contract:合同, budget:预算)，这样能保证每次只对具体的服务发布消息
    private String destinationService;
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID submittedBy; //提交人

}
