package com.hand.hcf.app.mdata.system.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.system.domain.MobileValidate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Transy on 2017/8/2.
 */
public interface MobileValidateMapper extends BaseMapper<MobileValidate> {
    List<MobileValidate> findAll(@Param("enabled") Boolean enabled);

    void insetMobileValidateI18n(@Param("id") Long id, @Param("language") String language, @Param("countryName") String countryName);
}
