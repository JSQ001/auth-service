package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowMapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private CompanyService companyService;

    public final String WARNING = "不可删除已勾兑的数据！";


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
     * @param blendStatus
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
                                                 Boolean blendStatus,/*勾兑状态*/
                                                 Boolean status,/*报账状态*/
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
                .eq(blendStatus != null, "blend_status", blendStatus)
                .eq(status != null, "status", status);
        List<ExpBankFlow> expBankFlowList = expBankFlowMapper.selectPage(page, wrapper);
        converDesc(expBankFlowList);
        return expBankFlowList;
    }

    /**
     * 分页查询银行流水数据
     *
     * @param companyId
     * @param fundFlowNumber
     * @param bankAccountName
     * @param currencyCode
     * @param payDateFrom
     * @param payDateTo
     * @param flowAmountFrom
     * @param flowAmountTo
     * @param blendStatus
     * @param status
     * @param page
     * @return
     */
    public Page<ExpBankFlow> getBankFlowByPage(
            String companyId,/*公司*/
            String fundFlowNumber,/*资金流水号*/
            String bankAccountName,/*对方户名*/
            String currencyCode,/*币种*/
            ZonedDateTime payDateFrom,/*支付日期从*/
            ZonedDateTime payDateTo,/*支付日期至*/
            BigDecimal flowAmountFrom,/*流水金额从*/
            BigDecimal flowAmountTo,/*流水金额至*/
            Boolean blendStatus,/*勾兑状态*/
            Boolean status,/*报账状态*/
            Page page
    ) {

        Wrapper<ExpBankFlow> wrapper =
                new EntityWrapper<ExpBankFlow>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(fundFlowNumber != null, "fund_flow_number", fundFlowNumber)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(bankAccountName), "bank_account_name", bankAccountName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .le(payDateTo != null, "pay_date", payDateTo)
                        .ge(flowAmountFrom != null, "flow_amount", flowAmountFrom)
                        .le(flowAmountTo != null, "flow_amount", flowAmountTo)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .eq(status != null, "status", status);


        List<ExpBankFlow> expBankFlowList = this.selectPage(page, wrapper).getRecords();
        converDesc(expBankFlowList);
        return page.setRecords(expBankFlowList);
    }

    /**
     * 导出银行流水数据
     *
     * @param companyId
     * @param fundFlowNumber
     * @param bankAccountName
     * @param currencyCode
     * @param payDateFrom
     * @param payDateTo
     * @param flowAmountFrom
     * @param flowAmountTo
     * @param blendStatus
     * @param status
     * @return
     */

    public List<ExpBankFlow> exportExpBankFlow(
            String companyId,/*公司*/
            String fundFlowNumber,/*资金流水号*/
            String bankAccountName,/*对方户名*/
            String currencyCode,/*币种*/
            ZonedDateTime payDateFrom,/*支付日期从*/
            ZonedDateTime payDateTo,/*支付日期至*/
            BigDecimal flowAmountFrom,/*流水金额从*/
            BigDecimal flowAmountTo,/*流水金额至*/
            Boolean blendStatus,/*勾兑状态*/
            Boolean status/*报账状态*/
    ) {
        Wrapper<ExpBankFlow> wrapper =
                new EntityWrapper<ExpBankFlow>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(fundFlowNumber != null, "fund_flow_number", fundFlowNumber)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(bankAccountName), "bank_account_name", bankAccountName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .le(payDateTo != null, "pay_date", payDateTo)
                        .ge(flowAmountFrom != null, "flow_amount", flowAmountFrom)
                        .le(flowAmountTo != null, "flow_amount", flowAmountTo)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .eq(status != null, "status", status);

        List<ExpBankFlow> expTaxReportList = this.selectList(wrapper);
        converDesc(expTaxReportList);
        return expTaxReportList;

    }

    /**
     * 转换DESC
     *
     * @param expBankFlowList
     * @return
     */
    public List<ExpBankFlow> converDesc(List<ExpBankFlow> expBankFlowList) {
        expBankFlowList.stream().forEach(expBankFlow -> {
            //公司转化
            if (null != expBankFlow.getCompanyId()) {
                Company company = companyService.selectById(expBankFlow.getCompanyId());
                if (StringUtils.isNotEmpty(company.getName())) {
                    expBankFlow.setCompanyName(company.getName());
                }
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

        });
        return expBankFlowList;
    }

    ;


    /**
     * 根据Id 批量删除银行流水数据
     *
     * @param rowIds
     */
    public void deleteBankFlow(String rowIds) {
        String idsArr[] = rowIds.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpBankFlow expBankFlow = expBankFlowMapper.selectById(idsArr[i]);
            //当勾兑状态为0或者false--未勾兑时 才可以删除
            if ((!expBankFlow.getBlendStatus()) || (expBankFlow.getBlendStatus() == false)) {
                expBankFlowMapper.deleteById(Long.valueOf(idsArr[i]));
            } else {
                throw new BizException(WARNING);
            }
        }
    }

    /**
     * 根据相同的公司和币种更新数据状态
     *
     * @param companyId
     * @param currencyCode
     */
    public void updateBankFlow(Long companyId, String currencyCode) {
        expBankFlowMapper.updateBankFlow(companyId, currencyCode);
    }

    /**
     * 根据外键获取税金明细信息(分页查询）--报账单详情数据税金信息查询
     *
     * @param headerId
     * @param page
     * @return
     */
    public List<ExpBankFlow> getBankFlowDetailList(String headerId, Page page) {
        List<ExpBankFlow> expBankFlowList = new ArrayList<>();
        if(StringUtils.isNotEmpty(headerId)){
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpBankFlow> wrapper = new EntityWrapper<ExpBankFlow>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expBankFlowList = expBankFlowMapper.selectPage(page, wrapper);
            expBankFlowList.stream().forEach(expBankFlow -> {
                //公司转化
                if (null != expBankFlow.getCompanyId()) {
                    Company company = companyService.selectById(expBankFlow.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expBankFlow.setCompanyName(company.getName());
                    }
                }
            });
        }
        return expBankFlowList;
    }

    /**
     * 根据外键获取支付明细信息(分页查询）--导出数据用
     *
     * @param headerId
     * @param page
     * @return
     */
    public Page<ExpBankFlow> getPaymentDetail(String headerId, Page page) {
        List<ExpBankFlow> expBankFlowList = new ArrayList<>();
        if(StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpBankFlow> wrapper = new EntityWrapper<ExpBankFlow>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expBankFlowList = this.selectPage(page, wrapper).getRecords();
            expBankFlowList.stream().forEach(expBankFlow -> {
                //公司转化
                if (null != expBankFlow.getCompanyId()) {
                    Company company = companyService.selectById(expBankFlow.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expBankFlow.setCompanyName(company.getName());
                    }
                }
            });
        }
        return page.setRecords(expBankFlowList);
    }

    /**
     * 根据外键导出支付明细信息
     *
     * @param headerId
     * @return
     */
    public List<ExpBankFlow> exportPaymentDeatil(String headerId){
        List<ExpBankFlow> expBankFlowList = new ArrayList<>();
        if(StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpBankFlow> wrapper = new EntityWrapper<ExpBankFlow>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expBankFlowList = this.selectList(wrapper);
            expBankFlowList.stream().forEach(expBankFlow -> {
                //公司转化
                if (null != expBankFlow.getCompanyId()) {
                    Company company = companyService.selectById(expBankFlow.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expBankFlow.setCompanyName(company.getName());
                    }
                }
            });
        }
        return expBankFlowList;
    }



}
