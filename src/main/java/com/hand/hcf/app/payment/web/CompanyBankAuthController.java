package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import com.hand.hcf.app.payment.service.CompanyBankAuthService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.web.dto.CompanyBankAuthDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@Api(tags = "银行授权API")
@RestController
@RequestMapping("/api/companyBankAuth")
public class CompanyBankAuthController {
    private final CompanyBankAuthService companyBankAuthService;

    public CompanyBankAuthController(CompanyBankAuthService companyBankAuthService) {
        this.companyBankAuthService = companyBankAuthService;
    }

    /**
     * 新增或修改
     * @param companyBankAuth
     * @return
     */

    @ApiOperation(value = "新增或修改银行授权", notes = "新增或修改银行授权信息 开发:")
    @RequestMapping(value = "/insertOrUpdate",method= RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBankAuth> insertOrUpdate(@ApiParam(value = "银行授权") @RequestBody @Valid CompanyBankAuth companyBankAuth){
        return ResponseEntity.ok(companyBankAuthService.insertOrUpdateCompanyBankAuth(companyBankAuth));
    }


    /**
     * 根据id逻辑删除
     * @param id
     * @return
     */

    @ApiOperation(value = "根据id逻辑删除", notes = "根据id逻辑删除 开发:")
    @RequestMapping(value = "/deleteById",method= RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@ApiParam(value = "id") @RequestParam Long id){
        return ResponseEntity.ok(companyBankAuthService.deleteById(id));
    }


    /**
     * 根据银行账户id查看当前银行账户下的授权列表
     * @param id
     * @param pageable
     * @return
     */

    @ApiOperation(value = "根据银行账户id查看当前银行账户下的授权列表", notes = "根据银行账户id查看当前银行账户下的授权列表 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/selectCompanyBankId",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankAuthDTO>> selectByInput(@ApiParam(value = "id") @RequestParam Long id,@ApiIgnore Pageable pageable){
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankAuthDTO> result = companyBankAuthService.selectConpanyBankAuths(id,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyBankAuth/selectByInput");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    /***
     * 此方法用于通用支付接口，点支付时，付款账户的下拉列表
     * @param empId
     * @return
     */

    @ApiOperation(value = "查询通用支付接口下拉列表", notes = "查询通用支付接口下拉列表 开发:")
    @RequestMapping(value = "/selectAuthBank",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBank>> selectByEmpAuth(@ApiParam(value = "用户id") @RequestParam UUID empId){
        return ResponseEntity.ok(companyBankAuthService.selectByEmpAuth(empId));
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/get/own/info/{userOID}",method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBankAuthDTO>> getOwnInfo(
        @PathVariable("userOID") String empOid,
        @ApiParam(value = "公司code") @RequestParam(value = "companyCode",required = false) String companyCode,
        @ApiParam(value = "公司名称") @RequestParam(value = "companyName",required = false) String companyName,
        @ApiIgnore Pageable pageable){
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankAuthDTO> result = companyBankAuthService.getCompanyBankAuthDTOByEmpOid(empOid,companyCode,companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyBankAuth/get/own/info");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @ApiOperation(value = "通过Lov获取用户信息 ", notes = "通过Lov获取用户信息 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/get/own/info/lov/{userOID}",method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBankAuthDTO>> getOwnInfoByLov(
        @PathVariable("userOID") String empOid,
        @ApiParam(value = "公司code") @RequestParam(value = "companyCode",required = false) String companyCode,
        @ApiParam(value = "公司名称") @RequestParam(value = "companyName",required = false) String companyName,
        @ApiIgnore Pageable pageable){
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankAuthDTO> result = companyBankAuthService.getCompanyInfoByEmpOid(empOid,companyCode,companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyBankAuth/get/own/info/lov");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
