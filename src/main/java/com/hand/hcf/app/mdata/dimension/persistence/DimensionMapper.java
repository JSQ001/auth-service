package com.hand.hcf.app.mdata.dimension.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.dimension.domain.Dimension;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DimensionMapper extends BaseMapper<Dimension> {

    List<Dimension> listDimensionsByCompanyId(@Param("setOfBooksId") Long setOfBooksId,
                                              @Param("companyId") Long companyId,
                                              @Param("enabled") Boolean enabled);
}
