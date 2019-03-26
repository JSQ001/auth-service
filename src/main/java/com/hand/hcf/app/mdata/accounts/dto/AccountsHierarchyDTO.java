package com.hand.hcf.app.mdata.accounts.dto;

import lombok.Data;

@Data
public class AccountsHierarchyDTO {

    private Long id;  //  子科目表主键ID

    private Long parentAccountId;  //  父科目ID

    private Long subAccountId;  //  子科目ID
    private String accountCode;  //  子科目代码
    private String accountName;  //  子科目名称
    private String accountDesc;  //  子科目描述
    private String accountType;  //  子科目类型
    private String accountTypeName;  //  子科目类型名称
}
