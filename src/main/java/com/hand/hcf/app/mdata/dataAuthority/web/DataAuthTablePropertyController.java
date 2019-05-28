package com.hand.hcf.app.mdata.dataAuthority.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthTableProperty;
import com.hand.hcf.app.mdata.dataAuthority.service.DataAuthTablePropertyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 17:33
 * @remark
 */
@RestController
@RequestMapping(value = "/api/data/auth/table/properties")
@AllArgsConstructor
public class DataAuthTablePropertyController {

    private final DataAuthTablePropertyService dataAuthTablePropertyService;

    /**
     * 新建 参数配置
     * @param dataAuthTableProperty
     * @return
     */
    /**
     * @api {POST} /api/data/auth/table/properties 【参数配置】新建
     * @apiDescription 新建参数配置
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} tableName 表名称
     * @apiParam (请求参数) {String} dataType 参数类型
     * @apiParam (请求参数) {String} filterMethod 筛选方式
     * @apiParam (请求参数) {String} [customSql] 自定义SQL
     * @apiParam (请求参数) {String} [columnName] 参数名称
     * @apiSuccess (返回参数) {Long} id 主键id
     * @apiSuccess (返回参数) {Boolean} enabled 启用标志
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} createdBy 创建人ID
     * @apiSuccess (返回参数) {Long} {ZonedDateTime} createdDate 创建时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 更新人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess (返回参数) {String} tableName 表名称
     * @apiSuccess (返回参数) {String} dataType 参数类型
     * @apiSuccess (返回参数) {String} filterMethod 筛选方式
     * @apiSuccess (返回参数) {String} customSql 自定义SQL
     * @apiSuccess (返回参数) {String} columnName 参数名称
     * @apiParamExample {json} 请求报文:
     * {
     * "tableName":"hx_test_table",
     * "dataType":"EMPLOYEE",
     * "filterMethod":"TABLE_COLUMN",
     * "columnName":"lqtest2"
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1072026321615949826",
     * "createdDate": "2018-12-10T15:12:47.019+08:00",
     * "createdBy": "1054",
     * "lastUpdatedDate": "2018-12-10T15:12:47.019+08:00",
     * "lastUpdatedBy": "1054",
     * "versionNumber": 1,
     * "enabled": true,
     * "tableName": "hx_test_table",
     * "filterMethod": "3481",
     * "dataType": "3338",
     * "columnName": "lqtest2",
     * "customSql": null
     * }
     */
    @PostMapping
    public ResponseEntity<DataAuthTableProperty> createDataAuthTableProperty (@Valid @RequestBody DataAuthTableProperty dataAuthTableProperty){
        return ResponseEntity.ok(dataAuthTablePropertyService.createDataAuthTableProperty(dataAuthTableProperty));
    }

    /**
     * 编辑 参数配置
     * @param dataAuthTableProperty
     * @return
     */
    /**
     * @api {PUT} /api/data/auth/table/properties 【参数配置】编辑
     * @apiDescription 编辑参数配置
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 主键id
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} tableName 表名称
     * @apiParam (请求参数) {String} dataType 参数类型
     * @apiParam (请求参数) {String} filterMethod 筛选方式
     * @apiParam (请求参数) {String} [customSql] 自定义SQL
     * @apiParam (请求参数) {String} [columnName] 参数名称
     * @apiSuccess (返回参数) {Long} id 主键id
     * @apiSuccess (返回参数) {Boolean} enabled 启用标志
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} createdBy 创建人ID
     * @apiSuccess (返回参数) {Long} {ZonedDateTime} createdDate 创建时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 更新人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess (返回参数) {String} tableName 表名称
     * @apiSuccess (返回参数) {String} dataType 参数类型
     * @apiSuccess (返回参数) {String} filterMethod 筛选方式
     * @apiSuccess (返回参数) {String} customSql 自定义SQL
     * @apiSuccess (返回参数) {String} columnName 参数名称
     * @apiParamExample {json} 请求报文:
     * {
     * "id":1072020351384940546,
     * "tableName":"hx_test_table",
     * "dataType":"EMPLOYEE",
     * "filterMethod":"TABLE_COLUMN",
     * "columnName":"lqtest333",
     * "versionNumber":1
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1072020351384940546",
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 2,
     * "enabled": null,
     * "tableName": "hx_test_table",
     * "filterMethod": "3481",
     * "dataType": "3338",
     * "columnName": "lqtest333",
     * "customSql": null
     * }
     */
    @PutMapping
    public ResponseEntity<DataAuthTableProperty> updateDataAuthTableProperty (@Valid @RequestBody DataAuthTableProperty dataAuthTableProperty){
        dataAuthTablePropertyService.updateDataAuthTableProperty(dataAuthTableProperty);
        return ResponseEntity.ok(dataAuthTableProperty);
    }

    /**
     * 删除 参数配置
     * @param id
     * @return
     */
    /**
     * @api {DELETE} /api/data/auth/table/properties 【参数配置】删除
     * @apiDescription 删除参数配置
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/data/auth/table/properties/1072020351384940546
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDataAuthTableProperty (@PathVariable Long id){
        dataAuthTablePropertyService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据条件 分页查询参数配置
     * @param tableName
     * @param dataType
     * @param filterMethod
     * @param columnName
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/data/auth/table/properties/query 【参数配置】分页查询
     * @apiDescription 根据条件分页查询参数配置
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} [tableName] 表名称
     * @apiParam (请求参数) {String} [dataType] 参数类型
     * @apiParam (请求参数) {String} [filterMethod] 筛选方式
     * @apiParam (请求参数) {String} [columnName] 参数名称
     * @apiSuccess (返回参数) {Long} id 主键id
     * @apiSuccess (返回参数) {Boolean} enabled 启用标志
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} createdBy 创建人ID
     * @apiSuccess (返回参数) {Long} {ZonedDateTime} createdDate 创建时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 更新人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess (返回参数) {String} tableName 表名称
     * @apiSuccess (返回参数) {String} dataType 参数类型
     * @apiSuccess (返回参数) {String} filterMethod 筛选方式
     * @apiSuccess (返回参数) {String} customSql 自定义SQL
     * @apiSuccess (返回参数) {String} columnName 参数名称
     * @apiParamExample {json} 请求报文:
     * localhost:9082/api/data/auth/table/properties/query?page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1072014180158664706",
     * "createdDate": "2018-12-10T14:24:32.271+08:00",
     * "createdBy": "1054",
     * "lastUpdatedDate": "2018-12-10T14:24:32.271+08:00",
     * "lastUpdatedBy": "1054",
     * "versionNumber": 1,
     * "enabled": true,
     * "tableName": "hx_test_table",
     * "filterMethod": "TABLE_COLUMN",
     * "dataType": "COMPANY",
     * "columnName": "hxtest1",
     * "customSql": null,
     * "dataTypeName": "公司",
     * "filterMethodName": "表字段"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<DataAuthTableProperty>> getDataAuthTablePropertyByCond (
        @RequestParam(value = "tableName",required = false) String tableName,
        @RequestParam(value = "dataType",required = false) String dataType,
        @RequestParam(value = "filterMethod",required = false) String filterMethod,
        @RequestParam(value = "columnName",required = false) String columnName,
        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DataAuthTableProperty> list = dataAuthTablePropertyService.getDataAuthTablePropertyByCond(tableName,dataType, filterMethod,columnName, page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/data/auth/table/properties/query");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }
}
