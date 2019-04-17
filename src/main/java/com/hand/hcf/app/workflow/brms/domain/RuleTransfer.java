package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 转交人
 */
@TableName("sys_rule_transfer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTransfer extends Domain implements Serializable {
    @NotNull
    private UUID ruleTransferOid;
    private Integer status;
    private UUID sourceOid;
    private UUID targetOid;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String remark;
}
