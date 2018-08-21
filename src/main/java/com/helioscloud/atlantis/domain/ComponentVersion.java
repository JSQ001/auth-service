package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件版本表
 */
@Data
@TableName("sys_component_version")
public class ComponentVersion extends VersionDomainObject {

    @TableField("remark")
    private String remark; //备注

    @TableField("contents")
    private String contents; // 内容信息

    @TableField("component_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long componentId;  // 组件ID

}
