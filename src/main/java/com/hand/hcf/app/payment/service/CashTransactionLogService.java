package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.exception.core.IdNotNullInCreateActionException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionLog;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CashTransactionLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 韩雪 on 2017/9/30.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashTransactionLogService extends BaseService<CashTransactionLogMapper, CashTransactionLog> {
    private final CashTransactionLogMapper cashTransactionLogMapper;

    private final PaymentOrganizationService organizationService;

    public CashTransactionLogService(CashTransactionLogMapper cashTransactionLogMapper, PaymentOrganizationService organizationService) {
        this.cashTransactionLogMapper = cashTransactionLogMapper;
        this.organizationService = organizationService;
    }

    /**
     * 新增 通用支付平台日志表
     *
     * @param cashTransactionLog
     * @return
     */
    public CashTransactionLog createCashTransactionLog(CashTransactionLog cashTransactionLog) {
        if (cashTransactionLog.getId() != null) {
            throw new IdNotNullInCreateActionException();
        }

        cashTransactionLogMapper.insert(cashTransactionLog);
        return cashTransactionLog;
    }

    /**
     * 自定义条件查询 通用支付平台日志表(分页)
     *
     * @param paymentDetailId
     * @param page
     * @return
     */
    public List<CashTransactionLog> getCashTransactionLogByCond(Long paymentDetailId, Page page) {
        return cashTransactionLogMapper.selectPage(page,
                new EntityWrapper<CashTransactionLog>()
                        .eq("payment_detail_id", paymentDetailId)
                        .orderBy("operation_time", false)
        );
    }

    /**
     * 自定义条件查询 通用支付平台日志表(不分页)
     *
     * @param paymentDetailId
     * @return
     */
    public List<CashTransactionLog> getCashTransactionLogAllByCond(Long paymentDetailId) {
        return cashTransactionLogMapper.selectList(
                new EntityWrapper<CashTransactionLog>()
                        .eq("payment_detail_id", paymentDetailId)
                        .orderBy("operation_time", false)
        );
    }

    /**
     * 创建通用支付平台日志表
     *
     * @param paymentDetailId 支付明细表ID
     * @param operationType   操作用户ID
     * @param remark           备注
     * @return
     */
    public CashTransactionLog createCashTransactionLog(Long paymentDetailId, String operationType,String remark,byte[] bankMessage) {
        if (paymentDetailId == null || operationType == null) {
            return null;
        }
        CashTransactionLog cashTransactionLog = new CashTransactionLog();
        cashTransactionLog.setPaymentDetailId(paymentDetailId);
        cashTransactionLog.setUserId(OrgInformationUtil.getCurrentUserId());
        cashTransactionLog.setOperationType(operationType);
        cashTransactionLog.setOperationTime(ZonedDateTime.now());

        cashTransactionLog.setRemark(remark);
        cashTransactionLog.setBankMessage(bankMessage);

        cashTransactionLogMapper.insert(cashTransactionLog);
        return cashTransactionLog;
    }

    /**
     * 创建通用支付平台日志表
     *
     * @param paymentDetailId 支付明细表ID
     * @param operationType   操作用户ID
     * @param remark           备注
     * @return
     */
    public CashTransactionLog createPayOperatorLog(Long paymentDetailId, String operationType,String remark,Long userId) {
        if (paymentDetailId == null || operationType == null) {
            return null;
        }
        CashTransactionLog cashTransactionLog = new CashTransactionLog();
        cashTransactionLog.setPaymentDetailId(paymentDetailId);
        cashTransactionLog.setUserId(userId);
        cashTransactionLog.setOperationType(operationType);
        cashTransactionLog.setOperationTime(ZonedDateTime.now());

        cashTransactionLog.setRemark(remark);
        cashTransactionLog.setBankMessage(null);

        cashTransactionLogMapper.insert(cashTransactionLog);
        return cashTransactionLog;
    }
    /**
     * 根据支付表id查询对应的支付明细id的日志
     *
     * @param dateId
     * @param page
     * @return
     */
    public List<CashTransactionLog> getCashTransactionLogByDataId(Long dateId,Page page){
        List<CashTransactionLog> list = cashTransactionLogMapper.getCashTransactionLogByDataId(dateId, page);

        list.stream().forEach(cashTransactionLog -> {
            //拿到userName
            List<Long> userIds = new ArrayList<>();
            userIds.add(cashTransactionLog.getUserId());
            List<ContactCO> standardCOs = organizationService.listByUserIds(userIds);
            if (standardCOs != null){
                cashTransactionLog.setUserName(standardCOs.get(0).getFullName());
            }
            //拿到支付日志操作类型name

            cashTransactionLog.setOperationTypeName(organizationService.getSysCodeValueByCodeAndValue(
                    PaymentSystemCustomEnumerationType.CSH_LOG_OPERATION_TYPE, cashTransactionLog.getOperationType()).getName());

        });
        return list;
    }
}
