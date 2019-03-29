package com.hand.hcf.app.mdata.bank.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by 刘亮 on 2017/12/19.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivablesDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    //id
    private Long id;

    //员工：员工编号   供应商：供应商编号
    private String code;

    //员工或供应商名称
    private String name;

    //员工职位
    private String job;

    //员工部门
    private String department;

    private List<BankInfo> bankInfos;

//    //员工或供应商银行账号
//    private String number;
//
//    //员工或供应商银行账户名称
//    private String BankNumberName;

    //员工/供应商标志
    private Boolean isEmp;

    private String sign;//唯一标识

//    //银行code
//    private String bankCode;
//
//    //银行name
//    private String bankName;

}
