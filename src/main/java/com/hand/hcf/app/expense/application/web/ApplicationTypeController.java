package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignCompany;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeDimension;
import com.hand.hcf.app.expense.application.service.ApplicationTypeService;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;


/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@RestController
@RequestMapping("/api/expense/application/type")
public class ApplicationTypeController {

    @Autowired
    private ApplicationTypeService service;

    /**
     *
     * @api {POST} /api/expense/application/type 【申请单类型】创建
     * @apiDescription 创建一个申请单类型
     * @apiGroup ExpenseService
     * @apiParam (请求对象) {Object} applicationType  申请单类型对象
     * @apiParam (请求对象) {Object} expenseTypeInfos  分配的申请类型对象
     * @apiParam (请求对象) {Object} userInfos  分配的适用人员对象
     * @apiParam (applicationType对象) {Long} setOfBooksId  账套Id
     * @apiParam (applicationType对象) {Boolean} enabled  是否启用
     * @apiParam (applicationType对象) {String} typeCode  代码
     * @apiParam (applicationType对象) {String} typeName  类型名称
     * @apiParam (applicationType对象) {String} formOid  关联表单Oid
     * @apiParam (applicationType对象) {Integer} formType  单据大类 默认为 801009
     * @apiParam (applicationType对象) {Boolean} budgetFlag  是否预算管控
     * @apiParam (applicationType对象) {Boolean} allFlag  是否关联全部申请类型
     * @apiParam (applicationType对象) {Boolean} requireInput  合同是否必输
     * @apiParam (applicationType对象) {Boolean} associateContract  是否关联合同
     * @apiParam (applicationType对象) {String} applyEmployee  '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiParam (expenseTypeInfos对象) {Long} [applicationTypeId]  申请单类型Id
     * @apiParam (expenseTypeInfos对象) {Long} expenseTypeId  申请类型Id
     * @apiParam (userInfos对象) {Long} applyType  适用人员 '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiParam (userInfos对象) {Long} userTypeId  部门/人员组Id
     * @apiParam (userInfos对象) {Long} [applicationTypeId]  申请单类型Id
     * @apiParamExample {json} 请求报文:
     * {
     *     "applicationType": {
     *         "enabled": true,
     *         "typeCode": "test",
     *         "typeName": "1111",
     *         "setOfBooksId": "123456",
     *         "tenantId": "1022057230117146625",
     *         "formOid": "196b2caf-2e60-4c2a-b3f6-64fed636bfcc",
     *         "formName": "1223",
     *         "formType": 801009,
     *         "budgetFlag": false,
     *         "allFlag": true,
     *         "associateContract": false,
     *         "requireInput": false,
     *         "applyEmployee": "101"
     *     },
     *     "userInfos": [],
     *     "expenseTypeInfos": []
     * }
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping
    public ResponseEntity createApplicationType(@RequestBody @Validated ApplicationTypeDTO dto){
        return ResponseEntity.ok(service.createApplicationType(dto));
    }


    /**
     * @api {PUT} /api/expense/application/type/query/{id} 【申请单类型】id查询
     * @apiDescription  根据id查询申请单类型（编辑时查询)
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} id  申请单类型id
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/123333333
     * @apiSuccess (返回对象) {Object} applicationType 申请单类型对象
     * @apiSuccess (返回对象) {Object} expenseTypeInfos 分配的申请类型对象
     * @apiSuccess (返回对象) {Object} userInfos 分配的适用人员对象
     * @apiSuccess (applicationType对象) {Long} setOfBooksId  账套Id
     * @apiSuccess (applicationType对象) {Long} id  申请单类型Id
     * @apiSuccess (applicationType对象) {Boolean} enabled  是否启用
     * @apiSuccess (applicationType对象) {String} typeCode  代码
     * @apiSuccess (applicationType对象) {String} typeName  类型名称
     * @apiSuccess (applicationType对象) {String} formOid  关联表单Oid
     * @apiSuccess (applicationType对象) {Integer} formType  单据大类 默认为 801009
     * @apiSuccess (applicationType对象) {String} formName  关联表单名称
     * @apiSuccess (applicationType对象) {Boolean} budgetFlag  是否预算管控
     * @apiSuccess (applicationType对象) {Boolean} allFlag  是否关联全部申请类型
     * @apiSuccess (applicationType对象) {Boolean} requireInput  合同是否必输
     * @apiSuccess (applicationType对象) {Boolean} associateContract  是否关联合同
     * @apiSuccess (applicationType对象) {String} applyEmployee  '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiSuccess (expenseTypeInfos对象) {Long} applicationTypeId  申请单类型Id
     * @apiSuccess (expenseTypeInfos对象) {Long} expenseTypeId  申请类型Id
     * @apiSuccess (userInfos对象) {Long} applyType  适用人员 '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiSuccess (userInfos对象) {Long} userTypeId  部门/人员组Id
     * @apiSuccess (userInfos对象) {String} pathOrName  部门/人员组名称
     * @apiSuccess (userInfos对象) {Long} applicationTypeId  申请单类型Id
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "applicationType": {
     *         "id": "1060457723043524609",
     *         "createdDate": "2018-11-08T17:03:18.182+08:00",
     *         "createdBy": "1036",
     *         "lastUpdatedDate": "2018-11-08T17:03:18.182+08:00",
     *         "lastUpdatedBy": "1036",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "typeCode": "test",
     *         "typeName": "1111",
     *         "setOfBooksId": "1037906263432859649",
     *         "setOfBooksName": null,
     *         "tenantId": "1022057230117146625",
     *         "formOid": "2489af52-4548-4207-a852-bbbacc9dddca",
     *         "formName": "测试abc",
     *         "formType": 801009,
     *         "budgetFlag": true,
     *         "allFlag": true,
     *         "associateContract": false,
     *         "requireInput": false,
     *         "applyEmployee": "101"
     *     },
     *     "userInfos": [],
     *     "expenseTypeInfos": []
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity getTypeForUpdate(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.getTypeForUpdate(id));
    }

    /**
     *
     * @api {PUT} /api/expense/application/type 【申请单类型】更新
     * @apiDescription  更新一个申请单类型
     * @apiGroup ExpenseService
     * @apiParam (请求对象) {Object} applicationType  申请单类型对象
     * @apiParam (请求对象) {Object} expenseTypeInfos  分配的申请类型对象
     * @apiParam (请求对象) {Object} userInfos  分配的适用人员对象
     * @apiParam (applicationType对象) {Long} setOfBooksId  账套Id
     * @apiParam (applicationType对象) {Long} id  申请单类型Id
     * @apiParam (applicationType对象) {Boolean} enabled  是否启用
     * @apiParam (applicationType对象) {String} typeCode  代码
     * @apiParam (applicationType对象) {String} typeName  类型名称
     * @apiParam (applicationType对象) {String} formOid  关联表单Oid
     * @apiParam (applicationType对象) {Integer} formType  单据大类 默认为 801009
     * @apiParam (applicationType对象) {Boolean} budgetFlag  是否预算管控
     * @apiParam (applicationType对象) {Boolean} allFlag  是否关联全部申请类型
     * @apiParam (applicationType对象) {Boolean} requireInput  合同是否必输
     * @apiParam (applicationType对象) {Boolean} associateContract  是否关联合同
     * @apiParam (applicationType对象) {String} applyEmployee  '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiParam (expenseTypeInfos对象) {Long} [applicationTypeId]  申请单类型Id
     * @apiParam (expenseTypeInfos对象) {Long} expenseTypeId  申请类型Id
     * @apiParam (userInfos对象) {Long} applyType  适用人员 '101'-全部人员 '102'-按部门 '103'-按人员组
     * @apiParam (userInfos对象) {Long} userTypeId  部门/人员组Id
     * @apiParam (userInfos对象) {Long} [applicationTypeId]  申请单类型Id
     * @apiParamExample {json} 请求报文:
     * {
     *     "applicationType": {
     *         "id":1223232313,
     *         "enabled": true,
     *         "typeCode": "test",
     *         "typeName": "1111",
     *         "setOfBooksId": "123456",
     *         "tenantId": "1022057230117146625",
     *         "formOid": "196b2caf-2e60-4c2a-b3f6-64fed636bfcc",
     *         "formName": "1223",
     *         "formType": 801009,
     *         "budgetFlag": false,
     *         "allFlag": true,
     *         "associateContract": false,
     *         "requireInput": false,
     *         "applyEmployee": "101"
     *     },
     *     "userInfos": [],
     *     "expenseTypeInfos": []
     * }
     *
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PutMapping
    public ResponseEntity updateApplicationType(@RequestBody @Validated ApplicationTypeDTO dto){
        return ResponseEntity.ok(service.updateApplicationType(dto));
    }


    /**
     * @api {GET} /api/expense/application/type/query 【申请单类型】条件查询
     * @apiDescription  界面分页可以根据条件查询
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} setOfBooksId  账套Id
     * @apiParam (请求参数) {String} [typeCode]  申请单类型代码
     * @apiParam (请求参数) {String} [typeName]  申请单类型名称
     * @apiParam (请求参数) {Boolean} [enabled]  是否启用
     * @apiParam (请求参数) {Integer} [page]  当前页数从0开始 默认 0
     * @apiParam (请求参数) {Boolean} [size]  每页大小 默认20
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query?page=0&size=10&setOfBooksId=12544445&typeCode=&typeName=&enabled=
     * @apiSuccess (返回对象) {Long} setOfBooksId  账套Id
     * @apiSuccess (返回对象) {String} setOfBooksName  账套名称
     * @apiSuccess (返回对象) {Long} id  申请单类型Id
     * @apiSuccess (返回对象) {Boolean} enabled  是否启用
     * @apiSuccess (返回对象) {String} typeCode  代码
     * @apiSuccess (返回对象) {String} typeName  类型名称
     * @apiSuccess (返回对象) {String} formOid  关联表单Oid
     * @apiSuccess (返回对象) {Integer} formType  单据大类 默认为 801009
     * @apiSuccess (返回对象) {String} formName  关联表单名称
     * @apiSuccess (返回对象) {Boolean} budgetFlag  是否预算管控
     * @apiSuccess (返回对象) {Boolean} allFlag  是否关联全部申请类型
     * @apiSuccess (返回对象) {Boolean} requireInput  合同是否必输
     * @apiSuccess (返回对象) {Boolean} associateContract  是否关联合同
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "id": "1076017251497086977",
     *         "enabled": true,
     *         "typeCode": "123456",
     *         "typeName": "654321",
     *         "setOfBooksId": "1037906263432859649",
     *         "setOfBooksName": "PANNGPANG_SOB-胖成一个大熊猫账套",
     *         "tenantId": "1034363055694327809",
     *         "formOid": "2489af52-4548-4207-a852-bbbacc9dddca",
     *         "formName": null,
     *         "formType": null,
     *         "budgetFlag": false,
     *         "allFlag": false,
     *         "associateContract": true,
     *         "requireInput": true,
     *         "applyEmployee": "101"
     *     }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<ApplicationType>> queryByCondition(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                  @RequestParam(value = "typeCode", required = false) String typeCode,
                                                                  @RequestParam(value = "typeName", required = false) String typeName,
                                                                  @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ApplicationType> list = service.queryByCondition(setOfBooksId, typeCode, typeName,enabled, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }



    /**
     * @api {POST} /api/expense/application/type/{applicationTypeId}/assign/company 【申请单类型】分配公司
     * @apiDescription  批量分配公司
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} applicationTypeId  申请单类型Id
     * @apiParam (请求参数) {Long} id 公司Id集合
     * @apiParamExample {json} 请求报文:
     * [1252,156,226,555]
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping("/{applicationTypeId}/assign/company")
    public ResponseEntity<Boolean> assignCompanies(@RequestBody List<Long> ids,
                                                   @PathVariable("applicationTypeId") Long applicationTypeId){


        return ResponseEntity.ok(service.assignCompanies(ids, applicationTypeId));

    }

    /**
     * @api {PUT} /api/expense/application/type/assign/company 【申请单类型】更改公司状态
     * @apiDescription  更改已经分配的公司启用状态
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} id  主键Id，分配公司表Id
     * @apiParam (请求参数) {Boolean} enabled 是否启用
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     *     "id":1122,
     *     "enabled":true,
     *     "versionNumber":1
     * }
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PutMapping("/assign/company")
    public ResponseEntity<Boolean> updateCompanyEnabled(@RequestBody ApplicationTypeAssignCompany company){
        return ResponseEntity.ok(service.updateCompanyEnabled(company));
    }

    /**
     * @api {GET} /api/expense/application/type/{applicationTypeId}/company/query 【申请单类型】已分配公司查询
     * @apiDescription  分页查询已经分配了的公司
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} applicationTypeId  申请单类型Id
     * @apiParam (请求参数) {Integer} [page] 当前页 从0开始
     * @apiParam (请求参数) {Integer} [size] 每页大小
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/1063006185823985665/company/query?page=0&size=10
     * @apiSuccess (返回对象) {Long} id 主键Id
     * @apiSuccess (返回对象) {Long} companyId 公司Id
     * @apiSuccess (返回对象) {Long} companyName 公司名称
     * @apiSuccess (返回对象) {Long} applicationTypeId 申请单类型Id
     * @apiSuccess (返回对象) {Boolean} enabled 是否启用
     * @apiSuccess (返回对象) {Integer} versionNumber 版本号
     * @apiSuccess (返回对象) {String} companyCode 公司代码
     * @apiSuccess (返回对象) {String} companyTypeName 公司类型名称
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1063006185823985665",
     *         "createdDate": "2018-11-15T17:49:59.062+08:00",
     *         "createdBy": "1059",
     *         "lastUpdatedDate": "2018-11-15T18:09:05.318+08:00",
     *         "lastUpdatedBy": "1059",
     *         "versionNumber": 2,
     *         "enabled": false,
     *         "companyId": "1024",
     *         "applicationTypeId": "1060457723043524609",
     *         "companyName": "胖成一个大熊猫公司",
     *         "companyTypeName": "业务实体",
     *         "companyCode": "PP10001"
     *     }
     * ]
     */
    @GetMapping("/{applicationTypeId}/company/query")
    public ResponseEntity<List<ApplicationTypeAssignCompany>> queryAssignCompanies(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                                                   Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ApplicationTypeAssignCompany> companies = service.queryAssignCompanies(applicationTypeId, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(companies.getRecords(), httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {GET} /api/expense/application/type/{applicationTypeId}/company/query/filter 【申请单类型】未分配公司查询
     * @apiDescription  分页查询申请单类型尚未分配的公司信息
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} applicationTypeId  申请单类型Id
     * @apiParam (请求参数) {Integer} [page] 当前页 从0开始
     * @apiParam (请求参数) {Integer} [size] 每页大小
     * @apiParam (请求参数) {String} [companyCode] 公司代码
     * @apiParam (请求参数) {String} [companyCodeFrom] 公司代码从
     * @apiParam (请求参数) {String} [companyCodeTo] 公司代码至
     * @apiParam (请求参数) {String} [companyName] 公司名称
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/1063006185823985665/company/query/filter?page=0&size=10&companyCode=&companyName=
     * @apiSuccess (返回对象) {Long} id 公司ID
     * @apiSuccess (返回对象) {String} name 公司名称
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1029",
     *         "name": "胖成一个小熊猫"
     *     }
     *     ]
     */
    @GetMapping("/{applicationTypeId}/company/query/filter")
    public ResponseEntity getCompanyByConditionFilter(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                @RequestParam(required = false) String companyCode,
                                                @RequestParam(required = false) String companyName,
                                                @RequestParam(required = false) String companyCodeFrom,
                                                @RequestParam(required = false) String companyCodeTo,
                                                Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = service.getCompanyByConditionFilter(applicationTypeId, companyCode,
                companyName, companyCodeFrom, companyCodeTo, page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/expense/application/type/"+ applicationTypeId + "/company/query/filter");

        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/expense/application/type/{applicationTypeId}/assign/dimension 【申请单类型】保存维度
     * @apiDescription  分页查询申请单类型尚未分配的公司信息
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} applicationTypeId  申请单类型Id
     * @apiParam (请求参数) {Long} [id] 主键Id，新增为null, 更新时必输
     * @apiParam (请求参数) {Long} dimensionId  维度Id
     * @apiParam (请求参数) {Boolean} headerFlag  是否在单据头信息上
     * @apiParam (请求参数) {Integer} sequence  排序
     * @apiParam (请求参数) {Long} defaultValue 默认值
     * @apiParamExample {json} 请求报文:
     * [
     * 	{
     * 		"dimensionId":1031,
     * 		"headerFlag":true,
     * 		"sequence":10,
     * 		"defaultValue":1035
     * 	},
     * 	{
     * 		"dimensionId":1033,
     * 		"headerFlag":true,
     * 		"sequence":20,
     * 		"defaultValue":1038
     * 	}
     * 	]

     * @apiSuccessExample {json} 返回报文:
     *  true
     */
    @PostMapping("/{applicationTypeId}/assign/dimension")
    public ResponseEntity assignDimensions(@RequestBody List<ApplicationTypeDimension> dimensions,
                                           @PathVariable("applicationTypeId") Long applicationTypeId){

        return ResponseEntity.ok(service.assignDimensions(applicationTypeId, dimensions));
    }


    /**
     * @api {GET} /api/expense/application/type/{applicationTypeId}/dimension/query 【申请单类型】已添加维度查询
     * @apiDescription  分页查询已经分配的维度信息
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} applicationTypeId  申请单类型Id
     * @apiParam (请求参数) {Integer} [page] 当前页 从0开始
     * @apiParam (请求参数) {Integer} [size] 每页大小
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/1060457723043524609/dimension/query
     * @apiSuccess (返回参数) {Long} id 主键Id
     * @apiSuccess (返回参数) {Long} typeId 申请单类型Id
     * @apiSuccess (返回参数) {Long} dimensionId 维度Id
     * @apiSuccess (返回参数) {String} dimensionName 维度名称
     * @apiSuccess (返回参数) {Integer} sequence 排序号
     * @apiSuccess (返回参数) {Long} defaultValue 默认值
     * @apiSuccess (返回参数) {String} valueName 默认值描述
     * @apiSuccess (返回参数) {Boolean} headerFlag 是否在单据头信息上
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1064781613686026241",
     *         "versionNumber": 1,
     *         "typeId": "1060457723043524609",
     *         "dimensionId": "1032",
     *         "dimensionName": "区域",
     *         "sequence": 10,
     *         "defaultValue": "1032",
     *         "valueName": "区域004",
     *         "headerFlag": true
     *     }
     *     ]
     */
    @GetMapping("/{applicationTypeId}/dimension/query")
    public ResponseEntity<List<ApplicationTypeDimension>> queryDimension(@PathVariable("applicationTypeId") Long applicationTypeId,
                                         Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ApplicationTypeDimension> result = service.queryDimension(applicationTypeId,  page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * @api {DELETE} /api/expense/application/type/dimension/{id} 【申请单类型】删除维度
     * @apiDescription  根据id删除申请单类型分配了的维度信息
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} id  维度分配表主键Id
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/dimension/1064781613686026241
     * @apiSuccess (返回参数) {Long} id 主键Id
     * @apiSuccess (返回参数) {Long} typeId 申请单类型Id
     * @apiSuccess (返回参数) {Long} dimensionId 维度Id
     * @apiSuccess (返回参数) {String} dimensionName 维度名称
     * @apiSuccess (返回参数) {Integer} sequence 排序号
     * @apiSuccess (返回参数) {Long} defaultValue 默认值
     * @apiSuccess (返回参数) {String} valueName 默认值描述
     * @apiSuccess (返回参数) {Boolean} headerFlag 是否在单据头信息上
     * @apiSuccessExample {json} 返回报文:
     *  true
     */
    @DeleteMapping("/dimension/{id}")
    public ResponseEntity deleteDimension(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.deleteDimension(id));
    }

    /**
     * @api {GET} /api/expense/application/type/query/all 【申请单类型】所有查询
     * @apiDescription  查询账套下的所有的申请单类型（前端查询条件下拉框)
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} [setOfBooksId]  为空时默认为当前用户的账套Id
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/all?setOfBooksId=1037906263432859649
     * @apiSuccess (返回参数) {Long} id 申请单类型Id
     * @apiSuccess (返回参数) {String} typeName 申请单类型名称
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1076017251497086977",
     *         "typeName": "654321"
     *     }
     *     ]
     */
    @GetMapping("/query/all")
    public ResponseEntity<List<ApplicationType>> queryAllType(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                              @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(service.queryAllType(setOfBooksId,enabled));
    }

    /**
     * @api {GET} /api/expense/application/type/query/created 【申请单类型】查询已创建申请单类型
     * @apiDescription  查询账套下的所有已创建的申请单类型（前端查询条件下拉框)
     * @apiGroup ExpenseService
     * @apiParam (url参数) {Long} [setOfBooksId]  为空时默认为当前用户的账套Id
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/created?setOfBooksId=1037906263432859649
     * @apiSuccess (返回参数) {Long} id 申请单类型Id
     * @apiSuccess (返回参数) {String} typeName 申请单类型名称
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1076017251497086977",
     *         "typeName": "654321"
     *     }
     *     ]
     */
    @GetMapping("/query/created")
    public ResponseEntity<List<ApplicationType>> queryCreatedType(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                  @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(service.queryCreatedType(setOfBooksId,enabled));
    }

    /**
     * @api {GET} /api/expense/application/type/query/condition/user 【申请单类型】用户条件查询
     * @apiDescription  查询当前用户可以新建的申请单类型
     * @apiGroup ExpenseService
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/condition/user
     * @apiSuccess (返回参数) {Long} id 申请单类型Id
     * @apiSuccess (返回参数) {String} typeName 申请单类型名称
     * @apiSuccessExample {json} 返回报文:
     *  [
     *     {
     *         "id": "1076017251497086977",
     *         "typeName": "654321"
     *     }
     *     ]
     */
    @GetMapping("/query/condition/user")
    public ResponseEntity<List<ApplicationType>> queryByUser(){

        return ResponseEntity.ok(service.queryByUser());
    }


    /**
     * @api {GET} /api/expense/application/type/query/header/{id} 【申请单类型】头创建时查询
     * @apiDescription  申请单头创建时，根据类型ID查询分配的维度，以便动态生成表单
     * @apiGroup ExpenseService
     * @apiParam (url参数) Long id 申请单类型Id
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/header/11112545210
     * @apiSuccess (返回参数) {Long} id 申请单类型Id
     * @apiSuccess (返回参数) {String} typeName 申请单类型名称
     * @apiSuccess (返回参数) {String} typeCode 申请单类型代码
     * @apiSuccess (返回参数) {Boolean} associateContract 是否关联合同
     * @apiSuccess (返回参数) {Boolean} requireInput 合同是否必输
     * @apiSuccess (返回参数) {Dimensions} dimensions 维度对象集合
     * @apiSuccess (Dimensions) {Long} value 默认的维值Id
     * @apiSuccess (Dimensions) {Long} dimensionId 维度Id
     * @apiSuccess (Dimensions) {String} name 维度名称
     * @apiSuccess (Dimensions) {String} valueName 维值名称
     * @apiSuccessExample {json} 返回报文:
     *  {
     *     "id": "1060457723043524609",
     *     "createdDate": "2018-11-08T17:03:18.182+08:00",
     *     "createdBy": "1036",
     *     "lastUpdatedDate": "2018-12-24T20:20:51.419+08:00",
     *     "lastUpdatedBy": "1059",
     *     "versionNumber": 1,
     *     "enabled": true,
     *     "typeCode": "test",
     *     "typeName": "1111",
     *     "setOfBooksId": "1037906263432859649",
     *     "setOfBooksName": null,
     *     "tenantId": "1034363055694327809",
     *     "formOid": "2489af52-4548-4207-a852-bbbacc9dddca",
     *     "formName": "测试abc",
     *     "formType": 801009,
     *     "budgetFlag": true,
     *     "allFlag": true,
     *     "associateContract": false,
     *     "requireInput": false,
     *     "applyEmployee": "101",
     *     "dimensions": [
     *         {
     *             "id": null,
     *             "createdDate": null,
     *             "createdBy": null,
     *             "lastUpdatedDate": null,
     *             "lastUpdatedBy": null,
     *             "versionNumber": null,
     *             "documentType": null,
     *             "value": "1032",
     *             "valueName": "区域004",
     *             "name": "区域",
     *             "dimensionId": "1032",
     *             "dimensionField": "dimension1Id",
     *             "headerFlag": true,
     *             "headerId": null,
     *             "sequence": 10
     *         }
     *   ]
     */
    @GetMapping("/query/header/{id}")
    public ResponseEntity<ApplicationTypeDimensionDTO> queryByHeaderCreated(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.queryTypeAndDimensionById(id, true));
    }


    /**
     * @api {GET} /api/expense/application/type/query/expense/type 【申请单类型】查询申请类型
     * @apiDescription  创建单据行时，查询该类型分配的申请类型详细信息
     * @apiGroup ExpenseService
     * @apiParam (请求参数) {Long} applicationTypeId 申请单类型Id
     * @apiParam (请求参数) {Long} [categoryId] 所属大类
     * @apiParam (请求参数) {String} [expenseTypeName] 申请类型名称
     * @apiParam (请求参数) {int} page 当前页
     * @apiParam (请求参数) {int} size 每页大小
     * @apiParamExample {url} 请求报文:
     * /api/expense/application/type/query/expense/type?applicationTypeId=12131&categoryId=&expenseTypeName=&page=&size=
     * @apiSuccess (返回参数) {Long} id 费用申请类型Id
     * @apiSuccess (返回参数) {String} name 费用申请类型名称
     * @apiSuccess (返回参数) {String} code 费用申请类型代码
     * @apiSuccess (返回参数) {String} iconUrl 费用申请类型图标地址
     * @apiSuccess (返回参数) {Boolean} entryMode 是否按单价输入
     * @apiSuccess (返回参数) {String} priceUnit 单位
     * @apiSuccess (返回参数) {Fields} fields 费用申请类型动态列
     * @apiSuccess (Fields) {String} value 值
     * @apiSuccess (Fields) {Long} dimensionId 维度Id
     * @apiSuccess (Fields) {String} fieldType 类型
     * @apiSuccess (Fields) {String} fieldDataType 数据类型
     * @apiSuccess (Fields) {Boolean} required 是否必输
     * @apiSuccessExample {json} 返回报文:
     *  {
     *         "i18n": null,
     *         "id": "1072130541355110402",
     *         "enabled": true,
     *         "name": "6666",
     *         "iconName": "oilCard",
     *         "code": "6666",
     *         "iconUrl": "http://115.159.108.80:25296/upload/expenseIcon/887d2e14-700a-4678-93a8-ac96023aeee2-oilCard.png",
     *         "tenantId": "1034363055694327809",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1062918778447642626",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "sourceTypeName": null,
     *         "priceUnit": null,
     *         "typeCategoryName": null,
     *         "setOfBooksName": null,
     *         "budgetItemName": null,
     *         "fields": [
     *             {
     *                 "id": "1077741307905789954",
     *                 "fieldType": "TEXT",
     *                 "fieldDataType": "TEXT",
     *                 "name": "文本",
     *                 "value": null,
     *                 "codeName": null,
     *                 "messageKey": null,
     *                 "sequence": 0,
     *                 "customEnumerationOid": null,
     *                 "mappedColumnId": 111,
     *                 "printHide": false,
     *                 "required": false,
     *                 "showOnList": true,
     *                 "fieldOid": "10db18d8-6ffc-4876-b59a-4a107fd9b599",
     *                 "editable": true,
     *                 "defaultValueMode": "CURRENT",
     *                 "defaultValueKey": null,
     *                 "showValue": null,
     *                 "defaultValueConfigurable": null,
     *                 "commonField": null,
     *                 "reportKey": null,
     *                 "i18n": null,
     *                 "options": null
     *             }
     *             ]
     *             ]
     */
    @GetMapping("/query/expense/type")
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseType(@RequestParam("applicationTypeId") Long applicationTypeId,
                                                                    @RequestParam("employeeId") Long employeeId,
                                                                    @RequestParam("companyId") Long companyId,
                                                                    @RequestParam("departmentId") Long departmentId,
                                                                    @RequestParam(value = "categoryId",required = false) Long categoryId,
                                                                    @RequestParam(value = "expenseTypeName", required = false) String expenseTypeName,
                                                                    Pageable pageable){
        Page<ExpenseTypeWebDTO> page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> result = service.queryExpenseTypeByApplicationTypeId(applicationTypeId,
                categoryId, expenseTypeName, companyId, employeeId, departmentId, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }


    /**
     * @api {GET} /query/expense/type/by/setOfBooksId?setOfBooksId=123&page=&size= 【申请单类型】根据账套查询费用类型
     */
    @GetMapping("/query/expense/type/by/setOfBooksId")
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseTypeBySetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                    @RequestParam(value = "typeFlag",defaultValue = "0")Integer typeFlag,
                                                                    @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                                    @RequestParam(value = "name",required = false) String name,
                                                                    Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> result = service.queryExpenseTypeBySetOfBooksId(setOfBooksId,null,typeCategoryId,name,typeFlag,page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/expense/application/type/query/expense/type");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/expense/application/type/queryDimensionByTypeIdAndCompanyId 【申请单类型】根据申请单类型id和公司id查询公司下已分配的启用维度
     * @param applicationTypeId
     * @param companyId
     * @return
     */
    @GetMapping("/queryDimensionByTypeIdAndCompanyId")
    public List<ApplicationTypeDimension> queryDimensionByTypeIdAndCompanyId(@RequestParam("applicationTypeId") Long applicationTypeId,
                                                                             @RequestParam("companyId") Long companyId) {
        List<ApplicationTypeDimension> result = service.queryDimensionByTypeIdAndCompanyId(applicationTypeId, companyId);

        return result;
    }

    /**
     * @api {GET} /api/expense/application/type/{applicationTypeId}/dimensions/query/filter 【申请单类型】未分配维度查询
     */
    @GetMapping("/{applicationTypeId}/dimensions/query/filter")
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                            @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                            @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                            @RequestParam(value = "enabled",required = false) Boolean enabled
                                                            ) throws URISyntaxException {
        List<DimensionCO> result = service.listDimensionByConditionFilter(applicationTypeId,setOfBooksId,dimensionCode,dimensionName,enabled);
        return result;
    }

    /**
     * @api {GET} /api/expense/application/type/users 【申请单类型】根据单据id查询有该单据权限的用户
     */
    @GetMapping("/users")
    public ResponseEntity listUsersByApplicationType(@RequestParam(value = "applicationTypeId") Long applicationTypeId,
                                                     @RequestParam(value = "userCode", required = false) String userCode,
                                                     @RequestParam(value = "userName", required = false) String userName,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = service.listUsersByApplicationType(applicationTypeId, userCode, userName, queryPage);
        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * 根据所选范围查询账套下符合条件的费用申请单类型
     * @param applicationTypeForOtherCO
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/query/by/cond")
    public ResponseEntity<Page<ApplicationTypeCO>> queryApplicationTypeByCond(@RequestBody ApplicationTypeForOtherCO applicationTypeForOtherCO,
                                                       @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false,defaultValue = "10") int size) throws URISyntaxException {
        Page pageInfo = PageUtil.getPage(page,size);
        Page<ApplicationTypeCO> list = service.queryApplicationTypeByCond(applicationTypeForOtherCO,pageInfo);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(pageInfo);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }
}
