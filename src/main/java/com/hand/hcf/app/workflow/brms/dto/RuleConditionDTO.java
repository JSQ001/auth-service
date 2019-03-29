package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.workflow.brms.web.filter.SimpleValueDetailSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

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

    /*@Type(typeNumber = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonIgnore
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime createdDate;*/

    private Integer status;

    private UUID companyOid;

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

    private UUID customEnumerationOid;

    private String fieldContent;

    private UUID refCostCenterOid;

    private String costCenterName;
}
