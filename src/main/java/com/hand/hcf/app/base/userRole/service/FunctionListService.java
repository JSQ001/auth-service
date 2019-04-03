package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.ContentFunctionRelation;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.persistence.ContentFunctionRelationMapper;
import com.hand.hcf.app.base.userRole.persistence.FunctionListMapper;
import com.hand.hcf.app.base.userRole.persistence.FunctionPageRelationMapper;
import com.hand.hcf.app.base.userRole.persistence.PageListMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Service
@AllArgsConstructor
@Transactional
public class FunctionListService extends BaseService<FunctionListMapper,FunctionList>{
    private final FunctionListMapper functionListMapper;

    private final BaseI18nService baseI18nService;

    private final FunctionPageRelationMapper functionPageRelationMapper;

    private final ContentFunctionRelationMapper contentFunctionRelationMapper;

    private final PageListMapper pageListMapper;

    /**
     * 新增 功能
     * @param functionList
     * @return
     */
    public FunctionList createFunctionList(FunctionList functionList){
        if (functionList.getId() != null){
            throw new BizException(RespCode.FUNCTION_LIST_EXIST);
        }
        if (functionList.getFunctionName() == null){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_NAME_IS_NULL);
        }
        if (functionList.getFunctionRouter() == null){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_ROUTER_IS_NULL);
        }
        if (functionList.getSequenceNumber() == null){
            throw new BizException(RespCode.FUNCTION_LIST_SEQUENCE_NUMBER_IS_NULL);
        }
        if (functionList.getApplicationId() == null){
            throw new BizException(RespCode.FUNCTION_LIST_APPLICATION_ID_IS_NULL);
        }
        if (functionListMapper.selectList(
                new EntityWrapper<FunctionList>()
                        .eq("function_name",functionList.getFunctionName())
                        .eq("function_router",functionList.getFunctionRouter())
        ).size() > 0 ){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_ROUTER_REPEAT);
        }
        functionListMapper.insert(functionList);

        //将 该功能选择的主页面pageId作为关联关系存到 功能分配页面表中
        FunctionPageRelation functionPageRelation = FunctionPageRelation.builder()
                .functionId(functionList.getId())
                .pageId(functionList.getPageId())
                .build();
        functionPageRelationMapper.insert(functionPageRelation);
        //设置主页面的functionRouter(功能路由)
        PageList pageList = pageListMapper.selectById(functionList.getPageId());
        pageList.setFunctionRouter(functionList.getFunctionRouter());
        pageListMapper.updateAllColumnById(pageList);

        return functionListMapper.selectById(functionList);
    }

    /**
     * 逻辑删除 功能
     * @param id
     */
    public void deleteFunctionListById(Long id){
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }

        //将功能页面关联关系中的数据物理删除
        List<FunctionPageRelation> functionPageRelationList = functionPageRelationMapper.selectList(
                new EntityWrapper<FunctionPageRelation>()
                        .eq("function_id",id)
        );
        if (functionPageRelationList.size() > 0) {
            throw new BizException(RespCode.FUNCTION_LIST_HAVE_BEEN_ALLOCATED_PAGES);
            /*functionPageRelationList.stream().forEach(functionPageRelation -> {
                PageList pageList = pageListMapper.selectById(functionPageRelation.getPageId());
                pageList.setFunctionRouter(null);
                pageList.setContentRouter(null);
                pageListMapper.updateAllColumnById(pageList);
            });
            functionPageRelationMapper.deleteBatchIds(functionPageRelationList);*/
        }
        //将目录功能关联关系中的数据物理删除
        List<Long> contentFunctionRelationIdList = contentFunctionRelationMapper.selectList(
                new EntityWrapper<ContentFunctionRelation>()
                        .eq("function_id",id)
        ).stream().map(ContentFunctionRelation::getId).collect(Collectors.toList());
        if (contentFunctionRelationIdList.size() > 0) {
            contentFunctionRelationMapper.deleteBatchIds(contentFunctionRelationIdList);
        }

        deleteById(functionList);
    }

    /**
     * 修改 功能
     * @param functionList
     * @return
     */
    public FunctionList updateFunctionList(FunctionList functionList){
        FunctionList oldFunctionList = functionListMapper.selectById(functionList.getId());
        if (oldFunctionList == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        //如果修改了functionRouter，那要把该功能分配的页面中的functionRouter改掉
        if ( !oldFunctionList.getFunctionRouter().equals(functionList.getFunctionRouter()) ){
            List<Long> pageIdList = functionPageRelationMapper.selectList(
                    new EntityWrapper<FunctionPageRelation>()
                            .eq("function_id",functionList.getId())
            ).stream().map(FunctionPageRelation::getPageId).collect(Collectors.toList());
            if (pageIdList.size() > 0){
                pageIdList.stream().forEach(pageId -> {
                    PageList pageList = pageListMapper.selectById(pageId);
                    pageList.setFunctionRouter(functionList.getFunctionRouter());
                    pageListMapper.updateAllColumnById(pageList);
                });
            }
        }

        //判断该功能选择的主页面id->pageId是否改变，如果改变，将原来功能分配页面关联表中的数据删除
        if ( !oldFunctionList.getPageId().equals(functionList.getPageId()) ){
            FunctionPageRelation oldFunctionPageRelation = functionPageRelationMapper.selectOne(
                    FunctionPageRelation.builder()
                            .functionId(oldFunctionList.getId())
                            .pageId(oldFunctionList.getPageId()).build()
            );
            functionPageRelationMapper.deleteById(oldFunctionPageRelation);

            functionPageRelationMapper.insert(
                    FunctionPageRelation.builder().functionId(functionList.getId()).pageId(functionList.getPageId()).build());
            //设置主页面的functionRouter(功能路由)
            PageList pageList = pageListMapper.selectById(functionList.getPageId());
            pageList.setFunctionRouter(functionList.getFunctionRouter());
            pageListMapper.updateAllColumnById(pageList);
        }

        functionListMapper.updateAllColumnById(functionList);
        return functionListMapper.selectById(functionList);
    }

    /**
     * 根据id查询 功能
     * @param id
     * @return
     */
    public FunctionList getFunctionListById(Long id){
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        return baseI18nService.selectOneTranslatedTableInfoWithI18nByEntity(functionList,FunctionList.class);
    }

    /**
     * 条件分页查询 功能
     * @param functionName
     * @param functionRouter
     * @param page
     * @return
     */
    public Page<FunctionList> getFunctionListByCond(String functionName, String functionRouter, Page page){
        Page<FunctionList> result = this.selectPage(page,
                new EntityWrapper<FunctionList>()
                        .eq("deleted",false)
                        .like(functionName != null,"function_name",functionName)
                        .like(functionRouter != null,"function_router",functionRouter)
                        .orderBy("sequence_number",true)
                        .orderBy("last_updated_date",false)
        );
        if (result.getRecords().size() > 0) {
            result.setRecords(baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result.getRecords(),FunctionList.class));
        }
        return result;
    }
}
