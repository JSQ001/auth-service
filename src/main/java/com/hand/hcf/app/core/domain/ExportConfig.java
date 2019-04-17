package com.hand.hcf.app.core.domain;

import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/8/3
 */
@Data
public class ExportConfig {
    private String fileName; // 文件名

    private List<ColumnInfo> columnsInfo; //导出列信息

    private String excelType;
}
