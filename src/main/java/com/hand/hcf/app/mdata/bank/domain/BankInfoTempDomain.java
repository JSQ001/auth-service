package com.hand.hcf.app.mdata.bank.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

/**
 * @Auther: chen
 * @Date: 2018/12/21 10:24
 * @Description: 导入数据domain
 */
@Data
@TableName("sys_bank_info_temp")
public class BankInfoTempDomain extends Domain {
    // 国家编码
    private String countryCode;
    // 国家名称
    private String countryName;
    // 银行编码
    private String bankCode;
    // swift 编码
    private String swiftCode;
    // 银行名称
    private String bankName;
    // 支行银行名称
    private String bankBranchName;
    // 开户地
    private String openAccount;
    // 详细地址
    private String detailAddress;
    // 是否启用
    private Boolean enabled;
    //接收模板数据
    private String enabledStr;
    // 错误描述
    private String errorDetail;
    // 错误标识
    private Boolean errorFlag;
    // 行号
    private String rowNumber;
    //批次号
    private String batchNumber;
}
