package com.hand.hcf.app.workflow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ApprovalAddSignScopeDTO implements Serializable{
    /**
     * 公司Oid
     */
    private List<UUID> companyOids = new ArrayList<>();

}
