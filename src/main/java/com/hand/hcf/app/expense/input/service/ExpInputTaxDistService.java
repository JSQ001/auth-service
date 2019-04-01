package com.hand.hcf.app.expense.input.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxDist;
import com.hand.hcf.app.expense.input.dto.ExpInputForReportDistDTO;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxSumAmountDTO;
import com.hand.hcf.app.expense.input.persistence.ExpInputTaxDistMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Service
@AllArgsConstructor
@Transactional
public class ExpInputTaxDistService extends BaseService<ExpInputTaxDistMapper, ExpInputTaxDist> {
    @Autowired
    private ExpInputTaxDistMapper expInputTaxDistMapper;


    public List<ExpInputForReportDistDTO> listDistByLineId(Long expReportLineId, Long inputTaxLineId) {
        return expInputTaxDistMapper.listDistByLineId(expReportLineId, inputTaxLineId);
    }

    public void deleteDistById(Long id) {
        expInputTaxDistMapper.deleteById(id);
    }

    public void insertDistData(ExpInputTaxDist expInputTaxDist) {
        expInputTaxDistMapper.insert(expInputTaxDist);
    }

    public ExpInputTaxSumAmountDTO getSumAmount(Long inputTaxLineId) {
        return expInputTaxDistMapper.getSumAmount(inputTaxLineId);
    }

    public void deleteByLineId(Long id){
        expInputTaxDistMapper.delete(new EntityWrapper<ExpInputTaxDist>().eq("input_tax_line_id",id));
    }
}
