package com.hand.hcf.app.mdata.responsibilityCenter.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_res_center")
public class ResponsibilityCenter extends DomainI18nEnable implements Serializable {
    //租户id
    private Long tenantId;

    //账套id
    private Long setOfBooksId;

    //账套名称
    @TableField(exist = false)
    private String setOfBooksName;

    //责任中心代码-名称
    @TableField(exist = false)
    private String responsibilityCenterCodeName;

    //责任中心代码
    @TableField("responsibility_center_code")
    private String responsibilityCenterCode;

    //责任中心名称
    @TableField("responsibility_center_name")
    @I18nField
    private String responsibilityCenterName;

    //责任中心类型
    @TableField("responsibility_center_type")
    private String responsibilityCenterType;

}
