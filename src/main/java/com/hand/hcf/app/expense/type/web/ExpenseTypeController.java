package com.hand.hcf.app.expense.type.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeIcon;
import com.hand.hcf.app.expense.type.service.ExpenseTypeIconService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeAssignInfoDTO;
import com.hand.hcf.app.expense.type.web.dto.SortBySequenceDTO;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
@RestController
@RequestMapping("/api/expense/types")
public class ExpenseTypeController {
    @Autowired
    private ExpenseTypeService service;
    @Autowired
    private ExpenseTypeIconService expenseTypeIconService;

    /**
     *
     * @api {POST} /api/expense/types 【费用/申请类别】创建类别
     * @apiDescription 创建一个费用或申请类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Object} i18n 多语言信息
     * @apiParam (请求参数) {Integer} sequence 序号
     * @apiParam (请求参数) {Long} setOfBooksId 账套ID
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Boolean} enabled 是否启用
     * @apiParam (请求参数) {String} name 名称
     * @apiParam (请求参数) {String} code 代码
     * @apiParam (请求参数) {String} iconName 图标名称
     * @apiParam (请求参数) {String} iconUrl 图标地址
     * @apiParam (请求参数) {Long} typeCategoryId 所属大类
     * @apiParam (请求参数) {Boolean} entryMode 是否使用单价
     * @apiParam (请求参数) {Integer} priceUnit 单价模式
     * @apiParam (请求参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiParam (请求参数) {Integer} attachmentFlag 附件模式
     * @apiParam (请求参数) {Long} sourceTypeId 申请类型
     * @apiParamExample {json} 请求报文:
     * {
     *     "i18n": {
     *         "name": [
     *             {
     *                 "language": "en_us",
     *                 "value": "test12"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "测试12"
     *             }
     *         ]
     *     },
     *     "enabled": true,
     *     "name": "测试12",
     *     "iconName": "meetings",
     *     "code": "test12",
     *     "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *     "tenantId": "1022057230117146625",
     *     "setOfBooksId": "1037906263432859649",
     *     "sequence": 0,
     *     "typeCategoryId": "1059677501352337410",
     *     "entryMode": false,
     *     "priceUnit": "1",
     *     "typeFlag": 0,
     *     "attachmentFlag": 1,
     *     "sourceTypeId":11222
     * }
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "i18n": {
     *         "name": [
     *             {
     *                 "language": "en_us",
     *                 "value": "test12"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "测试12"
     *             }
     *         ]
     *     },
     *     "id": "1065526794659414018",
     *     "deleted": false,
     *     "createdDate": "2018-11-22T16:45:59.035+08:00",
     *     "createdBy": "1059",
     *     "lastUpdatedDate": "2018-11-22T16:45:59.035+08:00",
     *     "lastUpdatedBy": "1059",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "name": "测试123",
     *     "iconName": "meetings",
     *     "code": "test123",
     *     "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *     "tenantId": "1034363055694327809",
     *     "setOfBooksId": "1037906263432859649",
     *     "sequence": 0,
     *     "typeCategoryId": "1059677501352337410",
     *     "typeFlag": 0,
     *     "entryMode": false,
     *     "attachmentFlag": null,
     *     "sourceTypeId": null,
     *     "priceUnit": null,
     *     "typeCategoryName": null
     * }
     */
    @PostMapping
    public ResponseEntity createType(@RequestBody @Validated ExpenseType dto){

        return ResponseEntity.ok(service.createType(dto));
    }

    /**
     *
     * @api {PUT} /api/expense/types 【费用/申请类别】修改类别
     * @apiDescription 修改一个费用或申请类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Object} i18n 多语言信息
     * @apiParam (请求参数) {Integer} sequence 序号
     * @apiParam (请求参数) {Long} setOfBooksId 账套ID
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Boolean} enabled 是否启用
     * @apiParam (请求参数) {String} name 名称
     * @apiParam (请求参数) {String} code 代码
     * @apiParam (请求参数) {String} iconName 图标名称
     * @apiParam (请求参数) {String} iconUrl 图标地址
     * @apiParam (请求参数) {Long} typeCategoryId 所属大类
     * @apiParam (请求参数) {Boolean} entryMode 是否使用单价
     * @apiParam (请求参数) {Integer} priceUnit 单价模式
     * @apiParam (请求参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiParam (请求参数) {Integer} attachmentFlag 附件模式
     * @apiParam (请求参数) {Long} sourceTypeId 申请类型
     * @apiParamExample {json} 请求报文:
     * {
     *     "i18n": {
     *         "name": [
     *             {
     *                 "language": "en_us",
     *                 "value": "test12"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "测试12"
     *             }
     *         ]
     *     },
     *     "id":1063320966439194625,
     *     "enabled": true,
     *     "name": "测试12",
     *     "iconName": "meetings",
     *     "code": "test12",
     *     "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *     "tenantId": "1022057230117146625",
     *     "setOfBooksId": "1037906263432859649",
     *     "sequence": 0,
     *     "typeCategoryId": "1059677501352337410",
     *     "entryMode": false,
     *     "priceUnit": "1",
     *     "typeFlag": 0,
     *     "attachmentFlag": 1,
     *     "sourceTypeId":11222
     * }
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "i18n": {
     *         "name": [
     *             {
     *                 "language": "en_us",
     *                 "value": "test12"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "测试12"
     *             }
     *         ]
     *     },
     *     "id": "1065526794659414018",
     *     "deleted": false,
     *     "createdDate": "2018-11-22T16:45:59.035+08:00",
     *     "createdBy": "1059",
     *     "lastUpdatedDate": "2018-11-22T16:45:59.035+08:00",
     *     "lastUpdatedBy": "1059",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "name": "测试123",
     *     "iconName": "meetings",
     *     "code": "test123",
     *     "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *     "tenantId": "1034363055694327809",
     *     "setOfBooksId": "1037906263432859649",
     *     "sequence": 0,
     *     "typeCategoryId": "1059677501352337410",
     *     "typeFlag": 0,
     *     "entryMode": false,
     *     "attachmentFlag": null,
     *     "sourceTypeId": null,
     *     "priceUnit": null,
     *     "typeCategoryName": null
     * }
     */
    @PutMapping
    public ResponseEntity updateType(@RequestBody @Validated ExpenseType dto){
        return ResponseEntity.ok(service.updateType(dto));
    }

    /**
     *
     * @api {DELETE} /api/expense/types/{id} 【费用/申请类别】删除类别
     * @apiDescription 删除一个费用或申请类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParamExample {url} 请求报文:
     *  /api/expense/types/122222
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteType(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.deleteTypeById(id));
    }

    /**
     *
     * @api {GET} /api/expense/types/select/{id} 【费用/申请类别】根据ID查询
     * @apiDescription 根据ID查询一个费用或申请类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id 主键ID

     * @apiParamExample {url} 请求报文:
     * /api/expense/types/select/12222223
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "i18n": {
     *         "name": [
     *             {
     *                 "language": "en_us",
     *                 "value": "test12"
     *             },
     *             {
     *                 "language": "zh_cn",
     *                 "value": "测试12"
     *             }
     *         ]
     *     },
     *     "id": "1065526794659414018",
     *     "deleted": false,
     *     "createdDate": "2018-11-22T16:45:59.035+08:00",
     *     "createdBy": "1059",
     *     "lastUpdatedDate": "2018-11-22T16:45:59.035+08:00",
     *     "lastUpdatedBy": "1059",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "name": "测试123",
     *     "iconName": "meetings",
     *     "code": "test123",
     *     "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *     "tenantId": "1034363055694327809",
     *     "setOfBooksId": "1037906263432859649",
     *     "sequence": 0,
     *     "typeCategoryId": "1059677501352337410",
     *     "typeFlag": 0,
     *     "entryMode": false,
     *     "attachmentFlag": null,
     *     "sourceTypeId": null,
     *     "priceUnit": null,
     *     "typeCategoryName": null
     * }
     */
    @GetMapping("/select/{id}")
    public ResponseEntity<ExpenseType> queryById(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.getTypeById(id));
    }

    /**
     *
     * @api {GET} /api/expense/types/icon 【费用/申请类别】查询图标
     * @apiDescription 查询所有的费用图标
     * @apiGroup ExpenseService
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {uuid} expenseTypeIconOid  图标OID
     * @apiSuccess (返回参数) {Long} attachmentId  附件ID
     * @apiSuccess (返回参数) {String} iconName  图标名称
     * @apiSuccess (返回参数) {String} iconURL  图标URL
     * @apiSuccess (返回参数) {int} sequence  排序
     * @apiSuccess (返回参数) {Boolean} deleted  是否删除
     * @apiSuccess (返回参数) {Boolean} enabled  是否启用
     * @apiSuccess (返回参数) {String} string1  备用1
     * @apiSuccess (返回参数) {String} string1  备用2
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "id": "1",
     *         "expenseTypeIconOid": "a92f34f7-a66c-4b67-bea8-84f823d8714b",
     *         "attachmentId": 19990,
     *         "iconName": "airTickets",
     *         "iconURL": "http://115.159.108.80:25296/upload/expenseIcon/191a75a0-1d79-4147-b41a-c147d60ff879-airTickets.png",
     *         "deleted": false,
     *         "enabled": true,
     *         "sequence": null,
     *         "string1": null,
     *         "string2": null
     *     }]
     */
    @GetMapping("/icon")
    public ResponseEntity<List<ExpenseTypeIcon>> queryIcon(){
        return ResponseEntity.ok(expenseTypeIconService.selectList(new EntityWrapper<ExpenseTypeIcon>().eq("enabled",true)));
    }


    /**
     *
     * @api {POST} /api/expense/types/{expenseTypeId}/fields 【费用/申请类别】保存控件
     * @apiDescription 保存控件
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} expenseTypeId 类型ID
     * @apiParam (请求参数) {String} name 名称
     * @apiParam (请求参数) {String} fieldType 控件类型
     * @apiParam (请求参数) {int} sequence 排序
     * @apiParam (请求参数) {Boolean} enabled 是否启用
     * @apiParam (请求参数) {int} limitCount
     * @apiParam (请求参数) {String} type 模式
     * @apiParam (请求参数) {Object} i18n 名称多语言信息
     * @apiParam (请求参数) {Boolean} editable 是否可以编辑
     * @apiParam (请求参数) {Boolean} showOnList 是否展示
     * @apiParamExample {json} 请求报文:
     * [
     * 	{
     * 		"name": "文本",
     * 		"fieldType": "TEXT",
     * 		"sequence": 0,
     * 		"enabled": true,
     * 		"limitCount": 6,
     * 		"type": "CUSTOM",
     * 		"counterFlag": 0,
     * 		"i18n": {
     * 			"name": [
     * 				{
     * 					"language": "zh_cn",
     * 					"value": "文本"
     * 				},
     * 				{
     * 					"language": "en_us",
     * 					"value": "文本"
     * 				}
     * 			]
     * 		},
     * 		"editable": true,
     * 		"showOnList": true
     * 	}]
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping(value = "/{expenseTypeId}/fields")
    public ResponseEntity<Boolean> saveExpenseTypeFields(@PathVariable(value = "expenseTypeId")Long expenseTypeId,
                                                         @RequestBody List<ExpenseFieldDTO> fieldDTOS){
        service.saveExpenseTypeFields(expenseTypeId, fieldDTOS);
        return ResponseEntity.ok(true);
    }


    /**
     *
     * @api {DELETE} /api/expense/types/{expenseTypeId}/{fieldOid} 【费用/申请类别】删除控件
     * @apiDescription 根据控件OID删除控件
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} expenseTypeId 类型ID
     * @apiParam (请求参数) {UUID} fieldOid 控件OID
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/12333/121212-1212121-1212
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @DeleteMapping("/{expenseTypeId}/{fieldOid}")
    public ResponseEntity<Boolean> deleteExpenseTypeField(@PathVariable(value = "expenseTypeId")Long expenseTypeId,
                                                          @PathVariable(value = "fieldOid") UUID fieldOid){
        service.deleteFieldByOid(expenseTypeId, fieldOid);
        return ResponseEntity.ok(true);
    }

    /**
     *
     * @api {GET} /api/expense/types/{expenseTypeId}/fields 【费用/申请类别】查询控件
     * @apiDescription 查询控件
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} expenseTypeId 类型ID
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/1222/fields
     * @apiSuccessExample {json} 返回报文:
     *[
     *     {
     *     "id": "1063348875203792897",
     *     "fieldType": "TEXT",
     *     "fieldDataType": "TEXT",
     *     "name": "文本",
     *     "value": null,
     *     "codeName": null,
     *     "messageKey": null,
     *     "sequence": 0,
     *     "customEnumerationOID": null,
     *     "mappedColumnId": 112,
     *     "printHide": false,
     *     "required": false,
     *     "showOnList": true,
     *     "fieldOID": "86b42265-303f-4d42-916f-a99c459c8d95",
     *     "editable": true,
     *     "defaultValueMode": "CURRENT",
     *     "defaultValueKey": null,
     *     "showValue": null,
     *     "defaultValueConfigurable": true,
     *     "commonField": false,
     *     "reportKey": null,
     *     "i18n": {
     *     "name": [
     *     {
     *     "language": "en_us",
     *     "value": "文本"
     *     },
     *     {
     *     "language": "zh_cn",
     *     "value": "文本"
     *     }
     *     ]
     *     }
     *     }]
     */
    @GetMapping("/{expenseTypeId}/fields")
    public ResponseEntity<List<ExpenseFieldDTO>> queryFields(@PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.queryFields(expenseTypeId));
    }

    /**
     *
     * @api {POST} /api/expense/types/{expenseTypeId}/assign 【费用/申请类别】保存权限
     * @apiDescription 保存权限
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Boolean} allCompanyFlag 是否全部公司
     * @apiParam (请求参数) {Long} expenseTypeId  类别ID
     * @apiParam (请求参数) {String} applyType  人员类型  101-全部 102-部门 103-人员组
     * @apiParam (请求参数) {Object} assignCompanies 公司信息
     * @apiParam (请求参数) {Object} assignUsers 人员信息
     * @apiParamExample {json} 请求报文:
     * {
     * 	"assignCompanies": [
     *     {
     *         "companyId": "1005"
     *     },
     *     {
     *         "companyId": "1014"
     *     },
     *     {
     *         "companyId": "1016"
     *     }
     * ]
     * ,
     * "assignUsers":[
     *     {
     *         "userTypeId": "1005"
     *     },
     *     {
     *         "userTypeId": "1032"
     *     },
     *     {
     *         "userTypeId": "1055"
     *     }
     * ],
     * "allCompanyFlag": false,
     * "applyType": "101"
     * }
     *
     * @apiSuccess (返回参数) {Boolean} allCompanyFlag 是否全部公司
     * @apiSuccess (返回参数) {String} applyType  人员类型  101-全部 102-部门 103-人员组
     * @apiSuccess (返回参数) {Object} assignCompanies 公司信息
     * @apiSuccess (返回参数) {Object} assignUsers 人员信息
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     *{
     * 	"assignCompanies": [
     *     {
     *         "companyId": "1005"
     *     },
     *     {
     *         "companyId": "1014"
     *     },
     *     {
     *         "companyId": "1016"
     *     }
     * ]
     * ,
     * "assignUsers":[
     *     {
     *         "userTypeId": "1005"
     *     },
     *     {
     *         "userTypeId": "1032"
     *     },
     *     {
     *         "userTypeId": "1055"
     *     }
     * ],
     * "allCompanyFlag": false,
     * "applyType": "101"
     * }
     */
    @PostMapping("/{expenseTypeId}/assign")
    public ResponseEntity<ExpenseTypeAssignInfoDTO> saveAssignInfo(@RequestBody ExpenseTypeAssignInfoDTO infoDTO,
                                                                   @PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.saveAssignInfo(infoDTO, expenseTypeId));
    }

    /**
     *
     * @api {GET} /api/expense/types/{expenseTypeId}/assign/query 【费用/申请类别】查询权限
     * @apiDescription 查询权限
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} expenseTypeId  类别ID
     * @apiParamExample {url} 请求报文:
     * /api/expense/types/12333/assign/query
     *
     * @apiSuccess (返回参数) {Boolean} allCompanyFlag 是否全部公司
     * @apiSuccess (返回参数) {String} applyType  人员类型  101-全部 102-部门 103-人员组
     * @apiSuccess (返回参数) {Object} assignCompanies 公司信息
     * @apiSuccess (返回参数) {Object} assignUsers 人员信息
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     *{
     * 	"assignCompanies": [
     *     {
     *         "companyId": "1005"
     *     },
     *     {
     *         "companyId": "1014"
     *     },
     *     {
     *         "companyId": "1016"
     *     }
     * ]
     * ,
     * "assignUsers":[
     *     {
     *         "userTypeId": "1005"
     *     },
     *     {
     *         "userTypeId": "1032"
     *     },
     *     {
     *         "userTypeId": "1055"
     *     }
     * ],
     * "allCompanyFlag": false,
     * "applyType": "101"
     * }
     */
    @GetMapping("/{expenseTypeId}/assign/query")
    public ResponseEntity<ExpenseTypeAssignInfoDTO> queryAssign(@PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.queryAssign(expenseTypeId));

    }

    /**
     *
     * @api {GET} /api/expense/types/{setOfBooksId}/query 【费用/申请类别】条件查询
     * @apiDescription 条件查询类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId  账套ID
     * @apiParam (请求参数) {String} code  代码
     * @apiParam (请求参数) {String} name  名称
     * @apiParam (请求参数) {int} typeFlag  类别类型 0-申请 1-费用
     * @apiParam (请求参数) {Long} typeCategoryId 所属大类
     * @apiParam (请求参数) {int} page 页数
     * @apiParam (请求参数) {int} size 每页大小
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1060011184605777922",
     *         "deleted": false,
     *         "createdDate": "2018-11-07T11:28:55.13+08:00",
     *         "createdBy": "1031",
     *         "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *         "lastUpdatedBy": "1031",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "name": "测试",
     *         "iconName": "meetings",
     *         "code": "test",
     *         "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *         "tenantId": "1022057230117146625",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1059677501352337410",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "priceUnit": null,
     *         "typeCategoryName": "aaa"
     *     }]
     */
    @GetMapping("/{setOfBooksId}/query")
    public ResponseEntity<List<ExpenseType>> queryByCondition(@PathVariable("setOfBooksId") Long setOfBooksId,
                                                              @RequestParam(value = "code", required = false) String code,
                                                              @RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag,
                                                              @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                              @RequestParam(value ="enabled",required = false,defaultValue = "true") Boolean enabled,
                                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> expenseTypes = service.queryByCondition(page, setOfBooksId, code, name, typeCategoryId, typeFlag,enabled);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/" + setOfBooksId + "/query");
        return new ResponseEntity<>(expenseTypes, httpHeaders, HttpStatus.OK);
    }

    /**
     *
     * @api {GET} /api/expense/types/query/by/category 【费用/申请类别】申请类型查询
     * @apiDescription 根据大类查询该大类下的申请类型
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} typeCategoryId  大类Id
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1060011184605777922",
     *         "deleted": false,
     *         "createdDate": "2018-11-07T11:28:55.13+08:00",
     *         "createdBy": "1031",
     *         "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *         "lastUpdatedBy": "1031",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "name": "测试",
     *         "iconName": "meetings",
     *         "code": "test",
     *         "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *         "tenantId": "1022057230117146625",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1059677501352337410",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "priceUnit": null,
     *         "typeCategoryName": "aaa"
     *     }]
     */
    @GetMapping("/query/by/category")
    public ResponseEntity queryByCategoryId(@RequestParam("typeCategoryId") Long typeCategoryId){

        return ResponseEntity.ok(service.queryByCategoryId(typeCategoryId, 0));
    }

    /**
     *
     * @api {POST} /api/expense/types/sort 【费用/申请类别】排序
     * @apiDescription 申请类型或者费用类型排序
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id  主键Id
     * @apiParam (请求参数) {Integer} sequence  序号
     * @apiParamExample {json} 请求报文:
     * [{122222,1},{12121212,33}]
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping("/sort")
    public ResponseEntity sort(@RequestBody List<SortBySequenceDTO> dtos){

        return ResponseEntity.ok(service.sort(dtos));
    }

    /**
     *
     * @api {GET} /api/expense/types/query/by/document/assign  【费用/申请类别】lov查询
     * @apiDescription 单据类型关联费用类型LOV查询
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId  账套ID
     * @apiParam (请求参数) {String} range  范围 all-全部 selected-已选 其他为未选
     * @apiParam (请求参数) {Integer} documentType  单据大类
     * @apiParam (请求参数) {Long} typeCategoryId 费用大类ID
     * @apiParam (请求参数) {Integer} typeFlag  0-申请类型 1-费用类型
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {String} typeCategoryName 所属大类名称
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1060011184605777922",
     *         "deleted": false,
     *         "createdDate": "2018-11-07T11:28:55.13+08:00",
     *         "createdBy": "1031",
     *         "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *         "lastUpdatedBy": "1031",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "name": "测试",
     *         "iconName": "meetings",
     *         "code": "test",
     *         "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *         "tenantId": "1022057230117146625",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1059677501352337410",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "priceUnit": null,
     *         "typeCategoryName": "aaa"
     *     }]
     */
    @GetMapping("/query/by/document/assign")
    public ResponseEntity queryLovByDocumentTypeAssign(@RequestParam("setOfBooksId") Long setOfBooksId,
                                              @RequestParam("range") String range,
                                              @RequestParam("documentType") Integer documentType,
                                              @RequestParam(value = "id", required = false) Long documentTypeId,
                                              @RequestParam(value = "code",required = false) String code,
                                              @RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "typeCategoryId",required = false) Long typeCategoryId,
                                              @RequestParam("typeFlag") Integer typeFlag,
                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> list = service.queryLovByDocumentTypeAssign(setOfBooksId, range, documentType, documentTypeId, code, name,typeCategoryId,typeFlag, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/query/by/adjust/assign");
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }

    /**
     *
     * @api {GET} /api/expense/types/lov/query 【费用/申请类别】条件查询(弹框用到的)
     * @apiDescription 条件查询类别
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId  账套ID
     * @apiParam (请求参数) {String} code  代码
     * @apiParam (请求参数) {String} name  名称
     * @apiParam (请求参数) {int} typeFlag  类别类型 0-申请 1-费用
     * @apiParam (请求参数) {Long} typeCategoryId 所属大类
     * @apiParam (请求参数) {int} page 页数
     * @apiParam (请求参数) {int} size 每页大小
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1060011184605777922",
     *         "deleted": false,
     *         "createdDate": "2018-11-07T11:28:55.13+08:00",
     *         "createdBy": "1031",
     *         "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *         "lastUpdatedBy": "1031",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "name": "测试",
     *         "iconName": "meetings",
     *         "code": "test",
     *         "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *         "tenantId": "1022057230117146625",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1059677501352337410",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "priceUnit": null,
     *         "typeCategoryName": "aaa"
     *     }]
     */
    @GetMapping("/chooser/query")
    public ResponseEntity<List<ExpenseType>> queryLovByCondition(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                              @RequestParam(value = "code", required = false) String code,
                                                              @RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag,
                                                              @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                              @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> expenseTypes = service.queryByCondition(page, setOfBooksId, code, name, typeCategoryId, typeFlag, enabled);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/chooser/query");
        return new ResponseEntity<>(expenseTypes, httpHeaders, HttpStatus.OK);
    }
}
