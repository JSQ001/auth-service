package com.hand.hcf.app.mdata.dimension.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.dimension.domain.Dimension;
import com.hand.hcf.app.mdata.dimension.service.DimensionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dimension")
public class DimensionController {

    private final Logger log = LoggerFactory.getLogger(DimensionController.class);

    @Autowired
    private DimensionService dimensionService;

    /**
     * @apiDefine Dimension 维度定义
     */

    /**
     * @api {POST} /api/dimension 【维度】创建
     * @apiGroup Dimension
     * @apiParam {Object} dimension  维度定义
     * @apiParam {String} dimension.dimensionCode  维度定义代码
     * @apiParam {String} dimension.dimensionName  维度定义名称
     * @apiParam {Integer} dimension.dimensionSequence  维度定义序号
     * @apiParam {Long} dimension.setOfBooksId  账套id
     * @apiParam {Boolean} dimension.enabled  是否启用
     * @apiParamExample {json} 请求参数:
     * {
     *      "i18n": {
     *          "dimensionName": [
     *          {
     *          "language": "zh_cn",
     *          "value": "中文1"
     *          },
     *          {
     *          "language": "en",
     *          "value": "english1"
     *          }
     *          ]
     *       },
     *      "dimensionCode":"dimension1",
     *      "dimensionName":"维度定义1",
     *      "dimensionSequence":1,
     *      "setOfBooksId":"2354525235252",
     *      "enabled":true
     *}
     * @apiSuccessExample {json} 成功返回值:
     * {
     *      "i18n": {
     *         "dimensionName": [
     *             {
     *                 "language": "zh_cn",
     *                 "value": "维度定义2"
     *             },
     *             {
     *                 "language": "en",
     *                 "value": "dimension2"
     *             }
     *         ]
     *     },
     *     "id": "1077125882492088321",
     *     "deleted": false,
     *     "createdDate": "2018-12-24T16:56:37.076+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-24T16:56:37.077+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "dimensionCode": "dimension1",
     *     "dimensionName": "维度定义1",
     *     "dimensionSequence": 1,
     *     "setOfBooksId": "2354525235252"
     * }
     *@apiErrorExample {json} 错误返回值:
     * {
     *     "message": "账套下维度代码不允许重复！",
     *     "errorCode": "VALIDATION_ERROR",
     *     "category": "ERROR",
     *     "bizErrorCode": "DIMENSION_CODE_REPEAT"
     * }
     */
    @PostMapping
    public Dimension insertDimension(@RequestBody Dimension dimension){
        log.debug("REST request to save dimension : {}", dimension);
        return dimensionService.insertDimension(dimension);
    }

    /**
     * @api {PUT} /api/dimension 【维度】更新
     * @apiGroup Dimension
     * @apiParam {Object} dimension  维度定义
     * @apiParam {String} dimension.id  维度定义id
     * @apiParam {String} dimension.dimensionCode  维度定义代码
     * @apiParam {String} dimension.dimensionName  维度定义名称
     * @apiParam {Integer} dimension.dimensionSequence  维度定义序号
     * @apiParam {Long} dimension.setOfBooksId  账套id
     * @apiParam {Boolean} dimension.enabled  是否启用
     * @apiParam {Integer} dimension.versionNumber  版本号
     * @apiParam {ZonedDateTime} dimension.createdDate  创建时间
     * @apiParam {ZonedDateTime} dimension.lastUpdatedDate  最后更新时间
     * @apiParam {Long} dimension.createdBy  创建人
     * @apiParam {Long} dimension.lastUpdatedBy  最后更新人
     * @apiParamExample {json} 请求参数:
     * {
     *      "i18n": {
     *         "dimensionName": [
     *         {
     *         "language": "zh_cn",
     *         "value": "维度定义1"
     *         },
     *         {
     *         "language": "en",
     *         "value": "dimension1"
     *         }
     *         ]
     *      },
     *      "id": "2345252353421",
     *      "deleted": false,
     *      "dimensionCode": "dimension1",
     *      "dimensionName": "维度1",
     *      "dimensionSequence": 1,
     *      "setOfBooksId": "2354525235252",
     *      "enabled": true,
     *      "versionNumber": 1,
     *      "createdDate": "2018-12-21T07:31:06Z",
     *      "lastUpdatedDate": "2018-12-21T07:31:06Z",
     *      "createdBy": 1001,
     *      "lastUpdatedBy": 1001
     *}
     * @apiSuccessExample {json} 成功返回值:
     * {
     *      "i18n": {
     *          "dimensionName": [
     *              {
     *                 "language": "zh_cn",
     *                 "value": "维度定义1"
     *              },
     *              {
     *                  "language": "en",
     *                  "value": "dimension1"
     *              }
     *          ]
     *       },
     *     "id": "1077125882492088321",
     *     "deleted": false,
     *     "createdDate": "2018-12-24T16:56:37.076+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-24T16:56:37.077+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 2,
     *     "enabled": true,
     *     "dimensionCode": "dimension1",
     *     "dimensionName": "维度定义222",
     *     "dimensionSequence": 1,
     *     "setOfBooksId": "2354525235252"
     * }
     *@apiErrorExample {json} 错误返回值:
     * {
     *     "message": "账套下维度代码不允许重复！",
     *     "errorCode": "VALIDATION_ERROR",
     *     "category": "ERROR",
     *     "bizErrorCode": "DIMENSION_CODE_REPEAT"
     * }
     */
    @PutMapping
    public Dimension updateDimension(@RequestBody Dimension dimension){
        log.debug("REST request to update dimension : {}", dimension);
        return dimensionService.updateDimension(dimension);
    }

    /**
     * @api {DELETE} /api/dimension/{dimensionId} 【维度】删除
     * @apiGroup Dimension
     * @apiParam {Long} dimensionId  待删除的id
     */
    @DeleteMapping("/{dimensionId}")
    public void deleteDimensionById(@PathVariable(value = "dimensionId") Long dimensionId){
        log.debug("REST request to delete dimension : {}", dimensionId);
        dimensionService.deleteDimensionById(dimensionId);
    }

    /**
     * @api {GET} /api/dimension/list/unselected/sequence/by/{setOfBooksId}  【维度】查询账套下未定义的维度序号
     * @apiDescription 用于创建维度时序号下拉框
     * @apiGroup Dimension
     * @apiParam {Long} setOfBooksId  账套id
     * @apiSuccessExample {json} 成功返回值:
     * [
     *      18,
     *      19,
     *      20
     *]
     */
    @GetMapping("/list/unselected/sequence/by/{setOfBooksId}")
    public List<Integer> listUnselectedSequenceBySetOfBooksId(@PathVariable(value = "setOfBooksId") Long setOfBooksId){
        return  dimensionService.listUnselectedSequenceBySetOfBooksId(setOfBooksId);
    }

    /**
     * @api {GET} /api/dimension/page/by/cond 【维度】条件查询
     * @apiDescription 用于维度列表界面的条件查询
     * @apiGroup Dimension
     * @apiParam  {Long} [setOfBooksId] 账套ID
     * @apiParam  {String} [dimensionCode] 维度代码
     * @apiParam  {String} [dimensionName] 维度名称
     * @apiParam  {Boolean} [enabled] 是否启用
     * @apiParam  {int} page 分页page
     * @apiParam  {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1077125882492088321",
     *         "deleted": false,
     *         "createdDate": "2018-12-24T16:56:37.076+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T17:07:14.753+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 2,
     *         "enabled": true,
     *         "dimensionCode": "dimension1",
     *         "dimensionName": "维度定义222",
     *         "dimensionSequence": 1,
     *         "setOfBooksId": "2354525235252"
     *     }
     * ]
     */
    @GetMapping("/page/by/cond")
    public ResponseEntity<List<Dimension>> pageDimensionsBySetOfBooksIdAndCond(@RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                               @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                                               @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                                               @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                               @RequestParam(value = "page",defaultValue = "0") int page,
                                                                               @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<Dimension> result = dimensionService.pageDimensionsBySetOfBooksIdAndCond(setOfBooksId, dimensionCode, dimensionName, enabled, queryPage,false);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/page/by/cond");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * 维度定义查询 数据权限控制
     * @param setOfBooksId  账套ID
     * @param dimensionCode 维度代码
     * @param dimensionName 维度名称
     * @param enabled       是否启用
     * @param page          分页page
     * @param size          分页size
     * @return              维度信息
     */
    @ApiOperation(value = "【维度】维度定义查询 数据权限控制", notes = "维度定义查询 数据权限控制 开发：王帅")
    @GetMapping("/page/by/cond/enable/dataAuth")
    public ResponseEntity<List<Dimension>> pageDimensionsBySetOfBooksIdAndCondEnableDataAuth(@ApiParam(value = "账套ID")
                                                                                             @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                                             @ApiParam(value = "维度代码")
                                                                                             @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                                                             @ApiParam(value = "维度名称")
                                                                                             @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                                                             @ApiParam(value = "是否启用")
                                                                                             @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                             @ApiParam(value = "分页page")
                                                                                             @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                             @ApiParam(value = "分页size")
                                                                                             @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<Dimension> result = dimensionService.pageDimensionsBySetOfBooksIdAndCond(setOfBooksId, dimensionCode, dimensionName, enabled, queryPage,true);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/page/by/cond");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/{dimensionId} 【维度】详情查询
     * @apiDescription 点击编辑时查询维值详情
     * @apiGroup Dimension
     * @apiParam {Long} dimensionId  维度id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "i18n": {
     *         "dimensionName": [
     *             {
     *                 "language": "en",
     *                 "value": "dimension3"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "维度定义3"
     *             }
     *         ]
     *     },
     *     "id": "1077898381186142209",
     *     "deleted": false,
     *     "createdDate": "2018-12-26T20:06:15.117+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-26T21:29:10.403+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 3,
     *     "enabled": true,
     *     "dimensionCode": "dimension333",
     *     "dimensionName": "维度定义3",
     *     "dimensionSequence": 3,
     *     "setOfBooksId": "1"
     * }
     */
    @GetMapping("/{dimensionId}")
    public Dimension getDimensionById(@PathVariable(value = "dimensionId") Long dimensionId){
        return dimensionService.getDimensionById(dimensionId);
    }

    @PostMapping("/list/dimension/by/ids")
    public List<Dimension> getDimensionsByIds(@RequestBody List<Long> dimensionIds){
        return dimensionService.getDimensionsByIds(dimensionIds);
    }
}
