package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/10/24 16:15
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractHeaderCO extends DomainObjectDTO {
    //合同编号
    private String contractNumber;

    //公司ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    //公司名称
    private String companyName;

    //合同大类
    private String contractCategory;

    //合同大类名称
    private String contractCategoryName;

    //合同类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractTypeId;

    //合同类型名称
    private String contractTypeName;

    //合同名称
    private String contractName;

    //签订日期
    private ZonedDateTime signDate;

    //开始日期
    private ZonedDateTime startDate;

    //结束日期
    private ZonedDateTime endDate;

    //合同总金额
    private BigDecimal amount;

    //币种
    private String currency;

    //责任部门ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;

    //责任部门名称
    private String unitName;

    //责任人ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    //责任人信息
    private ContactCO employee;

    //合同方类型
    private String partnerCategory;

    //合同方类型名称
    private String partnerCategoryName;

    //合同方ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;

    //合同方名称
    private String partnerName;

    //备注
    private String remark;

    private Integer status;

    private String statusName;

    private List<ContractLineCO> lineDTOList;

    //版本号
    private Integer versionNumber;

    //创建人信息
    private ContactCO created;

    private List<String> attachmentOIDs ;

    private List<AttachmentCO> attachments;//统一使用一个附件共用类

    private String documentOid; //单据oid

    private String documentType;  //单据类型

    private String formOid; //表单oid

    private String unitOid;//部门oid

    private String applicantOid;//申请人oid

    private ZonedDateTime submittedDate;

    private BigDecimal functionAmount;
    private Double exchangeRate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
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
