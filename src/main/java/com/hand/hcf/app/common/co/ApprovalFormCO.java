package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


/**
 * Created by liuzhiyu on 2018/2/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalFormCO {
    private UUID formOid;
    private Long formId;
    private Integer formType;
    private String formName;
}
