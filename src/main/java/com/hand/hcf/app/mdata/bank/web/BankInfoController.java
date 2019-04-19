package com.hand.hcf.app.mdata.bank.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import com.hand.hcf.app.mdata.bank.dto.BankInfoDTO;
import com.hand.hcf.app.mdata.bank.dto.ReceivablesDTO;
import com.hand.hcf.app.mdata.bank.enums.BankInfoImportCode;
import com.hand.hcf.app.mdata.bank.service.BankInfoService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.utils.FileUtil;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import io.micrometer.core.annotation.Timed;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/21 10:54
 */
@RestController
@RequestMapping(value = "/api/bank/infos")
public class BankInfoController {

    @Autowired
    private BankInfoService bankInfoService;

    @Autowired
    private ExcelExportService excelService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private MessageService messageService;


    /**
     * @api {put} /api/bank/infos/custom/create 创建租户自定义银行信息
     * @apiGroup BankInfo
     * @apiUse BankInfoParam
     * @apiSuccess {Object} bankInfoDTO  银行信息集合
     * @apiSuccess {Long}       bankInfoDTO.id   银行id
     * @apiSuccess {String}     bankInfoDTO.bankCode   银行编码
     * @apiSuccess {String}     bankInfoDTO.bankBranchName   银行分行名称
     * @apiSuccess {String}     bankInfoDTO.bankName   银行名称
     * @apiSuccess {boolean}    bankInfoDTO.enable   是否启用
     * @apiSuccess {String}     bankInfoDTO.countryCode   国家编码
     * @apiSuccess {String}     bankInfoDTO.bankType   银行类型
     * @apiSuccess {String}     bankInfoDTO.province   省份
     * @apiSuccess {String}     bankInfoDTO.provinceCode   省份编码
     * @apiSuccess {String}     bankInfoDTO.city       城市
     * @apiSuccess {String}     bankInfoDTO.cityCode   城市编码
     * @apiSuccess {String}     bankInfoDTO.bankHead   银行头信息
     * @apiSuccess {Long}       bankInfoDTO.tenantId   租户id 0：为系统银行
     * @apiSuccessExample {json} Success-Result
     * {
     * "id": "131180",
     * "bankCode": "320314300022",
     * "bankBranchName": "扬中恒丰村镇银行股份有限公司",
     * "bankName": "农村合作银行",
     * "enable": true,
     * "countryCode": "CHN",
     * "bankType": "",
     * "province": "江苏省",
     * "provinceCode": "320000",
     * "city": "镇江市",
     * "cityCode": "321100",
     * "bankHead": "320",
     * "tenantId": "1"
     * }
     */
    @RequestMapping(value = "/custom/create", method = RequestMethod.POST)
    public ResponseEntity<BankInfoDTO> createCustomBankInfo(@RequestBody BankInfoDTO bankInfoDTO) {
        return ResponseEntity.ok(mapperFacade.map(bankInfoService.addOrUpdateBankInfo(bankInfoDTO, true, OrgInformationUtil.getCurrentTenantId()), BankInfoDTO.class));
    }

    /**
     * @api {put} /api/bank/infos/custom/modify 修改租户自定义银行信息
     * @apiGroup BankInfo
     * @apiUse BankInfoParam
     * @apiSuccess {Object} bankInfoDTO  银行信息集合
     * @apiSuccess {Long}       bankInfoDTO.id   银行id
     * @apiSuccess {String}     bankInfoDTO.bankCode   银行编码
     * @apiSuccess {String}     bankInfoDTO.bankBranchName   银行分行名称
     * @apiSuccess {String}     bankInfoDTO.bankName   银行名称
     * @apiSuccess {boolean}    bankInfoDTO.enable   是否启用
     * @apiSuccess {String}     bankInfoDTO.countryCode   国家编码
     * @apiSuccess {String}     bankInfoDTO.bankType   银行类型
     * @apiSuccess {String}     bankInfoDTO.province   省份
     * @apiSuccess {String}     bankInfoDTO.provinceCode   省份编码
     * @apiSuccess {String}     bankInfoDTO.city       城市
     * @apiSuccess {String}     bankInfoDTO.cityCode   城市编码
     * @apiSuccess {String}     bankInfoDTO.bankHead   银行头信息
     * @apiSuccess {Long}       bankInfoDTO.tenantId   租户id 0：为系统银行
     * @apiSuccessExample {json} Success-Result
     * {
     * "id": "131180",
     * "bankCode": "320314300022",
     * "bankBranchName": "扬中恒丰村镇银行股份有限公司",
     * "bankName": "农村合作银行",
     * "enable": true,
     * "countryCode": "CHN",
     * "bankType": "",
     * "province": "江苏省",
     * "provinceCode": "320000",
     * "city": "镇江市",
     * "cityCode": "321100",
     * "bankHead": "320",
     * "tenantId": "1"
     * }
     */
    @RequestMapping(value = "/custom/modify", method = RequestMethod.PUT)
    public ResponseEntity<BankInfoDTO> modifyCustomBankInfo(@RequestBody BankInfoDTO bankInfoDTO) {
        return ResponseEntity.ok(mapperFacade.map(bankInfoService.addOrUpdateBankInfo(bankInfoDTO, true, OrgInformationUtil.getCurrentTenantId()), BankInfoDTO.class));
    }

    /**
     * @api {delete} /api/bank/infos/remove/id 根据银行id删除自定义银行信息
     * @apiGroup BankInfo
     * @apiParam {Long} id 银行id
     */
    @RequestMapping(value = "/custom/remove/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeBankInfo(@PathVariable Long id) {
        bankInfoService.removeBankInfo(id, OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok().build();
    }

    /**
     * @api {post} /api/bank/infos/custom/search 根据文本分页查询自定义银行信息接口
     * @apiGroup BankInfo
     * @apiParam {String} keyword 文本
     * @apiParam {Integer} page 页码
     * @apiParam {Integer} size 条数
     * @apiParam {boolean} isAll 是否查询所有
     * @apiSuccess {Object[]} solrBankInfoDTOs  银行信息集合
     * @apiSuccess {Long}     solrBankInfoDTOs.id   银行id
     * @apiSuccess {String}     solrBankInfoDTOs.bankCode   银行编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankBranchName   银行分行名称
     * @apiSuccess {String}     solrBankInfoDTOs.bankBranchNamePinyin   银行分行名称
     * @apiSuccess {String}     solrBankInfoDTOs.bankName   银行名称
     * @apiSuccess {boolean}     solrBankInfoDTOs.enable   是否启用
     * @apiSuccess {String}     solrBankInfoDTOs.countryCode   国家编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankType   银行类型
     * @apiSuccess {String}     solrBankInfoDTOs.province   省份
     * @apiSuccess {String}     solrBankInfoDTOs.provinceCode   省份编码
     * @apiSuccess {String}     solrBankInfoDTOs.city   城市
     * @apiSuccess {String}     solrBankInfoDTOs.cityCode   城市编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankHead   银行头信息
     * @apiSuccess {Long}     solrBankInfoDTOs.tenantId   租户id 0：为系统银行
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "131180",
     * "bankCode": "320314300022",
     * "bankBranchName": "扬中恒丰村镇银行股份有限公司",
     * "bankBranchNamePinyin": "扬中恒丰村镇银行股份有限公司",
     * "bankName": "农村合作银行",
     * "enable": true,
     * "countryCode": "CHN",
     * "bankType": "",
     * "province": "江苏省",
     * "provinceCode": "320000",
     * "city": "镇江市",
     * "cityCode": "321100",
     * "bankHead": "320",
     * "tenantId": "0",
     * "_version_": 1590446310892240906,
     * "score": 78.9598
     * }
     * ]
     */
    @RequestMapping(value = "/custom/search", method = RequestMethod.POST)
    public ResponseEntity<List<BankInfoDTO>> searchSolrCustomBankInfo(@RequestBody BankInfoDTO bankInfoDTO,
                                                                      @RequestParam(name = "isAll", required = false, defaultValue = "false") boolean isAll,
                                                                      Pageable pageable) throws URISyntaxException {
        bankInfoDTO.setTenantId(OrgInformationUtil.getCurrentTenantId());
        Page<BankInfoDTO> page = bankInfoService.findBankInfosByKeyword(null,isAll, bankInfoDTO.getTenantId(), null, bankInfoDTO.getBankBranchName(),
                bankInfoDTO.getBankCode(), bankInfoDTO.getBankBranchName(), bankInfoDTO.getOpenAccount(), bankInfoDTO.getCountryCode(), bankInfoDTO.getCityCode(), null, null, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bank/infos/custom/search");
        return new ResponseEntity(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {get} /api/bank/infos/search 根据文本分页查询solr所有银行信息接口
     * @apiGroup BankInfo
     * @apiParam {String} keyword 文本
     * @apiParam {Integer} page 页码
     * @apiParam {Integer} size 条数
     * @apiParam {boolean} isAll 是否查询所有
     * @apiSuccess {Object[]} solrBankInfoDTOs  银行信息集合
     * @apiSuccess {Long}     solrBankInfoDTOs.id   银行id
     * @apiSuccess {String}     solrBankInfoDTOs.bankCode   银行编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankBranchName   银行分行名称
     * @apiSuccess {String}     solrBankInfoDTOs.bankBranchNamePinyin   银行分行名称
     * @apiSuccess {String}     solrBankInfoDTOs.bankName   银行名称
     * @apiSuccess {boolean}     solrBankInfoDTOs.enable   是否启用
     * @apiSuccess {String}     solrBankInfoDTOs.countryCode   国家编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankType   银行类型
     * @apiSuccess {String}     solrBankInfoDTOs.province   省份
     * @apiSuccess {String}     solrBankInfoDTOs.provinceCode   省份编码
     * @apiSuccess {String}     solrBankInfoDTOs.city   城市
     * @apiSuccess {String}     solrBankInfoDTOs.cityCode   城市编码
     * @apiSuccess {String}     solrBankInfoDTOs.bankHead   银行头信息
     * @apiSuccess {Long}     solrBankInfoDTOs.tenantId   租户id 0：为系统银行
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "131180",
     * "bankCode": "320314300022",
     * "bankBranchName": "扬中恒丰村镇银行股份有限公司",
     * "bankBranchNamePinyin": "扬中恒丰村镇银行股份有限公司",
     * "bankName": "农村合作银行",
     * "enable": true,
     * "countryCode": "CHN",
     * "bankType": "",
     * "province": "江苏省",
     * "provinceCode": "320000",
     * "city": "镇江市",
     * "cityCode": "321100",
     * "bankHead": "320",
     * "tenantId": "0",
     * "_version_": 1590446310892240906,
     * "score": 78.9598
     * }
     * ]
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<BankInfoDTO>> searchSolrAllBankInfo(@RequestParam(name = "bankHead", required = false) String bankHead,
                                                                   @RequestParam(name = "bankBranchName", required = false) String bankBranchName,
                                                                   @RequestParam(name = "bankCode", required = false) String bankCode,
                                                                   @RequestParam(name = "openAccount", required = false) String openAccount,
                                                                   @RequestParam(name = "countryCode", required = false) String countryCode,
                                                                   @RequestParam(name = "cityCode", required = false) String cityCode,
                                                                   @RequestParam(name = "swiftCode", required = false) String swiftCode,
                                                                   @RequestParam(name = "enable", required = false) Boolean enable,
                                                                   @RequestParam(name = "isAll", required = false, defaultValue = "false") Boolean isAll,
                                                                   Pageable pageable) throws URISyntaxException {
        Page<BankInfoDTO> page = bankInfoService.findBankInfosByKeyword(bankHead,isAll, OrgInformationUtil.getCurrentTenantId(), 0L, bankBranchName, bankCode, bankBranchName, openAccount, countryCode, cityCode, swiftCode, enable, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bank/infos/search");
        return new ResponseEntity(page.getRecords(),headers, HttpStatus.OK);
    }

    /**
     * @api {get} /api/bank/infos 根据文本分页查询银行信息接口
     * @apiGroup BankInfo
     * @apiParam {String} keyword 文本
     * @apiParam {String} bankCode 银行编码
     * @apiParam {String} bankBranchName 银行分行名称
     * @apiParam {String} openAccount 开户地
     * @apiParam {String} countryCode 国家编码
     * @apiParam {boolean} isAll 是否查询所有
     * @apiParam {Integer} page 页码
     * @apiParam {Integer} size 条数
     * @apiSuccess {Object[]} bankInfoDTOs  银行信息集合
     * @apiSuccess {Long}     bankInfoDTOs.id   银行id
     * @apiSuccess {String}     bankInfoDTOs.bankCode   银行编码
     * @apiSuccess {String}     bankInfoDTOs.bankBranchName   银行分行名称
     * @apiSuccess {String}     bankInfoDTOs.bankName   银行名称
     * @apiSuccess {boolean}    bankInfoDTOs.enable   是否启用
     * @apiSuccess {String}     bankInfoDTOs.countryCode   国家编码
     * @apiSuccess {String}     bankInfoDTOs.bankType   银行类型
     * @apiSuccess {String}     bankInfoDTOs.province   省份
     * @apiSuccess {String}     bankInfoDTOs.provinceCode   省份编码
     * @apiSuccess {String}     bankInfoDTOs.city   城市
     * @apiSuccess {String}     bankInfoDTOs.cityCode   城市编码
     * @apiSuccess {String}     bankInfoDTOs.bankHead   银行头信息
     * @apiSuccess {Long}     bankInfoDTOs.tenantId   租户id 0：为系统银行
     *@apiSuccessExample {json} Success-Result
    [
    {
    "id": "131180",
    "bankCode": "320314300022",
    "bankBranchName": "扬中恒丰村镇银行股份有限公司",
    "bankName": "农村合作银行",
    "enable": true,
    "countryCode": "CHN",
    "bankType": "",
    "province": "江苏省",
    "provinceCode": "320000",
    "city": "镇江市",
    "cityCode": "321100",
    "bankHead": "320",
    "tenantId": "0"
    }
    ]
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BankInfoDTO>> searchBankInfo(@RequestParam(name = "keyword",required = false) String keyword,
                                                            @RequestParam(name = "bankCode",required = false) String bankCode,
                                                            @RequestParam(name = "bankBranchName",required = false) String bankBranchName,
                                                            @RequestParam(name = "openAccount",required = false) String openAccount,
                                                            @RequestParam(name = "countryCode",required = false) String countryCode,
                                                            @RequestParam(name = "cityCode", required = false) String cityCode,
                                                            @RequestParam(name = "isAll",required = false,defaultValue = "false") boolean isAll,
                                                            @RequestParam(name = "isSearchGeneral",required = false,defaultValue = "false") boolean isSearchGeneral,
                                                            Pageable pageable) throws URISyntaxException {
        Page<BankInfoDTO> page = null;
        if (isSearchGeneral) {
            page = bankInfoService.findBankInfosByKeyword(null,isAll, 0L, null, keyword, bankCode, bankBranchName, openAccount, countryCode, cityCode, null, null, pageable);
        } else {
            page = bankInfoService.findBankInfosByKeyword(null,isAll, OrgInformationUtil.getCurrentTenantId(), 0L, keyword, bankCode, bankBranchName, openAccount, countryCode, cityCode, null, null, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bank/infos?keyword="+(StringUtils.isNotEmpty(keyword)?keyword.replace(" ",""):keyword));
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {get} /api/bank/infos/custom/bank/info/template 自定义银行模板下载
     * @apiGroup BankInfo
     * @apiParam {MultipartFile} file 银行excel文件
     */
    @RequestMapping(value = "/custom/bank/info/template", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<byte[]> exportEnumerationItemTemplate() throws IOException {
        byte[] bytes = FileUtil.getFileBinaryForDownload(FileUtil.getTemplatePath(BankInfoImportCode.IMPORT_TEMPLATE_PATH, OrgInformationUtil.getCurrentLanguage()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    /**
     * @api {post} /api/bank/infos/import/custom/bank/info/new 导入自定义银行信息
     * @apiGroup BankInfo
     * @apiParam {MultipartFile} file 银行excel文件
     */
    @RequestMapping(value = "/import/custom/bank/info/new", method = RequestMethod.POST)
    public ResponseEntity importCustomBankInfoNew(@RequestParam MultipartFile file) throws Exception {
        try(InputStream in = file.getInputStream()){
            UUID transactionUUID = bankInfoService.importCustomBankInfoNew(in, OrgInformationUtil.getCurrentTenantId());
            return ResponseEntity.ok(transactionUUID);
        }catch (IOException e){
            throw new BizException("read File error", "读取文件失败！");
        }
    }

    /**
     * 查询导入结果 导入第二步 查询导入成功多少，失败多少，失败的数据有哪些
     * @param transactionID
     * @return
     * @throws IOException
     */
    @GetMapping("/import/new/query/result/{transactionID}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionID") String transactionID) throws IOException {
        ImportResultDTO importResultDTO = bankInfoService.queryResultInfo(transactionID);

        return ResponseEntity.ok(importResultDTO);
    }

    /**
     * 导出错误信息  导出错误信息excel
     * @param transactionID
     * @throws IOException
     */
    @GetMapping("/import/new/error/export/{transactionID}")
    public ResponseEntity errorExport(
            @PathVariable("transactionID") String transactionID) throws IOException {
        return ResponseEntity.ok(bankInfoService.exportFullBankInfoResults(transactionID));
    }

    /**
     * 删除导入的数据 点击取消时删除当前导入的数据（删除临时表数据)
     * @param transactionID
     * @return
     */
    @DeleteMapping("/import/new/delete/{transactionID}")
    public ResponseEntity deleteImportData(@PathVariable("transactionID") String transactionID){
        return ResponseEntity.ok(bankInfoService.deleteImportData(transactionID));
    }

    /**
     *  点击确定时 把临时表数据新增到正式表中
     * @param transactionID
     * @return
     */
    @PostMapping("/import/new/confirm/{transactionID}")
    public ResponseEntity confirmImport(@PathVariable("transactionID") String transactionID){
        return ResponseEntity.ok(bankInfoService.confirmImport(transactionID));
    }
    /**
     * 导出数据
     *
     * @param exportConfig
     * @param pageable
     * @return
     * @throws IOException
     */
    /**
     * @api {GET} /api/bank/infos/export/custom/bank/info/new 【自定义银行数据】导出数据
     * @apiDescription 导出自定义银行数据
     * @apiGroup AccountingService
     * @apiParam (请求参数) {ExportConfig} exportConfig 导出的信息
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     */
    @RequestMapping(value = "/export/custom/bank/info/new")
    public void exportCustomBankInfoNew(HttpServletRequest request,
                                        @RequestBody ExportConfig exportConfig,
                                        HttpServletResponse response,
                                        @RequestParam(name = "bankCode", required = false) String bankCode,
                                        @RequestParam(name = "bankBranchName", required = false) String bankBranchName,
                                        @RequestParam(name = "countryCode", required = false) String countryCode,
                                        @RequestParam(name = "openAccount", required = false) String openAccount,
                                        Pageable pageable) throws IOException {
        com.baomidou.mybatisplus.plugins.Page page = PageUtil.getPage(pageable);
        bankInfoService.findByTenantIdAndBankBranchNameContaining(true, OrgInformationUtil.getCurrentTenantId(), null, bankBranchName, bankCode, countryCode, openAccount, null, null, true, page);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<BankInfoDTO, BankInfoDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<BankInfoDTO> queryDataByPage(com.baomidou.mybatisplus.plugins.Page page) {
                List<BankInfo> results = bankInfoService.findByTenantIdAndBankBranchNameContaining(true, OrgInformationUtil.getCurrentTenantId(), null, bankBranchName, bankCode, countryCode, openAccount, null, null, true, page);
                return mapperFacade.mapAsList(results, BankInfoDTO.class);
            }

            @Override
            public BankInfoDTO toDTO(BankInfoDTO t) {
                if(t != null && t.getEnabled() != null) {
                    if (t.getEnabled()) {
                        t.setEnabledStr(messageService.getMessageDetailByCode("sys.enabled"));
                    }else {
                        t.setEnabledStr(messageService.getMessageDetailByCode("sys.disabled"));
                    }
                }
                return t;
            }


            @Override
            public Class<BankInfoDTO> getEntityClass() {
                return BankInfoDTO.class;
            }
        },threadNumber, request, response);
    }

    /**
     * empFlag:1001 员工
     *         1002 供应商
     *         1003 员工和供应商
     * @param name
     * @param empFlag
     * @return
     */
    @GetMapping(value = "/get/bank/info/by/name")
    public ResponseEntity<List<ReceivablesDTO>> getBankInfoByName(@RequestParam String name, @RequestParam Integer empFlag){
        List<ReceivablesDTO> receivablesDTOS = bankInfoService.getBankInfoByName(name,empFlag);
        return ResponseEntity.ok(receivablesDTOS);
    }
}
