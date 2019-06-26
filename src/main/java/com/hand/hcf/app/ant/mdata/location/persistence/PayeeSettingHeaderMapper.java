package com.hand.hcf.app.ant.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingHeader;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingHeaderDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 14:59:03
 * @remark
 */
public interface PayeeSettingHeaderMapper extends BaseMapper<PayeeSettingHeader>{
    List<PayeeSettingHeaderDTO> queryHeader(@Param("payeeSettingHeaderId") Long payeeSettingHeaderId,
                                            @Param("payeeCountryCode") String payeeCountryCode,
                                            @Param("payeeCityCode") String payeeCityCode,
                                            @Param("payerCountryCode") String payerCountryCode,
                                            @Param("payerCityCode") String payerCityCode,
                                            Pagination mybatisPage);
}
