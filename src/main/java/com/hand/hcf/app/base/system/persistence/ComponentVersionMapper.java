package com.hand.hcf.app.base.system.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.system.domain.ComponentVersion;
import org.apache.ibatis.annotations.Param;

public interface ComponentVersionMapper extends BaseMapper<ComponentVersion> {
        //通过菜单id 获取组件最后一个版本的contents
        ComponentVersion getLatestComponentVersionByMenuId(@Param("menuId") Long menuId);
}
