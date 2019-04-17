

package com.hand.hcf.app.core.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/3 13:34
 * @remark 框架基类，除临时表以及多语言类，都需要继承该类或其子类。
 * @remark 使用范围：非定义类数据，不需要使用逻辑删除，并无启用标志的数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
abstract public class Domain extends BaseObject {

    @TableField(fill = FieldFill.INSERT)
    protected ZonedDateTime createdDate;

    @TableField(fill = FieldFill.INSERT)
    protected Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected ZonedDateTime lastUpdatedDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Long lastUpdatedBy;

    @TableField(strategy = FieldStrategy.NOT_NULL)
    @Version
    protected Integer versionNumber;
}
