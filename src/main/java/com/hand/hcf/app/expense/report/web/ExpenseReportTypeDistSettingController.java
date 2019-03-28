package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDistSetting;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDistSettingRequestDTO;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeDistSettingService;
import com.hand.hcf.app.mdata.client.com.CompanyCO;
import com.hand.hcf.app.mdata.client.department.DepartmentCO;
import com.hand.hcf.app.mdata.client.rescenter.ResponsibilityCenterCO;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/3
 */
@RestController
@RequestMapping("/api/expense/report/type/dist/setting")
public class ExpenseReportTypeDistSettingController {
    private final ExpenseReportTypeDistSettingService expenseReportTypeDistSettingService;

    public ExpenseReportTypeDistSettingController(ExpenseReportTypeDistSettingService expenseReportTypeDistSettingService){
        this.expenseReportTypeDistSettingService = expenseReportTypeDistSettingService;
    }

    /**
     * 单个新增 报账单类型分摊设置
     *
     * @param expenseReportTypeDistSettingRequestDTO
     * @return
     */
    /**
     * @api {POST} /api/expense/report/type/dist/setting 【报账单类型分摊设置】单个新增
     * @apiDescription 报账单类型分摊设置单个新增
     * @apiGroup ReportTypeService
     * @apiParam (请求参数) {ExpenseReportTypeDistSettingRequestDTO} expenseReportTypeDistSettingRequestDTO 分摊设置分装DTO
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {ExpenseReportTypeDistSetting} expenseReportTypeDistSetting 分摊设置对象
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} companyIdList 自定义公司id集合
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} departmentIdList 自定义部门id集合
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} resIdList 自定义责任中心id集合
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} reportTypeId 报账单类型ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} companyDistFlag 公司参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [companyDistRange] 公司分摊范围 (账套下所有公司:ALL_COM_IN_SOB;本公司及下属公司:'CURRENT_COM_&_SUB_COM';下属公司:SUB_COM;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [companyDefaultId] 默认分摊公司ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} companyVisible 公司可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [departmentDistRange] 部门分摊范围 (租户下所有部门:ALL_DEP_IN_TENANT;账套下所有部门:ALL_DEP_IN_SOB;公司下所有部门:ALL_DEP_IN_COM;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [departmentDefaultId] 默认分摊部门ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} departmentVisible 部门可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [resDistRange] 责任中心分摊范围 (部门对应责任中心:DEP_RES_CENTER;账套下所有责任中心:ALL_RES_CENTER_IN_SOB;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [resDefaultId] 默认分摊责任中心ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} resVisible 责任中心可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Boolean} companyDistFlag 公司参与分摊标志
     * @apiSuccess {String} companyDistRange 公司分摊范围
     * @apiSuccess {Long} companyDefaultId 默认分摊公司ID
     * @apiSuccess {String} companyVisible 公司可见设置
     * @apiSuccess {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiSuccess {String} departmentDistRange 部门分摊范围
     * @apiSuccess {Long} departmentDefaultId 默认分摊部门ID
     * @apiSuccess {String} departmentVisible 部门可见设置
     * @apiSuccess {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiSuccess {String} resDistRange 责任中心分摊范围
     * @apiSuccess {Long} resDefaultId 默认分摊责任中心ID
     * @apiSuccess {String} resVisible 责任中心可见设置
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    {
    "expenseReportTypeDistSetting":{
    "reportTypeId":1101007150869291010,
    "companyDistFlag":false,
    "companyVisible":"EDITABLE",
    "departmentDistFlag":false,
    "departmentVisible":"EDITABLE",
    "resCenterDistFlag":false,
    "resVisible":"EDITABLE"
    },
    "companyIdList":[],
    "departmentIdList":[],
    "resIdList":[]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1102723101186908161",
    "createdDate": "2019-03-05T08:10:49.589+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-05T08:10:49.589+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "reportTypeId": "1101007150869291010",
    "companyDistFlag": false,
    "companyDistRange": null,
    "companyDefaultId": null,
    "companyVisible": "EDITABLE",
    "departmentDistFlag": false,
    "departmentDistRange": null,
    "departmentDefaultId": null,
    "departmentVisible": "EDITABLE",
    "resCenterDistFlag": false,
    "resDistRange": null,
    "resDefaultId": null,
    "resVisible": "EDITABLE"
    }
     */
    @PostMapping
    public ResponseEntity<ExpenseReportTypeDistSetting> createExpenseReportTypeDistSetting(@RequestBody ExpenseReportTypeDistSettingRequestDTO expenseReportTypeDistSettingRequestDTO){
        return ResponseEntity.ok(expenseReportTypeDistSettingService.createExpenseReportTypeDistSetting(expenseReportTypeDistSettingRequestDTO));
    }

    /**
     * 单个修改 报账单类型分摊设置
     *
     * @param expenseReportTypeDistSettingRequestDTO
     * @return
     */
    /**
     * @api {PUT} /api/expense/report/type/dist/setting 【报账单类型分摊设置】单个修改
     * @apiDescription 报账单类型分摊设置单个修改
     * @apiGroup ReportTypeService
     * @apiParam (请求参数) {ExpenseReportTypeDistSettingRequestDTO} expenseReportTypeDistSettingRequestDTO 分摊设置分装DTO
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {ExpenseReportTypeDistSetting} expenseReportTypeDistSetting 分摊设置对象
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} companyIdList 自定义公司id集合
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} departmentIdList 自定义部门id集合
     * @apiParam (ExpenseReportTypeDistSettingRequestDTO的属性) {List} resIdList 自定义责任中心id集合
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} id 报账单类型分摊设置id
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} reportTypeId 报账单类型ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} companyDistFlag 公司参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [companyDistRange] 公司分摊范围 (账套下所有公司:ALL_COM_IN_SOB;本公司及下属公司:'CURRENT_COM_&_SUB_COM';下属公司:SUB_COM;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [companyDefaultId] 默认分摊公司ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} companyVisible 公司可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [departmentDistRange] 部门分摊范围 (租户下所有部门:ALL_DEP_IN_TENANT;账套下所有部门:ALL_DEP_IN_SOB;公司下所有部门:ALL_DEP_IN_COM;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [departmentDefaultId] 默认分摊部门ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} departmentVisible 部门可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} [resDistRange] 责任中心分摊范围 (部门对应责任中心:DEP_RES_CENTER;账套下所有责任中心:ALL_RES_CENTER_IN_SOB;自定义范围:CUSTOM_RANGE)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} [resDefaultId] 默认分摊责任中心ID
     * @apiParam (ExpenseReportTypeDistSetting的属性) {String} resVisible 责任中心可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParam (ExpenseReportTypeDistSetting的属性) {Long} versionNumber 版本号
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Boolean} companyDistFlag 公司参与分摊标志
     * @apiSuccess {String} companyDistRange 公司分摊范围
     * @apiSuccess {Long} companyDefaultId 默认分摊公司ID
     * @apiSuccess {String} companyVisible 公司可见设置
     * @apiSuccess {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiSuccess {String} departmentDistRange 部门分摊范围
     * @apiSuccess {Long} departmentDefaultId 默认分摊部门ID
     * @apiSuccess {String} departmentVisible 部门可见设置
     * @apiSuccess {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiSuccess {String} resDistRange 责任中心分摊范围
     * @apiSuccess {Long} resDefaultId 默认分摊责任中心ID
     * @apiSuccess {String} resVisible 责任中心可见设置
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    {
    "expenseReportTypeDistSetting":{
    "id":1102723101186908161,
    "reportTypeId":1101007150869291010,
    "companyDistFlag":true,
    "companyDistRange":"CUSTOM_RANGE",
    "companyDefaultId":1083751704185716737,
    "companyVisible":"EDITABLE",
    "departmentDistFlag":false,
    "departmentVisible":"EDITABLE",
    "resCenterDistFlag":false,
    "resVisible":"EDITABLE",
    "versionNumber": 1
    },
    "companyIdList":[1083751704185716737],
    "departmentIdList":[],
    "resIdList":[]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1102723101186908161",
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": 2,
    "reportTypeId": "1101007150869291010",
    "companyDistFlag": true,
    "companyDistRange": "CUSTOM_RANGE",
    "companyDefaultId": "1083751704185716737",
    "companyVisible": "EDITABLE",
    "departmentDistFlag": false,
    "departmentDistRange": null,
    "departmentDefaultId": null,
    "departmentVisible": "EDITABLE",
    "resCenterDistFlag": false,
    "resDistRange": null,
    "resDefaultId": null,
    "resVisible": "EDITABLE"
    }
     */
    @PutMapping
    public ResponseEntity<ExpenseReportTypeDistSetting> updateExpenseReportTypeDistSetting(@RequestBody ExpenseReportTypeDistSettingRequestDTO expenseReportTypeDistSettingRequestDTO){
        return ResponseEntity.ok(expenseReportTypeDistSettingService.updateExpenseReportTypeDistSetting(expenseReportTypeDistSettingRequestDTO));
    }

    /**
     * 根据报账单类型id查询 报账单类型分摊设置
     *
     * @param id
     * @return
     */
    /**
     * @api {PUT} /api/expense/report/type/dist/setting/{id} 【报账单类型分摊设置】单个查询
     * @apiDescription 根据报账单类型id查询单个 报账单类型分摊设置
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Boolean} companyDistFlag 公司参与分摊标志
     * @apiSuccess {String} companyDistRange 公司分摊范围
     * @apiSuccess {Long} companyDefaultId 默认分摊公司ID
     * @apiSuccess {String} companyVisible 公司可见设置
     * @apiSuccess {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiSuccess {String} departmentDistRange 部门分摊范围
     * @apiSuccess {Long} departmentDefaultId 默认分摊部门ID
     * @apiSuccess {String} departmentVisible 部门可见设置
     * @apiSuccess {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiSuccess {String} resDistRange 责任中心分摊范围
     * @apiSuccess {Long} resDefaultId 默认分摊责任中心ID
     * @apiSuccess {String} resVisible 责任中心可见设置
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/1101007150869291010
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1102723101186908161",
    "createdDate": "2019-03-05T08:10:49.589+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-05T09:17:51.501+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "reportTypeId": "1101007150869291010",
    "companyDistFlag": true,
    "companyDistRange": "CUSTOM_RANGE",
    "companyDefaultId": "1083751704185716737",
    "companyVisible": "EDITABLE",
    "departmentDistFlag": false,
    "departmentDistRange": null,
    "departmentDefaultId": null,
    "departmentVisible": "EDITABLE",
    "resCenterDistFlag": false,
    "resDistRange": null,
    "resDefaultId": null,
    "resVisible": "EDITABLE"
    }
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseReportTypeDistSettingRequestDTO> getExpenseReportTypeDistSettingById(@PathVariable Long id){
        return ResponseEntity.ok(expenseReportTypeDistSettingService.getExpenseReportTypeDistSettingById(id));
    }

    /**
     * 根据 “公司分摊范围” 分页查询公司 （自定义范围弹框和选择公司默认值时通用）
     * @param companyDistRange 公司分摊范围
     * @param companyCode 公司代码
     * @param companyName 公司名称
     * @param companyCodeFrom 公司代码从
     * @param companyCodeTo 公司代码至
     * @param pageable 分页信息
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/dist/setting/query/company/by/company/dist/range 【报账单类型分摊设置】根据“公司分摊范围”分页查询公司
     * @apiDescription 根据 “公司分摊范围” 分页查询公司 （自定义范围弹框和选择公司默认值时通用）
     * @apiGroup ReportTypeService
     * @apiParam {String} companyDistRange 公司分摊范围
     * @apiParam {String} companyCode 公司代码
     * @apiParam {String} companyName 公司名称
     * @apiParam {String} companyCodeFrom 公司代码从
     * @apiParam {String} companyCodeTo 公司代码至
     * @apiParam {String} pageable 分页信息
     * @apiSuccess {Long} id  公司ID
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {String} name 公司名称
     * @apiSuccess {String} companyTypeName 公司类型名称
     * @apiSuccess {String} companyTypeCode 公司类型代码
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/query/company/by/company/dist/range?companyDistRange=CUSTOM_RANGE&companyCode&companyName&companyCodeFrom&companyCodeTo&page=0&size=10
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1083751704185716737",
    "companyOid": "49fa21ac-f072-4717-bf43-7e2609c3cd67",
    "name": "小嘛呀小二郎公司",
    "setOfBooksId": "1083762150064451585",
    "setOfBooksName": "测试账套",
    "legalEntityId": "1083762392822378498",
    "companyCode": "GS00001",
    "address": "adadad",
    "companyLevelId": null,
    "parentCompanyId": null,
    "companyTypeCode": "1",
    "companyTypeName": "业务实体",
    "tenantId": "1083751703623680001",
    "baseCurrency": null
    }
    ]
     */
    @GetMapping("/query/company/by/company/dist/range")
    public ResponseEntity<List<CompanyCO>> queryCompanyByCompanyDistRange (
            @RequestParam(value = "companyDistRange") String companyDistRange,
            @RequestParam(value = "companyCode",required = false) String companyCode,
            @RequestParam(value = "companyName",required = false) String companyName,
            @RequestParam(value = "companyCodeFrom",required = false) String companyCodeFrom,
            @RequestParam(value = "companyCodeTo",required = false) String companyCodeTo,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = expenseReportTypeDistSettingService.queryCompanyByCompanyDistRange(companyDistRange,companyCode,companyName,companyCodeFrom,companyCodeTo,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据 “部门分摊范围” 分页查询部门 （自定义范围弹框和选择部门默认值时通用）
     * @param departmentDistRange 部门分摊范围
     * @param departmentCode 部门代码
     * @param departmentName 部门名称
     * @param departmentCodeFrom 部门代码从
     * @param departmentCodeTo 部门代码至
     * @param pageable 分页信息
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/dist/setting/query/department/by/department/dist/range 【报账单类型分摊设置】根据“部门分摊范围”分页查询部门
     * @apiDescription 根据 “部门分摊范围” 分页查询部门 （自定义范围弹框和选择部门默认值时通用）
     * @apiGroup ReportTypeService
     * @apiParam {String} departmentDistRange 部门分摊范围
     * @apiParam {String} departmentCode 部门代码
     * @apiParam {String} departmentName 部门名称
     * @apiParam {String} departmentCodeFrom 部门代码从
     * @apiParam {String} departmentCodeTo 部门代码至
     * @apiParam {String} pageable 分页信息
     * @apiSuccess {Long} id  部门ID
     * @apiSuccess {String} departmentCode 部门代码
     * @apiSuccess {String} name 部门名称
     * @apiSuccess {String} path 部门path
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/query/department/by/department/dist/range?departmentDistRange=ALL_DEP_IN_TENANT&departmentCode&departmentName&departmentCodeFrom&departmentCodeTo&page=0&size=10
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1103478642276868098",
    "departmentOid": "1683e276-f000-4f76-839c-c66252fec71c",
    "departmentCode": "YZ1000",
    "name": "YZ财务部",
    "path": "YZ财务部"
    }
    ]
     */
    @GetMapping("/query/department/by/department/dist/range")
    public ResponseEntity<List<DepartmentCO>> queryDepartmentByDepartmentDistRange (
            @RequestParam(value = "departmentDistRange") String departmentDistRange,
            @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @RequestParam(value = "departmentName",required = false) String departmentName,
            @RequestParam(value = "departmentCodeFrom",required = false) String departmentCodeFrom,
            @RequestParam(value = "departmentCodeTo",required = false) String departmentCodeTo,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentCO> result = expenseReportTypeDistSettingService.queryDepartmentByDepartmentDistRange(departmentDistRange,departmentCode,departmentName,departmentCodeFrom,departmentCodeTo,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }


    /**
     * 根据费用类型获取公司范围
     * @param expenseTypeId
     * @param companyCode
     * @param companyName
     * @param pageable
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/dist/setting/query/company/by/expenseTypeId 【报账单】根据费用类型获取公司
     * @apiDescription 根据费用类型获取公司范围
     * @apiGroup ExpenseReport
     * @apiParam {Long} expenseTypeId 费用类型ID
     * @apiParam {String} [companyCode] 公司代码
     * @apiParam {String} [companyName] 公司名称
     * @apiParam {int} [page] 页数
     * @apiParam {int} [size] 每页大小
     * @apiSuccess {Long} id  公司ID
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {String} name 公司名称
     * @apiSuccess {String} companyTypeName 公司类型名称
     * @apiSuccess {String} companyTypeCode 公司类型代码
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/query/company/by/expenseTypeId?expenseTypeId=1
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1083751704185716737",
    "companyOid": "49fa21ac-f072-4717-bf43-7e2609c3cd67",
    "name": "小嘛呀小二郎公司",
    "setOfBooksId": "1083762150064451585",
    "setOfBooksName": "测试账套",
    "legalEntityId": "1083762392822378498",
    "companyCode": "GS00001",
    "address": "adadad",
    "companyLevelId": null,
    "parentCompanyId": null,
    "companyTypeCode": "1",
    "companyTypeName": "业务实体",
    "tenantId": "1083751703623680001",
    "baseCurrency": null
    }
    ]
     */
    @GetMapping("/query/company/by/expenseTypeId")
    public ResponseEntity<List<CompanyCO>> queryCompanyByExpenseTypeId (
            @RequestParam(value = "expenseTypeId") Long expenseTypeId,
            @RequestParam(value = "companyCode",required = false) String companyCode,
            @RequestParam(value = "companyName",required = false) String companyName,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = expenseReportTypeDistSettingService.queryCompanyByExpenseTypeId(expenseTypeId,companyCode,companyName,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据费用类型获取部门范围
     * @param expenseTypeId
     * @param departmentCode
     * @param departmentName
     * @param page
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/dist/setting/query/department/by/expenseTypeId 【报账单】根据费用类型查询分摊行部门
     * @apiDescription 根据费用类型查询分摊行部门范围
     * @apiGroup ExpenseReport
     * @apiParam {Long} expenseTypeId 费用类型ID
     * @apiParam {String} [departmentCode] 部门代码
     * @apiParam {String} [departmentName] 部门名称
     * @apiParam {int} [page] 页数
     * @apiParam {int} [size] 每页大小
     * @apiSuccess {Long} id  部门ID
     * @apiSuccess {String} departmentCode 部门代码
     * @apiSuccess {String} name 部门名称
     * @apiSuccess {String} path 部门path
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/query/department/by/expenseTypeId?expenseTypeId=1
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1103478642276868098",
    "departmentOid": "1683e276-f000-4f76-839c-c66252fec71c",
    "departmentCode": "YZ1000",
    "name": "YZ财务部",
    "path": "YZ财务部"
    }
    ]
     */
    @GetMapping("/query/department/by/expenseTypeId")
    public ResponseEntity<List<DepartmentCO>> queryDepartmentByDepartmentDistRange (
            @RequestParam(value = "expenseTypeId") Long expenseTypeId,
            @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @RequestParam(value = "departmentName",required = false) String departmentName,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentCO> result = expenseReportTypeDistSettingService.queryDepartmentByExpenseTypeId(expenseTypeId,departmentCode,departmentName,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据费用类型获取责任中心范围
     * @param expenseTypeId
     * @param responsibilityCenterCode
     * @param responsibilityCenterCodeName
     * @param companyId
     * @param departmentId
     * @param pageable
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/dist/setting/query/respCenter/by/expenseTypeId【报账单】根据费用类型获取责任中心
     * @apiDescription 根据费用类型获取责任中心范围
     * @apiGroup ExpenseReport
     * @apiParam {Long} expenseTypeId 费用类型ID
     * @apiParam {String} [responsibilityCenterCode] 责任中心代码
     * @apiParam {String} [responsibilityCenterCodeName] 责任中心名称
     * @apiParam {Long} [companyId] 公司ID(当分摊规则为DEP_RES_CENTER时，传递，且不能为空)
     * @apiParam {Long} [departmentId] 部门ID(当分摊规则为DEP_RES_CENTER时，传递，且不能为空)
     * @apiParam {int} [page] 页数
     * @apiParam {int} [size] 每页大小
     * @apiSuccess {Long} id  部门ID
     * @apiSuccess {String} departmentCode 部门代码
     * @apiSuccess {String} name 部门名称
     * @apiSuccess {String} path 部门path
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/dist/setting/query/respCenter/by/expenseTypeId?expenseTypeId=1
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1103478642276868098",
    "responsibilityCenterCode": "1-1",
    "responsibilityCenterName": "测试责任中心"
    }
    ]
     */
    @GetMapping("/query/respCenter/by/expenseTypeId")
    public ResponseEntity<List<ResponsibilityCenterCO>> queryResponsibilityCenterByExpenseTypeId (
            @RequestParam(value = "expenseTypeId") Long expenseTypeId,
            @RequestParam(value = "responsibilityCenterCode",required = false) String responsibilityCenterCode,
            @RequestParam(value = "responsibilityCenterCodeName",required = false) String responsibilityCenterCodeName,
            @RequestParam(value = "companyId",required = false) Long companyId,
            @RequestParam(value = "departmentId",required = false) Long departmentId,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenterCO> result = expenseReportTypeDistSettingService.queryResponsibilityCenterByExpenseTypeId(expenseTypeId,
                companyId,
                departmentId,
                responsibilityCenterCode,
                responsibilityCenterCodeName,
                page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(),httpHeaders, HttpStatus.OK);
    }


}
