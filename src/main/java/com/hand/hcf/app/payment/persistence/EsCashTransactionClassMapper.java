package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.web.dto.EsCashTransactionClassDTO;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by zhaohzu on 2018/6/4.
 */
public interface EsCashTransactionClassMapper extends BaseMapper<EsCashTransactionClassDTO>{
    List<EsCashTransactionClassDTO> selectAllCashTransactionClass(RowBounds page);
}
