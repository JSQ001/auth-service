package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTaxDist;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTaxDistMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:39
 * @remark
 */
@Service
public class ExpenseReportTaxDistService extends BaseService<ExpenseReportTaxDistMapper,ExpenseReportTaxDist>{


    /**
     * 根据报账单ID删除税金分摊行信息
     * @param headerId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportTaxDistByHeaderId(Long headerId){
        return delete(new EntityWrapper<ExpenseReportTaxDist>().eq("exp_report_header_id",headerId));
    }

    /**
     * 根据分摊行ID删除税金分摊行
     * @param distId
     * @return
     */
    public boolean deleteExpenseReportTaxDistByDistId(Long distId){
        return delete(new EntityWrapper<ExpenseReportTaxDist>().eq("exp_report_dist_id",distId));
    }

    /**
     * 根据分摊行ID集合删除税金分摊行
     * @param distIds
     * @return
     */
    public boolean deleteExpenseReportTaxDistByDistIds(List<Long> distIds){
        return delete(new EntityWrapper<ExpenseReportTaxDist>().in("exp_report_dist_id",distIds));
    }

}
