package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.*;
import com.hand.hcf.app.base.userRole.dto.ContentFunctionDTO;
import com.hand.hcf.app.base.userRole.persistence.ContentFunctionRelationMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class ContentFunctionRelationService extends BaseService<ContentFunctionRelationMapper,ContentFunctionRelation>{
    private final ContentFunctionRelationMapper contentFunctionRelationMapper;

    private final ContentListService contentListService;

    private final FunctionListService functionListService;

    private final PageListService pageListService;

    private final FunctionPageRelationService functionPageRelationService;

    /**
     * 批量新增 目录功能关联
     * @return
     */
    @Transactional
    public List<ContentFunctionRelation> createContentFunctionRelationBatch(List<ContentFunctionRelation> list){
        list.stream().forEach(contentFunctionRelation -> {
            //校验数据
            ContentList contentList = contentListService.selectById(contentFunctionRelation.getContentId());
            if (contentList == null){
                throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
            }
            ContentList parentContentList = null;
            if (contentList.getParentId() != null){
                parentContentList = contentListService.selectById(contentList.getParentId());
                if (parentContentList == null){
                    throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
                }
            }
            FunctionList functionList = functionListService.selectById(contentFunctionRelation.getFunctionId());
            if (functionList == null){
                throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
            }
            if (contentFunctionRelationMapper.selectList(
                    new EntityWrapper<ContentFunctionRelation>()
                            .eq("content_id",contentFunctionRelation.getContentId())
                            .eq("function_id",contentFunctionRelation.getFunctionId())
            ).size() > 0 ){
                throw new BizException(RespCode.CONTENT_FUNCTION_RELATION_EXIST);
            }
            //插入数据
            List<Long> pageIdList = functionPageRelationService.selectList(
                    new EntityWrapper<FunctionPageRelation>()
                            .eq("function_id",functionList.getId())
            ).stream().map(FunctionPageRelation::getPageId).collect(Collectors.toList());
            if (pageIdList.size() > 0){
                for (Long pageId : pageIdList) {
                    PageList pageList = pageListService.selectById(pageId);
                    if (parentContentList != null) {
                        pageList.setContentRouter(parentContentList.getContentRouter() + contentList.getContentRouter());
                    }else {
                        pageList.setContentRouter(contentList.getContentRouter());
                    }
                    pageListService.updateAllColumnById(pageList);
                }
            }
            contentFunctionRelationMapper.insert(contentFunctionRelation);
        });
        return list;
    }

    /**
     * 批量物理删除 目录功能关联
     * @param idList
     */
    @Transactional
    public void deleteContentFunctionRelationBatch(List<Long> idList){
        idList.stream().forEach(id -> {
            //校验数据
            ContentFunctionRelation contentFunctionRelation = contentFunctionRelationMapper.selectById(id);
            if (contentFunctionRelation == null){
                throw new BizException(RespCode.CONTENT_FUNCTION_RELATION_NOT_EXIST);
            }
            ContentList contentList = contentListService.selectById(contentFunctionRelation.getContentId());
            if (contentList == null){
                throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
            }
            if (contentList.getParentId() != null){
                if (contentListService.selectById(contentList.getParentId()) == null){
                    throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
                }
            }
            if (functionListService.selectById(contentFunctionRelation.getFunctionId()) == null){
                throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
            }
            //删除数据
            List<Long> pageIds = functionPageRelationService.selectList(
                    new EntityWrapper<FunctionPageRelation>()
                            .eq("function_id",contentFunctionRelation.getFunctionId())
            ).stream().map(FunctionPageRelation::getPageId).collect(Collectors.toList());
            if (pageIds.size() > 0){
                for (Long pageId : pageIds) {
                    PageList pageList = pageListService.selectById(pageId);
                    pageList.setContentRouter(null);
                    pageListService.updateAllColumnById(pageList);
                }
            }
            //关联关系改为物理删除
            contentFunctionRelationMapper.deleteById(contentFunctionRelation);
        });
    }

    /**
     * 条件查询 目录功能关联
     * @param contentId
     * @return
     */
    public List<ContentFunctionRelation> getContentFunctionRelationByCond(Long contentId){
        List<ContentFunctionRelation> result = new ArrayList<>();
        result = this.selectList(
                new EntityWrapper<ContentFunctionRelation>()
                        .eq(contentId != null,"content_id",contentId)
                        .orderBy("last_updated_date",false)
        );
        if (result.size() > 0){
            result.stream().forEach(contentFunctionRelation -> {
                contentFunctionRelation.setContentName(contentListService.selectById(contentFunctionRelation.getContentId()).getContentName());
                contentFunctionRelation.setFunctionName(functionListService.selectById(contentFunctionRelation.getFunctionId()).getFunctionName());
            });
        }
        return result;
    }

    /**
     * 过滤查询 目录功能关联
     * @param functionName
     * @param page
     * @return
     */
    public List<FunctionList> filterContentFunctionRelationByCond(String functionName,Page page){
        List<FunctionList> result = contentFunctionRelationMapper.filterContentFunctionRelationByCond( functionName, page);
        return result;
    }

    public List<ContentFunctionDTO> listContentFunctions(List<Long> roleIds){
        return baseMapper.listContentFunctions(roleIds);
    }

    public List<ContentFunctionDTO> listNotAssignFunction(List<Long> functionIds) {
        return baseMapper.listNotAssignFunction(functionIds);
    }

    /**
     * 查询角色可分配的菜单
     * @return
     */
    public List<ContentFunctionDTO> listCanAssignFunction(Long roleId) {
        return baseMapper.listCanAssignFunction(roleId);
    }
}
