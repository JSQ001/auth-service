package com.hand.hcf.app.mdata.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by yangqi on 2016/12/28.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BankInfoDTO implements Serializable {

    private Long id;

    private String bankCode;//银联号

    private String bankBranchName;//银行全名

    private String bankName;//银行名称

    private Boolean enabled;//是否有效

    private String enabledStr;

    private String countryName; // 国家名称

    private String countryCode;//国家编码

    private String bankType;//银行类型

    private String province;//所在省份

    private String provinceCode;//省编码

    private String city;//所在城市

    private String cityCode;//市编码

    private String bankHead;//所属银行

    private ZonedDateTime createdDate;//创建时间

    private ZonedDateTime lastUpdatedDate;//最后修改时间


    private Long tenantId = 0L;      // 租户id 默认为系统银行

    private String swiftCode;       // swift 编码

    private String openAccount;     // 开户地

    private String detailAddress;   // 详细地址

    private Boolean deleted;    // 是否删除

    private String currencyCode;   // 币种代码

    private String currencyName;   // 币种名称


}
