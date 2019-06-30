package com.hand.hcf.app.ant.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.ant.invoice.dto.InvoiceLine;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.ant.invoice.persistence.AntInvoiceLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AntInvoiceLineService extends BaseService<AntInvoiceLineMapper, InvoiceLine> {


    @Autowired
    private AntInvoiceLineMapper antInvoiceLineMapper;

    /*
     * 查询发票头下行信息
     */

    public List<InvoiceLine> getHeaderLines(Long headerId){

        return antInvoiceLineMapper.selectList(
                new EntityWrapper<InvoiceLine>()
                .eq("header_id",headerId)
        );
    }
}
