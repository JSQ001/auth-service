package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by ll on 2017/8/22.
 * 合同头信息DTO
 */
@Data
public class ContractReponseCO {
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;
    protected Boolean enabled;
    protected Boolean deleted;
    protected ZonedDateTime createdDate;
    protected Long createdBy;
    protected ZonedDateTime lastUpdatedDate;
    protected Long lastUpdatedBy;
    private Integer versionNumber;
    private String contractNumber;//合同编号
    private Long companyId;//公司ID
    private String contractCategory;//合同大类  AMORTIZE 摊销类  EXPENDITURE 非摊销类
    private Long contractTypeId;//合同类型ID
    private String contractName;//合同名称
    private ZonedDateTime signDate;//签订日期
    private ZonedDateTime startDate;//开始日期
    private ZonedDateTime endDate;//结束日期
    private BigDecimal amount;//合同总金额
    private String currency;//币种
    private BigDecimal functionAmount;
    private Double exchangeRate;
    private Long unitId;//责任部门ID
    private Long employeeId;//责任人ID
    private String partnerCategory;//合同方类型
    private Long partnerId;//合同方ID
    private String remark;//备注
    private Integer status;//状态（GENERATE -> 新建;SUBMITTED -> 提交;CONFIRM -> 确认;CANCEL ->取消;HOLD ->暂挂;FINISH ->完成;REJECTED -> 拒绝; WITHDRAWAL->合同撤回）
    private Long approvalId;//审批人ID
    private String attachmentOIDs;
    private String documentOid; //单据oid
    private String documentType;  //单据类型
    private String formOid; //表单oid
    private String unitOid;//部门oid
    private String applicantOid;//申请人oid
    /* 合同类型代码 */
    private String contractTypeCode;
    /* 合同类型名称 */
    private String contractTypeName;
    //提交日期
    private ZonedDateTime submittedDate;
    private Page page;//20181129 分页信息返回给调用模块


}
