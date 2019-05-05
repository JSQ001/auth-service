package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

/**
 * 工作流轨迹
 * @version 1.0
 * @author mh.z
 * @date 2019/05/04
 */
@TableName("sys_wfl_trace")
@Data
public class WorkflowTrace extends Domain {
    @ApiModelProperty(value = "实体类型")
    @TableField("entity_type")
    Integer entityType;

    @ApiModelProperty(value = "实体oid")
    @TableField("entity_oid")
    UUID entityOid;

    @ApiModelProperty(value = "信息")
    @TableField("message")
    String message;

    @ApiModelProperty(value = "详情")
    @TableField("detail")
    String detail;
}
