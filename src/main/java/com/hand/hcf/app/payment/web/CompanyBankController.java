package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.service.CompanyBankService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.web.dto.CompanyBankDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/8.
 */

@Api(tags = "公司银行账户API")
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

    @ApiOperation(value = "新增或修改公司银行账户", notes = "新增或修改公司银行账户 开发: ")
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBank> insertOrUpdateCompanyBank(@ApiParam(value = "公司银行账户") @RequestBody @Valid CompanyBank companyBank) {
        return ResponseEntity.ok(companyBankService.insertOrUpdateCompanyBank(companyBank, OrgInformationUtil.getCurrentUserOid()));
    }

    /**
     * 根据id逻辑删除公司银行账户
     *
     * @param id
     * @return
     */

    @ApiOperation(value = "根据id逻辑删除公司银行账户", notes = "根据id逻辑删除公司银行账户 开发: ")
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

    @ApiOperation(value = "根据公司id查询公司下的公司银行列表", notes = "根据公司id查询公司下的公司银行列表 开发: ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/selectByCompanyId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankDTO>> selectByCompanyId(
            @ApiParam(value = "公司id") @RequestParam(value = "companyId", required = false) Long companyId,
            @ApiParam(value = "公司code") @RequestParam(value = "companyCode", required = false) String companyCode,
            @ApiParam(value = "公司名称") @RequestParam(value = "companyName", required = false) String companyName,
            @ApiParam(value = "公司银行账户名称") @RequestParam(value = "companyBankName", required = false) String companyBankName,
            @ApiParam(value = "公司银行账户code") @RequestParam(value = "companyBankCode", required = false) String companyBankCode,
            @ApiParam(value = "账套id") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "币种") @RequestParam(value = "currency", required = false) String currency,
            @ApiIgnore Pageable pageable) {
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

    @ApiOperation(value = "根据公司id是否授权查询公司下的公司银行列表", notes = "根据公司id是否授权查询公司下的公司银行列表 开发: ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/selectByCompanyId/enable/dataAuth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankDTO>> selectByCompanyIdEnableDataAuth(
            @ApiParam(value = "公司id") @RequestParam(value = "companyId", required = false) Long companyId,
            @ApiParam(value = "公司code") @RequestParam(value = "companyCode", required = false) String companyCode,
            @ApiParam(value = "公司名称") @RequestParam(value = "companyName", required = false) String companyName,
            @ApiParam(value = "公司银行账户名称") @RequestParam(value = "companyBankName", required = false) String companyBankName,
            @ApiParam(value = "公司银行账户code") @RequestParam(value = "companyBankCode", required = false) String companyBankCode,
            @ApiParam(value = "账套id") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "币种") @RequestParam(value = "currency", required = false) String currency,
            @ApiIgnore Pageable pageable) {
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankDTO> result = companyBankService.selectCompanyBankByCompanyId(companyId, companyCode, companyName, companyBankCode, companyBankName, setOfBooksId, currency, page, true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/CompanyBank/selectByCompanyId/enable/dataAuth");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /*根据公司id和付款方式大类code,币种查询
     * */
    @ApiOperation(value = "根据公司id和付款方式大类code,币种查询", notes = "根据公司id和付款方式大类code,币种查询 开发: ")
    @RequestMapping(value = "/getCompanyBank/by/companyId/and/paymentMethodCode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBank>> getByCompanyAndCode(
            @ApiParam(value = "公司Id") @RequestParam(value = "companyId", required = false) Long companyId,
            @ApiParam(value = "付款方式code") @RequestParam(value = "paymentMethodCode", required = false) String paymentMethodCode,
            @ApiParam(value = "币种") @RequestParam(value = "currency", required = false) String currency
    ) {
        List<CompanyBank> companyBanks = companyBankService.getByCompanyIdAndPaymentMethodCode(companyId, paymentMethodCode, currency);
        return ResponseEntity.ok(companyBanks);
    }

    @ApiOperation(value = "根据公司id查询", notes = "根据公司id查询 开发: ")
    @RequestMapping(value = "/selectById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyBank> selectById(@ApiParam(value = "公司银行账户id") @RequestParam Long companyBankId) {
        return ResponseEntity.ok(companyBankService.selectById(companyBankId));
    }

    @ApiOperation(value = "查询公司账套id", notes = "查询公司账套id 开发: ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/get/by/setOfBooksId", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBank>> getBySetOfBooksId(@ApiParam(value = "账套id") @RequestParam Long setOfBooksId,
                                                               @ApiIgnore Pageable pageable
    ) {
        Page page = MyBatisPageUtil.getPage(pageable);
        Page result = companyBankService.getBySetOfBooksId(setOfBooksId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/CompanyBank/get/by/setOfBooksId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


}
