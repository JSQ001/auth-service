package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 期间详细信息
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/11/6 15:38
 */

@Data
public class PeriodCO {

    /**
     * 期间名称
     */
    private String periodName;
    /**
     * 期间所在年
     */
    private Integer periodYear;
    /**
     * 期间所在季度
     */
    private Integer quarterNum;
    /**
     * 期间数
     */
    private Integer periodNum;

}
