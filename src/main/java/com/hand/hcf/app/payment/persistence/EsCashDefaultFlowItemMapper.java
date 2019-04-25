package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.web.dto.EsCashDefaultFlowItemDTO;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by zhaohzu on 2018/6/4.
 */
public interface EsCashDefaultFlowItemMapper extends BaseMapper<EsCashDefaultFlowItemDTO>{
    List<EsCashDefaultFlowItemDTO> selectAllCashDefaultFlowItem(RowBounds page);
}
