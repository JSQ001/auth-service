package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/21.
 */
@Data
@TableName("art_language")
public class Language {
    @TableField("id")
    private Long id;
    @TableField("language")
    private String language;// 中文 zh_CN 英文 eu
    @TableField("language_name")
    private String languageName;
}
