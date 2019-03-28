package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;
import org.apache.ibatis.reflection.Reflector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/8 11:01
 * @remark
 */
@Data
public class ExpenseReportHeaderDTO extends ExpenseReportHeader{

    /**
     * 单据维度布局
     */
    private List<ExpenseDimension> expenseDimensions;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司代码
     */
    private String companyCode;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 部门代码
     */
    private String departmentCode;
    /**
     * 创建人名称
     */
    private String createdName;
    /**
     * 创建人编码
     */
    private String createdCode;
    /**
     * 申请人名称
     */
    private String applicantName;
    /**
     * 申请人编码
     */
    private String applicantCode;
    /**
     * 合同编号
     */
    private String contractNumber;
    /**
     * 收款方代码
     */
    private String payCode;
    /**
     * 收款方代码
     */
    private String payName;
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 税金分摊方式
     */
    private String expTaxDist;
}
