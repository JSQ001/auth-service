package com.hand.hcf.app.mdata.area.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by chenliangqin on 16/11/15.
 */

@TableName("sys_level")
@Data
public class Level extends Domain {

    private Long id;

    @TableField("level_oid")
    private UUID levelOid;


    @TableField("company_oid")
    private UUID companyOid;

    @TableField("level_name")
    private String levelName;

    @TableField("deleted")
    private boolean deleted = false;

    @TableField("tenant_id")

    private Long tenantId;

    @TableField("source")

    private Long source;

    @TableField("code")
    private String code;

    @TableField("comment_")
    private String comments;
}
