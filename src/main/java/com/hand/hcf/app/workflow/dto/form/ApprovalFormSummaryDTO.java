package com.hand.hcf.app.workflow.dto.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class ApprovalFormSummaryDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formId;
    private UUID formOid;
    private String formName;
    private String iconName;
    private String messageKey;
    private Integer formType;
    private Boolean proxyForm;//当前用户是否对该单据只有代提权限
    private Integer visibleUserScope;

}
