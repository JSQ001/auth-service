package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/3/25 20:15
 * @version: 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityCenterGroupCO {
    //责任中心组Id
    private Long id;

    //责任中心组代码
    private String groupCode;

    //责任中心组名称
    private String groupName;

    //责任中心Ids
    private List<Long> resCenterIds;

}
