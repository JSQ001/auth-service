package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 模块
 */
@Data
@TableName("sys_module")
public class Module extends DomainLogicEnable {

    @TableField("module_code")
    private String moduleCode; //模块代码

    @TableField("module_name")
    private String moduleName; // 模块名称

}
