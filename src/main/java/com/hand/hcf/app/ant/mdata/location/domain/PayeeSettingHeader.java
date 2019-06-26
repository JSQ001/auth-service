package com.hand.hcf.app.ant.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zihao.yang on 2019-6-11 19:52:16
 */
@Data
@TableName("sys_payee_setting_header")
@ApiModel(description = "收款方配置头表")
public class PayeeSettingHeader extends Domain {

    /**
     * 收款方国家code
     */
    @TableField(value = "payee_country_code")
    @ApiModelProperty(value = "收款方国家code",dataType = "String")
    private String payeeCountryCode;

    /**
     * 收款方城市code
     */
    @TableField(value = "payee_city_code")
    @ApiModelProperty(value = "收款方城市code",dataType = "String")
    private String payeeCityCode;

    /**
     * 付款方国家code
     */
    @TableField(value = "付款方国家code")
    @ApiModelProperty(value = "收款方城市code",dataType = "String")
    private String payerCountryCode;

    /**
     * 付款方城市code
     */
    @TableField(value = "payer_city_code")
    @ApiModelProperty(value = "付款方城市code",dataType = "String")
    private String payerCityCode;
}
