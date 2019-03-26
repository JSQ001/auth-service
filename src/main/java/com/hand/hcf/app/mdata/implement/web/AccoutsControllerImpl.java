package com.hand.hcf.app.mdata.implement.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AccountsCO;
import com.hand.hcf.app.common.co.QueryParameterQO;
import com.hand.hcf.app.mdata.accounts.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/18
 */

@RestController
public class AccoutsControllerImpl {

    @Autowired
    private AccountsService accountsService;
    /**
     * 根据id 查询科目信息
     *
     * @param id 科目ID
     */
    public AccountsCO getById(@RequestParam("id") Long id){
        return accountsService.getById(id);
    }

    /**
     * 根据科目id集合查询科目信息
     *
     * @param ids 科目id集合
     */
    public List<AccountsCO> listByIds(@RequestBody List<Long> ids){
        return accountsService.listByIds(ids);
    }


    /**
     * 根据账套ID条件查询科目并且忽略传入的ignoreIds
     *
     * @param setOfBooksId 账套id
     * @param accountCode  科目代码 条件查询
     * @param accountName  科目名称 条件查询
     * @param ignoreIds    需要忽略的id集合 添加查询
     * @param page         当前页 从0 开始
     * @param size         每页大小
     */
    public Page<AccountsCO> pageBySetOfBooksIdByCondition(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                          @RequestParam(name = "accountCode", required = false) String accountCode,
                                                          @RequestParam(name = "accountName", required = false) String accountName,
                                                          @RequestBody(required = false) List<Long> ignoreIds,
                                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<AccountsCO> mybatisPage = new Page<>(page + 1, size);
        return accountsService.pageBySetOfBooksIdByCondition(setOfBooksId, accountCode, accountName, ignoreIds, mybatisPage);
    }

    /**
     * 通过科目代码查询科目信息
     *
     * @param code 科目代码
     */
    public AccountsCO getByCode(@RequestParam("code") String code){
        return accountsService.getByCode(code);
    }


    /**
     * 根据所给范围查询科目信息
     *
     * @param queryParams 科目查询参数对象
     * @param page        当前页 从0 开始
     * @param size        每页大小
     */
    public Page<AccountsCO> pageByRangeAndByCondition(@RequestBody QueryParameterQO queryParams,
                                                      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<AccountsCO> mybatisPage = new Page<>(page + 1, size);
        return accountsService.pageByRangeAndByCondition(queryParams, mybatisPage);
    }

}
