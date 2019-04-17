package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件版本表
 */
@Data
@TableName("sys_component_version")
public class ComponentVersion extends DomainLogicEnable {

    @TableField("remark")
    private String remark; //备注

    @TableField("contents")
    private String contents; // 内容信息

    @TableField("component_id")

    private Long componentId;  // 组件ID

}
