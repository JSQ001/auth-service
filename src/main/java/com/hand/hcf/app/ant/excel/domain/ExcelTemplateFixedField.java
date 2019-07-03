package com.hand.hcf.app.ant.excel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("template_fixed_field")
public class ExcelTemplateFixedField extends Domain {
    /**
     * 表字段
     */
    @TableField(value="column_field")
    private String columnField;

    /**
     * 列名称
     */
    @TableField(value="column_name")
    private String columnName;

    /**
     * 拓展字段集合
     */
    @TableField(exist = false)
    private List<String> expandList;

}
