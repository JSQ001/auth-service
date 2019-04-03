package com.hand.hcf.app.workflow.dto;

import java.util.UUID;

/**
 * @author elvis.xu
 * @since 2016-07-11 19:46
 */

public class ApprovalUserDTO {
    private UUID userOid;
    private String fullName;
    private String iconUrl;
    private Integer invoiceNum;
    private Double invoiceAmount;

    public UUID getUserOid() {
        return userOid;
    }

    public void setUserOid(UUID userOid) {
        this.userOid = userOid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(Integer invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }
}
