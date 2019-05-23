package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.payment.domain.PaymentCompanyConfig;
import com.hand.hcf.app.payment.service.PaymentCompanyConfigService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.web.dto.PaymentCompanyConfigDTO;
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
 * Created by 刘亮 on 2017/9/28.
 */

@Api(tags="支付公司配置API")
@RestController
@RequestMapping("/api/paymentCompanyConfig")
public class PaymentCompanyConfigController {

    @Autowired
    private PaymentCompanyConfigService paymentCompanyConfigService;


    /**
     * 新增或修改支付公司配置
     *
     * @param paymentCompanyConfig
     * @return
     */
    @ApiOperation(value = "新增或修改支付公司配置", notes = "新增或修改支付公司配置  开发:刘亮")
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCompanyConfigDTO> insertOrUpdate(@ApiParam(value = "支付公司配置") @RequestBody @Valid PaymentCompanyConfig paymentCompanyConfig) {
        return ResponseEntity.ok(paymentCompanyConfigService.insertOrUpdatePaymentCompanyConfig(paymentCompanyConfig));
    }
    /*

     */
/**
 * 逻辑删除支付公司配置
 * @param id
 * @return
 *//*

    @Timed
    @RequestMapping(value = "/deleteById",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@RequestParam Long id){
        return ResponseEntity.ok(paymentCompanyConfigService.deleteById(id));
    }
*/


    /**
     * 根据前台输入查询
     *
     * @param companyCode
     * @param companyName
     * @param ducumentCategory
     * @return
     */
    @ApiOperation(value = "根据前台输入查询", notes = "根据前台输入查询  开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/selectByInput", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCompanyConfigDTO>> selectByInput(
            @ApiParam(value = "单据公司代码") @RequestParam(value = "companyCode") String companyCode,//单据公司代码
            @ApiParam(value = "公司名称") @RequestParam(value = "companyName") String companyName,
            @ApiParam(value = "单据类型") @RequestParam(value = "ducumentCategory") String ducumentCategory,
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiIgnore Pageable pageable
    ) {
        if (setOfBooksId == null) {
            return null;
        }
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<PaymentCompanyConfigDTO> result = paymentCompanyConfigService.selectByInput(companyCode, companyName, ducumentCategory, setOfBooksId, page, false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/paymentCompanyConfig/selectByInput");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据前台输入查询
     *
     * @param companyCode
     * @param companyName
     * @param ducumentCategory
     * @return
     */

    @ApiOperation(value = "根据前台输入是否授权查询", notes = "根据前台输入是否授权查询  开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/selectByInput/enable/dataAuth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCompanyConfigDTO>> selectByInputEnableDataAuth(
            @ApiParam(value = "单据公司代码") @RequestParam(value = "companyCode") String companyCode,//单据公司代码
            @ApiParam(value = "公司名称") @RequestParam(value = "companyName") String companyName,
            @ApiParam(value = "单据类型") @RequestParam(value = "ducumentCategory") String ducumentCategory,
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiIgnore Pageable pageable
    ) {
        if (setOfBooksId == null) {
            return null;
        }
        Page page = MyBatisPageUtil.getPage(pageable);
        Page<PaymentCompanyConfigDTO> result = paymentCompanyConfigService.selectByInput(companyCode, companyName, ducumentCategory, setOfBooksId, page, true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/paymentCompanyConfig/selectByInput/enable/dataAuth");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


/*
    @Timed
    @RequestMapping(value = "/selectByPaymentInput",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<CompanyDTO> selectByPaymentInput(@RequestParam Long companyId,
                                                               @RequestParam String ducumentCategory,
                                                               @RequestParam Long ducumentTypeId){
            return ResponseEntity.ok(paymentCompanyConfigService.selectByPayMentInput(companyId, ducumentCategory, ducumentTypeId));
    }
*/

    @ApiOperation(value = "根据ID删除", notes = "根据ID删除  开发:刘亮")
    @DeleteMapping("/deleteByIds")
    public ResponseEntity<Boolean> deleteByIds(@ApiParam(value = "id") @RequestBody List<Long> ids) {
        return ResponseEntity.ok(paymentCompanyConfigService.deleteBatchIds(ids));
    }


}
