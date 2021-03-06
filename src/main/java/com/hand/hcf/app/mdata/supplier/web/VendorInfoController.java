package com.hand.hcf.app.mdata.supplier.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.supplier.service.VendorInfoService;
import com.hand.hcf.app.mdata.supplier.service.dto.VendorInfoforStatusDTO;
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
 * @Date: 2018/4/11 17:55
 */
@RestController
@RequestMapping("/api")
public class VendorInfoController {

    private VendorInfoService vendorInfoService;

    public VendorInfoController(VendorInfoService vendorInfoService) {
        this.vendorInfoService = vendorInfoService;
    }

    /**
     * 新增供应商
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/ven/info/insert")
    public ResponseEntity<VendorInfoforStatusDTO> createVendorInfo(@RequestBody VendorInfoforStatusDTO vendorInfoCO,
                                                                   @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {

        return ResponseEntity.ok(vendorInfoService.insertOrUpdateVendorInfo(vendorInfoCO, roleType));
    }

    /**
     * 修改供应商
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/ven/info/update")
    public ResponseEntity<VendorInfoCO> updateVendorInfo(@RequestBody VendorInfoforStatusDTO vendorInfoCO,
                                                 @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(vendorInfoService.insertOrUpdateVendorInfo(vendorInfoCO, roleType));
    }
    /**
     * 对供应商状态进行操作
     *提交，审批和拒绝
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/ven/info/operation")
    public ResponseEntity<VendorInfoCO> operationVendorInfo(@RequestBody VendorInfoforStatusDTO vendorInfoCO,
                                                         @RequestParam(value = "roleType", required = false) String roleType,
                                                            @RequestParam(value = "action",required = true) String action) throws URISyntaxException {
        return ResponseEntity.ok(vendorInfoService.operationVendor(vendorInfoCO, roleType, action));
    }
    /**
     * 分页查询供应商信息，包含供应商下的银行账号信息
     *
     * @param venderTypeId
     * @param venderCode
     * @param venNickname
     * @param bankAccount
     * @param venType 状态 1001 启用  1002 禁用
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/info")
    public ResponseEntity<List<VendorInfoforStatusDTO>> searchVendorInfos(@RequestParam(value = "venderTypeId", required = false) Long venderTypeId,
                                                                          @RequestParam(value = "venderCode", required = false) String venderCode,
                                                                          @RequestParam(value = "venNickname", required = false) String venNickname,
                                                                          @RequestParam(value = "bankAccount", required = false) String bankAccount,
                                                                          @RequestParam(value = "venType", required = false) Integer venType,
                                                                          @RequestParam(value = "roleType", required = false) String roleType,
                                                                          @RequestParam(value = "vendorStatus", required = false) String vendorStatus,
                                                                          Pageable pageable) throws URISyntaxException {
        Page<VendorInfoforStatusDTO> page = vendorInfoService.searchVendorInfos(venderTypeId, venderCode, venNickname, bankAccount, venType, roleType,vendorStatus, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ven/info");
        return new ResponseEntity<>(page.getRecords(),headers,HttpStatus.OK);
    }

    /**
     * 获取指定的供应商信息，不包含供应商银行账号信息
     *
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/info/{id}")
    public ResponseEntity<VendorInfoforStatusDTO> searchVendorInfoByOne(@PathVariable("id") Long id) throws URISyntaxException {
        return ResponseEntity.ok(vendorInfoService.searchVendorInfoByOne(id));
    }

    /**
     * 获取指定供应商信息，包含供应商银行账号信息
     *
     * @param vendorInfoId
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/infoBank")
    public ResponseEntity<VendorInfoCO> searchVendorInfoAndBank(@RequestParam("vendorInfoId") String vendorInfoId,
                                                        @RequestParam(value = "bankId", required = false) String bankId) throws URISyntaxException {
        return ResponseEntity.ok(vendorInfoService.searchVendorInfoAndBank(vendorInfoId, bankId));
    }

    /**
     * 通过表单 获取供应商列表[支持供应商名称模糊匹配]
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/info/search")
    public ResponseEntity<List<VendorInfoCO>> searchVendorInfosByPage(@RequestParam(value = "venNickname", required = false) String venNickname,
                                                        Pageable pageable) throws URISyntaxException {
        Page<VendorInfoCO> page = vendorInfoService.searchVendorInfosByPage(venNickname, OrgInformationUtil.getCurrentUserOid().toString(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ven/info/search");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 通过表单 新增供应商[包含银行信息]
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/ven/infobank/insert")
    public ResponseEntity<VendorInfoCO> createVendorInfoByBill(@RequestBody VendorInfoCO vendorInfoCO) throws URISyntaxException {
        return ResponseEntity.ok(vendorInfoService.insertVendorInfoByBill(vendorInfoCO, OrgInformationUtil.getCurrentUserOid().toString()));
    }

    /**
     * 分页查询供应商信息，包含供应商下的银行账号信息
     *
     * @param venderTypeId
     * @param venderCode
     * @param venNickname
     * @param bankAccount
     * @param venType 状态 1001 启用  1002 禁用
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/ven/info/approval")
    public ResponseEntity<List<VendorInfoforStatusDTO>> searchVendorInfosForApproval(@RequestParam(value = "venderTypeId", required = false) Long venderTypeId,
                                                                                     @RequestParam(value = "venderCode", required = false) String venderCode,
                                                                                     @RequestParam(value = "venNickname", required = false) String venNickname,
                                                                                     @RequestParam(value = "bankAccount", required = false) String bankAccount,
                                                                                     @RequestParam(value = "venType", required = false) Integer venType,
                                                                                     @RequestParam(value = "roleType", required = false) String roleType,
                                                                                     @RequestParam(value = "vendorStatus", required = false) String vendorStatus,
                                                                                     Pageable pageable) throws URISyntaxException {
        Page<VendorInfoforStatusDTO> page = vendorInfoService.searchVendorInfosforApproval(venderTypeId, venderCode, venNickname, bankAccount, venType, roleType,vendorStatus, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/ven/info/approval");
        return new ResponseEntity<>(page.getRecords(),headers,HttpStatus.OK);
    }
}
