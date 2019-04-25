package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import com.hand.hcf.app.payment.service.CompanyBankAuthService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.web.dto.CompanyBankAuthDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/28.
 */
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
    @RequestMapping(value = "/insertOrUpdate",method= RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBankAuth> insertOrUpdate(@RequestBody @Valid CompanyBankAuth companyBankAuth){
        return ResponseEntity.ok(companyBankAuthService.insertOrUpdateCompanyBankAuth(companyBankAuth));
    }


    /**
     * 根据id逻辑删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById",method= RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@RequestParam Long id){
        return ResponseEntity.ok(companyBankAuthService.deleteById(id));
    }


    /**
     * 根据银行账户id查看当前银行账户下的授权列表
     * @param id
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/selectCompanyBankId",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankAuthDTO>> selectByInput(@RequestParam Long id, Pageable pageable){
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
    @RequestMapping(value = "/selectAuthBank",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBank>> selectByEmpAuth(@RequestParam UUID empId){
        return ResponseEntity.ok(companyBankAuthService.selectByEmpAuth(empId));
    }

    @RequestMapping(value = "/get/own/info/{userOID}",method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBankAuthDTO>> getOwnInfo(
        @PathVariable("userOID") String empOid,
        @RequestParam(value = "companyCode",required = false) String companyCode,
        @RequestParam(value = "companyName",required = false) String companyName,
        Pageable pageable){
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankAuthDTO> result = companyBankAuthService.getCompanyBankAuthDTOByEmpOid(empOid,companyCode,companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyBankAuth/get/own/info");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/own/info/lov/{userOID}",method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBankAuthDTO>> getOwnInfoByLov(
        @PathVariable("userOID") String empOid,
        @RequestParam(value = "companyCode",required = false) String companyCode,
        @RequestParam(value = "companyName",required = false) String companyName,
        Pageable pageable){
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankAuthDTO> result = companyBankAuthService.getCompanyInfoByEmpOid(empOid,companyCode,companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyBankAuth/get/own/info/lov");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


}
