package com.hand.hcf.app.base.dto;

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

    private String valueKey;

    private String valueKeyCode;

    private String valueKeyDesc;
}
