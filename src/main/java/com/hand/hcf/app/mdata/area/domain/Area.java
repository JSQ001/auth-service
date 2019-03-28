package com.hand.hcf.app.mdata.area.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@TableName("sys_area")
@Data
public class Area extends Domain {

    private Long id;

    @NotEmpty
    @TableField("code")
    private String code;

    @NotEmpty
    @TableField("name")
    private String name;
    @NotNull
    @TableField("type")
    private String areaType;

    @TableField("parent_id")
    private Long parentId;

}
