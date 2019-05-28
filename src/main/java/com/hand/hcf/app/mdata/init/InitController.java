package com.hand.hcf.app.mdata.init;

import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.mdata.accounts.dto.AccountsDTO;
import com.hand.hcf.app.mdata.accounts.dto.AccountsHierarchyDTO;
import com.hand.hcf.app.mdata.accounts.service.AccountsHierarchyService;
import com.hand.hcf.app.mdata.accounts.service.AccountsService;
import com.hand.hcf.app.mdata.company.domain.CompanyImportDTO;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.UserGroupDTO;
import com.hand.hcf.app.mdata.contact.service.UserGroupService;
import com.hand.hcf.app.mdata.department.domain.DepartmentImportDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionImportDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionUserBeginningImportDTO;
import com.hand.hcf.app.mdata.department.service.DepartmentPositionService;
import com.hand.hcf.app.mdata.department.service.DepartmentPositionUserService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityImportDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.service.DepartmentSobResponsibilityService;
import com.hand.hcf.app.mdata.supplier.service.VendorBankAccountService;
import com.hand.hcf.app.mdata.supplier.service.VendorInfoService;
import com.hand.hcf.app.mdata.supplier.service.dto.VendorInfoforStatusDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;


/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/17
 */
@RestController
@RequestMapping("/api/init")
@Api(tags = "初始化数据控制器")
public class InitController {

    @Autowired
    private DepartmentPositionService departmentPositionService;
    @Autowired
    private DepartmentPositionUserService departmentPositionUserService;
    @Autowired
    private DepartmentSobResponsibilityService departmentSobResponsibilityService;
    @Autowired
    private LegalEntityService legalEntityService;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private AccountsHierarchyService accountsHierarchyService;
    @Autowired
    private UserGroupService userGroupService;

    @PostMapping("/company")
    public ResponseEntity initCompany() {
        return ResponseEntity.ok().build();
    }

    @Autowired
    private VendorInfoService vendorInfoService;
    @Autowired
    private VendorBankAccountService vendorBankAccountService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 部门角色导入
     * @param departmentPositionImportDTOList
     * @return
     */
    @PostMapping(value = "/departmentposition", produces = "application/json")
    @ApiOperation(value = "部门角色期初导入", notes = "通过模板批量导入部门角色")
    public ResponseEntity initDepartmentPosition(@ApiParam(value = "部门角色json数据")
            @RequestBody List<DepartmentPositionImportDTO> departmentPositionImportDTOList) {
        return ResponseEntity.ok(departmentPositionService.importDepartmentPosition(departmentPositionImportDTOList));
    }

    /**
     * 部门角色对应人员导入
     * @param departmentPositionUserBeginningImportDTOS
     * @return
     */
    @PostMapping(value = "/departmentpositionuser", produces = "application/json")
    @ApiOperation(value = "部门角色对应人员数据期初导入", notes = "通过模板批量导入部门角色对应人员数据")
    public ResponseEntity initDepartmentPositionUser(@ApiParam(value = "部门角色对应人员json数据")
            @RequestBody List<DepartmentPositionUserBeginningImportDTO> departmentPositionUserBeginningImportDTOS) {
        return ResponseEntity.ok(departmentPositionUserService.importDepartmentPositionUser(
                departmentPositionUserBeginningImportDTOS));
    }

    /**
     * 部门下责任中心数据导入
     * @param departmentSobResponsibilityImportDTOS
     * @return
     */
    @PostMapping(value = "/departmentsobresponsibility", produces = "application/json")
    @ApiOperation(value = "部门责任中心关联关系期初导入", notes = "通过模板批量导入部门责任中心关联关系")
    public ResponseEntity initDepartmentSobResponsibility(@ApiParam(value = "部门责任中心关联关系json数据")
            @RequestBody List<DepartmentSobResponsibilityImportDTO> departmentSobResponsibilityImportDTOS) {
        return ResponseEntity.ok(departmentSobResponsibilityService.importDepartmentSobResponsibility(
                departmentSobResponsibilityImportDTOS));
    }

    /**
     * 法人实体批量导入
     *
     * @param list
     * @return
     */
    @PostMapping(value = "/legalentity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过模板批量导入法人实体", notes = "通过模板批量导入法人实体 开发：刘飞")
    public ResponseEntity<String> importLegalEntityBatch(@ApiParam("法人实体json数据") @RequestBody List<LegalEntityDTO> list) {
        return ResponseEntity.ok(legalEntityService.importLegalEntityBatch(list));
    }


    /**
     * 科目明细批量导入
     *
     * @param list
     * @return
     */
    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过模板批量导入科目明细", notes = "通过模板批量导入科目明细 开发：刘飞")
    public ResponseEntity<String> importAccountsBatch(@ApiParam("科目明细json数据") @RequestBody List<AccountsDTO> list) {
        return ResponseEntity.ok(accountsService.importAccountBatch(list));
    }

    /**
     * 科目层次批量导入
     *
     * @param list
     * @return校验结果
     */
    @PostMapping(value = "/accountshierarchy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过模板批量导入科目层次", notes = "通过模板批量导入科目层次 开发：刘飞")
    public ResponseEntity<String> importAccountsHierarchy(@ApiParam("科目层次json数据") @RequestBody List<AccountsHierarchyDTO> list) {
        return ResponseEntity.ok(accountsHierarchyService.importAccountsHierarchyBatch(list));
    }

    /**
     * 人员组批量导入
     *
     * @param userGroupDTOS
     * @return
     */
    @PostMapping(value = "/usergroup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过模板批量导入人员组", notes = "通过模板批量导入人员组 开发：刘飞")
    public ResponseEntity importUserGroupBatch(@ApiParam("人员组json数据") @RequestBody List<UserGroupDTO> userGroupDTOS) {
        return ResponseEntity.ok(userGroupService.importUserGroup(userGroupDTOS));
    }

    /**
     * 人员组下员工批量导入
     *
     * @param userGroupDTOS
     * @return
     */
    @PostMapping(value = "/usergroup/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过模板批量导入人员组下员工", notes = "通过模板批量导入人员组下员工 开发：刘飞")
    public ResponseEntity importUserGroupUsersBatch(@ApiParam("员工组下员工Json数据") @RequestBody List<UserGroupDTO> userGroupDTOS) {
        return ResponseEntity.ok(userGroupService.importUserGroupUsersBatch(userGroupDTOS));
    }

    /**
     * 通过模板批量导入供应商
     *
     * @param vendorInfoCOList 供应商信息list
     * @return 校验出错结果
     * @throws URISyntaxException
     */
    @PostMapping(value = "/ven/info/create/batch", produces = "application/json")
    @ApiOperation(value = "通过模板批量导入供应商", notes = "通过模板批量导入供应商")
    public ResponseEntity createVendorInfoBatch(@ApiParam(value = "供应商Json数据") @RequestBody List<VendorInfoforStatusDTO> vendorInfoCOList) {
        return  ResponseEntity.ok(vendorInfoService.createVendorInfoBatch(vendorInfoCOList));
    }

    /**
     * 通过模板批量导入银行帐号
     *
     * @param vendorBankAccountCOList 银行信息list
     * @return  校验出错结果
     * @throws URISyntaxException
     */
    @PostMapping(value = "/ven/bank/insert/batch" ,produces = "application/json")
    @ApiOperation(value = "通过模板批量导入银行帐号", notes = "通过模板批量导入银行帐号")
    public ResponseEntity createVendorBankAccountBatch(@ApiParam(value = "银行Json数据") @RequestBody List<VendorBankAccountCO> vendorBankAccountCOList) {
        return ResponseEntity.ok(vendorBankAccountService.createOrUpdateVendorBankAccountBatch(vendorBankAccountCOList));
    }

    /**
     * 公司导入
     * @param CompanyImportDTOS
     * @return
     */
    @PostMapping(value = "/company/import", produces = "application/json")
    @ApiOperation(value = "通过模板批量导入公司信息", notes = "通过模板批量导入公司信息 开发：赵忠岩")
    public ResponseEntity importCompany(@ApiParam(value = "公司Json数据") @RequestBody List<CompanyImportDTO> CompanyImportDTOS) {
        return ResponseEntity.ok(companyService.importCompany(CompanyImportDTOS));
    }

    /**
     * 部门导入
     * @param departmentImportDTOS
     * @return
     */
    @PostMapping(value = "/department/import", produces = "application/json")
    @ApiOperation(value = "通过模板批量导入部门信息", notes = "通过模板批量导入部门信息 开发：赵忠岩")
    public ResponseEntity importDepartment(@ApiParam(value = "部门Json数据") @RequestBody List<DepartmentImportDTO> departmentImportDTOS){
        return ResponseEntity.ok(departmentService.importDepartment(departmentImportDTOS));
    }
}
