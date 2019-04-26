package com.hand.hcf.app.payment.web.adapter;

import com.hand.hcf.app.core.web.adapter.DomainObjectAdapter;
import com.hand.hcf.app.payment.domain.CashWriteOff;
import com.hand.hcf.app.payment.externalApi.PaymentPrepaymentService;
import com.hand.hcf.app.payment.web.dto.CashPrepaymentQueryDTO;
import com.hand.hcf.app.payment.web.dto.CashTransactionWriteOffDTO;
import com.hand.hcf.app.payment.web.dto.CashWriteOffRequestWebDto;
import com.hand.hcf.app.payment.web.dto.CashWriteOffWebDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kai.zhang on 2017-11-01.
 */
@Component
public class CashWriteOffAdapter {

    private static PaymentPrepaymentService prepaymentService;

    //根据set方法注入静态对象
    @Autowired
    public void setPrepaymentService(PaymentPrepaymentService prepaymentService) {
        CashWriteOffAdapter.prepaymentService = prepaymentService;
    }

    private CashWriteOffAdapter(){
    }

    public static CashWriteOff toDomain(CashWriteOffWebDto detailDto){
        CashWriteOff domain = new CashWriteOff();
        DomainObjectAdapter.toDomain(domain,detailDto);
        domain.setCshTransactionDetailId(detailDto.getCshTransactionDetailId());
        domain.setWriteOffAmount(detailDto.getWriteOffAmount());
        domain.setWriteOffDate(detailDto.getWriteOffDate());
        domain.setPeriodName(detailDto.getPeriodName());
        domain.setTenantId(detailDto.getTenantId());
        return domain;
    }

    public static CashWriteOffWebDto toDto(CashWriteOff domain){
        CashWriteOffWebDto dto = new CashWriteOffWebDto();
        DomainObjectAdapter.toDto(dto,domain);
        dto.setWriteOffAmount(domain.getWriteOffAmount());
        dto.setWriteOffDate(domain.getWriteOffDate());
        dto.setPeriodName(domain.getPeriodName());
        dto.setCshTransactionDetailId(domain.getCshTransactionDetailId());
        dto.setTenantId(domain.getTenantId());
        dto.setOperationType(domain.getOperationType());
        dto.setDocumentHeaderId(domain.getDocumentHeaderId());
        dto.setDocumentLineId(domain.getDocumentLineId());
        return dto;
    }

    public static void setDocumentFields(CashWriteOffRequestWebDto requestDto, CashWriteOff accept){
        accept.setDocumentType(requestDto.getDocumentType());
        accept.setDocumentHeaderId(requestDto.getDocumentHeaderId());
        accept.setDocumentLineId(requestDto.getDocumentLineId());
    }

    public static void setDtoBasicsMessage(CashWriteOffWebDto detailDto, CashWriteOff cashWriteOff){
        detailDto.setStatus(cashWriteOff.getStatus());
        DomainObjectAdapter.toDto(detailDto,cashWriteOff);
    }

    public static CashWriteOffWebDto paymentToDto(CashPrepaymentQueryDTO payDto){
        CashWriteOffWebDto detailDto = new CashWriteOffWebDto();
        detailDto.setBillcode(payDto.getBillCode());
        detailDto.setCshTransactionDetailId(payDto.getCashTransactionDetailId());
        detailDto.setPayDate(payDto.getPayDate());
        detailDto.setPrepaymentRequisitionAmount(payDto.getAmount());
        detailDto.setUnWriteOffAmount(payDto.getWriteOffAmount());
        if(StringUtils.isNotEmpty(payDto.getDocumentTypeName())){
            detailDto.setPrepaymentRequisitionTypeDesc(payDto.getDocumentTypeName());
        }else{
            String prepaymentTypeName = prepaymentService.getPrepaymentTypeByID(payDto.getDocumentTypeId());
            if(prepaymentTypeName != null){
                detailDto.setPrepaymentRequisitionTypeDesc(prepaymentTypeName);
            }
        }
        detailDto.setPrepaymentRequisitionNumber(payDto.getDocumentNumber());
        detailDto.setTenantId(payDto.getTenantId());
        detailDto.setCurrencyCode(payDto.getCurrencyCode());
        detailDto.setWriteOffAmount(payDto.getWriteOffAmountForThisDocument().compareTo(BigDecimal.ZERO) == 0? null : payDto.getWriteOffAmountForThisDocument());
        return detailDto;
    }

    public static List<CashWriteOffWebDto> paymentToDto(List<CashPrepaymentQueryDTO> payDtoList){
        return payDtoList.stream().map(payDto -> {
            return paymentToDto(payDto);
        }).collect(Collectors.toList());
    }

    public static CashTransactionWriteOffDTO toTransactionWriteOffDto(CashWriteOffWebDto detailDto, Long lastUpdatedBy){
        CashTransactionWriteOffDTO dto = new CashTransactionWriteOffDTO();
        dto.setCashTransactionDetailId(detailDto.getCshTransactionDetailId());
        dto.setWriteOffAmountAfter(detailDto.getWriteOffAmount());
        dto.setWriteOffAmountBefore(detailDto.getWriteOffAmountBefore());
        dto.setLastUpdatedBy(lastUpdatedBy);
        return dto;
    }

    public static CashTransactionWriteOffDTO toTransactionWriteOffDto(CashWriteOff domain, Long lastUpdatedBy){
        CashTransactionWriteOffDTO dto = new CashTransactionWriteOffDTO();
        dto.setCashTransactionDetailId(domain.getCshTransactionDetailId());
        dto.setWriteOffAmountAfter(domain.getWriteOffAmount());
        dto.setWriteOffAmountBefore(BigDecimal.ZERO);
        dto.setLastUpdatedBy(lastUpdatedBy);
        return dto;
    }
}
