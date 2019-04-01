package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeMouldDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceTypeMouldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票模板定义
 * @date 2019/1/17 9:37
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/api/invoice/type/mould")
public class InvoiceTypeMouldController {
    @Autowired
    private InvoiceTypeMouldService invoiceTypeMouldService;

    /**
     * @api {POST} /api/invoice/type/mould/insertOrUpdate 【发票模板定义-新增/修改】
     * @apiDescription  据发票类型是否抵扣初始化不同的发票模板
     * @apiGroup InvoiceTypeMould
     * @apiParam {Object}  invoiceTypeMouldHeadColumn 发票类型模板头 domain 类
     * @apiParam {Long} invoiceTypeId 发票类型Id
     * @apiParam {String} invoiceDate 开票日期（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} invoiceNo 发票号码（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} checkCode 校验码（后6位）（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} currencyCode 币种（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} totalAmount价税合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} invoiceAmount 金额合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} taxTotalAmount 税额合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} remark 备注（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} buyerName 购方名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} buyerTaxNo 购方纳税人识别号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} buyerAddPh 购方地址/电话（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} buyerAccount 购方开户行/账号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} salerName 销方名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} salerTaxNo 销方纳税人识别号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} salerAddPh 销方地址/电话（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} salerAccount 销方开户行/账号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {Object} invoiceTypeMouldLineColumn 发票类型模板行 domain 类
     * @apiParam {Long} invoiceTypeId 发票类型Id
     * @apiParam {String} goodsName 货物或应税劳务、服务名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} specificationModel 规格型号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} unit 单位（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} num 数量（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} unitPrice 单价（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} detailAmount 金额（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} taxRate 税率（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParam {String} taxAmount 税额（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
     * @apiParamExample json} Request-Param:
     * {
        "invoiceTypeMouldHeadColumn": {
        "invoiceTypeId":"11",
        "invoiceDate": "REQUIRED",
        "invoiceNo": "REQUIRED",
        "invoiceCode": "REQUIRED",
        "machineNo":"ENABLED",
        "checkCode":"REQUIRED",
        "currencyCode":"REQUIRED",
        "totalAmount":"ENABLED",
        "invoiceAmount":"ENABLED",
        "taxTotalAmount":"REQUIRED",
        "remark":"ENABLED",
        "buyerName":"REQUIRED",
        "buyerTaxNo":"ENABLED",
        "buyerAddPh":"ENABLED",
        "buyerAccount":"REQUIRED",
        "salerName":"REQUIRED",
        "salerTaxNo":"ENABLED",
        "salerAddPh":"ENABLED",
        "salerAccount":"REQUIRED"
        },
        "invoiceTypeMouldLineColumn": {
        "invoiceTypeId":"11",
        "goodsName": "REQUIRED",
        "specificationModel": "REQUIRED",
        "unit": "ENABLED",
        "num":"REQUIRED",
        "unitPrice":"ENABLED",
        "detailAmount":"REQUIRED",
        "taxRate":"REQUIRED",
        "taxAmount":"REQUIRED"

        }
    }
     * @apiSuccessExample {json} Success-Response:
     * {
        "invoiceTypeMouldHeadColumn": {
        "id": "1085751747810443265",
        "createdDate": "2019-01-17T12:12:43.63+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-17T12:12:43.63+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "invoiceTypeId": "11",
        "invoiceDate": "REQUIRED",
        "invoiceNo": "REQUIRED",
        "invoiceCode": "REQUIRED",
        "machineNo": "ENABLED",
        "checkCode": "REQUIRED",
        "currencyCode": "REQUIRED",
        "totalAmount": "ENABLED",
        "invoiceAmount": "ENABLED",
        "taxTotalAmount": "REQUIRED",
        "remark": "ENABLED",
        "buyerName": "REQUIRED",
        "buyerTaxNo": "ENABLED",
        "buyerAddPh": "ENABLED",
        "buyerAccount": "REQUIRED",
        "salerName": "REQUIRED",
        "salerTaxNo": "ENABLED",
        "salerAddPh": "ENABLED",
        "salerAccount": "REQUIRED"
        },
        "invoiceTypeMouldLineColumn": {
        "id": "1085751748309565441",
        "createdDate": "2019-01-17T12:12:43.747+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-17T12:12:43.747+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "invoiceTypeId": "11",
        "goodsName": "REQUIRED",
        "specificationModel": "REQUIRED",
        "unit": "ENABLED",
        "num": "REQUIRED",
        "unitPrice": "ENABLED",
        "detailAmount": "REQUIRED",
        "taxRate": "REQUIRED",
        "taxAmount": "REQUIRED"
        }
    }
     */
    @PostMapping(value = "/insertOrUpdate",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity insertOrUpdateInvoiceTypeMould(@RequestBody InvoiceTypeMouldDTO invoiceTypeMouldDTO){
        return ResponseEntity.ok(invoiceTypeMouldService.insertOrUpdateInvoiceTypeMould(invoiceTypeMouldDTO));
    }
    /**
     * @api {GET}    /api/invoice/type/mould/query/{invoiceTypeId} 【发票模板定义-查询】
     * @apiDescription  根据发票类型Id获取发票模板
     * @apiGroup InvoiceTypeMould
     * @apiParam {Long}  invoiceTypeId 发票类型Id
     * @apiParamExample json} Request-Param:
     *  http://127.0.0.1:9095/expense/api/invoice/type/mould/query/11
     * @apiSuccessExample {json} Success-Response:
     * {
        "invoiceTypeMouldHeadColumn": {
        "id": "1085751747810443265",
        "createdDate": "2019-01-17T12:12:43.63+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-17T12:12:43.63+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "invoiceTypeId": "11",
        "invoiceDate": "REQUIRED",
        "invoiceNo": "REQUIRED",
        "invoiceCode": "REQUIRED",
        "machineNo": "ENABLED",
        "checkCode": "REQUIRED",
        "currencyCode": "REQUIRED",
        "totalAmount": "ENABLED",
        "invoiceAmount": "ENABLED",
        "taxTotalAmount": "REQUIRED",
        "remark": "ENABLED",
        "buyerName": "REQUIRED",
        "buyerTaxNo": "ENABLED",
        "buyerAddPh": "ENABLED",
        "buyerAccount": "REQUIRED",
        "salerName": "REQUIRED",
        "salerTaxNo": "ENABLED",
        "salerAddPh": "ENABLED",
        "salerAccount": "REQUIRED"
        },
        "invoiceTypeMouldLineColumn": {
        "id": "1085751748309565441",
        "createdDate": "2019-01-17T12:12:43.747+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-17T12:12:43.747+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "invoiceTypeId": "11",
        "goodsName": "REQUIRED",
        "specificationModel": "REQUIRED",
        "unit": "ENABLED",
        "num": "REQUIRED",
        "unitPrice": "ENABLED",
        "detailAmount": "REQUIRED",
        "taxRate": "REQUIRED",
        "taxAmount": "REQUIRED"
        }
    }
     */
    @GetMapping(value = "/query/{invoiceTypeId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceTypeMouldDTO> getInvoiceTypeMouldByTypeId(@PathVariable("invoiceTypeId") Long invoiceTypeId ){
        return ResponseEntity.ok(invoiceTypeMouldService.getInvoiceTypeMouldByTypeId(invoiceTypeId));
    }
}
