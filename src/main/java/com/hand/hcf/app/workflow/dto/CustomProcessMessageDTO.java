package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.common.co.WorkflowMessageCO;

import java.time.ZonedDateTime;
import java.util.UUID;

//  CustomProcessDTO
public class CustomProcessMessageDTO extends WorkflowMessageCO {

    private String operation;

    private String createDate;

    private String message;

    private String comment;

    private String userName;

    private UUID userOID;

    private ZonedDateTime createdDate;

    public CustomProcessMessageDTO(String operation, String comment, String userName, UUID userOID) {
        this.operation = operation;
        this.comment = comment;
        this.userName = userName;
        this.userOID = userOID;
        this.createdDate = ZonedDateTime.now();
    }

    public CustomProcessMessageDTO(String operation, String createDate, String message, String comment, String userName, UUID userOID) {
        this.operation = operation;
        this.createDate = createDate;
        this.message = message;
        this.comment = comment;
        this.userName = userName;
        this.userOID = userOID;
        this.createdDate = ZonedDateTime.now();
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getUserOID() {
        return userOID;
    }

    public void setUserOID(UUID userOID) {
        this.userOID = userOID;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
