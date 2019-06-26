package com.hand.hcf.app.expense.input.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxHeaderDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;


/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 14:43
 */
public interface ExpInputTaxHeaderMapper extends BaseMapper<ExpInputTaxHeader>{
    List<ExpInputTaxHeaderDTO> queryExpInputFinance(RowBounds rowBounds,
                                                    @Param("ew") Wrapper<ExpInputTaxHeader> wrapper);

    int getCountByCondition(  @Param("ew") Wrapper<ExpInputTaxHeader> wrapper);
}
