package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import com.hand.hcf.app.mdata.bank.service.BankInfoService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccountTempDomain;
import com.hand.hcf.app.mdata.contact.enums.ContactBankAccountImportCode;
import com.hand.hcf.app.mdata.contact.persistence.ContactBankAccountTempMapper;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.itextpdf.text.io.StreamUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactBankAccountImportService extends BaseService<ContactBankAccountTempMapper, ContactBankAccountTempDomain> {

    @Autowired
    ContactService contactService;

    @Autowired
    ContactBankAccountService contactBankAccountService;
    
    @Autowired
    ExcelImportService excelImportService;

    @Autowired
    BankInfoService bankInfoService;

    /**
     * 获取导入用户数据的模板
     *
     * @return
     */
    public byte[] exportContactBankAccountImportTemplate() {
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            inputStream = StreamUtil.getResourceStream(ContactBankAccountImportCode.TEMPLATE_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException("");
        }
    }

    public UUID importUserInfo(MultipartFile file) throws Exception{
        UUID batchNumber = UUID.randomUUID();
        InputStream in = file.getInputStream();
        ExcelImportHandler<ContactBankAccountTempDomain> excelImportHandler = new ExcelImportHandler<ContactBankAccountTempDomain>() {
            @Override
            public void clearHistoryData() {
                ContactBankAccountImportService.this.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return ContactBankAccountTempDomain.class;
            }

            @Override
            public List<ContactBankAccountTempDomain> persistence(List<ContactBankAccountTempDomain> list) {
                // 导入数据
                ContactBankAccountImportService.this.insertBatch(list);
                // 数据唯一性校验
                ContactBankAccountImportService.this.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<ContactBankAccountTempDomain> list) {
                ContactBankAccountImportService.this.checkImportData(list, batchNumber.toString());
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }

    private void checkImportData(List<ContactBankAccountTempDomain> list, String batchNumber) {
        list.stream().forEach(item -> item.setErrorDetail(""));
        // 必输字段非空校验
        list.stream().filter(item -> StringUtil.isNullOrEmpty(item.getEmployeeId())
                || StringUtil.isNullOrEmpty(item.getBankAccountName())
                || StringUtil.isNullOrEmpty(item.getBankAccountNo())
                || StringUtil.isNullOrEmpty(item.getBranchName())
                || StringUtil.isNullOrEmpty(item.getPrimaryStr())
                || StringUtil.isNullOrEmpty(item.getEnabledStr()))
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空！");
                });
        // 数据合法性校验
        for(ContactBankAccountTempDomain tempDomain : list) {
            UUID userOid = null;
            //工号合法校验
            String employeeId = tempDomain.getEmployeeId();
            if(!StringUtil.isNullOrEmpty(employeeId)){
                userOid = baseMapper.selectUserOidByEmployeeIdAndTenantId(employeeId,OrgInformationUtil.getCurrentTenantId());
                if(userOid != null){
                    tempDomain.setUserOid(userOid);
                }else {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "工号对应员工不存在！");
                }
            }
            //银行卡号需唯一性校验
            String bankAccountNo = tempDomain.getBankAccountNo();
            if(!StringUtil.isNullOrEmpty(bankAccountNo)){
                bankAccountNo = UserInfoEncryptUtil.encrypt(bankAccountNo);
                tempDomain.setBankAccountNo(bankAccountNo);
                if (!contactBankAccountService.checkBankCardNoExists(bankAccountNo)) {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "该银行卡号已被占用！");
                }
            }
            //银行名称合法性校验
            String branchName = tempDomain.getBranchName();
            if(!StringUtil.isNullOrEmpty(branchName)){
                // 判断系统银行是否存在此分行名称、如果存在则用系统银行、否则查询自定义银行是否存在此分行名称
                BankInfo bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(0L, branchName);
                if (bankInfo == null) {
                    bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(OrgInformationUtil.getCurrentTenantId(), branchName);
                }
                if(bankInfo != null){
                    tempDomain.setBankName(bankInfo.getBankName());
                    tempDomain.setBankCode(bankInfo.getBankCode());
                }else {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "银行不存在！");
                }
            }
            String enabledStr = tempDomain.getEnabledStr();
            if(!StringUtil.isNullOrEmpty(enabledStr)){
                if("Y".equals(enabledStr)){
                    tempDomain.setEnabled(true);
                }else if("N".equals(enabledStr)){
                    tempDomain.setEnabled(false);
                }else{
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "是否启用输入有误!");
                }
            }
            String primaryStr = tempDomain.getPrimaryStr();
            if(userOid != null){
                List<ContactBankAccount> accounts = contactBankAccountService.findByUserOid(userOid);
                if(accounts.size()>0){
                    tempDomain.setHasPrimary(true);
                }else{
                    tempDomain.setHasPrimary(false);
                }
            }
            if(!StringUtil.isNullOrEmpty(primaryStr)){
                if("Y".equals(primaryStr)){
                    if(tempDomain.isHasPrimary()){
                        tempDomain.setErrorFlag(true);
                        tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "该员工下已有默认银行卡!");
                    }
                    tempDomain.setPrimaryFlag(true);
                }else if("N".equals(primaryStr)){
                    tempDomain.setPrimaryFlag(false);
                }else{
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "是否默认输入有误!");
                }
            }
            tempDomain.setContactBankAccountOid(UUID.randomUUID());
            tempDomain.setBatchNumber(batchNumber);
        }
    }

    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<ContactBankAccountTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    public void updateExists(String batchNumber) {
        baseMapper.updateBankAccountNoExists(batchNumber);
        List<ContactBankAccountTempDomain> list = selectList(new EntityWrapper<ContactBankAccountTempDomain>()
                .eq("batch_number",batchNumber)
                .eq("has_primary",false)
                .eq("error_flag",false));
        Map<UUID,List<ContactBankAccountTempDomain>> map = list.stream().collect(Collectors.groupingBy(ContactBankAccountTempDomain::getUserOid));
        map.forEach((u,list1) ->{
            boolean exists = false;
            for(ContactBankAccountTempDomain item:list1){
                if(exists && item.isPrimaryFlag()){
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "该员工下已有默认银行卡!");
                    updateById(item);
                }
                if(item.isPrimaryFlag()){
                    exists = true;
                }
            }
            if(!exists){
                list1.get(0).setPrimaryFlag(true);
                updateById(list1.get(0));
            }
        });
    }

    public ImportResultDTO queryResultInfo(String transactionID) {
        return baseMapper.queryInfo(transactionID);
    }

    public Integer deleteImportData(String transactionID) {
        return baseMapper.delete(new EntityWrapper<ContactBankAccountTempDomain>().eq("batch_number", transactionID));
    }

    public boolean confirmImport(String transactionID) {
        baseMapper.confirmImport(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now());
        return true;
    }

    public Boolean varifyBatchNumberExsits(String transactionID){
        return baseMapper.varifyBatchNumberExsits(transactionID);
    }

    public byte[] exportFailedData(String transactionID) {
        List<ContactBankAccountTempDomain> contactBankAccountTempDomains = selectList(
                new EntityWrapper<ContactBankAccountTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(ContactBankAccountImportCode.IMPORT_ERROR_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = ContactBankAccountImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ContactBankAccountTempDomain bankAccountTempDomain : contactBankAccountTempDomains) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(ContactBankAccountImportCode.ROW_NUMBER_ERROR);
                cell.setCellValue(bankAccountTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(ContactBankAccountImportCode.EMPLOYEE_ID_ERROR);
                cell.setCellValue(bankAccountTempDomain.getEmployeeId());
                cell = row.createCell(ContactBankAccountImportCode.BANK_ACCOUNT_NAME_ERROR);
                cell.setCellValue(bankAccountTempDomain.getBankAccountName());
                cell = row.createCell(ContactBankAccountImportCode.BANK_ACCOUNT_NO_ERROR);
                cell.setCellValue(UserInfoEncryptUtil.detrypt(bankAccountTempDomain.getBankAccountNo()));
                cell = row.createCell(ContactBankAccountImportCode.ACCOUNT_LOCATION_ERROR);
                cell.setCellValue(bankAccountTempDomain.getAccountLocation());
                cell = row.createCell(ContactBankAccountImportCode.BRANCH_NAME_ERROR);
                cell.setCellValue(bankAccountTempDomain.getBranchName());
                cell = row.createCell(ContactBankAccountImportCode.ENABLED_STR_ERROR);
                cell.setCellValue(bankAccountTempDomain.getEnabledStr());
                cell = row.createCell(ContactBankAccountImportCode.PRIMARY_STR_ERROR);
                cell.setCellValue(bankAccountTempDomain.getPrimaryStr());
                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(bankAccountTempDomain.getErrorDetail());
            }
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }
}
