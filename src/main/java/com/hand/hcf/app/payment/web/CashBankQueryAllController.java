package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.payment.service.CashBankQueryAllService;
import com.hand.hcf.app.payment.web.dto.BankQueryAllDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by liudong on 2017/12/22.
 */

@Api(tags = "业务规则")
@RestController
@RequestMapping("/api/cash/bank/query/all")
public class CashBankQueryAllController {

    private final CashBankQueryAllService cashBankQueryAllService;

    public CashBankQueryAllController(CashBankQueryAllService cashBankQueryAllService) {
        this.cashBankQueryAllService = cashBankQueryAllService;
    }

    /**
     * @api {GET} /api/cash/bank/query/all 【通用银行】分页查询返回DTO类
     * @apiDescription 根据条件分页查询银行，返回自定义DTO类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} [bankCode] 银行代码
     * @apiParam (请求参数) {String} [bankName] 银行名称
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} list 返回泛型为BankQueryAllDTO的List集合
     * @apiSuccess (BankQueryAllDTO的属性) {String} bankCode 银行代码
     * @apiSuccess (BankQueryAllDTO的属性) {String} swiftCode 用于跨国业务，外币业务
     * @apiSuccess (BankQueryAllDTO的属性) {String} bankName 银行名称
     * @apiSuccess (BankQueryAllDTO的属性) {String} countryCode 所在国家代码
     * @apiSuccess (BankQueryAllDTO的属性) {String} countryName 所在国家名称
     * @apiSuccess (BankQueryAllDTO的属性) {String} provinceCode 所在省份代码
     * @apiSuccess (BankQueryAllDTO的属性) {String} provinceName 所在省份名称
     * @apiSuccess (BankQueryAllDTO的属性) {String} cityCode 所在城市代码
     * @apiSuccess (BankQueryAllDTO的属性) {String} cityName 所在城市名称
     * @apiSuccess (BankQueryAllDTO的属性) {String} districtCode 区/县代码
     * @apiSuccess (BankQueryAllDTO的属性) {String} districtName 区/县名称
     * @apiSuccess (BankQueryAllDTO的属性) {String} address 详细地址
     * @apiParamExample {json} 请求参数
     * /api/cash/bank/query/all?page=1&size=1
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "bankCode": "4",
     *   "swiftCode":"2",
     *   "countryCode":"23",
     *   "countryName":"true",
     *   "provinceCode":2,
     *   "provinceName":"asd",
     *   "cityCode":"23",
     *   "cityName":"@4",
     *   "districtCode":"putuo",
     *   "districtName":"普陀",
     *   "address":"金沙江路1915弄",
     *   "bankName":"建某银行"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件分页查询银行返回自定义DTO类", notes = "根据条件分页查询银行返回自定义DTO类 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping
    public ResponseEntity<List<BankQueryAllDTO>> queryAllBankDTO(
            @ApiParam(value = "银行代码") @RequestParam(required = false) String bankCode,
            @ApiParam(value = "银行名称") @RequestParam(required = false) String bankName,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<BankQueryAllDTO> pages =  cashBankQueryAllService.queryAllBankDTO(bankCode,bankName,page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(pages, "api/cash/bank/query/all");
        return new ResponseEntity(pages.getRecords(), headers, HttpStatus.OK);
    }
}
