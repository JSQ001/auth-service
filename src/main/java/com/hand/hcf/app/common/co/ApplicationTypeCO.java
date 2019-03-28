package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTypeCO {
    private Long id;

    //费用申请单代码
    private String typeCode;

    //费用申请单名称
    private String typeName;

    //启用禁用
    private Boolean enabled;
}
