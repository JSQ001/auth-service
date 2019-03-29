package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class RuleApproverUserOidsDTO {

    private List<UUID> ruleApproverUserOids;

    private Map<String,Set<UUID>> ruleApproverMap;
}
