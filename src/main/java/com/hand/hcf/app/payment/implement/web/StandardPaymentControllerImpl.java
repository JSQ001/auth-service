package com.hand.hcf.app.payment.implement.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.payment.service.CashFlowItemService;
import com.hand.hcf.app.payment.service.CashTransactionClassService;
import com.hand.hcf.app.payment.service.CompanyBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class StandardPaymentControllerImpl /*implements StandardPaymentInterface*/{
    @Autowired
    private CompanyBankService companyBankService;

    @Autowired
    private CashTransactionClassService cashTransactionClassService;

    @Autowired
    private CashFlowItemService cashFlowItemService;

    public Page<BasicCO> pageCompanyBankByInfoResultBasic(@RequestParam(value = "selectId", required = false) String selectId,
                                                          @RequestParam(value = "code", required = false) String code,
                                                          @RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "securityType") String securityType,
                                                          @RequestParam(value = "filterId") Long filterId,
                                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page mybaitsPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = companyBankService.pageCompanyBankByInfoResultBasic(selectId, code,  name, securityType,  filterId,mybaitsPage);
        return result;
    }


    public Page<BasicCO> pageCashTransactionClassByInfoResultBasic(@RequestParam(value = "selectId", required = false) Long selectId,
                                                                   @RequestParam(value = "code", required = false) String code,
                                                                   @RequestParam(value = "name", required = false) String name,
                                                                   @RequestParam(value = "securityType") String securityType,
                                                                   @RequestParam(value = "filterId") Long filterId,
                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page mybaitsPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = cashTransactionClassService.listCashTransactionClassByIdOrCond(selectId,filterId,code,name,mybaitsPage);
        return result;
    }


    public Page<BasicCO> pageCashFlowItemByInfoResultBasic(@RequestParam(value = "selectId", required = false) Long selectId,
                                                           @RequestParam(value = "code", required = false) String code,
                                                           @RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "securityType") String securityType,
                                                           @RequestParam(value = "filterId") Long filterId,
                                                           @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page mybaitsPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = cashFlowItemService.listCashFlowItemByIdOrCond(selectId,filterId,code,name,mybaitsPage);
        return result;
    }
}
