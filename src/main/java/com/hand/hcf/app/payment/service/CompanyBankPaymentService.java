package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashPaymentMethod;
import com.hand.hcf.app.payment.domain.CompanyBankPayment;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CashPaymentMethodMapper;
import com.hand.hcf.app.payment.persistence.CompanyBankPaymentMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.CompanyBankPaymentDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@Service
@Transactional
public class CompanyBankPaymentService extends ServiceImpl<CompanyBankPaymentMapper, CompanyBankPayment> {

    @Autowired
    private CompanyBankPaymentMapper companyBankPaymentMapper;


    @Autowired
    private CashPaymentMethodMapper cashPaymentMethodMapper;

    @Autowired
    private PaymentOrganizationService organizationService;

    public Boolean insertOrUpdateCompanyBankPayment(List<CompanyBankPayment> companyBankPayments) {
        for (CompanyBankPayment companyBankPayment : companyBankPayments) {
            if (companyBankPayment.getPaymentMethodId() != null) {
                CashPaymentMethod cashPaymentMethod = cashPaymentMethodMapper.selectById(companyBankPayment.getPaymentMethodId());
                if (cashPaymentMethod == null) {
                    throw new BizException(RespCode.PAYMENT_METHOD_NOT_FOUNT);
                }
                companyBankPayment.setPaymentMethodCategory(cashPaymentMethod.getPaymentMethodCategory());
                companyBankPayment.setPaymentMethodCode(cashPaymentMethod.getPaymentMethodCode());
            }
            if (companyBankPayment.getId() == null) {//新增
                companyBankPayment.setCreatedDate(ZonedDateTime.now());
                companyBankPayment.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
                companyBankPayment.setLastUpdatedDate(ZonedDateTime.now());
                companyBankPayment.setCreatedBy(OrgInformationUtil.getCurrentUserId());
                //校验银行账户和付款方式id,联合唯一
                check(companyBankPayment);
                companyBankPaymentMapper.insert(companyBankPayment);
                continue;
            }
            //修改
            check(companyBankPayment);
            if (companyBankPayments.size() > 1) {
                throw new BizException(RespCode.UPDATE_SIZE_TOO_BIG);
            }
            companyBankPaymentMapper.updateById(companyBankPayment);
        }
        return true;

    }

    //逻辑删除
    public Boolean deleteById(Long id) {
        CompanyBankPayment companyBankPayment = companyBankPaymentMapper.selectById(id);
        if (companyBankPayment == null) {
            return true;
        }
        companyBankPayment.setDeleted(true);
        int i = companyBankPaymentMapper.updateById(companyBankPayment);
        return i != 0 ? true : false;
    }

    public Boolean deleteByIds(List<Long> ids) {
        Integer integer = companyBankPaymentMapper.deleteBatchIds(ids);
        return integer != 0;
    }

    //分页查询当前银行账户下的付款方式
    public Page<CompanyBankPaymentDTO> selectByBankId(Long bankId, Page page) {
        page.getRecords();
        EntityWrapper<CompanyBankPayment> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_id", bankId);
        wrapper.eq("deleted", false);
        wrapper.orderBy("payment_method_category", false);
        wrapper.orderBy("payment_method_code");
        List<CompanyBankPayment> list = companyBankPaymentMapper.selectPage(page, wrapper);


        List<CompanyBankPaymentDTO> listDTO = new ArrayList<>();
        //此处需要与支付模块的付款方式表结合。
        for (CompanyBankPayment companyBankPayment : list) {
            CompanyBankPaymentDTO dto = new CompanyBankPaymentDTO();
            if (companyBankPayment.getPaymentMethodId() != null) {
                CashPaymentMethod cashPaymentMethod = cashPaymentMethodMapper.selectById(companyBankPayment.getPaymentMethodId());
                if (cashPaymentMethod == null) {
                    throw new BizException(RespCode.PAYMENT_METHOD_NOT_FOUNT);
                }
                BeanUtils.copyProperties(cashPaymentMethod, dto, "id");
                dto.setPaymentMethodId(cashPaymentMethod.getId());
                //dto.setPaymentMethodCategoryName(cashPaymentMethod.getPaymentMethodCategoryName());
                dto.setPaymentMethodCategoryName(
                organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                        cashPaymentMethod.getPaymentMethodCategory()).getName());
            }
            dto.setId(companyBankPayment.getId());
            listDTO.add(dto);
        }

        if (CollectionUtils.isNotEmpty(listDTO)) {
            page.setRecords(listDTO);
        }
        return page;
    }


    //根据银行账户id(bankAccountId)查询对应的付款方式ids
    public List<Long> listPaymentMethodIdsByBankAccountId(Long bankAccountId) {
        EntityWrapper<CompanyBankPayment> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_id", bankAccountId);
        wrapper.eq("deleted", false);
        List<CompanyBankPayment> list = companyBankPaymentMapper.selectList(wrapper);
        return list.stream().map(CompanyBankPayment::getPaymentMethodId).collect(Collectors.toList());
    }


    //不分页查询当前银行账户下的付款方式
    public List<CompanyBankPaymentDTO> selectByBankIdAndCode(Long bankId, String code) {
        EntityWrapper<CompanyBankPayment> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_id", bankId);
        wrapper.eq("payment_method_category", code);
        wrapper.eq("deleted", false);
        wrapper.orderBy("payment_method_category", false);
        wrapper.orderBy("payment_method_code");
        List<CompanyBankPayment> list = companyBankPaymentMapper.selectList(wrapper);
        List<CompanyBankPaymentDTO> listDTO = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(
                    companyBankPayment -> {
                        CompanyBankPaymentDTO dto = new CompanyBankPaymentDTO();
                        if (companyBankPayment.getPaymentMethodId() != null) {
                            CashPaymentMethod cashPaymentMethod = cashPaymentMethodMapper.selectById(companyBankPayment.getPaymentMethodId());
                            if (cashPaymentMethod == null) {
                                throw new BizException(RespCode.PAYMENT_METHOD_NOT_FOUNT);
                            }
                            BeanUtils.copyProperties(cashPaymentMethod, dto, "id");
                            dto.setPaymentMethodId(cashPaymentMethod.getId());
                        }
                        dto.setId(companyBankPayment.getId());
                        listDTO.add(dto);
                    }
            );
        }
        return listDTO;
    }


    private void check(CompanyBankPayment companyBankPayment) {
        List<CompanyBankPayment> list = this.selectList(
                new EntityWrapper<CompanyBankPayment>()
                        .eq("bank_account_id", companyBankPayment.getBankAccountId())
                        .eq("payment_method_id", companyBankPayment.getPaymentMethodId())
                        .eq("deleted", false)
                        .eq("enabled", true)

        );
        if (list.size() == 1) {
            if (list.get(0).getId() == (companyBankPayment.getId() == null ? -1 : companyBankPayment.getId())) {
                return;
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BizException(RespCode.PAYMENT_METHOD_EXIT);
        }
        return;
    }

    //不分页查询当前银行账户下的付款方式
    public List<CompanyBankPayment> selectCompanyBankPaymentByBankId(Long bankId) {
        EntityWrapper<CompanyBankPayment> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_id", bankId);
        wrapper.eq("deleted", false);
        List<CompanyBankPayment> list = companyBankPaymentMapper.selectList(wrapper);
        return list;
    }
}
