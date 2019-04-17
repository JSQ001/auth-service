package com.hand.hcf.app.mdata.responsibilityCenter.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("sys_res_center_group")
public class ResponsibilityCenterGroup extends DomainI18nEnable implements Serializable {
    //租户id
    private Long tenantId;

    //账套id
    private Long setOfBooksId;

    //责任中心组代码
    @TableField("group_code")
    private String groupCode;

    //责任中心组名称
    @TableField("group_name")
    @I18nField
    private String groupName;

    //是否关联责任中心
    @TableField(exist = false )
    private Boolean relation;

    //关联责任中心id数据
    @TableField(exist = false)
    private List<Long> responsibilityCenterIdList;

}
