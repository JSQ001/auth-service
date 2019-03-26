package com.hand.hcf.app.mdata.parameter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.parameter.domain.ParameterSetting;
import com.hand.hcf.app.mdata.parameter.dto.ParameterSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 14:28
 */
public interface ParameterSettingMapper extends BaseMapper<ParameterSetting> {

    List<ParameterSettingDTO> pageParameterSettingByLevelAndCond(@Param("parameterLevel") String parameterLevel,
                                                                 @Param("tenantId") Long tenantId,
                                                                 @Param("setOfBooksId") Long setOfBooksId,
                                                                 @Param("companyId") Long companyId,
                                                                 @Param("moduleCode") String moduleCode,
                                                                 @Param("parameterCode") String parameterCode,
                                                                 @Param("parameterName") String parameterName,
                                                                 Pagination pagination);
}
