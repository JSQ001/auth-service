package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.contact.domain.UserTempDomain;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountImportDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactCardImportDTO;
import com.hand.hcf.app.mdata.contact.enums.UserImportCode;
import com.hand.hcf.app.mdata.contact.persistence.UserTempMapper;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.web.dto.ImportResultDTO;
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
public class UserImportService extends BaseService<UserTempMapper, UserTempDomain> {

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

        List<UserTempDomain> userTempDomains = selectList(
                new EntityWrapper<UserTempDomain>()
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
            for (UserTempDomain userTempDomain : userTempDomains) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(UserImportCode.ROW_NUMBER_ERROR);
                cell.setCellValue(userTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(UserImportCode.EMPLOYEE_ID_ERROR);
                cell.setCellValue(userTempDomain.getEmployeeId());
                cell = row.createCell(UserImportCode.FULL_NAME_ERROR);
                cell.setCellValue(userTempDomain.getFullName());
                cell = row.createCell(UserImportCode.COMPANY_CODE_ERROR);
                cell.setCellValue(userTempDomain.getCompanyCode());
                cell = row.createCell(UserImportCode.DEPARTMENT_CODE_ERROR);
                cell.setCellValue(userTempDomain.getDepartmentCode());
                cell = row.createCell(UserImportCode.EMAIL_ERROR);
                cell.setCellValue(userTempDomain.getEmail());
                cell = row.createCell(UserImportCode.MOBILE_AREA_CODE_ERROR);
                cell.setCellValue(userTempDomain.getMobileAreaCode());
                cell = row.createCell(UserImportCode.MOBILE_ERROR);
                cell.setCellValue(userTempDomain.getMobile());
                cell = row.createCell(UserImportCode.DIRECT_MANAGER_ERROR);
                cell.setCellValue(userTempDomain.getDirectManagerId());
                cell = row.createCell(UserImportCode.DUTY_CODE_ERROR);
                cell.setCellValue(userTempDomain.getDutyCode());
                cell = row.createCell(UserImportCode.TITLE_ERROR);
                cell.setCellValue(userTempDomain.getTitle());
                cell = row.createCell(UserImportCode.EMPLOYEE_TYPE_CODE_ERROR);
                cell.setCellValue(userTempDomain.getEmployeeTypeCode());
                cell = row.createCell(UserImportCode.RANK_CODE_ERROR);
                cell.setCellValue(userTempDomain.getRankCode());
                cell = row.createCell(UserImportCode.GENDER_CODE_ERROR);
                cell.setCellValue(userTempDomain.getGenderCode());
                cell = row.createCell(UserImportCode.BIRTHDAY_STR);
                cell.setCellValue(userTempDomain.getBirthdayStr());
                cell = row.createCell(UserImportCode.ENTRYDATE_STR);
                cell.setCellValue(userTempDomain.getEntryDateStr());
                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(userTempDomain.getErrorDetail());
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
        baseMapper.delete(new EntityWrapper<UserTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
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
        return baseMapper.delete(new EntityWrapper<UserTempDomain>().eq("batch_number", transactionID));
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
//        baseMapper.delete(new EntityWrapper<UserTempDomain>().eq("batch_number",transactionID));
//        return true;
//    }

    public List<UserTempDomain> listImportMessageByTransactionID(String transactionID,Page page){
        return baseMapper.selectPage(page,new EntityWrapper<UserTempDomain>().eq("batch_number",transactionID).eq("error_flag",false));
    }

    @Transactional
    public void deleteImportMessageByTransactionID(String transactionID){
        baseMapper.delete(new EntityWrapper<UserTempDomain>().eq("batch_number",transactionID));
    }

    public Boolean varifyBatchNumberExsits(String transactionID){
        return baseMapper.varifyBatchNumberExsits(transactionID);
    }
}
