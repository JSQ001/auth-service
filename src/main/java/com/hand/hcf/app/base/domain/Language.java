package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/21.
 */
@Data
@TableName("art_language")
public class Language {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;
    @TableField("language")
    private String language;// 中文 zh_CN 英文 eu
    @TableField("language_name")
    private String languageName;
}
