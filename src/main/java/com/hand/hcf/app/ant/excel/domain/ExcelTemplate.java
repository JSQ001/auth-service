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
 * @date: 2019/6/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("excel_template")
public class ExcelTemplate extends Domain {
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
     * 模板名称
     */
    @TableField(value = "template_name")
    private String templateName;

    /**
     * 业务属性
     */
    @TableField(value ="expense_attribute")
    private String expenseAttribute;

    /**
     * 业务属性名称
     */
    @TableField(exist = false)
    private String expenseAttributeName;

}