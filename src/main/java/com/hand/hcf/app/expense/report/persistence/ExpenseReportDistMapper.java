package com.hand.hcf.app.expense.report.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.report.dto.ExpenseReportDistDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 09:59
 * @remark
 */
public interface ExpenseReportDistMapper extends BaseMapper<ExpenseReportDist>{
    List<ExpenseReportDistDTO> queryExpenseReportDistFromApplication(RowBounds rowBounds,
                                                                     @Param("ew")Wrapper<ExpenseReportDist> wrapper,
                                                                     @Param("documentNumber")String documentNumber,
                                                                     @Param("reportDocumentNumber")String reportDocumentNumber);
}
