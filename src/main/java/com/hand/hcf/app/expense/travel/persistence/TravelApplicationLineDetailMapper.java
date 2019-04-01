package com.hand.hcf.app.expense.travel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineDetailWebDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TravelApplicationLineDetailMapper extends BaseMapper<TravelApplicationLineDetail> {
}
