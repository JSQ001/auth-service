package com.hand.hcf.app.expense.type.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFieldDTO implements Serializable{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private FieldType fieldType;
    /**
     * 字段数据类型
     */
    private String fieldDataType;

    private String name;
    private String value;
    private String codeName;
    private String messageKey;
    private Integer sequence;
    /**
     * 值列表oid
     */
    private UUID customEnumerationOid;
    /**
     * 映射列
     */
    private Integer mappedColumnId;
    /**
     * 打印隐藏
     */
    private Boolean printHide;
    /**
     * 是否必填
     */
    private Boolean required;
    /**
     * 列表展示
     */
    private Boolean showOnList;
    /**
     * 费用字段oid
     */
    private UUID fieldOid;
    /**
     * 费用字段是否可编辑，true可编辑，false不可编辑
     */
    private Boolean editable;

    /**
     * 默认值模式CURRENT CUSTOM API
     */
    private String defaultValueMode;
    /**
     * 默认值存储
     */
    private String defaultValueKey;
    /**
     * 默认值显示
     */
    private String showValue;
    /**
     * 管理员是否可配置默认值
     */
    private Boolean defaultValueConfigurable;

    /**
     * 公共字段标记
     */
    private Boolean commonField;
    /**
     * 报表key
     */
    private String reportKey;

    /**
     * 费用字段名称多语言字段
     */
    private Map<String, List<Map<String, String>>> i18n;

    /**
     * 值列表值信息
     */
    private List<OptionDTO> options;

}
