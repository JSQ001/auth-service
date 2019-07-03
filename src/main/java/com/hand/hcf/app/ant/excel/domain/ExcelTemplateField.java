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
@TableName("excel_template_field")
public class ExcelTemplateField extends Domain {
    /**
     * 模板id
     */
    @TableField(value ="excel_template_id")
    private Long excelTemplateId;

    /**
     * 模板字段名称
     */
    @TableField(value ="field_name")
    private String fieldName;

    /**
     * 模板字段代码
     */
    @TableField(value = "field_code")
    private String fieldCode;

    /**
     * 备注
     */
    @TableField(value="remark")
    private String remark;
}
