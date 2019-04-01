package com.hand.hcf.app.expense.travel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationHeader;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationHeaderWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author zhu.zhao
 * @date 2019/3/11
 */
public interface TravelApplicationHeaderMapper extends BaseMapper<TravelApplicationHeader> {

    /**
     * 根据ID查询差旅申请单头信息，以及其头维度信息
     * @param id
     * @param headerFlag
     * @return
     */
    TravelApplicationHeaderWebDTO getHeaderWebDTOById(@Param("id") Long id,
                                                      @Param("headerFlag") Integer headerFlag);

    /**
     * 分页条件查询费用申请单头信息
     * （选出当前用户为创建人或统一订票人或出行人的单据头）
     * @param rowBounds
     * @param wrapper
     * @return
     */
    List<TravelApplicationHeaderWebDTO> listByCondition(RowBounds rowBounds,
                                                        @Param("ew") Wrapper wrapper,
                                                        @Param("currentUserId") Long currentUserId);
}
