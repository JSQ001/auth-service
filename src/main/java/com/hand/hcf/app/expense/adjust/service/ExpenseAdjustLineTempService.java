package com.hand.hcf.app.expense.adjust.service;

import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLineTemp;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustLineTempMapper;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/11
 */
@Service
public class ExpenseAdjustLineTempService extends BaseService<ExpenseAdjustLineTempMapper, ExpenseAdjustLineTemp> {


    public ImportResultDTO queryImportResultInfo(String transactionUUID) {
        return baseMapper.queryImportResultInfo(transactionUUID);
    }

    public List<ExpenseAdjustLineTemp> listResult(String transactionNumber) {
        return baseMapper.listResult(transactionNumber);
    }
}
