package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by vance on 2017/2/23.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleValueSymbolDTO implements Serializable {

    public SimpleValueSymbolDTO(String value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    private String value;

    private String symbol;

    private String valueOids;

    @Override
    public String toString() {
        return "{ \"value\" =" + value + ", \"symbol\"=\"" + symbol + "\"}";
    }

}
