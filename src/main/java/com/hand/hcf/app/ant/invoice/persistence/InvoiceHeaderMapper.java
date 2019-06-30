package com.hand.hcf.app.ant.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.ant.invoice.dto.InvoiceHeader;
import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface InvoiceHeaderMapper extends BaseMapper<InvoiceHeader> {

    List<InvoiceHeader> selectHeaderLines(
            @Param("id") Long id,
            Pagination page);

    boolean deleteInvoiceById(@Param("id") Long id);
}
