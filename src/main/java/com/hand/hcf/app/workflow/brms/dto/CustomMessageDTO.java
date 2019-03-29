package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement
@Data
public class CustomMessageDTO {

    private String ruleFiredResult;
    private String message;
    private String formFieldOid;
    private String actualResult;
    private String expectedResult;
    private Boolean firedRuleFlg;

    public CustomMessageDTO() {
    }

    public CustomMessageDTO(String ruleFiredResult, String message) {
        this.ruleFiredResult = ruleFiredResult;
        this.message = message;
    }

}
