package com.hand.hcf.app.ant.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeHeader;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeHeaderDTO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 14:59:03
 * @remark
 */
public interface PayeeHeaderMapper extends BaseMapper<PayeeHeader>{
    List<PayeeHeaderDTO> selectForReport(@Param("payeeHeaderId") Long payeeHeaderId,
                                         @Param("payeeType") String payeeType,
                                         @Param("payeeCountryCode") String payeeCountryCode,
                                         @Param("payeeCityCode") String payeeCityCode,
                                         @Param("payerCountryCode") String payerCountryCode,
                                         @Param("payerCityCode") String payerCityCode,
                                         @Param("payeeCode") String payeeCode,
                                         @Param("payeeName") String payeeName,
                                         Pagination mybatisPage);
}
