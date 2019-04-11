package com.hand.hcf.app.workflow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserApprovalDTO {
    private Long id;
    private UUID userOid;
    private String employeeCode;
    private String fullName;
}
