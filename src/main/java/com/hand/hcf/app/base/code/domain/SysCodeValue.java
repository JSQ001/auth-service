package com.hand.hcf.app.base.code.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by fanfuqiang 2018/11/26
 */
@Data
@TableName("sys_code_value")
public class SysCodeValue extends DomainI18nEnable {


    @I18nField
    @TableField("name")
    @NotBlank
    private String name;

    @TableField(value = "value")
    @NotBlank
    private String value;


    @TableField(value = "code_id")
    private Long codeId;   // 值列表Id


    @TableField(value = "remark")
    private String remark;  // 说明

}
