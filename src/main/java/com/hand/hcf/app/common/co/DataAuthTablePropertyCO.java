package com.hand.hcf.app.common.co;

import com.hand.hcf.core.enums.DataAuthFilterMethodEnum;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
import lombok.Data;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 18:21
 * @remark 数据权限表设置相关
 */
@Data
public class DataAuthTablePropertyCO extends DomainObjectDTO {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 筛选方式
     * TABLE_COLUMN 表字段，CUSTOM_SQL自定义sql
     */
    private DataAuthFilterMethodEnum filterMethod;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 匹配列名称
     */
    private String columnName;

    /**
     * 自定义sql
     */
    private String customSql;
}
