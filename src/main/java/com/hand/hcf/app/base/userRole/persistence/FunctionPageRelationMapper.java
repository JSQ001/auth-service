package com.hand.hcf.app.base.userRole.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
public interface FunctionPageRelationMapper extends BaseMapper<FunctionPageRelation>{
    List<PageList> filterFunctionPageRelationByCond(@Param("pageName") String pageName,
                                                    Page page);
}
