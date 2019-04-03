package com.hand.hcf.app.mdata.dimension.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DimensionItemMapper extends BaseMapper<DimensionItem> {

    List<DimensionItem> pageDimensionItemsByDimensionIdEnabledCompanyId(@Param("dimensionId") Long dimensionId,
                                                                        @Param("enabled") Boolean enabled,
                                                                        @Param("companyId") Long companyId,
                                                                        Page page);

    /**
     * 根据员工查询维值
     * @param contactId
     * @return
     */
    List<DimensionItem> listDimensionsByContactId(@Param("contactId") Long contactId);
}
