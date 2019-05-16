package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.dto.InvoiceCertificationDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
public interface InvoiceHeadMapper extends BaseMapper<InvoiceHead>{
    /**
     *  分页查询未关联的发票头
     * @param queryPage
     * @param wrapper
     * @return
     */
    List<InvoiceHead> pageInvoiceByCond(Page queryPage,
                                        @Param("ew") Wrapper<InvoiceHead> wrapper);


    /**
     * 分页获取发票信息 （已提交或未提交认证）
     * @param invoiceTypeId 单据类型Id
     * @param invoiceNo 发票号码
     * @param invoiceCode 发票代码
     * @param invoiceDateFrom 开票时间从
     * @param invoiceDateTo 开票时间至
     * @param invoiceAmountFrom 金额合计从
     * @param invoiceAmountTo 金额合计至
     * @param createdMethod 创建方式
     * @param certificationStatus 认证状态
     * @param isSubmit true:已提交 false：未提交
     * @param page
     * @return
     */
    List<InvoiceCertificationDTO> pageInvoiceCertifiedByCond(@Param("invoiceTypeId") Long invoiceTypeId,
                                                         @Param("invoiceNo") String invoiceNo,
                                                         @Param("invoiceCode") String invoiceCode,
                                                         @Param("invoiceDateFrom") ZonedDateTime invoiceDateFrom,
                                                         @Param("invoiceDateTo") ZonedDateTime invoiceDateTo,
                                                         @Param("invoiceAmountFrom") BigDecimal invoiceAmountFrom,
                                                         @Param("invoiceAmountTo") BigDecimal invoiceAmountTo,
                                                         @Param("createdMethod") String createdMethod,
                                                         @Param("certificationStatus") Long certificationStatus,
                                                         @Param("isSubmit") Boolean isSubmit,
                                                         Page page);

    /**
     *  根据报账单行id查询关联发票头id
     * @param reportLineId 报账单行id
     * @return
     */
    List<Long> getInvoiceHeadIdByReportLineId(@Param("reportLineId") Long reportLineId);
}
