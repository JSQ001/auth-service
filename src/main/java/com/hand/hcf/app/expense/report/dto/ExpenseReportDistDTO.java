package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/22 00:01
 * @remark
 */
@Data
public class ExpenseReportDistDTO extends ExpenseReportDist{

    private String dimension1Name;
    
    private String dimension2Name;
    
    private String dimension3Name;
    
    private String dimension4Name;
    
    private String dimension5Name;
    
    private String dimension6Name;
    
    private String dimension7Name;
    
    private String dimension8Name;
    
    private String dimension9Name;
    
    private String dimension10Name;
    
    private String dimension11Name;
    
    private String dimension12Name;
    
    private String dimension13Name;
    
    private String dimension14Name;
    
    private String dimension15Name;
    
    private String dimension16Name;
    
    private String dimension17Name;
    
    private String dimension18Name;
    
    private String dimension19Name;
    
    private String dimension20Name;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 受益部门名称
     */
    private String departmentName;
    /**
     * 预算部门名称
     */
    private String budgetDepName;
    /**
     * 责任中心名称
     */
    private String responsibilityCenterName;
    /**
     * 序号
     */
    private Integer index;

    private String  reportDocumentNumber;

    private  Long reportId;

    //备注
    private String description;

    //报账单状态
    private Integer status;
}
