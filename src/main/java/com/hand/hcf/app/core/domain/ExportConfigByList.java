package com.hand.hcf.app.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 导出配置类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportConfigByList extends ExportConfig {

    private List<?> listDTO; // 需要导出的信息

    private Class<?> clazz; // 需要导出的对象类型
    public ExportConfigByList(ExportConfig exportConfig){
        this.setColumnsInfo(exportConfig.getColumnsInfo());
        this.setListDTO(null);
        this.setExcelType(exportConfig.getExcelType());
        this.setFileName(exportConfig.getFileName());
    }

}
