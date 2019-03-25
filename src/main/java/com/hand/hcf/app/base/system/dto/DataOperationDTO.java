package com.hand.hcf.app.base.system.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DataOperationDTO {
    private String username;
    private UUID userOid;
    private String operationTime;
    private String entityType;
    private String operationType;
    private String operationTitle;
    private Long tenantId;
    private String oldObj;
    private String newObj;
    private String currentObj;
    private boolean showMore;
    private boolean showCompare;
}
