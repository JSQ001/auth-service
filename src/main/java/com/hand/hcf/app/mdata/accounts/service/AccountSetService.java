package com.hand.hcf.app.mdata.accounts.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.accounts.domain.AccountSet;
import com.hand.hcf.app.mdata.accounts.persistence.AccountSetMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.utils.DataFilteringUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AccountSetService extends ServiceImpl<AccountSetMapper, AccountSet> {

    private final Logger log = LoggerFactory.getLogger(AccountSetService.class);

    @Autowired
    private AccountSetMapper accountSetMapper;
    @Autowired
    private BaseI18nService baseI18nService;

    /**
     * 新建科目表
     *
     * @param accountSet
     * @return AccountSet
     */
    public AccountSet addAccountSet(AccountSet accountSet) {
        //  参数是否为空校验
        if (accountSet.getId() != null){
            throw new BizException(RespCode.ACCOUNTSET_ID_NOT_NULL);
        }
        if (StringUtil.isNullOrEmpty(accountSet.getAccountSetCode())){
            throw new BizException(RespCode.ACCOUNTSET_CODE_NULL);
        }
        if (StringUtil.isNullOrEmpty(accountSet.getAccountSetDesc())){
            throw new BizException(RespCode.ACCOUNTSET_DESC_NULL);
        }
        if (accountSet.getTenantId() == null){
            throw new BizException(RespCode.ACCOUNTSET_TENANT_ID_NULL);
        }
        //  Code过滤后重新Set
        accountSet.setAccountSetCode(DataFilteringUtil.getDataFilterCode(accountSet.getAccountSetCode()));
        //  Name过滤后重新Set
        //accountSet.setAccountSetDesc(DataFilteringUtil.getDataFilterName(accountSet.getAccountSetDesc()));
        //  Desc只做长度校验
        if (accountSet.getAccountSetDesc().length() > 100){
            throw new BizException(RespCode.DataFilteringUtil_29002);
        }
        //  查询判断是否重复
        AccountSet target = new AccountSet();
        target.setAccountSetCode(accountSet.getAccountSetCode());
        target.setTenantId(accountSet.getTenantId());
        AccountSet selectResult = accountSetMapper.selectOne(target);
        //  查询结果不为空则有重复
        if (selectResult != null){
            throw new BizException(RespCode.ACCOUNTSET_EXISTS);
        }
        //  插入校验后的数据
        accountSetMapper.insert(accountSet);
        return accountSet;
    }


    /**
     * 更新科目表
     *
     * @param accountSet
     * @return AccountSet
     */
    public AccountSet updateAccountSet(AccountSet accountSet) {
        if (accountSet.getId() == null) {
            throw new BizException(RespCode.ACCOUNTSET_ID_NULL);
        }
        //  使用selectById则不需要像使用selectOne重新创建实体类进行单个参数注入
        AccountSet result = accountSetMapper.selectById(accountSet);
        if(result == null || result.getDeleted() == true){
            throw new BizException(RespCode.ACCOUNTSET_NOT_EXISTS);
        }
        //  检查是否输入科目代码
        if (!StringUtil.isNullOrEmpty(accountSet.getAccountSetCode())){
            //  Code过滤后的结果重新Set
            accountSet.setAccountSetCode(DataFilteringUtil.getDataFilterCode(accountSet.getAccountSetCode()));
        }
        //  检查是否输入科目名称
        if (!StringUtil.isNullOrEmpty(accountSet.getAccountSetDesc()) && accountSet.getAccountSetDesc().length() > 100){
            //  Name过滤后的结果重新Set
            //accountSet.setAccountSetDesc(DataFilteringUtil.getDataFilterName(accountSet.getAccountSetDesc()));
            throw new BizException(RespCode.DataFilteringUtil_29002);
        }
        try {
            accountSetMapper.updateById(accountSet);
        }catch (DuplicateKeyException exce){
            throw new BizException(RespCode.ACCOUNTSET_CODE_EXISTS);
        }
        return accountSet;
    }


    /**
     * 根据科目表主键ID删除科目表
     *
     * @param id 科目表主键ID
     * @return
     */
    public Boolean deleteAccountSet(Long id) {
        AccountSet accountSet = new AccountSet();
        accountSet.setId(id);
        AccountSet result = accountSetMapper.selectOne(accountSet);
        if(null != result && result.getDeleted() != true){ //  删除成功
            result.setDeleted(true);
            result.setAccountSetCode(result.getAccountSetCode()+"_DELETE_"+ RandomStringUtils.randomNumeric(6));
            accountSetMapper.updateById(result);
        }else { //  删除失败
            throw new BizException(RespCode.ACCOUNTSET_NOT_EXISTS);
        }
        // 返回成功标志
        return true;
    }

    /**
     * 科目表ID查询(非多语言)
     * @param  id   科目表ID
     * @return AccountSet
     */
    public AccountSet queryAccountSetById(Long id) {
        return accountSetMapper.selectById(id);
    }

    /**
     * 科目表ID查询
     * @param  id   科目表ID
     * @return AccountSet
     */
    public AccountSet findAccountSetById(Long id) {
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, AccountSet.class);
    }

    /**
     * 科目表ID查询(多语言翻译后)
     * @param  id   科目表ID
     * @return AccountSet
     */
    public AccountSet findTransAccountSetById(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, AccountSet.class);
    }

    /**
     * 分页查询 科目表条件查询
     * @param accountSetCode 科目代码
     * @param accountSetDesc 科目描述
     * @param page 分页对象
     * @return
     */
    public Page<AccountSet> findAccountSetByCodeOrDesc(String accountSetCode,
                                                       String accountSetDesc,
                                                       Page<AccountSet> page) {
        List<AccountSet> list = accountSetMapper.selectPage(page,new EntityWrapper<AccountSet>()
            .eq("deleted",false)
            .like("account_set_code",accountSetCode)
            .like("account_set_desc",accountSetDesc)
            .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
            .orderBy("enabled",false)
            .orderBy("account_set_code")
        );
        //  判断是否为空
        if(CollectionUtils.isNotEmpty(list)){
            /*List<AccountSet> i18ns = baseI18nService.convertListByLocale(list);*/
            List<AccountSet> i18ns = new ArrayList<>();
            list.stream().forEach((AccountSet accountSet) -> {
                i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(accountSet.getId(), AccountSet.class));
            });
            page.setRecords(i18ns);
        }
        return page;
    }

    /**
     * 通过租户ID查询科目表
     *
     * @param tenantId
     * @return list
     */
    public AccountSet getAccountSetByTenantId(Long tenantId, String accountSetCode) {
        AccountSet param = new AccountSet();
        param.setTenantId(tenantId);
        param.setAccountSetCode(accountSetCode);
        param.setDeleted(false);
        return accountSetMapper.selectOne(param);
    }

    /**
     * 添加科目表信息
     * @param tenantId：租户ID
     */
    @Transactional()
    public AccountSet addAccountSet(Long tenantId){
        AccountSet accountSet = new AccountSet();
        accountSet.setAccountSetCode("DEFAULT_ACC");
        accountSet.setAccountSetDesc("DEFAULT_DES");
        accountSet.setTenantId(tenantId);
        accountSet.setEnabled(true);
        accountSet.setDeleted(false);
        Map<String, List<Map<String, String>>> i18n=new HashMap<String, List<Map<String, String>>>();
        List<Map<String, String>> detail=new ArrayList<Map<String, String>>();
        Map<String, String> en=new HashMap<String, String>();
        en.put("language","en_US");
        en.put("value","Default chart of accounts");
        detail.add(en);
        Map<String, String> cn=new HashMap<String, String>();
        cn.put("language","zh_cn");
        cn.put("value","默认科目表");
        detail.add(cn);
        i18n.put("accountSetDesc",detail);
        accountSet.setI18n(i18n);
        accountSetMapper.insert(accountSet);
        return accountSet;
    }
}
