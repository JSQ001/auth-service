package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.mdata.client.contact.ContactCO;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeService;
import com.hand.hcf.app.prepayment.web.dto.CashPayRequisitionTypeDTO;
import com.hand.hcf.app.prepayment.web.dto.TypeDTO;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/10/24.
 */
@RestController
@RequestMapping("/api/cash/pay/requisition/types")
public class CashPayRequisitionTypeController {
    private final CashPayRequisitionTypeService cashSobPayReqTypeService;

    public CashPayRequisitionTypeController(CashPayRequisitionTypeService cashSobPayReqTypeService){
        this.cashSobPayReqTypeService = cashSobPayReqTypeService;
    }

    /**
     * 新增 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/types 【预付款单类型】 新增
     * @apiDescription 新增 预付款单类型定义
     * @apiGroup PrepaymentService
     * @apiParam {Object} cashPayRequisitionType 预付款单类型定义
     * @apiParam {List(Long)} requisitionTypeIdList 关联申请单类型id集合
     * @apiParam {List(Long)} transactionClassIdList 关联现金事务分类id集合
     * @apiParam {List(Long)} departmentOrUserGroupIdList 关联部门或人员组id集合
     * @apiParam {List(Object)} departmentOrUserGroupList 关联部门或人员组的对象集合
     * @apiParam {List(String)} returnRequisitionTypeIdList 根据id查询时，返回关联申请单类型id集合
     * @apiParam {List(String)} returnTransactionClassIdList 根据id查询时，返回关联现金事务分类id集合
     * @apiParam (cashPayRequisitionType) {Long} setOfBookId  账套ID
     * @apiParam (cashPayRequisitionType) {String} typeCode 预付款单类型代码
     * @apiParam (cashPayRequisitionType) {String} typeName 预付款单类型名称
     * @apiParam (cashPayRequisitionType) {String} paymentMethodCategory 付款方式类型(线上、线下、落地文件)
     * @apiParam (cashPayRequisitionType) {String} formOid 关联表单类型oid
     * @apiParam (cashPayRequisitionType) {String} formName 关联表单名称
     * @apiParam (cashPayRequisitionType) {Long} formType 关联表单类型
     * @apiParam (cashPayRequisitionType) {Object} allType 是否全部申请单类型,BASIS_00:不关联,BASIS_01:全部申请单类型,BASIS_02:部分申请类型
     * @apiParam (cashPayRequisitionType) {Boolean} allClass 是否全部现金事务分类
     * @apiParam (cashPayRequisitionType) {String} setOfBookCode 账套code
     * @apiParam (cashPayRequisitionType) {String} setOfBookName 账套name
     * @apiParam (cashPayRequisitionType) {String} paymentMethodCategoryName 付款方式类型name
     * @apiParam (cashPayRequisitionType) {Boolean} needApply 是否需要申请
     * @apiParam (cashPayRequisitionType) {CashPayRequisitionTypeBasisEnum} applicationFormBasis 关联申请单依据,BASIS_00:不选择申请单依据,BASIS_01:申请单头公司+头部门，BASIS_02：申请单头申请人
     * @apiParam (cashPayRequisitionType) {CashPayRequisitionTypeEmployeeEnum} applyEmployee 适用人员，BASIS_01:全部人员,BASIS_02:按部门添加,BASIS_03:按人员组添加
     * @apiParam (departmentOrUserGroupList) {Long} id 关联表单类型
     * @apiParam (departmentOrUserGroupList) {String} Oid oid
     * @apiParam (departmentOrUserGroupList) {String} code 代码
     * @apiParam (departmentOrUserGroupList) {String} name 名称
     * @apiParam (departmentOrUserGroupList) {String} description 描述
     * @apiParam (departmentOrUserGroupList) {String} remark 备注
     * @apiParam (departmentOrUserGroupList) {List(Long)} parentId 上级id
     * @apiParam (departmentOrUserGroupList) {List(String)} parentOid 上级oid
     * @apiParam (departmentOrUserGroupList) {List(Long)} groupId 组别id
     * @apiParam (departmentOrUserGroupList) {List(String)} groupOid 组别oid
     * @apiParam (departmentOrUserGroupList) {List(Long)} subordinateId 下级id
     * @apiParam (departmentOrUserGroupList) {List(String)} subordinateOid 下级oid
     * @apiParam (departmentOrUserGroupList) {List(Long)} detailId 明细id
     * @apiParam (departmentOrUserGroupList) {List(String)} detailOid 明细oid
     * @apiParam (departmentOrUserGroupList) {Integer} priority 优先级或序号
     * @apiParam (departmentOrUserGroupList) {Long} setOfBooksId 账套id
     * @apiParam (departmentOrUserGroupList) {Long} tenantId 租户id
     * @apiParam (departmentOrUserGroupList) {Long} companyId 所属公司
     * @apiParam (departmentOrUserGroupList) {String} companyOId 所属公司oid
     * @apiParam (departmentOrUserGroupList) {Boolean} isEnabled 是否启用
     * @apiParam (departmentOrUserGroupList) {Boolean} isDeleted 是否删除
     * @apiParam (departmentOrUserGroupList) {ZonedDateTime} startDate 生效日期
     * @apiParam (departmentOrUserGroupList) {ZonedDateTime} invalidDate 失效日期
     * @apiParam (departmentOrUserGroupList) {ZonedDateTime} invalidDate 失效日期
     * @apiParam (departmentOrUserGroupList) {Object} i18n 多语言字段
     * @apiParam (departmentOrUserGroupList) {String} attribute1-20 备用字段
     * @apiParam (departmentOrUserGroupList) {List(Object)} detailList 明细值
     * @apiParam (departmentOrUserGroupList) {Boolean} isPublic 是否公用
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiParamExample {json} 请求参数:
    {
    "cashPayRequisitionType":{
    "setOfBookId":933328180238237697,
    "typeCode":"test1",
    "typeName":"测试1",
    "paymentMethodCategory":"OFFLINE_PAYMENT",
    "formId":1,
    "needApply":"true",
    "applicationFormBasis":"BASIS_01",
    "allType":"BASIS_02",
    "allClass":false,
    "applyEmployee":"BASIS_02"
    },
    "requisitionTypeIdList":[1,2],
    "transactionClassIdList":[1,2],
    "departmentOrUserGroupIdList":[1,2]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "setOfBookId": "933328180238237697",
    "typeCode": "test1",
    "typeName": "测试1",
    "paymentMethodCategory": "OFFLINE_PAYMENT",
    "formId": "1",
    "allType": "BASIS_02",
    "allClass": false,
    "setOfBookCode": null,
    "setOfBookName": null,
    "paymentMethodCategoryName": null,
    "formName": null,
    "needApply": true,
    "applicationFormBasis": "BASIS_01",
    "applyEmployee": "BASIS_02",
    "id": "946657516333940738",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2017-12-29T16:21:54.602+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2017-12-29T16:21:54.602+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 1
    }
     */
    @PostMapping
    public ResponseEntity<CashPayRequisitionType> createCashPayRequisitionType(@RequestBody @NotNull CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        return ResponseEntity.ok(cashSobPayReqTypeService.createCashPayRequisitionType(cashPayRequisitionTypeDTO));
    }

    /**
     * 修改 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/types 【预付款单类型】 修改
     * @apiDescription 修改 预付款单类型定义
     * @apiGroup PrepaymentService
     * @apiParam {Object} cashPayRequisitionType 预付款单类型定义
     * @apiParam {List(Long)} requisitionTypeIdList 关联申请单类型id集合
     * @apiParam {List(Long)} transactionClassIdList 关联现金事务分类id集合
     * @apiParam {List(Long)} departmentOrUserGroupIdList 关联部门或人员组id集合
     * @apiParam {List(Object)} departmentOrUserGroupList 关联部门或人员组的对象集合
     * @apiParam {List(String)} returnRequisitionTypeIdList 根据id查询时，返回关联申请单类型id集合
     * @apiParam {List(String)} returnTransactionClassIdList 根据id查询时，返回关联现金事务分类id集合
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiParamExample {json} 请求参数:
    {
    "cashPayRequisitionType":{
    "id":946657516333940738,
    "versionNumber":1,
    "needApply":false,
    "allType":"BASIS_00",
    "applicationFormBasis":"BASIS_00",
    "allClass":true,
    "applyEmployee":"BASIS_01"
    },
    "requisitionTypeList":[],
    "transactionClassList":[],
    "departmentOrUserGroupIdList":[]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "setOfBookId": null,
    "typeCode": null,
    "typeName": null,
    "paymentMethodCategory": null,
    "formId": null,
    "allType": "BASIS_00",
    "allClass": true,
    "setOfBookCode": null,
    "setOfBookName": null,
    "paymentMethodCategoryName": null,
    "formName": null,
    "needApply": false,
    "applicationFormBasis": "BASIS_00",
    "applyEmployee": "BASIS_01",
    "id": "946657516333940738",
    "isEnabled": null,
    "isDeleted": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": "2017-12-29T16:36:32.187+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 2
    }
     */
    @PutMapping
    public ResponseEntity<CashPayRequisitionType> updateCashPayRequisitionType(@RequestBody CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        return ResponseEntity.ok(cashSobPayReqTypeService.updateCashPayRequisitionType(cashPayRequisitionTypeDTO));
    }

    /**
     * 根据ID查询 预付款单类型定义
     *
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/cash/pay/requisition/types/940868530340466690 【预付款单类型】 根据ID查询
     * @apiDescription 根据ID查询 预付款单类型定义
     * @apiGroup PrepaymentService
     * @apiParam {Long} id 预付款单类型表ID
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiSuccessExample {json} 成功返回值:
    {
    "cashPayRequisitionType": {
    "setOfBookId": "933328180238237697",
    "typeCode": "test1",
    "typeName": "测试1",
    "paymentMethodCategory": "OFFLINE_PAYMENT",
    "formId": "1",
    "allType": "BASIS_02",
    "allClass": false,
    "setOfBookCode": "DEFAULT_SOB",
    "setOfBookName": "默认账套",
    "paymentMethodCategoryName": "线下",
    "formName": null,
    "needApply": true,
    "applicationFormBasis": "BASIS_01",
    "applyEmployee": "BASIS_02",
    "id": "946657516333940738",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2017-12-29T16:21:55+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2017-12-29T16:21:55+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 1
    },
    "requisitionTypeIdList": [
    1,
    2
    ],
    "transactionClassIdList": [
    1,
    2
    ],
    "departmentOrUserGroupIdList": [
    1,
    2
    ]
    }
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashPayRequisitionTypeDTO> getCashPayRequisitionType(@PathVariable Long id){
        return ResponseEntity.ok(cashSobPayReqTypeService.getCashPayRequisitionType(id));
    }

    /**
     * 自定义条件查询 预付款单类型定义(分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param paymentMethodCategory
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/types/query?setOfBookId=913384531111178242 【预付款单类型】 根据条件查询
     * @apiDescription 自定义条件查询 预付款单类型定义,分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} [setOfBookId] 账套ID
     * @apiParam {String} [typeCode] 预付款单类型代码
     * @apiParam {String} [typeName] 预付款单类型名称
     * @apiParam {String} [paymentMethodCategory] 付款方式类型 ONLINE_PAYMENT 线上,OFFLINE_PAYMENT 线下,EBANK_PAYMENT 落地文件
     * @apiParam {Boolean} [isEnabled] 是否启用
     * @apiParam {int} page
     * @apiParam {int} size
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "setOfBookId": "913384531111178242",
    "typeCode": "hx1",
    "typeName": "hxlq1",
    "paymentMethodCategory": "OFFLINE_PAYMENT",
    "formId": "1",
    "setOfBookCode": null,
    "setOfBookName": null,
    "paymentMethodCategoryName": "线下",
    "formName": null,
    "needApply": true,
    "applicationFormBasis": "BASIS_03",
    "id": "940868530340466690",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2017-12-13T16:58:32+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2017-12-13T17:28:20+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 2
    }
    ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeByCond(
            @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @RequestParam(value = "typeCode", required = false) String typeCode,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "paymentMethodCategory",required = false) String paymentMethodCategory,
            @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeByCond(setOfBookId,typeCode,typeName,paymentMethodCategory,isEnabled,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/types/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 预付款单类型定义(不分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @return
     */
    /**
     * @api {GET} /api/cash/pay/requisition/types/queryAll?setOfBookId=913384531111178242 【预付款单类型】 查询所有
     * @apiDescription 自定义条件查询 预付款单类型定义,不分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} [setOfBookId] 账套ID
     * @apiParam {String} [typeCode] 预付款单类型代码
     * @apiParam {String} [typeName] 预付款单类型名称
     * @apiParam {String} [companyCode] 公司code
     * @apiParam {String} [companyName] 公司名称
     * @apiParam {Long} [companyId] 公司ID
     * @apiParam {Boolean} [isEnabled] 是否启用
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "setOfBookId": "913384531111178242",
    "typeCode": "hx1",
    "typeName": "hxlq1",
    "paymentMethodCategory": "OFFLINE_PAYMENT",
    "formId": "1",
    "setOfBookCode": null,
    "setOfBookName": null,
    "paymentMethodCategoryName": "线下",
    "formName": null,
    "needApply": true,
    "applicationFormBasis": "BASIS_03",
    "id": "940868530340466690",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2017-12-13T16:58:32+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2017-12-13T17:28:20+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 2
    }
    ]
     */
    @GetMapping("/queryAll")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeAllByCond(
            @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @RequestParam(value = "typeCode", required = false) String typeCode,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "companyCode",required = false) String companyCode,
            @RequestParam(value = "companyName",required = false) String companyName,
            @RequestParam(value = "companyId",required = false)Long companyId,
            @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @RequestParam(value = "assginEnable",required = false) Boolean assginEnable
            ){
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeAllByCond(setOfBookId,typeCode,typeName,isEnabled,companyCode,companyName,companyId,assginEnable);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据预付款单类型id，获取其下已分配的现金事务分类
     * 为预付款单提供
     *
     * @param typeId
     * @return
     */
    /**
     * @api {GET} /api/cash/pay/requisition/types/queryTransactionClassByTypeId/970961962148040706 【预付款单类型】 通过现金事务查询
     * @apiDescription  根据预付款单类型id，获取其下已分配的现金事务分类
     * @apiGroup PrepaymentService
     * @apiParam {Long} typeId 预付款单类型表ID
     * @apiSuccess {Long} id 现金事务分类ID
     * @apiSuccess {String} typeCode 现金事务类型code
     * @apiSuccess {String} classCode 现金事务分类代码
     * @apiSuccess {String} description 现金事务分类名称
     * @apiSuccess {Boolean} assigned 是否被分配
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "975984582782459905",
    "typeCode": null,
    "classCode": "CSH0320001",
    "description": "HS001",
    "assigned": null
    },
    {
    "id": "975984754866364418",
    "typeCode": null,
    "classCode": "CSH0320002",
    "description": "HS002",
    "assigned": null
    }
    ]
     */
    @GetMapping("/queryTransactionClassByTypeId/{typeId}")
    public ResponseEntity<List<CashTransactionClassCO>> getTransactionClassByTypeId(@PathVariable Long typeId){
        return ResponseEntity.ok(cashSobPayReqTypeService.getTransactionClassByTypeId(typeId));
    }

    /**
     * 给新建预付款时选择预付款单类型提供
     * 根据想要新建预付款的人来筛选预付款单类型
     *
     * @param userId
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param isEnabled
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/types/queryByEmployeeId?userId=177606&setOfBookId=937515627984846850&isEnabled=true&page=0&size=10 【预付款单类型】 通过员工查询
     * @apiDescription  提供 给新建预付款时 选择预付款单类型,分页
     * @apiGroup  PrepaymentService
     * @apiParam {Long} userId 当前用户ID
     * @apiParam {Long} setOfBookId 	账套ID
     * @apiParam {String} [typeCode] 预付款单类型代码
     * @apiParam {String} [typeName] 预付款单类型名称
     * @apiSuccess {Object} cashPayRequisitionType 预付款单类型定义
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "setOfBookId": "937515627984846850",
    "typeCode": "YFK001",
    "typeName": "预付款单走工作流",
    "paymentMethodCategory": "ONLINE_PAYMENT",
    "formOid": "0fddcbfc-101a-4dcd-a4b0-ec81cef15563",
    "formName": "测试预付款单工作流",
    "formType": 801003,
    "allType": "BASIS_00",
    "allClass": true,
    "setOfBookCode": null,
    "setOfBookName": null,
    "paymentMethodCategoryName": null,
    "needApply": false,
    "applicationFormBasis": "BASIS_00",
    "applyEmployee": "BASIS_02",
    "id": "969399702304866306",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-03-14T14:23:35+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-03-19T17:34:36+08:00",
    "lastUpdatedBy": 177601,
    "versionNumber": 30
    }
    ]
     */
    @GetMapping("/queryByEmployeeId")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeByEmployeeId(
            @RequestParam Long userId,
            @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @RequestParam(value = "typeCode", required = false) String typeCode,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "isEnabled", required = false) Boolean isEnabled) throws URISyntaxException {
//            Pageable pageable) throws URISyntaxException {
//        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeByEmployeeId(userId,setOfBookId,typeCode,typeName,isEnabled);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/types/queryByEmployeeId");
//        return new ResponseEntity(list.getRecords(),headers, HttpStatus.OK);
        return ResponseEntity.ok(list);
    }


    /**
     * @apiDescription 通过id查询预付款单类型名称和code
     * @api {get} /api/cash/pay/requisition/types/get/by/id?id=969399702304866306 【预付款单类型】 查名称和code
     * @apiGroup PrepaymentService
     * @apiParam {Long} id 预付款单类型ID
     *@apiSuccessExample {json} 成功返回值
    {
    "id": "969399702304866306",
    "code": "YFK001",
    "name": "预付款单走工作流"
    }
     */
    @GetMapping("/get/by/id")
    public ResponseEntity<TypeDTO> getTypeById(@RequestParam(value = "id") Long id){
        CashPayRequisitionType type = cashSobPayReqTypeService.selectById(id);
        if(type==null){
            return null;
        }
        TypeDTO dto = new TypeDTO();
        dto.setId(type.getId());
        dto.setCode(type.getTypeCode());
        dto.setName(type.getTypeName());
        return ResponseEntity.ok(dto);
    }

    /**
     * @api {GET} /api/cash/pay/requisition/types/users/{id} 【预付款单类型】根据单据id查询有该单据权限的用户
     */
    @GetMapping("/users/{id}")
    public List<ContactCO> listUsersByCashSobPayReqTypeId(@PathVariable("id") Long id){

        return cashSobPayReqTypeService.listUsersByCashSobPayReqTypeId(id);
    }
}
