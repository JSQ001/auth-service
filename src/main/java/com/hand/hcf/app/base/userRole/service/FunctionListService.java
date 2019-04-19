package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.app.base.userRole.domain.ContentFunctionRelation;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.persistence.ContentFunctionRelationMapper;
import com.hand.hcf.app.base.userRole.persistence.FunctionListMapper;
import com.hand.hcf.app.base.userRole.persistence.PageListMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class FunctionListService extends BaseService<FunctionListMapper, FunctionList> {
    @Autowired
    private  FunctionListMapper functionListMapper;
    @Autowired
    private  TenantService tenantService;
    @Autowired
    private  BaseI18nService baseI18nService;

    @Autowired
    private  FunctionPageRelationService functionPageRelationService;
    @Autowired
    private  ContentFunctionRelationMapper contentFunctionRelationMapper;
    @Autowired
    private  PageListMapper pageListMapper;


    /**
     * 新增 功能
     *
     * @param functionList
     * @return
     */
    public FunctionList createFunctionList(FunctionList functionList) {
        if (functionList.getId() != null) {
            throw new BizException(RespCode.FUNCTION_LIST_EXIST);
        }
        if (functionList.getFunctionName() == null) {
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_NAME_IS_NULL);
        }
        if (functionList.getSequenceNumber() == null) {
            throw new BizException(RespCode.FUNCTION_LIST_SEQUENCE_NUMBER_IS_NULL);
        }
        if (functionList.getApplicationId() == null) {
            throw new BizException(RespCode.FUNCTION_LIST_APPLICATION_ID_IS_NULL);
        }
        functionList.setTenantId(LoginInformationUtil.getCurrentTenantId());
        if (functionListMapper.selectList(
                new EntityWrapper<FunctionList>()
                        .eq("function_name", functionList.getFunctionName())
                        .eq("tenant_id", functionList.getTenantId()
                        )
        ).size() > 0) {
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_ROUTER_REPEAT);
        }
        functionListMapper.insert(functionList);

        //将 该功能选择的主页面pageId作为关联关系存到 功能分配页面表中
        FunctionPageRelation functionPageRelation = FunctionPageRelation.builder()
                .functionId(functionList.getId())
                .pageId(functionList.getPageId())
                .build();
        functionPageRelationService.insert(functionPageRelation);
        //设置主页面的functionRouter(功能路由)
        PageList pageList = pageListMapper.selectById(functionList.getPageId());
        pageList.setFunctionRouter(functionList.getFunctionRouter());
        pageListMapper.updateAllColumnById(pageList);

        return functionListMapper.selectById(functionList);
    }

    /**
     * 逻辑删除 功能
     *
     * @param id
     */
    public void deleteFunctionListById(Long id) {
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null) {
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }

        //将功能页面关联关系中的数据物理删除
        List<FunctionPageRelation> functionPageRelationList = functionPageRelationService.selectList(
                new EntityWrapper<FunctionPageRelation>()
                        .eq("function_id", id)
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
                        .eq("function_id", id)
        ).stream().map(ContentFunctionRelation::getId).collect(Collectors.toList());
        if (contentFunctionRelationIdList.size() > 0) {
            contentFunctionRelationMapper.deleteBatchIds(contentFunctionRelationIdList);
        }

        deleteById(functionList);
    }

    /**
     * 修改 功能
     *
     * @param functionList
     * @return
     */
    public FunctionList updateFunctionList(FunctionList functionList) {
        FunctionList oldFunctionList = functionListMapper.selectById(functionList.getId());
        if (oldFunctionList == null) {
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        //判断该功能选择的主页面id->pageId是否改变，如果改变，将原来功能分配页面关联表中的数据删除
        if (!oldFunctionList.getPageId().equals(functionList.getPageId())) {
            FunctionPageRelation oldFunctionPageRelation = functionPageRelationService.selectOne(
                    new EntityWrapper<FunctionPageRelation>()
                            .eq("function_id", oldFunctionList.getId())
                            .eq("page_id", oldFunctionList.getPageId())
            );
            functionPageRelationService.deleteById(oldFunctionPageRelation);

            functionPageRelationService.insert(
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
     *
     * @param id
     * @return
     */
    public FunctionList getFunctionListById(Long id) {
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null) {
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        return baseI18nService.selectOneTranslatedTableInfoWithI18nByEntity(functionList, FunctionList.class);
    }

    /**
     * 条件分页查询 功能
     *
     * @param functionName
     * @param functionRouter
     * @param page
     * @return
     */
    public Page<FunctionList> getFunctionListByCond(String functionName, String functionRouter, Page page) {
        Page<FunctionList> result = this.selectPage(page,
                new EntityWrapper<FunctionList>()
                        .eq("deleted", false)
                        .eq("tenant_id",LoginInformationUtil.getCurrentTenantId())
                        .like(functionName != null, "function_name", functionName)
                        .like(functionRouter != null, "function_router", functionRouter)
                        .orderBy("sequence_number", true)
                        .orderBy("last_updated_date", false)
        );
        if (result.getRecords().size() > 0) {
            result.setRecords(baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result.getRecords(), FunctionList.class));
        }
        return result;
    }

    /**
     * 初始化租户功能
     *
     * @param tenantId
     * @return
     */
    public void initTenantFunction(Long tenantId) {
        List<FunctionList> functionLists = this.selectList(
                new EntityWrapper<FunctionList>()
                        .eq("deleted", false)
                        .eq("tenant_id", tenantService.getSystemTenantId())
        );

        if (!functionLists.isEmpty()) {
            functionLists.forEach(v -> {
                v.setTenantId(tenantId);
                v.setSourceId(v.getId());
                v.setId(null);
            });

            this.insertBatch(functionLists);

            List<FunctionPageRelation> functionPageRelations = functionPageRelationService.listRelationByTenant(tenantId);

            functionPageRelationService.insertBatch(functionPageRelations);
        }
    }
}
