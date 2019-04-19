package com.hand.hcf.app.mdata.implement.web;

import com.hand.hcf.app.mdata.bank.dto.BankInfoDTO;
import com.hand.hcf.app.mdata.bank.service.BankInfoService;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaowei.zhang
 * @Date: Create in 11:57 2019/4/17
 * =======================================
 **/
@RestController
public class BankInfoControllerImpl  {

    @Autowired
    private BankInfoService bankInfoService;

    @Autowired
    private MapperFacade mapper;

    //@Override
    public List<BankInfoDTO> getBankDataByNumList(List<String> bankCodeList) {
        List<BankInfoDTO> out = new ArrayList<>();
        bankInfoService.getBankDataByNumList(bankCodeList).stream().forEach(bank->{
            out.add(mapper.map(bank,BankInfoDTO.class));
        });
        return out;
    }

   // @Override
    public BankInfoDTO getBankDataByCode(String bankCode) {
        return mapper.map(bankInfoService.getBankDataByCode(bankCode),BankInfoDTO.class);
    }
}
