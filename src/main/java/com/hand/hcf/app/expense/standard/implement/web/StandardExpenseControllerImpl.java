package com.hand.hcf.app.expense.standard.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustTypeService;
import com.hand.hcf.app.expense.invoice.service.InvoiceTypeService;
import com.hand.hcf.core.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StandardExpenseControllerImpl {
    @Autowired
    private ExpenseAdjustTypeService expenseAdjustTypeService;
    @Autowired
    private InvoiceTypeService invoiceTypeService;

    public Page<BasicCO> pageExpenseReportTypeByInfoResultBasic(@Param("selectId") Long selectId,
                                                                @Param("code") String code,
                                                                @Param("name") String name,
                                                                @Param("securityType") String securityType,
                                                                @Param("filterId")Long filterId,
                                                                @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page pg = PageUtil.getPage(page, size);
        Page<BasicCO> result = expenseAdjustTypeService.pageExpenseReportTypeByInfoResultBasic(code, name, selectId,securityType ,filterId, pg);
        return result;
    }

    public Page<BasicCO> pageExpenseTypeByInfoResultBasic(@Param("selectId") Long selectId,
                                                          @Param("code") String code,
                                                          @Param("name") String name,
                                                          @Param("securityType") String securityType,
                                                          @Param("filterId")Long filterId,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page pg = PageUtil.getPage(page, size);
        return expenseAdjustTypeService.pageExpenseTypeByInfoResultBasic(selectId,code,name,securityType,filterId,pg);
    }

    public Page<BasicCO> pageInvoiceTypeByInfoResultBasic(@Param("selectId") Long selectId,
                                                          @Param("code") String code,
                                                          @Param("name") String name,
                                                          @Param("securityType") String securityType,
                                                          @Param("filterId")Long filterId,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        return invoiceTypeService.pageInvoiceTypeByInfoResultBasic(selectId, code, name, securityType, filterId, queryPage);
    }
}
