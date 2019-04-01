package com.hand.hcf.app.expense.type.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeCategory;
import com.hand.hcf.app.expense.type.service.ExpenseTypeCategoryService;
import com.hand.hcf.app.expense.type.web.dto.SortBySequenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/5
 */
@RestController
@RequestMapping("/api/expense/types/category")
public class ExpenseTypeCategoryController {

    @Autowired
    private ExpenseTypeCategoryService service;

    /**
     * @api {GET} /api/expense/types/category 【费用大类】所有费用大类
     * @apiDescription 根据账套ID查询所有的费用大类
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId 账套ID
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/category?setOfBooksId=123
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * [{
     * "id": "1031484643240869889",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:14:37.663+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:14:37.663+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "name": "测试版"
     * }]
     */
    @GetMapping
    public ResponseEntity<List<ExpenseTypeCategory>> queryBySetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId){
        ExpenseTypeCategory expenseTypeCategory = new ExpenseTypeCategory(setOfBooksId);
        return ResponseEntity.ok(service.selectList(new EntityWrapper<>(expenseTypeCategory).orderBy("sequence",true)));
    }

    /**
     * @api {GET} /api/expense/types/category/query 【费用大类】 所有费用大类
     * @apiDescription 根据账套ID费用大类(含里面申请类别或者费用类别)
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId 账套ID
     * @apiParam (请求参数) {Integer} typeFlag 类型类别 0-申请 1-费用
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/category/query?setOfBooksId=123&typeFlag=0
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {List} expenseTypes 费用大类下的子类
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     *   [
     *   {
     *   "i18n": {
     *   "name": [
     *   {
     *   "language": "en_us",
     *   "value": "aaaa"
     *   },
     *   {
     *   "language": "zh_cn",
     *   "value": "bbb"
     *   }
     *   ]
     *   },
     *   "id": "1059677501352337410",
     *   "deleted": false,
     *   "createdDate": "2018-11-06T13:22:58.838+08:00",
     *   "createdBy": "1031",
     *   "lastUpdatedDate": "2018-11-06T13:22:58.839+08:00",
     *   "lastUpdatedBy": "1031",
     *   "versionNumber": 1,
     *   "enabled": true,
     *   "name": "bbb",
     *   "setOfBooksId": "1037906263432859649",
     *   "tenantId": "1022057230117146625",
     *   "sequence": 0,
     *   "expenseTypes": [
     *   {
     *   "i18n": null,
     *   "id": "1060011184605777922",
     *   "deleted": false,
     *   "createdDate": "2018-11-07T11:28:55.13+08:00",
     *   "createdBy": "1031",
     *   "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *   "lastUpdatedBy": "1031",
     *   "versionNumber": 1,
     *   "enabled": true,
     *   "name": "测试",
     *   "iconName": "meetings",
     *   "code": "test",
     *   "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *   "tenantId": "1022057230117146625",
     *   "setOfBooksId": "1037906263432859649",
     *   "sequence": 0,
     *   "typeCategoryId": "1059677501352337410",
     *   "typeFlag": 0,
     *   "entryMode": false,
     *   "attachmentFlag": null,
     *   "sourceTypeId": null,
     *   "priceUnit": null,
     *   "typeCategoryName": null
     *   }]
     *   }
     *   ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<ExpenseTypeCategory>> queryByCondition(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                      @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag){
        return ResponseEntity.ok(service.listResult(setOfBooksId, typeFlag));
    }


    /**
     *
     * @api {POST} /api/expense/types/category 【费用大类】创建大类
     * @apiDescription 创建一个费用大类
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {String} name 名称
     * @apiParam (请求参数) {Object} i18n 多语言相关信息
     * @apiParam (请求参数) {Boolean} enabled 是否启用
     * @apiParam (请求参数) {Long} setOfBooksId 账套ID
     * @apiParamExample {json} 请求报文:
     * {
     * 	"name": "测是",
     * 	"i18n": {
     * 		"name": [
     * 			{
     * 				"language": "zh_cn",
     * 				"value": "测是"
     * 			},
     * 			{
     * 				"language": "en_us",
     * 				"value": "sss"
     * 			}
     * 		]
     * 	},
     * 	"enabled": true,
     * 	"setOfBooksId": "1037906263432859649"
     * }
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping
    public ResponseEntity<Boolean> createTypeCategory(@RequestBody @Validated ExpenseTypeCategory expenseTypeCategory){
        return ResponseEntity.ok(service.createTypeCategory(expenseTypeCategory));
    }

    /**
     *
     * @api {PUT} /api/expense/types/category 【费用大类】修改大类
     * @apiDescription 更新一个费用大类的名称
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {String} name 名称
     * @apiParam (请求参数) {Object} i18n 多语言相关信息
     * @apiParam (请求参数) {Long} id  主键ID
     * @apiParamExample {json} 请求报文:
     * {
     *         "i18n": {
     *             "name": [
     *                 {
     *                     "language": "en_us",
     *                     "value": "aaaa"
     *                 },
     *                 {
     *                     "language": "zh_cn",
     *                     "value": "老头"
     *                 }
     *             ]
     *         },
     *         "id": "1059375502648578050",
     *
     *         "name": "老头"
     *     }
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PutMapping
    public ResponseEntity updateTypeCategory(@RequestBody ExpenseTypeCategory expenseTypeCategory){
        return ResponseEntity.ok(service.updateTypeCategory(expenseTypeCategory));
    }

    /**
     *
     * @api {DELETE} /api/expense/types/category 【费用大类】删除大类
     * @apiDescription 根据大类ID删除指定的费用大类
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id id
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/category?id=123
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @DeleteMapping
    public ResponseEntity deleteType(@RequestParam("id") Long id){
        return ResponseEntity.ok(service.deleteTypeCategory(id));
    }


    /**
     *
     * @api {POST} /api/expense/types/category/sort 【费用大类】排序
     * @apiDescription 修改费用大类的排序
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id id
     * @apiParam (请求参数) {Integer} sequence 序号
     * @apiParamExample {json} 请求报文:
     * [{"id":1059375502648578050,"sequence":1},{"id":1059374228964290562,"sequence":111}]
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping("/sort")
    public ResponseEntity<Boolean> sort(@RequestBody List<SortBySequenceDTO> list){

        return ResponseEntity.ok(service.sort(list));
    }
}
