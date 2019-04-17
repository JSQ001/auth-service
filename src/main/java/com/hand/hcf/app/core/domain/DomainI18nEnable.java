package com.hand.hcf.app.core.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/3 14:06
 * @remark 框架多语言基类，有启用控制的类继承该类，必须使用逻辑删除
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainI18nEnable extends DomainI18n {

    @TableField(value = "enabled",
            strategy = FieldStrategy.NOT_NULL)
    protected Boolean enabled;
}
