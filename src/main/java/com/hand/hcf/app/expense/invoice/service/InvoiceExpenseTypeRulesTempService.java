package com.hand.hcf.app.expense.invoice.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceExpenseTypeRulesTempMapper;
import com.hand.hcf.app.expense.invoice.domain.temp.InvoiceExpenseTypeRulesTemp;

@Service
public class InvoiceExpenseTypeRulesTempService extends BaseService<InvoiceExpenseTypeRulesTempMapper, InvoiceExpenseTypeRulesTemp> {

    /**
     * 删除两天以前的数据
     * @author sq.l
     * @date 2019/04/22
     */
    public void deleteHistoryData() {
        baseMapper.delete(new EntityWrapper<InvoiceExpenseTypeRulesTemp>()
                .le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    /**
     * 查询导入结果
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionOid 批次号
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionOid) {
        return baseMapper.queryInfo(transactionOid);
    }

    /**
     * 确认导入
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionOid 批次号
     * @return
     */
    public Boolean confirmImport(String transactionOid) {
        baseMapper.confirmImport(transactionOid, OrgInformationUtil.getCurrentTenantId(),OrgInformationUtil.getCurrentUserId(), ZonedDateTime.now());
        //插入完成后清空临时表
        baseMapper.delete(new EntityWrapper<InvoiceExpenseTypeRulesTemp>().eq("batch_number", transactionOid));
        return true;
    }

}
