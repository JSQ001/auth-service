package com.hand.hcf.app.ant.excel.dto;

import lombok.Data;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/11
 */
@Data
public class ExcelTemplateMappingDTO {

    /**
     * 字段值
     */
    private String columnCode;

    /**
     * 字段名称
     */
    private String columnName;

}
