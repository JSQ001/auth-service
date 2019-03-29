package com.hand.hcf.app.workflow.workflow.domain;

import com.hand.hcf.app.workflow.workflow.dto.CustomProcessMessageDTO;
import com.thoughtworks.xstream.XStream;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public class CustomProcessDomain implements Serializable {

    private int applyType;

    private UUID applyTypeOid;

    private String comment;

    private String businessCode;

    private String applicant;

    private String applicantComment;

    private String nextApprover;

    private String approverPath;

    private String approverIndex;

    private boolean approvalFlag;

    private boolean draftFlag;

    private boolean autoApproval;

    private CustomProcessMessageDTO applicantMessage;

    private CustomProcessMessageDTO lastMessage;

    private Set<CustomProcessMessageDTO> messages;

    private ApprovalForm approvalForm;

    private boolean updated;

    private boolean approvalEndFllag;

    private UUID companyOid;

    private ZonedDateTime invalidDate;

    private UUID bizOid;

    public CustomProcessDomain() {

    }

    public static CustomProcessDomain deSerialize(String serializeString) {
        XStream xstream = new XStream();
        xstream.alias("processDomain", CustomProcessDomain.class);
        xstream.alias("processMessageDTO", CustomProcessMessageDTO.class);
        xstream.alias("approvalForm", ApprovalForm.class);
        return (CustomProcessDomain) xstream.fromXML(serializeString);
    }

    public boolean isAutoApproval() {
        return autoApproval;
    }

    public void setAutoApproval(boolean autoApproval) {
        this.autoApproval = autoApproval;
    }

    public String serialize() {
        //SerializableXmlUtils.formatXML(this);
        XStream xstream = new XStream();
        xstream.alias("processDomain", CustomProcessDomain.class);
        xstream.alias("approvalForm", ApprovalForm.class);
        return xstream.toXML(this);
    }

    public ZonedDateTime getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(ZonedDateTime invalidDate) {
        this.invalidDate = invalidDate;
    }

    public boolean isApprovalEndFllag() {
        return approvalEndFllag;
    }

    public void setApprovalEndFllag(boolean approvalEndFllag) {
        this.approvalEndFllag = approvalEndFllag;
    }

    public UUID getApplyTypeOid() {
        return applyTypeOid;
    }

    public void setApplyTypeOid(UUID applyTypeOid) {
        this.applyTypeOid = applyTypeOid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicantComment() {
        return applicantComment;
    }

    public void setApplicantComment(String applicantComment) {
        this.applicantComment = applicantComment;
    }

    public String getNextApprover() {
        return nextApprover;
    }

    public void setNextApprover(String nextApprover) {
        this.nextApprover = nextApprover;
    }

    public String getApproverPath() {
        return approverPath;
    }

    public void setApproverPath(String approverPath) {
        this.approverPath = approverPath;
    }

    public String getApproverIndex() {
        return approverIndex;
    }

    public void setApproverIndex(String approverIndex) {
        this.approverIndex = approverIndex;
    }

    public boolean isApprovalFlag() {
        return approvalFlag;
    }

    public void setApprovalFlag(boolean approvalFlag) {
        this.approvalFlag = approvalFlag;
    }

    public boolean isDraftFlag() {
        return draftFlag;
    }

    public void setDraftFlag(boolean draftFlag) {
        this.draftFlag = draftFlag;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public UUID getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(UUID companyOid) {
        this.companyOid = companyOid;
    }

    public CustomProcessMessageDTO getApplicantMessage() {
        return applicantMessage;
    }

    public void setApplicantMessage(CustomProcessMessageDTO applicantMessage) {
        this.applicantMessage = applicantMessage;
    }

    public CustomProcessMessageDTO getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(CustomProcessMessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Set<CustomProcessMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(Set<CustomProcessMessageDTO> messages) {
        this.messages = messages;
    }

    public ApprovalForm getApprovalForm() {
        return approvalForm;
    }

    public void setApprovalForm(ApprovalForm approvalForm) {
        this.approvalForm = approvalForm;
    }

    public int getApplyType() {
        return applyType;
    }

    public void setApplyType(int applyType) {
        this.applyType = applyType;
    }

    public UUID getBizOid() {
        return bizOid;
    }

    public void setBizOid(UUID bizOid) {
        this.bizOid = bizOid;
    }
}
