package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *  费用申请财务查询的请求对象
 * </p>
 * ApplicationFinancRequsetDTO
 *
 * @author hao.yi
 * @date 2019/3/7
 */
@Data
public class ApplicationFinancRequsetDTO extends ApplicationHeader {
    //单据报销已关联金额
    private BigDecimal associatedAmount;
    //报销可关联金额
    private  BigDecimal relevanceAmount;
    //
    private String companyName;
    private String departmentName;
    private String employeeName;
    private String typeName;
    private String createdName;
}
