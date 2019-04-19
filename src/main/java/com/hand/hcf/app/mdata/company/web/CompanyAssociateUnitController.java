package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.company.domain.CompanyAssociateUnit;
import com.hand.hcf.app.mdata.company.dto.CompanyAssociateUnitDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovQueryParams;
import com.hand.hcf.app.mdata.company.service.CompanyAssociateUnitService;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovQueryParams;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLovDTO;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 *  公司部门关联关系控制器
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/15
 */
@RestController
@RequestMapping("/api/company/associate/department")
@Api(tags = "公司部门关联关系Controller")
public class CompanyAssociateUnitController {
    @Autowired
    private CompanyAssociateUnitService service;

    @GetMapping("/query")
    @ApiOperation(value = "查询公司关联的部门", notes = "查询公司关联的部门 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<CompanyAssociateUnitDTO>> queryByCompanyId(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam Long companyId){
        Page page = PageUtil.getPage(pageable);
        List<CompanyAssociateUnitDTO> result = service.queryByCompanyId(page, companyId);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "公司关联部门", notes = "公司关联部门 开发：谢宾")
    @PostMapping
    public ResponseEntity<Boolean> associate(@ApiParam(value = "公司id") @RequestParam Long companyId,
                                             @ApiParam(value = "部门id集合") @RequestBody List<Long> departmentIds){
        return ResponseEntity.ok(service.associate(companyId, departmentIds));
    }

    @ApiOperation(value = "更改公司部门关联关系状态", notes = "更改公司部门关联关系状态 开发：谢宾")
    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody CompanyAssociateUnit domain){
        return ResponseEntity.ok(service.updateAssociate(domain));
    }

    @GetMapping("/query/canAssociate")
    @ApiOperation(value = "查询公司可关联的部门", notes = "查询公司可关联的部门 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<CompanyAssociateUnitDTO>> queryCanAssociate(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam Long companyId,
            @ApiParam("部门代码/名称") @RequestParam(required = false) String codeName,
            @ApiParam("部门代码从") @RequestParam(required = false) String codeFrom,
            @ApiParam("部门代码至") @RequestParam(required = false) String codeTo){
        Page page = PageUtil.getPage(pageable);
        List<CompanyAssociateUnitDTO> result = service.queryCanAssociate(page, companyId, codeName, codeFrom, codeTo);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/query/contact")
    @ApiOperation(value = "查询公司和部门下的员工", notes = "查询公司和部门下的员工 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<ContactDTO>> queryContact(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam Long companyId,
            @ApiParam(value = "部门id") @RequestParam Long departmentId,
            @ApiParam("员工姓名/工号") @RequestParam(required = false) String codeName,
            @ApiParam("职务") @RequestParam(required = false) String dutyCode,
            @ApiParam("状态") @RequestParam(required = false) Integer status){
        Page page = PageUtil.getPage(pageable);
        List<ContactDTO> result = service.queryContact(page, companyId, departmentId, codeName, dutyCode, status);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/query/responsibility")
    @ApiOperation(value = "查询公司和部门下的责任中心", notes = "查询公司和部门下的责任中心 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<ResponsibilityLovDTO>> queryResponsibility(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam Long companyId,
            @ApiParam(value = "部门id") @RequestParam Long departmentId,
            @ApiParam("责任中心代码/名称") @RequestParam(required = false) String codeName){
        Page page = PageUtil.getPage(pageable);
        List<ResponsibilityLovDTO> result = service.queryResponsibility(page, companyId, departmentId, codeName);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/query/department/lov")
    @ApiOperation(value = "查询部门lov", notes = "查询部门lov 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<DepartmentLovDTO>> queryDepartmentLov(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam(required = false) Long companyId,
            @ApiParam(value = "部门代码") @RequestParam(required = false) String departmentCode,
            @ApiParam(value = "部门代码从") @RequestParam(required = false) String departmentCodeFrom,
            @ApiParam(value = "部门代码至") @RequestParam(required = false) String departmentCodeTo,
            @ApiParam(value = "部门名称") @RequestParam(required = false) String departmentName,
            @ApiParam(value = "部门id") @RequestParam(required = false) Long id,
            @ApiParam(value = "账套id") @RequestParam(required = false) Long setOfBooksId,
            @ApiParam(value = "部门状态") @RequestParam(required = false) Integer status,
            @ApiParam("部门代码/名称") @RequestParam(required = false) String codeName){
        Page page = PageUtil.getPage(pageable);
        Long currentTenantId = LoginInformationUtil.getCurrentTenantId();
        DepartmentLovQueryParams queryParams = DepartmentLovQueryParams.builder()
                .companyId(companyId)
                .codeName(codeName)
                .departmentCode(departmentCode)
                .departmentCodeFrom(departmentCodeFrom)
                .departmentCodeTo(departmentCodeTo)
                .id(id)
                .tenantId(currentTenantId)
                .status(status)
                .setOfBooksId(setOfBooksId)
                .departmentName(departmentName).build();
        List<DepartmentLovDTO> result = service.queryDepartmentLov(page, queryParams);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/query/company/lov")
    @ApiOperation(value = "查询公司lov", notes = "查询公司lov 开发：谢宾")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<CompanyLovDTO>> queryCompanyLov(
            @ApiIgnore Pageable pageable,
            @ApiParam(value = "公司id") @RequestParam(required = false) Long id,
            @ApiParam(value = "公司代码") @RequestParam(required = false) String companyCode,
            @ApiParam(value = "公司代码从") @RequestParam(required = false) String companyCodeFrom,
            @ApiParam(value = "公司代码至") @RequestParam(required = false) String companyCodeTo,
            @ApiParam(value = "公司名称") @RequestParam(required = false) String companyName,
            @ApiParam(value = "部门id") @RequestParam(required = false) Long departmentId,
            @ApiParam(value = "公司状态") @RequestParam(required = false) Boolean enabled,
            @ApiParam(value = "账套Id") @RequestParam(required = false) Long setOfBooksId,
            @ApiParam("公司代码/名称") @RequestParam(required = false) String codeName){
        Page page = PageUtil.getPage(pageable);
        Long currentTenantId = LoginInformationUtil.getCurrentTenantId();
        CompanyLovQueryParams queryParams = CompanyLovQueryParams.builder()
                .id(id)
                .codeName(codeName)
                .companyCode(companyCode)
                .companyCodeFrom(companyCodeFrom)
                .companyCodeTo(companyCodeTo)
                .departmentId(departmentId)
                .tenantId(currentTenantId)
                .enabled(enabled)
                .setOfBooksId(setOfBooksId)
                .companyName(companyName).build();
        List<CompanyLovDTO> result = service.queryCompanyLov(page, queryParams);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }
}
