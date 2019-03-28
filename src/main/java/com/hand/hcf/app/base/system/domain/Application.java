package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

/**
 * Created by weishan on 2019/3/5.
 * 应用服务
 */
@Data
@TableName("sys_application")
public class Application extends DomainI18n {

    @TableField("app_code")
    private String appCode; //应用代码

    @I18nField
    @TableField("app_name")
    private String appName; // 应用名称

    @TableField(exist = false)
    private String status= Constants.SERVICE_DOWN;

}
