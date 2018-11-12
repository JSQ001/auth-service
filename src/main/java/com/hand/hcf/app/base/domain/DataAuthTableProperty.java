package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import com.hand.hcf.core.enums.DataAuthFilterMethodEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 17:08
 * @remark 数据权限表配置
 */
@Data
@TableName(value = "sys_data_auth_table_property")
public class DataAuthTableProperty extends DomainEnable{

    /**
     * 表名
     */
    @TableField(value = "table_name")
    @NotNull
    private String tableName;

    /**
     * 筛选方式
     * TABLE_COLUMN 表字段，CUSTOM_SQL自定义sql
     */
    @TableField(value = "filter_method")
    @NotNull
    private DataAuthFilterMethodEnum filterMethod;

    /**
     * 数据类型
     */
    @TableField(value = "data_type")
    @NotNull
    private String dataType;

    /**
     * 匹配列名称
     */
    @TableField(value = "column_name")
    @NotNull
    private String columnName;

    /**
     * 自定义sql
     */
    @TableField(value = "custom_sql")
    private String customSql;
}
