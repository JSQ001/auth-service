package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

/**
 * Created by liudong on 2018/1/18.
 */
@Data
public class BankQueryAllDTO {
    private String bankCode; //银行代码
    private String swiftCode; //用于跨国业务，外币业务
    private String bankName; //银行名称
    private String countryCode; //所在国家代码
    private String countryName; //所在国家名称
    private String provinceCode; //所在省份代码
    private String provinceName; //所在省份名称
    private String cityCode; //所在城市代码
    private String cityName; //所在城市名称
    private String districtCode; //区/县代码
    private String districtName; //区/县名称
    private String address; //详细地址
}
