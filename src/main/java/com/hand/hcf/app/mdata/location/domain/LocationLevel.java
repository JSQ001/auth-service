package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  地点定义级别表
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/26
 */
@TableName("sys_location_level")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("地点级别")
public class LocationLevel extends DomainI18nEnable {
    @ApiModelProperty(hidden = true)
    private Long tenantId;
    @ApiModelProperty("账套id")
    private Long setOfBooksId;
    @ApiModelProperty("地点级别代码")
    @UniqueField
    private String code;
    @I18nField
    @ApiModelProperty("地点级别名称")
    private String name;
    @TableField(value = "remarks", strategy = FieldStrategy.IGNORED)
    @ApiModelProperty("描述")
    private String remarks;
    @ApiModelProperty("账套")
    @TableField(exist = false)
    private String setOfBooksName;
}
