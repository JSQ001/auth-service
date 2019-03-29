package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 费用申请单头
 * </p>
 *
 * @author bin.xie
 * @since 2018-11-21
 */
@Data
public class ApplicationHeaderCO implements Serializable {

    protected Long id;
    protected Integer versionNumber;
    /**
     * 单据编号
     */
    private String documentNumber;
    /**
     * 单据类型ID
     */
    private Long typeId;
    /**
     * 提交日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 申请人ID
     */
    private Long employeeId;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 原币金额
     */
    private BigDecimal amount;
    /**
     * 本位币金额
     */
    private BigDecimal functionalAmount;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 单据OID
     */
    private String documentOid;
    /**
     * 表单OID
     */
    private String formOid;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 单据类型
     */
    private Integer documentType;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 汇率
     */
    private BigDecimal exchangeRate;
    /**
     * 关联合同头ID
     */
    private Long contractHeaderId;
    /**
     * 是否预算管控 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean budgetFlag;
    /**
     * 是否可以关联合同 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean associateContract;
    /**
     * 合同是否必输 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean requireInput;
    /**
     * 是否超预算
     */
    private Boolean budgetStatus;
    /**
     * 超预算描述
     */
    private String budgetErrorMessage;
    /**
     * 申请单关闭标志 true 关闭，false 不关闭 默认false
     */
    private String closedFlag;

    /**
     * 关联附件的OID 用 , 分割
     */
    private String attachmentOid;

    private String applicationOid;

    /**
     * 根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤
     * true表示跳过,false表示不跳
     */
    private Boolean filterFlag;
    /**
     * 历史驳回类型 RejectTypeEnum 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回
     1004-开票驳回
     */
    private String lastRejectType;
    private String rejectType;
    private String rejectReason;

    private String companyName;
    private String typeName;
    private String departmentName;
    /**
     * 部门OID
     */
    private String departmentOid;
}
