package com.hand.hcf.app.mdata.supplier.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.VendorBankAccountCO;
import com.hand.hcf.app.mdata.supplier.service.VendorBankAccountService;
import com.hand.hcf.app.mdata.supplier.web.dto.VendorAccountDTO;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.PaginationUtil;
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
}
