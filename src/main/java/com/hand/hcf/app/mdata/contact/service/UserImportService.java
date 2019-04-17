package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.contact.domain.MdataUserTempDomain;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountImportDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactCardImportDTO;
import com.hand.hcf.app.mdata.contact.enums.UserImportCode;
import com.hand.hcf.app.mdata.contact.persistence.MdataUserTempMapper;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.itextpdf.text.io.StreamUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author yuan.liu
 *
 *
 */
@Service
public class UserImportService extends BaseService<MdataUserTempMapper, MdataUserTempDomain> {

    public static final Map<UUID, BatchTransactionLog> transactions = new HashMap<>();
    public static final Map<UUID, List<ContactCardImportDTO>> failedContactCards = new HashMap<>();
    public static final Map<UUID, List<ContactBankAccountImportDTO>> failedContactBankAccounts = new HashMap<>();


    /**
     * 获取导入用户数据的模板
     *
     * @return
     */
    public byte[] exportUserInfoImportTemplate() {
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            inputStream = StreamUtil.getResourceStream(UserImportCode.TEMPLATE_PATH);
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

    //导入失败后,导出错误信息
    public byte[] exportFailedData(String transactionID) {

        List<MdataUserTempDomain> mdataUserTempDomains = selectList(
                new EntityWrapper<MdataUserTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(UserImportCode.IMPORT_ERROR_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = UserImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (MdataUserTempDomain mdataUserTempDomain : mdataUserTempDomains) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(UserImportCode.ROW_NUMBER_ERROR);
                cell.setCellValue(mdataUserTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(UserImportCode.EMPLOYEE_ID_ERROR);
                cell.setCellValue(mdataUserTempDomain.getEmployeeId());
                cell = row.createCell(UserImportCode.FULL_NAME_ERROR);
                cell.setCellValue(mdataUserTempDomain.getFullName());
                cell = row.createCell(UserImportCode.COMPANY_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getCompanyCode());
                cell = row.createCell(UserImportCode.DEPARTMENT_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getDepartmentCode());
                cell = row.createCell(UserImportCode.EMAIL_ERROR);
                cell.setCellValue(mdataUserTempDomain.getEmail());
                cell = row.createCell(UserImportCode.MOBILE_AREA_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getMobileAreaCode());
                cell = row.createCell(UserImportCode.MOBILE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getMobile());
                cell = row.createCell(UserImportCode.DIRECT_MANAGER_ERROR);
                cell.setCellValue(mdataUserTempDomain.getDirectManagerId());
                cell = row.createCell(UserImportCode.DUTY_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getDutyCode());
                cell = row.createCell(UserImportCode.TITLE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getTitle());
                cell = row.createCell(UserImportCode.EMPLOYEE_TYPE_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getEmployeeTypeCode());
                cell = row.createCell(UserImportCode.RANK_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getRankCode());
                cell = row.createCell(UserImportCode.GENDER_CODE_ERROR);
                cell.setCellValue(mdataUserTempDomain.getGenderCode());
                cell = row.createCell(UserImportCode.BIRTHDAY_STR);
                cell.setCellValue(mdataUserTempDomain.getBirthdayStr());
                cell = row.createCell(UserImportCode.ENTRYDATE_STR);
                cell.setCellValue(mdataUserTempDomain.getEntryDateStr());
                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(mdataUserTempDomain.getErrorDetail());
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


    /**
     * 删除两天以前的数据
     */
    @Transactional
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<MdataUserTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    /**
     * 校验唯一性
     */
    public void updateExists(String batchNumber) {
        baseMapper.updateEmployeeIdExists(batchNumber);
        baseMapper.updateEmailExists(batchNumber);
        baseMapper.updatePhoneExists(batchNumber);
    }

    public ImportResultDTO queryResultInfo(String transactionID) {
        return baseMapper.queryInfo(transactionID);
    }

    @Transactional
    public Integer deleteImportData(String transactionID) {
        return baseMapper.delete(new EntityWrapper<MdataUserTempDomain>().eq("batch_number", transactionID));
    }

    public UUID getUserOidByEmployeeIdAndTenantId(String employeeId){
        return baseMapper.selectUserOidByEmployeeIdAndTenantId(employeeId);
    }

//    @Transactional
//    public boolean confirmImport(String transactionID) {
//        baseMapper.insertToContact(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now());
//        baseMapper.insertToUser(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now(),OrgInformationUtil.getCurrentTenantId());
//        baseMapper.insertToPhone(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now());
//        baseMapper.insertToDepartmentUser(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now());
//        baseMapper.delete(new EntityWrapper<MdataUserTempDomain>().eq("batch_number",transactionID));
//        return true;
//    }

    public List<MdataUserTempDomain> listImportMessageByTransactionID(String transactionID, Page page){
        return baseMapper.selectPage(page,new EntityWrapper<MdataUserTempDomain>().eq("batch_number",transactionID).eq("error_flag",false));
    }

    @Transactional
    public void deleteImportMessageByTransactionID(String transactionID){
        baseMapper.delete(new EntityWrapper<MdataUserTempDomain>().eq("batch_number",transactionID));
    }

    public Boolean varifyBatchNumberExsits(String transactionID){
        return baseMapper.varifyBatchNumberExsits(transactionID);
    }
}
