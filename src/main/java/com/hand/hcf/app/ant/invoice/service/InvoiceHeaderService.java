package com.hand.hcf.app.ant.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.invoice.dto.InvoiceHeader;
import com.hand.hcf.app.ant.invoice.dto.InvoiceLine;
import com.hand.hcf.app.ant.invoice.persistence.InvoiceHeaderMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceHeaderService extends BaseService<InvoiceHeaderMapper,InvoiceHeader> {


    @Autowired
    private InvoiceHeaderMapper invoiceHeaderMapper;

    @Autowired
    private AntInvoiceLineService invoiceLineService;

    /*
    * 分页条件(单个)查询发票头行信息
     */
    public List<InvoiceHeader> queryHeaderPages (Long id,Page page){
        return invoiceHeaderMapper.selectHeaderLines(id,page);
    }

    /*
     * 新增或修改发票头
     *
     */
    public InvoiceHeader myInsertOrUpdate(InvoiceHeader invoiceHeader){
        if(this.insertOrUpdate(invoiceHeader)){
            // 插入行表
            invoiceHeader.getInvoiceLines().forEach(item->item.setHeaderId(invoiceHeader.getId()));
            invoiceLineService.insertBatch(invoiceHeader.getInvoiceLines());
        }
        return invoiceHeader;
    }
    /*
     * 删除发票头行
     *
     */
    public boolean deleteInvoiceById(Long id){
        return baseMapper.deleteInvoiceById(id);
    }
}
