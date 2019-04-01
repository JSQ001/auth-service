package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Vance on 2017/1/22.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_drools_rule_detail_res")
public class DroolsRuleDetailResult extends Domain {
    @NotNull
    private UUID droolsRuleDetailResultOid;
    private UUID droolsRuleDetailOid;
    @TableField(value = "drools_rule_detail_res_msg")
    private String droolsRuleDetailResultMessage;
    @TableField(value = "drools_rule_detail_res_flg")
    private Boolean droolsRuleDetailResultFlg;
    private Long droolsRuleDetailId;
    private Long sequenceNumber;
    @TableField(exist = false)
    private DroolsRuleDetail droolsRuleDetail;
}
