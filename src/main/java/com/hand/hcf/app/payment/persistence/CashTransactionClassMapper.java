package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.domain.CashTransactionClass;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 韩雪 on 2017/9/7.
 */
public interface CashTransactionClassMapper extends BaseMapper<CashTransactionClass>{

    CashTransactionClass getById(@Param("id") Long id);
}
