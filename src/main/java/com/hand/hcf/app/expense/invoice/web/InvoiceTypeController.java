package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.invoice.domain.InvoiceType;
import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceTypeService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/16 16:35
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/api/invoice/type")
public class InvoiceTypeController {
    @Autowired
    private InvoiceTypeService invoiceTypeService;

    /**
     * @api {POST}  /api/invoice/type 【发票类型定义-新增】
     * @apiDescription 新增发票类型定义
     * @apiGroup InvoiceType
     * @apiParam {Object} InvoiceType 发票类型定义domain类
     * @apiParam {Object} i18n 多语言
     * @apiParam {Long} setOfBooksId 账套Id
     * @apiParam {String} invoiceTypeCode 发票类型代码
     * @apiParam {String} invoiceTypeName 发票类型名称
     * @apiParam {String} deductionFlag 抵扣标志
     * @apiParam {String} creationMethod 创建方式（系统预置：SYS；自定义：CUSTOM）
     * @apiParam {String} invoiceCodeLength 发票代码长度
     * @apiParam {String} invoiceNumberLength 发票号码长度
     * @apiParam {String} defaultTaxRate 默认税率
     * @apiParam {String} interfaceMapping 接口映射值
     * @apiParamExample json} Request-Param:
     * {
        "i18n": {
            "name": [
                {
                "language": "zh_cn",
                "invoice_type_name": "物业服务申请单"
                },
                {
                "language": "en_us",
                "invoice_type_name": "sss"
                }
            ]
        },
        "invoiceTypeCode": "YW001",
        "invoiceTypeName": "物业服务申请单",
        "deductionFlag": "N",
        "creationMethod": "CUSTOM",
        "defaultTaxRate":"5%",
        "interfaceMapping":"1221",
        "invoiceCodeLength":"12",
        "invoiceNumberLength":"12",
        "setOfBooksId":"1084658256482856961"
    }
     * @apiSuccessExample {json} Success-Response:
     * {
        "i18n": {
            "name": [
                {
                "language": "zh_cn",
                "invoice_type_name": "物业服务申请单"
                },
                {
                "language": "en_us",
                "invoice_type_name": "sss"
                }
            ]
        },
        "id": "1085551213254115329",
        "deleted": false,
        "createdDate": "2019-01-16T22:55:52.464+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-16T22:55:52.464+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "enabled": true,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1084658256482856961",
        "invoiceTypeCode": "YW001",
        "invoiceTypeName": "物业服务申请单",
        "deductionFlag": "N",
        "creationMethod": "CUSTOM",
        "invoiceCodeLength": "12",
        "invoiceNumberLength": "12",
        "defaultTaxRate": "5%",
        "interfaceMapping": "1221"
    }
    */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceType> insertInvoiceType(@RequestBody InvoiceType invoiceType){
        return ResponseEntity.ok(invoiceTypeService.insertInvoiceType(invoiceType));
    }
    /**
     * @api {PUT}  /api/invoice/type 【发票类型定义-修改】
     * @apiDescription 编辑发票类型定义
     * @apiGroup InvoiceType
     * @apiParam {Object} InvoiceType 发票类型定义domain类
     * @apiParam {Object} i18n 多语言
     * @apiParam {Long} setOfBooksId 账套Id
     * @apiParam {String} invoiceTypeCode 发票类型代码
     * @apiParam {String} invoiceTypeName 发票类型名称
     * @apiParam {String} deductionFlag 抵扣标志
     * @apiParam {String} creationMethod 创建方式（系统预置：SYS；自定义：CUSTOM）
     * @apiParam {String} invoiceCodeLength 发票代码长度
     * @apiParam {String} invoiceNumberLength 发票号码长度
     * @apiParam {String} defaultTaxRate 默认税率
     * @apiParam {String} interfaceMapping 接口映射值
     * @apiParamExample {json} Request-Param:
     *{
        "i18n": {
            "name": [
                        {
                        "language": "zh_cn",
                        "invoice_type_name": "咸鱼测试申请单"
                        },
                        {
                        "language": "en_us",
                        "invoice_type_name": "rookie"
                        }
            ]
        },
        "id": "1085551213254115329",
        "invoiceTypeCode": "YW001",
        "invoiceTypeName": "咸鱼测试申请单",
        "deductionFlag": "Y",
        "creationMethod": "CUSTOM",
        "defaultTaxRate":"5%",
        "interfaceMapping":"10086",
        "invoiceCodeLength":"24",
        "invoiceNumberLength":"24",
        "setOfBooksId":"1084658256482856961"
    }
     * @apiSuccessExample {json} Success-Response:
     *{
        "i18n": {
            "name": [
                {
                "language": "zh_cn",
                "invoice_type_name": "咸鱼测试申请单"
                },
                {
                "language": "en_us",
                "invoice_type_name": "rookie"
                }
            ]
        },
        "id": "1085551213254115329",
        "deleted": null,
        "createdDate": null,
        "createdBy": null,
        "lastUpdatedDate": "2019-01-16T23:29:19.615+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": null,
        "enabled": null,
        "tenantId": null,
        "setOfBooksId": "1084658256482856961",
        "invoiceTypeCode": "YW001",
        "invoiceTypeName": "咸鱼测试申请单",
        "deductionFlag": "Y",
        "creationMethod": "CUSTOM",
        "invoiceCodeLength": "24",
        "invoiceNumberLength": "24",
        "defaultTaxRate": "5%",
        "interfaceMapping": "10086"
    }
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceType> updateInvoiceType(@RequestBody InvoiceType invoiceType){
        return ResponseEntity.ok(invoiceTypeService.updateInvoiceType(invoiceType));
    }

    /**
     * @api {GET}  /api/invoice/type/query 【发票类型定义-查询】
     * @apiDescription 发票类型定义-查询
     * @apiGroup InvoiceType
     * @apiParam {String} invoiceTypeCode 发票类型代码
     * @apiParam {String} invoiceTypeName 发票类型名称
     * @apiParam {String} deductionFlag 抵扣标志
     * @apiParam {Boolean} enabled 启用/禁用
     * @apiParam {Long} setOfBooksId 账套Id
     * @apiParam {String} interfaceMapping 接口映射值
     * @apiParamExample json} Request-Param:
     *  http://127.0.0.1:9095/expense/api/invoice/type/query?invoiceTypeCode=SQ09
     * @apiSuccessExample {json} Success-Response:
     * [
        {
        "i18n": {},
        "id": "1085550080980779010",
        "deleted": false,
        "createdDate": "2019-01-16T22:51:22.515+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-16T22:51:22.517+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "enabled": true,
        "tenantId": "1083751703623680001",
        "setOfBooksId": null,
        "invoiceTypeCode": "SQ09",
        "invoiceTypeName": "测试业务",
        "deductionFlag": "N",
        "creationMethod": "CUSTOM",
        "invoiceCodeLength": "12",
        "invoiceNumberLength": "12",
        "defaultTaxRate": "5%",
        "interfaceMapping": "1221"
        }
    ]
     *
     */
    @GetMapping(value = "query",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvoiceType>> pageInvoiceTypeByCond(@RequestParam(value = "invoiceTypeCode",required = false) String invoiceTypeCode,
                                                                   @RequestParam(value = "invoiceTypeName",required = false) String invoiceTypeName,
                                                                   @RequestParam(value = "deductionFlag",required = false) String deductionFlag,
                                                                   @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                   @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                   @RequestParam(value = "interfaceMapping",required = false) String interfaceMapping,
                                                                   @RequestParam(value = "page",defaultValue = "0") int page,
                                                                   @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<InvoiceType> invoiceTypeList =  invoiceTypeService.pageInvoiceTypeByCond(invoiceTypeCode, invoiceTypeName, deductionFlag, enabled,setOfBooksId,interfaceMapping,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(invoiceTypeList,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/invoice/type/sob/tenant/query 【发票类型定义-查询-租户、账套】
     * @apiDescription 查询当前租户下所有启用的，以及账套下所有启用的发票类型
     * @apiGroup InvoiceType
     * @apiParamExample {json} Request-Param:
     *   http://127.0.0.1:9095/expense/api/invoice/type
     * @apiSuccessExample {json} Success-Response:
     * [
            {
            "i18n": null,
            "id": "1087374219022475265",
            "deleted": false,
            "createdDate": "2019-01-21T23:39:50.935+08:00",
            "createdBy": "1083751705402064897",
            "lastUpdatedDate": "2019-01-21T23:39:50.941+08:00",
            "lastUpdatedBy": "1083751705402064897",
            "versionNumber": 1,
            "enabled": true,
            "tenantId": "1083751703623680001",
            "setOfBooksId": "1083762150064451585",
            "setOfBooksCode": null,
            "setOfBooksName": null,
            "invoiceTypeCode": "Test001",
            "invoiceTypeName": "测试001",
            "deductionFlag": "Y",
            "creationMethod": "CUSTOM",
            "invoiceCodeLength": "12",
            "invoiceNumberLength": "12",
            "defaultTaxRate": "13%",
            "interfaceMapping": "9527",
            "invoiceTypeMouldHeadColumn": null,
            "invoiceTypeMouldLineColumn": null
            }
    ]
     */
    @GetMapping("/sob/tenant/query")
    public ResponseEntity<List<InvoiceTypeDTO>> listInvoiceTypeBySobAndTenant(){
        return ResponseEntity.ok(invoiceTypeService.listInvoiceTypeBySobAndTenant());
    }

    /**
     * 给 我的票夹页面 的提供的 发票类型查询接口
     * @param tenantId
     * @return
     */
    @GetMapping("/query/for/invoice")
    public ResponseEntity<List<InvoiceType>> queryInvoiceTypeForInvoice(
            @RequestParam("tenantId") Long tenantId,
            @RequestParam("setOfBooksId") Long setOfBooksId){
        return ResponseEntity.ok(invoiceTypeService.queryInvoiceTypeForInvoice(tenantId,setOfBooksId));
    }

}
