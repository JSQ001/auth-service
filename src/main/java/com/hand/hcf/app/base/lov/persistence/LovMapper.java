package com.hand.hcf.app.base.lov.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.lov.domain.Lov;
import com.hand.hcf.app.base.lov.web.dto.LovInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LovMapper extends BaseMapper<Lov> {

    /**
     * Lov  分页查询
     *
     * @param lovCode lov 代码
     * @param lovName lov名称
     * @param appId 应用id
     * @param remarks 备注
     * @return
     */
    List<Lov> pageAll(@Param("lovCode") String lovCode,
                      @Param("lovName") String lovName,
                      @Param("appId") Long appId,
                      Page page,
                      @Param("remarks") String remarks);

    /**
     * 根据id获取
     * @param id id
     * @return Lov
     */
    Lov getById(@Param("id")Long id);

    /**
     * 根据Code获取
     * @param code id
     * @return Lov
     */
    LovInfoDTO getDetailInfoByCode(@Param("code")String code);
}
