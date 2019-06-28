package com.hand.hcf.app.ant.invoice.service;

import com.hand.hcf.app.ant.invoice.dto.InvoiceLine;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.ant.invoice.persistence.AntInvoiceLineMapper;
import org.springframework.stereotype.Service;

@Service
public class AntInvoiceLineService extends BaseService<AntInvoiceLineMapper, InvoiceLine> {

}
