package com.hand.hcf.app.mdata.setOfBooks.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.mdata.accounts.domain.AccountSet;
import com.hand.hcf.app.mdata.accounts.service.AccountSetService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.service.CurrencyI18nService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.period.service.PeriodSetService;
import com.hand.hcf.app.mdata.setOfBooks.adapter.SetOfBooksPeriodAdapter;
import com.hand.hcf.app.mdata.setOfBooks.cover.SetOfBooksCover;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksDTO;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksPeriodDTO;
import com.hand.hcf.app.mdata.setOfBooks.persistence.SetOfBooksMapper;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import io.netty.util.internal.StringUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.SET_OF_BOOKS})
public class SetOfBooksService extends ServiceImpl<SetOfBooksMapper, SetOfBooks> {

    private final Logger log = LoggerFactory.getLogger(SetOfBooksService.class);

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private SetOfBooksMapper setOfBooksMapper;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private CurrencyI18nService currencyI18nService;

    @Autowired
    private SetOfBooksCacheService setOfBooksCacheService;

    @Autowired
    private LegalEntityService legalEntityService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PeriodSetService periodSetService;

    @Autowired
    private AccountSetService accountSetService;

    @Autowired
    private HcfOrganizationInterface organizationInterface;

    @Autowired
    private ContactService contactService;


    /**
     * 新建账套
     *
     * @param setOfBooks
     * @return SetOfBooks
     */
    @Transactional
    public SetOfBooks addSetOfBooks(SetOfBooks setOfBooks) {

        //  参数是否为空校验
        if (setOfBooks.getId() != null) {
            throw new BizException(RespCode.SETOFBOOKS_ID_NULL);
        }
        if (StringUtil.isNullOrEmpty(setOfBooks.getSetOfBooksCode())) {
            throw new BizException(RespCode.SETOFBOOKS_CODE_NULL);
        }
        if (StringUtil.isNullOrEmpty(setOfBooks.getPeriodSetCode())) {
            throw new BizException(RespCode.SETOFBOOKS_PERIODSETCODE_NULL);
        }
        if (StringUtil.isNullOrEmpty(setOfBooks.getFunctionalCurrencyCode())) {
            throw new BizException(RespCode.SETOFBOOKS_FCURRENCYCODE_NULL);
        }
        if (setOfBooks.getAccountSetId() == null) {
            throw new BizException(RespCode.SETOFBOOKS_18007);
        }
        if (StringUtil.isNullOrEmpty(setOfBooks.getSetOfBooksName())) {
            throw new BizException(RespCode.SETOFBOOKS_NAME_NULL);
        }
        //  校验是否获取到租户ID
        if (setOfBooks.getTenantId() == null) {
            throw new BizException(RespCode.SETOFBOOKS_TENANTID_NULL);
        }
        //  查询判断是否重复,根据租户ID与Code
        SetOfBooks target = new SetOfBooks();
        target.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        target.setTenantId(setOfBooks.getTenantId());
        SetOfBooks selectResult = setOfBooksMapper.selectOne(target);
        //  查询结果不为空则有重复
        if (selectResult != null) {
            throw new BizException(RespCode.SETOFBOOKS_18001);
        }
        //  插入检验后数据
        setOfBooksMapper.insert(setOfBooks);
        //初始化账套下唯一序列
//        sequenceService.initSequence("SETOFBOOKS",setOfBooks.getTenantId(), setOfBooks.getId());
//        //初始化帐套默认费用类型
//        expenseTypeService.initSetOfBooksDefaultExpenseTypes(setOfBooks.getTenantId(), setOfBooks.getId());


        //创建默认租户级别帐套本位币对本位币的维护
        String baseCode = setOfBooks.getFunctionalCurrencyCode();

        Boolean existCurrency = currencyRateService.checkIsExistCurrencyRate(setOfBooks.getTenantId(), setOfBooks.getId(), baseCode, baseCode);
        if (!existCurrency) {
            CurrencyRateDTO currencyRateDTO = new CurrencyRateDTO();
            currencyRateDTO.setBaseCurrencyCode(baseCode);
            currencyRateDTO.setBaseCurrencyName(currencyI18nService.i18nTranstateByCurrencyCodes(Arrays.asList(baseCode), null).get(baseCode));
            currencyRateDTO.setCurrencyCode(baseCode);
            currencyRateDTO.setCurrencyName(currencyI18nService.i18nTranstateByCurrencyCodes(Arrays.asList(baseCode), null).get(baseCode));
            currencyRateDTO.setRate(1D);
            currencyRateDTO.setApplyDate(ZonedDateTime.now());
            currencyRateDTO.setSource("MANUAL");
            currencyRateDTO.setEnableAutoUpdate(false);
            currencyRateDTO.setEnabled(true);
            currencyRateDTO.setSetOfBooksId(setOfBooks.getId());
            currencyRateDTO.setTenantId(setOfBooks.getTenantId());
            currencyRateDTO.setLastUpdatedDate(ZonedDateTime.now());
            currencyRateService.insertCurrencyRate(currencyRateDTO);
        }
        setOfBooksCacheService.evictTenantSetOfBooks(setOfBooks.getTenantId());
        return setOfBooks;
    }


    /**
     * 更新账套
     *
     * @param setOfBooks
     * @return SetOfBooks
     */
    public SetOfBooks updateSetOfBooks(SetOfBooks setOfBooks) {
        if (setOfBooks.getId() == null) {
            throw new BizException(RespCode.SETOFBOOKS_18010);
        }
        //  使用selectById则不需要像使用selectOne重新创建实体类进行单个参数注入
        SetOfBooks result = setOfBooksMapper.selectById(setOfBooks);
        if (result == null || result.getDeleted() == true) {
            throw new BizException(RespCode.SETOFBOOKS_18002);
        }
        //  检查是否输入账套代码
        if (StringUtil.isNullOrEmpty(setOfBooks.getSetOfBooksCode())) {
            throw new BizException(RespCode.SETOFBOOKS_CODE_NULL);
        }
        //  检查是否输入账套名称
        if (StringUtil.isNullOrEmpty(setOfBooks.getSetOfBooksName())) {
            throw new BizException(RespCode.SETOFBOOKS_NAME_NULL);
        }
        //  如果改变状态为禁用时,需要检查账套下是否存在法人
        if (setOfBooks.getEnabled() == false) {
            //  查询此账套ID的法人
            List<LegalEntity> list = legalEntityService.selectList(new EntityWrapper<LegalEntity>()
                    .where("deleted = false")
                    .eq("set_of_books_id", setOfBooks.getId())
            );
            //  判断是否存在法人
            if (list.size() != 0) {
                throw new BizException(RespCode.SETOFBOOKS_18013);
            }
        }
        setOfBooksMapper.updateById(setOfBooks);
        setOfBooksCacheService.evictTenantSetOfBooks(result.getTenantId());
        return setOfBooks;
    }


    /**
     * 根据账套表主键ID删除账套
     *
     * @param id 账套表ID
     * @return
     */
    public Boolean deleteSetOfBooks(Long id) {
        SetOfBooks setOfBooks = new SetOfBooks();
        setOfBooks.setId(id);
        SetOfBooks result = setOfBooksMapper.selectOne(setOfBooks);
        if (null != result && result.getDeleted() != true) { // 删除成功
            result.setDeleted(true);
            result.setSetOfBooksCode(result.getSetOfBooksCode() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
            setOfBooksMapper.updateById(result);

        } else { // 删除失败
            throw new BizException(RespCode.SETOFBOOKS_18002);
        }
        // 返回成功标志
        return true;
    }

    /**
     * 账套表ID查询
     *
     * @param id 账套表ID
     * @return SetOfBooks
     */
    public SetOfBooks findSetOfBooksById(Long id) {
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, SetOfBooks.class);
    }

    /**
     * 账套表ID查询(多语言翻译后)
     *
     * @param id 账套表ID
     * @return SetOfBooks
     */
    public SetOfBooks findTransSetOfBooksById(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, SetOfBooks.class);
    }

    /**
     * 分页查询 账套表条件查询
     *
     * @param setOfBooksCode 账套代码
     * @param setOfBooksName 账套名字
     * @param roleType       安全角色
     * @param tenantId       租户ID
     * @param page           分页对象
     * @return
     */
    public Page<SetOfBooks> findSetOfBooksByCode(String setOfBooksCode, String setOfBooksName, Boolean enabled, String roleType, Long tenantId, Page<SetOfBooks> page) {
        List<SetOfBooks> list = setOfBooksMapper.selectPage(page, new EntityWrapper<SetOfBooks>()
                .where("deleted = false")
                .eq(enabled != null, "enabled", enabled)
                .like("set_of_books_code", setOfBooksCode)
                .like("set_of_books_name", setOfBooksName)
                .eq("tenant_id", tenantId)
                .orderBy("set_of_books_code")
        );

        //  判断是否为空
        if (CollectionUtils.isNotEmpty(list)) {
            //  多语言翻译
            /* List<SetOfBooks> i18ns = baseI18nService.convertListByLocale(list);*/
            List<SetOfBooks> i18ns = new ArrayList<>();
            list.stream().forEach((SetOfBooks setOfBooks) -> {
                i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(setOfBooks.getId(), SetOfBooks.class));
            });
            page.setRecords(i18ns);
        }
        return page;
    }


    /**
     * 分页查询 账套DTO条件查询
     *
     * @param setOfBooksCode 账套代码
     * @param setOfBooksName 账套名字
     * @param roleType       安全角色
     * @param tenantId       租户ID
     * @param page           分页对象
     * @return
     */
    public Page<SetOfBooksDTO> findSetOfBooksDTO(String setOfBooksCode, String setOfBooksName, Boolean enabled, String roleType, Long tenantId, Page<SetOfBooksDTO> page) {
        List<SetOfBooks> list = new ArrayList<>();
        //  判断是否传入这个参数,并且权限也是租户管理员
        list = setOfBooksMapper.selectPage(page, new EntityWrapper<SetOfBooks>()
                .where("deleted = false")
                .eq(enabled != null, "enabled", enabled)
                .like("set_of_books_code", setOfBooksCode)
                .like("set_of_books_name", setOfBooksName)
                .eq("tenant_id", tenantId)
                .orderBy("enabled",false)
                .orderBy("set_of_books_code")
        );
        //  判断是否为空
        if (CollectionUtils.isNotEmpty(list)) {
            List<SetOfBooksDTO> dtoList = new ArrayList<>();
            // 翻译list
            List<SetOfBooks> i18ns = baseI18nService.convertListByLocale(list);
            // 转化为DTO
            i18ns.stream().forEach((SetOfBooks setOfBooks) -> {
                dtoList.add(SetOfBooksCover.toDTO(setOfBooks));
            });
            // 给DTO中其它表字段赋值
            dtoList.stream().forEach((SetOfBooksDTO dto) -> {
                //  查询会计期ID
                PeriodSet result = periodSetService.selectOne(
                        new EntityWrapper<PeriodSet>()
                                .eq("period_set_code", dto.getPeriodSetCode())
                                .eq("tenant_id", tenantId)
                );
                if (result != null) {
                    dto.setPeriodSetId(result.getId());
                }
                //  查询科目表Code
                AccountSet accountSet = accountSetService.selectById(dto.getAccountSetId());
                if (accountSet != null) {
                    dto.setAccountSetCode(accountSet.getAccountSetCode());
                }
            });

            page.setRecords(dtoList);
        }
        return page;
    }


    /**
     * 头信息查询 账套期间查询
     *
     * @param setOfBooksId 账套ID
     * @param periodSetId  会计期ID
     * @return
     */
    public SetOfBooksPeriodDTO findSetOfBooksPeriodById(Long setOfBooksId, Long periodSetId) {
        SetOfBooks setOfBooks = selectById(setOfBooksId);

        PeriodSet periodSet = periodSetService.selectById(periodSetId);
        if (setOfBooks != null && periodSet != null) {
            SetOfBooksPeriodDTO dto = SetOfBooksPeriodAdapter.toDTO(setOfBooks, periodSet);
            return dto;
        } else {
            return null;
        }
    }


    /**
     * 根据租户id，账套code获取账套
     *
     * @param tenantId：租户id
     * @return
     */
    public SetOfBooks getSetOfBooksByTenantId(Long tenantId, String code) {
        SetOfBooks param = new SetOfBooks();
        param.setTenantId(tenantId);
        param.setSetOfBooksCode(code);
        param.setDeleted(false);
        return setOfBooksMapper.selectOne(param);
    }

    /**
     * 根据租户ID，获取账套
     *
     * @param tenantId
     * @return
     */
    public List<SetOfBooks> getListByTenantId(Long tenantId) {
        Map<String, Object> map = new HashedMap();
        map.put("tenant_id", tenantId);
        map.put("enabled", true);
        List<SetOfBooks> setOfBooksList = selectByMap(map);
        setOfBooksList = baseI18nService.convertListByLocale(setOfBooksList);
        return setOfBooksList;
    }

    public List<SetOfBooks> getCompanySetOfBooks(Long companyId) {
        Company company = companyService.findOne(companyId);
        SetOfBooks setOfBooksById = findSetOfBooksById(company.getSetOfBooksId());
        if (setOfBooksById == null) {
            throw new BizException(RespCode.SETOFBOOKS_18012);
        }
        List<SetOfBooks> list = new ArrayList<>();
        list.add(setOfBooksById);
        return list;
    }


    public Long getSetOfBooksIdByCompanyOid(UUID companyOid) {
        Company company = companyService.getByCompanyOidCache(companyOid);
        return company.getSetOfBooksId();
    }

    /**
     * 根据账套id查询账套
     *
     * @param id：账套id
     * @return
     */
    public SetOfBooks getSetOfBooksById(Long id) {
        SetOfBooks setOfBooks = selectById(id);
        return baseI18nService.convertOneByLocale(setOfBooks);
    }

    public SetOfBooksDTO getSetOfBooksDTOById(Long id) {
        SetOfBooks setOfBooks = getSetOfBooksById(id);
        return SetOfBooksCover.toDTO(setOfBooks);
    }

    /**
     * 根据用户oid查询所属账套
     *
     * @param userOid
     * @return
     */
    public Long getSetOfBooksIdByUserOid(UUID userOid) {
        UserDTO ContactCO = contactService.getUserDTOByUserOid(userOid);
        if (ContactCO == null) {
            return null;
        }
        return this.getDistinctSetOfBooksId(ContactCO.getCompanyId());
    }

    /**
     * 根据用户id查询所属账套
     *
     * @param userId
     * @return
     */
    public Long getSetOfBooksIdByUserId(Long userId) {
        UserDTO ContactCO = contactService.getUserDTOByUserId(userId);
        if (ContactCO == null) {
            return null;
        }
        return this.getDistinctSetOfBooksId(ContactCO.getCompanyId());
    }


    /**
     * 根具当前登录人来获取对应的帐套
     * 老公司：用户法人对应帐套，为空则为公司下第一个法人对应帐套
     * 新公司：用户对应的公司对应的帐套
     */
    public Long getDistinctSetOfBooksId(Long companyId) {
        UUID companyOid = companyService.findOne(companyId).getCompanyOid();

        Long setOfBooksId = null;
        //表单属于公司级别，暂时屏蔽人员的法人帐套取法
        setOfBooksId = this.getSetOfBooksIdByCompanyOid(companyOid);
        return setOfBooksId;
    }

    /**
     * 根据账套code和租户id查询账套信息
     *
     * @param setOfBooksCode：账套code
     * @param tenantId：租户id
     * @return
     */
    public SetOfBooks getSetOfBooksIdByCodeAndTenantId(String setOfBooksCode, Long tenantId) {
        SetOfBooks setOfBooks = new SetOfBooks();
        setOfBooks.setSetOfBooksCode(setOfBooksCode);
        setOfBooks.setTenantId(tenantId);
        return setOfBooksMapper.selectOne(setOfBooks);
    }

    /**
     * 根具当前登录人来获取对应的帐套
     * 老公司：租户下所有账套
     * 新公司：用户对应的公司对应的帐套
     */
    public List<SetOfBooks> getCompanyAvailableSetOfBooksId(Long tenantId, Long companyId) {
        List<SetOfBooks> result = new ArrayList<SetOfBooks>();

        if (companyService.getCompanyById(companyId) != null) {

            //老用户,租户下所有账套
            result.add(this.getSetOfBooksById(OrgInformationUtil.getCurrentSetOfBookId()));

        } else {
            //新租户，帐套为公司对应帐套
            result = this.getCompanySetOfBooks(companyId);
        }
        return baseI18nService.convertListByLocale(result);
    }


    //根据用户oid查询用户所在账套
    public SetOfBooks selectSetOfBooksByUserOid(String userOid) {
        UserDTO ContactCO = contactService.getUserDTOByUserOid(userOid);
        SetOfBooks setOfBooks = selectById(companyService.findOne(ContactCO.getCompanyId()).getSetOfBooksId());
        return setOfBooks;
    }

    public List<SetOfBooks> getSetOfBooksListByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return selectBatchIds(ids);
    }


    public List<SetOfBooks> getSetOfBooksListByIds(List<Long> ids, String keyWord, Page page) {
        Wrapper<SetOfBooks> wrapper = new EntityWrapper<SetOfBooks>().in("id", ids).orderBy("set_of_books_code");
        if (!StringUtils.isEmpty(keyWord)) {
            wrapper.andNew()
                    .like("set_of_books_code", keyWord)
                    .or()
                    .like("set_of_books_name", keyWord);
        }
        if (page == null) {
            return selectList(wrapper);
        }
        return setOfBooksMapper.selectPage(page, wrapper);
    }

    /**
     * 根据租户信息查询账套
     *
     * @param TenantId
     * @return
     */
    public Page<SetOfBooks> getSetOfBooksListByTenantId(Long TenantId,
                                                        String setOfBooksCode,
                                                        String setOfBooksName,
                                                        String keyWord,
                                                        List<Long> excludeIds,
                                                        Page page) {
        Wrapper<SetOfBooks> setOfBooksWrapper = new EntityWrapper<SetOfBooks>()
                .eq("tenant_id", TenantId)
                .like(StringUtils.isNotEmpty(setOfBooksCode), "set_of_books_code", setOfBooksCode)
                .like(StringUtils.isNotEmpty(setOfBooksName), "set_of_books_name", setOfBooksName)
                .notIn(CollectionUtils.isNotEmpty(excludeIds), "id", excludeIds)
                .eq("deleted", false)
                .orderBy("set_of_books_code");
        if (!StringUtils.isEmpty(keyWord)) {
            setOfBooksWrapper.andNew()
                    .like("set_of_books_code", keyWord)
                    .or()
                    .like("set_of_books_name", keyWord);
        }
        List<SetOfBooks> setOfBooks = setOfBooksMapper.selectPage(page, setOfBooksWrapper);

        page.setRecords(setOfBooks);
        return page;
    }

    public List<SetOfBooksInfoCO> setOfBooksToInfoCO(List<SetOfBooks> lists) {
        return mapper.mapAsList(lists, SetOfBooksInfoCO.class);
    }


    public SetOfBooksInfoCO setOfBooksToInfoCO(SetOfBooks books) {
        return mapper.map(books, SetOfBooksInfoCO.class);
    }

    /**
     * 根据租户信息查询账套
     *
     * @param TenantId
     * @return
     */
    public Page<SetOfBooksInfoCO> pageSetOfBooksListByTenantIdAndCond(Long TenantId,
                                                                      String setOfBooksCode,
                                                                      String setOfBooksName,
                                                                      String keyWord,
                                                                      List<Long> excludeIds,
                                                                      Page page) {
        Wrapper<SetOfBooks> setOfBooksWrapper = new EntityWrapper<SetOfBooks>()
                .eq("tenant_id", TenantId)
                .like(StringUtils.isNotEmpty(setOfBooksCode), "set_of_books_code", setOfBooksCode)
                .like(StringUtils.isNotEmpty(setOfBooksName), "set_of_books_name", setOfBooksName)
                .notIn(CollectionUtils.isNotEmpty(excludeIds), "id", excludeIds)
                .eq("deleted", false)
                .orderBy("set_of_books_code");
        if (!StringUtils.isEmpty(keyWord)) {
            setOfBooksWrapper.andNew()
                    .like("set_of_books_code", keyWord)
                    .or()
                    .like("set_of_books_name", keyWord);
        }
        List<SetOfBooks> setOfBooks = setOfBooksMapper.selectPage(page, setOfBooksWrapper);
        page.setRecords(this.setOfBooksToInfoCO(setOfBooks));
        return page;
    }
}
