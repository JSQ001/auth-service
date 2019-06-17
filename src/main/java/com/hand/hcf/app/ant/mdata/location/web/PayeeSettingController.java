package com.hand.hcf.app.ant.mdata.location.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingHeader;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingLine;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingHeaderDTO;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingLineDTO;
import com.hand.hcf.app.ant.mdata.location.service.PayeeSettingHeaderService;
import com.hand.hcf.app.ant.mdata.location.service.PayeeSettingLineService;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;


/**
 * @author zihao.yang
 * @create 2019-6-13 10:13:15
 * @remark
 */
@Api(tags = "")
@RestController
@RequestMapping("/api/location/payee/setting")
public class PayeeSettingController {


    @Autowired
    private PayeeSettingHeaderService payeeSettingHeaderService;
    @Autowired
    private PayeeSettingLineService payeeSettingLineService;



    /**
     * 根据条件分页查询收款方配置头信息
     */
    @RequestMapping(value = "/header/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PayeeSettingHeaderDTO>> queryHeader(@RequestParam(value = "payeeSettingHeaderId", required = false) Long payeeSettingHeaderId,
                                                      @RequestParam(value = "payeeCountryCode", required = false) String payeeCountryCode,
                                                      @RequestParam(value = "payeeCityCode", required = false) String payeeCityCode,
                                                      @RequestParam(value = "payerCountryCode", required = false) String payerCountryCode,
                                                      @RequestParam(value = "payerCityCode", required = false) String payerCityCode,
                                                      Pageable pageable) throws URISyntaxException {
        Page<PayeeSettingHeaderDTO> page = payeeSettingHeaderService.queryHeader(payeeSettingHeaderId,
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/location/payee/setting/header/query");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据头id分页查询收款方配置行信息
     */
    @RequestMapping(value = "/line/query/byHeaderId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PayeeSettingLineDTO>> queryLineByHeaderId(@RequestParam(value = "payeeSettingHeaderId", required = false) Long payeeSettingHeaderId,
                                                                         Pageable pageable) throws URISyntaxException {
        Page<PayeeSettingLineDTO> page = payeeSettingLineService.queryLineByHeaderId(payeeSettingHeaderId, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/location/payee/setting/line/query/byHeaderId");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    /**
     * 更新收款方配置头表信息
     */
    @PostMapping(value = "header/saveOrUpdate")
    public ResponseEntity<PayeeSettingHeader> saveOrUpdateHeader(@ApiParam(value = "收款方配置头") @RequestBody @Valid PayeeSettingHeaderDTO payeeSettingHeaderDTO){
        return ResponseEntity.ok(payeeSettingHeaderService.saveOrUpdateHeader(payeeSettingHeaderDTO));
    }

    /**
     * 根据头表id，更新收款方配置行表信息
     */
    @PostMapping(value = "line/saveOrUpdateLine")
    public ResponseEntity<PayeeSettingLine> saveOrUpdateLine(@ApiParam(value = "收款方配置行") @RequestBody @Valid PayeeSettingLineDTO payeeSettingLineDTO){
        return ResponseEntity.ok(payeeSettingLineService.saveOrUpdateLine(payeeSettingLineDTO));
    }

    /**
     * 删除收款方头表记录
     * @param id
     */
    @DeleteMapping("header/delete/byId/{id}")
    public void deleteHeaderById(@PathVariable Long id){
        payeeSettingHeaderService.deleteHeaderById(id);
    }

    /**
     * 删除收款方行表记录
     * @param id
     */
    @DeleteMapping("line/delete/byId/{id}")
    public void deleteLineById(@PathVariable Long id){
        payeeSettingLineService.deleteLineById(id);
    }

    /**
     * 根据条件查询收款方配置字段
     */
    @RequestMapping(value = "/header/query/forReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PayeeSettingHeaderDTO>> queryHeaderForReport(@RequestParam(value = "payeeCountryCode", required = false) String payeeCountryCode,
                                                                   @RequestParam(value = "payeeCityCode", required = false) String payeeCityCode,
                                                                   @RequestParam(value = "payerCountryCode", required = false) String payerCountryCode,
                                                                   @RequestParam(value = "payerCityCode", required = false) String payerCityCode,
                                                                    Pageable pageable) throws URISyntaxException {
        Page<PayeeSettingHeaderDTO> page = payeeSettingHeaderService.queryHeaderForReport(
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/location/payee/setting/header/query/forReport");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }
}
