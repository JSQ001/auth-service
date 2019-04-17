package com.hand.hcf.app.mdata.responsibilityCenter.domain.temp;


import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_res_center_temp")
public class ResponsibilityCenterTemp extends DomainEnable implements Serializable {

    private String responsibilityCenterCode;

    private String responsibilityCenterName;

    private String enabledStr;

    private String batchNumber;

    private String rowNumber;

    private String errorDetail;

    private Boolean errorFlag;

    //账套id
    private Long setOfBooksId;
}
