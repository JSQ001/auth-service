package com.hand.hcf.app.mdata.parameter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.mdata.parameter.domain.ParameterValues;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 14:26
 */
public interface ParameterValuesMapper extends BaseMapper<ParameterValues> {

    List<BasicCO> listParameterValuesByPVType(@Param("parameterCode") String parameterCode);
}
