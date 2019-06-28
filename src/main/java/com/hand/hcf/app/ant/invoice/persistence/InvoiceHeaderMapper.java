package com.hand.hcf.app.ant.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.invoice.dto.InvoiceHeader;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface InvoiceHeaderMapper extends BaseMapper<InvoiceHeader> {
}
