package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 9:57 2018/1/24
 * @Modified by
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("csh_acp_requisition_hds")
public class PaymentRequisitionHeader extends Domain {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "company_id")
    private Long companyId; //机构ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "unit_id")
    @NotNull(message = "部门ID不能为空")
    private Long unitId; //部门ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "acp_req_type_id")
    private Long acpReqTypeId; //付款申请单类型ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "employee_id")
    private Long employeeId;//员工ID

    @TableField(value = "requisition_number")
    private String requisitionNumber; //单据编号

    @TableField(value = "requisition_date")
    private ZonedDateTime requisitionDate;//申请日期

    @TableField(value = "function_amount")
    private BigDecimal functionAmount;//总金额

    @TableField(value = "description")
    @NotNull(message = "描述不能为空")
    private  String description;//头描述

    @TableField(value = "status")
    private  Integer status;

    @TableField("document_oid")
    private String documentOid; //单据oid

    @TableField("document_type")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentType;  //单据类型

    @TableField("form_oid")
    private String formOid; //表单oid

    @TableField("unit_oid")
    private String unitOid;//部门oid

    @TableField("applicant_oid")
    private String applicantOid;//申请人oid

    @TableField("submit_date")
    private String submitDate;//提交日期：使用string,方便服务之间传递

    @TableField(exist = false)
    private String acpReqTypeName; // 付款申请单类型名称

    @TableField(exist = false)
    private List<Long> reportLineIds; // 付款申请单下-关联的所有的报账单行表id集合

    @TableField("attachment_oid")
    private String attachmentOid; // 附件OIDS
}
