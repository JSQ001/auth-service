package com.hand.hcf.app.base.dataAuthority.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:04
 * @remark 数据权限规则明细值
 */
@TableName(value = "sys_data_auth_rule_del_value")
@Data
public class DataAuthorityRuleDetailValue extends Domain{

    /**
     * 数据权限ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "data_authority_id")
    private Long dataAuthorityId;

    /**
     * 规则明细ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    @TableField(value = "data_auth_rule_detail_id")
    private Long dataAuthRuleDetailId;

    /**
     * 明细值
     */
    @NotNull
    @TableField(value = "value_key")
    private String valueKey;

}
