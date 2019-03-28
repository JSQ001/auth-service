package com.hand.hcf.app.mdata.accounts.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.co.AccountsCO;
import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.dto.AccountsDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface AccountsMapper extends BaseMapper<Accounts> {
    List<AccountsDTO> findAccountsDTO(@Param("accountSetId") Long accountSetId,
                                      @Param("accountType") String accountType,
                                      @Param("accountCode") String accountCode,
                                      @Param("accountDesc") String accountDesc,
                                      Pagination page);

    List<Accounts> findAccountsBySetOfBooksId(@Param("setOfBooksId") Long setOfBooksId,
                                              @Param("accountCode") String accountCode,
                                              @Param("accountName") String accountName,
                                              Pagination page);

    int findAccountsCount(@Param("accountSetId") Long accountSetId,
                          @Param("accountType") String accountType,
                          @Param("accountCode") String accountCode,
                          @Param("accountDesc") String accountDesc
    );

    List<AccountsCO> listBySetOfBooksId(@Param("ew") Wrapper<AccountsCO> wrapper,
                                        @Param("language") String language,
                                        RowBounds rowBounds);
}
