package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @description: 地点级别分配地点实体类
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/27 13:24
 */
@TableName("sys_location_level_assign")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("地点级别分配地点")
public class LocationLevelAssign extends Domain {
    /**
     * 地点级别id
     */
    @ApiModelProperty("地点级别id")
    private Long levelId;
    /**
     * 地点id
     */
    @ApiModelProperty("地点id")
    private Long locationId;

    /**
     * 地点code
     */
    @ApiModelProperty("地点code")
    @TableField(exist = false)
    private String code;
    /**
     * 国家
     */
    @ApiModelProperty("国家")
    @TableField(exist = false)
    private String country;
    /**
     * 国家code
     */
    @ApiModelProperty("国家code")
    @TableField(exist = false)
    private String countryCode;
    /**
     *  城市
     */
    @ApiModelProperty("城市")
    @TableField(exist = false)
    private String city;
    /**
     * 城市code
     */
    @ApiModelProperty("城市code")
    @TableField(exist = false)
    private String cityCode;
    /**
     * 区县
     */
    @ApiModelProperty("区县")
    @TableField(exist = false)
    private String district;
    /**
     * 区县code
     */
    @ApiModelProperty("区县code")
    @TableField(exist = false)
    private String districtCode;
    /**
     *  地点类型
     */
    @ApiModelProperty("地点类型")
    @TableField(exist = false)
    private String type;
    /**
     *  地点类型描述
     */
    @ApiModelProperty("地点类型描述")
    @TableField(exist = false)
    private String typeDesc;
    /**
     * 省市
     */
    @ApiModelProperty("省市")
    @TableField(exist = false)
    private String state;
    /**
     * 省市代码
     */
    @ApiModelProperty("省市代码")
    @TableField(exist = false)
    private String stateCode;
    /**
     * 描述
     */
    @ApiModelProperty("描述")
    @TableField(exist = false)
    private String description;


}
