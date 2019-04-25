package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.web.dto.EsCashFlowItemDTO;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by zhaohzu on 2018/6/4.
 */
public interface EsCashFlowItemMapper extends BaseMapper<EsCashFlowItemDTO>{
    List<EsCashFlowItemDTO> selectAllCashFlowItem(RowBounds page);
}
