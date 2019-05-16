package com.hand.hcf.app.expense.input.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxDist;
import com.hand.hcf.app.expense.input.dto.ExpInputForReportDistDTO;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxSumAmountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 14:43
 */
public interface ExpInputTaxDistMapper extends BaseMapper<ExpInputTaxDist> {
    List<ExpInputForReportDistDTO> listDistByLineId(@Param("expReportLineId") Long expReportLineId, @Param("inputTaxLineId") Long inputTaxLineId);
    ExpInputTaxSumAmountDTO getSumAmount(@Param("inputTaxLineId") Long inputTaxLineId);

    List<ExpInputTaxDist> getExpInputTaxDistByLineIds(@Param("ew") Wrapper wrapper);
}
