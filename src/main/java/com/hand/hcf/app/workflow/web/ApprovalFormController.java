package com.hand.hcf.app.workflow.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.ApprovalFormProperty;
import com.hand.hcf.app.workflow.dto.ApprovalFormAllDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormForOtherRequestDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormPropertyInitDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormSummaryDTO;
import com.hand.hcf.app.workflow.enums.ApprovalFormEnum;
import com.hand.hcf.app.workflow.service.ApprovalFormPropertyService;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.app.workflow.util.HeaderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api")
public class ApprovalFormController {
    @Inject
    ApprovalFormService approvalFormService;
    @Inject
    ApprovalFormPropertyService approvalFormPropertyService;

    /**
     * 创建自定义表单
     *
     * @param dto
     * @return
     * @throws URISyntaxException
     */

    @RequestMapping(value = "/custom/forms", method = RequestMethod.POST)
    public ResponseEntity<ApprovalFormDTO> createCustomForm(@RequestBody @Valid ApprovalFormDTO dto) throws URISyntaxException {
        ApprovalFormDTO customForm = approvalFormService.createCustomForm(dto, OrgInformationUtil.getCurrentUserOid(), OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.created(new URI(("/api/custom/forms/" + customForm.getFormOid()))).headers(HeaderUtil.createEntityCreationAlert("custom.form.created", customForm.getFormOid().toString())).body(customForm);
    }

    /**
     * 表单修改
     *
     * @param dto
     * @return
     */

    @RequestMapping(value = "/custom/forms", method = RequestMethod.PUT)
    public ResponseEntity<ApprovalForm> updateCustomForm(@RequestBody @Valid ApprovalForm dto) {
        ApprovalForm approvalFormDTO = approvalFormService.updateApprovalForm(dto, OrgInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok(approvalFormDTO);
    }

    @RequestMapping(value = "/custom/forms/all", method = RequestMethod.PUT)
    public ResponseEntity<ApprovalFormAllDTO> updateCustomForm(@RequestBody ApprovalFormAllDTO dto) {
        ApprovalFormAllDTO approvalFormAllDTO = approvalFormService.updateApprovalForm(dto, OrgInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok(approvalFormAllDTO);
    }


    /**
     * for app 动态返回审批人
     *
     * @param formOid
     * @return
     */
    @RequestMapping(value = "/custom/forms/{formOid}", method = RequestMethod.GET)
    public ResponseEntity<ApprovalFormDTO> getCustomForm(@PathVariable UUID formOid) {
        ApprovalFormDTO customForm = approvalFormService.getDTOByOid(formOid);
        return ResponseEntity.ok(customForm);
    }

    //for 中控，不返回审批人
    @RequestMapping(value = "/custom/forms/{formOid}/simple", method = RequestMethod.GET)
    public ResponseEntity<ApprovalFormDTO> getCustomFormForCenterControl(@PathVariable UUID formOid) {
        ApprovalFormDTO customForm = approvalFormService.getCustomForm(formOid);
        return ResponseEntity.ok(customForm);
    }

    //for 中控，不返回审批人
    @RequestMapping(value = "/custom/forms/{formOid}/setup", method = RequestMethod.GET)
    public ResponseEntity<ApprovalFormDTO> getCustomFormForCenterControlExpense(@PathVariable UUID formOid) {
        ApprovalFormDTO customForm = approvalFormService.getCustomForm(OrgInformationUtil.getCurrentCompanyId(), formOid);
        return ResponseEntity.ok(customForm);
    }

    /**
     * 查询公司所有自定义表单 (包含禁用)
     *
     * @param pageable
     * @param fromType 1：公司表单  2：租户下表单
     * @return
     */
    //todo 添加分页
    @RequestMapping(value = "/custom/forms/company/all", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalForm>> getCompanyCustomForms(@RequestParam(value = "roleType", required = false) String roleType,
                                                                    @RequestParam(value = "fromType", required = false) String fromType,
                                                                    @RequestParam(value = "booksID", required = false) String booksId,
                                                                    @RequestParam(value = "formTypeId", required = false) Long formTypeId,
                                                                    @RequestParam(name = "formName", required = false) String formName,
                                                                    @RequestParam(name = "remark", required = false) String remark,
                                                                    @RequestParam(name = "valid", required = false) Boolean valid,
                                                                    Pageable pageable) {
        List<ApprovalForm> lists = null;
        Page page=PageUtil.getPage(pageable);
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType) && StringUtils.isNotEmpty(booksId)) {
            lists = approvalFormService.listDTOByTenantAndCondition(OrgInformationUtil.getCurrentTenantId(), formTypeId,formName,remark,valid,page);
            return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
        }
        if (String.valueOf(ApprovalFormEnum.CUSTOMER_FROM_TENANT.getId()).equals(fromType)) {
            lists = approvalFormService.listByCondition(null,null,null,page);
            return ResponseEntity.ok(lists);
        } else {
            lists = approvalFormService.listByCompanyId(OrgInformationUtil.getCurrentCompanyId());
        }
        return ResponseEntity.ok(lists);
    }


    /**
     * 用户查询可用表单
     *
     * @api {GET} /api/custom/forms/company/my/available/all?formType=101 用户查询可用表单
     * @apiGroup ApprovalForm
     * @apiParam {String} formType 表单类型101:申请表单，102报销单表单，103全部
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "formOid": "659dbb76-4e81-42d4-8b6b-2447f7fe8608",
     * "formName": "订票申请",
     * "iconName": "",
     * "name": "",
     * "formType": 2003
     * },
     * {
     * "formOid": "a1b7add3-d349-4e17-8674-53705a30d595",
     * "formName": "借款申请",
     * "iconName": "loan_application",
     * "name": "loan_application_form",
     * "formType": 2005
     * }
     * ]
     */
   /* @RequestMapping(value = "/custom/forms/company/my/available/all", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormSummaryDTO>> getAvailableCustomForm(@RequestParam(name = "formType", required = true) Integer formType,
                                                                             @RequestParam(name = "available",required = false) Long available) {
        if ("".equals(available) || null == available){
            available = null;
        }
        List<ApprovalFormDTO> customFormDTOs = customFormService.getAvailableCustomForm(OrgInformationUtil.getCurrentUserOid(), formType,available);
        return ResponseEntity.ok(customFormDTOs.stream().map(ApprovalFormMapper::convertToSummaryDTO).collect(Collectors.toList()));
    }*/

    /**
     * 用户查询所有表单
     *
     * @api {GET} /api/custom/forms/company/my/available/get/all?formType=101 用户查询所有表单
     * @apiGroup ApprovalForm
     * @apiParam {String} formType 表单类型101:申请表单，102报销单表单，103全部
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "formOid": "659dbb76-4e81-42d4-8b6b-2447f7fe8608",
     * "formName": "订票申请",
     * "iconName": "",
     * "name": "",
     * "formType": 2003
     * },
     * {
     * "formOid": "a1b7add3-d349-4e17-8674-53705a30d595",
     * "formName": "借款申请",
     * "iconName": "loan_application",
     * "name": "loan_application_form",
     * "formType": 2005
     * }
     * ]
     */
   /* @RequestMapping(value = "/custom/forms/company/my/available/get/all", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormSummaryDTO>> getCustomFormList(@RequestParam(name = "formType", required = true) Integer formType){
        List<ApprovalFormDTO> customFormDTOs = customFormService.getAvailableCustomForm(OrgInformationUtil.getCurrentUserOid(), formType,1L);
        return ResponseEntity.ok(customFormDTOs.stream().map(ApprovalFormMapper::convertToSummaryDTO).collect(Collectors.toList()));

    }*/

    /**
     * 用户查询可用帐套级表单
     *
     * @api {GET} /api/custom/forms/setOfBooks/my/available/all?formTypeId=801008&setOfBooksId=111111111 用户查询可用帐套级表单
     * @apiGroup ApprovalForm
     * @apiParam {String} formTypeId 表单类型 801008,801002 ...
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "formOid": "659dbb76-4e81-42d4-8b6b-2447f7fe8608",
     * "formName": "订票申请",
     * "tenant": "",
     * "formType": 2003
     * },
     * {
     * "formOid": "659dbb76-4e81-42d4-8b6b-2447f7fe8608",
     * "formName": "订票申请",
     * "tenant": "",
     * "formType": 2003
     * }
     * ]
     */
    @RequestMapping(value = "/custom/forms/setOfBooks/my/available/all", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormSummaryDTO>> listAvailableForms(@RequestParam(name = "formTypeId", required = true) Integer formTypeId,
                                                                          @RequestParam(name = "setOfBooksId", required = false) Long setOfBooksId) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<ApprovalFormSummaryDTO> approvalFormSummaryDTOS = approvalFormService.listByFormType(tenantId, formTypeId);
        return ResponseEntity.ok(approvalFormSummaryDTOS);
    }


    //启用
/*
    @RequestMapping(value = "/custom/forms/enable/{formOid}", method = RequestMethod.POST)
    public ResponseEntity<ApprovalFormDTO> enableCustomForm(@PathVariable UUID formOid) {
        return ResponseEntity.ok(customFormService.enableCustomForm(formOid));
    }*/

    //禁用

  /*  @RequestMapping(value = "/custom/forms/disable/{formOid}", method = RequestMethod.POST)
    public ResponseEntity<ApprovalFormDTO> disableCustomForm(@PathVariable UUID formOid) {
        return ResponseEntity.ok(customFormService.disableCustomForm(formOid));
    }*/


    /**
     * 初始化申请单配置（指定表单）
     *
     * @param approvalFormPropertyInitDTO
     * @return
     */
    @RequestMapping(value = "/custom/forms/property/init", method = RequestMethod.POST)
    public Boolean exportCustomFormProperty(@RequestBody ApprovalFormPropertyInitDTO approvalFormPropertyInitDTO) {
        List<ApprovalFormProperty> approvalFormPropertyList = approvalFormPropertyInitDTO.getApprovalFormPropertyList();
        if (CollectionUtils.isEmpty(approvalFormPropertyList)) {
            ApprovalFormPropertyInitDTO formPropertyInitDTO = new ApprovalFormPropertyInitDTO();
            approvalFormPropertyList = formPropertyInitDTO.getApprovalFormPropertyList();
        }
        List<UUID> formOidList = approvalFormPropertyInitDTO.getFormOidList();
        if (CollectionUtils.isEmpty(formOidList) || CollectionUtils.isEmpty(approvalFormPropertyList)) {
            return false;
        }
        approvalFormPropertyService.saveList(approvalFormPropertyList, formOidList);
        return true;
    }

    /**
     * 删除表单配置项
     *
     * @param formOid       表单Oid
     * @param propertyNames 配置名称 ApprovalFormPropertyConstants
     * @return
     */
    @RequestMapping(value = "/custom/forms/property", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteCustomFormProperty(@RequestParam UUID formOid, @RequestParam List<String> propertyNames) {
        return ResponseEntity.ok(approvalFormPropertyService.deleteCustomFormProperty(formOid, propertyNames));
    }



    /**
     * 根据表单编码和当前租户查询表单
     *
     * @param formCode：表单code
     * @return
     */
    @RequestMapping(value = "/custom/forms/by/form/code", method = RequestMethod.GET)
    public ResponseEntity<ApprovalFormDTO> getCustomFormByFormCode(@RequestParam(name = "formCode") String formCode) {
        return ResponseEntity.ok(approvalFormService.getDTOByFormCode(formCode, OrgInformationUtil.getCurrentTenantId()));
    }



    @RequestMapping(value = "/custom/form/company", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormDTO>> getCustomFormByCompanyOidAndFormType(@RequestParam(name = "companyOid") UUID companyOid,
                                                                                      @RequestParam(name = "formType") Integer formType) {
        return ResponseEntity.ok(approvalFormService.listDTOByCompanyOidAndFormType(companyOid, formType));
    }


    /**
     * 给 表单 提供， 根据所给范围查询申请类型
     *
     * @param forOtherRequestDTO
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/custom/forms/getCustomFormByRange", method = RequestMethod.POST)
    public ResponseEntity<Page<ApprovalFormDTO>> getCustomFormByRange(
            @RequestBody @Valid ApprovalFormForOtherRequestDTO forOtherRequestDTO, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page<ApprovalFormDTO> result = approvalFormService.pageByRangeForForm(forOtherRequestDTO, page);
        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * 用户查询可用表单
     *
     * @api {GET} /api/custom/forms/available?formType=101 用户查询可用表单
     * @apiGroup ApprovalForm
     * @apiParam {String} formType 表单类型101:申请表单，102报销单表单，103全部
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "formOid": "659dbb76-4e81-42d4-8b6b-2447f7fe8608",
     * "formName": "订票申请",
     * "iconName": "",
     * "name": "",
     * "formType": 2003
     * },
     * {
     * "formOid": "a1b7add3-d349-4e17-8674-53705a30d595",
     * "formName": "借款申请",
     * "iconName": "loan_application",
     * "name": "loan_application_form",
     * "formType": 2005
     * }
     * ]
     */
    /*@RequestMapping(value = "/custom/forms/my/available", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormSummaryDTO>> getNewAvailableCustomForm(@RequestParam(name = "formType", required = true) Integer formType,
                                                                                @RequestParam(name = "userOid", required = false) UUID UserOid) {
        List<ApprovalFormDTO> customFormDTOs = customFormService.getNewAvailableCustomForm(UserOid, formType);
        return ResponseEntity.ok(customFormDTOs.stream().map(ApprovalFormMapper::convertToSummaryDTO).collect(Collectors.toList()));
    }*/



    /**
     * @api {get} /api/custom/forms/company/all/types 按条件搜索公司下的表单
     * @apiParam {Integer} enabledFlag 0禁用,1启用,2全部
     * @apiParam {Integer[]} formTypes 表单类型id
     * @apiGroup ApprovalForm
     * @apiSuccess {Array} rows 对象数组
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "formOid": "d8203e09-599d-4f85-b42a-bd93027adcd6",
     * "formName": "测试跪求别改表单",
     * "iconName": "",
     * "name": "",
     * "formType": 3001,
     * "asSystem": true,
     * "valid": true,
     * "createdDate": "2017-06-11T16:00:00Z",
     * "lastModifiedDate": "2017-09-06T16:00:00Z",
     * "parentOid": null,
     * "associateExpenseReport": false,
     * "customFormFields": [],
     * "remark": "系统默认日常报销单，不可修改2",
     * "referenceOid": null,
     * "groupOids": [],
     * "visibleExpenseTypeScope": 1002,
     * "expenseTypeOids": [],
     * "expenseReport": null,
     * "formCode": null,
     * "companyOid": null,
     * "travelManageType": null,
     * "controlFields": null,
     * "customFormProperties": null,
     * "customFormPropertyMap": null,
     * "expenseTypeCheckStatus": null,
     * "visibleUserScope": 1001,
     * "submitWorkflow": false
     * }
     * ]
     */
    @RequestMapping(value = "/custom/forms/company/all/types", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalFormSummaryDTO>> listSummaryForms(@RequestParam(value = "enabledFlag", required = false) Integer enabledFlag, @RequestParam(value = "formTypes") List<Integer> formTypes) throws URISyntaxException {
        List<ApprovalFormSummaryDTO> customFormDTOs = approvalFormService.listSummaryForm();
        return ResponseEntity.ok(customFormDTOs);
    }

}
