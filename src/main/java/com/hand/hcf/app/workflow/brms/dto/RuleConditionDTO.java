package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.workflow.brms.util.SimpleValueDetailSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleConditionDTO {

    private UUID ruleConditionOid;



    private Integer status;


    private String code;

    private String name;

    private String remark;

    @NotNull
    private String field;

    @NotNull
    private Integer fieldTypeId;

    private Integer type;

    @NotNull
    private Integer symbol;

    private String value;

    @JsonSerialize(using = SimpleValueDetailSerializer.class)
    @JsonProperty
    private SimpleValueDetailDTO valueDetail;

    private Long batchCode;

    @NotNull
    private Integer entityType;

    @NotNull
    private UUID entityOid;

    private String fieldContent;

}
