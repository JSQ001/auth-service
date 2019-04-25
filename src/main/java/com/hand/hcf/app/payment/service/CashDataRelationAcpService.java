package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.payment.domain.CashDataRelationAcp;
import com.hand.hcf.app.payment.domain.CashTransactionData;
import com.hand.hcf.app.payment.persistence.CashDataRelationAcpMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionDataMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 付款申请单关联支付通用表
 * @Date: Created in 17:42 2018/4/25
 * @Modified by
 */
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CashDataRelationAcpService extends BaseService<CashDataRelationAcpMapper,CashDataRelationAcp> {

    public static final String ACP_REQUISITION = "ACP_REQUISITION";


    private CashTransactionDataMapper cashTransactionDataMapper;

    /**
     * @Author: bin.xie
     * @Description: 创建关联记录
     * @param: list
     * @return: void
     * @Date: Created in 2018/4/25 17:46
     * @Modified by
     */
    @Transactional
    public void createData(List<CashDataRelationAcp> list){
        list.stream().forEach( u -> {
            if (u.getAmount().doubleValue() <= 0) {
                throw new BizException(RespCode.PAYMENT_ACP_EXP_REPORT_RELATION_AMOUNT_SMALL);
            }
            u.setDocumentType(ACP_REQUISITION);
            //删除可能存在的数据
            EntityWrapper<CashDataRelationAcp> ew = new EntityWrapper<>();
            ew.eq("report_head_id", u.getReportHeadId());
            ew.eq("report_line_id", u.getReportLineId());
            ew.eq("document_type", u.getDocumentType());
            ew.eq("document_head_id", u.getDocumentHeadId());
            ew.eq("document_line_id", u.getDocumentLineId());
            baseMapper.delete(ew);

            //获取通用支付明细
            List<CashTransactionData> dataList = cashTransactionDataMapper.selectList(new EntityWrapper<CashTransactionData>()
                .eq("document_header_id",u.getReportHeadId())
                .eq("document_line_id",u.getReportLineId())
                .eq("frozen_flag",1)
                .eq("document_category","PUBLIC_REPORT"));
            // 关联的报账单行信息在通用支付数据内存在多条记录
            if (dataList == null || dataList.size() != 1){
                throw new BizException(RespCode.PAYMENT_ACP_CASH_DATA_NOT_UNIQUE);
            }
            //按照付款申请单头和付款申请单行查询此付款申请单已被关联金额a，本次关联金额b，此计划付款行总金额c;a+b 要<= c，当 > c 时则错误
            BigDecimal amountA = BigDecimal.ZERO;
            List<CashDataRelationAcp> listRelation = baseMapper.selectList(
                    new EntityWrapper<CashDataRelationAcp>()
                            .eq("report_head_id", u.getReportHeadId())
                            .eq("report_line_id", u.getReportLineId())
                            .eq("document_type", u.getDocumentType()));
            for (CashDataRelationAcp item : listRelation){
                amountA = TypeConversionUtils.roundHalfUp(item.getAmount().add(amountA));
            }
            // 关联金额超过计划付款行总金额
            if (dataList.get(0).getAmount().compareTo(TypeConversionUtils.roundHalfUp(u.getAmount().add(amountA))) == -1){
                throw new BizException(RespCode.PAYMENT_ACP_RELATION_AMOUNT_TOO_BIG);
            }
            u.setId(null);
            u.setVersionNumber(1);
            u.setLastUpdatedBy(u.getCreatedBy());
            u.setLastUpdatedDate(ZonedDateTime.now());
            u.setCreatedDate(ZonedDateTime.now());
            this.insert(u);
        });
    }

    /**
     * @Author: bin.xie
     * @Description: 通过付款申请单ID删除关联记录
     * @param: id
     * @return: void
     * @Date: Created in 2018/4/25 17:49
     * @Modified by
     */
    @Transactional
    public void deleteByAcp(Long id){
        baseMapper.delete(new EntityWrapper<CashDataRelationAcp>().eq("document_head_id",id));
    }
}
