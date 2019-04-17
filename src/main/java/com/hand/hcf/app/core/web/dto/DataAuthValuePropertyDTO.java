package com.hand.hcf.app.core.web.dto;

import lombok.Data;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/26 10:45
 * @remark 数据权限配置信息
 */
@Data
public class DataAuthValuePropertyDTO {

    /**
     * 数据类型
     * 账套 SOB；公司 COMPANY； 部门 UNIT； 员工 EMPLOYEE
     */
    private String dataType;

    /**
     * 数据范围是否为全部
     */
    private Boolean allFlag;

    /**
     * 数据取值方式
     * 包含 INCLUDE； 排除EXCLUDE
     */
    private String filtrateMethod;

    /**
     * 明细值
     */
    private List<String> valueKeyList;
}
