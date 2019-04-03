package com.hand.hcf.app.demo.book.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.co.CurrencyRateCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.demo.book.domain.ExpenseBookDemo;
import com.hand.hcf.app.demo.book.persistence.ExpenseBookMapperDemo;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.persistence.ExpenseBookMapper;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceHeadService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineDistService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineExpenceService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineService;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.OperationUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiancheng.li@hand-china.com
 * @description
 * @date 2019/4/2 11:56
 * @version: 1.0.0
 */
@Service
public class ExpenseBookDemoService extends BaseService<ExpenseBookMapperDemo,ExpenseBookDemo>{

    @Autowired
    private ExpenseTypeService expenseTypeService;
    public List<ExpenseBookDemo> pageExpenseBookByCond(Long expenseTypeId,
                                                       String dateFrom,
                                                       String dateTo,
                                                       BigDecimal amountFrom,
                                                       BigDecimal amountTo,
                                                       String currencyCode,
                                                       String remarks,
                                                       Long expenseReportTypeId,
                                                       Page queryPage) {

        Wrapper wrapper =  new EntityWrapper<ExpenseBookDemo>();

        List<ExpenseBookDemo> expenseBooks = baseMapper.selectPage(queryPage,wrapper);
        return expenseBooks;
    }

    public ExpenseBookDemo insertExpenseBook(ExpenseBookDemo expenseBookDemo) {

        Integer id = baseMapper.insert(expenseBookDemo);
        return baseMapper.selectById(id);
    }

    public ExpenseBookDemo updateExpenseBook(ExpenseBookDemo expenseBook) {
        this.updateById(expenseBook);

        return expenseBook;
    }

    public void deleteExpenseBook(Long expenseBookId) {

        baseMapper.deleteById(expenseBookId);
    }

}
