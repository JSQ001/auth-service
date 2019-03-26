package com.hand.hcf.app.mdata.dimension.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemGroup;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemGroupAssignItem;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemGroupMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.TypeConversionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DimensionItemGroupService extends BaseService<DimensionItemGroupMapper, DimensionItemGroup> {

    private final Logger log = LoggerFactory.getLogger(DimensionItemGroupService.class);

    @Autowired
    private DimensionItemGroupMapper dimensionItemGroupMapper;

    @Autowired
    private DimensionItemGroupAssignItemService assignItemService;

    @Autowired
    private DimensionItemService dimensionItemService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private BaseI18nService baseI18nService;

    /**
     * 新建维值组
     * @param dimensionItemGroup
     * @return
     */
    @Transactional
    public DimensionItemGroup insertDimensionItemGroup(DimensionItemGroup dimensionItemGroup) {
        log.debug("REST request to save dimensionItemGroup : {}", dimensionItemGroup);
        if (dimensionService.selectById(dimensionItemGroup.getDimensionId()) == null) {
            throw new BizException(RespCode.DIMENSION_NOT_EXIST);
        }
        if (TypeConversionUtils.isNotEmpty(dimensionItemGroup.getId())){
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        check(dimensionItemGroup);
        dimensionItemGroupMapper.insert(dimensionItemGroup);

        return dimensionItemGroup;
    }

    /**
     *  更新维值组
     * @param dimensionItemGroup
     * @return
     */
    @Transactional
    public DimensionItemGroup updateDimensionItemGroup(DimensionItemGroup dimensionItemGroup) {
        log.debug("REST request to update dimensionItemGroup : {}", dimensionItemGroup);
        if (TypeConversionUtils.isEmpty(dimensionItemGroup.getId())){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        check(dimensionItemGroup);
        dimensionItemGroupMapper.updateById(dimensionItemGroup);
        return dimensionItemGroup;
    }

    //插入、更新时字段检验
    private void check(DimensionItemGroup dimensionItemGroup){
        List<DimensionItemGroup> dimensionList = dimensionItemGroupMapper.selectList(
                new EntityWrapper<DimensionItemGroup>()
                        .eq("dimension_id", dimensionItemGroup.getDimensionId())
                        .ne(TypeConversionUtils.isNotEmpty(dimensionItemGroup.getId()), "id", dimensionItemGroup.getId())
        );
        //维值组编码重复性校验
        if (TypeConversionUtils.isNotEmpty(dimensionItemGroup.getDimensionItemGroupCode())) {
            if (dimensionList
                    .stream()
                    .filter(d -> d.getDimensionItemGroupCode().equals(dimensionItemGroup.getDimensionItemGroupCode()))
                    .count() > 0) {
                throw  new BizException(RespCode.DIMENSION_ITEM_GROUP_CODE_REPEAT);
            }
        }
        //维值组名称重复性校验
        if (TypeConversionUtils.isNotEmpty(dimensionItemGroup.getDimensionItemGroupName())) {
            if (dimensionList
                    .stream()
                    .filter(d -> d.getDimensionItemGroupName().equals(dimensionItemGroup.getDimensionItemGroupName()))
                    .count() > 0) {
                throw new BizException(RespCode.DIMENSION_ITEM_GROUP_NAME_REPEAT);
            }
        }
    }

    /**
     * 根据维度ID和维值组维值组代码查询维值组
     * @param dimensionId
     * @param dimensionItemGroupCode
     * @param page
     * @return
     */
    public List<DimensionItemGroup> pageDimensionItemGroupsByDimensionIdAndCond(Long dimensionId, String dimensionItemGroupCode, Page page) {
        List<DimensionItemGroup> dimensionItemGroupList = dimensionItemGroupMapper.selectPage(
                page,
                new EntityWrapper<DimensionItemGroup>()
                        .eq("dimension_id", dimensionId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemGroupCode), "dimension_item_group_code",dimensionItemGroupCode)
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_group_code")
        );
        dimensionItemGroupList.stream().forEach(e -> {
            e.setHasChildren(this.hasSubDimensionItems(e.getId()));
        });
        return dimensionItemGroupList;
    }

    /**
     * 根据维值组ID批量新增子维值
     * @param dimensionItemGroupId
     * @param dimensionItemIds
     * @return
     */
    public List<DimensionItemGroupAssignItem> insertSubDimensionItemBatch(Long dimensionItemGroupId, List<Long> dimensionItemIds) {
        List<DimensionItemGroupAssignItem> assignItems = new ArrayList<>();
        dimensionItemIds.stream().forEach(id -> {
            DimensionItemGroupAssignItem item = new DimensionItemGroupAssignItem();
            item.setDimensionItemGroupId(dimensionItemGroupId);
            item.setDimensionItemId(id);
            assignItems.add(item);
        });
        assignItemService.insertBatch(assignItems);
        return assignItems;
    }

    /**
     * 根据维值组ID批量删除子维值
     * @param dimensionItemGroupId
     * @param dimensionItemIds
     */
    @Transactional
    public void deleteSubDimensionItemBatch(Long dimensionItemGroupId, List<Long> dimensionItemIds) {
        if (dimensionItemIds != null && dimensionItemIds.size() > 0) {
            assignItemService.delete(
                    new EntityWrapper<DimensionItemGroupAssignItem>()
                            .eq("dimension_item_group_id", dimensionItemGroupId)
                            .in(dimensionItemIds != null, "dimension_item_id", dimensionItemIds)
            );
        }
    }

    /**
     * 删除维值组
     * @param dimensionItemGroupId
     */
    @Transactional
    public void deleteDimensionItemGroupById(Long dimensionItemGroupId) {
        DimensionItemGroup dimensionItemGroup = dimensionItemGroupMapper.selectById(dimensionItemGroupId);
        if (dimensionItemGroup == null) {
            return;
        }
        dimensionItemGroup.setDeleted(true);
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        dimensionItemGroup.setDimensionItemGroupCode(dimensionItemGroup.getDimensionItemGroupCode() + "_DELETED_" + randomNumeric);
        dimensionItemGroup.setDimensionItemGroupName(dimensionItemGroup.getDimensionItemGroupName() + "_DELETED_" + randomNumeric);
        this.updateById(dimensionItemGroup);

        this.deleteSubDimensionItemBatch(dimensionItemGroupId, null);
    }

    /**
     * 批量删除维值组
     * @param dimensionItemGroupIds
     */
    @Transactional
    public void deleteDimensionItemGroupBatch(List<Long> dimensionItemGroupIds) {
        dimensionItemGroupIds.stream().forEach(this::deleteDimensionItemGroupById);
    }

    /**
     * 根据维值组ID和维值ID删除关联
     * @param dimensionItemGroupId
     * @param dimensionItemId
     */
    public void deleteSubDimensionItem(Long dimensionItemGroupId, Long dimensionItemId) {
        assignItemService.delete(
                new EntityWrapper<DimensionItemGroupAssignItem>()
                        .eq("dimension_item_group_id", dimensionItemGroupId)
                        .eq("dimension_item_id", dimensionItemId)
        );
    }

    /**
     * 子维值查询
     * @param dimensionItemGroupId
     * @param dimensionItemCode
     * @param queryPage
     * @return
     */
    public List<DimensionItem> pageDimensionItemByCond(Long dimensionItemGroupId, String dimensionItemCode, Page queryPage) {
        List<Long> dimensionItemIds = assignItemService.selectList(
                new EntityWrapper<DimensionItemGroupAssignItem>()
                        .eq("dimension_item_group_id",dimensionItemGroupId)
        ).stream().map(DimensionItemGroupAssignItem::getDimensionItemId).collect(Collectors.toList());
        List<DimensionItem> dimensionItemList = dimensionItemService.pageDimensionItemsByIdsAndCond(dimensionItemIds, this.selectById(dimensionItemGroupId).getDimensionId(), dimensionItemCode, queryPage);
        return  dimensionItemList;
    }

    /**
     * 分配页面的维值筛选查询
     * @param dimensionItemGroupId
     * @param dimensionItemCode
     * @param dimensionItemName
     * @param enabled
     * @param queryPage
     * @return
     */
    public List<DimensionItem> pageDimensionItemByCond(Long dimensionItemGroupId, String dimensionItemCode, String dimensionItemName, Boolean enabled, Page queryPage) {
        List<Long> dimensionItemIds = assignItemService.selectList(
                new EntityWrapper<DimensionItemGroupAssignItem>()
                        .eq("dimension_item_group_id",dimensionItemGroupId)
        ).stream().map(DimensionItemGroupAssignItem::getDimensionItemId).collect(Collectors.toList());
        List<DimensionItem> dimensionItemList = dimensionItemService.pageDimensionItemsByIdsAndCond(dimensionItemIds, this.selectById(dimensionItemGroupId).getDimensionId(), dimensionItemCode, dimensionItemName, enabled, queryPage);
        return  dimensionItemList;
    }

    /**
     * 根据维度id删除维值组
     * @param dimensionId
     */
    public void deleteByDimensionId(Long dimensionId) {
        List<DimensionItemGroup> dimensionItemGroups = dimensionItemGroupMapper.selectList(
                new EntityWrapper<DimensionItemGroup>().eq("dimension_id",dimensionId)
        );
        if (dimensionItemGroups.size() > 0) {
            assignItemService.delete(
                    new EntityWrapper<DimensionItemGroupAssignItem>().in("dimension_item_group_id", dimensionItemGroups.stream().map(DimensionItemGroup::getId).collect(Collectors.toList()))
            );
        }
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        dimensionItemGroups.stream().forEach(e -> {
            e.setDeleted(true);
            e.setDimensionItemGroupCode(e.getDimensionItemGroupCode() + "_DELETED_" + randomNumeric);
            e.setDimensionItemGroupName(e.getDimensionItemGroupName() + "_DELETED_" + randomNumeric);
            this.updateById(e);
        });
    }

    /**
     * 根据id查询维值组详情
     * @param dimensionItemGroupId
     * @return
     */
    public DimensionItemGroup getDimensionItemGroupById(Long dimensionItemGroupId) {
        DimensionItemGroup dimensionItemGroup = dimensionItemGroupMapper.selectById(dimensionItemGroupId);
        if (dimensionItemGroup == null) {
            return dimensionItemGroup;
        }
        dimensionItemGroup.setI18n(baseI18nService.getI18nMap(DimensionItemGroup.class, dimensionItemGroup.getId()));
        return dimensionItemGroup;
    }

    private Boolean hasSubDimensionItems(Long dimensionItemGroupId) {
        List<DimensionItemGroupAssignItem> groupAssignItemList = assignItemService.selectList(
                new EntityWrapper<DimensionItemGroupAssignItem>().eq("dimension_item_group_id", dimensionItemGroupId)
        );
        if (groupAssignItemList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}

