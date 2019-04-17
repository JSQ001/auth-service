package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.common.co.DimensionDetailCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.common.enums.MdataRangeEnum;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.dimension.domain.Dimension;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import com.hand.hcf.app.mdata.dimension.service.DimensionItemService;
import com.hand.hcf.app.mdata.dimension.service.DimensionService;

import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/12/26 16:40
 * @remark 维度第三方接口
 */

@RestController
public class DimensionControllerImpl {

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private DimensionItemService dimensionItemService;

    /**
     * 根据维度id列表获取维度信息
     * @param ids
     * @return
     */
    public List<DimensionCO> listDimensionsByIds(@RequestBody List<Long> ids) {
        List<Dimension> dimensionList = dimensionService.selectBatchIds(ids);
        List<DimensionCO> dimensionCOList = new ArrayList<>();
        dimensionList.stream().forEach(e -> {
            DimensionCO dimensionCO = mapper.map(e, DimensionCO.class);
            dimensionCOList.add(dimensionCO);
        });
        return dimensionCOList;
    }

    /**
     * 根据维值id列表获取维值信息
     * @param ids
     * @return
     */
    public List<DimensionItemCO> listDimensionItemsByIds(@RequestBody List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }else {
            List<DimensionItem> dimensionItemList = dimensionItemService.selectBatchIds(ids);
            List<DimensionItemCO> dimensionItemCOList = new ArrayList<>();
            dimensionItemList.stream().forEach(e -> {
                DimensionItemCO dimensionItemCO = mapper.map(e, DimensionItemCO.class);
                dimensionItemCOList.add(dimensionItemCO);
            });
            return dimensionItemCOList;
        }
    }

    /**
     * 根据账套Id查询维度集合，按状态条件查询，状态字段可为空
     * @param setOfBooksId
     * @param enabled
     * @return
     */
    public List<DimensionCO> listDimensionsBySetOfBooksIdAndEnabled(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                    @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<Dimension> dimensionList = dimensionService.selectList(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id", setOfBooksId)
                        .eq(enabled != null, "enabled",enabled)
        );
        List<DimensionCO> dimensionCOList = new ArrayList<>();
        dimensionList.forEach(e -> {
            DimensionCO dimensionCO = mapper.map(e, DimensionCO.class);
            dimensionCOList.add(dimensionCO);
        });
        return dimensionCOList;
    }

    /**
     * 条件查询维度信息
     * @param setOfBooksId
     * @param enabled
     * @return
     */
    public List<DimensionDetailCO> listDimensionsBySetOfBooksIdAndIdsAndEnabled(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                                @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                                @RequestBody(required = false) List<Long> ids) {
        List<Dimension> dimensionList = dimensionService.selectList(
                new EntityWrapper<Dimension>()
                        .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                        .eq(enabled != null, "enabled",enabled)
                        .in(ids != null && ids.size() > 0, "id", ids)
        );
        List<DimensionDetailCO> dimensionDetailCOList = new ArrayList<>();
        dimensionList.forEach(e -> {
            DimensionDetailCO dimensionDetailCO = mapper.map(e, DimensionDetailCO.class);

            List<DimensionItem> dimensionItemList = dimensionItemService.selectList(
                    new EntityWrapper<DimensionItem>().eq("dimension_id", e.getId())
            );
            List<DimensionItemCO> dimensionItemCOList = new ArrayList<>();
            dimensionItemList.stream().forEach(item -> {
                DimensionItemCO dimensionItemCO = mapper.map(item, DimensionItemCO.class);
                dimensionItemCOList.add(dimensionItemCO);
            });
            dimensionDetailCO.setSubDimensionItemCOS(dimensionItemCOList);
            dimensionDetailCOList.add(dimensionDetailCO);
        });
        return dimensionDetailCOList;
    }

    /**
     * 根据账套Id分页查询启用的维度信息,条件含代码，名称，范围，维度Id集合
     * @param setOfBooksId
     * @param range
     * @param ids
     * @param code
     * @param name
     * @param page
     * @param size
     * @return
     */
    public Page<DimensionCO> pageEnabledDimensionsBySetOfBooksIdAndRange(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                         @RequestParam(value = "range", required = false) Integer range,
                                                                         @RequestBody(required = false) List<Long> ids,
                                                                         @RequestParam(value = "code", required = false) String code,
                                                                         @RequestParam(value = "name", required = false) String name,
                                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Page myBatisPage = PageUtil.getPage(page, size);
        Wrapper wrapper = new EntityWrapper<Dimension>();
        if (range != null) {
            if (range.equals(MdataRangeEnum.SELECTED.getId())) {
                if(CollectionUtils.isEmpty(ids)){
                    return myBatisPage;
                }
                wrapper = wrapper.in("id", ids);
            } else if (range.equals(MdataRangeEnum.UN_SELECTED.getId())) {
                wrapper = wrapper.notIn(ids != null, "id", ids);
            }
        }

        Page<Dimension> dimensionPage = dimensionService.selectPage(
                myBatisPage,
                wrapper.eq("set_of_books_id", setOfBooksId)
                        .eq("enabled", true)
                        .like(code != null, "dimension_code", code)
                        .like(name != null, "dimension_name", name)
        );
        List<DimensionCO> dimensionCOList = new ArrayList<>();
        dimensionPage.getRecords().stream().forEach(e -> {
            DimensionCO dimensionCO = mapper.map(e, DimensionCO.class);
            dimensionCOList.add(dimensionCO);
        });
        myBatisPage.setRecords(dimensionCOList);
        return myBatisPage;
    }

    /**
     * 根据账套ID、维度序号、维值代码查询维值信息
     * @param setOfBooksId
     * @param sequence
     * @param itemCode
     * @return
     */
    public DimensionItemCO getDimensionItemBySetOfBooksIdAndSequenceAndCode(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                            @RequestParam(value = "sequence") Integer sequence,
                                                                            @RequestParam(value = "itemCode") String itemCode) {
        Dimension dimension = dimensionService.selectOne(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id", setOfBooksId)
                        .eq("dimension_sequence",sequence)
        );
        if (dimension == null) {
            return null;
        }
        DimensionItem dimensionItem = dimensionItemService.selectOne(
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id",dimension.getId())
                        .eq("dimension_item_code", itemCode)
        );

        return mapper.map(dimensionItem, DimensionItemCO.class);
    }

    /**
     * 根据维度ID、维值代码、维值名称查询维值信息
     * @param dimensionId
     * @param itemCode
     * @param itemName
     * @return
     */
    public List<DimensionItemCO> listEnabledDimensionItemsByDimensionIdAndCond(@RequestParam(value = "dimensionId") Long dimensionId,
                                                                               @RequestParam(value = "itemCode", required = false) String itemCode,
                                                                               @RequestParam(value = "itemName", required = false) String itemName) {
        List<DimensionItem> dimensionItemList = dimensionItemService.selectList(
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id",dimensionId)
                        .eq("enabled", true)
                        .like(itemCode != null, "dimension_item_code", itemCode)
                        .like(itemName != null, "dimension_item_name", itemName)
        );
        List<DimensionItemCO> dimensionItemCOList = new ArrayList<>();
        dimensionItemList.stream().forEach(e -> {
            DimensionItemCO dimensionItemCO = mapper.map(e, DimensionItemCO.class);
            dimensionItemCOList.add(dimensionItemCO);
        });
        return dimensionItemCOList;
    }

    /**
     * 通过账套ID、维度ID集合、维度code、维度name，查询账套下不在所传维度id集合中的维度信息
     * @param setOfBooksId
     * @param dimensionIds
     * @param dimensionCode
     * @param dimensionName
     * @return
     */
    public List<DimensionCO> listDimensionsBySetOfBooksIdAndDimensionIdsAndCodeAndName(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                                       @RequestBody(required = false) List<Long> dimensionIds,
                                                                                       @RequestParam(value = "dimensionCode", required = false) String dimensionCode,
                                                                                       @RequestParam(value = "dimensionName", required = false) String dimensionName) {
        List<Dimension> dimensionList = dimensionService.selectList(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id", setOfBooksId)
                        .notIn(dimensionIds != null && dimensionIds.size() > 0, "id", dimensionIds)
                        .like(dimensionCode != null, "dimension_code", dimensionCode)
                        .like(dimensionName != null, "dimension_name", dimensionName)
        );
        List<DimensionCO> dimensionCOList = new ArrayList<>();
        dimensionList.stream().forEach(e -> {
            DimensionCO dimensionCO = mapper.map(e, DimensionCO.class);
            dimensionCOList.add(dimensionCO);
        });
        return dimensionCOList;
    }

    public List<DimensionCO> listDimensionsByCompanyId(Long companyId) {
        List<Dimension> dimensionList = dimensionService.listDimensionsByCompanyId(companyId);

        List<DimensionCO> dimensionCOList = new ArrayList<>();
        dimensionList.forEach(e -> {
            DimensionCO dimensionCO = mapper.map(e, DimensionCO.class);
            dimensionCOList.add(dimensionCO);
        });
        return dimensionCOList;
    }

    public List<DimensionCO> listDimensionsBySetOfBooksIdConditionByIgnoreIds(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                              @RequestParam(value = "dimensionCode", required = false) String dimensionCode,
                                                                              @RequestParam(value = "dimensionName", required = false) String dimensionName,
                                                                              @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                              @RequestBody(required = false) List<Long> ignoreIds) {
        List<Dimension> dimensionList = dimensionService.listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId,dimensionCode,dimensionName,enabled,ignoreIds);
        return mapper.mapAsList(dimensionList,DimensionCO.class);
    }

    public List<DimensionDetailCO> listDetailByIdsConditionCompanyId(@RequestBody List<Long> dimensionIds,
                                                                     @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                     @RequestParam(value = "companyId") Long companyId,
                                                                     @RequestParam(value = "unitId") Long unitId,
                                                                     @RequestParam(value = "userId") Long userId) {
        return dimensionItemService.listItemsByDimensionIdsAndEnabled(dimensionIds, enabled, companyId, unitId, userId);
    }

   // @Override
    public void proInsertDimensionItem(Long setOfBooksId,
                                       String parameterCode,
                                       String projectNumber,
                                       String projectName,
                                       String startDate,
                                       String endDate,
                                       Integer visibleUserScope,
                                       List<Long> departmentOrUserGroupIdList) {
        dimensionItemService.proInsertDimensionItem(setOfBooksId,parameterCode,projectNumber,projectName,startDate,endDate,visibleUserScope,departmentOrUserGroupIdList);
    }

   // @Override
    public List<DimensionItemCO> listDimensionItemsByDimensionIdAndEnabledAndPermission(Long dimensionId, Boolean enabled, Long companyId, Long departmentId, Long userId) {
        List<DimensionItem> dimensionItemList = dimensionItemService.listDimensionItemsByDimensionIdAndEnabledAndPermission(dimensionId,enabled,companyId,departmentId,userId);
        List<DimensionItemCO> dimensionItemCOList = new ArrayList<>();
        dimensionItemList.stream().forEach(e -> {
            DimensionItemCO dimensionItemCO = mapper.map(e, DimensionItemCO.class);
            dimensionItemCOList.add(dimensionItemCO);
        });
        return dimensionItemCOList;
    }

    //jiu.zhao 修改三方接口 20190411
    public List<DimensionDetailCO> listDimensionsByIdsAndEnabled(List<Long> ids, Boolean enabled) {
        List<DimensionDetailCO> dimensionDetailCOS = listDimensionsBySetOfBooksIdAndIdsAndEnabled((Long)null, enabled, ids);
        return (List)(dimensionDetailCOS == null ? new ArrayList() : dimensionDetailCOS);
    }

    public List<DimensionCO> listDimensionBySetOfBooksIdAndEnabled(Long setOfBooksId, Boolean enabled) {
        List<DimensionCO> dimensionCOList = listDimensionsBySetOfBooksIdAndEnabled(setOfBooksId, enabled);
        return (List)(dimensionCOList == null ? new ArrayList() : dimensionCOList);
    }

    public DimensionCO getDimensionById(Long dimensionId) {
        List<DimensionCO> dimensionCOList = listDimensionsByIds(Arrays.asList(dimensionId));
        return dimensionCOList != null && dimensionCOList.size() > 0 ? (DimensionCO)dimensionCOList.get(0) : null;
    }

    public DimensionItemCO getDimensionItemById(Long dimensionItemId) {
        List<DimensionItemCO> dimensionItemCOList = listDimensionItemsByIds(Arrays.asList(dimensionItemId));
        return dimensionItemCOList != null && dimensionItemCOList.size() > 0 ? (DimensionItemCO)dimensionItemCOList.get(0) : null;
    }
}
