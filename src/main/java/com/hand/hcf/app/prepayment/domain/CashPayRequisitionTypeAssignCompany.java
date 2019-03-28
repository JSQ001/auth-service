package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.Data;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@Data
@TableName("csh_sob_pay_req_t_ass_company")
public class CashPayRequisitionTypeAssignCompany extends DomainEnable {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "sob_pay_req_type_id")
    private Long sobPayReqTypeId;//预付款单类型ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "company_id")
    private Long companyId;//公司ID

    //公司代码
    @TableField("company_code")
    private String companyCode;

    //公司名称
    @TableField(exist = false)
    private String companyName;

    //公司类型
    @TableField(exist = false)
    private String companyType;
}
