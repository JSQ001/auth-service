package com.hand.hcf.app.mdata.currency.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.cover.CurrencyRateCover;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.domain.CurrencyRate;
import com.hand.hcf.app.mdata.currency.domain.CurrencyStatus;
import com.hand.hcf.app.mdata.currency.dto.CurrencyChangeLogDTO;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyRateMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.system.enums.CurrencyRateSourceEnum;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.workflow.util.StringUtil;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Service
public class CurrencyRateService extends BaseService<CurrencyRateMapper, CurrencyRate> {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateService.class);

    @Autowired
    private CurrencyStatusService currencyStatusService;

    @Autowired
    private CurrencyRateMapper currencyRateMapper;

    @Autowired
    private CurrencyI18nService currencyI18nService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private MapperFacade mapperFacade;

    public CurrencyRateDTO insertCurrencyRate(CurrencyRateDTO currencyRateDTO) {

        //本位币校验(防止前端误传值)
        if (!currencyRateDTO.getBaseCurrencyCode().equals(this.getSetOfBooksBaseCurrency(currencyRateDTO.getSetOfBooksId()))) {
            throw new BizException(RespCode.CURRENCY_5001, new Object[]{currencyRateDTO.getTenantId(), currencyRateDTO.getSetOfBooksId(), currencyRateDTO.getBaseCurrencyCode()});
        }
        //校验新建币种是否已存在
        CurrencyRate dbCurrencyRate = this.selectOne(new EntityWrapper<CurrencyRate>().eq("currency_code", currencyRateDTO.getCurrencyCode()).eq("base_currency_code", currencyRateDTO.getBaseCurrencyCode()).eq("set_of_books_id", currencyRateDTO.getSetOfBooksId()).eq("tenant_id", currencyRateDTO.getTenantId()));
        if (dbCurrencyRate != null) {
            throw new BizException(RespCode.CURRENCY_5005, new Object[]{currencyRateDTO.getTenantId(), currencyRateDTO.getSetOfBooksId(), currencyRateDTO.getBaseCurrencyCode(), currencyRateDTO.getCurrencyCode()});
        }

        //本币情况下，校验汇率值
        this.validateRate(currencyRateDTO);

        //未开启汇率更新服务的公司，用户在新建汇率时不能选择对该币种进行自动更新
        if (currencyRateDTO.getEnableAutoUpdate() && !currencyStatusService.checkAllEnableAutoStatusTrue(currencyRateDTO.getTenantId(), currencyRateDTO.getSetOfBooksId())) {
            throw new BizException(RespCode.CURRENCY_5004, new Object[]{currencyRateDTO.getTenantId(), currencyRateDTO.getSetOfBooksId()});
        }

        //插入币种信息
        CurrencyRate currencyRate = CurrencyRateCover.dtoToCurrencyRate(currencyRateDTO);

        this.insert(currencyRate);
        //插入币种状态信息
        CurrencyStatus currencyStatus = currencyStatusService.insertOrUpdateCurrencyStatus(currencyRateDTO.getCurrencyCode(), currencyRateDTO.getEnabled(), currencyRateDTO.getEnableAutoUpdate(), currencyRateDTO.getSetOfBooksId(), currencyRateDTO.getTenantId());


        BeanUtils.copyProperties(currencyRate, currencyRateDTO);
        BeanUtils.copyProperties(currencyStatus, currencyRateDTO);

        return currencyRateDTO;
    }

    public CurrencyRateDTO updateCurrencyRate(CurrencyRateDTO currencyRateDTO, String language) {
        //数据校验
        CurrencyRate currencyRate = this.selectOne(new EntityWrapper<CurrencyRate>().eq("currency_rate_oid", currencyRateDTO.getCurrencyRateOid()));
        if (null == currencyRate) {
            throw new BizException(RespCode.CURRENCY_5003);
        }
        CurrencyStatus currencyStatus = currencyStatusService.selectOne(new EntityWrapper<CurrencyStatus>().eq("currency_code", currencyRate.getCurrencyCode()).eq("set_of_books_id", currencyRate.getSetOfBooksId()).eq("tenant_id", currencyRate.getTenantId()));
        if (null == currencyStatus) {
            throw new BizException(RespCode.CURRENCY_5003);
        }

        //本币情况下，校验汇率值
        this.validateRate(currencyRateDTO);


        //币种汇率更新时，禁止对币种状态信息进行更新
        if (currencyRateDTO.getEnableAutoUpdate() && !currencyRateDTO.getEnableAutoUpdate().equals(currencyStatus.getEnableAutoUpdate())) {
            throw new BizException(RespCode.CURRENCY_5012);
        }


        //汇率及汇率生效日期为空校验
        if ((currencyRateDTO.getRate() == null || currencyRateDTO.getApplyDate() == null)) {
            throw new BizException(RespCode.CURRENCY_5013);
        }

        CurrencyRate target = new CurrencyRate();
        if (!currencyRate.getRate().equals(currencyRateDTO.getRate()) || !currencyRate.getApplyDate().equals(currencyRateDTO.getApplyDate())) {
            //只对汇率金额、生效日期进行更新
            BeanUtils.copyProperties(currencyRate, target);
            target.setCurrencyRateOid(UUID.randomUUID());
            target.setApplyDate(currencyRateDTO.getApplyDate());
            target.setRate(currencyRateDTO.getRate());
            target.setLastUpdatedDate(ZonedDateTime.now());
            target.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            target.setSource(currencyRateDTO.getSource());
            target.setId(null);
            //新增汇率信息历史记录
            this.insert(target);
            //汇率值、生效日期都未变动，数据不再入库
        } else {
            target = currencyRate;
        }
        BeanUtils.copyProperties(target, currencyRateDTO);
        BeanUtils.copyProperties(currencyStatus, currencyRateDTO);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(Arrays.asList(currencyRateDTO), language);
        return currencyRateDTO;
    }

    public CurrencyRateDTO selectByCurrencyOidAndLanguage(UUID currencyOid, String language) {
        if (StringUtils.isEmpty(language)) {
            language = LanguageEnum.ZH_CN.getKey();
        }
        if (null == currencyOid) {
            return null;
        }
        CurrencyRateDTO currencyRateDTO = currencyRateMapper.selectByCurrencyOid(currencyOid);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(Arrays.asList(currencyRateDTO), language);
        return currencyRateDTO;
    }

    public Boolean deleteByCurrencyRateOid(List<UUID> currencyRateOids) {
        if (CollectionUtils.isEmpty(currencyRateOids)) {
            return true;
        }
        return this.delete(new EntityWrapper<CurrencyRate>().in("currency_rate_oid", currencyRateOids));
    }

    public Page<CurrencyRateDTO> selectHistoryByCurrencyOidAndLanguage(UUID currencyRateOid, String language, String startDate, String endDate, Page<CurrencyRateDTO> page) {
        CurrencyRate currencyRate = this.selectOne(new EntityWrapper<CurrencyRate>().eq("currency_rate_oid", currencyRateOid));
        if (null == currencyRate) {
            throw new BizException(RespCode.CURRENCY_5003);
        }
        ZonedDateTime dateFrom = null;
        if (StringUtils.hasText(startDate)) {
            dateFrom = DateUtil.stringToZonedDateTime(startDate.substring(0, 10));
        }
        ZonedDateTime dateTo = null;
        if (StringUtils.hasText(endDate)) {
            dateTo = DateUtil.stringToZonedDateTime(endDate.substring(0, 10));
            dateTo = dateTo.plusDays(1);
        }

        List<CurrencyRateDTO> currencyRateDTOs = currencyRateMapper.selectHistoryByCurrencyCodeAndSetOfBooksIdAndTenantId(currencyRate.getCurrencyCode(), currencyRate.getSetOfBooksId(), currencyRate.getTenantId(), currencyRate.getCurrencyRateOid(), dateFrom, dateTo, currencyRate.getBaseCurrencyCode(), page);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(currencyRateDTOs, language);
        page.setRecords(currencyRateDTOs);
        return page;
    }

    public Page<CurrencyRateDTO> selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(String baseCurrencyCode, Long setOfBooksId, Long tenantId, String language, Boolean enable, Page page) {
        language = StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language;
        List<CurrencyRateDTO> currencyRateDTOS = currencyRateMapper.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(baseCurrencyCode, setOfBooksId, tenantId, enable, page);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(currencyRateDTOS, language);
        currencyRateDTOS.stream().forEach(e->{
            StringBuilder currencyCodeAndName = new StringBuilder()
                    .append(e.getCurrencyCode())
                    .append("-")
                    .append(e.getCurrencyName());
            e.setCurrencyCodeAndName(currencyCodeAndName.toString());
        });
        page.setRecords(currencyRateDTOS);
        return page;
    }

    public List<CurrencyRateDTO> selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(String baseCurrencyCode, Long setOfBooksId, Long tenantId, String language, Boolean enable) {
        language = StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language;
        List<CurrencyRateDTO> currencyRateDTOS = currencyRateMapper.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(baseCurrencyCode, setOfBooksId, tenantId, enable);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(currencyRateDTOS, language);
        return currencyRateDTOS;
    }

    public Page<CurrencyRateDTO> updateBatch(List<CurrencyRateDTO> currencyRateDTOS, String language, Page page) {
        List<CurrencyRateDTO> targets = new ArrayList<>();
        if (CollectionUtils.isEmpty(currencyRateDTOS)) {
            return page;
        }
        currencyRateDTOS.stream().forEach(u -> targets.add(this.updateCurrencyRate(u, language)));
        page.setRecords(targets);
        return page;
    }

    public List<CurrencyRate> selectActiveCurrencyRatesByCompanyOid(UUID companyOid, List<String> currencyCodes, ZonedDateTime applyDate) {
        List<CurrencyRate> targets = new ArrayList<CurrencyRate>();
        if (CollectionUtils.isEmpty(currencyCodes)) {
            return null;
        }
        currencyCodes.stream().forEach(u -> {
            targets.add(selectActiveCurrencyRateByCompanyOid(companyOid, u, applyDate));
        });
        return targets;
    }

    public CurrencyRate selectActiveCurrencyRateByCompanyOid(UUID companyOid, String currencyCode, ZonedDateTime applyDate) {
        //入参校验
        if (null == currencyCode) {
            return null;
        }
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        if (null == companyOid) {
            log.error("companyOid can not be null");
            validationErrors.add(new ValidationError("companyOid", "companyOid can not be null"));
        }
        if (null == applyDate) {
            log.error("applyDate can not be null");
            validationErrors.add(new ValidationError("applyDate", "applyDate can not be null"));
        }
        if (!CollectionUtils.isEmpty(validationErrors)) {
            throw new ValidationException(validationErrors);
        }
        //数据查询
        String baseCurrencyCode = this.getCompanySetOfBooksBaseCurrency(companyOid);
        CompanyCO companyCO = companyService.getByCompanyOid(String.valueOf(companyOid));
        Long setOfBooksId = companyCO.getSetOfBooksId();
        Long tenantID = companyCO.getTenantId();

        return selectActiveCurrencyRate(tenantID, setOfBooksId, baseCurrencyCode, currencyCode, true, applyDate);
    }

    public List<CurrencyRateDTO> selectAllActiveCurrencyRates(Long tenantId, Long setOfBooksId, String baseCurrencyCode, List<String> currencyCodes, Boolean enable, ZonedDateTime applyDate, String language) {
        if (CollectionUtils.isEmpty(currencyCodes)) {
            return null;
        }
        language = StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language;
        List<CurrencyRateDTO> targets = new ArrayList<>();

        for (String currencyCode : currencyCodes) {
            CurrencyRate currencyRate = this.selectActiveCurrencyRate(tenantId, setOfBooksId, baseCurrencyCode, currencyCode, enable, applyDate);
            CurrencyRateDTO currencyRateDTO = CurrencyRateCover.parse(currencyRate);
            currencyI18nService.i18nTranslateCurrencyRateDTOs(Arrays.asList(currencyRateDTO), language);
            targets.add(currencyRateDTO);
        }
        return targets;
    }

    public CurrencyRate selectActiveCurrencyRate(Long tenantId, Long setOfBooksId, String baseCurrencyCode, String currencyCode, Boolean enable, ZonedDateTime applyDate) {
        List<CurrencyRate> currencyRates = currencyRateMapper.selectCurrencyRates(setOfBooksId, tenantId, baseCurrencyCode, currencyCode, true);
        return this.getActiveCurrencyRateByApplyDate(currencyRates, applyDate);
    }


    public List<CurrencyRate> selectActiveCurrencyRatesByUserOid(UUID userOid, List<String> currencyCodes, ZonedDateTime applyDate) {
        List<CurrencyRate> target = new ArrayList<CurrencyRate>();
        if (CollectionUtils.isEmpty(currencyCodes)) {
            return target;
        }
        currencyCodes.stream().forEach(u -> {
            target.add(selectActiveCurrencyRateByUserOid(userOid, u, applyDate));
        });
        return target;
    }

    public CurrencyRate selectActiveCurrencyRateByUserOid(UUID userOid, String currencyCode, ZonedDateTime applyDate) {
        //入参校验
        if (null == currencyCode) {
            return null;
        }
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        if (null == userOid) {
            log.error("userOid can not be null");
            validationErrors.add(new ValidationError("userOid", "userOid can not be null"));
        }
        if (null == applyDate) {
            log.error("applyDate can not be null");
            validationErrors.add(new ValidationError("applyDate", "applyDate can not be null"));
        }
        if (!CollectionUtils.isEmpty(validationErrors)) {
            throw new ValidationException(validationErrors);
        }

        //数据查询
        String baseCurrencyCode = this.getUserSetOfBooksBaseCurrency(userOid);
        CompanyCO companyCO = companyService.getCompanyByUserOid(userOid);
        Long setOfBooksId = companyCO.getSetOfBooksId();
//        Tenant tenant = userService.findCurrentTenantByUSerOid(userOid);
//        if (null == tenant) {
//            throw new BizException(RespCode.TENANT_NOT_EXIST);
//
//        }
//        Long tenantID = tenant.getId();
        Long tenantID = companyCO.getTenantId();
        return this.selectActiveCurrencyRate(tenantID, setOfBooksId, baseCurrencyCode, currencyCode, true, applyDate);
    }

    public Boolean checkIsExistCurrencyRate(Long tenantId, Long setOfBooksId, String baseCurrencyCode, String currencyCode) {
        List<CurrencyRate> currencyRates = currencyRateMapper.selectCurrencyRates(setOfBooksId, tenantId, baseCurrencyCode, currencyCode, true);
        if (CollectionUtils.isEmpty(currencyRates)) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户账套本位币
     *
     * @param userOid
     * @return
     */
    public String getUserSetOfBooksBaseCurrency(UUID userOid) {
        CompanyCO companyCO = companyService.getCompanyByUserOid(userOid);
        Long distinctSetOfBooksId = companyCO.getSetOfBooksId();
        if (distinctSetOfBooksId == null) {
            return "CNY";
        } else {
            return getSetOfBooksBaseCurrency(distinctSetOfBooksId);
        }
    }

    /**
     * 获取公司账套本位币
     *
     * @param companyOid
     * @return
     */
    public String getCompanySetOfBooksBaseCurrency(UUID companyOid) {
        Long setOfBooksIdByCompanyOid = companyService.getByCompanyOid(String.valueOf(companyOid)).getSetOfBooksId();
        if (setOfBooksIdByCompanyOid == null) {
            return "CNY";
        } else {

            return getSetOfBooksBaseCurrency(setOfBooksIdByCompanyOid);
        }
    }

    /**
     * 获取账套本位币
     *
     * @param setOfBooksId
     * @return
     */
    public String getSetOfBooksBaseCurrency(Long setOfBooksId) {
        SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(setOfBooksId);
        if (null == setOfBooks) {
            throw new BizException(RespCode.CURRENCY_5002);
        }
        return setOfBooks.getFunctionalCurrencyCode();
    }

    public CurrencyRateDTO getActiveCurrencyRateByCurrencyCode(Long tenantId, Long setOfBooksId, String currencyCode, String language) {
        CurrencyRateDTO target = new CurrencyRateDTO();
        String baseCurrencyCode = this.getSetOfBooksBaseCurrency(setOfBooksId);
        List<CurrencyRate> currencyRates = currencyRateMapper.selectCurrencyRates(setOfBooksId, tenantId, baseCurrencyCode, currencyCode, true);
        //获取最新的生效汇率
        CurrencyRate activeCurrencyRate = this.getActiveCurrencyRateByApplyDate(currencyRates, null);
        if (null == activeCurrencyRate) {
            return null;
        }
        //查询币种状态
        CurrencyStatus currencyStatus = currencyStatusService.selectOne(new EntityWrapper<CurrencyStatus>().eq("currency_code", currencyCode).eq("tenant_id", tenantId).eq("set_of_books_id", setOfBooksId));
        BeanUtils.copyProperties(currencyStatus, target);
        BeanUtils.copyProperties(activeCurrencyRate, target);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(Arrays.asList(target), language);
        return target;
    }

    public CurrencyRate insertOrUpdateCurrencyRateForAutoUpdate(CurrencyRate currencyRate) {
        //重复数据过滤
        CurrencyRate dbCurrencyRate = this.selectOne(new EntityWrapper<CurrencyRate>()
                .eq("base_currency_code", currencyRate.getBaseCurrencyCode())
                .eq("currency_code", currencyRate.getCurrencyCode())
                .eq("rate", currencyRate.getRate())
                .eq("apply_date", currencyRate.getApplyDate())
                .eq("set_of_books_id", currencyRate.getSetOfBooksId())
                .eq("tenant_id", currencyRate.getTenantId())
                .eq("source", CurrencyRateSourceEnum.ECB.getSource()));

        //无重复复数据的情况下，才执行插入操作
        if (dbCurrencyRate == null) {
            this.insertOrUpdate(currencyRate);
        }
        return currencyRate;
    }

    public List<CurrencyChangeLogDTO> selectCompanyStandardCurrencyChangeLogByTenantId(Long tenantId) {
        return currencyRateMapper.selectCompanyStandardCurrencyChangeLogByTenantId(tenantId);
    }

    public CurrencyRate insertOrUpdateCurrencyRateForTranslateHistory(CurrencyRate currencyRate) {
        //重复数据过滤
        CurrencyRate dbCurrencyRate = this.selectOne(new EntityWrapper<CurrencyRate>()
                .eq("base_currency_code", currencyRate.getBaseCurrencyCode())
                .eq("currency_code", currencyRate.getCurrencyCode())
                .eq("rate", currencyRate.getRate())
                .eq("apply_date", currencyRate.getApplyDate())
                .eq("set_of_books_id", currencyRate.getSetOfBooksId())
                .eq("tenant_id", currencyRate.getTenantId())
                .eq("last_updated_date", currencyRate.getLastUpdatedDate()));

        //无重复复数据的情况下，才执行插入操作
        if (dbCurrencyRate == null) {
            this.insertOrUpdate(currencyRate);
        }
        return currencyRate;
    }

    /**
     * 生效日汇率求值
     * 1.applyDate为null默认返回当前最新的生效汇率
     * 2.此方法只能处理currencyRates根据生效日期倒叙排列的日期
     *
     * @param currencyRates 根据applyDate和id排倒叙
     * @param applyDate
     * @return
     */
    private CurrencyRate getActiveCurrencyRateByApplyDate(List<CurrencyRate> currencyRates, ZonedDateTime applyDate) {
        if (CollectionUtils.isEmpty(currencyRates)) {
            return null;
        }
        //applyDate为空返回最近的一条生效汇率
        if (applyDate == null) {
            return currencyRates.get(0);
        }
        //applyDate早于最早的汇率生效日期
        if (currencyRates.get(currencyRates.size() - 1).getApplyDate().isAfter(applyDate)) {
            return currencyRates.get(currencyRates.size() - 1);
        }
        //applyDate 大于最近的汇率生效日期
        if (currencyRates.get(0).getApplyDate().isBefore(applyDate)
                || currencyRates.get(0).getApplyDate().isEqual(applyDate)) {
            return currencyRates.get(0);
        }
        //applyDate < 最近的汇率生效日期
        for (int i = 0; i < currencyRates.size() - 1; i++) {
            ZonedDateTime afterDate = currencyRates.get(i).getApplyDate();
            ZonedDateTime beforeDate = currencyRates.get(i + 1).getApplyDate();
            //beforeDate <= applyDate < afterDate 生效汇率取beforeDate
            if (afterDate.isAfter(applyDate) && (beforeDate.isBefore(applyDate) || beforeDate.isEqual(applyDate))) {
                return currencyRates.get(i + 1);
            }
        }
        return null;
    }

    public CurrencyRateDTO getCurrencyRateByOtherAndBaseCurrency(Long setOfBooksId, Long tenantId, String baseCurrency, String otherCurrency, String language) {
        CurrencyRateDTO target = new CurrencyRateDTO();
        List<CurrencyRate> currencyRates = currencyRateMapper.selectList(
                new EntityWrapper<CurrencyRate>()
                        .eq(!StringUtil.isNullOrEmpty(baseCurrency),"tenant_id", tenantId)
                        .eq(!StringUtil.isNullOrEmpty(baseCurrency),"base_currency_code", baseCurrency)
                        .eq(setOfBooksId != null,"set_of_books_id",setOfBooksId)
                        .eq("currency_code", otherCurrency)
        );
        CurrencyRate activeCurrencyRate = this.getActiveCurrencyRateByApplyDate(currencyRates, null);
        if (null == activeCurrencyRate) {
            return null;
        }
        List<CurrencyI18n> currencyI18ns = currencyI18nService.selectList(
                new EntityWrapper<CurrencyI18n>()
                        .eq("currency_code", activeCurrencyRate.getCurrencyCode())
                        .eq("language", StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language)
        );
        mapperFacade.map(activeCurrencyRate,target);
        mapperFacade.map(currencyI18ns.get(0),target);
        return target;
    }


    public CurrencyI18n getOneOtherCurrencyByBaseCurrency(Long tenantId, String baseCurrency, String otherCurrency, String language) {
        List<CurrencyRate> list = currencyRateMapper.selectList(
                new EntityWrapper<CurrencyRate>()
                        .eq(!StringUtil.isNullOrEmpty(baseCurrency),"tenant_id", tenantId)
                        .eq(!StringUtil.isNullOrEmpty(baseCurrency),"base_currency_code", baseCurrency)
                        .eq("currency_code", otherCurrency)
        );
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            List<CurrencyI18n> currencyI18ns = currencyI18nService.selectList(
                    new EntityWrapper<CurrencyI18n>()
                            .eq("currency_code", list.get(0).getCurrencyCode())
                            .eq("language", StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language)
            );
            return currencyI18ns.get(0);
        }

    }

    public void validateRate(CurrencyRateDTO currencyRateDTO) {
        if (currencyRateDTO.getBaseCurrencyCode().equals(currencyRateDTO.getCurrencyCode())) {
            if (currencyRateDTO.getRate() != 1d) {
                throw new BizException(RespCode.CURRENCY_5015);
            }
        }
        //创建汇率-applyDate时间过久,报错标准化 bug17953
        if (currencyRateDTO.getApplyDate() != null && currencyRateDTO.getApplyDate().isBefore(Constants.MIN_DATE_TIME_IN_DB)) {
            throw new BizException(RespCode.E_120712);
        }

    }

    public List<CurrencyRate> getListCurrencyByOtherCurrencies(Long tenantId, String baseCurrency, List<String> otherCurrencies, String language) {
        List<CurrencyRate> list = currencyRateMapper.selectList(
                new EntityWrapper<CurrencyRate>()
                        .eq("tenant_id", tenantId)
                        .eq("base_currency_code", baseCurrency)
                        .in(!CollectionUtils.isEmpty(otherCurrencies), "currency_code", otherCurrencies)
        );
        if (!CollectionUtils.isEmpty(list)) {
            return list;
        }
        return new ArrayList<>();
    }

    public Page<BasicCO> pageCurrenciesByInfoResultBasic(String code, String name, String selectId, String securityType, Long filterId, Page page) {
        SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(filterId);
        /*币种传来的selectId是code---特殊处理*/
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(selectId)) {
            //根据币种code查询币种

            String baseCurrency = setOfBooks.getFunctionalCurrencyCode();
            //CurrencyI18n cny = this.getOneOtherCurrencyByBaseCurrency(setOfBooks.getTenantId(), baseCurrency, selectId, null);
            CurrencyI18n cny = this.getOneOtherCurrencyByBaseCurrency(OrgInformationUtil.getCurrentTenantId(), baseCurrency, selectId, null);
            BasicCO basicCO = BasicCO
                    .builder()
                    .id(cny.getCurrencyCode())
                    .code(cny.getCurrencyCode())
                    .name(cny.getCurrencyName())
                    .build();
            page.setRecords(Arrays.asList(basicCO));
        } else {
            //Page<CurrencyI18n> currencyPage = currencyI18nService.getOneOtherCurrencyByBaseCurrencyAndName(setOfBooks.getTenantId(), "CNY", code, name, null, page);
            Page<CurrencyI18n> currencyPage = currencyI18nService.getOneOtherCurrencyByBaseCurrencyAndName(OrgInformationUtil.getCurrentTenantId(), "CNY", code, name, null, page);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(currencyPage.getRecords())) {
                return page;
            }
            List<BasicCO> list = new ArrayList<>();
            currencyPage.getRecords().stream().forEach(
                    companyStandardCurrency -> {
                        BasicCO basicCO = BasicCO.builder()
                                .id(companyStandardCurrency.getCurrencyCode())
                                .code(companyStandardCurrency.getCurrencyCode())
                                .name(companyStandardCurrency.getCurrencyName()).build();
                        if (companyStandardCurrency.getCurrencyCode().equals("CNY")) {
                            list.add(0, basicCO);
                        } else {
                            list.add(basicCO);
                        }

                    }
            );
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
                page.setRecords(list);
            }
        }
        return page;
    }
}
