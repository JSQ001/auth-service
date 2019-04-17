package com.hand.hcf.app.mdata.period.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import java.io.Serializable;

/**
 * Project Name:会计期间类
 */
@Data
@TableName(value = "sys_period_status")
public class PeriodStatus extends DomainLogicEnable implements Serializable {

    private Long periodId;//期间id

    private Long setOfBooksId;//账套id
    private String periodName;//期间
    private Integer periodSeq;//期间序号
    private String periodStatusCode;//期间状态

    private Long tenantId;//租户id
}
