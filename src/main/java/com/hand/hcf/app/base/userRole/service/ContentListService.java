package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.ContentList;
import com.hand.hcf.app.base.userRole.persistence.ContentListMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Service
@AllArgsConstructor
@Transactional
public class ContentListService extends BaseService<ContentListMapper,ContentList>{
    private final ContentListMapper contentListMapper;

    private final BaseI18nService baseI18nService;

    /**
     * 新增 目录
     * @param contentList
     * @return
     */
    public ContentList createContentList(ContentList contentList){
        if (contentList.getId() != null){
            throw new BizException(RespCode.CONTENT_LIST_EXIST);
        }
        if (contentList.getContentName() == null){
            throw new BizException(RespCode.CONTENT_LIST_CONTENT_NAME_IS_NULL);
        }
        if (contentList.getContentRouter() == null){
            throw new BizException(RespCode.CONTENT_LIST_CONTENT_ROUTER_IS_NULL);
        }
        if (contentList.getIcon() == null){
            throw new BizException(RespCode.CONTENT_LIST_ICON_IS_NULL);
        }
        if (contentList.getParentId() != null){
            if (contentListMapper.selectById(contentList.getParentId()) == null){
                throw new BizException(RespCode.CONTENT_LIST_PARENT_CONTENT_NOT_EXIST);
            }
        }
        if (contentList.getSequenceNumber() == null){
            throw new BizException(RespCode.CONTENT_LIST_SEQUENCE_NUMBER_IS_NULL);
        }
        if (contentListMapper.selectList(
                new EntityWrapper<ContentList>()
                        .eq("content_name",contentList.getContentName())
                        .eq("content_router",contentList.getContentRouter())
        ).size() > 0 ){
            throw new BizException(RespCode.CONTENT_LIST_CONTENT_ROUTER_REPEAT);
        }
        contentListMapper.insert(contentList);
        return contentListMapper.selectById(contentList);
    }

    /**
     * 逻辑删除 目录
     * @param id
     */
    public void deleteContentListById(Long id){
        ContentList contentList = contentListMapper.selectById(id);
        if (contentList == null){
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }
        contentList.setDeleted(true);
        contentListMapper.updateById(contentList);
    }

    /**
     * 修改 目录
     * @param contentList
     * @return
     */
    public ContentList updateContentList(ContentList contentList){
        if (contentList.getId() == null){
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }
        contentListMapper.updateById(contentList);
        return contentListMapper.selectById(contentList);
    }

    /**
     * 根据id查询 目录
     * @param id
     * @return
     */
    public ContentList getContentListById(Long id){
        ContentList contentList = contentListMapper.selectById(id);
        if (contentList == null){
            throw new BizException(RespCode.CONTENT_LIST_NOT_EXIST);
        }
        return baseI18nService.selectOneTranslatedTableInfoWithI18nByEntity(contentList,ContentList.class);
    }

    /**
     * 条件分页查询 目录
     * @param contentName
     * @param contentRouter
     * @param page
     * @return
     */
    public Page<ContentList> getContentListByCond(String contentName, String contentRouter, Page page){
        Page<ContentList> result = this.selectPage(page,
                new EntityWrapper<ContentList>()
                        .eq("deleted",false)
                        .like(contentName != null,"content_name",contentName)
                        .like(contentRouter != null,"content_router",contentRouter)
                        .orderBy("sequence_number",true)
                        .orderBy("last_updated_date",false)
        );
        if (result.getRecords().size() > 0) {
            result.setRecords(baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result.getRecords(),ContentList.class));
        }
        return result;
    }
}
