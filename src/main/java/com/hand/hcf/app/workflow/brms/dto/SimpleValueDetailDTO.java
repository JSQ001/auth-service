package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vance on 2017/2/23.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleValueDetailDTO implements Serializable {

    public SimpleValueDetailDTO(ArrayList<String> value, String fieldType) {
        this.value = value;
        this.fieldType = fieldType;
    }

    public SimpleValueDetailDTO(ArrayList<SimpleValueSymbolDTO> list) {
        this.value = value;
    }

    private ArrayList<String> value;

    private List<SimpleValueSymbolDTO> list;

    private String fieldType;

    private List<String> valueOids;

}
