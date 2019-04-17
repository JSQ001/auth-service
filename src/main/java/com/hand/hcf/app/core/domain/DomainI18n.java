package com.hand.hcf.app.core.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/3 13:34
 * @remark 框架多语言基类，没有启用控制的实体继承该类，该实体必须使用逻辑删除
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DomainI18n extends BaseI18nDomain implements Serializable {
    @TableId
    protected Long id;

    @TableField(value = "deleted",
            strategy = FieldStrategy.NOT_NULL)
    @TableLogic
    protected Boolean deleted;

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
