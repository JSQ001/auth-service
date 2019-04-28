package com.hand.hcf.app.mdata.supplier.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.supplier.service.VendorBankAccountService;
import com.hand.hcf.app.mdata.supplier.web.dto.VendorAccountDTO;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 17:19
 */
@Api(tags = "供应商银行账户")
@RestController
@RequestMapping("/api")
public class VendorBankAccountController {

    private VendorBankAccountService vendorBankAccountService;

    public VendorBankAccountController(VendorBankAccountService vendorBankAccountService) {
        this.vendorBankAccountService = vendorBankAccountService;
    }

    @PostMapping("/ven/bank/insert")
    public ResponseEntity<VendorBankAccountCO> createVendorBankAccount(@RequestBody VendorBankAccountCO vendorBankAccountCO,
                                                        @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(vendorBankAccountService.createOrUpdateVendorBankAccount(vendorBankAccountCO, roleType));
    }

    @PutMapping("/ven/bank/update")
    public ResponseEntity<VendorBankAccountCO> updateVendorBankAccount(@RequestBody VendorBankAccountCO vendorBankAccountCO,
                                                        @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(vendorBankAccountService.createOrUpdateVendorBankAccount(vendorBankAccountCO, roleType));
    }

    @GetMapping("/ven/bank")
    public ResponseEntity<List<VendorBankAccountCO>> searchVendorBankAccounts(@RequestParam("vendorInfoId") String vendorInfoId,
                                                         @RequestParam(value = "status",required = false) Integer status,
                                                         Pageable pageable)
            throws URISyntaxException {
        Page<VendorBankAccountCO> page = vendorBankAccountService.searchVendorBankAccounts(vendorInfoId,status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ven/bank");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 获取指定供应商下的银行信息
     *
     * @param vendorInfoId
     * @return
     */
    @GetMapping("/ven/artemis/{vendorInfoId}")
    public ResponseEntity<List<VendorBankAccountCO>> searchVendorBankAccounts(@PathVariable("vendorInfoId") String vendorInfoId){
        return ResponseEntity.ok(vendorBankAccountService.searchVendorBankAccounts(vendorInfoId));
    }

    /**
     * 获取批量供应商下的所有银行信息，artemis调用
     *
     * @param ids
     * @return
     */
    @GetMapping("/ven/artemis/bank")
    public ResponseEntity<List<VendorBankAccountCO>> searchVendorBankAccounts(@RequestParam("ids") List<String> ids){
        return ResponseEntity.ok(vendorBankAccountService.listVendorBankAccounts(ids));
    }

    /**
     * 根据银行账号获取 银行信息
     *
     * @param bankAccount
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/bank/get/by/bank/account")
    public ResponseEntity<List<VendorBankAccountCO>> searchVendorBankAccountsByBankAccount(@RequestParam("bankAccount") String bankAccount) throws URISyntaxException {
        return ResponseEntity.ok(vendorBankAccountService.listVendorBankAccountsByBankAccount(bankAccount));
    }
    /**
     * 根据租户id和供应商名称,代码【模糊】分页查询 获取供应商银行信息
     * 获取供应商及银行信息，（预付款，合同）
     * @param name
     * @param code
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/vendor/account/by/name/code", method = RequestMethod.GET)
    public ResponseEntity<List<VendorAccountDTO>> getReceivablesByNameAndCode(@RequestParam(value = "name",required = false) String name,
                                                                              @RequestParam(value = "code",required = false) String code,
                                                                              @RequestParam(value = "page",defaultValue = "0") int page,
                                                                              @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<VendorAccountDTO> result = vendorBankAccountService.getReceivablesByNameAndCode(name, code, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据公司id和供应商名称,代码【模糊】分页查询 获取供应商银行信息
     * 获取供应商及银行信息（审核状态为审核通过的租户下的供应商）
     * @param companyId
     * @param name
     * @param code
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "分页查询供应商银行信息", notes = "分页查询获取供应商银行信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", dataType = "String"),
            @ApiImplicitParam(name = "code", value = "代码", dataType = "String"),
            @ApiImplicitParam(name = "vendorStatus", value = "状态(审批通过、拒绝、编辑中，如为空则默认查询审批通过的)", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/vendor/account/by/companyId/name/code", method = RequestMethod.GET)
    public ResponseEntity<List<VendorAccountDTO>> getVendorByCompanyIdAndNameAndCode(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "code",required = false) String code,
            @RequestParam(value = "vendorStatus",required = false) String vendorStatus,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<VendorAccountDTO> result = vendorBankAccountService.getVendorByCompanyIdAndNameAndCode(
                name, code, vendorStatus ,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }



    /**
     * 对供应商下银行状态进行操作
     *提交，审批和拒绝
     * @param VendorBankAccountCO
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/ven/bank/info/operation")
    public ResponseEntity<VendorBankAccountCO> operationBankVendorInfo(@RequestBody VendorBankAccountCO vendorBankAccountCO,
                                                            @RequestParam(value = "roleType", required = false) String roleType,
                                                            @RequestParam(value = "action",required = true) String action) throws URISyntaxException {
       return ResponseEntity.ok(vendorBankAccountService.operationVendor(vendorBankAccountCO,roleType,action));

    }
}
