package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 语言
 */
@Data
@TableName("sys_language")
public class Language extends VersionDomainObject {

    @TableField("code")
    private String code; //代码

    @TableField("zh_cn")
    private String zhCn; // 中文

    @TableField("en_us")
    private String enUs; // 英文

    @TableField("module_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID

}
