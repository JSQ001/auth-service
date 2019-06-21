package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReportTempDomain;
import com.hand.hcf.app.ant.taxreimburse.dto.TaxBlendDataDTO;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowMapper;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportMapper;
import com.hand.hcf.app.ant.taxreimburse.utils.TaxReimburseConstans;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.persistence.CompanyMapper;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.persistence.DepartmentMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报service
 * @date 2019/5/29 11:30
 */
@Service
public class ExpTaxReportService extends BaseService<ExpTaxReportMapper, ExpTaxReport> {

    @Autowired
    ExpTaxReportMapper expTaxReportMapper;

    @Autowired
    ExpBankFlowMapper expBankFlowMapper;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private SysCodeValeMapper sysCodeValeMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExpTaxReportTempDomainService taxReportTempDomainService;

    @Autowired
    private ExpBankFlowTempDomainService expBankFlowTempDomainService;

    @Autowired
    private ExpBankFlowService expBankFlowService;


    /**
     * 获取税金申报List的数据-分页查询
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param taxCategoryId
     * @param status
     * @param page
     * @return
     */
    public List<ExpTaxReport> getExpTaxReportByCon(String companyId,/*公司*/
                                                   String currencyCode,/*币种*/
                                                   Boolean blendStatus,/*勾兑状态*/
                                                   String requestPeriodFrom,/*申报期间从*/
                                                   String requestPeriodTo,/*申报期间至*/
                                                   BigDecimal requestAmountFrom,/*申报金额从*/
                                                   BigDecimal requestAmountTo,/*申报金额至*/
                                                   String businessSubcategoryName,/*业务小类代码*/
                                                   String taxCategoryId,/*税种代码*/
                                                   Boolean status,/*报账状态*/
                                                   Page page) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }

        Wrapper<ExpTaxReport> wrapper = new EntityWrapper<ExpTaxReport>()
                .eq(companyId != null, "company_id", companyId)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .eq(blendStatus != null, "blend_status", blendStatus)
                .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                .le(requestAmountTo != null, "request_amount", requestAmountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                .eq(status != null, "status", status);
        List<ExpTaxReport> expTaxReportList = expTaxReportMapper.selectPage(page, wrapper);
        converDesc(expTaxReportList);
        return expTaxReportList;
    }

    /**
     * 分页查询税金申报数据
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @param page
     * @return
     */
    public Page<ExpTaxReport> getExpTaxReportByPage(
            String companyId,/*公司*/
            String currencyCode,/*币种*/
            Boolean blendStatus,/*勾兑状态*/
            String requestPeriodFrom,/*申报期间从*/
            String requestPeriodTo,/*申报期间至*/
            BigDecimal requestAmountFrom,/*申报金额从*/
            BigDecimal requestAmountTo,/*申报金额至*/
            String businessSubcategoryName,/*业务小类代码*/
            String taxCategoryId,/*税种代码*/
            Boolean status,/*报账状态*/
            Page page
    ) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }

        Wrapper<ExpTaxReport> wrapper =
                new EntityWrapper<ExpTaxReport>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                        .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                        .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                        .le(requestAmountTo != null, "request_amount", requestAmountTo)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                        .eq(status != null, "status", status);


        List<ExpTaxReport> expTaxReportList = this.selectPage(page, wrapper).getRecords();
        converDesc(expTaxReportList);
        return page.setRecords(expTaxReportList);
    }

    /**
     * 导出税金申报数据
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @return
     */
    public List<ExpTaxReport> exportExpTaxReport(
            String companyId,/*公司*/
            String currencyCode,/*币种*/
            Boolean blendStatus,/*勾兑状态*/
            String requestPeriodFrom,/*申报期间从*/
            String requestPeriodTo,/*申报期间至*/
            BigDecimal requestAmountFrom,/*申报金额从*/
            BigDecimal requestAmountTo,/*申报金额至*/
            String businessSubcategoryName,/*业务小类代码*/
            String taxCategoryId,/*税种代码*/
            Boolean status/*报账状态*/
    ) {
        String taxCategoryCode = null;
        if (StringUtils.isNotEmpty(taxCategoryId)) {
            SysCodeValue sysCodeValue = sysCodeValeMapper.selectById(taxCategoryId);
            //根据传递过来的Id获取到传递过来的taxCategoryCode
            taxCategoryCode = sysCodeValue.getValue();
        }
        Wrapper<ExpTaxReport> wrapper =
                new EntityWrapper<ExpTaxReport>()
                        .eq(companyId != null, "company_id", companyId)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                        .eq(blendStatus != null, "blend_status", blendStatus)
                        .ge(requestPeriodFrom != null, "request_period", requestPeriodFrom)
                        .le(requestPeriodTo != null, "request_period", requestPeriodTo)
                        .ge(requestAmountFrom != null, "request_amount", requestAmountFrom)
                        .le(requestAmountTo != null, "request_amount", requestAmountTo)
                        .like(org.apache.commons.lang3.StringUtils.isNotEmpty(businessSubcategoryName), "business_subcategory_name", businessSubcategoryName)
                        .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(taxCategoryCode), "tax_category_code", taxCategoryCode)
                        .eq(status != null, "status", status);


        List<ExpTaxReport> expTaxReportList = this.selectList(wrapper);
        converDesc(expTaxReportList);
        return expTaxReportList;

    }

    /**
     * 转换DESC
     *
     * @param expTaxReportList
     * @return
     */
    public List<ExpTaxReport> converDesc(List<ExpTaxReport> expTaxReportList) {
        expTaxReportList.stream().forEach(expTaxReport -> {

            //公司转化
            if (null != expTaxReport.getCompanyId()) {
                Company company = companyService.selectById(expTaxReport.getCompanyId());
                if (StringUtils.isNotEmpty(company.getName())) {
                    expTaxReport.setCompanyName(company.getName());
                }
            }

            //币种转化
            if (StringUtils.isNotEmpty(expTaxReport.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(expTaxReport.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                    expTaxReport.setCurrencyName(currencyOne.getCurrencyName());
                }
            }

            //税种转化
            if (StringUtils.isNotEmpty(expTaxReport.getTaxCategoryCode())) {
                SysCodeValue sysCodeValueOne = new SysCodeValue();
                sysCodeValueOne.setValue(expTaxReport.getTaxCategoryCode());
                SysCodeValue sysCodeValue1 = sysCodeValeMapper.selectOne(sysCodeValueOne);
                if (StringUtils.isNotEmpty(sysCodeValue1.getName())) {
                    expTaxReport.setTaxCategoryName(sysCodeValue1.getName());
                }
            }

        });
        return expTaxReportList;
    }

    ;

    /**
     * 根据Id 批量删除税金申报数据
     *
     * @param ids
     */
    public void deleteTaxReport(String ids) {
        String idsArr[] = ids.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
            //当勾兑状态为0或者false--未勾兑时 才可以删除
            if ((!expTaxReport.getBlendStatus()) || (expTaxReport.getBlendStatus() == false)) {
                expTaxReportMapper.deleteById(Long.valueOf(idsArr[i]));
            } else {
                throw new BizException(TaxReimburseConstans.WARNING);
            }
        }
    }

    /**
     * 分组查询
     *
     * @return
     */
    public List<TaxBlendDataDTO> getBlendDataByGroup() {
        return expTaxReportMapper.getBlendDataByGroup();
    }


    /**
     * 根据相同的公司和币种更新数据状态
     *
     * @param companyId
     * @param currencyCode
     */
    public void updateTaxReport(Long companyId, String currencyCode) {
        expTaxReportMapper.updateTaxReport(companyId, currencyCode);
    }


    public List<ExpTaxReport> saveExpTaxReport(String ids, List<ExpTaxReport> expTaxReportList) {
        String idsArr[] = ids.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
        }
        return null;
    }

    /**
     * 发起报账
     *
     * @param ids
     */
    public Boolean makeReimburse(String ids) {
        Boolean flag = false;//返回是否可以发起报账的标志
        String idsArr[] = ids.split(",");
        Map<Long, String> map = getMap(ids);//也可以使用hashSet存储
        //循环获取map中的key,value
        int sum = 0;//合计map中涉及到的数据的总计
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            Long companyId = entry.getKey();
            String currencyCode = entry.getValue();
            Wrapper<ExpTaxReport> wrapper =
                    new EntityWrapper<ExpTaxReport>()
                            .eq(companyId != null, "company_id", companyId)
                            .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                            .eq("blend_status", '1')
                            .eq("status", '0');

            int count = expTaxReportMapper.selectCount(wrapper);
            sum = +count;
        }

        //根据获取到的companyId、currencyCode查询此分组下已勾兑的未报账的所有数据，然后比较数量
        // 判断是否全部勾选，若全部勾选，则进行发起报账
        if (sum == idsArr.length) {
            flag = true;
        } else {
            //若未全部勾选，则返回提示
            throw new BizException(TaxReimburseConstans.WARNING1);
        }
        //确认以上税金申报数据可以进行报账，然后关联流水数据--根据公司/币种/勾兑状态/报账状态进行，然后跳转页面，
        return flag;
    }

    /**
     * 根据选择的税金申报数据的id,返回涉及到的公司和币种的kv组合，以便判断是否全部将同组合的数据勾选，然后查询出对应已勾兑的银行流水的数据
     *
     * @param ids
     * @return
     */
    public Map<Long, String> getMap(String ids) {
        Map<Long, String> map = new HashMap<>();
        String idsArr[] = ids.split(",");
        //将所有id涉及到的companyId、currencyCode组合加入到map中，然后进行比较。
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
            Long companyId = expTaxReport.getCompanyId();
            String currencyCode = expTaxReport.getCurrencyCode();
            boolean blendStatus = expTaxReport.getBlendStatus();
            boolean status = expTaxReport.getStatus();
            if (status == false) {
                if (blendStatus || blendStatus == true) {
                    //当map中不存在此key,或者不存在此vlaue就加入--只有两种同时存在则不加入
                    while (!map.containsKey(companyId) || !map.containsValue(currencyCode)) {
                        map.put(companyId, currencyCode);
                    }
                } else {
                    throw new BizException(TaxReimburseConstans.WARNING2);
                }
            } else if (status == true) {
                throw new BizException(TaxReimburseConstans.WARNING3);
            }
        }
        return map;
    }


    /**
     * 根据外键获取税金明细信息(分页查询）--导出数据用
     *
     * @param headerId
     * @param page
     * @return
     */
    public Page<ExpTaxReport> getTaxReportDetail(String headerId, Page page) {
        List<ExpTaxReport> expTaxReportList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpTaxReport> wrapper = new EntityWrapper<ExpTaxReport>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expTaxReportList = this.selectPage(page, wrapper).getRecords();
            expTaxReportList.stream().forEach(expTaxReport -> {
                //公司转化
                if (null != expTaxReport.getCompanyId()) {
                    Company company = companyService.selectById(expTaxReport.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expTaxReport.setCompanyName(company.getName());
                    }
                }
            });
        }
        return page.setRecords(expTaxReportList);
    }

    /**
     * 根据外键导出税金明细信息
     *
     * @param headerId
     * @return
     */
    public List<ExpTaxReport> exportTaxDetail(String headerId) {
        List<ExpTaxReport> expTaxReportList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpTaxReport> wrapper = new EntityWrapper<ExpTaxReport>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expTaxReportList = this.selectList(wrapper);
            expTaxReportList.stream().forEach(expTaxReport -> {
                //公司转化
                if (null != expTaxReport.getCompanyId()) {
                    Company company = companyService.selectById(expTaxReport.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expTaxReport.setCompanyName(company.getName());
                    }
                }
            });
        }
        return expTaxReportList;
    }


    /**
     * 根据外键获取税金明细信息(分页查询）--报账单详情数据税金信息查询
     *
     * @param headerId
     * @param page
     * @return
     */
    public List<ExpTaxReport> getTaxReportDetailList(String headerId, Page page) {
        List<ExpTaxReport> expTaxReportList = new ArrayList<>();
        if (StringUtils.isNotEmpty(headerId)) {
            Long expReimburseHeaderId = Long.valueOf(headerId);
            Wrapper<ExpTaxReport> wrapper = new EntityWrapper<ExpTaxReport>()
                    .eq(expReimburseHeaderId != null, "exp_reimburse_header_id", expReimburseHeaderId);
            expTaxReportList = expTaxReportMapper.selectPage(page, wrapper);
            expTaxReportList.stream().forEach(expTaxReport -> {
                //公司转化
                if (null != expTaxReport.getCompanyId()) {
                    Company company = companyService.selectById(expTaxReport.getCompanyId());
                    if (StringUtils.isNotEmpty(company.getName())) {
                        expTaxReport.setCompanyName(company.getName());
                    }
                }

                //税种转化
                if (StringUtils.isNotEmpty(expTaxReport.getTaxCategoryCode())) {
                    SysCodeValue sysCodeValueOne = new SysCodeValue();
                    sysCodeValueOne.setValue(expTaxReport.getTaxCategoryCode());
                    SysCodeValue sysCodeValue1 = sysCodeValeMapper.selectOne(sysCodeValueOne);
                    if (StringUtils.isNotEmpty(sysCodeValue1.getName())) {
                        expTaxReport.setTaxCategoryName(sysCodeValue1.getName());
                    }
                }
            });
        }
        return expTaxReportList;
    }

    /**
     * 导入数据，根据sheet页名称来区别
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public UUID importPublicData(MultipartFile file) throws Exception {
        try {
            InputStream in = file.getInputStream();
            XSSFSheet sheet = new XSSFWorkbook(in).getSheetAt(0);
            String sheetName = sheet.getSheetName();
            if (TaxReimburseConstans.TEMPLATE_SHEET_KEYWORD1.contains(sheetName)) {
                return importTaxReport(file);
            } else if (TaxReimburseConstans.TEMPLATE_SHEET_KEYWORD2.contains(sheetName)) {
                return expBankFlowService.importBankFlow(file);
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * 导入税金申报的数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public UUID importTaxReport(MultipartFile file) throws Exception {
        //导入之前清除所有未勾兑的数据
        Wrapper wrapper = new EntityWrapper<ExpTaxReport>()
                .eq("blend_status", 0);
        expTaxReportMapper.delete(wrapper);
        UUID batchNumber = UUID.randomUUID();
        InputStream in = file.getInputStream();
        ExcelImportHandler<ExpTaxReportTempDomain> excelImportHandler = new ExcelImportHandler<ExpTaxReportTempDomain>() {
            @Override
            public void clearHistoryData() {
                taxReportTempDomainService.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return ExpTaxReportTempDomain.class;
            }

            @Override
            public List<ExpTaxReportTempDomain> persistence(List<ExpTaxReportTempDomain> list) {
                // 导入数据
                taxReportTempDomainService.insertBatch(list);
                return list;
            }

            @Override
            public void check(List<ExpTaxReportTempDomain> list) {

                //数据合法性校验-有效性+唯一性
                taxReportTempDomainService.checkImportData(list, batchNumber);
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }


    /**
     * 查询导入结果
     *
     * @param transactionID
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionID) {
        if (taxReportTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return taxReportTempDomainService.queryResultInfo(transactionID);
        } else if (expBankFlowTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return expBankFlowTempDomainService.queryResultInfo(transactionID);
        } else {
            return null;
        }
    }

    /**
     * 根据批次号查询失败的数据
     *
     * @param transactionID
     * @return
     */
    public byte[] exportFailedData(String transactionID) {
        if (taxReportTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return taxReportTempDomainService.exportFailedData(transactionID);
        } else if (expBankFlowTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return expBankFlowTempDomainService.exportFailedData(transactionID);
        } else {
            return null;
        }
    }

    /**
     * 根据批次号删除临时表中的数据
     *
     * @param transactionID
     * @return
     */
    public Integer deleteImportData(String transactionID) {
        if (taxReportTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return taxReportTempDomainService.deleteImportData(transactionID);
        } else if (expBankFlowTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return expBankFlowTempDomainService.deleteImportData(transactionID);
        } else {
            return null;
        }
    }

    /**
     * 确认导入的公共方法
     *
     * @param transactionID
     * @return
     */
    public Object confirmImport(String transactionID) {
        if (taxReportTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return taxReportConfirmImport(transactionID, null);
        } else if (expBankFlowTempDomainService.varifyBatchNumberExsits(transactionID)) {
            return expBankFlowService.bankFlowConfirmImport(transactionID, null);
        } else {
            return null;
        }
    }

    /**
     * 将全部通过校验没有报错的数据全部导入正式的税金申报表中
     *
     * @param transactionID
     * @param page
     * @return
     */
    public boolean taxReportConfirmImport(String transactionID, Page page) {
        ImportResultDTO importResultDTO = taxReportTempDomainService.queryResultInfo(transactionID);
        boolean flag = false;
        if (page == null) {
            page = PageUtil.getPage(0, 30);
        }
        //当错误结果为零/空的时候才可正式导入到正式表中
        if(null != importResultDTO){
            if(importResultDTO.getFailureEntities()==0 && importResultDTO.getErrorData().size()==0) {
                List<ExpTaxReportTempDomain> expTaxReportTempDomainList = taxReportTempDomainService.listImportMessageByTransactionID(transactionID, page);
                List<ExpTaxReport> expTaxReportList = expTaxReportTempDomainList.stream().map(expTaxReportTempDomain -> {
                    ExpTaxReport expTaxReport = new ExpTaxReport();
                    //将公司code的公司名称映射到数据表中
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getCompanyCode())) {
                        Company companyone = new Company();
                        companyone.setCompanyCode(expTaxReportTempDomain.getCompanyCode());
                        Company company = companyMapper.selectOne(companyone);
                        if (StringUtils.isNotEmpty(company.getName())) {
                            expTaxReport.setCompanyName(company.getName());
                            expTaxReport.setCompanyId(company.getId());
                        }
                    }
                    //转化税种code和name--暂时将name也加入到数据表中，先不存放code
                    expTaxReport.setTaxCategoryName(expTaxReportTempDomain.getTaxCategoryName());
                    //申报期间映射
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getRequestPeriod())) {
                        expTaxReport.setRequestPeriod(expTaxReportTempDomain.getRequestPeriod());
                    }

                    String companyCode = expTaxReportTempDomain.getCompanyCode();
                    String taxCategoryName = expTaxReportTempDomain.getTaxCategoryName();
                    String requestPeroid = expTaxReportTempDomain.getRequestPeriod();
                    String requestAmountStr = expTaxReportTempDomain.getRequestAmount();
                    CompanyCO companyCO = companyService.getByCompanyCode(companyCode);
                    Long companyId = null;
                    if (companyCO != null) {
                        companyId = companyCO.getId();
                    }
                    if (StringUtils.isNotEmpty(companyCode) && StringUtils.isNotEmpty(taxCategoryName) && StringUtils.isNotEmpty(requestPeroid)) {
                        //首先清空OU+期间+未勾兑
                        //其次根据导入数据的OU+期间+税种查询系统中是否已经存在“已勾兑”状态的相同数据
                        Wrapper wrapper1 = new EntityWrapper<ExpTaxReport>()
                                .eq(companyId != null, "company_id", companyId)
                                .ge(requestPeroid != null, "request_period", requestPeroid)
                                .eq("blend_status", 1);
                        expTaxReportMapper.delete(wrapper1);
                        List<ExpTaxReport> taxReportList = this.selectList(wrapper1);
                        BigDecimal sumAmount = BigDecimal.ZERO;
                        //如果存在则计算该条导入数据与原数据的申报金额的差值（如果查到多条，则计算累差），并生成一条新的申报资料
                        if (taxReportList.size() > 0) {
                            for (ExpTaxReport taxReport : taxReportList) {
                                sumAmount = sumAmount.add(taxReport.getRequestAmount());
                            }
                            BigDecimal value = sumAmount.subtract(new BigDecimal(requestAmountStr));
                            if (value.doubleValue() > 0) {
                                //申报金额映射
                                expTaxReport.setRequestAmount(value);
                            }
                        } else {
                            expTaxReport.setRequestAmount(new BigDecimal(requestAmountStr));
                        }

                    }
                    //会计科目映射
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getBusinessSubcategoryName())) {
                        expTaxReport.setBusinessSubcategoryName(expTaxReportTempDomain.getBusinessSubcategoryName());
                    }
                    //映射预算部门
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getBudgetDepartmentCode())) {
                        Department departmentone = new Department();
                        departmentone.setDepartmentCode(expTaxReportTempDomain.getBudgetDepartmentCode());
                        Department department = departmentMapper.selectOne(departmentone);
                        if (StringUtils.isNotEmpty(department.getName())) {
                            expTaxReport.setBudgetDepartmentId(department.getId());
                        }
                    }
                    //映射受益部门
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getBenefitDepartmentCode())) {
                        Department departmentone = new Department();
                        departmentone.setDepartmentCode(expTaxReportTempDomain.getBenefitDepartmentCode());
                        Department department = departmentMapper.selectOne(departmentone);
                        if (StringUtils.isNotEmpty(department.getName())) {
                            expTaxReport.setBenefitDepartmentId(department.getId());
                        }
                    }
                    //科目代码
                    if (StringUtils.isNotEmpty(expTaxReportTempDomain.getBusinessSubcategoryCode())) {
                        expTaxReport.setBusinessSubcategoryCode(expTaxReportTempDomain.getBusinessSubcategoryCode());
                    }

                    //国内税金申报-币种代码默认为CNY
                    expTaxReport.setCurrencyCode("CNY");
                    //勾兑状态默认为未勾兑-false-0
                    expTaxReport.setBlendStatus(false);
                    //报账状态默认为未勾兑-false-0
                    expTaxReport.setStatus(false);
                    return expTaxReport;
                }).collect(Collectors.toList());
                System.out.println("===expTaxReportList==" + expTaxReportList.size());
                //导入之前到清空未勾兑的数据
                flag = this.insertBatch(expTaxReportList);

                //导入完毕后删除临时表的数据
                if (flag) {
                    taxReportTempDomainService.deleteImportData(transactionID);
                }
            }
        }
        return flag;
    }


}
