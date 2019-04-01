package com.hand.hcf.app.workflow.workflow.dto;


import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author elvis.xu
 * @since 2016-07-11 19:46
 */

public class ApprovalPassUserDTO {
    private UUID userOid;
    private String fullName;
    private String iconUrl;
    private ZonedDateTime latestInvoiceUpdateTime;

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

    public ZonedDateTime getLatestInvoiceUpdateTime() {
        return latestInvoiceUpdateTime;
    }

    public void setLatestInvoiceUpdateTime(ZonedDateTime latestInvoiceUpdateTime) {
        this.latestInvoiceUpdateTime = latestInvoiceUpdateTime;
    }
}
