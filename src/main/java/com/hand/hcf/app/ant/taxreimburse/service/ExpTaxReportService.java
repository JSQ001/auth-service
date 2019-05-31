package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportMapper;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.app.base.code.persistence.SysCodeValueTempMapper;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.implement.web.CompanyControllerImpl;
import com.hand.hcf.app.payment.utils.RespCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报service
 * @date 2019/5/29 11:30
 */
@Service
public class ExpTaxReportService extends BaseService<ExpTaxReportMapper, ExpTaxReport> {
    @Autowired
    ExpTaxReportMapper expTaxReportMapper;

    @Autowired
    private CompanyControllerImpl hcfOrganizationInterface;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private SysCodeValeMapper sysCodeValeMapper;

    public final String NO_BLENDING = "未勾兑";

    public final String BLENDED = "已勾兑";


    /**
     * 获取税金申报List的数据-分页查询
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatusCode
     * @param requestPeriodFrom
     * @param reqDateTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param taxCategoryId
     * @param status
     * @param page
     * @return
     */
    public List<ExpTaxReport> getExpTaxReportByCon(String companyId,/*公司*/
                                                   String currencyCode,/*币种*/
                                                   String blendStatusCode,/*勾兑状态*/
                                                   String requestPeriodFrom,/*申报期间从*/
                                                   String requestPeriodTo,/*申报期间至*/
                                                   BigDecimal requestAmountFrom,/*申报金额从*/
                                                   BigDecimal requestAmountTo,/*申报金额至*/
                                                   String businessSubcategoryName,/*业务小类代码*/
                                                   String taxCategoryId,/*税种代码*/
                                                   Integer status,/*报账状态*/
                                                   Page page) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }

        Wrapper<ExpTaxReport> wrapper = new EntityWrapper<ExpTaxReport>()
                .eq(companyId != null, "company_id", companyId)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .eq(blendStatusCode != null, "blend_status_code", blendStatusCode)
                .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                .le(requestAmountTo != null, "request_amount", requestAmountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                .eq(status != null, "status", status);
        List<ExpTaxReport> expTaxReportList = expTaxReportMapper.selectPage(page, wrapper);
        expTaxReportList.stream().forEach(expTaxReport -> {
            //公司转化
            Map<Long, String> comanyMap = new HashMap<Long, String>();
            if (comanyMap.get(expTaxReport.getCompanyId()) != null) {
                expTaxReport.setCompanyName(comanyMap.get(expTaxReport.getCompanyId()));
            } else {
                CompanyCO otherCompany = hcfOrganizationInterface.getById(expTaxReport.getCompanyId());
                String companyName = Optional
                        .ofNullable(otherCompany)
                        .map(u -> TypeConversionUtils.parseString(u.getName()))
                        .orElseThrow(() -> new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS));
                comanyMap.put(expTaxReport.getCompanyId(), companyName);
                expTaxReport.setCompanyName(companyName);
            }

            //币种转化
            if (StringUtils.isNotEmpty(expTaxReport.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(expTaxReport.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                    expTaxReport.setCurrencyName(currencyOne.getCurrencyName());
                }
            }

            //税种转化
            if (StringUtils.isNotEmpty(expTaxReport.getTaxCategoryCode())) {
                SysCodeValue sysCodeValueOne = new SysCodeValue();
                sysCodeValueOne.setValue(expTaxReport.getTaxCategoryCode());
                SysCodeValue sysCodeValue1 = sysCodeValeMapper.selectOne(sysCodeValueOne);
                if (StringUtils.isNotEmpty(sysCodeValue1.getName())) {
                    expTaxReport.setTaxCategoryName(sysCodeValue1.getName());
                }
            }

            //勾兑状态转化-只有两种状态
            if (StringUtils.isNotEmpty(expTaxReport.getBlendStatusCode())) {
                if ("1001".equals(expTaxReport.getBlendStatusCode())) {
                    expTaxReport.setBlendStatus(NO_BLENDING);
                } else if ("1002".equals(expTaxReport.getBlendStatusCode())) {
                    expTaxReport.setBlendStatus(BLENDED);
                }
            }

        });
        return expTaxReportList;
    }

}
