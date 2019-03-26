package com.hand.hcf.app.mdata.supplier.web.dto;

import com.hand.hcf.app.mdata.bank.dto.BankAccountDTO;
import lombok.Data;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/23 20:47
 */
@Data
public class VendorAccountDTO {
    private Long id;
    private String code;
    private String name;
    private String department;
    private String job;
    private Boolean isEmp;//员工/供应商标志
    private String sign;//唯一标识
    private List<BankAccountDTO> bankInfos;
}
