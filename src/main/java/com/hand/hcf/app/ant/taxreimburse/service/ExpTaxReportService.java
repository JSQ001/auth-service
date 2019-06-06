package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.dto.TaxBlendDataDTO;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowMapper;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportMapper;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    ExpBankFlowMapper expBankFlowMapper;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private SysCodeValeMapper sysCodeValeMapper;

    @Autowired
    private CompanyService companyService;

    public final String WARNING = "不可删除已勾兑的数据！";

    public final String WARNING1 = "存在其他已勾兑的数据！";

    public final String WARNING2 = "未勾兑的数据不可报账！";


    /**
     * 获取税金申报List的数据-分页查询
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param taxCategoryId
     * @param status
     * @param page
     * @return
     */
    public List<ExpTaxReport> getExpTaxReportByCon(String companyId,/*公司*/
                                                   String currencyCode,/*币种*/
                                                   Boolean blendStatus,/*勾兑状态*/
                                                   String requestPeriodFrom,/*申报期间从*/
                                                   String requestPeriodTo,/*申报期间至*/
                                                   BigDecimal requestAmountFrom,/*申报金额从*/
                                                   BigDecimal requestAmountTo,/*申报金额至*/
                                                   String businessSubcategoryName,/*业务小类代码*/
                                                   String taxCategoryId,/*税种代码*/
                                                   Boolean status,/*报账状态*/
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
                .eq(blendStatus != null, "blend_status", blendStatus)
                .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                .le(requestAmountTo != null, "request_amount", requestAmountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                .eq(status != null, "status", status);
        List<ExpTaxReport> expTaxReportList = expTaxReportMapper.selectPage(page, wrapper);
        converDesc(expTaxReportList);
        return expTaxReportList;
    }

    /**
     * 分页查询税金申报数据
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @param page
     * @return
     */
    public Page<ExpTaxReport> getExpTaxReportByPage(
            String companyId,/*公司*/
            String currencyCode,/*币种*/
            Boolean blendStatus,/*勾兑状态*/
            String requestPeriodFrom,/*申报期间从*/
            String requestPeriodTo,/*申报期间至*/
            BigDecimal requestAmountFrom,/*申报金额从*/
            BigDecimal requestAmountTo,/*申报金额至*/
            String businessSubcategoryName,/*业务小类代码*/
            String taxCategoryId,/*税种代码*/
            Boolean status,/*报账状态*/
            Page page
    ) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }

        Wrapper<ExpTaxReport> wrapper =
                new EntityWrapper<ExpTaxReport>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                        .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                        .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                        .le(requestAmountTo != null, "request_amount", requestAmountTo)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                        .eq(status != null, "status", status);


        List<ExpTaxReport> expTaxReportList = this.selectPage(page, wrapper).getRecords();
        converDesc(expTaxReportList);
        return page.setRecords(expTaxReportList);
    }

    /**
     * 导出税金申报数据
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @return
     */
    public List<ExpTaxReport> exportExpTaxReport(
            String companyId,/*公司*/
            String currencyCode,/*币种*/
            Boolean blendStatus,/*勾兑状态*/
            String requestPeriodFrom,/*申报期间从*/
            String requestPeriodTo,/*申报期间至*/
            BigDecimal requestAmountFrom,/*申报金额从*/
            BigDecimal requestAmountTo,/*申报金额至*/
            String businessSubcategoryName,/*业务小类代码*/
            String taxCategoryId,/*税种代码*/
            Boolean status/*报账状态*/
    ) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }
        Wrapper<ExpTaxReport> wrapper =
                new EntityWrapper<ExpTaxReport>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                        .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                        .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                        .le(requestAmountTo != null, "request_amount", requestAmountTo)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                        .eq(status != null, "status", status);


        List<ExpTaxReport> expTaxReportList = this.selectList(wrapper);
        converDesc(expTaxReportList);
        return expTaxReportList;

    }

    /**
     * 转换DESC
     *
     * @param expTaxReportList
     * @return
     */
    public List<ExpTaxReport> converDesc(List<ExpTaxReport> expTaxReportList) {
        expTaxReportList.stream().forEach(expTaxReport -> {

            //公司转化
            if (null != expTaxReport.getCompanyId()) {
                Company company = companyService.selectById(expTaxReport.getCompanyId());
                if (StringUtils.isNotEmpty(company.getName())) {
                    expTaxReport.setCompanyName(company.getName());
                }
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

        });
        return expTaxReportList;
    }

    ;

    /**
     * 根据Id 批量删除税金申报数据
     *
     * @param ids
     */
    public void deleteTaxReport(String ids) {
        String idsArr[] = ids.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
            //当勾兑状态为0或者false--未勾兑时 才可以删除
            if ((!expTaxReport.getBlendStatus()) || (expTaxReport.getBlendStatus() == false)) {
                expTaxReportMapper.deleteById(Long.valueOf(idsArr[i]));
            }
            else {
                    throw new BizException(WARNING);
                }
        }
    }

    /**
     * 分组查询
     *
     * @return
     */
    public List<TaxBlendDataDTO> getBlendDataByGroup() {
        return expTaxReportMapper.getBlendDataByGroup();
    }


    /**
     * 根据相同的公司和币种更新数据状态
     *
     * @param companyId
     * @param currencyCode
     */
    public void updateTaxReport(Long companyId, String currencyCode) {
        expTaxReportMapper.updateTaxReport(companyId, currencyCode);
    }


    public List<ExpTaxReport> saveExpTaxReport(String ids, List<ExpTaxReport> expTaxReportList){
        String idsArr[] = ids.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
        }
        return null;
    }

    /**
     * 发起报账
     *
     * @param ids
     */
    public Boolean makeReimburse(String ids) {
        Boolean flag = false;
        String idsArr[] = ids.split(",");
        List<ExpTaxReport> expTaxReportList = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<String>();
        Map<Long,String> map = new HashMap<Long,String>();
        //将所有id涉及到的companyId、currencyCode组合加入到map中，然后进行比较。
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
            Long companyId = expTaxReport.getCompanyId();
            String currencyCode = expTaxReport.getCurrencyCode();
            boolean blendStatus = expTaxReport.getBlendStatus();
            boolean status = expTaxReport.getStatus();
            if((blendStatus || blendStatus ==true) &&(status == false) ){
                //当map中不存在此key,或者不存在此vlaue就加入--只有两种同时存在则不加入
                while(!map.containsKey(companyId) || !map.containsValue(currencyCode)){
                        map.put(companyId,currencyCode);
                    }
            } else{
                throw new BizException(WARNING2);
            }
            //第二种方式
            /*if(blendStatus || blendStatus ==true){
                String companyAndCurrency = companyId + "-" + currencyCode;
                hashSet.add(companyAndCurrency);
            }*/

        }
        //循环获取map中的key,value
        int sum = 0;//合计map中涉及到的数据的总计
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            Long companyId = entry.getKey();
            String currencyCode = entry.getValue();
            Wrapper<ExpTaxReport> wrapper =
                    new EntityWrapper<ExpTaxReport>()
                            .eq(companyId != null, "company_id", companyId)
                            .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                            .eq("blend_status", '1')
                            .eq("status", '0');

            int count  = expTaxReportMapper.selectCount(wrapper);
            sum =+ count;
        }

        //根据获取到的companyId、currencyCode查询此分组下已勾兑的未报账的所有数据，然后比较数量
        // 判断是否全部勾选，若全部勾选，则进行发起报账
        if(sum==idsArr.length){
            //更新税金勾兑的数据的报账状态为已勾兑
            for (int i = 0; i < idsArr.length; i++) {
                ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
                expTaxReport.setStatus(true);
                expTaxReportMapper.updateById(expTaxReport);
                //expTaxReportMapper.updateStatusById(Long.valueOf(idsArr[i]));
            }
            //更新已勾兑的税金申报数据关联的银行流水的报账状态为已报账
            for (Map.Entry<Long, String> entry : map.entrySet()) {
                Long companyId = entry.getKey();
                String currencyCode = entry.getValue();
                expBankFlowMapper.updateStatueByGroup(companyId,currencyCode);
            }
            flag = true;

            //抽取基本
        }else{
            //若未全部勾选，则返回提示
            throw new BizException(WARNING1);
        }

        //确认以上税金申报数据可以进行报账，然后关联流水数据--根据公司/币种/勾兑状态/报账状态进行，然后跳转页面，

        return flag;
    }


}
