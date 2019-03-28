package com.hand.hcf.app.mdata.contact.dto;

import com.hand.hcf.app.mdata.bank.dto.BankAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 刘亮 on 2018/1/31.
 */
@Data
public class ContactAccountDTO implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String department;
    private String job;
    private Boolean isEmp;//员工/供应商标志
    private String sign;//唯一标识
    private List<BankAccountDTO> bankInfos;
}
