package com.hand.hcf.app.workflow.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@TableName("sys_approval_form_property")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalFormProperty extends Domain {

    @TableField(value = "form_oid")
    private UUID formOid;
    private String propertyName;
    private String propertyValue;
    private String propertyOther;

    public ApprovalFormProperty(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

}
