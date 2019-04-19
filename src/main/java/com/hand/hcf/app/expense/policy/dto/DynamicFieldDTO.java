package com.hand.hcf.app.expense.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shouting.cheng
 * @date 2019/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFieldDTO {
    /**
     * 动态字段
     */
    private Long fieldId;
    /**
     * 动态字段名称
     */
    private String fieldName;
    /**
     * 字段数据类型 DATE -> 日期; LONG -> 数字; TEXT -> 文本
     */
    private String fieldDataType;
    /**
     * 动态字段值
     */
    private String fieldValue;
    /**
     * 控件类型（对应前端）
     * 日期 时间 月份 参与人等
     */
    private Integer fieldTypeId;
}
