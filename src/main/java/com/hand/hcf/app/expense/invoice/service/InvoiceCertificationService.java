package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.InvoiceAuthenticationReturnCO;
import com.hand.hcf.app.common.co.InvoiceAuthenticationSendCO;
import com.hand.hcf.app.common.co.InvoiceAuthenticationSendLineCO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.dto.InvoiceCertificationDTO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.invoice.enums.CertificationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/4/19 10:39
 * @version: 1.0.0
 */
@Service
public class InvoiceCertificationService {

    private final Logger log = LoggerFactory.getLogger(InvoiceCertificationService.class);
    /**
     * 税号
     * 发票认证需求：暂时写死方正,后续自动生成
     */
    private final String BUYER_TAX_NO = "91111122223333444D";

    /**
     * 认证类型:发票暂时只存在抵扣的情况
     */
    private final String APPLYRZLX = "1";

    /**
     * 认证返回状态码 0000：提交成功
     */
    private final String RETURN_CODE = "0000";

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private InvoiceHeadService invoiceHeadService;

    @Autowired
    private OrganizationService organizationService;
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

    /**
     * 更新发票认证状态
     * @param headerIds 发票Id信息
     * @param certificationStatus 认证状态
     * @param certificationReason 认证失败原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoiceCertifiedStatus(List<Long> headerIds,
                                             Integer certificationStatus,
                                             String certificationReason) {
        if(CollectionUtils.isEmpty(headerIds)){
            return;
        }
        headerIds.stream().forEach(headerId-> {
            InvoiceHead header = invoiceHeadService.selectById(headerId);
            if (header == null) {
                throw new BizException(RespCode.INVOICE_HEADER_IS_NULL);
            }
            checkCertificationStatus(certificationStatus, Math.toIntExact(header.getCertificationStatus()));
            //提交待认证
            if (CertificationEnum.SUCCESS.getId().equals(certificationStatus)) {
                header.setCertificationDate(ZonedDateTime.now());
            } else if (CertificationEnum.FAIL.getId().equals(certificationStatus)) {
                header.setCertificationReason(certificationReason);
            }
            header.setCertificationStatus(certificationStatus.longValue());
            invoiceHeadService.updateById(header);
        });
    }

    private void checkCertificationStatus(Integer status, Integer certificationStatus) {
        switch (status) {
            //提交 (失败以及未提交)
            case 1:
                if (!(certificationStatus.equals(CertificationEnum.UNCERTIFIED.getId()) ||
                        certificationStatus.equals(CertificationEnum.FAIL.getId())) ) {
                    throw new BizException(RespCode.INVOICE_CERTIFICATION_STATUS_ERROR);
                }
                break;
             //认证成功
            case 4:
                if(certificationStatus.equals(CertificationEnum.UNCERTIFIED.getId())){
                    throw new BizException(RespCode.INVOICE_CERTIFICATION_STATUS_ERROR);
                }
        }
    }

    /**
     * 提交发票认证接口
     * @param headerIds
     * @param taxDate
     */
    public void submitInvoiceCertified(List<Long> headerIds,
                                       String taxDate) {
       List<InvoiceAuthenticationSendLineCO> invoices = new ArrayList<>();
        if(CollectionUtils.isEmpty(headerIds)){
            return;
        }
        headerIds.stream().forEach(id -> {
           InvoiceHead invoiceHead =  invoiceHeadService.selectById(id);
           if(invoiceHead == null){
               throw new BizException(RespCode.INVOICE_HEADER_IS_NULL);
           }
           invoices.add(InvoiceAuthenticationSendLineCO.builder()
                   .id(id).invoiceCode(invoiceHead.getInvoiceCode())
                   .invoiceNo(invoiceHead.getInvoiceNo())
                   .applyTaxPeriod(taxDate)
                   .buyerTaxNo(BUYER_TAX_NO)
                   .applyRzlx(APPLYRZLX)
                   .build());
        });
        InvoiceAuthenticationReturnCO returnCO = organizationService.invoiceAuthentication(
                InvoiceAuthenticationSendCO.builder()
                .batchNo(UUID.randomUUID().toString())
                .contentRows(headerIds.size())
                .buyerTaxNo(BUYER_TAX_NO)
                .invoices(invoices)
                .build());
        //提交成功
        if(RETURN_CODE.equals(returnCO.getReturnCode())){
            this.updateInvoiceCertifiedStatus(headerIds,CertificationEnum.CERTIFICATION.getId(),null);
        }else {
            throw  new BizException(returnCO.getReturnMessage());
        }
    }

    /**
     * 认证失败更新单张发票状态
     * @param headerId
     * @param invoiceStatus
     */
    @Transactional
    public void updateInvoiceStatus(Long headerId,Integer invoiceStatus){
        InvoiceHead invoiceHead =  invoiceHeadService.selectById(headerId);
        if(invoiceHead == null){
            throw new BizException(RespCode.INVOICE_HEADER_IS_NULL);
        }
        invoiceHead.setInvoiceStatus(invoiceStatus.longValue());
        invoiceHeadService.updateById(invoiceHead);
    }
}
