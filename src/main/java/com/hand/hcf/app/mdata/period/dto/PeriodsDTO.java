package com.hand.hcf.app.mdata.period.dto;

import com.hand.hcf.app.mdata.period.domain.Periods;
import lombok.Data;


/**
 *总账期间实体视图对象类
 */
@Data
public class PeriodsDTO extends Periods {
    private  String periodStatusCode;//O为状态已经打开，C为状态已关闭
}
