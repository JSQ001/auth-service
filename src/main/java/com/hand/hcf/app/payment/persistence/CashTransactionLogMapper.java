package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.payment.domain.CashTransactionLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 韩雪 on 2017/9/30.
 */
public interface CashTransactionLogMapper extends BaseMapper<CashTransactionLog>{
    List<CashTransactionLog> getCashTransactionLogByDataId(@Param("dateId") Long dateId, Page page);
}
