package com.hand.hcf.app.mdata.bank.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.bank.domain.BankTransaction;
import com.hand.hcf.app.mdata.bank.dto.BanTranPoolDTO;
import com.hand.hcf.app.mdata.bank.persistence.BankTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/24 10:02
 */
@Service
public class BankTransactionService extends BaseService<BankTransactionMapper,BankTransaction> {

    @Autowired
    private BankTransactionMapper bankTransactionMapper;

    /**
     * 更新交易备注
     *
     * @param id
     * @param remark
     * @return
     */
    public Integer updateTransactionRemark(Long id, String remark) {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setId(id);
        bankTransaction.setRemark(remark);
        return bankTransactionMapper.updateById(bankTransaction);
    }
    /**
     * X用户X行商务卡消费列表展示
     *
     * @param cardTypeCode
     * @param userOID
     * @param used
     * @param page
     * @return
     */
    public Page<BanTranPoolDTO> getUserTransactions(String cardTypeCode, UUID userOID, Long currMaxID, String trsDate, String trxTime, Boolean used, Page<BanTranPoolDTO> page) {
        page.setRecords(bankTransactionMapper.selectBanTranPoolDTOPageable(cardTypeCode, userOID, currMaxID, trsDate, trxTime, used, page));
        return page;
    }
}
