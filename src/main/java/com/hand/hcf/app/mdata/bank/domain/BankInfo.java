package com.hand.hcf.app.mdata.bank.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * @author zhaowei.zhang01@hand-china.com
 * @description 银行定义表
 * @date 2019-04-17 09:09
 */

@Data
@TableName("sys_bank_info")
public class BankInfo extends DomainLogicEnable {
    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 联行号
     */
    @TableField(value = "bank_code")
    private String bankCode;

    /**
     * 分支行名称
     */
    @TableField(value = "bank_branch_name")
    private String bankBranchName;

    /**
     * 所属银行代码
     */
    @TableField(value = "bank_head")
    private String bankHead;

    /**
     * 所属银行
     */
    @TableField(value = "bank_name")
    private String bankName;

    /**
     * 国家code
     */
    @TableField(value = "country_code")
    private String countryCode;

    /**
     * 国家名称
     */
    @TableField(value = "country_name")
    private String countryName;

    /**
     * 银行类型
     */
    @TableField(value = "bank_type")
    private String bankType;

    /**
     * 省编码
     */
    @TableField(value = "province_code")
    private String provinceCode;

    /**
     * 省名称
     */
    @TableField(value = "province")
    private String province;

    /**
     * 市编码
     */
    @TableField(value = "city_code")
    private String cityCode;

    /**
     * 市名称
     */
    @TableField(value = "city")
    private String city;

    /**
     * swift编码
     */
    @TableField(value = "swift_code")
    private String swiftCode;

    /**
     * 开户地
     */
    @TableField(value = "open_account")
    private String openAccount;

    /**
     * 详细地址
     */
    @TableField(value = "detail_address")
    private String detailAddress;





    //员工或供应商银行账号
    @TableField(exist = false)
    private String number;

}
