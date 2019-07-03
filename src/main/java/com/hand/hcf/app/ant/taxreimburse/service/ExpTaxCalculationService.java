package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculation;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculationTempDomain;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseHead;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxCalculationMapper;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金计提service
 * @date 2019/6/24 18:45
 */
@Service
public class ExpTaxCalculationService extends BaseService<ExpTaxCalculationMapper, ExpTaxCalculation> {

    @Autowired
    private ExpTaxCalculationMapper expTaxCalculationMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SysCodeValeMapper sysCodeValeMapper;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExpTaxCalculationTempDomainService expTaxCalculationTempDomainService;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private ExpenseTaxReimburseHeadService expenseTaxReimburseHeadService;

    /**
     * 根据外键获取计提明细信息(分页查询）--报账单详情数据计提信息查询
     *
     * @param headerId
     * @param page
     * @return
     */
    public List<ExpTaxCalculation> getTaxCalculationDetailList(String headerId, Page page) {
        List<ExpTaxCalculation> expTaxCalculationList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpTaxCalculation> wrapper = new EntityWrapper<ExpTaxCalculation>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expTaxCalculationList = expTaxCalculationMapper.selectPage(page, wrapper);
            expTaxCalculationList.stream().forEach(expTaxCalculation -> {
                //公司转化
                if (null != expTaxCalculation.getCompanyId()) {
                    Company company = companyService.selectById(expTaxCalculation.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expTaxCalculation.setCompanyName(company.getName());
                    }
                }

                //税种转化
                if (StringUtils.isNotEmpty(expTaxCalculation.getTaxCategoryCode())) {
                    SysCodeValue sysCodeValueOne = new SysCodeValue();
                    sysCodeValueOne.setValue(expTaxCalculation.getTaxCategoryCode());
                    SysCodeValue sysCodeValue1 = sysCodeValeMapper.selectOne(sysCodeValueOne);
                    if (StringUtils.isNotEmpty(sysCodeValue1.getName())) {
                        expTaxCalculation.setTaxCategoryName(sysCodeValue1.getName());
                    }
                }


                //币种转化
                if (StringUtils.isNotEmpty(expTaxCalculation.getCurrencyCode())) {
                    CurrencyI18n currencyI18n = new CurrencyI18n();
                    currencyI18n.setCurrencyCode(expTaxCalculation.getCurrencyCode());
                    String language = OrgInformationUtil.getCurrentLanguage();
                    currencyI18n.setLanguage(language);
                    CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                    if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                        expTaxCalculation.setCurrencyName(currencyOne.getCurrencyName());
                    }
                }
            });
        }
        return expTaxCalculationList;
    }


    /**
     * 导入税金计提的数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public UUID importTaxCalculation(MultipartFile file) throws Exception {
        UUID batchNumber = UUID.randomUUID();
        InputStream in = file.getInputStream();
        ExcelImportHandler<ExpTaxCalculationTempDomain> excelImportHandler = new ExcelImportHandler<ExpTaxCalculationTempDomain>() {
            @Override
            public void clearHistoryData() {
                expTaxCalculationTempDomainService.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return ExpTaxCalculationTempDomain.class;
            }

            @Override
            public List<ExpTaxCalculationTempDomain> persistence(List<ExpTaxCalculationTempDomain> list) {
                // 导入数据
                expTaxCalculationTempDomainService.insertBatch(list);
                return list;
            }

            @Override
            public void check(List<ExpTaxCalculationTempDomain> list) {
                //数据合法性校验-有效性+唯一性
                expTaxCalculationTempDomainService.checkImportData(list, batchNumber);
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }

    /**
     * 批量删除计提明细信息
     *
     * @param ids
     * @return
     */
    public boolean deleteTaxCalcuations(String ids) {
        Boolean flag = false;
        String idsArr[] = ids.split(",");
        BigDecimal sumAmount = BigDecimal.ZERO;
        Long headId = null;
        ExpTaxCalculation expTaxCalculationOne = expTaxCalculationMapper.selectById(idsArr[0]);
        if (null != expTaxCalculationOne) {
            headId = expTaxCalculationOne.getExpReimburseHeaderId();
        }
        int sum = 0;
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxCalculation expTaxCalculation = expTaxCalculationMapper.selectById(idsArr[i]);
            if (null != expTaxCalculation) {
                BigDecimal requestAmount = expTaxCalculation.getRequestAmount();
                sumAmount = sumAmount.add(requestAmount);
            }
            int success = expTaxCalculationMapper.deleteById(expTaxCalculation);
            sum = sum + success;
        }
        ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadService.selectById(headId);
        if (null != expenseTaxReimburseHead) {
            BigDecimal totalAmount = expenseTaxReimburseHead.getTotalAmount();
            BigDecimal newAmount = totalAmount.subtract(sumAmount);
            expenseTaxReimburseHead.setTotalAmount(newAmount);
            //ToDo 原币和本币由汇率来计算
            expenseTaxReimburseHead.setFunctionalAmount(newAmount);
            expenseTaxReimburseHeadService.updateById(expenseTaxReimburseHead);
        }

        if (sum == idsArr.length) {
            flag = true;
        }
        return flag;
    }

    /**
     * 批量更新计提明细信息
     * @param expTaxCalculationList
     * @return
     */
    public List<ExpTaxCalculation> saveTaxCalculation(List<ExpTaxCalculation> expTaxCalculationList) {
        expTaxCalculationList.stream().forEach(expTaxCalculation -> {
            Wrapper wrapper = new EntityWrapper<ExpTaxCalculation>()
                    .eq(expTaxCalculation.getId() != null, "id", expTaxCalculation.getId());
            expTaxCalculationMapper.update(expTaxCalculation,wrapper);
        });
        return expTaxCalculationList;
    }


}
