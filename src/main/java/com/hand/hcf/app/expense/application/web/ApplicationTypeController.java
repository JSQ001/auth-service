package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignCompany;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeDimension;
import com.hand.hcf.app.expense.application.service.ApplicationTypeService;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;


/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@RestController
@RequestMapping("/api/expense/application/type")
@Api(tags = "申请单类型控制器")
public class ApplicationTypeController {

    @Autowired
    private ApplicationTypeService service;

    @PostMapping
    @ApiOperation(value = "创建一个申请单类型", notes = "创建一个申请单类型 开发:bin.xie")
    public ResponseEntity createApplicationType(@ApiParam(value = "申请单类型") @RequestBody @Validated ApplicationTypeDTO dto){
        return ResponseEntity.ok(service.createApplicationType(dto));
    }


    @GetMapping("/query/{id}")
    @ApiOperation(value = "根据id查询申请单类型（编辑时查询)", notes = "根据id查询申请单类型（编辑时查询) 开发:bin.xie")
    public ResponseEntity getTypeForUpdate(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.getTypeForUpdate(id));
    }


    @PutMapping
    @ApiOperation(value = "更新一个申请单类型", notes = "更新一个申请单类型 开发:bin.xie")
    public ResponseEntity updateApplicationType(@ApiParam(value = "申请单类型") @RequestBody @Validated ApplicationTypeDTO dto){
        return ResponseEntity.ok(service.updateApplicationType(dto));
    }


    @GetMapping("/query")
    @ApiOperation(value = "界面分页可以根据条件查询", notes = "界面分页可以根据条件查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationType>> queryByCondition(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                  @ApiParam(value = "申请单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
                                                                  @ApiParam(value = "申请单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
                                                                  @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                  @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ApplicationType> list = service.queryByCondition(setOfBooksId, typeCode, typeName,enabled, page,false);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/query/enable/dataAuth")
    @ApiOperation(value = "界面分页可以根据条件查询", notes = "界面分页可以根据条件查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationType>> queryByConditionEnableDataAuth(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                  @ApiParam(value = "申请单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
                                                                  @ApiParam(value = "申请单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
                                                                  @ApiParam(value = "是否启用")  @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                                @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ApplicationType> list = service.queryByCondition(setOfBooksId, typeCode, typeName,enabled, page,true);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }



    @PostMapping("/{applicationTypeId}/assign/company")
    @ApiOperation(value = "批量分配公司", notes = "批量分配公司 开发:bin.xie")
    public ResponseEntity<Boolean> assignCompanies(@ApiParam(value = "ids")  @RequestBody List<Long> ids,
                                                   @PathVariable("applicationTypeId") Long applicationTypeId){


        return ResponseEntity.ok(service.assignCompanies(ids, applicationTypeId));

    }


    @PutMapping("/assign/company")
    @ApiOperation(value = "更改已经分配的公司启用状态", notes = "更改已经分配的公司启用状态 开发:bin.xie")
    public ResponseEntity<Boolean> updateCompanyEnabled(@ApiParam(value = "费用申请单类型关联机构") @RequestBody ApplicationTypeAssignCompany company){
        return ResponseEntity.ok(service.updateCompanyEnabled(company));
    }

    @GetMapping("/{applicationTypeId}/company/query")
    @ApiOperation(value = "分页查询已经分配了的公司", notes = "分页查询已经分配了的公司 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationTypeAssignCompany>> queryAssignCompanies(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                                                   @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ApplicationTypeAssignCompany> companies = service.queryAssignCompanies(applicationTypeId, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(companies.getRecords(), httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/{applicationTypeId}/company/query/filter")
    @ApiOperation(value = "分页查询申请单类型尚未分配的公司信息", notes = "分页查询申请单类型尚未分配的公司信息 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getCompanyByConditionFilter(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                      @ApiParam(value = "公司编码") @RequestParam(required = false) String companyCode,
                                                      @ApiParam(value = "公司名称") @RequestParam(required = false) String companyName,
                                                      @ApiParam(value = "公司编码从") @RequestParam(required = false) String companyCodeFrom,
                                                      @ApiParam(value = "公司编码到") @RequestParam(required = false) String companyCodeTo,
                                                      @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = service.getCompanyByConditionFilter(applicationTypeId, companyCode,
                companyName, companyCodeFrom, companyCodeTo, page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/expense/application/type/"+ applicationTypeId + "/company/query/filter");

        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    @PostMapping("/{applicationTypeId}/assign/dimension")
    @ApiOperation(value = "分页查询申请单类型尚未分配的公司信息", notes = "分页查询申请单类型尚未分配的公司信息 开发:bin.xie")
    public ResponseEntity assignDimensions(@ApiParam(value = "申请单类型分配维度表domain") @RequestBody List<ApplicationTypeDimension> dimensions,
                                           @PathVariable("applicationTypeId") Long applicationTypeId){

        return ResponseEntity.ok(service.assignDimensions(applicationTypeId, dimensions));
    }


    @GetMapping("/{applicationTypeId}/dimension/query")
    @ApiOperation(value = "分页查询申请单类型尚未分配的公司信息", notes = "分页查询申请单类型尚未分配的公司信息 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationTypeDimension>> queryDimension(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                                         @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ApplicationTypeDimension> result = service.queryDimension(applicationTypeId,  page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }


    @DeleteMapping("/dimension/{id}")
    @ApiOperation(value = "根据id删除申请单类型分配了的维度信息", notes = "根据id删除申请单类型分配了的维度信息 开发:bin.xie")
    public ResponseEntity deleteDimension(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.deleteDimension(id));
    }


    @GetMapping("/query/all")
    @ApiOperation(value = "查询账套下的所有的申请单类型（前端查询条件下拉框)", notes = "查询账套下的所有的申请单类型（前端查询条件下拉框) 开发:bin.xie")
    public ResponseEntity<List<ApplicationType>> queryAllType(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                              @ApiParam(value = "是否启动") @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(service.queryAllType(setOfBooksId,enabled));
    }


    @GetMapping("/query/created")
    @ApiOperation(value = "查询账套下的所有的申请单类型（前端查询条件下拉框)", notes = "查询账套下的所有的申请单类型（前端查询条件下拉框) 开发:bin.xie")
    public ResponseEntity<List<ApplicationType>> queryCreatedType(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                  @ApiParam(value = "是否启动") @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(service.queryCreatedType(setOfBooksId,enabled));
    }


    @ApiOperation(value = "获取当前人分配的申请单类型",
            notes = "获取当前人分配的申请单类型 开发：谢宾")
    @GetMapping("/query/condition/user")
    public ResponseEntity<List<ApplicationType>> queryByUserAndAuth(
            @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag){

        return ResponseEntity.ok(service.queryByUserAndAuth(authFlag));
    }


    @GetMapping("/query/header/{id}")
    @ApiOperation(value = "申请单头创建时，根据类型ID查询分配的维度，以便动态生成表单", notes = "申请单头创建时，根据类型ID查询分配的维度，以便动态生成表单 开发:bin.xie")
    public ResponseEntity<ApplicationTypeDimensionDTO> queryByHeaderCreated(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.queryTypeAndDimensionById(id, true));
    }


    @GetMapping("/query/expense/type")
    @ApiOperation(value = "创建单据行时，查询该类型分配的申请类型详细信息", notes = "创建单据行时，查询该类型分配的申请类型详细信息 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseType(@ApiParam(value = "申请类型ID") @RequestParam("applicationTypeId") Long applicationTypeId,
                                                                    @ApiParam(value = "员工ID") @RequestParam("employeeId") Long employeeId,
                                                                    @ApiParam(value = "公司ID") @RequestParam("companyId") Long companyId,
                                                                    @ApiParam(value = "部门ID") @RequestParam("departmentId") Long departmentId,
                                                                    @ApiParam(value = "类型ID") @RequestParam(value = "typeCategoryId",required = false) Long typeCategoryId,
                                                                    @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                                                    @ApiIgnore Pageable pageable){
        Page<ExpenseTypeWebDTO> page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> result = service.queryExpenseTypeByApplicationTypeId(applicationTypeId,
                typeCategoryId, name, companyId, employeeId, departmentId, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }



    @ApiOperation(value = "【申请单类型】根据账套查询费用类型", notes = "【申请单类型】根据账套查询费用类型 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query/expense/type/by/setOfBooksId")
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseTypeBySetOfBooksId(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                    @ApiParam(value = "类型标志") @RequestParam(value = "typeFlag",defaultValue = "0")Integer typeFlag,
                                                                    @ApiParam(value = "类型ID") @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                                    @ApiParam(value = "名称") @RequestParam(value = "name",required = false) String name,
                                                                                  @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> result = service.queryExpenseTypeBySetOfBooksId(setOfBooksId,null,typeCategoryId,name,typeFlag,page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/expense/application/type/query/expense/type");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }


    @GetMapping("/queryDimensionByTypeIdAndCompanyId")
    @ApiOperation(value = "【申请单类型】根据申请单类型id和公司id查询公司下已分配的启用维度", notes = "【申请单类型】根据申请单类型id和公司id查询公司下已分配的启用维度 开发:bin.xie")
    public List<ApplicationTypeDimension> queryDimensionByTypeIdAndCompanyId(@ApiParam(value = "申请类型ID") @RequestParam("applicationTypeId") Long applicationTypeId,
                                                                             @ApiParam(value = "公司ID") @RequestParam("companyId") Long companyId) {
        List<ApplicationTypeDimension> result = service.queryDimensionByTypeIdAndCompanyId(applicationTypeId, companyId);

        return result;
    }


    @GetMapping("/{applicationTypeId}/dimensions/query/filter")
    @ApiOperation(value = "【申请单类型】未分配维度查询", notes = "【申请单类型】未分配维度查询 开发:bin.xie")
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("applicationTypeId") Long applicationTypeId,
                                                            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @ApiParam(value = "维度编码") @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                            @ApiParam(value = "维度名称") @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                            @ApiParam(value = "是否启动") @RequestParam(value = "enabled",required = false) Boolean enabled
                                                            ) throws URISyntaxException {
        List<DimensionCO> result = service.listDimensionByConditionFilter(applicationTypeId,setOfBooksId,dimensionCode,dimensionName,enabled);
        return result;
    }


    @GetMapping("/users")
    @ApiOperation(value = "【申请单类型】根据单据id查询有该单据权限的用户", notes = "【申请单类型】根据单据id查询有该单据权限的用户 开发:bin.xie")
    public ResponseEntity listUsersByApplicationType(@ApiParam(value = "申请类型ID") @RequestParam(value = "applicationTypeId") Long applicationTypeId,
                                                     @ApiParam(value = "用户编码") @RequestParam(value = "userCode", required = false) String userCode,
                                                     @ApiParam(value = "用户名称") @RequestParam(value = "userName", required = false) String userName,
                                                     @ApiParam(value = "页数") @RequestParam(defaultValue = "0") int page,
                                                     @ApiParam(value = "每页多少条") @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = service.listUsersByApplicationType(applicationTypeId, userCode, userName, queryPage);
        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * 根据所选范围查询账套下符合条件的费用申请单类型
     * @param applicationTypeForOtherCO
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/query/by/cond")
    @ApiOperation(value = "根据所选范围查询账套下符合条件的费用申请单类型", notes = "根据所选范围查询账套下符合条件的费用申请单类型 开发:bin.xie")

    public ResponseEntity<Page<ApplicationTypeCO>> queryApplicationTypeByCond(@RequestBody ApplicationTypeForOtherCO applicationTypeForOtherCO,
                                                       @ApiParam(value = "页数") @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                       @ApiParam(value = "每页多少条")@RequestParam(value = "size", required = false,defaultValue = "10") int size) throws URISyntaxException {
        Page pageInfo = PageUtil.getPage(page,size);
        Page<ApplicationTypeCO> list = service.queryApplicationTypeByCond(applicationTypeForOtherCO,pageInfo);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(pageInfo);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }
}
