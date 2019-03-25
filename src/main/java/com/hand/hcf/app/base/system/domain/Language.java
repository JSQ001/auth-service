package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/21.
 */
@Data
@TableName("sys_language")
public class Language {

    @TableField("id")
    private Long id;
    @TableField("language")
    private String language;// 中文 zh_cn 英文 eu
    @TableField("language_name")
    private String languageName;
}
