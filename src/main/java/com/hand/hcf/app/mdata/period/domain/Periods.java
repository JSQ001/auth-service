package com.hand.hcf.app.mdata.period.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

@Data
@TableName(value = "sys_periods")
public class Periods extends DomainLogicEnable {

    private Long periodSetId;//会计期id
    private Integer periodYear;//年
    private Integer periodNum;//月份
    private String periodName;//期间
    private Integer periodSeq;//期间序号
    private String startDate;//日期从
    private String  endDate;//日期至
    private Integer quarterNum;//季度

    private Long tenantId;//租户id

}
