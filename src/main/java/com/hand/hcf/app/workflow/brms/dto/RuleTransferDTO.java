package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true,value ={"startDateTime","endDateTime"} )
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleTransferDTO {

    private UUID ruleTransferOid;

    private ZonedDateTime createdDate;

    private Integer status;

    private UUID sourceOid;

    private UUID targetOid;

    private String startDate;

    private String endDate;

    private String remark;

    private List<UUID> formOids;

    private Integer alertStatus;

    private String sourceName;

    private String targetName;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

}
