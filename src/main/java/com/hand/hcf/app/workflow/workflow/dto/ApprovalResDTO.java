package com.hand.hcf.app.workflow.workflow.dto;

import java.util.HashMap;
import java.util.Map;

public class ApprovalResDTO {
    private Integer successNum;
    private Integer failNum;
    private Boolean finishFlag;
    private Map<String, String> failReason = new HashMap<>();

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public Map<String, String> getFailReason() {
        return failReason;
    }

    public void setFailReason(Map<String, String> failReason) {
        this.failReason = failReason;
    }

    public Boolean getFinishFlag() {
        return finishFlag;
    }

    public void setFinishFlag(Boolean finishFlag) {
        this.finishFlag = finishFlag;
    }
}
