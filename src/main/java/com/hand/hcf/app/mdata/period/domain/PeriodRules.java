package com.hand.hcf.app.mdata.period.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName(value = "sys_period_rules")
public class PeriodRules extends DomainLogicEnable implements Serializable {

    @NotNull

    private Long periodSetId;//会计期id
    @NotNull
    private Integer periodNum;//月份
    @NotNull
    private String periodAdditionalName;//期间名称附加
    @NotNull
    private Integer monthFrom;//月份从
    @NotNull
    private Integer monthTo;//月份至
    @NotNull
    private Integer dateFrom;//日期从
    @NotNull
    private Integer dateTo;//日期至
    @NotNull
    private Integer quarterNum;//季度

    private Long tenantId;//租户id
}
