package com.hand.hcf.app.mdata.supplier.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.RelationVendorCompanyCO;
import com.hand.hcf.app.mdata.supplier.service.RelationVendorCompanyService;
import com.hand.hcf.app.mdata.supplier.web.dto.CompanyDTO;
import com.hand.hcf.app.core.util.PageUtil;
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
 * @Date: 2018/4/14 16:53
 */
@RestController
@RequestMapping("/api/ven/info/assign/company")
public class RelationVendorCompanyController {

    private RelationVendorCompanyService relationVendorCompanyService;

    public RelationVendorCompanyController(RelationVendorCompanyService relationVendorCompanyService) {
        this.relationVendorCompanyService = relationVendorCompanyService;
    }

    /**
     * 批量新增操作-供应商关联公司，old url:/api/ven/info/deploy/company/CO
     *
     * @param relationVendorCompanyCO
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/batch")
    public ResponseEntity<RelationVendorCompanyCO> batchCreateRelationVendorCompanys(@RequestBody RelationVendorCompanyCO relationVendorCompanyCO,
                                                                                     @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(relationVendorCompanyService.batchCreateRelationVendorCompanys(relationVendorCompanyCO, roleType));
    }

    /**
     * 修改操作--供应商关联公司
     *
     * @param relationVendorCompanyCO
     * @return
     * @throws URISyntaxException
     */
    @PutMapping
    public ResponseEntity<RelationVendorCompanyCO> updateRelationVendorCompany(@RequestBody RelationVendorCompanyCO relationVendorCompanyCO,
                                                                               @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(relationVendorCompanyService.updateRelationVendorCompany(relationVendorCompanyCO, roleType));
    }

    /**
     * 分页获取供应商下 分配的公司
     *
     * @param infoId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/company/dto")
    public ResponseEntity<List<CompanyDTO>> selectRelationVendorCompanys(@RequestParam("infoId") Long infoId,
                                                                         Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyDTO> result = relationVendorCompanyService.selectRelationVendorCompanys(infoId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/ven/info/assign/company/query/company/CO");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * artemis调用，根据供应商获取对应的分配公司
     *
     * @param infoId
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/companies/by/InfoId")
    public ResponseEntity<List<RelationVendorCompanyCO>> selectRelationVendorCompanysByVendorInfoId(@RequestParam("infoId") Long infoId) throws URISyntaxException {
        return ResponseEntity.ok(relationVendorCompanyService.selectRelationVendorCompanysByVendorInfoId(infoId));
    }

    /**
     * 查询某租户下（某供应商）尚未分配的公司
     * @param tenantId
     * @param vendorInfoId
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/unassigned/company/by/cond")
    public ResponseEntity<List<CompanyCO>> selectVendorUnassignedCompany(@RequestParam(value = "tenantId") Long tenantId,
                                                                         @RequestParam(value = "vendorInfoId",required = false) Long vendorInfoId,
                                                                         @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                         @RequestParam(value = "companyCode",required = false) String companyCode,
                                                                         @RequestParam(value = "companyName",required = false) String companyName,
                                                                         Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = relationVendorCompanyService.selectVendorUnassignedCompany(tenantId, vendorInfoId, setOfBooksId, companyCode, companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/ven/info/assign/company/query/unassigned/company/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


}
