package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dong.liu on 2017-11-07
 */
@ApiModel(description = "付款单头信息")
@Data
@TableName("csh_banks_datas")
public class CashBankData extends DomainI18nEnable {

    @ApiModelProperty(value = "银行代码")
    @NotNull
    @TableField("bank_code")
    private String bankCode; //银行代码

    @ApiModelProperty(value = "银行代码")
    @TableField("bank_code_en")
    private String bankCodeEn; //银行代码

    @ApiModelProperty(value = "用于跨国业务，外币业务")
    @TableField("swift_code")
    private String swiftCode; //用于跨国业务，外币业务

    @ApiModelProperty(value = "银行名称")
    @NotNull
    @I18nField
    @TableField("bank_name")
    private String bankName; //银行名称

    @ApiModelProperty(value = "所在国家代码")
    @TableField("country_code")
    private String countryCode; //所在国家代码

    @ApiModelProperty(value = "所在国家名称")
    @TableField("country_name")
    private String countryName; //所在国家名称

    @ApiModelProperty(value = "所在省份代码")
    @NotNull
    @TableField("province_code")
    private String provinceCode; //所在省份代码

    @ApiModelProperty(value = "所在省份名称")
    @TableField("province_name")
    private String provinceName; //所在省份名称

    @ApiModelProperty(value = "所在城市代码")
    @TableField("city_code")
    private String cityCode; //所在城市代码

    @ApiModelProperty(value = "所在城市名称")
    @TableField("city_name")
    private String cityName; //所在城市名称

    @ApiModelProperty(value = "区/县代码")
    @TableField("district_code")
    private String districtCode; //区/县代码

    @ApiModelProperty(value = "区/县名称")
    @TableField("district_name")
    private String districtName; //区/县名称

    @ApiModelProperty(value = "详细地址")
    private String address; //详细地址

}
