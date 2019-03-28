package com.hand.hcf.app.mdata.bank.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.bank.domain.BankTransaction;
import com.hand.hcf.app.mdata.bank.dto.BanTranPoolDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/24 09:44
 */
public interface BankTransactionMapper extends BaseMapper<BankTransaction> {
    /**
     * 查询X用户可用于报销的商务卡交易数据
     * 交易日期由大-->小进行排序
     * @param cardTypeCode
     * @param userOID
     * @param currMaxID 当前页最大ID
     * @param used
     * @param page
     * @return
     */
    List<BanTranPoolDTO> selectBanTranPoolDTOPageable(@Param("cardTypeCode") String cardTypeCode,
                                                      @Param("userOID") UUID userOID,
                                                      @Param("currMaxID") Long currMaxID,
                                                      @Param("trsDate") String trsDate,
                                                      @Param("trxTime") String trxTime,
                                                      @Param("used") Boolean used,
                                                      Page<BanTranPoolDTO> page);

}
