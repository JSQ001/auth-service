package com.hand.hcf.app.mdata.accounts.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.accounts.domain.AccountsHierarchy;
import com.hand.hcf.app.mdata.accounts.dto.AccountsHierarchyDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountsHierarchyMapper extends BaseMapper<AccountsHierarchy> {
    List<AccountsHierarchyDTO> findParentAccountsHierarchyDTO(@Param("parentAccountId") Long parentAccountId,
                                                              @Param("accountCode") String accountCode,
                                                              @Param("accountName") String accountName,
                                                              Pagination page);

    List<AccountsHierarchyDTO> findChildAccountsHierarchyDTO(@Param("accountSetId") Long accountSetId,
                                                             @Param("parentAccountId") Long parentAccountId,
                                                             @Param("accountCode") String accountCode,
                                                             @Param("accountName") String accountName,
                                                             @Param("codeFrom") String codeFrom,
                                                             @Param("codeTo") String codeTo,
                                                             Pagination page);
}
