package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignCompany;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeAssignCompanyService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@RestController
@RequestMapping("/api/cash/pay/requisition/type/assign/companies")
public class CashPayRequisitionTypeAssignCompanyController {
    private final CashPayRequisitionTypeAssignCompanyService cashSobPayReqTypeAssignCompanyService;

    public CashPayRequisitionTypeAssignCompanyController(CashPayRequisitionTypeAssignCompanyService cashSobPayReqTypeAssignCompanyService){
        this.cashSobPayReqTypeAssignCompanyService = cashSobPayReqTypeAssignCompanyService;
    }

    /**
     * 批量新增 预付款单类型关联的公司表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/companies/batch 【类型关联公司】增公司
     * @apiDescription 预付款单类型批量分配公司
     * @apiGroup PrepaymentService
     * @apiParam {String} companyCode 公司code
     * @apiParam {Long} companyId 公司ID
     * @apiParam {Long} sobPayReqTypeId 预付款单类型Id
     * @apiParam {Long} versionNumber 版本号
     * @apiParam {Boolean} isEnabled    启用标志
     * @apiParam {Boolean} isDeleted    删除标志
     * @apiParam {ZonedDateTime} createdDate 创建时间
     * @apiParam {Long} createdBy 创建人id
     * @apiParam {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiParam {Long} lastUpdatedBy 最后更新人id
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} companyCode 公司code
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型Id
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} isEnabled    启用标志
     * @apiSuccess {Boolean} isDeleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    [
    {
    "companyCode":"GS00001",
    "companyId":"929",
    "companyName":null,
    "companyType":null,
    "createdBy":177601,
    "createdDate":"2018-05-31T14:41:02.348+08:00",
    "id":null,
    "isDeleted":false,
    "isEnabled":true,
    "lastUpdatedBy":177601,
    "lastUpdatedDate":"2018-05-31T14:41:02.348+08:00",
    "sobPayReqTypeId":"1002070555621064706",
    "versionNumber":1
    },
    {
    "companyCode":"GS00002",
    "companyId":"935",
    "companyName":null,
    "companyType":null,
    "createdBy":177601,
    "createdDate":"2018-05-31T14:41:02.348+08:00",
    "id":null,
    "isDeleted":false,
    "isEnabled":true,
    "lastUpdatedBy":177601,
    "lastUpdatedDate":"2018-05-31T14:41:02.348+08:00",
    "sobPayReqTypeId":"1002070555621064706",
    "versionNumber":1
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1002084195551694849",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-05-31T15:07:44.699+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2018-05-31T15:07:44.699+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 1,
    "sobPayReqTypeId": "1002070555621064706",
    "companyId": "929",
    "companyCode": "GS00001",
    "companyName": null,
    "companyType": null
    },
    {
    "id": "1002084195811741697",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-05-31T15:07:44.76+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2018-05-31T15:07:44.76+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 1,
    "sobPayReqTypeId": "1002070555621064706",
    "companyId": "935",
    "companyCode": "GS00002",
    "companyName": null,
    "companyType": null
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的公司不允许重复!",
    "errorCode": "10303"
    }
     */
    @PostMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignCompany>> createCashPayRequisitionTypeAssignCompanyBatch(@RequestBody List<CashPayRequisitionTypeAssignCompany> list){
        return ResponseEntity.ok(cashSobPayReqTypeAssignCompanyService.createCashPayRequisitionTypeAssignCompanyBatch(list));
    }

    /**
     * 批量修改 预付款单类型关联的公司表
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/type/assign/companies/batch 【类型关联公司】改公司
     * @apiDescription 批量修改预付款单类型关联的公司表
     * @apiGroup PrepaymentService
     * @apiParam {String} companyCode 公司code
     * @apiParam {Long} companyId 公司ID
     * @apiParam {Long} sobPayReqTypeId 预付款单类型Id
     * @apiParam {Long} versionNumber 版本号
     * @apiParam {Boolean} isEnabled    启用标志
     * @apiParam {Boolean} isDeleted    删除标志
     * @apiParam {ZonedDateTime} createdDate 创建时间
     * @apiParam {Long} createdBy 创建人id
     * @apiParam {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiParam {Long} lastUpdatedBy 最后更新人id
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} companyCode 公司code
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型Id
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} isEnabled    启用标志
     * @apiSuccess {Boolean} isDeleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    [
    {
    "companyCode":null,
    "companyId":null,
    "companyName":null,
    "companyType":null,
    "createdBy":null,
    "createdDate":null,
    "id":1002084195551694849,
    "isDeleted":null,
    "isEnabled":true,
    "lastUpdatedBy":177601,
    "lastUpdatedDate":"2018-05-31T14:41:02.348+08:00",
    "sobPayReqTypeId":null,
    "versionNumber":5
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1002084195551694849",
    "isEnabled": true,
    "isDeleted": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": "2018-05-31T16:01:42.611+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 6,
    "sobPayReqTypeId": null,
    "companyId": null,
    "companyCode": "null",
    "companyName": null,
    "companyType": null
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "版本号不一致，该数据已在其他客户端更新，请刷新！",
    "errorCode": "00017"
    }
     */
    @PutMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignCompany>> updateCashPayRequisitionTypeAssignCompanyBatch(@RequestBody List<CashPayRequisitionTypeAssignCompany> list){
        return ResponseEntity.ok(cashSobPayReqTypeAssignCompanyService.updateCashPayRequisitionTypeAssignCompanyBatch(list));
    }

    /**
     *根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param sobPayReqTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     *
     * @api {GET} /api/cash/pay/requisition/type/assign/companies/query?sobPayReqTypeId=1002070555621064706 【类型关联公司】查公司
     * @apiDescription 根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     * @apiGroup PrepaymentService
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} companyName 公司名称
     * @apiSuccess {String} companyType 公司类型
     * @apiSuccess {String} companyCode 公司code
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型Id
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} isEnabled    启用标志
     * @apiSuccess {Boolean} isDeleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiSuccessExample {json} Success-Result
     * [
    {
    "id": "1002084195811741697",
    "isEnabled": false,
    "isDeleted": false,
    "createdDate": "2018-05-31T15:07:45+08:00",
    "createdBy": 174342,
    "lastUpdatedDate": "2018-05-31T16:17:08+08:00",
    "lastUpdatedBy": 174342,
    "versionNumber": 4,
    "sobPayReqTypeId": "1002070555621064706",
    "companyId": "935",
    "companyCode": "GS00002",
    "companyName": "哈尔滨分公司",
    "companyType": "业务实体"
    }
    ]

     */
    @GetMapping("/query")
    public ResponseEntity<List<CashPayRequisitionTypeAssignCompany>> getCashPayRequisitionTypeAssignCompanyByCond(
            @RequestParam(value = "sobPayReqTypeId") Long sobPayReqTypeId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionTypeAssignCompany> list = cashSobPayReqTypeAssignCompanyService.getCashPayRequisitionTypeAssignCompanyByCond(sobPayReqTypeId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/companies/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param sobPayReqTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/companies/filter?&page=0&size=10&setOfBooksCode=?&sobPayReqTypeId=1002070555621064706 【类型关联公司】筛公司
     * @apiDescription 分配页面的公司筛选查询
     * @apiGroup PrepaymentService
     * @apiParam {Long} setOfBooksCode 账套code
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiParam {Long} [companyName] 公司名称
     * @apiParam {Long} [companyCode] 公司代码
     * @apiParam {Long} [companyCodeFrom] 公司代码从
     * @apiParam {Long} [companyCodeTo] 公司代码至
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} name 公司名称
     * @apiSuccess {String} attribute4 公司类型
     * @apiSuccess {String} code 公司code
     * @apiSuccessExample {json} Success-Result
     * [
    {
    "id": 935,
    "code": "GS00002",
    "name": "哈尔滨分公司",
    "description": null,
    "remark": null,
    "parentId": null,
    "parentOid": null,
    "groupId": null,
    "groupOid": null,
    "subordinateId": null,
    "subordinateOid": null,
    "detailId": null,
    "detailOid": null,
    "priority": null,
    "setOfBooksId": null,
    "tenantId": null,
    "companyId": null,
    "companyOId": null,
    "isEnabled": null,
    "isDeleted": null,
    "startDate": null,
    "invalidDate": null,
    "i18n": null,
    "attribute1": null,
    "attribute2": null,
    "attribute3": null,
    "attribute4": "业务实体",
    "attribute5": null,
    "attribute6": null,
    "attribute7": null,
    "attribute8": null,
    "attribute9": null,
    "attribute10": null,
    "attribute11": null,
    "attribute12": null,
    "attribute13": null,
    "attribute14": false,
    "detailList": null,
    "isPublic": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "oid": null
    }
    ]
     */
    @GetMapping("/filter")
    public ResponseEntity assignCompanyQuery(@RequestParam Long sobPayReqTypeId,
                                             @RequestParam(required = false) String companyCode,
                                             @RequestParam(required = false) String companyName,
                                             @RequestParam(required = false) String companyCodeFrom,
                                             @RequestParam(required = false) String companyCodeTo,
                                             Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = cashSobPayReqTypeAssignCompanyService.assignCompanyQuery(sobPayReqTypeId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);

        HttpHeaders headers = PageUtil.getTotalHeader(result);
        List<Map<String, Object>> list = new ArrayList<>();
        result.getRecords().stream().forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id",e.getId());
            map.put("code",e.getCompanyCode());
            map.put("name", e.getName());
            map.put("attribute4", e.getCompanyTypeName());
            list.add(map);
        });
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }
}
