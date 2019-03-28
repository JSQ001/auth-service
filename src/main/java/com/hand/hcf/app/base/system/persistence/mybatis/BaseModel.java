package com.hand.hcf.app.base.system.persistence.mybatis;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.FieldFill;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Created by lichao on 2017/11/16.
 */
@Getter
@Setter
public abstract class BaseModel<T> extends Model {

    @TableId(value = "id")
    private Long id;
    /**
     * 创建时间
     */
    @TableField(value="created_date",fill= FieldFill.INSERT)
    protected ZonedDateTime createdDate;
    /**
     * 最后更改时间
     */
    @TableField(value="last_updated_date",fill= FieldFill.INSERT_UPDATE)
    protected ZonedDateTime lastUpdatedDate;
    /**
     * 创建人
     */
    @TableField(value="created_by",fill= FieldFill.INSERT)
    protected Long createdBy;
    /**
     * 最后更新人
     */
    @TableField(value="last_updated_by",fill= FieldFill.INSERT_UPDATE)
    protected Long lastUpdatedBy;


}
