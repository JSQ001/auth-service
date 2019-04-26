package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToCompany;
import com.hand.hcf.app.payment.service.PaymentRequisitionTypesToCompanyService;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesCompanyDTO;
import lombok.AllArgsConstructor;
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
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 11:37 2018/1/25
 * @Modified by
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/acp/request/type/company")
public class PaymentRequisitionTypesToCompanyController {
    private final PaymentRequisitionTypesToCompanyService service;

    /**
     * @Author: bin.xie
     * @Description: 查询该类别分配的公司
     * @param: contractTypeId
     * @param: setOfBooksId
     * @param: pageable
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/1/23 16:02
     * @Modified by
     */

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/company/{setOfBooksId}/queryCompany【付款申请单类型】付款申请单类型分配公司查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型分配公司查询
     * @apiParam {Long} setOfBooksId  账套id
     * @apiParam {Long} acpReqTypeId 类型id
     *
     */
    @GetMapping("/{setOfBooksId}/queryCompany")
    public ResponseEntity<List<PaymentRequisitionTypesToCompany>> getContractTypeAssignCompanys(@RequestParam Long acpReqTypeId, @PathVariable Long setOfBooksId,
                                                                                                Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionTypesToCompany> resultDTOList = service.getAcpReqTypeAssignCompanys(acpReqTypeId, page, setOfBooksId);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/" + setOfBooksId + "/queryCompany");
        return new ResponseEntity(resultDTOList,headers, HttpStatus.OK);
    }


    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/company/{setOfBooksId}/companies/query/filter【付款申请单类型】付款申请单类型分配公司过滤查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型分配公司过滤查询
     * @apiParam {Long} acpReqTypeId 类型id
     * @apiParam {String} companyCode 公司代码
     * @apiParam {String} companyName 公司名称
     * @apiParam {String} companyCodeFrom 公司代码从
     * @apiParam {String} companyCodeTo 公司代码至
     * @apiParam {Long} setOfBooksId 账套
     * @apiParam {Pageable} pageable 分页
     *
     */
    @GetMapping("/{setOfBooksId}/companies/query/filter")
    public ResponseEntity getCompanyByCondFiter(
            @RequestParam Long acpReqTypesId,
            @RequestParam(required = false) String companyCode,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String companyCodeFrom,
            @RequestParam(required = false) String companyCodeTo,
            @PathVariable Long setOfBooksId,
            Pageable pageable
    ) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CompanyCO> result = service.getCompanyByConditionFilter(setOfBooksId, acpReqTypesId, companyCode,
                companyName, companyCodeFrom, companyCodeTo, page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/companies/query/filter");
        List<Map<String, Object>> list = new ArrayList<>();
        result.stream().forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id",e.getId());
            map.put("code",e.getCompanyCode());
            map.put("name", e.getName());
            map.put("attribute4", e.getCompanyTypeName());
            list.add(map);
        });
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * @Author: bin.xie
     * @Description: 批量分配机构
     * @param: contractTypeToCompanyDto
     * @param: setOfBooksId
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/1/23 16:54
     * @Modified by
     */

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/company//{setOfBooksId}/batchAssignCompany【付款申请单类型分配公司】付款申请单类型分配公司
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型分配公司
     * @apiParam {Long} setOfBooksId 账套
     * @apiParam {Pageable} pageable 分页
     * @apiParamExample {json}请求样例:
     *  {
     *      "acpReqTypesId": 12,
     *      "companyIds": [1,2]
     *
     *  }
     *
     */
    @PostMapping("/{setOfBooksId}/batchAssignCompany")
    public ResponseEntity batchAssignCompany(@RequestBody PaymentRequisitionTypesCompanyDTO paymentRequisitionTypesCompanyDTO,
                                             @PathVariable Long setOfBooksId) {
        return ResponseEntity.ok(service.saveAcpReqTypesToCompany(paymentRequisitionTypesCompanyDTO));
    }

    /**
     * @api {put} {{payment-service_url}}/api/acp/request/type/company/{setOfBooksId}/updateCompany【付款申请单类型分配公司】更新付款申请单类型分配公司
     * @apiGroup PaymentService
     * @apiDescription 更新付款申请单类型分配公司
     * @apiParam {Long} setOfBooksId 账套
     * @apiParam {PaymentRequisitionTypesToCompany} paymentRequisitionTypesToCompany 付款申请单类型分配公司
     * @apiParamExample {json}请求样例:
     *  {
     *      "acpReqTypesId": 12,
     *      "companyId": 1,
     *      "id":1
     *  }
     *
     */
    @PutMapping("/{setOfBooksId}/updateCompany")
    public ResponseEntity updateCompanyEnabledById(@PathVariable Long setOfBooksId,
                                                   @RequestBody PaymentRequisitionTypesToCompany paymentRequisitionTypesToCompany){
        return ResponseEntity.ok(service.updateCompanyEnabledById(paymentRequisitionTypesToCompany));
    }
}
