package com.hand.hcf.app.mdata.init;

import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.mdata.supplier.service.dto.VendorInfoforStatusDTO;
import com.hand.hcf.app.mdata.supplier.service.VendorBankAccountService;
import com.hand.hcf.app.mdata.supplier.service.VendorInfoService;

import com.hand.hcf.app.mdata.department.dto.DepartmentPositionImportDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionUserBeginningImportDTO;
import com.hand.hcf.app.mdata.department.service.DepartmentPositionService;
import com.hand.hcf.app.mdata.department.service.DepartmentPositionUserService;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityImportDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.service.DepartmentSobResponsibilityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private VendorInfoService vendorInfoService;
    @Autowired
    private VendorBankAccountService vendorBankAccountService;

    /**
     * 部门角色导入
     * @param departmentPositionImportDTOList
     * @return
     */
    @PostMapping("/departmentposition")
    public ResponseEntity initDepartmentPosition(
            @RequestBody List<DepartmentPositionImportDTO> departmentPositionImportDTOList) {
        return ResponseEntity.ok(departmentPositionService.importDepartmentPosition(departmentPositionImportDTOList));
    }

    /**
     * 部门角色对应人员导入
     * @param departmentPositionUserBeginningImportDTOS
     * @return
     */
    @PostMapping("/departmentpositionuser")
    public ResponseEntity initDepartmentPositionUser(
            @RequestBody List<DepartmentPositionUserBeginningImportDTO> departmentPositionUserBeginningImportDTOS) {
        return ResponseEntity.ok(departmentPositionUserService.importDepartmentPositionUser(
                departmentPositionUserBeginningImportDTOS));
    }

    /**
     * 部门下责任中心数据导入
     * @param departmentSobResponsibilityImportDTOS
     * @return
     */
    @PostMapping("/departmentsobresponsibility")
    public ResponseEntity initDepartmentSobResponsibility(@RequestBody List<DepartmentSobResponsibilityImportDTO> departmentSobResponsibilityImportDTOS) {
        return ResponseEntity.ok(departmentSobResponsibilityService.importDepartmentPositionUser(departmentSobResponsibilityImportDTOS));
    }

    /**
     * 通过模板批量导入供应商
     *
     * @param vendorInfoCOList 供应商信息list
     * @return 校验出错结果
     * @throws URISyntaxException
     */
    @PostMapping(value = "/ven/info/create/batch", produces = "application/json")
    @ApiOperation(value = "通过模板批量导入供应商", notes = "通过模板批量导入供应商 开发：王帅")
    public ResponseEntity createVendorInfoBatch(@ApiParam(value = "供应商Json数据") @RequestBody List<VendorInfoforStatusDTO> vendorInfoCOList) throws URISyntaxException {
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
    @ApiOperation(value = "通过模板批量导入银行帐号", notes = "通过模板批量导入银行帐号 开发：王帅")
    public ResponseEntity createVendorBankAccountBatch(@ApiParam(value = "银行Json数据") @RequestBody List<VendorBankAccountCO> vendorBankAccountCOList) throws URISyntaxException {
        return ResponseEntity.ok(vendorBankAccountService.createOrUpdateVendorBankAccountBatch(vendorBankAccountCOList));
    }
}
