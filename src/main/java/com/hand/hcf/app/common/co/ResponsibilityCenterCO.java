package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 责任中心详情
 * @date 2019/3/5 9:37
 * @version: 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityCenterCO {

    private Long id;

    //责任中心代码
    private String responsibilityCenterCode;

    //责任中心名称
    private String responsibilityCenterName;

    //责任中心类型
    private String responsibilityCenterType;

    //责任中心代码-名称
    private String responsibilityCenterCodeName;
}
