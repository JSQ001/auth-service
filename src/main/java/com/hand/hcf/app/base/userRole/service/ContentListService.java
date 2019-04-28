package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.app.base.userRole.domain.ContentFunctionRelation;
import com.hand.hcf.app.base.userRole.domain.ContentList;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.persistence.ContentListMapper;
import com.hand.hcf.app.base.userRole.persistence.FunctionPageRelationMapper;
import com.hand.hcf.app.base.userRole.persistence.PageListMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import ma.glasnost.orika.MapperFacade;
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
public class ContentListService extends BaseService<ContentListMapper, ContentList> {
    @Autowired
    private ContentListMapper contentListMapper;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private TenantService tenantService;

    @Autowired
    private ContentFunctionRelationService contentFunctionRelationService;
    @Autowired
    private FunctionPageRelationMapper functionPageRelationMapper;
    @Autowired
    private PageListMapper pageListMapper;
    @Autowired
    private MapperFacade mapperFacade;

    /**
     * 新增 目录
     *
     * @param contentList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ContentList createContentList(ContentList contentList) {
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (contentList.getId() != null) {
            throw new BizException(RespCode.CONTENT_LIST_EXIST);
        }
        if (contentList.getContentName() == null) {
            throw new BizException(RespCode.CONTENT_LIST_CONTENT_NAME_IS_NULL);
        }
        if (contentList.getIcon() == null) {
            throw new BizException(RespCode.CONTENT_LIST_ICON_IS_NULL);
        }
        if (contentList.getParentId() != null) {
            ContentList parentContent = contentListMapper.selectById(contentList.getParentId());
            if (parentContent == null) {
                throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
            }
            parentContent.setHasSonContent(true);
            contentListMapper.updateAllColumnById(parentContent);
        }
        if (contentList.getSequenceNumber() == null) {
            throw new BizException(RespCode.CONTENT_LIST_SEQUENCE_NUMBER_IS_NULL);
        }
        contentList.setTenantId(tenantId);
        if (contentListMapper.selectList(
                new EntityWrapper<ContentList>()
                        .eq("content_name", contentList.getContentName())
                        .eq("tenant_id", contentList.getTenantId())
        ).size() > 0) {
            throw new BizException(RespCode.CONTENT_LIST_CONTENT_ROUTER_REPEAT);
        }
        this.insert(contentList);
        // 如果是系统租户，则将菜单分配给其他的租户
        if (tenant.getSystemFlag()) {
            Long sourceId = contentList.getId();
            List<Tenant> tenants = tenantService.findAll()
                    .stream()
                    .filter(e -> !e.getId().equals(tenantId)).collect(Collectors.toList());
            if (tenants.size() > 0) {
                List<ContentList> contentLists = tenants.stream().map(domain -> {
                    ContentList contentList1 = mapperFacade.map(contentList, ContentList.class);
                    contentList1.setId(null);
                    contentList1.setTenantId(domain.getId());
                    contentList1.setSourceId(sourceId);
                    return contentList1;
                }).collect(Collectors.toList());
                this.insertBatch(contentLists);
            }
        }
        return contentList;
    }

    /**
     * 逻辑删除 目录
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContentListById(Long id) {
        ContentList contentList = contentListMapper.selectById(id);
        if (contentList == null) {
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }

        //判断是否有父目录
        if (contentList.getParentId() != null) {
            ContentList parentContent = contentListMapper.selectById(contentList.getParentId());
            if (parentContent == null) {
                throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
            }
            //集合 <=1 说明该父目录下只有这一个子目录，则直接把父目录的hasSonContent字段改为false
            if (contentListMapper.selectList(
                    new EntityWrapper<ContentList>()
                            .eq("deleted", false)
                            .eq("parent_id", parentContent.getId())
            ).size() <= 1) {
                parentContent.setHasSonContent(false);
                contentListMapper.updateAllColumnById(parentContent);
            }
        }

        //将目录功能关联关系中的数据物理删除
        List<ContentFunctionRelation> contentFunctionRelationList = contentFunctionRelationService.selectList(
                new EntityWrapper<ContentFunctionRelation>()
                        .eq("content_id", id)
        );
        if (contentFunctionRelationList.size() > 0) {
            //将功能分配页面的目录router设置为null
            for (ContentFunctionRelation contentFunctionRelation : contentFunctionRelationList) {
                List<Long> pageIdList = functionPageRelationMapper.selectList(
                        new EntityWrapper<FunctionPageRelation>()
                                .eq("function_id", contentFunctionRelation.getFunctionId())
                ).stream().map(FunctionPageRelation::getPageId).collect(Collectors.toList());
                if (pageIdList.size() > 0) {
                    pageIdList.stream().forEach(pageId -> {
                        PageList pageList = pageListMapper.selectById(pageId);
                        pageList.setContentRouter(null);
                        pageListMapper.updateAllColumnById(pageList);
                    });
                }
            }
            contentFunctionRelationService.deleteBatchIds(contentFunctionRelationList);
        }
        deleteById(contentList);
        Long tenantId = contentList.getTenantId();
        Tenant tenant = tenantService.selectById(tenantId);
        //如果是系统租户则删除所有租户下的功能及目录关联关系表
        if(tenant.getSystemFlag()){
            List<Long> contentIds = contentListMapper.selectList(
                    new EntityWrapper<ContentList>()
                            .eq("source_id", id)
            ).stream().map(ContentList::getId).collect(Collectors.toList());
            if(contentIds.size() > 0) {
                contentIds.stream().forEach(contentId -> {
                    List<Long> ids = contentFunctionRelationService.selectList(
                            new EntityWrapper<ContentFunctionRelation>()
                                    .eq("content_id", contentId)
                    ).stream().map(ContentFunctionRelation::getId).collect(Collectors.toList());
                    if (ids.size() > 0) {
                        contentFunctionRelationService.deleteBatchIds(ids);
                    }
                });
                this.deleteBatchIds(contentIds);
            }
        }
    }

    /**
     * 修改 目录
     *
     * @param contentList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ContentList updateContentList(ContentList contentList) {
        ContentList oldContentList = contentListMapper.selectById(contentList.getId());
        if (oldContentList == null) {
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }

        //处理hasSonContent字段
        //1:之前没有父目录，现在有了
        if (oldContentList.getParentId() == null && contentList.getParentId() != null) {
            ContentList parentContent = contentListMapper.selectById(contentList.getParentId());
            if (parentContent == null) {
                throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
            }
            parentContent.setHasSonContent(true);
            contentListMapper.updateAllColumnById(parentContent);
        }
        //2:之前有父目录，现在换了
        if (oldContentList.getParentId() != null && contentList.getParentId() != null) {
            if (!oldContentList.getParentId().equals(contentList.getParentId())) {
                //判断contentList的父目录hasSonContent字段是否为true
                ContentList nowParentContentList = contentListMapper.selectById(contentList.getParentId());
                if (nowParentContentList == null) {
                    throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
                }
                if (nowParentContentList.getHasSonContent() == false || nowParentContentList.getHasSonContent() == null) {
                    nowParentContentList.setHasSonContent(true);
                    contentListMapper.updateAllColumnById(nowParentContentList);
                }
                //判断oldContentList的父目录是否还有其他子目录
                ContentList oldParentContentList = contentListMapper.selectById(oldContentList.getParentId());
                if (contentListMapper.selectList(
                        new EntityWrapper<ContentList>()
                                .eq("deleted", false)
                                .eq("parent_id", oldParentContentList.getId())
                ).size() <= 1) {
                    oldParentContentList.setHasSonContent(false);
                    contentListMapper.updateAllColumnById(oldParentContentList);
                }
            }
        }
        contentListMapper.updateAllColumnById(contentList);
        return contentListMapper.selectById(contentList);
    }

    /**
     * 根据id查询 目录
     *
     * @param id
     * @return
     */
    public ContentList getContentListById(Long id) {
        ContentList contentList = contentListMapper.selectById(id);
        if (contentList == null) {
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }
        return baseI18nService.selectOneTranslatedTableInfoWithI18nByEntity(contentList, ContentList.class);
    }

    /**
     * 条件分页查询 目录
     *
     * @param contentName
     * @param contentRouter
     * @param page
     * @return
     */
    public Page<ContentList> getContentListByCond(String contentName, String contentRouter, Page page) {
        Page<ContentList> result = this.selectPage(page,
                new EntityWrapper<ContentList>()
                        .eq("deleted", false)
                        .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
                        .like(contentName != null, "content_name", contentName)
                        .like(contentRouter != null, "content_router", contentRouter)
                        .isNull("parent_id")
                        .orderBy("sequence_number", true)
                        .orderBy("last_updated_date", false)
        );
        if (result.getRecords().size() > 0) {
            result.setRecords(baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result.getRecords(), ContentList.class));
        }
        return result;
    }

    /**
     * 查询某个目录的子目录
     *
     * @param id
     * @return
     */
    public List<ContentList> getSonContent(Long id) {
        List<ContentList> result = contentListMapper.selectList(
                new EntityWrapper<ContentList>()
                        .eq("deleted", false)
                        .eq("parent_id", id)
                        .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
        );
        return result;
    }


    /**
     * 初始化租户目录
     *
     * @param tenantId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void initTenantContent(Long tenantId) {
        List<ContentList> contentLists = this.selectList(
                new EntityWrapper<ContentList>()
                        .eq("deleted", false)
                        .eq("tenant_id", tenantService.getSystemTenantId())
        );

        if (!contentLists.isEmpty()) {
            contentLists.forEach(v -> {
                v.setTenantId(tenantId);
                v.setSourceId(v.getId());
                v.setId(null);
            });

            this.insertBatch(contentLists);

            List<ContentFunctionRelation> functionPageRelations = contentFunctionRelationService.listRelationByTenant(tenantId);

            contentFunctionRelationService.insertBatch(functionPageRelations);
        }
    }

    /**
     * 根据来源id和租户id获得目录
     * @param tenantId
     * @param sourceId
     * @return
     */
    public ContentList getContentListByTenantIdAndSourceId(Long tenantId, Long sourceId) {
        return this.selectOne(new EntityWrapper<ContentList>()
                .eq("source_id", sourceId)
                .eq("tenant_id", tenantId));
    }
}
