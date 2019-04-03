package com.hand.hcf.app.base.userRole.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.ContentFunctionRelation;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.dto.ContentFunctionDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
public interface ContentFunctionRelationMapper extends BaseMapper<ContentFunctionRelation>{
    List<FunctionList> filterContentFunctionRelationByCond(@Param("functionName") String functionName,
                                                          Page page);

    List<ContentFunctionDTO> listContentFunctions(@Param("functionIds") List<Long> functionIds);

    List<ContentFunctionDTO> listNotAssignFunction(@Param("functionIds")List<Long> functionIds);

    /**
     * 查询角色可分配的菜单
     * @param roleId
     * @return
     */
    List<ContentFunctionDTO> listCanAssignFunction(@Param("roleId") Long roleId);
}
