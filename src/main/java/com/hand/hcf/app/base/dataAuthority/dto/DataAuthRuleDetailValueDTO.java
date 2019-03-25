package com.hand.hcf.app.base.dataAuthority.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/11/27 16:10
 * @remark
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataAuthRuleDetailValueDTO {

    /**
     * 明细值
     */
    private String valueKey;

    /**
     * 明细值代码
     */
    private String valueKeyCode;

    /**
     * 明细值名称
     */
    private String valueKeyDesc;

    /**
     * 数据取值方式描述
     */
    private String filtrateMethodDesc;
}
