package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashWriteOffDocumentAmountCO;
import com.hand.hcf.app.payment.domain.CashWriteOff;
import com.hand.hcf.app.payment.web.dto.CashWriteOffReserveDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by kai.zhang on 2017-10-30.
 */
public interface CashWriteOffMapper extends BaseMapper<CashWriteOff> {

    @MapKey("documentLineId")
    Map<Long,Map> selectDocumentWriteOffAmount(@Param(value = "documentCategory") String documentCategory,
                                               @Param(value = "documentId") Long documentId,
                                               @Param(value = "documentLineId") Long documentLineId);

    /**
     * 获取待反冲明细数据
     * @param setOfBooksId
     * @param documentNumber
     * @param applicantId
     * @param sourceDocumentNumber
     * @param billCode
     * @param createdDateFrom
     * @param createdDateTo
     * @param writeOffAmountFrom
     * @param writeOffAmountTo
     * @param writeOffDateFrom
     * @param writeOffDateTo
     * @param page
     * @return
     */
    List<CashWriteOffReserveDTO> selectWaitingReserveWriteOffDetail(@Param(value = "setOfBooksId") Long setOfBooksId,
                                                                    @Param(value = "documentNumber") String documentNumber,
                                                                    @Param(value = "applicantId") Long applicantId,
                                                                    @Param(value = "sourceDocumentNumber") String sourceDocumentNumber,
                                                                    @Param(value = "billCode") String billCode,
                                                                    @Param(value = "createdDateFrom") ZonedDateTime createdDateFrom,
                                                                    @Param(value = "createdDateTo") ZonedDateTime createdDateTo,
                                                                    @Param(value = "writeOffAmountFrom") BigDecimal writeOffAmountFrom,
                                                                    @Param(value = "writeOffAmountTo") BigDecimal writeOffAmountTo,
                                                                    @Param(value = "writeOffDateFrom") ZonedDateTime writeOffDateFrom,
                                                                    @Param(value = "writeOffDateTo") ZonedDateTime writeOffDateTo,
                                                                    Page page);

    /**
     * 获取反冲数据 - 分页
     * @param setOfBooksId
     * @param documentNumber
     * @param applicantId
     * @param sourceDocumentNumber
     * @param billCode
     * @param createdDateFrom
     * @param createdDateTo
     * @param writeOffAmountFrom
     * @param writeOffAmountTo
     * @param status
     * @param approvalId
     * @param writeOffReverseAmountFrom
     * @param writeOffReverseAmountTo
     * @param writeOffDateFrom
     * @param writeOffDateTo
     * @param createdBy
     * @param sourceWriteOffId
     * @param page
     * @return
     */
    List<CashWriteOffReserveDTO> selectReservedWriteOffDetail(@Param(value = "setOfBooksId") Long setOfBooksId,
                                                              @Param(value = "documentNumber") String documentNumber,
                                                              @Param(value = "applicantId") Long applicantId,
                                                              @Param(value = "sourceDocumentNumber") String sourceDocumentNumber,
                                                              @Param(value = "billCode") String billCode,
                                                              @Param(value = "createdDateFrom") ZonedDateTime createdDateFrom,
                                                              @Param(value = "createdDateTo") ZonedDateTime createdDateTo,
                                                              @Param(value = "writeOffAmountFrom") BigDecimal writeOffAmountFrom,
                                                              @Param(value = "writeOffAmountTo") BigDecimal writeOffAmountTo,
                                                              @Param(value = "status") String status,
                                                              @Param(value = "approvalId") Long approvalId,
                                                              @Param(value = "writeOffReverseAmountFrom") BigDecimal writeOffReverseAmountFrom,
                                                              @Param(value = "writeOffReverseAmountTo") BigDecimal writeOffReverseAmountTo,
                                                              @Param(value = "writeOffDateFrom") ZonedDateTime writeOffDateFrom,
                                                              @Param(value = "writeOffDateTo") ZonedDateTime writeOffDateTo,
                                                              @Param(value = "createdBy") Long createdBy,
                                                              @Param(value = "sourceWriteOffId") Long sourceWriteOffId,
                                                              Page page);

    /**
     * 获取反冲数据
     * @param setOfBooksId
     * @param documentNumber
     * @param applicantId
     * @param sourceDocumentNumber
     * @param billCode
     * @param createdDateFrom
     * @param createdDateTo
     * @param writeOffAmountFrom
     * @param writeOffAmountTo
     * @param status
     * @param approvalId
     * @param writeOffReverseAmountFrom
     * @param writeOffReverseAmountTo
     * @param createdBy
     * @param sourceWriteOffId
     * @return
     */
    List<CashWriteOffReserveDTO> selectReservedWriteOffDetail(@Param(value = "setOfBooksId") Long setOfBooksId,
                                                              @Param(value = "documentNumber") String documentNumber,
                                                              @Param(value = "applicantId") Long applicantId,
                                                              @Param(value = "sourceDocumentNumber") String sourceDocumentNumber,
                                                              @Param(value = "billCode") String billCode,
                                                              @Param(value = "createdDateFrom") ZonedDateTime createdDateFrom,
                                                              @Param(value = "createdDateTo") ZonedDateTime createdDateTo,
                                                              @Param(value = "writeOffAmountFrom") Double writeOffAmountFrom,
                                                              @Param(value = "writeOffAmountTo") Double writeOffAmountTo,
                                                              @Param(value = "status") String status,
                                                              @Param(value = "approvalId") Long approvalId,
                                                              @Param(value = "writeOffReverseAmountFrom") Double writeOffReverseAmountFrom,
                                                              @Param(value = "writeOffReverseAmountTo") Double writeOffReverseAmountTo,
                                                              @Param(value = "createdBy") Long createdBy,
                                                              @Param(value = "sourceWriteOffId") Long sourceWriteOffId);

    List<CashWriteOffDocumentAmountCO> listDocumentByWriteOffAmount(@Param(value = "unWriteOffAmountFrom") BigDecimal unWriteOffAmountFrom,
                                                                    @Param(value = "unWriteOffAmountTo") BigDecimal unWriteOffAmountTo,
                                                                    @Param(value = "setOfBooksId") Long setOfBooksId,
                                                                    @Param(value = "tenantId") Long tenantId);

    List<Map<String,Map>> selectDocumentWriteOffAmountBatch(@Param(value = "documentCategory") String documentCategory,
                                                            @Param(value = "documentIds") List<Long> documentIds);

    List<CashWriteOff> getPrepaymentWriteOffHistory(RowBounds page,
                                                    @Param(value = "prepaymentRequisitionId") Long prepaymentRequisitionId);

    List<Long> listExcludeDocumentByWriteOffAmount(@Param(value = "unWriteOffAmountFrom") BigDecimal unWriteOffAmountFrom,
                                                   @Param(value = "unWriteOffAmountTo") BigDecimal unWriteOffAmountTo,
                                                   @Param(value = "setOfBooksId") Long setOfBooksId,
                                                   @Param(value = "tenantId") Long tenantId);
}
