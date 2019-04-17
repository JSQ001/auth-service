package com.hand.hcf.app.expense.adjust.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLineTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/11
 */
public interface ExpenseAdjustLineTempMapper extends BaseMapper<ExpenseAdjustLineTemp> {

    ImportResultDTO queryImportResultInfo(@Param("transactionUUID") String transactionUUID);

    List<ExpenseAdjustLineTemp> listResult(@Param("transactionNumber") String transactionNumber);
}
