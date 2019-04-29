package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.invoice.domain.temp.InvoiceExpenseTypeRulesTemp;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;

public interface InvoiceExpenseTypeRulesTempMapper extends BaseMapper<InvoiceExpenseTypeRulesTemp> {

    /**
     * 获取导入结果
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionID
     * @return
     */
    ImportResultDTO queryInfo(@Param("transactionID") String transactionID);


    /**
     * 临时表往发票费用映射规则插入数据
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionID
     * @param userId
     * @param currentDate
     * @param TenantId
     */
    void confirmImport(@Param("transactionID") String transactionID,
                       @Param("TenantId") Long  TenantId,
                       @Param("userId") Long userId,
                       @Param("currentDate") ZonedDateTime currentDate);


}
