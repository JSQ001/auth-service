package com.hand.hcf.app.base.codingrule.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author dong.liu on 2017-08-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_coding_rule_detail")
public class CodingRuleDetail extends DomainI18nEnable {


    @TableField("coding_rule_id")
    private Long codingRuleId; //编码规则id
    @NotNull
    @TableField("sequence_number")
    private Integer sequence; //顺序号
    @NotNull
    @TableField("segment_type")
    private String segmentType; //段
    @TableField(exist = false)
    private String segmentName; //段名称
    @TableField("segment_value")
    private String segmentValue; //段值
    @TableField("date_format")
    private String dateFormat; //日期格式
    @TableField(exist = false)
    private String dateFormatName; //日期格式名称
    @TableField("length")
    private Integer length; //位数
    @TableField("incremental")
    private Integer incremental; //步长
    @TableField("start_value")
    private Integer startValue; //开始值
    @TableField(value = "tenant_id")

    private Long tenantId;  //租户id

}
