package com.hand.hcf.app.mdata.accounts.cover;

import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.dto.AccountsDTO;

/**
 * @author Felix
 * @date 2019/4/17 4:47 PM
 * <p>
 * description:科目实体视图转换
 */
public class AccountsCover {

    /**
     * 科目实体视图转换到科目实体
     * @param accountsDTO
     * @return
     */
    public static Accounts AccountsDTOToAccounts(AccountsDTO accountsDTO){
        Accounts accounts = null;

        if(accountsDTO != null ){
            accounts = new Accounts();
            accounts.setId(accountsDTO.getId());
            accounts.setAccountDesc(accountsDTO.getAccountDesc());
            accounts.setAccountName(accountsDTO.getAccountName());
            accounts.setAccountCode(accountsDTO.getAccountCode());
            accounts.setAccountSetId(accountsDTO.getAccountSetId());
            accounts.setTenantId(accountsDTO.getTenantId());
            accounts.setAccountType(accountsDTO.getAccountType());
            accounts.setSummaryFlag(accountsDTO.getSummaryFlag());
            accounts.setBalanceDirection(accountsDTO.getBalanceDirection());
            accounts.setReportType(accountsDTO.getReportType());
            accounts.setEnabled(accountsDTO.getEnabled());
        }
        return  accounts;
    }
}
