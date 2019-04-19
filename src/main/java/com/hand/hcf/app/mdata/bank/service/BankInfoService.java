package com.hand.hcf.app.mdata.bank.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.dto.LocalizationDTO;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import com.hand.hcf.app.mdata.bank.domain.BankInfoTempDomain;
import com.hand.hcf.app.mdata.bank.dto.BankInfoDTO;
import com.hand.hcf.app.mdata.bank.dto.ReceivablesDTO;
import com.hand.hcf.app.mdata.bank.enums.BankInfoImportCode;
import com.hand.hcf.app.mdata.bank.persistence.BankInfoMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactQO;
import com.hand.hcf.app.mdata.contact.service.ContactBankAccountService;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.location.service.LocalizationDTOService;
import com.hand.hcf.app.mdata.location.service.LocationService;
import com.hand.hcf.app.mdata.supplier.service.VendorInfoService;
import com.hand.hcf.app.mdata.utils.FileUtil;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;


@Slf4j
@Service
public class BankInfoService extends BaseService<BankInfoMapper,BankInfo> {


    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private LocationService locationService;
    public final static String CODE_VALIDATION_REGEX = "[A-Za-z0-9]{1,36}";   // siwft code验证表达式 只能限制输入数字和字母
    public final static String COUNTRY_CODE_CHN = "CHN000000000";       // 中国编码
    public final static String COUNTRY_NAME_CHN = "中国";               // 中国
    public final static String BANK_CODE_VALIDATION_REGEX = "[0-9-]{1,36}";   // 银行code验证表达式 只能限制输入数字


    @Autowired
    private MapperFacade mapper;

    @Autowired
    private BankInfoTempService bankInfoTempService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private LocalizationDTOService localizationDTOService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactBankAccountService contactBankAccountService;

    @Autowired
    private VendorInfoService vendorInfoService;

    /**
     * 根据银行分行名称分页查询银行信息
     * @param keyword：文本
     * @param pageable：分页对象
     * @return
     */
    public Page<BankInfoDTO> findBankInfosByKeyword(boolean isAll, Long tenantId, Long systemTenantId, String keyword, String bankCode, String bankBranchName, String openAccount, String countryCode, String cityCode, String swiftCode, Boolean enable, Pageable pageable) {
        if(!StringUtils.isEmpty(keyword)){
            keyword = keyword.replace(" ","");
        }
        Page<BankInfoDTO> myBatisPage = PageUtil.getPage(pageable);
        List<BankInfo> results = bankInfoMapper.findByTenantIdAndBankBranchNameContaining(isAll, tenantId, systemTenantId, keyword, bankCode, countryCode, openAccount, cityCode, swiftCode, enable, myBatisPage);
        return myBatisPage.setRecords(mapper.mapAsList(results, BankInfoDTO.class));
    }

    public BankInfo findOneByTenantIdAndBankCode(Long tenantId,String bankCode){
        return selectOne(new EntityWrapper<BankInfo>()
        .eq("tenant_id",tenantId)
        .eq("bank_code",bankCode));
    }

    public BankInfo findOneByTenantIdAndBankBranchName(Long tenantId,String bankBranchName){
        return selectOne(new EntityWrapper<BankInfo>()
                .eq("tenant_id",tenantId)
                .eq("bank_branch_name",bankBranchName));
    }

    public BankInfo findOneByTenantIdAndBankBranchNameAndBankCode(Long tenantId,String bankBranchName,String bankCode){
        return selectOne(new EntityWrapper<BankInfo>()
        .eq(tenantId!=null,"tenant_id",tenantId)
        .eq(bankBranchName!=null,"bank_branch_name",bankBranchName)
        .eq(bankCode!=null,"bank_code",bankCode)
        );
    }


    public List<BankInfo> findByTenantIdAndBankBranchName(Long tenantId,String bankBranchName){
        return selectList(new EntityWrapper<BankInfo>()
                .eq(tenantId!=null,"tenant_id",tenantId)
                .eq(bankBranchName!=null,"bank_branch_name",bankBranchName)
                );
    }
    /**
     * 新增或修改银行信息
     * @param bankInfoDTO：银行视图对象
     * @param isCustom：是否自定义银行信息
     * @return
     */
    @Transactional
    public BankInfo addOrUpdateBankInfo(BankInfoDTO bankInfoDTO, boolean isCustom, Long tenantId) {
        // 验证必填字段是否为空
        validationAttribute(bankInfoDTO);
        BankInfo bankInfo = null;
        boolean isValidationBankName = true;
        boolean isValidationBankCode = true;
        BankInfo oldBankInfo = null;
        if(StringUtils.isNotEmpty(bankInfoDTO.getBankCode())){
            // 验证bank code长度是否超过36位
            if(bankInfoDTO.getBankCode().length() > 36){
                throw new BizException(RespCode.BANK_CODE_LT_36);
            }
            // 验证bank code只能包含数字
            if (!PatternMatcherUtil.validationPatterMatcherRegex(bankInfoDTO.getBankCode(), BANK_CODE_VALIDATION_REGEX)) {
                throw new BizException(RespCode.BANK_CODE_MUST_NUMBER);
            }
        }
        if(StringUtils.isNotEmpty(bankInfoDTO.getSwiftCode())){
            // 验证swift code长度是否超过36位
            if(bankInfoDTO.getSwiftCode().length() > 36){
                throw new BizException(RespCode.SWIFT_CODE_LT_36);
            }
            // 验证swift code只能包含数字和字母
            if(!PatternMatcherUtil.validationPatterMatcherRegex(bankInfoDTO.getSwiftCode(),CODE_VALIDATION_REGEX)){
                throw new BizException(RespCode.SWIFT_CODE_MUST_NUMBER);
            }
        }
        if(isCustom){
            bankInfoDTO.setTenantId(tenantId);
        }
        if(null == bankInfoDTO.getId()){    // 新增
            bankInfo = bankInfoDTOToBankInfo(bankInfoDTO);
        }else{  // 修改
            bankInfo = selectById(bankInfoDTO.getId());
            if(null == bankInfo){
                throw new ObjectNotFoundException(BankInfo.class,bankInfoDTO.getId());
            }
            if (bankInfo.getBankBranchName().equals(bankInfoDTO.getBankBranchName())) {
                isValidationBankName = false;
            }
            if(bankInfo.getBankCode().equals(bankInfoDTO.getBankCode())){
                isValidationBankCode = false;
            }
            mapper.map(bankInfoDTO,bankInfo);
        }
        // 验证分行名称是否存在
        if (isValidationBankName) {
            if (checkBankBranchNameIsExist(bankInfoDTO.getTenantId(), bankInfoDTO.getBankBranchName())) {
                throw new BizException("240015");
            }
        }
        // 验证银行代码是否存在
        if(isValidationBankCode){
            // 判断分支行bankCode是否存在
            if (checkBankCodeIsExist(bankInfoDTO.getTenantId(), bankInfoDTO.getBankCode())) {
                throw new BizException(RespCode.BANK_CODE_EXIST);
            }
            // 如果是自定义银行需要校验通用银行分支行名称和银行code是否相同
            if(isCustom){
                oldBankInfo = findOneByTenantIdAndBankBranchNameAndBankCode(0L,bankInfoDTO.getBankBranchName(),bankInfoDTO.getBankCode());
                if (oldBankInfo != null) {
                    throw new BizException(RespCode.BANK_BRANCH_AND_CODE_EXIST);
                }
            }
        }
        if(StringUtils.isNotEmpty(bankInfo.getCountryCode())){
           LocationDTO standardCity = locationService.getLocationByCode(OrgInformationUtil.getCurrentLanguage(),null,bankInfo.getCountryCode());
            if(null != standardCity){
                bankInfo.setCountryName(standardCity.getCountry());
            }else{
                bankInfo.setCountryName(COUNTRY_NAME_CHN);
            }
        }
        // 如果国家为中国
        if(bankInfo.getCountryName().equals(COUNTRY_NAME_CHN)){
            bankInfo.setOpenAccount(bankInfoDTO.getProvince()+"-"+bankInfoDTO.getCity());
        }
        insertOrUpdate(bankInfo);

        return bankInfo;
    }

    /**
     * 根据银行id删除自定义银行信息
     * @param id：银行id
     * @param tenantId：租户id
     */
    @Transactional
    public void removeBankInfo(Long id,Long tenantId){
        // 判断是否存在此自定义银行信息
        BankInfo param = new BankInfo();
        param.setId(id);
        param.setTenantId(tenantId);
        BankInfo bankInfo = bankInfoMapper.selectOne(param);
        if(null == bankInfo){
            throw new ObjectNotFoundException(BankInfo.class,id);
        }
        bankInfo.setDeleted(true);
        updateById(bankInfo);

    }

    public List<BankInfo> findAllBankInfo(Page page){
        return selectPage(page,new EntityWrapper<BankInfo>()).getRecords();
    }

    public List<BankInfo> findByTenantIdAndBankBranchNameContaining(Boolean isAll,  Long tenantId, Long systemTenantId,
                                                                    String keyword,
                                                                    String bankCode,
                                                                    String countryCode,
                                                                    String openAccount,
                                                                    String cityCode,
                                                                    String swiftCode,
                                                                    Boolean enable, Pagination page) {
        return bankInfoMapper.findByTenantIdAndBankBranchNameContaining(isAll, tenantId, systemTenantId, keyword, bankCode, countryCode, openAccount, cityCode, swiftCode, enable, page);
    }
    /**
     * 根据租户id、分行名称查询是否存在
     *
     * @param tenantId：租户id
     * @param bankBranchName：分行名称
     * @return
     */
    public boolean checkBankBranchNameIsExist(Long tenantId, String bankBranchName) {
        return bankInfoMapper.findByTenantIdAndBankBranchName(tenantId, bankBranchName) != null;
    }

    /**
     * 根据租户id、银行编码查询是否存在
     * @param tenantId：租户id
     * @param bankCode：银行编码
     * @return
     */
    public boolean checkBankCodeIsExist(Long tenantId, String bankCode) {
        return 0 != bankInfoMapper.checkBankCodeIsExist(tenantId, bankCode);
    }

    /**
     * 通过bankCode查询
     *
     * @param bankCode
     * @return
     */
    public BankInfoDTO findBankInfoByBankCode(Long tenantId, String bankCode) {
        //先找自定义银行
        BankInfo bankinfo = findOneByTenantIdAndBankBranchNameAndBankCode(tenantId, null,bankCode);
        //再找通用银行
        if (bankinfo == null) {
            bankinfo = findOneByTenantIdAndBankBranchNameAndBankCode(0L,null, bankCode);
        }
        if (bankinfo != null) {
            return bankInfoToBankInfoDTO(bankinfo);
        } else {
            return null;
        }
    }

    /**
     * 初始化系统银行信息对应国家名称信息
     */
    public void initAllCountryNameInfo(){
        Map<String,Object> param = new HashMap<>();
        param.put("tenant_id","0");
        List<BankInfo> bankInfoList = bankInfoMapper.selectByMap(param);
        if(CollectionUtils.isEmpty(bankInfoList)){
            log.error("Bank is empty");
            throw new RuntimeException("Bank is empty");
        }else{
            // 获取银行信息的国家编码
            List<String> countryCodes = bankInfoMapper.findAllCountryCode();
            Map<String,Object> countryMap = new HashMap<>();
            LocationDTO standardCity = null;
            String countryCode = "";
            String tempCountryCode = "";
            for(int i = 0;i < countryCodes.size();i++){
                countryCode = countryCodes.get(i);
                if(StringUtils.isEmpty(countryCode)){
                    countryCode = "CHN";
                }
                tempCountryCode = countryCode + "000000000";
                standardCity = locationService.getLocationByCode(OrgInformationUtil.getCurrentLanguage(),null,tempCountryCode);
                if(null != standardCity){
                    countryMap.put(countryCode,standardCity.getCountry());
                }
            }
            BankInfo bankInfo = null;
            String openAccount = "";
            for(int i = 0;i < bankInfoList.size();i++){
                bankInfo = bankInfoList.get(i);
                if(StringUtils.isEmpty(bankInfo.getCountryCode())){ // 默认为中国
                    bankInfo.setCountryCode(COUNTRY_CODE_CHN);
                    bankInfo.setCountryName(COUNTRY_NAME_CHN);
                }else{
                    if(countryMap.containsKey(bankInfo.getCountryCode())){
                        bankInfo.setCountryName(countryMap.get(bankInfo.getCountryCode()).toString());
                        bankInfo.setCountryCode(bankInfo.getCountryCode()+"000000000");
                    }else{  // 找不到则为中国
                        bankInfo.setCountryCode(COUNTRY_CODE_CHN);
                        bankInfo.setCountryName(COUNTRY_NAME_CHN);
                    }
                }
                if(StringUtils.isNotEmpty(bankInfo.getProvince()) && StringUtils.isNotEmpty(bankInfo.getCity())){
                    openAccount = (StringUtils.isNotEmpty(bankInfo.getProvince())?bankInfo.getProvince():"")+"-"+(StringUtils.isNotEmpty(bankInfo.getCity())?bankInfo.getCity():"");
                }else if(StringUtils.isNotEmpty(bankInfo.getProvince())){
                    openAccount = bankInfo.getProvince();
                }else if(StringUtils.isNotEmpty(bankInfo.getCity())){
                    openAccount = bankInfo.getCity();
                }
                bankInfo.setOpenAccount(openAccount);
                insertOrUpdate(bankInfo);
            }
        }
    }

    /**
     * 验证属性是否为空
     * @param bankInfoDTO：银行视图对象
     */
    private void validationAttribute(BankInfoDTO bankInfoDTO){
        if(StringUtils.isEmpty(bankInfoDTO.getBankCode())){
            throw new BizException(RespCode.BANK_CODE_NOT_NULL);
        }
        if(StringUtils.isEmpty(bankInfoDTO.getBankName())){
            throw new BizException(RespCode.BANK_NAME_NOT_NULL);
        }
        if(StringUtils.isEmpty(bankInfoDTO.getBankBranchName())){
            throw new BizException(RespCode.BANK_BRANCH_NAME_NOT_NULL);
        }
    }


    /**
     * 解析导入数据
     *
     * @param tenantId：租户id
     * @param bankInfoImportDTOs：导入银行信息集合
     * @param transactionLog：事务日志
     */
    /*private void paseImportData(Long tenantId, List<BankInfoImportDTO> bankInfoImportDTOs, BatchTransactionLog transactionLog) {
        BankInfo bankInfo = null;
        JSONObject error = transactionLog.getErrors();
        for (BankInfoImportDTO importDTO : bankInfoImportDTOs) {
            try {
                // 验证租户下银行分行是否存在
                if (checkBankBranchNameIsExist(tenantId, importDTO.getBankBranchName())) {
                    throw new BizException("240015", "bank branch name already exists");
                }
                // 验证租户下银行代码是否存在
                if (checkBankCodeIsExist(tenantId, importDTO.getBankCode())) {
                    throw new BizException("240006", "bank code already exists");
                }
                bankInfo = new BankInfo();
                bankInfo.setTenantId(tenantId);
                bankInfo.setBankCode(importDTO.getBankCode());
                bankInfo.setBankBranchName(importDTO.getBankBranchName());
                bankInfo.setBankName(importDTO.getBankName());
                if (StringUtils.isNotEmpty(importDTO.getCountryCode())) {
                    bankInfo.setCountryCode(importDTO.getCountryCode() + "000000000");
                }
                bankInfo.setCountryName(importDTO.getCountryName());
                bankInfo.setSwiftCode(importDTO.getSwiftCode());
                bankInfo.setOpenAccount(importDTO.getOpenAccount());
                bankInfo.setDetailAddress(importDTO.getDetailAddress());
                bankInfoMapper.insert(bankInfo);
                if (esBankIndexService.isElasticSearchEnable()) {
                    esBankIndexService.saveBankInfoIndex(bankInfo);
                }
                transactionLog.setSuccessEntities(transactionLog.getSuccessEntities() + 1);
            } catch (BizException e) {
                transactionLog.setSuccessEntities(transactionLog.getSuccessEntities() + 1);
                if (e.getMessage().equals("bank branch name already exists")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6048008"), importDTO.getRowNum());
                    importDTO.setErrorDetail(messageService.getMessageDetailByCode("6048008"));
                } else if (e.getMessage().equals("bank code already exists")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6048007"), importDTO.getRowNum());
                    importDTO.setErrorDetail(messageService.getMessageDetailByCode("6048007"));
                } else {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6047018"), importDTO.getRowNum());
                    importDTO.setErrorDetail(messageService.getMessageDetailByCode("6047018"));
                }
                bankInfoImportMap.get(transactionLog.getTransactionOid()).add(importDTO);
            } catch (Exception e) {
                transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
                ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6047018"), importDTO.getRowNum());
                importDTO.setErrorDetail(messageService.getMessageDetailByCode("6047018"));
                bankInfoImportMap.get(transactionLog.getTransactionOid()).add(importDTO);
            }
        }
    }*/





    /**
     * 生成记录日志
     *
     * @param sheet
     * @return
     */
    /*private BatchTransactionLog genraterLog(Sheet sheet, BatchOperationTypeEnum batchOperationType) {
        BatchTransactionLog transactionLog = new BatchTransactionLog(batchOperationType, sheet.getPhysicalNumberOfRows() - 5);
        transactionLog.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        JSONObject error = new JSONObject();
        transactionLog.setErrors(error);
        transactionLog.setStatus(TransactionStatus.PROCESS_DATA.getId());
        transactions.put(transactionLog.getTransactionOid(), transactionLog);
        bankInfoImportMap.put(transactionLog.getTransactionOid(), new ArrayList<>());
        return transactionLog;
    }*/

    /*public BankInfoDTO findBankInfoByBankBranchName(Long tenantId, String bankBranchName) {
        //先找自定义银行
        List<BankInfo> bankInfos = findByTenantIdAndBankBranchName(tenantId, bankBranchName);
        if (CollectionUtils.isEmpty(bankInfos)) {
            bankInfos = findByTenantIdAndBankBranchName(0L, bankBranchName);
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(bankInfos)) {
            return bankInfoToBankInfoDTO(bankInfos.get(0));
        } else {
            return null;
        }
    }*/

    /**
     * 根据银行id
     * @param bankInfoId  银行id
     * @return
     */
    public BankInfo selectBankInfoById(Long bankInfoId){
        return bankInfoMapper.selectById(bankInfoId);
    }



    public BankInfo bankInfoDTOToBankInfo(BankInfoDTO bankInfoDTO){
        BankInfo bankInfo = new BankInfo();
        mapper.map(bankInfoDTO, bankInfo);

        return bankInfo;
    }

    public BankInfoDTO bankInfoToBankInfoDTO(BankInfo bankInfo){
        BankInfoDTO bankInfoDTO = new BankInfoDTO();
        if (bankInfo != null) {
            mapper.map(bankInfo, bankInfoDTO);
        }

        return bankInfoDTO;
    }

    /**
     * 导出失败的银行数据信息
     *
     * @param transactionID：失败的银行数据信息
     * @return
     */
    public byte[] exportFullBankInfoResults(String transactionID) {
        List<BankInfoTempDomain> bankInfoImportDTOs = bankInfoTempService.selectList(
                new EntityWrapper<BankInfoTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            is = com.itextpdf.text.io.StreamUtil.getResourceStream(FileUtil.getTemplatePath(BankInfoImportCode.ERROR_TEMPLATE_PATH,OrgInformationUtil.getCurrentLanguage()));
            workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = BankInfoImportCode.EXCEL_BASEROW;

            Row row = null;
            Cell cell = null;
            for (BankInfoTempDomain importDTO : bankInfoImportDTOs) {
                row = sheet.createRow((startRow++));
                cell = row.createCell(BankInfoImportCode.ROW_NUMBER);
                cell.setCellValue(importDTO.getRowNumber());
                cell = row.createCell(BankInfoImportCode.COUNTRY_CODE);
                cell.setCellValue(importDTO.getCountryCode());
                cell = row.createCell(BankInfoImportCode.BANK_CODE);
                cell.setCellValue(importDTO.getBankCode());
                cell = row.createCell(BankInfoImportCode.SWIFT_CODE);
                cell.setCellValue(importDTO.getSwiftCode());
                cell = row.createCell(BankInfoImportCode.BANK_NAME);
                cell.setCellValue(importDTO.getBankName());
                cell = row.createCell(BankInfoImportCode.BANK_BRANCH_NAME);
                cell.setCellValue(importDTO.getBankBranchName());
//                cell = row.createCell(BankInfoImportCode.OPEN_ACCOUNT);
//                cell.setCellValue(importDTO.getOpenAccount());
//                cell = row.createCell(BankInfoImportCode.DETAIL_ADDRESS);
//                cell.setCellValue(importDTO.getDetailAddress());
                cell = row.createCell(BankInfoImportCode.IMPORT_ENABLED);
                cell.setCellValue(importDTO.getEnabledStr());
                cell = row.createCell(BankInfoImportCode.IMPORT_ERROR_DETAIL);
                cell.setCellValue(importDTO.getErrorDetail());
            }
            bos = new ByteArrayOutputStream();
            workbook.write(bos);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    @Transactional
    public UUID importCustomBankInfoNew(InputStream in, Long tenantID) throws Exception {
        UUID batchNumber = UUID.randomUUID();
        ExcelImportHandler<BankInfoTempDomain> excelImportHandler = new ExcelImportHandler<BankInfoTempDomain>() {

            @Override
            public void clearHistoryData() {
                bankInfoTempService.deleteHistoryData();
            }

            @Override
            public Class<BankInfoTempDomain> getEntityClass() {
                return BankInfoTempDomain.class;
            }

            @Override
            public List<BankInfoTempDomain> persistence(List<BankInfoTempDomain> list) {
                list.stream().forEach(bankInfoTempDomain -> {
                    bankInfoTempDomain.setVersionNumber(1);
                    bankInfoTempDomain.setCreatedBy(OrgInformationUtil.getCurrentUserId());
                    bankInfoTempDomain.setCreatedDate(ZonedDateTime.now());
                    bankInfoTempDomain.setLastUpdatedBy(OrgInformationUtil.getCurrentTenantId());
                    bankInfoTempDomain.setLastUpdatedDate(ZonedDateTime.now());
                });
                bankInfoTempService.insertBatch(list);
                bankInfoTempService.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<BankInfoTempDomain> list) {
                checkImportData(list,batchNumber.toString());
            }
        };
        excelImportService.importExcel(in,false,5,excelImportHandler);
        return batchNumber;
    }

    private void checkImportData(List<BankInfoTempDomain> list, String batchNumber) {
        // 非空校验
        list
                .stream()
                .filter(e -> StringUtils.isEmpty(e.getBankCode())
                        || StringUtils.isEmpty(e.getBankName())
                        || StringUtils.isEmpty(e.getBankBranchName())
                        || StringUtils.isEmpty(e.getEnabledStr()))
                .forEach(e -> {
                    e.setErrorFlag(true);
                    e.setErrorDetail("必输字段不能为空！");
                });
        List<String> countryCodeList = locationService.listCountryCode();
        list
                .stream()
                .forEach(e ->{
                    // 验证银行编码长度是否超过36位并只能包含数字和减号
                    if(e.getBankCode() != null && !PatternMatcherUtil.validationPatterMatcherRegex(e.getBankCode(), BANK_CODE_VALIDATION_REGEX)){
                        e.setErrorFlag(true);
                        if(e.getErrorDetail() != null){
                            e.setErrorDetail(e.getErrorDetail() + "银行编码长度是否超过36位并只能包含数字和减号!");
                        }else{
                            e.setErrorDetail("银行编码长度是否超过36位并只能包含数字和减号!");
                        }
                    }
                    if(!StringUtils.isEmpty(e.getCountryCode())){
                        Boolean locationExist = countryCodeList.stream().anyMatch((countryCode)-> countryCode.equals(e.getCountryCode()));
                        if(!locationExist){
                            e.setErrorFlag(true);
                            if(e.getErrorDetail() != null){
                                e.setErrorDetail(e.getErrorDetail() + "国家编码不存在!");
                            }else{
                                e.setErrorDetail("国家编码不存在!");
                            }
                        }else{
                            e.setCountryCode(e.getCountryCode()+"000000000");
                            LocalizationDTO local = localizationDTOService.getOneLocalizationCountryByCode(OrgInformationUtil.getCurrentLanguage(), e.getCountryCode());
                            e.setCountryName(local.getCountry());
                        }
                    }

                    e.setBatchNumber(batchNumber);
                    if(e.getErrorFlag() == null){
                        e.setErrorFlag(false);
                    }
                    // 验证启用输入是否合法，并设置值
                    if(!StringUtils.isEmpty(e.getEnabledStr())){
                        if(e.getEnabledStr().toUpperCase().equals("Y")){
                            e.setEnabled(true);
                        }else if(e.getEnabledStr().toUpperCase().equals("N")){
                            e.setEnabled(false);
                        }else{
                            throw new BizException(RespCode.BANK_IMPORT_ENABLE_ILLEGAL);
                        }
                    }
                });

    }

    /*查询导入结果*/
    public ImportResultDTO queryResultInfo(String transactionID) {

        return bankInfoTempService.queryResultInfo(transactionID);
    }
    /*取消时删除临时表数据*/

    @Transactional
    public Boolean deleteImportData(String transactionID) {
        boolean delete = bankInfoTempService.delete(new EntityWrapper<BankInfoTempDomain>().eq("batch_number", transactionID));
        return delete;
    }
    /*将临时表数据新增到正式表中*/
    @Transactional
    public Boolean confirmImport(String transactionID) {
        Boolean result = bankInfoTempService.confirmImport(transactionID);
        List<Long> bankInfoIds = bankInfoTempService.getImportBankInfoIds(transactionID);
        List<BankInfo> bankInfos = bankInfoMapper.selectBatchIds(bankInfoIds);
//        bankInfos.stream().forEach(bankInfo -> {
//            esBankInfoService.saveBankInfoIndex(bankInfo);
//        });
        this.deleteImportData(transactionID);
        return result;
    }


    //根据用户名称或供应商名称查询银行账户信息
    public List<ReceivablesDTO> getBankInfoByName(String name, Integer empFlag) {
        List<Contact> contactList = contactService.listByQO(ContactQO.builder()
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .fullName(name)
                .build());
        List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
        if (empFlag == 1001 || empFlag == 1003) {
            contactList.forEach(
                    contact -> {
                        ReceivablesDTO receivablesDTO = new ReceivablesDTO();

                        receivablesDTO.setId(contact.getUserId());
                        receivablesDTO.setName(contact.getFullName());
                        receivablesDTO.setCode(contact.getEmployeeId());
                        List<ContactBankAccountDTO> bankAccountDTOS = contactBankAccountService.getContactBankAccountByUserOidNoPage(contact.getUserOid());
                        List<BankInfo> bankInfos = new ArrayList<>();
                        for (ContactBankAccountDTO bankAccountDTO : bankAccountDTOS) {
                            BankInfo bankInfo = new BankInfo();
                            bankInfo.setBankCode(bankAccountDTO.getBankCode());
                            bankInfo.setBankName(bankAccountDTO.getBankName());
                            bankInfo.setNumber(bankAccountDTO.getBankAccountNo());
                            bankInfos.add(bankInfo);
                        }
                        receivablesDTO.setBankInfos(bankInfos);
                        receivablesDTOS.add(receivablesDTO);
                    }
            );
        }
        if (empFlag == 1001) {
            return receivablesDTOS;
        }
        if (empFlag == 1002 || empFlag == 1003) {
            List<ReceivablesDTO> vendorBankInfoByInput
                    = vendorInfoService.selectVendorInfosByCompanyIdAndVendorName(OrgInformationUtil.getCurrentCompanyId(), name);
            receivablesDTOS.addAll(vendorBankInfoByInput);
        }
        return receivablesDTOS;
    }
}
