package com.hand.hcf.app.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 18:48 2018/5/17
 * @Modified by
 */
public interface VendorAliasMapper extends BaseMapper<VendorAlias> {
    List<VendorAlias> selectEsByKey(RowBounds rowBounds, @Param("key") String key, @Param("ew") Wrapper<VendorAlias> wrapper);
}
