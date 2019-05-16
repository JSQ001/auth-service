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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormFieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID fieldOid;
    private Integer sequence;
    private String messageKey;
    private String fieldName;
    private FieldType fieldType;
    private String fieldContent;
    private String fieldCode;

}
