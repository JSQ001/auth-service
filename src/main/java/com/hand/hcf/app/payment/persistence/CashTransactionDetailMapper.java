package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionDetailCO;
import com.hand.hcf.app.common.co.PublicReportWriteOffCO;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.web.dto.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by cbc on 2017/9/29.
 */
public interface CashTransactionDetailMapper extends BaseMapper<CashTransactionDetail> {
    List<CashPrepaymentQueryDTO> getPrepaymentResult(PaymentDetailWriteOffCondition condition);

    List<CashPrepaymentQueryDTO> getPrepaymentResult(PaymentDetailWriteOffCondition condition, Page page);

    CashTransactionDetailWebDTO getDetailById(@Param("id") Long id);

    List<CashTransactionDetail> getDetailByContractHeaderId(RowBounds page, @Param("contractHeaderId") Long contractHeaderId);

    List<AmountAndDocumentNumberDTO> getTotalAmountAndDocumentNum(@Param("ew") Wrapper<CashTransactionDetail> wrapper);

    /**
     * @Author: bin.xie
     * @Description: 分页查询可退款数据
     * @param: page
     * @param: cashTransactionData
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/3 16:20
     * @Modified by
     */
    List<CashTransactionDetail> selectRefundByPage(RowBounds page, @Param("ew") Wrapper<CashTransactionDetail> CashTransactionDetail);

    /**
     * @Author: bin.xie
     * @Description: 分组查询支付明细收款方ID
     * @param: cashTransactionData
     * @return: java.util.List<PartnerSelectDTO>
     * @Date: Created in 2018/4/28 15:38
     * @Modified by
     */
    List<PartnerSelectDTO> listPartner(@Param("ew") Wrapper partnerSelectDTO);

    List<CashTransactionDetail> overrideSelectPage(RowBounds page, @Param("ew") Wrapper<CashTransactionDetail> CashTransactionDetail);

    /**
     * @Description: 根据报账单头ID查询报账单支付信息
     * @param: page
     * @param: headerId
     * @param: CashTransactionDetail
     * @return
     * @Date: Created in 2018/7/10 13:09
     * @Modified by
     */
    List<CashTransactionDetail> getDetailByPublicHeaderId(RowBounds page, @Param("headerId") Long headerId, @Param("ew") Wrapper<CashTransactionDetail> CashTransactionDetail);

    List<PublicReportWriteOffCO> selectIdByPrePayment(@Param("headerId") Long headerId, @Param("lineIds") List<Long> lineIds);
    List<Long> getWriteOffDetailsId();

    List<CashTransactionDetailCO> queryCashTransactionDetailByReport(@Param("ew") Wrapper eq);
}
