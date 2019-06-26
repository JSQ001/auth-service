package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDimension;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeDimensionService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/1
 */
@Api(tags = "报账单类型关联维度")
@RestController
@RequestMapping("/api/expense/report/type/dimension")
public class ExpenseReportTypeDimensionController {
    private final ExpenseReportTypeDimensionService expenseReportTypeDimensionService;
    public ExpenseReportTypeDimensionController(ExpenseReportTypeDimensionService expenseReportTypeDimensionService){
        this.expenseReportTypeDimensionService = expenseReportTypeDimensionService;
    }

    /**
     * 单个新增 报账单类型关联维度
     * @param expenseReportTypeDimension
     * @return
     */
    /**
     * @api {POST} /api/expense/report/type/dimension 【报账单类型关联维度】单个新增
     * @apiDescription 报账单类型单个新增维度
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiParam {Long} dimensionId 维度ID
     * @apiParam {Boolean} mustEnter 是否必输
     * @apiParam {Long} defaultValueId 默认维值ID
     * @apiParam {String} position  布局位置
     * @apiParam {Interge} sequenceNumber 优先级
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} dimensionId 维度ID
     * @apiSuccess {Boolean} mustEnter 是否必输
     * @apiSuccess {Long} defaultValueId 默认维值ID
     * @apiSuccess {String} position  布局位置
     * @apiSuccess {Interge} sequenceNumber 优先级
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    {
    "reportTypeId":1101007150869291010,
    "dimensionId":1084698231868743682,
    "mustEnter":true,
    "defaultValueId":1084698412072820738,
    "position":"HEADER",
    "sequenceNumber":10
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1102388310097612801",
    "createdDate": "2019-03-04T10:00:29.175+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-04T10:00:29.175+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "reportTypeId": "1101007150869291010",
    "dimensionId": "1084698231868743682",
    "mustEnter": true,
    "defaultValueId": "1084698412072820738",
    "position": "HEADER",
    "sequenceNumber": 10
    }
     */
    @PostMapping
    @ApiOperation(value = "报账单类型单个新增维度", notes = "报账单类型单个新增维度 开发:xue.han")
    public ResponseEntity<ExpenseReportTypeDimension> createExpenseReportTypeDimension(@ApiParam(value = "报账单类型关联维度") @RequestBody ExpenseReportTypeDimension expenseReportTypeDimension){
        return ResponseEntity.ok(expenseReportTypeDimensionService.createExpenseReportTypeDimension(expenseReportTypeDimension));
    }

    /**
     * 单个修改 报账单类型关联维度
     * @param expenseReportTypeDimension
     * @return
     */
    /**
     * @api {PUT} /api/expense/report/type/dimension 【报账单类型关联维度】单个修改
     * @apiDescription 报账单类型单个修改维度
     * @apiGroup ReportTypeService
     * @apiParam {Long} id 主键id
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiParam {Long} dimensionId 维度ID
     * @apiParam {Boolean} mustEnter 是否必输
     * @apiParam {Long} defaultValueId 默认维值ID
     * @apiParam {String} position  布局位置
     * @apiParam {Interge} sequenceNumber 优先级
     * @apiParam {Long} versionNumber 版本号
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} dimensionId 维度ID
     * @apiSuccess {Boolean} mustEnter 是否必输
     * @apiSuccess {Long} defaultValueId 默认维值ID
     * @apiSuccess {String} position  布局位置
     * @apiSuccess {Interge} sequenceNumber 优先级
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    {
    "id":1102388310097612801,
    "reportTypeId":1101007150869291010,
    "dimensionId":1084698231868743682,
    "mustEnter":true,
    "defaultValueId":1084698412072820738,
    "position":"HEADER",
    "sequenceNumber":20,
    "versionNumber":1
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1102388310097612801",
    "createdDate": "2019-03-04T10:00:29.175+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-04T10:56:19.109+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "reportTypeId": "1101007150869291010",
    "dimensionId": "1084698231868743682",
    "mustEnter": true,
    "defaultValueId": "1084698412072820738",
    "position": "HEADER",
    "sequenceNumber": 20
    }
     */
    @PutMapping
    @ApiOperation(value = "报账单类型单个修改维度", notes = "报账单类型单个修改维度 开发:xue.han")
    public ResponseEntity<ExpenseReportTypeDimension> updateExpenseReportTypeDimension(@ApiParam(value = "报账单类型关联维度") @RequestBody ExpenseReportTypeDimension expenseReportTypeDimension){
        return ResponseEntity.ok(expenseReportTypeDimensionService.updateExpenseReportTypeDimension(expenseReportTypeDimension));
    }

    /**
     * 根据id删除 报账单类型关联维度
     * @param id
     * @return
     */
    /**
     * @api {DELETE} /api/expense/report/type/dimension/{id} 【报账单类型关联维度】单个删除
     * @apiDescription 报账单类型单个删除维度
     * @apiGroup ReportTypeService
     * @apiParamExample {json} 请求参数:
        /api/expense/report/type/dimension/1102404301313626113
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "报账单类型单个删除维度", notes = "报账单类型单个删除维度 开发:xue.han")
    public ResponseEntity deleteExpenseReportTypeDimension(@PathVariable Long id){
        expenseReportTypeDimensionService.deleteExpenseReportTypeDimension(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 分页查询 某个报账单类型下分配的维度
     * @param reportTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     *
     * @api {GET} /api/expense/report/type/dimension/query/by/cond?reportTypeId=1101007150869291010&page=0&size=10 【报账单类型关联维度】分页查询
     * @apiDescription 根据报账单类型ID->reportTypeId 查询出与之对应的维度表中的数据(分页)
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} dimensionId 维度ID
     * @apiSuccess {String} dimensionName 维度名称
     * @apiSuccess {Boolean} mustEnter 是否必输
     * @apiSuccess {Long} defaultValueId 默认维值ID
     * @apiSuccess {String} defaultValueName 默认维值名称
     * @apiSuccess {String} position  布局位置
     * @apiSuccess {Interge} sequenceNumber 优先级
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiSuccessExample {json} Success-Result
     [
    {
    "id": "1102388310097612801",
    "createdDate": "2019-03-04T10:00:29.175+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-04T10:56:19.109+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "reportTypeId": "1101007150869291010",
    "dimensionId": "1084698231868743682",
    "mustEnter": true,
    "defaultValueId": "1084698412072820738",
    "position": "HEADER",
    "sequenceNumber": 20
    }
    ]
     */
    @GetMapping("/query/by/cond")
    @ApiOperation(value = "分页查询 某个报账单类型下分配的维度", notes = "分页查询 某个报账单类型下分配的维度 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportTypeDimension>> getExpenseReportTypeDimensionByCond(
            @ApiParam(value = "报账单类型ID") @RequestParam(value = "reportTypeId") Long reportTypeId,
            @ApiIgnore Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseReportTypeDimension> result = expenseReportTypeDimensionService.getExpenseReportTypeDimensionByCond(reportTypeId,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据报账单类型id 不分页查询 其尚未分配的维度
     *
     * @param reportTypeId 报账单类型id
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/dimension/query/not/assign/dimension?reportTypeId=1101007150869291010 【报账单类型关联维度】不分页查询未分配的维度
     * @apiDescription 根据报账单类型ID->reportTypeId 查询出其尚未分配的维度
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} id  主键ID
     * @apiSuccess {String} dimensionCode 维度代码
     * @apiSuccess {String} dimensionName 维度名称
     * @apiSuccess {Integer} dimensionSequence 维度优先级
     * @apiSuccess {Long} setOfBooksId 账套ID
     * @apiSuccess {Boolean} enabled 是否启用
     * @apiSuccess {Boolean} assigned 是否被分配
     * @apiSuccessExample {json} Success-Result
    [
    {
    "id": "1102388310097612801",
    "createdDate": "2019-03-04T10:00:29.175+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-04T10:56:19.109+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "reportTypeId": "1101007150869291010",
    "dimensionId": "1084698231868743682",
    "mustEnter": true,
    "defaultValueId": "1084698412072820738",
    "position": "HEADER",
    "sequenceNumber": 20
    }
    ]
     */
    @GetMapping("/query/not/assign/dimension")
    @ApiOperation(value = "根据报账单类型ID->reportTypeId 查询出其尚未分配的维度", notes = "根据报账单类型ID->reportTypeId 查询出其尚未分配的维度 开发:xue.han")
    public ResponseEntity<List<DimensionCO>> getNotAssignDimensionForExpenseReportType(
            @ApiParam(value = "报账单类型ID") @RequestParam("reportTypeId") Long reportTypeId
    ) throws URISyntaxException {
        List<DimensionCO> result = expenseReportTypeDimensionService.getNotAssignDimensionForExpenseReportType(reportTypeId);
        return ResponseEntity.ok(result);
    }
}
