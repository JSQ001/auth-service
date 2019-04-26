package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.web.adapter.DomainObjectAdapter;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.service.PaymentRequisitionTypesService;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesAllDTO;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: bin.xie
 * @Description: 付款申请单类型Controller
 * @Date: Created in 11:59 2018/1/22
 * @Modified by
 */
@RestController
@RequestMapping("/api/acp/request/type")
public class PaymentRequisitionTypesController {

    private final PaymentRequisitionTypesService service;
    @Autowired
    private PaymentOrganizationService organizationService;

    public PaymentRequisitionTypesController(PaymentRequisitionTypesService service) {
        this.service = service;
    }

    /**
     * @Author: bin.xie
     * @Description: 付款申请单类型保存
     * @param: paymentRequisitionTypes
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/1/22 12:55
     * @Modified by
     */

    /**
     * @api {POST} {{payment-service_url}}/api/acp/request/type【付款申请单类型】保存付款申请类型
     * @apiGroup PaymentService
     * @apiDescription 保存付款申请类型
     * @apiParam (PaymentRequisitionTypesAllDTO) {PaymentRequisitionTypesAllDTO} paymentRequisitionTypesAllDTO  付款申请类型dto
     *
     */
    @PostMapping
    public ResponseEntity saveTypes(@RequestBody PaymentRequisitionTypesAllDTO paymentRequisitionTypesAllDTO){
        return ResponseEntity.ok(service.savePaymentRequstTypes(paymentRequisitionTypesAllDTO));
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/query【付款申请单类型】付款申请单类型查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型查询
     * @apiParam (PaymentRequisitionTypesAllDTO) {PaymentRequisitionTypesAllDTO} paymentRequisitionTypesAllDTO  付款申请类型dto
     *
     */
    @GetMapping("/query")
    public ResponseEntity<List<PaymentRequisitionTypesDTO>> getTypesByCondition(@RequestParam(required = false) Long setOfBooksId,
                                                                                @RequestParam(required = false) String acpReqTypeCode,
                                                                                @RequestParam(required = false) String description,
                                                                                Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionTypes> list = service.getTypesByCondition(setOfBooksId,acpReqTypeCode,description,page,false);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/query");
        return new ResponseEntity(list.stream().map(u -> toDTO(u)).collect(Collectors.toList()),httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/query/enable/dataAuth【付款申请单类型】付款申请单类型查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型查询
     * @apiParam (PaymentRequisitionTypesAllDTO) {PaymentRequisitionTypesAllDTO} paymentRequisitionTypesAllDTO  付款申请类型dto
     *
     */
    @GetMapping("/query/enable/dataAuth")
    public ResponseEntity<List<PaymentRequisitionTypesDTO>> getTypesByConditionEnableDataAuth(@RequestParam(required = false) Long setOfBooksId,
                                                                                              @RequestParam(required = false) String acpReqTypeCode,
                                                                                              @RequestParam(required = false) String description,
                                                                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionTypes> list = service.getTypesByCondition(setOfBooksId,acpReqTypeCode,description,page,true);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/query/enable/dataAuth");
        return new ResponseEntity(list.stream().map(u -> toDTO(u)).collect(Collectors.toList()),httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/query/{id}【付款申请单类型】付款申请单类型通过id查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型通过id查询
     * @apiParam (PaymentRequisitionTypesAllDTO) {PaymentRequisitionTypesAllDTO} paymentRequisitionTypesAllDTO  付款申请类型dto
     *
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<PaymentRequisitionTypesAllDTO> getTypeAndOtherInfoByTypeId(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.getTypesAllDTOById(id));
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/queryById/{id}【付款申请单类型】付款申请单类型查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型查询
     * @apiParam  {Long} id
     *
     */
    @GetMapping("/queryById/{id}")
    public ResponseEntity<PaymentRequisitionTypesDTO> getTypeById(@PathVariable("id") Long id){

        return ResponseEntity.ok(toDTO(service.getTypesById(id)));
    }

    /**
     * @Author: bin.xie
     * @Description: 实体类转客户端DTO
     * @param: paymentRequisitionTypes
     * @return: com.hand.hcf.app.payment.PaymentRequisitionTypesDTO
     * @Date: Created in 2018/1/22 14:45
     * @Modified by
     */
    public PaymentRequisitionTypesDTO toDTO(PaymentRequisitionTypes paymentRequisitionTypes){
        SetOfBooksInfoCO standardCO = organizationService.getSetOfBooksById(paymentRequisitionTypes.getSetOfBooksId());
        PaymentRequisitionTypesDTO paymentRequisitionTypesDTO = PaymentRequisitionTypesDTO
                .builder()
                .accordingAsRelated(paymentRequisitionTypes.getAccordingAsRelated())
                .acpReqTypeCode(paymentRequisitionTypes.getAcpReqTypeCode())
                .description(paymentRequisitionTypes.getDescription())
                .formName(paymentRequisitionTypes.getFormName())
                .formOid(paymentRequisitionTypes.getFormOid())
                .formType(paymentRequisitionTypes.getFormType())
                .related(paymentRequisitionTypes.getRelated())
                .relatedType(paymentRequisitionTypes.getRelatedType())
                .versionNumber(paymentRequisitionTypes.getVersionNumber())
                .applyEmployee(paymentRequisitionTypes.getApplyEmployee())
                .tenantId(paymentRequisitionTypes.getTenantId())
                .setOfBooksId(paymentRequisitionTypes.getSetOfBooksId())
                .setOfBooksCode(standardCO.getSetOfBooksCode())
                .setOfBooksName(standardCO.getSetOfBooksName())
                .build();
        DomainObjectAdapter.toDto(paymentRequisitionTypesDTO, paymentRequisitionTypes);


        return paymentRequisitionTypesDTO;
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/query/{setOfBooksId}/{companyId}【付款申请单类型】付款申请单类型查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型查询
     * @apiParam  {Long} setOfBooksId  账套id
     * @apiParam  {Long} companyId  公司id
     *
     */
    @GetMapping("/query/{setOfBooksId}/{companyId}")
    public ResponseEntity<List<PaymentRequisitionTypes>> getTypesByCompanyId(@PathVariable("setOfBooksId") Long setOfBooksId,
                                                                             @PathVariable("companyId") Long companyId){
        return new ResponseEntity(service.selectTypesByCompanyIdAndSetOfBooksId(setOfBooksId,companyId),null, HttpStatus.OK);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据当前员工查询可以新建的付款申请单类型
     * @param: setOfBooksId 账套ID
     * @param: companyId 机构ID
     * @param: acpReqTypeCode 类型代码
     * @param: description 名称
     * @param: pageable
     * @return: org.springframework.http.ResponseEntity<java.util.List<com.hand.hcf.app.payment.PaymentRequisitionTypesDTO>>
     * @Date: Created in 2018/3/30 16:41
     * @Modified by
     */
    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/{setOfBooksId}/{companyId}/query【付款申请单类型】根据当前员工查询可以新建的付款申请单类型
     * @apiGroup PaymentService
     * @apiDescription 根据当前员工查询可以新建的付款申请单类型
     * @apiParam {Long} setOfBooksId  账套id
     * @apiParam {Long} companyId  付款申请类型dto
     * @apiParam {String} acpReqTypeCode  类型代码
     * @apiParam (String) description  名称
     *
     */
    @ApiOperation(value = "获取用户有权限创建的付款申请单类型", notes = "获取用户有权限创建的付款申请单类型 修改： 赵旭东")
    @GetMapping("/{setOfBooksId}/{companyId}/query")
    public ResponseEntity<List<PaymentRequisitionTypesDTO>> getTypeBySetBookIdAndCompanyIdAndCondition(@PathVariable Long setOfBooksId,
                                                                                                       @PathVariable Long companyId,
                                                                                                       @RequestParam(required = false) String acpReqTypeCode,
                                                                                                       @RequestParam(required = false) String description,
                                                                                                       @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag,
                                                                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionTypes> list = service.getTypeBySetBookIdAndCompanyIdAndCondition(setOfBooksId,companyId,acpReqTypeCode,description,page, authFlag);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/" + setOfBooksId
                + "/" + companyId +"/query");
        return new ResponseEntity(list.stream().map(u -> toDTO(u)).collect(Collectors.toList()),httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/acp/request/type/queryByCompanyId/{companyId}【付款申请单类型】付款申请单类型查询
     * @apiGroup PaymentService
     * @apiDescription 付款申请单类型查询
     * @apiParam {Long} companyId  公司id
     * @apiParam {String} acpReqTypeCode
     * @apiParam {String} description  名称
     * @apiParam {Long} id  付款申请类型id
     *
     */
    @GetMapping("/queryByCompanyId/{companyId}")
    public ResponseEntity<List<PaymentRequisitionTypes>> getTypeByCompanyIdAndCondition(@PathVariable Long companyId,
                                                                                        @RequestParam(required = false) String acpReqTypeCode,
                                                                                        @RequestParam(required = false) String description,
                                                                                        @RequestParam(required = false) Long id,
                                                                                        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PaymentRequisitionTypes> list = service.getTypeByCompanyIdAndCondition(acpReqTypeCode,description,id,page,companyId);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/acp/request/type/queryByCompanyId/" + companyId );
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/acp/request/type/users 【付款申请单类型】根据单据id查询有该单据权限的用户
     * @apiGroup PaymentService
     */
    @GetMapping("/users")
    public ResponseEntity listUsersByPaymentRequisitionType(@RequestParam(value = "paymentReqTypeId")
                                                                        Long paymentReqTypeId,
                                                            @RequestParam(required = false) String userCode,
                                                            @RequestParam(required = false) String userName,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = service.listUsersByPaymentRequisitionType(paymentReqTypeId,
                userCode, userName, queryPage);

        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
