package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.PaymentDocumentAmountCO;
import com.hand.hcf.app.common.co.PublicReportAmountCO;
import com.hand.hcf.app.payment.domain.CashTransactionData;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportHeaderDTO;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportLineDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cbc on 2017/9/29.
 */
public interface CashTransactionDataMapper extends BaseMapper<CashTransactionData> {

    List<CashTransactionData> selectPageCshTransactionData(RowBounds page, @Param("ew") Wrapper<CashTransactionData> cashTransactionData);


    List<CashTransactionData> overrideSelectList(@Param("ew") Wrapper<CashTransactionData> wrapper);

    CashTransactionData overrideSelectById(Serializable id);

    List<CashTransactionData> selectTotalAmount(@Param("ew") Wrapper<CashTransactionData> wrapper);

    /**
     * @Description: 查询付款申请单可关联的报账单信息（待付数据查询)
     * @param: reportNumber
     * @param: applicationId
     * @param: formTypes
     * @param: page
     * @return
     * @Date: Created in 2018/6/20 21:28
     * @Modified by
     */
    List<CashDataPublicReportHeaderDTO> queryReportAssociatedAcp(@Param("reportNumber") String reportNumber,
                                                                 @Param("applicationId") Long applicationId,
                                                                 @Param("formTypes") List<Long> formTypes,
                                                                 Page page,
                                                                 @Param("documentTypeId") Long documentTypeId);
    /**
     * @Description: 付款申请单关联报账单信息
     * @param: id
     * @return
     * @Date: Created in 2018/6/20 21:27
     * @Modified by
     */
    CashDataPublicReportLineDTO getRelationalById(@Param("id") Long id);
    /**
     * @Description: 查询可反冲的报账单的待付数据
     * @param: page
     * @param: wrapper
     * @return
     */
    List<CashTransactionData> selectPublicList(RowBounds page, @Param("ew") Wrapper<CashTransactionData> wrapper);
    List<CashTransactionData> selectPublicList(@Param("ew") Wrapper<CashTransactionData> wrapper);


    /**
     * @Description: 查询可反冲的报账单ID和可反冲金额
     * @param: wrapper
     * @return
     * @Date: Created in 2018/6/20 21:25
     * @Modified by
     */
    List<PublicReportAmountCO> findPublicReserveAmountAndId(@Param("amountFrom") BigDecimal amountFrom,
                                                            @Param("amountTo") BigDecimal amountTo);







    /**
     * @Description: 查询报账单ID和已付金额(未冻结的）
     * @Description: 查询报账单ID和已付金额
     * @param: wrapper
     * @return
     * @Date: Created in 2018/6/20 21:25
     * @Modified by
     */
    List<PublicReportAmountCO> findPublicPaidAmountAndId(@Param("amountFrom") BigDecimal amountFrom,
                                                         @Param("amountTo") BigDecimal amountTo);



    List<PaymentDocumentAmountCO> listAmountByPrepaymentLineIds(@Param("lineIds") List<Long> lineIds,
                                                                @Param("documentCategory") String documentCategory);

    List<PaymentDocumentAmountCO> findAmountByDocumentIds(@Param("documentIds") List<Long> documentIds,
                                                          @Param("documentCategory") String documentCategory,
                                                          @Param("employeeId") Long employeeId,
                                                          @Param("companyId") Long companyId,
                                                          @Param("documentTypeId") Long documentTypeId);
}
