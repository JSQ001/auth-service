package com.hand.hcf.app.core.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/3 13:34
 * @remark 框架基类，除临时表以及多语言类，不需要使用逻辑删除，并有启用控制的实体继承该类或其子类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DomainEnable extends Domain {

    @TableField(value = "enabled",
            strategy = FieldStrategy.NOT_NULL)
    protected Boolean enabled;
}
