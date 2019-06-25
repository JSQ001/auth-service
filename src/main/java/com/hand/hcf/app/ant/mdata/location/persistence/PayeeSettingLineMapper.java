package com.hand.hcf.app.ant.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingLine;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingLineDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 14:59:03
 * @remark
 */
public interface PayeeSettingLineMapper extends BaseMapper<PayeeSettingLine>{
    List<PayeeSettingLineDTO> queryLineByHeaderId(@Param("payeeSettingHeaderId") Long payeeSettingHeaderId,
                                                  Pagination mybatisPage);
}
