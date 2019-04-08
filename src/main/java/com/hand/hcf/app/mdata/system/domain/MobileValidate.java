package com.hand.hcf.app.mdata.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

/**
* Created by Transy on 2017-10-31.
*/
@Data
@TableName("sys_mobile_validate")
public class MobileValidate extends DomainI18nEnable{

    @TableField(value = "country_name_en")
    private String countryNameEn;
    @TableField(value = "country_name")
    @I18nField
    private String countryName;
    @TableField(value = "short_name")
    private String shortName;
    @TableField(value = "country_code")
    private String countryCode;
    @TableField(value = "mobile_length")
    private int mobileLength;

}
