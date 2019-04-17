package com.hand.hcf.app.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: bin.xie
 * @Description: 导出配置类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportConfigBySQLId extends ExportConfig {


    private Map<String,Object> param = null; //参数

    private String sqlId; // 执行的sql ID

    private Class<?> clazz; // 需要导出的对象类型

    public ExportConfigBySQLId(ExportConfig exportConfig){
        this.setColumnsInfo(exportConfig.getColumnsInfo());
        this.setClazz(null);
        this.setSqlId(null);
        this.setExcelType(exportConfig.getExcelType());
        this.setFileName(exportConfig.getFileName());
    }
}
