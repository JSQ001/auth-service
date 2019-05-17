package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.payment.domain.PaymentRequisitionHeader;
import com.hand.hcf.app.payment.domain.enumeration.PaymentConstants;
import com.hand.hcf.app.payment.service.PaymentRequisitionHeaderService;
import com.hand.hcf.app.payment.utils.StringUtil;
import com.hand.hcf.app.payment.web.dto.AcpPaymentApprovalDTO;
import com.hand.hcf.app.payment.web.dto.ApprovalEntityDTO;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionHeaderWebDTO;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: bin.xie
 * @Description: 付款申请单controllor
 * @Date: Created in 12:02 2018/1/24
 * @Modified by
 */

/**
 * @apiDefine PaymentService 支付模块
 */
@Api(tags = "付款申请单")
@AllArgsConstructor
@RestController
@RequestMapping("/api/acp/requisition/header")
public class PaymentRequisitionHeaderController {
    private final PaymentRequisitionHeaderService headerService;

    private ExcelExportService excelExportService;

    /**
     * @api {POST} {{payment-service_url}}/api/acp/requisition/header
     * @apiGroup PaymentService
     * @apiDescription 创建新的付款申请单头信息
     * @apiParam (请求参数) {Long} acpReqTypeId 付款申请单类型ID
     * @apiParam (请求参数) {Long} companyId 公司ID
     * @apiParam (请求参数) {String} description 描述
     * @apiParam (请求参数) {Long} employeeId 员工ID
     * @apiParam (请求参数) {BigDecimal} functionAmount 金额
     * @apiParam (请求参数) {ZonedDateTime} requisitionDate 申请日期
     * @apiParam (请求参数) {Long} unitId 部门ID
     * @apiParam (请求参数) {String} attachmentOid 附件Oids
     *
     * @apiParamExample {json}请求样例：
     * {
       "acpReqTypeId": "988598122356269058",
       "companyId": 928,
       "description": "232323",
       "employeeId": "177601",
       "functionAmount": 0,
       "requisitionDate": "2018-05-24T09:28:22.681Z",
       "unitId": "625575",
       "attachmentOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5,6bdfc6b0-ed2c-442f-8743-22cd522c28c5"
            }
     * @apiSuccess (Success 200) {Long} id 生成的ID
     * @apiSuccess (Success 200) {Long} companyId 公司ID
     * @apiSuccess (Success 200) {String} companyName 公司名称
     * @apiSuccess (Success 200) {Long} unitId 部门ID
     * @apiSuccess (Success 200) {String} unitName 部门名称
     * @apiSuccess (Success 200) {Long} acpReqTypeId 付款申请单类型ID
     * @apiSuccess (Success 200) {String} acpReqTypeName 付款申请单类型名称
     * @apiSuccess (Success 200) {Long} employeeId 员工ID
     * @apiSuccess (Success 200) {String} employeeName 员工名称
     * @apiSuccess (Success 200) {String} requisitionNumber 付款申请单编号
     * @apiSuccess (Success 200) {ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (Success 200) {BigDecimal} functionAmount 总金额
     * @apiSuccess (Success 200) {String} description 说明
     * @apiSuccess (Success 200) {int} status 状态 1001-新建 1002-提交 1003-撤回 1004-审批通过 1005-审批驳回
     * @apiSuccess (Success 200) {UUID} documentOid 单据OID
     * @apiSuccess (Success 200) {int} documentType 单据类型
     * @apiSuccess (Success 200) {UUID} formOid 关联表单OID
     * @apiSuccess (Success 200) {UUID} unitOid 部门OID
     * @apiSuccess (Success 200) {UUID} applicantOid 申请人OID
     * @apiSuccess (Success 200) {String} submitDate 提交日期
     * @apiSuccess (Success 200) {String} createdName 创建人名称
     * @apiSuccess (Success 200) {int} versionNumber 版本号
     * @apiSuccess (Success 200) {Boolean} isEnabled 是否启用
     * @apiSuccess (Success 200) {Boolean} isDeleted 是否删除
     * @apiSuccess (Success 200) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (Success 200) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (Success 200) {Long} lastUpdatedBy 最后更新人ID
     * @apiSuccess (Success 200) {Long} createdBy 创建人ID
     * @apiSuccess (Success 200) {Object} [paymentRequisitionLineDTO] 付款申请单行信息--为空
     * @apiSuccess (Success 200) {Object} [paymentRequisitionNumberDTO] 币种金额信息--为空
     * @apiSuccessExample {json} 成功返回样例:
     *     {
    "id": "999826002130374657",
    "companyId": "928",
    "companyName": "清浅集团",
    "unitId": "625575",
    "unitName": "预算部",
    "acpReqTypeId": "988598122356269058",
    "acpReqTypeName": "测试付款申请单",
    "employeeId": "177601",
    "employeeName": "清浅",
    "requisitionNumber": "ACP201805250000000029",
    "requisitionDate": "2018-05-25T01:38:22.673Z",
    "functionAmount": 0.0,
    "description": "23232323",
    "status": 1001,
    "documentOid": "aebedd10-e490-4909-84dc-aac69d67375a",
    "documentType": "801005",
    "formOid": "6cf1d81c-ef0e-4f31-ad02-ccf50d42c773",
    "unitOid": "41b7be80-04b8-4527-9073-e2cf95c9b914",
    "applicantOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "versionNumber": 1,
    "paymentRequisitionLineDTO": null,
    "createdName": "清浅",
    "submitDate": null,
    "paymentRequisitionNumberDTO": null,
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-05-25T09:34:29.431+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-05-25T09:34:29.432+08:00",
    "listAttachmentOid": null,
    "attachmentOid":"6bdfc6b0-ed2c-442f-8743-22cd522c28c5,6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "attachments":null,
    "lastUpdatedBy": 177601
    }

     */
    @ApiOperation(value = "创建新的付款申请单头信息", notes = "创建新的付款申请单头信息 开发：")
    @PostMapping
    public ResponseEntity createHeader(@ApiParam(value = "付款申请单头信息") @RequestBody PaymentRequisitionHeader header){

        return ResponseEntity.ok(headerService.createHeader(header));
    }

    @ApiOperation(value = "修改付款申请单头信息", notes = "修改付款申请单头信息 开发：")
    @PutMapping
    public ResponseEntity updateHeader(@ApiParam(value = "付款申请单头信息") @RequestBody PaymentRequisitionHeader header){

        return ResponseEntity.ok(headerService.updateHeader(header));
    }

    /**
     * @api {DELETE} {{payment-service_url}}/api/acp/requisition/header/:id
     * @apiGroup PaymentService
     * @apiDescription 根据ID删除付款申请单
     * @apiParam (请求参数) {Long} id 付款申请单头ID
     */
    @ApiOperation(value = "根据ID删除付款申请单", notes = "根据ID删除付款申请单 开发：")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteHeader(@PathVariable Long id){
        headerService.deleteHeaderById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/requisition/header/query
     * @apiGroup PaymentService
     * @apiDescription 分页查询付款申请单信息
     * @apiParam (请求参数) {String} [requisitionNumber] 付款申请单编号
     * @apiParam (请求参数) {Long} [employeeId] 员工ID
     * @apiParam (请求参数) {Long} [acpReqTypeId] 付款申请单类型ID
     * @apiParam (请求参数) {Long} [status] 状态
     * @apiParam (请求参数) {String} [requisitionDateFrom] 申请日期从
     * @apiParam (请求参数) {String} [requisitionDateTo] 申请日期至
     * @apiParam (请求参数) {BigDecimal} [functionAmountFrom] 金额从
     * @apiParam (请求参数) {BigDecimal} [functionAmountTo] 金额至
     * @apiParam (请求参数) {String} [description] 说明
     * @apiParam (请求参数) {int} [page=0]   当前页
     * @apiParam (请求参数) {int} [size=20]  每页多少条
     * @apiParamExample {json}请求样例:
     * ?page=0&size=10&requisitionNumber=1111
     *
     * @apiSuccess (Success 200) {Long} id 付款申请单头ID
     * @apiSuccess (Success 200) {Long} companyId 公司ID
     * @apiSuccess (Success 200) {String} companyName 公司名称
     * @apiSuccess (Success 200) {Long} unitId 部门ID
     * @apiSuccess (Success 200) {String} unitName 部门名称
     * @apiSuccess (Success 200) {Long} acpReqTypeId 付款申请单类型ID
     * @apiSuccess (Success 200) {String} acpReqTypeName 付款申请单类型名称
     * @apiSuccess (Success 200) {Long} employeeId 员工ID
     * @apiSuccess (Success 200) {String} employeeName 员工名称
     * @apiSuccess (Success 200) {String} requisitionNumber 付款申请单编号
     * @apiSuccess (Success 200) {ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (Success 200) {BigDecimal} functionAmount 总金额
     * @apiSuccess (Success 200) {String} description 说明
     * @apiSuccess (Success 200) {int} status 状态
     * @apiSuccess (Success 200) {UUID} documentOid 单据OID
     * @apiSuccess (Success 200) {int} documentType 单据类型
     * @apiSuccess (Success 200) {UUID} formOid 关联表单OID
     * @apiSuccess (Success 200) {UUID} unitOid 部门OID
     * @apiSuccess (Success 200) {UUID} applicantOid 申请人OID
     * @apiSuccess (Success 200) {String} createdName 创建人名称
     * @apiSuccess (Success 200) {String} submitDate 提交日期
     * @apiSuccess (Success 200) {int} versionNumber 版本号
     * @apiSuccess (Success 200) {Boolean} isEnabled 是否启用
     * @apiSuccess (Success 200) {Boolean} isDeleted 是否删除
     * @apiSuccess (Success 200) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (Success 200) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (Success 200) {Long} lastUpdatedBy 最后更新人ID
     * @apiSuccess (Success 200) {Long} createdBy 创建人ID
     * @apiSuccess (Success 200) {String} [attachmentOid] 附件OIDs
     * @apiSuccess (Success 200) {List} [listAttachmentOid] 附件OID集合
     * @apiSuccess (Success 200) {List} [attachments] 附件信息
     * @apiSuccessExample {json} 成功返回样例:
     *  [
            {
            "id": "989751757453774849",
            "companyId": "928",
            "companyName": "清浅集团",
            "unitId": "625675",
            "unitName": "预算部1",
            "acpReqTypeId": "988598122356269058",
            "acpReqTypeName": "测试付款申请单",
            "employeeId": "177601",
            "employeeName": "清浅",
            "requisitionNumber": "ACP201804270000000055",
            "requisitionDate": "2018-04-27T14:23:01+08:00",
            "functionAmount": 20,
            "description": "谁道人生无再少，门前流水尚能西，休将白发唱黄鸡",
            "status": 1004,
            "documentOid": "d673bd8d-7480-442a-9237-9ab24083003c",
            "documentType": "801005",
            "formOid": "6cf1d81c-ef0e-4f31-ad02-ccf50d42c773",
            "unitOid": "f9941b12-652b-4758-b9e1-748342eea72a",
            "applicantOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
            "versionNumber": 5,
            "paymentRequisitionLineDTO": null,
            "createdName": "清浅",
            "submitDate": "2018-04-27",
            "paymentRequisitionNumberDTO": null,
            "isEnabled": true,
            "isDeleted": false,
            "createdDate": "2018-04-27T14:23:02+08:00",
            "createdBy": 177601,
            "lastUpdatedDate": "2018-04-27T15:04:45+08:00",
            "lastUpdatedBy": 177693,
            "listAttachmentOid": null,
            "attachmentOid":"6bdfc6b0-ed2c-442f-8743-22cd522c28c5,6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
            "attachments":null
            }
        ]
     */
    @ApiOperation(value = "分页查询付款申请单信息", notes = "分页查询付款申请单信息 开发：")
    @GetMapping(value = "/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<PaymentRequisitionHeaderWebDTO>> getHeaders(@ApiParam(value = "付款申请单编号") @RequestParam(required = false) String requisitionNumber,
                                                                           @ApiParam(value = "员工ID") @RequestParam(required = false) Long employeeId,
                                                                           @ApiParam(value = "付款申请单类型ID") @RequestParam(required = false) Long acpReqTypeId,
                                                                           @ApiParam(value = "状态") @RequestParam(required = false) String status,
                                                                           @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                           @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                           @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal functionAmountFrom,
                                                                           @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal functionAmountTo,
                                                                           @ApiParam(value = "说明") @RequestParam(required = false) String description,
                                                                           @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionHeaderWebDTO> lists = headerService.getHeaderByCondition(requisitionNumber,employeeId,
                acpReqTypeId,status,requisitionDateFrom,requisitionDateTo,functionAmountFrom,functionAmountTo,page,description);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/acp/requisition/header/query");
        return new ResponseEntity(lists,httpHeaders, HttpStatus.OK);
    }

    /**
     * @Author: bin.xie
     * @Description: 操作付款申请单
     * @param: operateType 操作类型  approving --审批中 approved --审批 approve-rejected --审批驳回
     * @param: id
     * @return: ResponseEntity
     * @Date: Created in 2018/1/24 14:55
     * @Modified by
     */
    @ApiOperation(value = "操作付款申请单", notes = "操作付款申请单 开发：")
    @PutMapping(value = "/{operateType}/{id}")
    public ResponseEntity<PaymentRequisitionHeader> operateHeader(@PathVariable Integer operateType,
                                                                  @PathVariable Long id){
        return ResponseEntity.ok(headerService.operateHeader(id,operateType));
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/requisition/header/query/:id
     * @apiGroup PaymentService
     * @apiDescription 根据ID查询付款申请单头信息
     * @apiParam (请求参数) {Long} id 付款申请单头ID
     *
     * @apiSuccess (Success 200) {Long} id 头ID
     * @apiSuccess (Success 200) {Long} companyId 公司ID
     * @apiSuccess (Success 200) {String} companyName 公司名称
     * @apiSuccess (Success 200) {Long} unitId 部门ID
     * @apiSuccess (Success 200) {String} unitName 部门名称
     * @apiSuccess (Success 200) {Long} acpReqTypeId 付款申请单类型ID
     * @apiSuccess (Success 200) {String} acpReqTypeName 付款申请单类型名称
     * @apiSuccess (Success 200) {Long} employeeId 员工ID
     * @apiSuccess (Success 200) {String} employeeName 员工名称
     * @apiSuccess (Success 200) {String} requisitionNumber 付款申请单编号
     * @apiSuccess (Success 200) {ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (Success 200) {BigDecimal} functionAmount 总金额
     * @apiSuccess (Success 200) {String} description 说明
     * @apiSuccess (Success 200) {int} status 状态
     * @apiSuccess (Success 200) {UUID} documentOid 单据OID
     * @apiSuccess (Success 200) {int} documentType 单据类型
     * @apiSuccess (Success 200) {UUID} formOid 关联表单OID
     * @apiSuccess (Success 200) {UUID} unitOid 部门OID
     * @apiSuccess (Success 200) {UUID} applicantOid 申请人OID
     * @apiSuccess (Success 200) {String} submitDate 提交日期
     * @apiSuccess (Success 200) {String} createdName 创建人名称
     * @apiSuccess (Success 200) {int} versionNumber 版本号
     * @apiSuccess (Success 200) {Boolean} isEnabled 是否启用
     * @apiSuccess (Success 200) {Boolean} isDeleted 是否删除
     * @apiSuccess (Success 200) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (Success 200) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (Success 200) {Long} lastUpdatedBy 最后更新人ID
     * @apiSuccess (Success 200) {Long} createdBy 创建人ID
     * @apiSuccess (Success 200) {Object} [paymentRequisitionLineDTO=null] 付款申请单行信息
     * @apiSuccess (Success 200) {Object} [paymentRequisitionNumberDTO=null] 币种金额信息
     * @apiSuccess (Success 200) {String} [attachmentOid] 附件OIDs
     * @apiSuccess (Success 200) {List} [listAttachmentOid] 附件OID集合
     * @apiSuccess (Success 200) {List} [attachments] 附件信息
     * @apiSuccessExample {json} 成功返回样例:
     * {
    "id": "989755833058942978",
    "companyId": "928",
    "companyName": "清浅集团",
    "unitId": "625675",
    "unitName": "预算部1",
    "acpReqTypeId": "988598122356269058",
    "acpReqTypeName": "测试付款申请单",
    "employeeId": "177601",
    "employeeName": "清浅",
    "requisitionNumber": "ACP201804270000000056",
    "requisitionDate": "2018-04-29T14:39:13+08:00",
    "functionAmount": 30,
    "description": "哀吾生之须臾，羡长江之无穷。",
    "status": 1004,
    "documentOid": "a92c0c34-d988-4a66-9c1b-c0002b0a1e6c",
    "documentType": "801005",
    "formOid": "6cf1d81c-ef0e-4f31-ad02-ccf50d42c773",
    "unitOid": "f9941b12-652b-4758-b9e1-748342eea72a",
    "applicantOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "versionNumber": 19,
    "paymentRequisitionLineDTO": null,
    "createdName": "清浅",
    "submitDate": "2018-05-18",
    "paymentRequisitionNumberDTO": null,
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-04-27T14:39:14+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-05-17T14:30:21+08:00",
    "lastUpdatedBy": 177693,
    "listAttachmentOid": null,
    "attachmentOid":"6bdfc6b0-ed2c-442f-8743-22cd522c28c5,6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "attachments":null
    }
     */
    @ApiOperation(value = "根据ID查询付款申请单头信息", notes = "根据ID查询付款申请单头信息 开发：")
    @GetMapping(value = "/query/{id}")
    public ResponseEntity<PaymentRequisitionHeaderWebDTO> getHeaderById(@PathVariable Long id){
        return ResponseEntity.ok(headerService.getHeaderById(id,false));
    }

    /**
     * @api {GET} {{service_url}}/api/acp/requisition/header/query/detail/:id
     * @apiGroup PaymentService
     * @apiDescription 根据ID查看付款申请单详细信息
     * @apiParam (请求参数) {Long} id 付款申请单头ID
     * @apiSuccessExample {json} 成功返回值:
     *{
    "id": "989755833058942978",
    "companyId": "928",
    "companyName": "清浅集团",
    "unitId": "625675",
    "unitName": "预算部1",
    "acpReqTypeId": "988598122356269058",
    "acpReqTypeName": "测试付款申请单",
    "employeeId": "177601",
    "employeeName": "清浅",
    "requisitionNumber": "ACP201804270000000056",
    "requisitionDate": "2018-04-29T14:39:13+08:00",
    "functionAmount": 30,
    "description": "哀吾生之须臾，羡长江之无穷。",
    "status": 1004,
    "documentOid": "a92c0c34-d988-4a66-9c1b-c0002b0a1e6c",
    "documentType": "801005",
    "formOid": "6cf1d81c-ef0e-4f31-ad02-ccf50d42c773",
    "unitOid": "f9941b12-652b-4758-b9e1-748342eea72a",
    "applicantOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "versionNumber": 19,
    "paymentRequisitionLineDTO": [
    {
    "id": "989756539371368450",
    "headerId": "989755833058942978",
    "cshTransactionId": "989742006093733889",
    "refDocumentType": "PUBLIC_REPORT",
    "refDocumentId": "989682575909838850",
    "refDocumentLineId": "989694547514613761",
    "companyId": "928",
    "companyName": "清浅集团",
    "partnerCategory": "EMPLOYEE",
    "partnerId": "177601",
    "partnerName": "清浅",
    "cshTransactionTypeCode": "",
    "cshTransactionClassId": "987208008816254977",
    "cshTransactionClassName": "14984测试付款现金事务类型",
    "cashFlowItemId": null,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "amount": 30,
    "functionAmount": 30,
    "lineDescription": "232323",
    "accountName": "21212231321312312312",
    "accountNumber": "1234123412341234123",
    "bankLocationCode": "啊宝的银行code",
    "bankLocationName": "招商银行",
    "provinceCode": null,
    "province": null,
    "cityCode": null,
    "cityName": null,
    "paymentMethodCategory": "OFFLINE_PAYMENT",
    "paymentMethodCategoryName": "线下",
    "contractHeaderId": "989043129767559170",
    "paymentScheduleLineId": "989043460400349186",
    "schedulePaymentDate": "2018-04-27T00:00:00+08:00",
    "availableAmount": 150,
    "freezeAmount": 200,
    "reportNumber": null,
    "refDocumentNumber": "PR180400162",
    "scheduleLineNumber": "2",
    "contractNumber": "CON20180400022",
    "contractLineNumber": "2",
    "contractDueDate": "2018-04-26",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-04-27T14:42:02+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-04-27T14:44:03+08:00",
    "lastUpdatedBy": 177601,
    "versionNumber": 2
    }
    ],
    "createdName": "清浅",
    "submitDate": "2018-05-18",
    "paymentRequisitionNumberDTO": [
    {
    "amount": 30,
    "functionAmount": 30,
    "countNumber": 1,
    "currencyCode": "CNY"
    }
    ],
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-04-27T14:39:14+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-05-17T14:30:21+08:00",
    "lastUpdatedBy": 177693,
    "listAttachmentOid": null,
    "attachmentOid":"6bdfc6b0-ed2c-442f-8743-22cd522c28c5,6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
    "attachments":null
    }
     */
    @ApiOperation(value = "根据ID查看付款申请单详细信息", notes = "根据ID查看付款申请单详细信息 开发：")
    @GetMapping(value = "/query/detail/{id}")
    public ResponseEntity<PaymentRequisitionHeaderWebDTO> getDocumentDetailById(@PathVariable Long id){
        return ResponseEntity.ok(headerService.getHeaderById(id,true));
    }


    /**
     * @Description: 付款申请单头行保存
     * @param: dto
     * @return
     * @Date: Created in 2018/7/9 22:20
     * @Modified by
     */
    @ApiOperation(value = "付款申请单头行保存", notes = "付款申请单头行保存 开发：")
    @PostMapping("/save")
    public ResponseEntity<PaymentRequisitionHeaderWebDTO> saveDocumentDetail(@ApiParam(value = "付款申请单头DTO") @RequestBody @Valid PaymentRequisitionHeaderWebDTO dto){

        return ResponseEntity.ok(headerService.saveDocumentDetail(dto));
    }

    /**
     * @Author: bin.xie
     * @Description: 根据付款申请单行ID删除行信息
     * @param: id  行ID
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/3/27 15:37
     * @Modified by
     */

    /**
     * @api {DELETE} {{payment-service_url}}/api/acp/requisition/header/line/{id}
     * @apiGroup PaymentService
     * @apiDescription 根据付款申请单行ID删除行信息
     * @apiParam (paymentRequisitionLineDTO) {Long} id  付款申请单行ID
     *
     */
    @ApiOperation(value = "根据付款申请单行ID删除行信息", notes = "根据付款申请单行ID删除行信息 开发：")
    @DeleteMapping("/line/{id}")
    public ResponseEntity deleteLineByLineId(@PathVariable Long id){
        headerService.deleteLineByLineId(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @Author: bin.xie
     * @Description: 根据头ID批量删除付款申请单
     * @param: ids   头ID 的集合
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/3/27 15:36
     * @Modified by
     */

    /**
     * @api {DELETE} {{payment-service_url}}/api/acp/requisition/header/deleteByIds
     * @apiGroup PaymentService
     * @apiDescription 根据付款申请单行ID删除行信息
     * @apiParam (paymentRequisitionLineDTO) {List} ids  付款申请单行ID
     *
     */
    @ApiOperation(value = "根据付款申请单行ID删除行信息", notes = "根据付款申请单行ID删除行信息 开发：bin.xie")
    @PostMapping("/deleteByIds")
    public ResponseEntity deleteHeaderByIds(@ApiParam(value = "付款申请单行ID") @RequestBody List<Long> ids){
        ids.stream().forEach(u -> headerService.deleteHeaderById(u));
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "提交付款申请单行信息", notes = "提交付款申请单行信息 开发：bin.xie")
    @RequestMapping(value = "/acpRequisition/submit",method = RequestMethod.POST)
    public ResponseEntity<Boolean> submit(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRe){
        return ResponseEntity.ok(headerService.submit(workFlowDocumentRe));
    }

    /**
     * @api {GET} /api/acp/requisition/header/approvals/filters
     * @apiDescription 获取未审批/已审批的付款申请单
     * @apiGroup PaymentService
     * @apiParam {String} userOid
     * @apiParam {String} businessCode
     * @apiParam {String} beginDate
     * @apiParam {boolean} finished
     * @apiParam {Long} typeId
     * @apiParam {String} endDate
     * @apiParam {Double} amountFrom
     * @apiParam {Double} amountTo
     * @apiParam {String} description
     * @apiParam {Pageable} pageable
     *
     * @author mh.z
     * @date 2019/02/25
     * @description 获取未审批/已审批的付款申请单
     */
    @ApiOperation(value = "获取未审批/已审批的付款申请单", notes = "获取未审批/已审批的付款申请单 开发：mh.z")
    @RequestMapping(value = "/approvals/filters", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApprovalEntityDTO>> getMyAcpPaymentApprovalList(@ApiParam(value = "用户oid") @RequestParam(value = "userOid", required = false) String userOid,
                                                                               @ApiParam(value = "业务编码") @RequestParam(value = "businessCode", required = false) String businessCode,
                                                                               @ApiParam(value = "开始日期") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                                               @ApiParam(value = "完成情况") @RequestParam(value = "finished", required = false) boolean finished,
                                                                               @ApiParam(value = "类型id") @RequestParam(value = "typeId", required = false) Long typeId,
                                                                               @ApiParam(value = "结束日期") @RequestParam(value = "endDate", required = false) String endDate,
                                                                               @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) Double amountFrom,
                                                                               @ApiParam(value = "金额至") @RequestParam(value = "amountTo", required = false) Double amountTo,
                                                                               @ApiParam(value = "描述") @RequestParam(value = "description", required = false) String description,
                                                                               @ApiIgnore Pageable pageable) throws URISyntaxException, ParseException {
        // 若参数是空字符串则转换成null
        userOid = StringUtils.isEmpty(userOid) ? null : userOid;
        businessCode = StringUtils.isEmpty(businessCode) ? null : businessCode;
        beginDate = StringUtils.isEmpty(beginDate) ? null : beginDate;
        endDate = StringUtils.isEmpty(endDate) ? null : endDate;
        description = StringUtils.isEmpty(description) ? null : description;

        // 过滤特殊字符
        if (businessCode != null) {
            businessCode = StringUtil.escapeSpecialCharacters(businessCode);
            businessCode = businessCode.toUpperCase();
        }

        // 获取未审批/已审批的付款申请单
        List<PaymentRequisitionHeaderWebDTO> list = headerService.listApprovalPayment(finished, beginDate, endDate, userOid, businessCode,
                typeId, amountFrom, amountTo, description, pageable);

        Page page = PageUtil.getPage(pageable);
        List<ApprovalEntityDTO> result = new ArrayList<ApprovalEntityDTO>();
        if(list.size() > 0) {
            page = list.get(0).getPage();
            //组装数据（保持和历史结构一致）
            list.stream().forEach(u -> {
                // userList.size();
                ApprovalEntityDTO approvalEntityDTO = new ApprovalEntityDTO();
                AcpPaymentApprovalDTO acpPaymentApprovalDTO = new AcpPaymentApprovalDTO();

                //申请人
                String applicantOidStr = u.getApplicantOid();
                if (applicantOidStr != null) {
                    UUID applicantOid = UUID.fromString(applicantOidStr);
                    ContactCO userCO = headerService.getUserByOid(applicantOid);
                    if (userCO != null) {
                        acpPaymentApprovalDTO.setApplicantName(userCO.getFullName()); //申请人姓名
                        acpPaymentApprovalDTO.setApplicantCode(userCO.getEmployeeCode());
                    }
                    acpPaymentApprovalDTO.setApplicationOid(applicantOid);
                }

                //表单
                String formOidStr = u.getFormOid();
                if (formOidStr != null) {
                    UUID formOid = UUID.fromString(formOidStr);
                    ApprovalFormCO approvalFormCO = headerService.getApprovalFormByOid(formOid);
                    if (approvalFormCO != null) {
                        acpPaymentApprovalDTO.setFormName(approvalFormCO.getFormName()); //表单名称
                    }
                    acpPaymentApprovalDTO.setFormOid(formOidStr); //设置表单oid
                    acpPaymentApprovalDTO.setFormType(String.valueOf(PaymentConstants.ACP_REQUISITION_ENTITY_TYPE)); //设置单据类型
                }

                //提交时间
                acpPaymentApprovalDTO.setSubmittedDate(null);
                //币种暂时默认处理为CNY
                acpPaymentApprovalDTO.setCurrencyCode("CNY");
                //设置单据状态--和预算日记账的一样
                acpPaymentApprovalDTO.setStatus(u.getStatus());
                //设置单据类型
                acpPaymentApprovalDTO.setFormType(String.valueOf(PaymentConstants.ACP_REQUISITION_ENTITY_TYPE));
                //设置单据code
                acpPaymentApprovalDTO.setAcpPaymentCode(u.getRequisitionNumber());
                //设置金额
                if (u.getFunctionAmount() != null) {
                    acpPaymentApprovalDTO.setTotalAmount(u.getFunctionAmount().doubleValue());
                }
                //设置预付款单类型 id和预付款单类型名称
                acpPaymentApprovalDTO.setPaymentReqTypeId(u.getAcpReqTypeId());
                acpPaymentApprovalDTO.setTypeName(u.getAcpReqTypeName());
                acpPaymentApprovalDTO.setDescription(u.getDescription());
                acpPaymentApprovalDTO.setStringSubmitDate(u.getSubmitDate());
                //设置单据id
                acpPaymentApprovalDTO.setId(u.getId());
                //设置单据oid
                approvalEntityDTO.setEntityOid(UUID.fromString(u.getDocumentOid()));
                approvalEntityDTO.setEntityType(PaymentConstants.ACP_REQUISITION_ENTITY_TYPE);
                approvalEntityDTO.setAcpPaymentApprovalView(acpPaymentApprovalDTO);
                result.add(approvalEntityDTO);
            });
        }

        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/acp/requisition/header/approvals/filters");
        ResponseEntity responseEntity = new ResponseEntity(result, httpHeaders, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * @api {GET} /api/acp/requisition/header/query/created 查询已创建调整单的申请人
     * @apiGroup PaymentService
     */
    @ApiOperation(value = "获取未审批/已审批的付款申请单", notes = "获取未审批/已审批的付款申请单 开发：")
    @GetMapping("/query/created")
    public List<ContactCO> listUsersByCreatedPaymentRequisitionHeaders(){

        return headerService.listUsersByCreatedPaymentRequisitionHeaders();
    }

    /**
     * getHeadersByCond : 根据付款申请单头表ID查询对应行表关联的报账单行表ID集合
     */

    @GetMapping(value = "/query/reportlineids/{id}")
    @ApiOperation(value = "根据付款申请单头表ID查询对应行表关联的报账单行表ID集合", notes = "根据付款申请单头表ID查询对应行表关联的报账单行表ID集合 开发:赵旭东")
    public ResponseEntity<List<Long>> getReortLineIds(@PathVariable Long id){
        return new ResponseEntity(headerService.getReportLineIds(id), HttpStatus.OK);
    }

    /**
     * 付款申请单财务查询
     * @api {GET} /api/acp/requisition/header/query/dto
     * @apiGroup PaymentService
     */
    @ApiOperation(value = "付款申请单财务查询", notes = "付款申请单财务查询 开发：")
    @GetMapping(value = "/query/dto")
    public ResponseEntity<List<PaymentRequisitionHeaderWebDTO>> getHeadersByCond(@ApiParam(value = "申请单编号") @RequestParam(required = false) String requisitionNumber,
                                                                                 @ApiParam(value = "账套id") @RequestParam(required = false) Long setOfBooksId,
                                                                                 @ApiParam(value = "公司id") @RequestParam(required = false) Long companyId,
                                                                                 @ApiParam(value = "付款申请单类型id") @RequestParam(required = false) Long acpReqTypeId,
                                                                                 @ApiParam(value = "员工id") @RequestParam(required = false) Long employeeId,
                                                                                 @ApiParam(value = "状态") @RequestParam(required = false) String status,
                                                                                 @ApiParam(value = "部门id") @RequestParam(required = false) Long unitId,
                                                                                 @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                                 @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                                 @ApiParam(value = "付款金额从") @RequestParam(required = false) BigDecimal payAmountFrom,
                                                                                 @ApiParam(value = "付款金额至") @RequestParam(required = false) BigDecimal payAmountTo,
                                                                                 @ApiParam(value = "功能金额从") @RequestParam(required = false) BigDecimal functionAmountFrom,
                                                                                 @ApiParam(value = "功能金额至") @RequestParam(required = false) BigDecimal functionAmountTo,
                                                                                 @ApiParam(value = "描述") @RequestParam(required = false) String description,
                                                                                 @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                 @ApiParam(value = "每页多少条") @RequestParam(value = "size", required = false, defaultValue = "10") int size) throws URISyntaxException {
        Page mybatisPage = PageUtil.getPage(page, size);
        Page<PaymentRequisitionHeaderWebDTO> lists = headerService.getHeaderByCond(
                requisitionNumber,
                setOfBooksId,
                companyId,
                acpReqTypeId,
                employeeId,
                status,
                unitId,
                requisitionDateFrom,
                requisitionDateTo,
                payAmountFrom,
                payAmountTo,
                functionAmountFrom,
                functionAmountTo,
                description,
                false,
                mybatisPage
        );
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity(lists.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 付款申请单财务查询 (数据权限控制)
     * @api {GET} /api/acp/requisition/header/query/dto/enable/dataAuth
     * @apiGroup PaymentService
     */
    @ApiOperation(value = "付款申请单财务查询(数据权限控制)", notes = "付款申请单财务查询(数据权限控制) 开发：")
    @GetMapping(value = "/query/dto/enable/dataAuth")
    public ResponseEntity<List<PaymentRequisitionHeaderWebDTO>> getHeadersByCondDataAuth(@ApiParam(value = "申请单编号") @RequestParam(required = false) String requisitionNumber,
                                                                                         @ApiParam(value = "账套id") @RequestParam(required = false) Long setOfBooksId,
                                                                                         @ApiParam(value = "公司id") @RequestParam(required = false) Long companyId,
                                                                                         @ApiParam(value = "付款申请单类型") @RequestParam(required = false) Long acpReqTypeId,
                                                                                         @ApiParam(value = "员工id") @RequestParam(required = false) Long employeeId,
                                                                                         @ApiParam(value = "状态") @RequestParam(required = false) String status,
                                                                                         @ApiParam(value = "部门id") @RequestParam(required = false) Long unitId,
                                                                                         @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                                         @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                                         @ApiParam(value = "付款金额从") @RequestParam(required = false) BigDecimal payAmountFrom,
                                                                                         @ApiParam(value = "付款金额至") @RequestParam(required = false) BigDecimal payAmountTo,
                                                                                         @ApiParam(value = "功能金额从") @RequestParam(required = false) BigDecimal functionAmountFrom,
                                                                                         @ApiParam(value = "功能金额至") @RequestParam(required = false) BigDecimal functionAmountTo,
                                                                                         @ApiParam(value = "描述") @RequestParam(required = false) String description,
                                                                                         @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                         @ApiParam(value = "每页多少条") @RequestParam(value = "size", required = false, defaultValue = "10") int size) throws URISyntaxException {
        Page mybatisPage = PageUtil.getPage(page, size);
        Page<PaymentRequisitionHeaderWebDTO> lists = headerService.getHeaderByCond(
                requisitionNumber,
                setOfBooksId,
                companyId,
                acpReqTypeId,
                employeeId,
                status,
                unitId,
                requisitionDateFrom,
                requisitionDateTo,
                payAmountFrom,
                payAmountTo,
                functionAmountFrom,
                functionAmountTo,
                description,
                true,
                mybatisPage
        );
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity(lists.getRecords(),httpHeaders, HttpStatus.OK);
    }

    /**
     * 付款申请单财务查询打印
     */
    @ApiOperation(value = "付款申请单财务查询打印", notes = "付款申请单财务查询打印 开发：")
    @PostMapping("export/header")
    public void exportHeader(@ApiParam(value = "申请单编号") @RequestParam(required = false) String requisitionNumber,
                             @ApiParam(value = "公司id") @RequestParam(required = false) Long companyId,
                             @ApiParam(value = "付款申请单类型id") @RequestParam(required = false) Long acpReqTypeId,
                             @ApiParam(value = "员工id") @RequestParam(required = false) Long employeeId,
                             @ApiParam(value = "状态") @RequestParam(required = false) String status,
                             @ApiParam(value = "部门id") @RequestParam(required = false) Long unitId,
                             @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                             @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                             @ApiParam(value = "付款金额从") @RequestParam(required = false) BigDecimal payAmountFrom,
                             @ApiParam(value = "付款金额至") @RequestParam(required = false) BigDecimal payAmountTo,
                             @ApiParam(value = "功能金额从") @RequestParam(required = false) BigDecimal functionAmountFrom,
                             @ApiParam(value = "功能金额至") @RequestParam(required = false) BigDecimal functionAmountTo,
                             @ApiParam(value = "描述") @RequestParam(required = false) String description,
                             @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        Page mybatisPage = PageUtil.getPage(1, 0);
        headerService.getHeaderByCond(
                requisitionNumber,
                null,
                companyId,
                acpReqTypeId,
                employeeId,
                status,
                unitId,
                requisitionDateFrom,
                requisitionDateTo,
                payAmountFrom,
                payAmountTo,
                functionAmountFrom,
                functionAmountTo,
                description,
                true,
                mybatisPage
        );
        int total = TypeConversionUtils.parseInt(mybatisPage.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<PaymentRequisitionHeaderWebDTO, PaymentRequisitionHeaderWebDTO>() {

            public int getTotal() {
                return total;
            }


            public List<PaymentRequisitionHeaderWebDTO> queryDataByPage(Page mybatisPage) {
                Page<PaymentRequisitionHeaderWebDTO> result = headerService.getHeaderByCond(
                        requisitionNumber,
                        null,
                        companyId,
                        acpReqTypeId,
                        employeeId,
                        status,
                        unitId,
                        requisitionDateFrom,
                        requisitionDateTo,
                        payAmountFrom,
                        payAmountTo,
                        functionAmountFrom,
                        functionAmountTo,
                        description,
                        true,
                        mybatisPage
                );

                return result.getRecords();
            }


            public PaymentRequisitionHeaderWebDTO toDTO(PaymentRequisitionHeaderWebDTO t) {
                return t;
            }


            public Class<PaymentRequisitionHeaderWebDTO> getEntityClass() {
                return PaymentRequisitionHeaderWebDTO.class;
            }
        },threadNumber,request,response);
    }
}

