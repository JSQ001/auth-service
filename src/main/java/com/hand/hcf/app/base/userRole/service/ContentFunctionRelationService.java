package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.app.base.userRole.domain.*;
import com.hand.hcf.app.base.userRole.dto.ContentFunctionDTO;
import com.hand.hcf.app.base.userRole.persistence.ContentFunctionRelationMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ContentFunctionRelationService extends BaseService<ContentFunctionRelationMapper,ContentFunctionRelation>{
    @Autowired
    private  ContentFunctionRelationMapper contentFunctionRelationMapper;

    @Autowired
    private  ContentListService contentListService;
    @Autowired
    private  FunctionListService functionListService;
    @Autowired
    private  PageListService pageListService;
    @Autowired
    private  FunctionPageRelationService functionPageRelationService;

    @Autowired
    private TenantService tenantService;

    /**
     * 批量新增 目录功能关联
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ContentFunctionRelation> createContentFunctionRelationBatch(List<ContentFunctionRelation> list){
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        Tenant tenant = tenantService.getTenantById(tenantId);
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

            contentFunctionRelationMapper.insert(contentFunctionRelation);

            // 如果是系统租户则需要将目录关联功能映射关系分配给所有租户
            if(tenant.getSystemFlag()){
                List<ContentFunctionRelation> relations = baseMapper.listBySystemTenant(tenant.getId(),
                        contentFunctionRelation.getFunctionId(), contentFunctionRelation.getContentId());
                if (CollectionUtils.isNotEmpty(relations)) {
                    this.insertBatch(relations);
                }
            }


        });
        return list;
    }

    /**
     * 批量物理删除 目录功能关联
     * @param idList
     */
    @Transactional(rollbackFor = Exception.class)
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

            Long tenantId = contentList.getTenantId();
            Tenant tenant = tenantService.selectById(tenantId);
            if(tenant.getSystemFlag()){
                List<Long> functionIds = functionListService.selectList(
                        new EntityWrapper<FunctionList>()
                                .eq("source_id", contentFunctionRelation.getFunctionId())
                ).stream().map(FunctionList::getId).collect(Collectors.toList());
                if(functionIds.size() > 0) {
                    functionIds.stream().forEach(functionId -> {
                        List<Long> ids = contentFunctionRelationMapper.selectList(
                                new EntityWrapper<ContentFunctionRelation>()
                                        .eq("function_id", functionId)
                        ).stream().map(ContentFunctionRelation::getId).collect(Collectors.toList());
                        if (ids.size() > 0) {
                            this.deleteBatchIds(ids);
                        }
                    });
                }
            }
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
        List<FunctionList> result = contentFunctionRelationMapper.filterContentFunctionRelationByCond( functionName, LoginInformationUtil.getCurrentTenantId(), page);
        return result;
    }

    public List<ContentFunctionDTO> listContentFunctions(List<Long> roleIds){
        return baseMapper.listContentFunctions(roleIds, LoginInformationUtil.getCurrentTenantId());
    }



    /**
     * 查询角色可分配的菜单
     * @return
     */
    public List<ContentFunctionDTO> listCanAssignFunction(Long roleId) {
        return baseMapper.listCanAssignFunction(roleId, LoginInformationUtil.getCurrentTenantId());
    }


    /**
     * 条件查询 获取租户来源目录功能关联关系
     * @param tenantId
     * @return
     */
    public List<ContentFunctionRelation> listRelationByTenant(Long tenantId){
        return baseMapper.listRelationByTenant(tenantId);
    }
}
