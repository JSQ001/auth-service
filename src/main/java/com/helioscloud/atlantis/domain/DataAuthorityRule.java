package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.annotation.I18nField;
import com.cloudhelios.atlantis.domain.DomainI18n;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:02
 * @remark 数据权限规则
 */
@TableName(value = "sys_data_authority_rule")
@Data
public class DataAuthorityRule extends DomainI18n{

    /**
     * 数据权限ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    @TableField(value = "data_authority_id")
    private Long dataAuthorityId;

    /**
     * 数据权限规则名称
     */
    @NotNull
    @TableField(value = "data_authority_rule_name")
    @I18nField
    private String dataAuthorityRuleName;

    /**
     * 规则明细
     */
    @TableField(exist = false)
    private List<DataAuthorityRuleDetail> dataAuthorityRuleDetails;
}
