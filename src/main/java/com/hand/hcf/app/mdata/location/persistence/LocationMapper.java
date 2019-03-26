package com.hand.hcf.app.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.location.domain.Location;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 18:15 2018/5/17
 * @Modified by
 */
public interface LocationMapper extends BaseMapper<Location> {
    List<Location> selectAll(RowBounds rowBounds);

    List<Location> selectDeatailByCode(@Param("code") String code, @Param("language") String language);

    List<String> listCountryCode();
}
