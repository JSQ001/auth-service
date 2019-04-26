package com.hand.hcf.app.payment.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashBankUserDefined;
import com.hand.hcf.app.payment.service.CashBankUserDefinedService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author dong.liu on 2017-11-07
 */
@RestController
@RequestMapping("/api/cash/bank/user/defineds")
public class CashBankUserDefinedController {

    private final CashBankUserDefinedService cashBankUserDefinedService;

    CashBankUserDefinedController(CashBankUserDefinedService cashBankUserDefinedService) {
        this.cashBankUserDefinedService = cashBankUserDefinedService;
    }

    /**
     * 新增一个银行
     *
     * @param cashBankUserDefined
     * @return ResponseEntity<CashBankUserDefined>
     */
    /**
     * @api {POST} /api/cash/bank/user/defineds 【自定义银行】单个新增
     * @apiDescription 新增单个自定义银行
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} bankCode 银行代码
     * @apiParam (请求参数) {String} [bankCodeEn] 银行英文代码
     * @apiParam (请求参数) {String} [swiftCode] 用于跨国业务，外币业务
     * @apiParam (请求参数) {String} bankName 银行名称
     * @apiParam (请求参数) {String} [countryCode] 所在国家代码
     * @apiParam (请求参数) {String} [countryName] 所在国家名称
     * @apiParam (请求参数) {String} [provinceCode] 所在省份代码
     * @apiParam (请求参数) {String} [provinceName] 所在省份名称
     * @apiParam (请求参数) {String} [cityCode] 所在城市代码
     * @apiParam (请求参数) {String} [cityName] 所在城市名称
     * @apiParam (请求参数) {String} [districtCode] 区/县代码
     * @apiParam (请求参数) {String} [districtName] 区/县名称
     * @apiParam (请求参数) {String} [address] 详细地址
     * @apiParam (请求参数) {Long} [tenantId] 租户id
     * @apiParam (请求参数) {Object} [i18n] 自定义银行名称国际化
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
     * @apiSuccess (返回参数) {Long} tenantId 租户id
     * @apiSuccess (返回参数) {Object} i18n 自定义银行名称国际化
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
     *  ]
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
     *  }
     * }
     */
    @PostMapping
    public ResponseEntity<CashBankUserDefined> createCshBank(@RequestBody @Valid CashBankUserDefined cashBankUserDefined) {
        return ResponseEntity.ok(cashBankUserDefinedService.createCshBank(cashBankUserDefined));
    }

    /**
     * 批量新增银行
     *
     * @param cashBankUserDefineds
     * @return ResponseEntity<List<CashBankUserDefined>>
     */
    /**
     * @api {POST} /api/cash/bank/user/defineds/batch 【自定义银行】批量新增
     * @apiDescription 批量新增自定义银行
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
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
     *  ]
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
     *  }
     * }
     * ]
     */
    @PostMapping("/batch")
    public ResponseEntity<List<CashBankUserDefined>> createCshBankBatch(@RequestBody List<CashBankUserDefined> cashBankUserDefineds) {
        return ResponseEntity.ok(cashBankUserDefinedService.createCshBankBatch(cashBankUserDefineds));
    }

    /**
     * 更新一个银行
     *
     * @param cashBankUserDefined
     * @return ResponseEntity<CashBankUserDefined>
     */
    /**
     * @api {PUT} /api/cash/bank/user/defineds 【自定义银行】单个修改
     * @apiDescription 修改单个自定义银行
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
     * @apiParam (请求参数) {Long} [tenantId] 租户id
     * @apiParam (请求参数) {Object} [i18n] 自定义银行名称国际化
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
     * @apiSuccess (返回参数) {Long} tenantId 租户id
     * @apiSuccess (返回参数) {Object} i18n 自定义银行名称国际化
     * @apiParamExample {json} 请求参数
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
     *  }
     *  ]
     *  }
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
     *  }
     * ]
     * }
     * }
     */
    @PutMapping
    public ResponseEntity<CashBankUserDefined> updateCshBank(@RequestBody @Valid CashBankUserDefined cashBankUserDefined) {
        return ResponseEntity.ok(cashBankUserDefinedService.updateCshBank(cashBankUserDefined));
    }

    /**
     * 批量更新银行
     *
     * @param cashBankUserDefineds
     * @return ResponseEntity<List<CashBankUserDefined>>
     */
    /**
     * @api {PUT} /api/cash/bank/user/defineds/batch 【自定义银行】批量修改
     * @apiDescription 批量修改自定义银行
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
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
     *  }
     *  ]
     *  }
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
     *  }
     * ]
     * }
     * }
     * ]
     */
    @PutMapping("/batch")
    public ResponseEntity<List<CashBankUserDefined>> updateCshBankBatch(@RequestBody List<CashBankUserDefined> cashBankUserDefineds) {
        return ResponseEntity.ok(cashBankUserDefinedService.updateCshBankBatch(cashBankUserDefineds));
    }

    /**
     * 根据ID逻辑删除
     *
     * @param id
     * @return
     */
    /**
     * @apiDefine myID
     * @apiParam (请求参数) {Long} id 自定义银行待删除的ID
     */
    /**
     * @apiDefine MyError
     * @apiError UserNotFound The <code>id</code> of the User was not found.
     */
    /**
     * @api {DELETE} /api/cash/bank/user/defineds/{id} 【自定义银行】单个删除
     * @apiDescription 根据id删除单个自定义银行
     * @apiGroup PaymentService
     * @apiUse myID
     * @apiUse MyError
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCshBankById(@PathVariable Long id) {
        cashBankUserDefinedService.deleteCshBankById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据id获取一个银行
     *
     * @param id
     * @return ResponseEntity<CashBankUserDefined>
     */
    /**
     * @api {GET} /api/cash/bank/user/defineds/{id} 【自定义银行】单个查询
     * @apiDescription 根据id查询单个自定义银行
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * /api/cash/bank/user/defineds/123
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
     *   ]
     *  }
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashBankUserDefined> getCshBankById(@PathVariable Long id) {
        return ResponseEntity.ok(cashBankUserDefinedService.selectById(id));
    }

    /**
     * 通用查询-分页
     *
     * @param bankCode  银行代码
     * @param bankName  银行名称
     * @param isEnabled 是否启用
     * @param pageable  页码
     * @return ResponseEntity<List<CashBankUserDefined>>
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/bank/user/defineds/query 【自定义银行】分页查询
     * @apiDescription 根据条件分页查询自定义银行
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} [bankCode] 银行代码
     * @apiParam (请求参数) {String} [bankName] 银行名称
     * @apiParam (请求参数) {String} [countryCode] 所在国家代码
     * @apiParam (请求参数) {String} [provinceCode] 所在省份代码
     * @apiParam (请求参数) {String} [cityCode] 所在城市代码
     * @apiParam (请求参数) {String} [districtCode] 区/县代码
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * /api/cash/bank/user/defineds/query?page=1&size=1
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
     *   ]
     *  }
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<CashBankUserDefined>> getCshBankByCond(
            @RequestParam(required = false) String bankCode,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false, value = "enabled") Boolean isEnabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashBankUserDefined> list = cashBankUserDefinedService.getCshBankDataByCond(bankCode, bankName, countryCode, provinceCode, cityCode, districtCode, isEnabled, page);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/banks/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
