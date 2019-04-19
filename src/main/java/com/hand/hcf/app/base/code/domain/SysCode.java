package com.hand.hcf.app.base.code.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.base.system.enums.SysCodeEnum;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by fanfuqiang 2018/11/26
 */
@Data
@TableName("sys_code")
public class SysCode extends DomainI18nEnable{

    @NotNull
    @TableField("code_oid")
    private String codeOid;

    @TableField("name")
    @I18nField
    private String name;

    @TableField("type_flag")
    private SysCodeEnum typeFlag;


    private Long tenantId;

    @TableField("code")
    private String code;  //代码

}
