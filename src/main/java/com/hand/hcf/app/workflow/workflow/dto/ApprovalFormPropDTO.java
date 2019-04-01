package com.hand.hcf.app.workflow.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Wkit on 2017/4/18.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApprovalFormPropDTO {
    private UUID formOid;
    private String propertyName;
    private String propertyValue;
    private String propertyOther;
}
