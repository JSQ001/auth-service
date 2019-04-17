package com.hand.hcf.app.core.domain;


import lombok.Data;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 表列名类
 */
@Data
public class ColumnInfo {

    private String title;

    private String name;

    private String type = "string";

    private int width = 80;

    private String align = "left";

    private String dataFormat;

    List<ColumnInfo> columnsInfo;
}
