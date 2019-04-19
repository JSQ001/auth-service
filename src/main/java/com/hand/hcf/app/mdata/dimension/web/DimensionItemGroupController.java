package com.hand.hcf.app.mdata.dimension.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemGroup;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemGroupAssignItem;
import com.hand.hcf.app.mdata.dimension.service.DimensionItemGroupService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dimension/item/group")
public class DimensionItemGroupController {

    private final Logger log = LoggerFactory.getLogger(DimensionItemGroupController.class);
    
    @Autowired
    private DimensionItemGroupService dimensionItemGroupService;

    /**
     * @api {POST} /api/dimension/item/group 【维值组】创建
     * @apiGroup Dimension
     * @apiParam {Object} dimensionItemGroup  维值组定义
     * @apiParam {String} dimensionItemGroup.dimensionItemGroupCode  维值组定义代码
     * @apiParam {String} dimensionItemGroup.dimensionItemGroupName  维值组定义名称
     * @apiParam {Long} dimensionItemGroup.dimensionId  维度id
     * @apiParam {Boolean} dimensionItemGroup.enabled  是否启用
     * @apiParamExample {json} 请求参数:
     * {
     *      "i18n": {
     *           "dimensionItemGroupName": [
     *              {
     *                  "language": "zh_cn",
     *                  "value": "中文1"
     *              },
     *              {
     *                  "language": "en",
     *                  "value": "english1"
     *              }
     *           ]
     *      },
     *     "enabled": true,
     *     "dimensionItemGroupCode": "dimensionItem1",
     *     "dimensionItemGroupName": "维值组定义1",
     *     "dimensionId": "1077180095153627137"
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *      "i18n": {
     *           "dimensionItemGroupName": [
     *              {
     *                  "language": "zh_cn",
     *                  "value": "中文1"
     *              },
     *              {
     *                  "language": "en",
     *                  "value": "english1"
     *              }
     *           ]
     *      },
     *     "id": "1077199698432942082",
     *     "deleted": false,
     *     "createdDate": "2018-12-24T21:49:56.167+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-24T21:49:56.167+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "dimensionItemGroupCode": "dimensionItem1",
     *     "dimensionItemGroupName": "维值组定义1",
     *     "dimensionId": "1077180095153627137"
     * }
     */
    @PostMapping
    public DimensionItemGroup insertDimensionItemGroup(@RequestBody DimensionItemGroup dimensionItemGroup){
        log.debug("REST request to save dimensionItemGroup : {}", dimensionItemGroup);
        return dimensionItemGroupService.insertDimensionItemGroup(dimensionItemGroup);
    }

    /**
     * @api {PUT} /api/dimension/item/group/{dimensionItemGroupId} 【维值组】更新
     * @apiGroup Dimension
     * @apiParam {Object} dimensionItemGroup  维值组定义
     * @apiParamExample {json} 请求参数:
     * {
     *      "i18n": {
     *           "dimensionItemGroupName": [
     *              {
     *                  "language": "zh_cn",
     *                  "value": "中文1"
     *              },
     *              {
     *                  "language": "en",
     *                  "value": "english1"
     *              }
     *           ]
     *      },
     *     "id": "1077199698432942082",
     *     "deleted": false,
     *     "createdDate": "2018-12-24T21:49:56.167+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-24T21:49:56.167+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "dimensionItemGroupCode": "dimensionItem1",
     *     "dimensionItemGroupName": "维值组2",
     *     "dimensionId": "1077180095153627137"
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *      "i18n": {
     *           "dimensionItemGroupName": [
     *              {
     *                  "language": "zh_cn",
     *                  "value": "中文1"
     *              },
     *              {
     *                  "language": "en",
     *                  "value": "english1"
     *              }
     *           ]
     *      },
     *     "id": "1077199698432942082",
     *     "deleted": false,
     *     "createdDate": "2018-12-24T21:49:56.167+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-24T21:49:56.167+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 2,
     *     "enabled": true,
     *     "dimensionItemGroupCode": "dimensionItem1",
     *     "dimensionItemGroupName": "维值组2",
     *     "dimensionId": "1077180095153627137"
     * }
     */
    @PutMapping
    public DimensionItemGroup updateDimensionItemGroup(@RequestBody DimensionItemGroup dimensionItemGroup){
        log.debug("REST request to update dimensionItemGroup : {}", dimensionItemGroup);
        return dimensionItemGroupService.updateDimensionItemGroup(dimensionItemGroup);
    }

    /**
     * @api {DELETE} /api/dimension/item/group/{dimensionItemGroupId} 【维值组】删除
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  待删除的id
     */
    @DeleteMapping("/{dimensionItemGroupId}")
    public void deleteDimensionItemGroupById(@PathVariable(value = "dimensionItemGroupId") Long dimensionItemGroupId){
        log.debug("REST request to delete dimensionItemGroup : {}", dimensionItemGroupId);
        dimensionItemGroupService.deleteDimensionItemGroupById(dimensionItemGroupId);
    }

    /**
     * @api {DELETE} /api/dimension/item/group/batch 【维值组】批量删除
     * @apiGroup Dimension
     * @apiParam {List} dimensionItemGroupIds  维值组id
     * @apiParamExample {json} 请求参数:
     * [
     *      "2345234343",
     *      "4222342323"
     * ]
     */
    @DeleteMapping("/batch")
    public void deleteDimensionItemGroupBatch(@RequestBody List<Long> dimensionItemGroupIds){
        log.debug("REST request to delete dimensionItemGroups : {}", dimensionItemGroupIds);
        dimensionItemGroupService.deleteDimensionItemGroupBatch(dimensionItemGroupIds);
    }

    /**
     * @api {GET} /api/dimension/item/group/page/by/cond 【维值组】条件查询
     * @apiDescription 维值组列表界面的条件查询
     * @apiGroup Dimension
     * @apiParam {Long} dimensionId  维度ID
     * @apiParam {String} [dimensionItemGroupCode]  维值组代码
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077199698432942082",
     *         "deleted": false,
     *         "createdDate": "2018-12-24T21:49:56.167+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T21:53:33.55+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 2,
     *         "enabled": true,
     *         "dimensionItemGroupCode": "dimensionItem1",
     *         "dimensionItemGroupName": "维值组2",
     *         "dimensionId": "1077180095153627137"
     *     }
     * ]
     */
    @GetMapping("/page/by/cond")
    public ResponseEntity<List<DimensionItemGroup>> pageDimensionItemGroupsByDimensionIdAndCond(
            @RequestParam(value = "dimensionId") Long dimensionId,
            @RequestParam(value = "dimensionItemGroupCode",required = false) String dimensionItemGroupCode,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItemGroup> result = dimensionItemGroupService.pageDimensionItemGroupsByDimensionIdAndCond(dimensionId, dimensionItemGroupCode, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/group/page/by/cond");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/dimension/item/group/subDimensionItem/batch 【维值组-子维值】批量插入
     * @apiDescription 维值组分配子维值弹窗界面点击确定
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiParam {List} dimensionItemIds  维值id
     * @apiParamExample {json} 请求参数:
     * [1077180351433990145]
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077205443387875329",
     *         "createdDate": "2018-12-24T22:12:45.892+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T22:12:45.895+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *         "dimensionItemId": "1077180351433990145",
     *         "dimensionItemGroupId": "1077199698432942082"
     *     }
     * ]
     */
    @PostMapping("/subDimensionItem/batch")
    public List<DimensionItemGroupAssignItem> insertSubDimensionItemBatch(
            @RequestParam(value = "dimensionItemGroupId") Long dimensionItemGroupId,
            @RequestBody List<Long> dimensionItemIds) {
        return dimensionItemGroupService.insertSubDimensionItemBatch(dimensionItemGroupId, dimensionItemIds);
    }

    /**
     * @api {DELETE} /api/dimension/item/group/subDimensionItem 【维值组-子维值】删除
     * @apiDescription 子维值界面删除
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiParam {Long} dimensionItemId  维值id
     */
    @DeleteMapping("/subDimensionItem")
    public void deleteSubDimensionItem(@RequestParam(value = "dimensionItemGroupId") Long dimensionItemGroupId,
                                       @RequestParam(value = "dimensionItemId") Long dimensionItemId) {
        dimensionItemGroupService.deleteSubDimensionItem(dimensionItemGroupId, dimensionItemId);
    }

    /**
     * @api {DELETE} /api/dimension/item/group/subDimensionItem/batch 【维值组-子维值】批量删除
     * @apiDescription 子维值界面批量删除
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiParam {List} dimensionItemIds  维值id
     */
    @DeleteMapping("/subDimensionItem/batch")
    public void deleteSubDimensionItemBatch(@RequestParam(value = "dimensionItemGroupId") Long dimensionItemGroupId,
                                            @RequestBody List<Long> dimensionItemIds){
        dimensionItemGroupService.deleteSubDimensionItemBatch(dimensionItemGroupId, dimensionItemIds);
    }

    /**
     * @api {GET} /api/dimension/item/group/subDimensionItem/query 【维值组-子维值】条件查询
     * @apiDescription 子维值界面条件查询
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiParam {String} [dimensionItemCode]  维值代码
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077180351433990145",
     *         "deleted": false,
     *         "createdDate": "2018-12-24T20:33:03.484+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T20:33:03.484+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "dimensionItemCode": "code1",
     *         "dimensionItemName": "name1",
     *         "dimensionId": "1077180095153627137",
     *         "visibleUserScope": 1003
     *     }
     * ]
     */
    @GetMapping(value = "/subDimensionItem/query")
    public ResponseEntity<List<DimensionItem>> pageDimensionItemByCond(@RequestParam(value = "dimensionItemGroupId") Long dimensionItemGroupId,
                                                                       @RequestParam(value = "dimensionItemCode",required = false) String dimensionItemCode,
                                                                       @RequestParam(value = "page",defaultValue = "0") int page,
                                                                       @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItem> result = dimensionItemGroupService.pageDimensionItemByCond(dimensionItemGroupId,dimensionItemCode,queryPage);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/group/subDimensionItem/query");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/item/group/subDimensionItem/filter 【维值组-子维值】分配条件查询
     * @apiDescription 分配子维值弹窗条件查询
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiParam {String} [dimensionItemCode]  维值代码
     * @apiParam {String} [dimensionItemName]  维值名称
     * @apiParam {Boolean} [enabled]  是否启用
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * []
     */
    @GetMapping(value = "/subDimensionItem/filter")
    public ResponseEntity<List<DimensionItem>> pageDimensionItemByCond(@RequestParam(value = "dimensionItemGroupId") Long dimensionItemGroupId,
                                                                       @RequestParam(value = "dimensionItemCode",required = false) String dimensionItemCode,
                                                                       @RequestParam(value = "dimensionItemName",required = false) String dimensionItemName,
                                                                       @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                       @RequestParam(value = "page",defaultValue = "0") int page,
                                                                       @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItem> result = dimensionItemGroupService.pageDimensionItemByCond(dimensionItemGroupId, dimensionItemCode, dimensionItemName, enabled, queryPage);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/group/subDimensionItem/filter");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/item/group/{dimensionItemGroupId} 【维值组】详情查询
     * @apiDescription 点击编辑时查询维值组详情
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemGroupId  维值组id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "i18n": {
     *         "dimensionItemGroupName": [
     *             {
     *                 "language": "en",
     *                 "value": "english1"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "中文1"
     *             }
     *         ]
     *     },
     *     "id": "1078130986510835714",
     *     "deleted": false,
     *     "createdDate": "2018-12-27T11:30:32.546+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-27T11:30:32.546+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "dimensionItemGroupCode": "dimensionItem2",
     *     "dimensionItemGroupName": "中文1",
     *     "dimensionId": "1077898381186142209"
     * }
     */
    @GetMapping("/{dimensionItemGroupId}")
    public DimensionItemGroup getDimensionItemById(@PathVariable(value = "dimensionItemGroupId") Long dimensionItemGroupId){
        return dimensionItemGroupService.getDimensionItemGroupById(dimensionItemGroupId);
    }
}
