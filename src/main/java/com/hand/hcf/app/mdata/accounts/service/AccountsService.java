package com.hand.hcf.app.mdata.accounts.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.AccountsCO;
import com.hand.hcf.app.common.co.QueryParameterQO;
import com.hand.hcf.app.common.enums.RangeEnum;
import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.domain.AccountsHierarchy;
import com.hand.hcf.app.mdata.accounts.dto.AccountsDTO;
import com.hand.hcf.app.mdata.accounts.persistence.AccountsMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.DataFilteringUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccountsService extends ServiceImpl<AccountsMapper,Accounts> {

    @Autowired
    private AccountsMapper accountsMapper;
    @Autowired
    private AccountsHierarchyService accountsHierarchyService;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private HcfOrganizationInterface organizationInterface;
    @Autowired
    private MapperFacade mapperFacade;
    /**
     * 新建科目表明细
     *
     * @param accounts
     * @return Accounts
     */
    public Accounts insertAccounts(Accounts accounts) {
        //  参数是否为空校验
        if (accounts.getId() != null){
            throw new BizException(RespCode.ACCOUNTS_ID_NOT_NULL);
        }
        if (accounts.getAccountSetId() == null){
            throw new BizException(RespCode.ACCOUNTS_ID_NULL);
        }
        if (StringUtil.isNullOrEmpty(accounts.getAccountCode())){
            throw new BizException(RespCode.ACCOUNTS_CODE_NULL);
        }
        if (StringUtil.isNullOrEmpty(accounts.getAccountName())){
            throw new BizException(RespCode.ACCOUNTS_NAME_NULL);
        }
        if (accounts.getTenantId() == null) {
            throw new BizException(RespCode.ACCOUNTS_TENANT_ID_NULL);
        }
        //  Code过滤后重新Set
        accounts.setAccountCode(DataFilteringUtil.getDataFilterCode(accounts.getAccountCode()));
        //  Name过滤后重新Set
        //accounts.setAccountName(DataFilteringUtil.getDataFilterName(accounts.getAccountName()));
        //科目名称不做过滤@20190321
        accounts.setAccountName(accounts.getAccountName());
        //  查询判断Code是否重复
        Accounts code = new Accounts();
        Accounts name = new Accounts();
        code.setAccountSetId(accounts.getAccountSetId());
        code.setAccountCode(accounts.getAccountCode());
        name.setAccountSetId(accounts.getAccountSetId());
        name.setAccountName(accounts.getAccountName());
        Accounts codeResult = accountsMapper.selectOne(code);
        Accounts nameResult = accountsMapper.selectOne(name);
        //  查询结果不为空则有重复
        if (codeResult != null){  // code
            throw new BizException(RespCode.ACCOUNTS_CODE_EXISTS);
        }
        if (nameResult != null){  // name
            throw new BizException(RespCode.ACCOUNTS_NAME_EXISTS);
        }
        //  插入校验后的数据
        try {
            accountsMapper.insert(accounts);
        }catch (DuplicateKeyException e){
            throw new BizException("insert error", "科目代码在该科目表已存在！");
        }
        return accounts;
    }

    /**
     * 更新科目表明细
     *
     * @param accounts
     * @return Accounts
     */
    public Accounts updateAccounts(Accounts accounts) {
        if (accounts.getId() == null) {
            throw new BizException(RespCode.ACCOUNTS_NOT_EXISTS);
        }
        //  查看是否存在
        Accounts result = accountsMapper.selectById(accounts);
        if(result == null || result.getDeleted() == true){
            throw new BizException(RespCode.ACCOUNTS_NOT_EXISTS);
        }
        //  检查是否输入科目代码
        if (!StringUtil.isNullOrEmpty(accounts.getAccountCode())){
            //  Code过滤后的结果重新Set
            accounts.setAccountCode(DataFilteringUtil.getDataFilterCode(accounts.getAccountCode()));
            //  检查是否重复
            Accounts code = new Accounts();
            code.setAccountCode(accounts.getAccountCode());
            code.setAccountSetId(accounts.getAccountSetId());
            Accounts codeResult = accountsMapper.selectOne(code);
            if (codeResult != null && !codeResult.getId().equals(accounts.getId())){  // code
                throw new BizException(RespCode.ACCOUNTS_CODE_EXISTS);
            }
        }
        //  检查是否输入科目名称
        if (!StringUtil.isNullOrEmpty(accounts.getAccountName())){
            //  Name过滤后的结果重新Set
            //accounts.setAccountName(DataFilteringUtil.getDataFilterName(accounts.getAccountName()));
            //科目名称不做过滤@20190321
            accounts.setAccountName(accounts.getAccountName());
            //  检查是否重复
            Accounts name = new Accounts();
            name.setAccountName(accounts.getAccountName());
            name.setAccountSetId(accounts.getAccountSetId());
            Accounts nameResult = accountsMapper.selectOne(name);
            if (nameResult != null && !nameResult.getId().equals(accounts.getId())){  // name
                throw new BizException(RespCode.ACCOUNTS_NAME_EXISTS);
            }
        }
        //  汇总标志是否为false
        if (accounts.getSummaryFlag() == false){
            //  查询是否有子科目
            List<AccountsHierarchy> list = accountsHierarchyService.selectList(new EntityWrapper<AccountsHierarchy>()
                .where("deleted = false")
                .eq("parent_account_id",accounts.getId())
            );
            // 汇总科目更改为非汇总时，需要校验其是否有子科目，如果有，则不能更改
            if (list.size() > 0){
                throw new BizException(RespCode.ACCOUNTS_HAS_SUB_ACCONUT);
            }
        }
        //  更新
        try {
            accountsMapper.updateById(accounts);
        }catch (DuplicateKeyException e){
            throw new BizException("update error", "科目代码在该科目表已存在！");
        }
        return accounts;
    }

    /**
     * 根据科目表明细主键ID删除科目表明细
     *
     * @param id 科目表明细主键ID
     * @return
     */
    public Boolean deleteAccounts(Long id) {
        Accounts result = accountsMapper.selectById(id);
        if(null != result && result.getDeleted() != true){ //  删除成功
            result.setDeleted(true);
            result.setAccountCode(result.getAccountCode()+"_DELETE_"+ RandomStringUtils.randomNumeric(6));
            accountsMapper.updateById(result);
        }else { //  删除失败
            throw new BizException(RespCode.ACCOUNTS_NOT_EXISTS);
        }
        // 返回成功标志
        return true;
    }

    /**
     * 科目表明细ID查询(多语言翻译后)
     * @param  id   科目表明细ID
     * @return Accounts
     */
    public Accounts findTransAccountsById(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id,Accounts.class);
    }

    /**
     * 科目表明细ID查询
     * @param  ids  科目表明细ID
     * @return Accounts
     */
    public List<Accounts> findAccountsByIds(List<Long> ids) {
        List<Accounts> list = accountsMapper.selectBatchIds(ids);
        return list;
    }

    /**
     * 分页查询 科目表明细条件查询
     * @param accountSetId 科目表ID
     * @param accountType 科目类型
     * @param info 科目代码或科目名称
     * @param page 分页对象
     * @return
     */
    public Page<AccountsDTO> findAccounts(Long accountSetId,
                                          String accountType,
                                          String info,
                                          Page<AccountsDTO> page) {
        //  最终结果集合
        List<AccountsDTO> list = new ArrayList<>();
        //  判断info是否有值
        if (StringUtil.isNullOrEmpty(info)){
            list = accountsMapper.findAccountsDTO(accountSetId,accountType,null,null,page);

        }else {
            /*//  按code查询
            List<AccountsDTO> codes = accountsMapper.findAccountsDTO(accountSetId,accountType,info,null,page);
            //  按name查询
            List<AccountsDTO> names = accountsMapper.findAccountsDTO(accountSetId,accountType,null,info,page);
            //  根据两个查询结果处理集合
            if (CollectionUtils.isNotEmpty(codes) && !CollectionUtils.isNotEmpty(names)){
                list = codes;
            }else if (!CollectionUtils.isNotEmpty(codes) && CollectionUtils.isNotEmpty(names)) {
                list = names;
            }else if (CollectionUtils.isNotEmpty(codes) && CollectionUtils.isNotEmpty(names)) {
                //  中间变量
                List<AccountsDTO> distinct = new ArrayList<>();
                //  codes值赋值给中间变量
                distinct.addAll(codes);
                //  此时distinct中是A集合中去除A与B交集
                distinct.removeAll(names);
                //  此时codes中是A和B交集
                codes.removeAll(distinct);
                //  此时names中是B集合中去除A与B交集
                names.removeAll(distinct);
                //  将三个结果拼接
                list.addAll(distinct);
                list.addAll(codes);
                list.addAll(names);
                //  排序
                Collections.sort(list, new Comparator<AccountsDTO>() {
                    @Override
                    public int compare(AccountsDTO o1, AccountsDTO o2) {
                        return o1.getAccountCode().compareTo(o2.getAccountCode());
                    }
                });
            }else {
                list = null;
            }*/
            list = accountsMapper.findAccountsDTO(accountSetId,accountType,info,info,page);
        }
        //  判断list是否为空
        if(CollectionUtils.isNotEmpty(list)){
            //  遍历
            list.stream().forEach((AccountsDTO dto) -> {
                //  多语言翻译
                Accounts i18n = baseI18nService.selectOneTranslatedTableInfoWithI18n(dto.getId(),Accounts.class);
                dto.setAccountTypeName(organizationInterface.getValueBySysCodeAndValue("2205", dto.getAccountType()).getName());
                if (dto.getReportType() != null){
                    dto.setReportTypeName(organizationInterface.getValueBySysCodeAndValue("2206",dto.getReportType()).getName());
                }
                dto.setBalanceDirectionName(organizationInterface.getValueBySysCodeAndValue("2207",dto.getBalanceDirection()).getName());
                dto.setAccountName(i18n.getAccountName());
                dto.setAccountDesc(i18n.getAccountDesc());
            });
            //  放入
            page.setRecords(list);
        }
        return page;
    }
    /**
     *  科目表明细条件查询启用条数
     * @param accountSetId 科目表ID
     * @param accountType 科目类型
     * @param info 科目代码或科目名称
     * @return
     */
    public int findAccountsCount(Long accountSetId,
                                 String accountType,
                                 String info){
        return accountsMapper.findAccountsCount(accountSetId,accountType,info,info);
    }
    /**
     * 分页查询 科目表明细条件查询
     * @param setOfBooksId  账套ID
     * @param page 分页对象
     * @return
     */
    public Page<Accounts> findAccountsBySetOfBooksId(Long setOfBooksId, String accountCode, String accountName, List<Long> accountsIds, Page<Accounts> page) {
        List<Accounts> list = accountsMapper.findAccountsBySetOfBooksId(setOfBooksId,accountCode,accountName,page);
        //  判断list是否为空
        if(CollectionUtils.isNotEmpty(list) && CollectionUtils.isNotEmpty(accountsIds)){ //  查询结果需要过滤
            //  创建目标集合
            List<Accounts> targets = new ArrayList<>();
            //  遍历
            list.stream().forEach((Accounts accounts) -> {
                boolean result = true;
                //  根据accountsIds过滤结果
                for (Long id : accountsIds){
                    if (id.equals(accounts.getId())){
                        result = false;
                        break;
                    }
                }
                //  通过检验加入集合
                if (result){
                    targets.add(accounts);
                }
            });
            //  多语言翻译
            targets.stream().forEach((Accounts accounts) -> {
                Accounts i18n = baseI18nService.selectOneTranslatedTableInfoWithI18n(accounts.getId(),Accounts.class);
                accounts.setAccountName(i18n.getAccountName());
                accounts.setAccountDesc(i18n.getAccountDesc());
            });
            //  放入
            page.setRecords(targets);
        }else if(CollectionUtils.isNotEmpty(list) && !CollectionUtils.isNotEmpty(accountsIds)){ //  查询结果不需要过滤
            //  遍历
            list.stream().forEach((Accounts accounts) -> {
                    Accounts i18n = baseI18nService.selectOneTranslatedTableInfoWithI18n(accounts.getId(),Accounts.class);
                    accounts.setAccountName(i18n.getAccountName());
                    accounts.setAccountDesc(i18n.getAccountDesc());
            });
            //  放入
            page.setRecords(list);
        }else {  //  无查询结果
            page.setRecords(null);
        }
        return page;
    }



    /**
     * 给核算模块提供，核算工单类型新增更新关联科目
     * 获取某个核算工单类型下，当前账套下 已分配的、未分配的 科目
     * @return
     */
    /*public PageList<AccountsDTO> getAccountByRange(GeneralLedgerWorkOrderTypeForOtherDTO generalLedgerWorkOrderTypeForOtherDTO, PageList page){
        List<AccountsDTO> list = new ArrayList<>();

        //先通过账套id找到其下唯一的科目表id，在通过科目表id找到其下的科目
        SetOfBooks setOfBooks = setOfBooksService.selectById(generalLedgerWorkOrderTypeForOtherDTO.getSetOfBooksId());

        //全部：all、已选：selected、未选：notChoose
        if (generalLedgerWorkOrderTypeForOtherDTO.getRange().equals("selected")){
            if (CollectionUtils.isEmpty(generalLedgerWorkOrderTypeForOtherDTO.getIdList())){
                page.setRecords(list);
                return page;
            }else {
                List<Accounts> returns = accountsMapper.selectPage(page,
                    new EntityWrapper<Accounts>()
                        .eq("account_set_id", setOfBooks.getAccountSetId())
                        .eq("enabled", true)
                        .eq("deleted",false)
                        .in("id", generalLedgerWorkOrderTypeForOtherDTO.getIdList())
                        .like(generalLedgerWorkOrderTypeForOtherDTO.getCode() != null, "account_code", generalLedgerWorkOrderTypeForOtherDTO.getCode())
                        .like(generalLedgerWorkOrderTypeForOtherDTO.getName() != null, "account_name", generalLedgerWorkOrderTypeForOtherDTO.getName())
                        .eq(generalLedgerWorkOrderTypeForOtherDTO.getType() != null,"account_type",generalLedgerWorkOrderTypeForOtherDTO.getType())
                        .orderBy("account_code")
                );
                returns.stream().forEach(account -> {
                    AccountsDTO accountDTO = new AccountsDTO();
                    BeanUtils.copyProperties(account, accountDTO);
                    list.add(accountDTO);
                });
            }

        }else if (generalLedgerWorkOrderTypeForOtherDTO.getRange().equals("notChoose")){
            List<Accounts> returns = accountsMapper.selectList(
                new EntityWrapper<Accounts>()
                    .eq("account_set_id",setOfBooks.getAccountSetId())
                    .eq("enabled",true)
                    .eq("deleted",false)
                    .notIn("id",generalLedgerWorkOrderTypeForOtherDTO.getIdList())
                    .like(generalLedgerWorkOrderTypeForOtherDTO.getCode() != null, "account_code", generalLedgerWorkOrderTypeForOtherDTO.getCode())
                    .like(generalLedgerWorkOrderTypeForOtherDTO.getName() != null, "account_name", generalLedgerWorkOrderTypeForOtherDTO.getName())
                    .eq(generalLedgerWorkOrderTypeForOtherDTO.getType() != null,"account_type",generalLedgerWorkOrderTypeForOtherDTO.getType())
                    .orderBy("account_code")
            );
            returns.stream().forEach(account -> {
                AccountsDTO accountDTO = new AccountsDTO();
                BeanUtils.copyProperties(account,accountDTO);
                list.add(accountDTO);
            });
        }else if (generalLedgerWorkOrderTypeForOtherDTO.getRange().equals("all")){
            if (generalLedgerWorkOrderTypeForOtherDTO.getIdList().size() > 0){
                List<Accounts> list1 = accountsMapper.selectList(
                    new EntityWrapper<Accounts>()
                        .eq("account_set_id", setOfBooks.getAccountSetId())
                        .eq("enabled", true)
                        .eq("deleted",false)
                        .in("id", generalLedgerWorkOrderTypeForOtherDTO.getIdList())
                        .like(generalLedgerWorkOrderTypeForOtherDTO.getCode() != null, "account_code", generalLedgerWorkOrderTypeForOtherDTO.getCode())
                        .like(generalLedgerWorkOrderTypeForOtherDTO.getName() != null, "account_name", generalLedgerWorkOrderTypeForOtherDTO.getName())
                        .eq(generalLedgerWorkOrderTypeForOtherDTO.getType() != null,"account_type",generalLedgerWorkOrderTypeForOtherDTO.getType())
                        .orderBy("account_code")
                );
                list1.stream().forEach(account -> {
                    AccountsDTO accountDTO = new AccountsDTO();
                    BeanUtils.copyProperties(account,accountDTO);
                    accountDTO.setAssigned(true);
                    list.add(accountDTO);
                });
            }
            List<Accounts> list2 = accountsMapper.selectList(
                new EntityWrapper<Accounts>()
                    .eq("account_set_id", setOfBooks.getAccountSetId())
                    .eq("enabled", true)
                    .eq("deleted",false)
                    .notIn("id",generalLedgerWorkOrderTypeForOtherDTO.getIdList())
                    .like(generalLedgerWorkOrderTypeForOtherDTO.getCode() != null, "account_code", generalLedgerWorkOrderTypeForOtherDTO.getCode())
                    .like(generalLedgerWorkOrderTypeForOtherDTO.getName() != null, "account_name", generalLedgerWorkOrderTypeForOtherDTO.getName())
                    .eq(generalLedgerWorkOrderTypeForOtherDTO.getType() != null,"account_type",generalLedgerWorkOrderTypeForOtherDTO.getType())
                    .orderBy("account_code")
            );
            list2.stream().forEach(account -> {
                AccountsDTO accountDTO = new AccountsDTO();
                BeanUtils.copyProperties(account,accountDTO);
                accountDTO.setAssigned(false);
                list.add(accountDTO);
            });

        }
        page.setTotal(list.size());
        page.setRecords(list);
        return page;
    }*/



    public AccountsCO getById(Long id) {
        Accounts accounts = this.selectById(id);
        return mapperFacade.map(accounts,AccountsCO.class);
    }

    public List<AccountsCO> listByIds(List<Long> ids) {
        List<Accounts> accounts = this.selectBatchIds(ids);
        return mapperFacade.mapAsList(accounts,AccountsCO.class);
    }

    public Page<AccountsCO> pageBySetOfBooksIdByCondition(Long setOfBooksId,
                                                          String accountCode,
                                                          String accountName,
                                                          List<Long> ignoreIds,
                                                          Page<AccountsCO> mybatisPage) {
        String language = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(language)){
            language = LanguageEnum.ZH_CN.getKey();
        }
        Wrapper<AccountsCO> wrapper = new EntityWrapper<AccountsCO>().eq("temp.set_of_books_id", setOfBooksId)
                .like(StringUtils.hasText(accountCode), "temp.account_code", accountCode)
                .like(StringUtils.hasText(accountName), "temp.account_name", accountName)
                .notIn(CollectionUtils.isNotEmpty(ignoreIds), "temp.id", ignoreIds);
        List<AccountsCO> accountsCOS = baseMapper.listBySetOfBooksId(wrapper, language, mybatisPage);
        mybatisPage.setRecords(accountsCOS);
        return mybatisPage;
    }

    public AccountsCO getByCode(String code) {
        Accounts accounts = this.selectOne(new EntityWrapper<Accounts>().eq("account_code",code));
        return mapperFacade.map(accounts,AccountsCO.class);
    }

    public Page<AccountsCO> pageByRangeAndByCondition(QueryParameterQO queryParams, Page<AccountsCO> mybatisPage) {
        String language = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(language)){
            language = LanguageEnum.ZH_CN.getKey();
        }
        Wrapper<AccountsCO> wrapper = new EntityWrapper<AccountsCO>().eq("temp.set_of_books_id", queryParams.getSetOfBooksId())
                .like(StringUtils.hasText(queryParams.getCode()), "temp.account_code", queryParams.getCode())
//                .ge(StringUtils.hasText(queryParams.getCode()), "temp.account_code", queryParams.getCode())
//                .le(StringUtils.hasText(queryParams.getCode()), "temp.account_code", queryParams.getCode())
                .like(StringUtils.hasText(queryParams.getName()), "temp.account_name", queryParams.getName())
                .eq(StringUtils.hasText(queryParams.getType()),"temp.account_type",queryParams.getType())
                .orderBy("temp.account_code");

        if (RangeEnum.SELECTED.equals(queryParams.getRange())){
            //已选id为空
            if(CollectionUtils.isEmpty(queryParams.getExistsIds())){
                return mybatisPage;
            }
            wrapper.in("temp.id", queryParams.getExistsIds());
        }
        if (RangeEnum.NOTCHOOSE.equals(queryParams.getRange())){
            wrapper.notIn(CollectionUtils.isNotEmpty(queryParams.getExistsIds()), "temp.id", queryParams.getExistsIds());
        }
        List<AccountsCO> accountsCOS = baseMapper.listBySetOfBooksId(wrapper, language, mybatisPage);
        mybatisPage.setRecords(accountsCOS);
        return mybatisPage;
    }
}
