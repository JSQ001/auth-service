package com.hand.hcf.app.ant.accrual.dto;

import com.hand.hcf.app.ant.accrual.domain.AccruedExpensesHeader;
import lombok.Data;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@Data
public class AccruedExpensesHeaderDTO extends AccruedExpensesHeader {
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司代码
     */
    private String companyCode;

    /**
     * 预算部门名称
     */
    private String budgetDepName;
    /**
     * 预算部门代码
     */
    private String budgetDepCode;

    /**
     * 受益部门名称
     */
    private String departmentName;

    /**
     * 受益部门代码
     */
    private String departmentCode;

    /**
     * 责任方名称
     */
    private String demanderName;

    /**
     * 责任方编码
     */
    private String demanderCode;

    /**
     * 创建人名称
     */
    private String applicantName;

    /**
     * 创建人编码
     */
    private String applicantCode;
}
