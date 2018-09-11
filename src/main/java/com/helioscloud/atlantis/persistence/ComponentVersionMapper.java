package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.ComponentVersion;
import org.apache.ibatis.annotations.Param;

public interface ComponentVersionMapper extends BaseMapper<ComponentVersion> {
        //通过菜单id 获取组件最后一个版本的contents
        ComponentVersion getLatestComponentVersionByMenuId(@Param("menuId")Long menuId);
}
