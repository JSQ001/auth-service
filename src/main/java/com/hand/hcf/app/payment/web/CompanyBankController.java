package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.service.CompanyBankService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.web.dto.CompanyBankDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/8.
 */
@RestController
@RequestMapping("/api/CompanyBank")
public class CompanyBankController {
    @Autowired
    private CompanyBankService companyBankService;

    /**
     * 新增或修改公司银行账户
     *
     * @param companyBank
     * @return
     */

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBank> insertOrUpdateCompanyBank(@RequestBody @Valid CompanyBank companyBank) {
        return ResponseEntity.ok(companyBankService.insertOrUpdateCompanyBank(companyBank, OrgInformationUtil.getCurrentUserOid()));
    }

    /**
     * 根据id逻辑删除公司银行账户
     *
     * @param id
     * @return
     */

    @RequestMapping(value = "/deleteById/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(companyBankService.deleteCompanyBankById(id));
    }

    /**
     * 根据公司id查询公司下的公司银行列表
     *
     * @param companyId
     * @param pageable
     * @return
     */

    @RequestMapping(value = "/selectByCompanyId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankDTO>> selectByCompanyId(
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "companyCode", required = false) String companyCode,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "companyBankName", required = false) String companyBankName,
            @RequestParam(value = "companyBankCode", required = false) String companyBankCode,
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "currency", required = false) String currency,
            Pageable pageable) {
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankDTO> result = companyBankService.selectCompanyBankByCompanyId(companyId, companyCode, companyName, companyBankCode, companyBankName, setOfBooksId, currency, page, false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/CompanyBank/selectByCompanyId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据公司id查询公司下的公司银行列表
     *
     * @param companyId
     * @param pageable
     * @return
     */

    @RequestMapping(value = "/selectByCompanyId/enable/dataAuth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankDTO>> selectByCompanyIdEnableDataAuth(
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "companyCode", required = false) String companyCode,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "companyBankName", required = false) String companyBankName,
            @RequestParam(value = "companyBankCode", required = false) String companyBankCode,
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "currency", required = false) String currency,
            Pageable pageable) {
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankDTO> result = companyBankService.selectCompanyBankByCompanyId(companyId, companyCode, companyName, companyBankCode, companyBankName, setOfBooksId, currency, page, true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/CompanyBank/selectByCompanyId/enable/dataAuth");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /*根据公司id和付款方式大类code,币种查询
     * */

    @RequestMapping(value = "/getCompanyBank/by/companyId/and/paymentMethodCode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBank>> getByCompanyAndCode(
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "paymentMethodCode", required = false) String paymentMethodCode,
            @RequestParam(value = "currency", required = false) String currency
    ) {
        List<CompanyBank> companyBanks = companyBankService.getByCompanyIdAndPaymentMethodCode(companyId, paymentMethodCode, currency);
        return ResponseEntity.ok(companyBanks);
    }


    @RequestMapping(value = "/selectById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBank> selectById(@RequestParam Long companyBankId) {
        return ResponseEntity.ok(companyBankService.selectById(companyBankId));
    }


    @RequestMapping(value = "/get/by/setOfBooksId", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBank>> getBySetOfBooksId(@RequestParam Long setOfBooksId,
                                                               Pageable pageable
    ) {
        Page page = MyBatisPageUtil.getPage(pageable);
        Page result = companyBankService.getBySetOfBooksId(setOfBooksId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/CompanyBank/get/by/setOfBooksId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


}
