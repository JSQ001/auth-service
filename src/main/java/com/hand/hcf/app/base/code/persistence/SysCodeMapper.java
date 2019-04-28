package com.hand.hcf.app.base.code.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.code.domain.SysCode;
import org.apache.ibatis.annotations.Param;

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

    List<Long> listNotExistsTenantIdByCode(@Param("code") String code);

    Integer checkValueExists(@Param("code") String code, @Param("value") String value);
}
