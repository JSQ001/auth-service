package com.hand.hcf.app.mdata.dimension.domain.temp;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

@Data
@TableName("sys_dimension_item_temp")
public class DimensionItemTemp extends DomainEnable {
    //维度ID
    private Long dimensionId;
    //维值代码
    private String dimensionItemCode;
    //维值名称
    private String dimensionItemName;

    private String enabledStr;

    private String batchNumber;
    private String rowNumber;
    private String errorDetail;
    private Boolean errorFlag;
}
