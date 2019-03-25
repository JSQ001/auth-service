package com.hand.hcf.app.base.code.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.code.domain.SysCode;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/25
 */
public interface SysCodeMapper extends BaseMapper<SysCode> {

    List<Long> getNotExistsTenantId();

}
