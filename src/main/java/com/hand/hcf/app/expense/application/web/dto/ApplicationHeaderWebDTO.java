package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
@Data
public class ApplicationHeaderWebDTO extends ApplicationHeader {

    /**
     * 维度信息
     */
    private List<ExpenseDimension> dimensions;

    private List<String> attachmentOidList;

    private List<AttachmentCO> attachments;

    private String companyName;
    private String departmentName;
    private String employeeName;
    private String typeName;
    private String createdName;

    /**
     * 合同编号
     */
    private String contractNumber;

    private BigDecimal reportAmount;
    private BigDecimal reportAbleAmount;
    /**
     * 可关闭金额
     */
    private BigDecimal canCloseAmount;

    /**
     * 关联金额
     */
    private  BigDecimal releaseAmount;
    /**
     * 费用类型
     */
    private  Long expenseType;
    private  String expenseTypeName;

    //是否可同时发起预付款标志（true：可发起，false：不可发起）
    private Boolean prePaymentFlag;
}
