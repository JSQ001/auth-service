package com.hand.hcf.app.base.userRole.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.dto.FunctionPageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/28
 */
public interface PageListMapper extends BaseMapper<PageList>{

    /**
     * 根据功能id集合获取其所有的界面
     *
     * @param roleIds 角色id集合
     * @return List<FunctionPageDTO>
     */
    List<FunctionPageDTO> listPageByRoleIds(@Param("roleIds") List<Long> roleIds);
}
