package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleSceneDTO {
    @JsonIgnore
    private Long id;

    private UUID ruleSceneOid;

    private ZonedDateTime createdDate;

    private Integer status;

    private UUID companyOid;

    private String code;

    private String name;

    private String remark;


}
