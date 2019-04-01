package com.hand.hcf.app.expense.invoice.service;

import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceLineDistMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/23
 */
@Service
@AllArgsConstructor
@Transactional
public class InvoiceLineDistService extends BaseService<InvoiceLineDistMapper,InvoiceLineDist>{
    private final InvoiceLineDistMapper invoiceLineDistMapper;
}
