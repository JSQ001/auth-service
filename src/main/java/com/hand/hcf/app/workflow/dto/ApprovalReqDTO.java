package com.hand.hcf.app.workflow.dto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ApprovalReqDTO {

    @NotNull
    private List<Entity> entities;
    private String approvalTxt;
    private UUID approvalOid;
    private UUID formOid; //2018-11-20 增加

    public UUID getFormOid() {
        return formOid;
    }

    public void setFormOid(UUID formOid) {
        this.formOid = formOid;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public String getApprovalTxt() {
        return approvalTxt;
    }

    public void setApprovalTxt(String approvalTxt) {
        this.approvalTxt = approvalTxt;
    }

    public UUID getApprovalOid() {
        return approvalOid;
    }

    public void setApprovalOid(UUID approvalOid) {
        this.approvalOid = approvalOid;
    }

    public static class Entity {
        private String entityOid;
        private Integer entityType;
        private List<UUID> countersignApproverOids;
        private boolean priceAuditor = false; //是否进行机票价格审核（针对订票申请单）
        //chain上的审批人
        private String approverOid;

        public List<UUID> getCountersignApproverOids() {
            return countersignApproverOids;
        }

        public void setCountersignApproverOids(List<UUID> countersignApproverOids) {
            this.countersignApproverOids = countersignApproverOids;
        }

        public String getEntityOid() {
            return entityOid;
        }

        public void setEntityOid(String entityOid) {
            this.entityOid = entityOid;
        }

        public Integer getEntityType() {
            return entityType;
        }

        public void setEntityType(Integer entityType) {
            this.entityType = entityType;
        }

        public boolean isPriceAuditor() {
            return priceAuditor;
        }

        public void setPriceAuditor(boolean priceAuditor) {
            this.priceAuditor = priceAuditor;
        }

        public String getApproverOid() {
            return approverOid;
        }

        public void setApproverOid(String approverOid) {
            this.approverOid = approverOid;
        }
    }
}
