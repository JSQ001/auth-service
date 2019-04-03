package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_application_type_company")
public class ApplicationTypeAssignCompany extends DomainEnable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationTypeId;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 公司类型
     */
    @TableField(exist = false)
    private String companyTypeName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    private String companyCode;

}
