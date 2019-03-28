package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.RelationVendorCompanyCO;
import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.supplier.constants.Constants;
import com.hand.hcf.app.mdata.supplier.service.RelationVendorCompanyService;
import com.hand.hcf.app.mdata.supplier.service.VendorBankAccountService;
import com.hand.hcf.app.mdata.supplier.service.VendorInfoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/12 16:04
 */
@RestController
//@RequestMapping("/api/implement")
public class SupplierImplementControllerImpl {

    private VendorInfoService vendorInfoService;

    private RelationVendorCompanyService relationVendorCompanyService;
    private VendorBankAccountService vendorBankAccountService;

    public SupplierImplementControllerImpl(VendorInfoService vendorInfoService,
                                           RelationVendorCompanyService relationVendorCompanyService,
                                           VendorBankAccountService vendorBankAccountService) {
        this.vendorInfoService = vendorInfoService;
        this.relationVendorCompanyService = relationVendorCompanyService;
        this.vendorBankAccountService = vendorBankAccountService;
    }

    /**
     * 中间件--artemis调用，分页获取供应商信息
     *
     * @param companyOID
     * @param companyId
     * @param startDate
     * @param endDate
     * @param page       在artemis那端已经转成mybatis-plus page，默认page=1,size=10
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/search/msg", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<VendorInfoCO> pageVendorInfos(@RequestParam(name = "companyOID", required = false) String companyOID,
                                              @RequestParam(name = "companyId", required = false) Long companyId,
                                              @RequestParam(name = "startDate") Long startDate,
                                              @RequestParam(name = "endDate") Long endDate,
                                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return vendorInfoService.pageVendorInfos(companyOID, companyId, OrgInformationUtil.getCurrentTenantId(), startDate, endDate, page, size);
    }

    /**
     * artemis调用，新增供应商信息
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    //@PostMapping(value = "/infobank/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO insertVendorInfoByArtemis(@RequestBody VendorInfoCO vendorInfoCO) {
        return vendorInfoService.insertOrUpdateVendorInfoByArtemis(vendorInfoCO, Constants.OPERATE_INSERT);
    }

    /**
     * artemis调用，修改供应商信息
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    //@PostMapping(value = "/infobank/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO updateVendorInfoByArtemis(@RequestBody VendorInfoCO vendorInfoCO) {
        return vendorInfoService.insertOrUpdateVendorInfoByArtemis(vendorInfoCO, Constants.OPERATE_UPDATE);
    }

    /**
     * artemis调用，根据供应商编码获取指定租户下的供应商信息
     *
     * @param vendorCode
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/infobank/search/one", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO getVendorInfoByVendorCode(@RequestParam("vendorCode") String vendorCode) {
        return vendorInfoService.getVendorInfoByVendorCode(vendorCode,OrgInformationUtil.getCurrentTenantId());
    }

    /**
     * artemis调用，批量获取供应商信息
     *
     * @param ids
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/ven/info/by/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VendorInfoCO> listVendorInfosByIds(@RequestParam(name = "ids") List<String> ids) {
        return vendorInfoService.listVendorInfosByIds(ids);
    }

    /**
     * 根据公司oid和供应商名称【模糊】查询供应商信息，包含银行账号信息
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    //@PostMapping(value = "/ven/artemis/search/nonsort", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<VendorInfoCO> pageVendorInfosByConditions(@RequestBody VendorInfoCO vendorInfoCO) {
        return vendorInfoService.pageVendorInfosByConditions(vendorInfoCO);
    }

    /**
     * artemis调用，获取指定供应商信息
     *
     * @param id
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/ven/info/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO getVendorInfoByArtemis(@PathVariable("id") String id) {
        return vendorInfoService.getVendorInfoByArtemis(id);
    }

    /**
     * artemis调用，获取指定供应商信息
     *
     * @param id
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/query/ven/info/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO getOneVendorInfoByArtemis(@PathVariable("id") String id) {
        return vendorInfoService.getOneVendorInfoByArtemis(id);
    }


    /**
     * 根据公司id和供应商名称[模糊]分页查询供应商，包含银行账号信息
     *
     * @param companyId
     * @param venNickname
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/vendorInfo/bankAccount/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<VendorInfoCO> pageVendorInfosByConditions(@RequestParam("companyId") Long companyId,
                                                             @RequestParam(value = "venNickname", required = false) String venNickname,
                                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<VendorInfoCO> pageo = vendorInfoService.pageVendorInfosByConditions(companyId, venNickname, page, size);

        return pageo;
    }
    /**
     * 根据公司id和供应商名称，代码[模糊]分页查询供应商，包含银行账号信息
     * 按代码升序
     * @param companyId
     * @param venNickname
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    public Page<VendorInfoCO> pageVendorInfosByConditions(@RequestParam("companyId") Long companyId,
                                                             @RequestParam(value = "venNickname", required = false) String venNickname,
                                                             @RequestParam(value = "venCode", required = false) String venCode,
                                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<VendorInfoCO> pageo = vendorInfoService.pageVendorInfosByConditions(companyId, venNickname,venCode, page, size);

        return pageo;
    }
    /**
     * artemis调用，指定供应商分配给指定公司
     *
     * @param vendorInfoCO
     * @return
     * @throws URISyntaxException
     */
    //@PostMapping(value = "/connect/vendor/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public VendorInfoCO saveConnectVendorAndCompany(@RequestBody VendorInfoCO vendorInfoCO) {
        return relationVendorCompanyService.saveConnectVendorAndCompany(vendorInfoCO);
    }


    /**
     * 根据银行账号获取 银行信息
     *
     * @param bankAccount
     * @return
     */
    //@GetMapping(value = "/ven/bank/get/by/bank/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VendorBankAccountCO> listVendorBankAccountsByBankAccount(@RequestParam("bankAccount") String bankAccount) {
        return vendorBankAccountService.listVendorBankAccountsByBankAccount(bankAccount);
    }

    /**
     * artemis调用，根据供应商获取对应的分配公司
     *
     * @param infoId
     * @return
     * @throws URISyntaxException
     */
    //@GetMapping(value = "/query/companies/by/InfoId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RelationVendorCompanyCO> listRelationVendorCompanysByVendorInfoId(@RequestParam("infoId") Long infoId) throws URISyntaxException {
        return relationVendorCompanyService.selectRelationVendorCompanysByVendorInfoId(infoId);
    }
    /**
     * 获取指定供应商下的银行信息
     *
     * @param vendorInfoId
     * @return
     */
    //@GetMapping(value = "/ven/artemis/{vendorInfoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VendorBankAccountCO> listVendorBankAccounts(@PathVariable("vendorInfoId") String vendorInfoId){
        return vendorBankAccountService.searchVendorBankAccounts(vendorInfoId);
    }

    /**
     * 获取批量供应商下的所有银行信息，artemis调用
     *
     * @param ids
     * @return
     */
    //@GetMapping(value = "/ven/artemis/bank")
    public List<VendorBankAccountCO> listVendorBankAccounts(@RequestParam("ids") List<String> ids){
        return vendorBankAccountService.listVendorBankAccounts(ids);
    }
}
