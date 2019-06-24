package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculation;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxCalculationMapper;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金计提service
 * @date 2019/6/24 18:45
 */
@Service
public class ExpTaxCalculationService extends BaseService<ExpTaxCalculationMapper, ExpTaxCalculation> {

    @Autowired
    private  ExpTaxCalculationMapper expTaxCalculationMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SysCodeValeMapper sysCodeValeMapper;

    /**
     * 根据外键获取税金明细信息(分页查询）--报账单详情数据税金信息查询
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
            });
        }
        return expTaxCalculationList;
    }
}
