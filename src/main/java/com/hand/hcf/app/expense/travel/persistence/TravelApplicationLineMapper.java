package com.hand.hcf.app.expense.travel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLine;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TravelApplicationLineMapper extends BaseMapper<TravelApplicationLine> {
    /**
     * 根据单据头Id查询行信息
     * @param headerId
     * @param rowBounds
     * @return
     */
    List<TravelApplicationLineWebDTO> getLinesByHeaderId(@Param("headerId") Long headerId,
                                                         @Param("currentUserId") Long currentUserId,
                                                         RowBounds rowBounds);
}
