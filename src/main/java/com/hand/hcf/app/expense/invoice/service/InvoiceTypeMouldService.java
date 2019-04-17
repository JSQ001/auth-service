package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceType;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldHeadColumn;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldLineColumn;
import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeMouldDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/17 10:10
 * @version: 1.0.0
 */
@Service
public class InvoiceTypeMouldService {
    @Autowired
    private  InvoiceTypeMouldHeadColumnService headColumnService;

    @Autowired
    private InvoiceTypeMouldLineColumnService lineColumnService;

    @Autowired
    private InvoiceTypeService invoiceTypeService;

    /**
     * 发票模板定义-新增/修改
     * @param invoiceTypeMouldDTO
     * @return
     */
    @Transactional
    public InvoiceTypeMouldDTO insertOrUpdateInvoiceTypeMould(InvoiceTypeMouldDTO invoiceTypeMouldDTO) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        InvoiceTypeMouldHeadColumn head = invoiceTypeMouldDTO.getInvoiceTypeMouldHeadColumn();
        InvoiceTypeMouldLineColumn line = invoiceTypeMouldDTO.getInvoiceTypeMouldLineColumn();
        InvoiceType invoiceType = invoiceTypeService.selectById(head.getInvoiceTypeId());
        if(invoiceType == null){
            throw new BizException(RespCode.INVOICE_TYPE_NOT_EXIST);
        }
        if(head.getId() != null && line.getId() != null){
            InvoiceTypeMouldHeadColumn invoiceTypeMouldHeadColumn = headColumnService.selectById(head.getId());
            if(invoiceTypeMouldHeadColumn == null){
                throw  new BizException(RespCode.INVOICE_TYPE_HEAD_COLUMN_NOT_EXIST);
            }
            InvoiceTypeMouldLineColumn invoiceTypeMouldLineColumn = lineColumnService.selectById(line.getId());
            if(invoiceTypeMouldLineColumn == null){
                throw  new BizException(RespCode.INVOICE_TYPE_LINE_COLUMN_NOT_EXIST);
            }
            headColumnService.updateById(head);
            lineColumnService.updateById(line);
        }else {
            head.setTenantId(tenantId);
            line.setTenantId(tenantId);
            headColumnService.insert(head);
            lineColumnService.insert(line);
        }
        return invoiceTypeMouldDTO;
    }

    /**
     * 根据发票类型Id 获取模板信息
     * @param invoiceTypeId 发票类型Id
     * @return
     */
    public InvoiceTypeMouldDTO getInvoiceTypeMouldByTypeId(Long invoiceTypeId) {
        InvoiceType invoiceType = invoiceTypeService.selectById(invoiceTypeId);
        if(invoiceType == null){
            throw new BizException(RespCode.INVOICE_TYPE_NOT_EXIST);
        }
        InvoiceTypeMouldDTO invoiceTypeMouldDTO =InvoiceTypeMouldDTO
                .builder()
                .invoiceTypeMouldHeadColumn(headColumnService.selectOne(
                        new EntityWrapper<InvoiceTypeMouldHeadColumn>()
                                .eq("invoice_type_id",invoiceTypeId)))
                .invoiceTypeMouldLineColumn(lineColumnService.selectOne(
                        new EntityWrapper<InvoiceTypeMouldLineColumn>()
                                .eq("invoice_type_id",invoiceTypeId)))
                .build();
      return invoiceTypeMouldDTO;
    }
}
