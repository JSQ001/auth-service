package com.hand.hcf.app.ant.mdata.location.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeHeaderDTO;
import com.hand.hcf.app.ant.mdata.location.service.PayeeService;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;


/**
 * @author zihao.yang
 * @create 2019-6-13 10:13:15
 * @remark
 */
@Api(tags = "")
@RestController
@RequestMapping("/api/location/payee")
public class PayeeController {


    @Autowired
    private PayeeService payeeService;



    /**
     * 根据条件分页查询收款方头信息
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PayeeHeaderDTO>> query(@RequestParam(value = "payeeHeaderId") Long payeeHeaderId,
                                                      @RequestParam(value = "payeeType", required = false) String payeeType,
                                                      @RequestParam(value = "payeeCountryCode", required = false) String payeeCountryCode,
                                                      @RequestParam(value = "payeeCityCode", required = false) String payeeCityCode,
                                                      @RequestParam(value = "payerCountryCode", required = false) String payerCountryCode,
                                                      @RequestParam(value = "payerCityCode", required = false) String payerCityCode,
                                                      @RequestParam(value = "payeeCode", required = false) String payeeCode,
                                                      @RequestParam(value = "payeeName", required = false) String payeeName,
                                                      Pageable pageable) throws URISyntaxException {
        Page<PayeeHeaderDTO> page = payeeService.queryForHeader(payeeHeaderId, payeeType,
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                payeeCode, payeeName,
                pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/location/payee/query");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }
}
