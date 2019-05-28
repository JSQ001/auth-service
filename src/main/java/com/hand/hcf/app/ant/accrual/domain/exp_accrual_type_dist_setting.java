package com.hand.hcf.app.ant.accrual.domain;

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

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_accrual_type_dist_setting")
public class exp_accrual_type_dist_setting extends Domain {
    //预提单类型ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("accrual_type_id")
    private Long accrualTypeId;

    //公司参与分摊标志
    @NotNull
    @TableField("company_dist_flag")
    private Boolean companyDistFlag;

    //公司分摊范围(账套下所有公司：ALL_COM_IN_SOB；本公司及下属公司：CURRENT_COM_&_SUB_COM；下属公司：SUB_COM；自定义范围：CUSTOM_RANGE)
    @TableField("company_dist_range")
    private String companyDistRange;

    //默认分摊公司ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("company_default_id")
    private Long companyDefaultId;

    //公司可见设置（只读：READ_ONLY；可编辑：EDITABLE；隐藏：HIDDEN）
    @NotNull
    @TableField("company_visible")
    private String companyVisible;

    //责任中心参与分摊标志
    @NotNull
    @TableField("res_center_dist_flag")
    private Boolean resCenterDistFlag;

    //责任中心分摊范围(部门对应责任中心：DEP_RES_CENTER；账套下所有责任中心：ALL_RES_CENTER_IN_SOB；自定义范围：CUSTOM_RANGE)
    @TableField("res_dist_range")
    private String resDistRange;

    //默认分摊责任中心ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("res_default_id")
    private Long resDefaultId;

    //责任中心可见设置（只读：READ_ONLY；可编辑：EDITABLE；隐藏：HIDDEN）
    @NotNull
    @TableField("res_visible")
    private String resVisible;


    //默认分摊公司代码
    @TableField(exist = false)
    private String companyCode;

    //默认分摊公司名称
    @TableField(exist = false)
    private String companyName;

    //默认分摊部门代码
    @TableField(exist = false)
    private String departmentCode;

    //默认分摊部门名称
    @TableField(exist = false)
    private String departmentName;

    //默认分摊责任中心代码
    @TableField(exist = false)
    private String resCode;

    //默认分摊责任中心名称
    @TableField(exist = false)
    private String resName;
}
