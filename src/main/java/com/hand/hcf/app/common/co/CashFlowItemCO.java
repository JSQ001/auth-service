package com.hand.hcf.app.common.co;

import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

/**
 * @description: 现金流项
 * @version: 1.0
 * @author: shouting.cheng@hand-china.com
 * @date: 2019/04/04
 */
@Data
public class CashFlowItemCO extends DomainObjectDTO {
    private Long setOfBookId;//帐套ID

    private String flowCode;//现金流量项代码

    private String description;//现金流量项描述
}
