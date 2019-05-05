package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.app.base.userRole.domain.*;
import com.hand.hcf.app.base.userRole.persistence.ContentFunctionRelationMapper;
import com.hand.hcf.app.base.userRole.persistence.FunctionPageRelationMapper;
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
@Transactional
public class FunctionPageRelationService extends BaseService<FunctionPageRelationMapper,FunctionPageRelation>{
    @Autowired
    private  FunctionPageRelationMapper functionPageRelationMapper;
    @Autowired
    private  PageListService pageListService;
    @Autowired
    private  FunctionListService functionListService;
    @Autowired
    private  ContentListService contentListService;
    @Autowired
    private  ContentFunctionRelationMapper contentFunctionRelationMapper;
    @Autowired
    private TenantService tenantService;
    /**
     * 批量新增 功能页面关联
     * @return
     */
    @Transactional
    public List<FunctionPageRelation> createFunctionPageRelationBatch(List<FunctionPageRelation> list){
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        Tenant tenant = tenantService.getTenantById(tenantId);
        list.forEach(functionPageRelation -> {
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
            //设置 功能Router
            pageList.setFunctionRouter(functionList.getFunctionRouter());
            //设置 目录Router
            List<ContentFunctionRelation> contentFunctionRelationList = contentFunctionRelationMapper.selectList(
                    new EntityWrapper<ContentFunctionRelation>()
                            .eq("function_id",functionList.getId())
            );
            if (contentFunctionRelationList.size() > 0){
                ContentFunctionRelation contentFunctionRelation = contentFunctionRelationList.get(0);
                ContentList contentList = contentListService.selectById(contentFunctionRelation.getContentId());
                if (contentList == null){
                    throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
                }
            }
            this.insert(functionPageRelation);
            // 如果是系统租户则需要将目录关联功能映射关系分配给所有租户
            if(tenant.getSystemFlag()){
                List<FunctionList> functions = functionListService.listOtherTenantFunction(tenantId,
                        functionPageRelation.getFunctionId());
                if (CollectionUtils.isNotEmpty(functions)) {
                    List<FunctionPageRelation> functionPageRelations = functions.stream().map(domain -> {
                        FunctionPageRelation functionPageRelation1 = new FunctionPageRelation();
                        functionPageRelation1.setPageId(pageList.getId());
                        functionPageRelation1.setFunctionId(domain.getId());
                        return functionPageRelation1;
                    }).collect(Collectors.toList());
                    this.insertBatch(functionPageRelations);
                }
            }
        });
        return list;
    }

    /**
     * 批量物理删除 功能页面关联
     * @param idList
     */
    @Transactional(rollbackFor = Exception.class)
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
            pageList.setContentRouter(null);
            pageListService.updateAllColumnById(pageList);
            //关联表里的改为物理删除
            functionPageRelationMapper.deleteById(functionPageRelation);

            FunctionList functionList = functionListService.selectById(functionPageRelation.getFunctionId());
            Long tenantId = functionList.getTenantId();
            Tenant tenant = tenantService.selectById(tenantId);
            if(tenant.getSystemFlag()){
                List<Long> functionIds = functionListService.selectList(
                        new EntityWrapper<FunctionList>()
                                .eq("source_id", functionList.getId())
                ).stream().map(FunctionList::getId).collect(Collectors.toList());
                if(functionIds.size() > 0) {
                    functionIds.forEach(functionId -> {
                        List<Long> ids = functionPageRelationMapper.selectList(
                                new EntityWrapper<FunctionPageRelation>()
                                        .eq("function_id", functionId)
                                        .eq("page_id",functionPageRelation.getPageId())
                        ).stream().map(FunctionPageRelation::getId).collect(Collectors.toList());
                        if (ids.size() > 0) {
                            this.deleteBatchIds(ids);
                        }
                    });
                }
            }
        });
    }

    /**
     * 条件查询 功能页面关联
     * @param functionId
     * @return
     */
    public List<FunctionPageRelation> getFunctionPageRelationByCond(Long functionId){
        List<FunctionPageRelation> result = new ArrayList<>();
        result = this.selectList(
                new EntityWrapper<FunctionPageRelation>()
                        .eq(functionId != null,"function_id",functionId)
                        .orderBy("last_updated_date",false)
        );
        if (result.size() > 0){
            result.forEach(functionPageRelation -> {
                functionPageRelation.setFunctionName(functionListService.selectById(functionPageRelation.getFunctionId()).getFunctionName());
                functionPageRelation.setPageName(pageListService.selectById(functionPageRelation.getPageId()).getPageName());
            });
        }
        return result;
    }

    /**
     * 条件查询 获取租户来源功能页面关联关系
     * @param tenantId
     * @return
     */
    public List<FunctionPageRelation> listRelationByTenant(Long tenantId){
      return baseMapper.listRelationByTenant(tenantId);
    }

    /**
     * 过滤查询 功能页面关联
     * @param pageName
     * @param page
     * @return
     */
    public List<PageList> filterFunctionPageRelationByCond(String pageName,Page page){
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        List<PageList> result = functionPageRelationMapper.filterFunctionPageRelationByCond( pageName, tenantId, page);
        return result;
    }
}
