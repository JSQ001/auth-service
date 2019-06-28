package com.hand.hcf.app.ant.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.invoice.dto.InvoiceLine;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AntInvoiceLineMapper extends BaseMapper<InvoiceLine> {
}
