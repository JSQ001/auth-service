package com.hand.hcf.app.workflow.dto.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.workflow.brms.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormValueDTO implements Serializable {

    private UUID formOid;
    private UUID formValueOid;
    private UUID bizOid;
    private UUID fieldOid;
    private String fieldName;
    private FieldType fieldType;
    private String value;
    private String messageKey;
    private Integer sequence;
    private String fieldContent;
    //属性code
    private String fieldCode;
}
