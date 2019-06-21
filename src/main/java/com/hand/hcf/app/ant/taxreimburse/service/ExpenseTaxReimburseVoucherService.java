package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseVoucher;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpenseTaxReimburseVoucherMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.persistence.DepartmentMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:国内税金缴纳报账单凭证信息service
 * @date 2019/6/14 16:19
 */
@Service
public class ExpenseTaxReimburseVoucherService extends BaseService<ExpenseTaxReimburseVoucherMapper, ExpenseTaxReimburseVoucher> {

    @Autowired
    private ExpenseTaxReimburseVoucherMapper expenseTaxReimburseVoucherMapper;

    @Autowired
    private DepartmentMapper departmentMapper;


    /**
     * 根据外键获取凭证信息(分页查询）--报账单详情页面凭证显示
     *
     * @param headerId
     * @param page
     * @return
     */
    public List<ExpenseTaxReimburseVoucher> getTaxReportDetailList(String headerId, Page page) {
        List<ExpenseTaxReimburseVoucher> taxReimburseVoucherList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpenseTaxReimburseVoucher> wrapper = new EntityWrapper<ExpenseTaxReimburseVoucher>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            taxReimburseVoucherList = expenseTaxReimburseVoucherMapper.selectPage(page, wrapper);
            taxReimburseVoucherList.stream().forEach(taxReimburseVoucher -> {
                //预算部门转化
                if (null != taxReimburseVoucher.getBudgetDepartmentId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBudgetDepartmentId());
                    if (null != department) {
                        taxReimburseVoucher.setBudgetDepartmentName(department.getName());
                    }
                }
                //受益部门转化
                if (null != taxReimburseVoucher.getBenefitedDepartId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBenefitedDepartId());
                    if (null != department) {
                        taxReimburseVoucher.setBenefitedDepartName(department.getName());
                    }
                }

            });
        }
        return taxReimburseVoucherList;
    }

    /**
     * 根据外键获取凭证信息(分页查询）--导出数据用
     *
     * @param headerId
     * @param page
     * @return
     */
    public Page<ExpenseTaxReimburseVoucher> getVoucherDetail(String headerId, Page page) {
        List<ExpenseTaxReimburseVoucher> taxReimburseVoucherList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpenseTaxReimburseVoucher> wrapper = new EntityWrapper<ExpenseTaxReimburseVoucher>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            taxReimburseVoucherList = this.selectPage(page, wrapper).getRecords();
            taxReimburseVoucherList.stream().forEach(taxReimburseVoucher -> {
                //预算部门转化
                if (null != taxReimburseVoucher.getBudgetDepartmentId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBudgetDepartmentId());
                    if (null != department) {
                        taxReimburseVoucher.setBudgetDepartmentName(department.getName());
                    }
                }
                //受益部门转化
                if (null != taxReimburseVoucher.getBenefitedDepartId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBenefitedDepartId());
                    if (null != department) {
                        taxReimburseVoucher.setBenefitedDepartName(department.getName());
                    }
                }
            });
        }
        return page.setRecords(taxReimburseVoucherList);
    }

    /**
     * 根据外键导出凭证信息
     *
     * @param headerId
     * @return
     */
    public List<ExpenseTaxReimburseVoucher> exportVoucherDeatil(String headerId) {
        List<ExpenseTaxReimburseVoucher> taxReimburseVoucherList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpenseTaxReimburseVoucher> wrapper = new EntityWrapper<ExpenseTaxReimburseVoucher>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            taxReimburseVoucherList = this.selectList(wrapper);
            taxReimburseVoucherList.stream().forEach(taxReimburseVoucher -> {
                //预算部门转化
                if (null != taxReimburseVoucher.getBudgetDepartmentId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBudgetDepartmentId());
                    if (null != department) {
                        taxReimburseVoucher.setBudgetDepartmentName(department.getName());
                    }
                }
                //受益部门转化
                if (null != taxReimburseVoucher.getBenefitedDepartId()) {
                    Department department = departmentMapper.selectOneSimpleById(taxReimburseVoucher.getBenefitedDepartId());
                    if (null != department) {
                        taxReimburseVoucher.setBenefitedDepartName(department.getName());
                    }
                }
            });
        }
        return taxReimburseVoucherList;
    }


}
