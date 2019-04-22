package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.invoice.dto.InvoiceCertificationDTO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/4/19 10:39
 * @version: 1.0.0
 */
@Service
public class InvoiceCertificationService {

    private final Logger log = LoggerFactory.getLogger(InvoiceCertificationService.class);

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private InvoiceHeadService invoiceHeadService;
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
    public List<InvoiceCertificationDTO> pageInvoiceCertifiedByCond(Long invoiceTypeId,
                                                                String invoiceNo,
                                                                String invoiceCode,
                                                                ZonedDateTime invoiceDateFrom,
                                                                ZonedDateTime invoiceDateTo,
                                                                BigDecimal invoiceAmountFrom,
                                                                BigDecimal invoiceAmountTo,
                                                                String createdMethod,
                                                                Long certificationStatus,
                                                                Boolean isSubmit,
                                                                Page page) {

        return invoiceHeadService.pageInvoiceCertifiedByCond(
                invoiceTypeId,
                invoiceNo,
                invoiceCode,
                invoiceDateFrom,
                invoiceDateTo,
                invoiceAmountFrom,
                invoiceAmountTo,
                createdMethod,
                certificationStatus,
                isSubmit,
                page);
    }
    /**
     * 导出发票认证信息 （已提交或未提交认证）
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
     * @return
     */
    public void exportInvoiceCertified(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ExportConfig exportConfig,
                                       Long invoiceTypeId,
                                       String invoiceNo,
                                       String invoiceCode,
                                       ZonedDateTime invoiceDateFrom,
                                       ZonedDateTime invoiceDateTo,
                                       BigDecimal invoiceAmountFrom,
                                       BigDecimal invoiceAmountTo,
                                       String createdMethod,
                                       Long certificationStatus,
                                       Boolean isSubmit) throws IOException {
        log.info("Start Exporting InvoiceUncertified Items");
        Page page = new Page<InvoiceCertificationDTO>(0, 0);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        try {
            excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<InvoiceCertificationDTO, InvoiceCertificationDTO>() {

                @Override
                public int getTotal() {
                    return total;
                }

                @Override
                public List<InvoiceCertificationDTO> queryDataByPage(Page page) {
                    return  invoiceHeadService.pageInvoiceCertifiedByCond(invoiceTypeId,
                            invoiceNo,
                            invoiceCode,
                            invoiceDateFrom,
                            invoiceDateTo,
                            invoiceAmountFrom,
                            invoiceAmountTo,
                            createdMethod,
                            certificationStatus,
                            isSubmit,
                            page);
                }

                @Override
                public InvoiceCertificationDTO toDTO(InvoiceCertificationDTO t) {
                    return t;
                }

                @Override
                public Class<InvoiceCertificationDTO> getEntityClass() {
                    return InvoiceCertificationDTO.class;
                }
            },threadNumber, request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
