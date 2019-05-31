package com.hand.hcf.app.ant.taxreimburse.service;

import com.alipay.fc.fcbuservice.open.util.commons.StringUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowMapper;
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
 * @description: 银行流水service
 * @date 2019/5/29 11:30
 */
@Service
public class ExpBankFlowService extends BaseService<ExpBankFlowMapper, ExpBankFlow> {
    @Autowired
    ExpBankFlowMapper expBankFlowMapper;

    @Autowired
    private CompanyControllerImpl hcfOrganizationInterface;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    public final String NO_BLENDING = "未勾兑";

    public final String BLENDED = "已勾兑";


    /**
     * 获取税金申报List的数据-分页查询
     *
     * @param companyId
     * @param fundFlowNumber
     * @param bankAccountName
     * @param currencyCode
     * @param payDateFrom
     * @param payDateTo
     * @param flowAmountFrom
     * @param flowAmountTo
     * @param blendStatusCode
     * @param status
     * @param page
     * @return
     */
    public List<ExpBankFlow> getExpBankFlowByCon(String companyId,/*公司*/
                                                 String fundFlowNumber,/*资金流水号*/
                                                 String bankAccountName,/*对方户名*/
                                                 String currencyCode,/*币种*/
                                                 ZonedDateTime payDateFrom,/*支付日期从*/
                                                 ZonedDateTime payDateTo,/*支付日期至*/
                                                 BigDecimal flowAmountFrom,/*流水金额从*/
                                                 BigDecimal flowAmountTo,/*流水金额至*/
                                                 String blendStatusCode,/*勾兑状态*/
                                                 Integer status,/*报账状态*/
                                                 Page page) {
        Wrapper<ExpBankFlow> wrapper = new EntityWrapper<ExpBankFlow>()
                .eq(companyId != null, "company_id", companyId)
                .eq(fundFlowNumber != null, "fund_flow_number", fundFlowNumber)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(bankAccountName), "bank_account_name", bankAccountName)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(payDateFrom != null, "pay_date", payDateFrom)
                .le(payDateTo != null, "pay_date", payDateTo)
                .ge(flowAmountFrom != null, "flow_amount", flowAmountFrom)
                .le(flowAmountTo != null, "flow_amount", flowAmountTo)
                .eq(blendStatusCode != null, "blend_status_code", blendStatusCode)
                .eq(status != null, "status", status);
        List<ExpBankFlow> expBankFlowList = expBankFlowMapper.selectPage(page, wrapper);
        expBankFlowList.stream().forEach(expBankFlow -> {
            //公司转化
            Map<Long, String> comanyMap = new HashMap<Long, String>();
            if (comanyMap.get(expBankFlow.getCompanyId()) != null) {
                expBankFlow.setCompanyName(comanyMap.get(expBankFlow.getCompanyId()));
            } else {
                CompanyCO otherCompany = hcfOrganizationInterface.getById(expBankFlow.getCompanyId());
                String companyName = Optional
                        .ofNullable(otherCompany)
                        .map(u -> TypeConversionUtils.parseString(u.getName()))
                        .orElseThrow(() -> new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS));
                comanyMap.put(expBankFlow.getCompanyId(), companyName);
                expBankFlow.setCompanyName(companyName);
            }

            //币种转化
            if (StringUtils.isNotEmpty(expBankFlow.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(expBankFlow.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                    expBankFlow.setCurrencyName(currencyOne.getCurrencyName());
                }
            }
            //勾兑状态转化-只有两种状态
            if (StringUtils.isNotEmpty(expBankFlow.getBlendStatusCode())) {
                if ("1001".equals(expBankFlow.getBlendStatusCode())) {
                    expBankFlow.setBlendStatus(NO_BLENDING);
                } else if ("1002".equals(expBankFlow.getBlendStatusCode())) {
                    expBankFlow.setBlendStatus(BLENDED);
                }
            }

        });
        return expBankFlowList;
    }

}
