package com.hand.hcf.app.ant.excel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("excel_template_mapping_field")
public class ExcelTemplateMappingField extends Domain {
    /**
     * 业务小类id
     */
    @TableField(value = "expense_type_id")
    private Long expenseTypeId;

    /**
     * 业务小类名称
     */
    @TableField(exist = false)
    private String expenseTypeName;

    /**
     * 映射模板id
     */
    @TableField(value = "id")
    private Long id;

    /**
     * 字段名称
     */
    @TableField(value="field_name")
    private String fieldName;

    /**
     * 是否必输
     */
    @TableField(value="required_flag")
    private Boolean requiredFlag;

    /**
     * 取值来源
     */
    @TableField(value="value_source")
    private String valueSource;

    /**
     * 模板名称
     */
    @TableField(value="template_name")
    private String templateName;

    /**
     * 取值字段
     */
    @TableField(value="value_field")
    private String valueField;

}
