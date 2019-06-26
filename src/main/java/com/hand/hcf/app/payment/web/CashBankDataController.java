package com.hand.hcf.app.payment.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashBankData;
import com.hand.hcf.app.payment.service.CashBankDataService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @author dong.liu on 2017-11-07
 */
@Api(tags = "银行数据API")
@RestController
@RequestMapping("/api/cash/bank/datas")
public class CashBankDataController {

    private final CashBankDataService cashBankDataService;

    CashBankDataController(CashBankDataService cashBankDataService) {
        this.cashBankDataService = cashBankDataService;
    }

    /**
     * 新增一个银行
     *
     * @param cashBankData
     * @return ResponseEntity<CashBankData>
     */
    /**
     * @api {POST} /api/cash/bank/datas 【通用银行】单个新增
     * @apiDescription 新增单个通用银行
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} bankCode 银行代码
     * @apiParam (请求参数) {String} [bankCodeEn] 银行英文代码
     * @apiParam (请求参数) {String} [swiftCode] 用于跨国业务，外币业务
     * @apiParam (请求参数) {String} bankName 银行名称
     * @apiParam (请求参数) {String} [countryCode] 所在国家代码
     * @apiParam (请求参数) {String} [countryName] 所在国家名称
     * @apiParam (请求参数) {String} provinceCode 所在省份代码
     * @apiParam (请求参数) {String} [provinceName] 所在省份名称
     * @apiParam (请求参数) {String} [cityCode] 所在城市代码
     * @apiParam (请求参数) {String} [cityName] 所在城市名称
     * @apiParam (请求参数) {String} [districtCode] 区/县代码
     * @apiParam (请求参数) {String} [districtName] 区/县名称
     * @apiParam (请求参数) {String} [address] 详细地址
     * @apiParam (请求参数) {Object} [i18n] 通用银行名称国际化
     * @apiParam (i18n的属性) {String} [language] 语言 英文(en_us),中文(zh_cn)
     * @apiParam (i18n的属性) {String} [value] 中/英
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {String} bankCode 银行代码
     * @apiSuccess (返回参数) {String} bankCodeEn 银行英文代码
     * @apiSuccess (返回参数) {String} swiftCode 用于跨国业务，外币业务
     * @apiSuccess (返回参数) {String} bankName 银行名称
     * @apiSuccess (返回参数) {String} countryCode 所在国家代码
     * @apiSuccess (返回参数) {String} countryName 所在国家名称
     * @apiSuccess (返回参数) {String} provinceCode 所在省份代码
     * @apiSuccess (返回参数) {String} provinceName 所在省份名称
     * @apiSuccess (返回参数) {String} cityCode 所在城市代码
     * @apiSuccess (返回参数) {String} cityName 所在城市名称
     * @apiSuccess (返回参数) {String} districtCode 区/县代码
     * @apiSuccess (返回参数) {String} districtName 区/县名称
     * @apiSuccess (返回参数) {String} address 详细地址
     * @apiSuccess (返回参数) {Object} i18n 通用银行名称国际化
     * @apiParamExample {json} 请求参数
     * {
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *   ]
     *  }
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "904992174175711233",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     * }
     * }
     */

    @ApiOperation(value = "新增单个通用银行", notes = "新增单个通用银行 开发:")
    @PostMapping
    public ResponseEntity<CashBankData> createCshBank(@ApiParam(value = "付款单头信息") @RequestBody CashBankData cashBankData) {
        return ResponseEntity.ok(cashBankDataService.createCshBank(cashBankData));
    }

    /**
     * 批量新增银行
     *
     * @param cashBankDatas
     * @return ResponseEntity<List<CashBankData>>
     */
    /**
     * @api {POST} /api/cash/bank/datas/batch 【通用银行】批量新增
     * @apiDescription 批量新增通用银行
     * @apiGroup PaymentService
     * @apiParamExample {josn} 请求参数
     * [
     * * {
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *   ]
     *  }
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "904992174175711233",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     * }
     * }
     * ]
     */

    @ApiOperation(value = "批量新增通用银行", notes = "批量新增通用银行 开发:")
    @PostMapping("/batch")
    public ResponseEntity<List<CashBankData>> createCshBankBatch(@ApiParam(value = "付款单头信息") @RequestBody List<CashBankData> cashBankDatas) {
        return ResponseEntity.ok(cashBankDataService.createCshBankBatch(cashBankDatas));
    }

    /**
     * 更新一个银行
     *
     * @param cashBankData
     * @return ResponseEntity<CashBankData>
     */
    /**
     * @api {PUT} /api/cash/bank/datas 【通用银行】单个修改
     * @apiDescription 修改单个通用银行
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {String} bankCode 银行代码
     * @apiParam (请求参数) {String} [bankCodeEn] 银行英文代码
     * @apiParam (请求参数) {String} [swiftCode] 用于跨国业务，外币业务
     * @apiParam (请求参数) {String} bankName 银行名称
     * @apiParam (请求参数) {String} countryCode 所在国家代码
     * @apiParam (请求参数) {String} countryName 所在国家名称
     * @apiParam (请求参数) {String} provinceCode 所在省份代码
     * @apiParam (请求参数) {String} provinceName 所在省份名称
     * @apiParam (请求参数) {String} cityCode 所在城市代码
     * @apiParam (请求参数) {String} cityName 所在城市名称
     * @apiParam (请求参数) {String} [districtCode] 区/县代码
     * @apiParam (请求参数) {String} [districtName] 区/县名称
     * @apiParam (请求参数) {String} address 详细地址
     * @apiParam (请求参数) {Object} [i18n] 通用银行名称国际化
     * @apiParam (i18n的属性) {String} [language] 语言 英文(en_us),中文(zh_cn)
     * @apiParam (i18n的属性) {String} [value] 中/英
     * @apiParamExample {json}
     * {
     *   "id":"123",
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     * }
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "123",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     *  }
     * }
     */

    @ApiOperation(value = "修改单个通用银行", notes = "修改单个通用银行 开发:")
    @PutMapping
    public ResponseEntity<CashBankData> updateCshBank(@ApiParam(value = "付款单头信息") @RequestBody CashBankData cashBankData) {
        return ResponseEntity.ok(cashBankDataService.updateCshBank(cashBankData));
    }

    /**
     * 批量更新银行
     *
     * @param cashBankDatas
     * @return ResponseEntity<List<CashBankData>>
     */
    /**
     * @api {PUT} /api/cash/bank/datas/batch 【通用银行】批量修改
     * @apiDescription 批量修改通用银行
     * @apiGroup PaymentService
     * @apiParamExample {json}
     * [
     * {
     *   "id":"123",
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     * }
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "123",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     *  }
     * }
     * ]
     */

    @ApiOperation(value = "批量修改通用银行", notes = "批量修改通用银行 开发:")
    @PutMapping("/batch")
    public ResponseEntity<List<CashBankData>> updateCshBankBatch(@ApiParam(value = "付款单头信息") @RequestBody List<CashBankData> cashBankDatas) {
        return ResponseEntity.ok(cashBankDataService.updateCshBankBatch(cashBankDatas));
    }

    /**
     * 根据id获取一个银行
     *
     * @param id
     * @return ResponseEntity<CashBankData>
     */
    /**
     * @api {GET} /api/cash/bank/datas/{id} 【通用银行】单个查询
     * @apiDescription 根据id查询单个通用银行
     * @apiGroup PaymentService
     * @apiParamExample {json}
     * /api/cash/bank/datas/123
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "123",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     * }
     * }
     */

    @ApiOperation(value = "根据id获取一个银行", notes = "根据id获取一个银行 开发:")
    @GetMapping("/{id}")
    public ResponseEntity<CashBankData> getCshBankById(@PathVariable Long id) {
        return ResponseEntity.ok(cashBankDataService.selectById(id));
    }

    /**
     * 通用查询-分页
     *
     * @param bankCode  银行代码
     * @param bankName  银行名称
     * @param isEnabled 是否启用
     * @param pageable  页码
     * @return ResponseEntity<List<CashBank>>
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/bank/datas/query 【通用银行】分页查询
     * @apiDescription 根据条件分页查询通用银行
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} [bankCode] 银行代码
     * @apiParam (请求参数) {String} [bankName] 银行名称
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json}
     * /api/cash/bank/datas/query?page=1&size=1
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "123",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "versionNumber": 1
     *   "bankCode": "4",
     *   "bankCodeEn":"2",
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
     *   "bankName":"建某银行",
     *   "i18n":{
     *   "bankName":[
     *   {
     *   "language":"zh_cn",
     *   "value":"某银行"
     *   },{
     *   "language":"en_us",
     *   "value":"CCB"
     *   }
     *  ]
     *  }
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件分页查询通用银行", notes = "根据条件分页查询通用银行 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashBankData>> getCshBankByCond(
            @ApiParam(value = "银行代码") @RequestParam(required = false) String bankCode,
            @ApiParam(value = "银行名称") @RequestParam(required = false) String bankName,
            @ApiParam(value = "是否启用") @RequestParam(required = false, value = "enabled") Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashBankData> list = cashBankDataService.getCshBankDataByCond(bankCode, bankName, isEnabled, page);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/bank/datas/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
