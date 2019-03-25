package com.hand.hcf.app.base.dataAuthority.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.base.dataAuthority.dto.DataAuthRuleDetailValueDTO;
import com.hand.hcf.core.domain.DomainLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:03
 * @remark 数据权限规则明细
 */
@TableName(value = "sys_data_auth_rule_detail")
@Data
public class DataAuthorityRuleDetail extends DomainLogic{

    /**
     * 数据权限ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "data_authority_id")
    private Long dataAuthorityId;

    /**
     * 规则ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    @TableField(value = "data_authority_rule_id")
    private Long dataAuthorityRuleId;

    /**
     * 数据类型
     * 账套 SOB；公司 COMPANY； 部门 UNIT； 员工 EMPLOYEE
     */
    @NotNull
    @TableField(value = "data_type")
    private String dataType;

    /**
     * 数据范围
     * 全部：1001  当前：1002 当前及下属：1003 手动选择：1004
     */
    @NotNull
    @TableField(value = "data_scope")
    private String dataScope;

    /**
     * 数据范围描述
     */
    @TableField(exist = false)
    private String dataScopeDesc;

    /**
     * 数据取值方式
     * 包含 INCLUDE； 排除EXCLUDE
     */
    @TableField(value = "filtrate_method")
    private String filtrateMethod;

    /**
     * 数据取值方式描述
     */
    @TableField(exist = false)
    private String filtrateMethodDesc;

    /**
     * 明细值 - 主要用于保存数据
     */
    @TableField(exist = false)
    private List<String> dataAuthorityRuleDetailValues;

    /**
     * 明细值描述信息
     */
    @TableField(exist = false)
    private List<DataAuthRuleDetailValueDTO> dataAuthorityRuleDetailValueDTOs;
}
