package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.annotation.I18nField;
import com.cloudhelios.atlantis.domain.DomainI18nEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:01
 * @remark 数据权限定义
 */
@TableName(value = "sys_data_authority")
@Data
public class DataAuthority extends DomainI18nEnable{

    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tenant_id")
    @NotNull
    private Long tenantId;

    /**
     * 数据权限代码
     */
    @TableField(value = "data_authority_code")
    @NotNull
    private String dataAuthorityCode;

    /**

     */
    @TableField(value = "data_authority_name")
    @NotNull
    @I18nField
    private String dataAuthorityName;

    /**
     * 描述
     */
    private String description;

    @TableField(exist = false)
    private List<DataAuthorityRule> dataAuthorityRules;
}
