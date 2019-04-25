package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: bin.xie
 * @Description:借款申请单分配机构
 * @Date: Created in 11:33 2018/1/22
 * @Modified by
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("csh_req_types_to_company")
public class PaymentRequisitionTypesToCompany extends DomainEnable {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//主键ID

    @TableField(value = "acp_req_types_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long acpReqTypesId;//付款申请单ID

    @TableField(value = "company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    //公司代码
    @TableField(exist = false)
    private String companyCode;

    //公司名称
    @TableField(exist = false)
    private String companyName;

    //公司类型
    @TableField(exist = false)
    private String companyTypeName;
}
