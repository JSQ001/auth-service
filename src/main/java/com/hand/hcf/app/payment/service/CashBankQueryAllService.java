package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.persistence.CashBankUserDefinedMapper;
import com.hand.hcf.app.payment.web.dto.BankQueryAllDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liudong on 2017/12/22.
 */
@Service
public class CashBankQueryAllService {

    private final CashBankUserDefinedMapper cashBankUserDefinedMapper;

    public CashBankQueryAllService(CashBankUserDefinedMapper cashBankUserDefinedMapper) {
        this.cashBankUserDefinedMapper = cashBankUserDefinedMapper;
    }

    public Page<BankQueryAllDTO> queryAllBankDTO(String bankCode, String bankName, Page page) {
        List<BankQueryAllDTO> list = cashBankUserDefinedMapper.
                getAllBankByCond(page, bankCode, bankName, OrgInformationUtil.getCurrentTenantId());
        page.setRecords(list);
        return page;
    }
}
