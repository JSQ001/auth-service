package com.hand.hcf.app.ant.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.invoice.dto.InvoiceHeader;
import com.hand.hcf.app.ant.invoice.persistence.InvoiceHeaderMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceHeaderService extends BaseService<InvoiceHeaderMapper,InvoiceHeader> {


    @Autowired
    private InvoiceHeaderMapper invoiceHeaderMapper;

    /*
    * 分页条件查询发票头信息
     */
    public List<InvoiceHeader> queryHeaderPages (Page page){

        return invoiceHeaderMapper.selectList(new EntityWrapper<>());
    }

    public InvoiceHeader myInsertOrUpdate(InvoiceHeader invoiceHeader){

        if(this.insertOrUpdate(invoiceHeader)){
            // 插入行表

        }
        return invoiceHeader;
    }
}
