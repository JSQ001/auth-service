package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.expense.type.domain.enums.FieldDataTypeEnum;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@Data
@TableName("exp_expense_field")
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseField extends DomainI18nEnable {

    @TableField(exist = false)
    private ExpenseType expenseType;
    /**
     * 费用类型id
     */
    @TableField(value = "expense_type_id")
    private Long expenseTypeId;
    /**
     * 映射列
     */
    private Integer mappedColumnId;
    private Integer sequence = 0;
    //添加多语言注解
    @I18nField
    private String name;

    @TableField(value = "message_key")
    private String messageKey;

    /**
     * 控件类型（对应前端）
     * 日期 时间 月份 参与人等
     */
    @TableField(value = "field_type_id")
    private Integer fieldTypeId;

    /**
     * 字段数据类型 DATA LONG TEXT
     */
    @TableField(value = "field_data_type")
    private String fieldDataType;

    //值列表

    @TableField(value = "custom_enumeration_oid", strategy = FieldStrategy.IGNORED)
    private UUID customEnumerationOid;
    //打印隐藏
    private Boolean printHide;
    //是否必填
    private Boolean required;
    //列表展示
    private Boolean showOnList;

    @TableField(value = "field_oid")
    private UUID fieldOid;

    /**
     * 默认值模式CURRENT CUSTOM API
     */
    private String defaultValueMode;
    /**
     * 默认值
     */
    @TableField(value = "default_value_key", strategy = FieldStrategy.IGNORED)
    private String defaultValueKey;
    /**
     * 管理员是否可配置默认值
     */
    @TableField(value = "is_default_value_configurable")
    private Boolean defaultValueConfigurable;
    /**
     * 费用字段是否可编辑，0可编辑，1不可编辑
     */
    private Boolean editable;

    /**
     * 公共字段标记
     */
    @TableField(value = "is_common_field")
    private Boolean commonField;


    /**
     * 报表key
     */
    private String reportKey;

    public FieldType getFieldType() {
        return FieldType.parse(this.fieldTypeId);
    }

    public void setFieldType(FieldType fieldType) {
        if (fieldType != null) {
            this.fieldTypeId = fieldType.getId();
        } else {
            this.fieldTypeId = null;
        }
    }

    public FieldDataTypeEnum paresFieldDataTypeEnum() {
        FieldDataTypeEnum fieldDataTypeEnum = FieldDataTypeEnum.parse(this.fieldDataType);
        if (fieldDataTypeEnum == null) {
            fieldDataTypeEnum = this.getFieldType().getFieldDataTypeEnum();
        }
        return fieldDataTypeEnum;
    }

    public ExpenseField(Long expenseTypeId, UUID fieldOid){
        this.expenseTypeId = expenseTypeId;
        this.fieldOid = fieldOid;
        this.sequence = null;
    }
}