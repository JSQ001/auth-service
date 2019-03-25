package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.persistence.FunctionPageRelationMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Service
@AllArgsConstructor
@Transactional
public class FunctionPageRelationService extends BaseService<FunctionPageRelationMapper,FunctionPageRelation>{
    private final FunctionPageRelationMapper functionPageRelationMapper;

    private final PageListService pageListService;

    private final FunctionListService functionListService;

    /**
     * 批量新增 功能页面关联
     * @return
     */
    @Transactional
    public List<FunctionPageRelation> createFunctionPageRelationBatch(List<FunctionPageRelation> list){
        list.stream().forEach(functionPageRelation -> {
            //校验数据
            FunctionList functionList = functionListService.selectById(functionPageRelation.getFunctionId());
            if (functionList == null){
                throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
            }
            PageList pageList = pageListService.selectById(functionPageRelation.getPageId());
            if (pageList == null){
                throw new BizException(RespCode.PAGE_LIST_NOT_EXIST);
            }
            if (functionPageRelationMapper.selectList(
                    new EntityWrapper<FunctionPageRelation>()
                            .eq("function_id",functionPageRelation.getFunctionId())
                            .eq("page_id",functionPageRelation.getPageId())
            ).size() > 0 ){
                throw new BizException(RespCode.FUNCTION_PAGE_RELATION_EXIST);
            }
            //插入数据
            pageList.setFunctionRouter(functionList.getFunctionRouter());
            pageListService.updateById(pageList);
            functionPageRelationMapper.insert(functionPageRelation);
        });
        return list;
    }

    /**
     * 批量逻辑删除 功能页面关联
     * @param idList
     */
    @Transactional
    public void deleteFunctionPageRelationBatch(List<Long> idList){
        idList.stream().forEach(id -> {
            //校验数据
            FunctionPageRelation functionPageRelation = functionPageRelationMapper.selectById(id);
            if (functionPageRelation == null){
                throw new BizException(RespCode.FUNCTION_PAGE_RELATION_NOT_EXIST);
            }
            if (functionListService.selectById(functionPageRelation.getFunctionId()) == null){
                throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
            }
            PageList pageList = pageListService.selectById(functionPageRelation.getPageId());
            if (pageList == null){
                throw new BizException(RespCode.PAGE_LIST_NOT_EXIST);
            }
            //删除数据
            pageList.setFunctionRouter(null);
            pageListService.updateById(pageList);
            functionPageRelation.setDeleted(true);
            functionPageRelationMapper.updateById(functionPageRelation);
        });
    }

    /**
     * 条件分页查询 功能页面关联
     * @param page
     * @return
     */
    public Page<FunctionPageRelation> geFunctionPageRelationByCond(Page page){
        page = this.selectPage(page,
                new EntityWrapper<FunctionPageRelation>()
                        .eq("deleted",false)
                        .orderBy("last_updated_date",false)
        );
        if (page.getRecords().size() > 0){
            List<FunctionPageRelation> result = page.getRecords();
            result.stream().forEach(functionPageRelation -> {
                functionPageRelation.setFunctionName(functionListService.selectById(functionPageRelation.getFunctionId()).getFunctionName());
                functionPageRelation.setPageName(pageListService.selectById(functionPageRelation.getPageId()).getPageName());
            });
        }
        return page;
    }
}
