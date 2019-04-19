package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.ContactCard;
import com.hand.hcf.app.mdata.contact.domain.ContactCardTempDomain;
import com.hand.hcf.app.mdata.contact.enums.ContactCardImportCode;
import com.hand.hcf.app.mdata.contact.persistence.ContactCardTempMapper;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
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
public class ContactCardImportService extends BaseService<ContactCardTempMapper,ContactCardTempDomain> {


    @Autowired
    ContactCardService contactCardService;

    @Autowired
    ContactService contactService;

    @Autowired
    ExcelImportService excelImportService;

    @Autowired
    HcfOrganizationInterface hcfOrganizationInterface;
    /**
     * 获取导入用户数据的模板
     *
     * @return
     */
    public byte[] exportContactCardImportTemplate() {
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            inputStream = StreamUtil.getResourceStream(ContactCardImportCode.TEMPLATE_PATH);
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
        ExcelImportHandler<ContactCardTempDomain> excelImportHandler = new ExcelImportHandler<ContactCardTempDomain>() {
            @Override
            public void clearHistoryData() {
                ContactCardImportService.this.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return ContactCardTempDomain.class;
            }

            @Override
            public List<ContactCardTempDomain> persistence(List<ContactCardTempDomain> list) {
                // 导入数据
                ContactCardImportService.this.insertBatch(list);
                // 数据唯一性校验
                ContactCardImportService.this.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<ContactCardTempDomain> list) {
                ContactCardImportService.this.checkImportData(list, batchNumber.toString());
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }

    private void checkImportData(List<ContactCardTempDomain> list, String batchNumber) {
        list.stream().forEach(item -> item.setErrorDetail(""));
        // 必输字段非空校验
        list.stream().filter(item -> StringUtil.isNullOrEmpty(item.getEmployeeId())
                || StringUtil.isNullOrEmpty(item.getLastName())
                || StringUtil.isNullOrEmpty(item.getCardTypeCode())
                || StringUtil.isNullOrEmpty(item.getPrimaryStr())
                || StringUtil.isNullOrEmpty(item.getEnabledStr()))
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空！");
                });
        // 数据合法性校验
        for(ContactCardTempDomain tempDomain : list) {
            UUID userOid = null;
            //工号合法校验
            String employeeId = tempDomain.getEmployeeId();
            if(!StringUtil.isNullOrEmpty(employeeId)){
                userOid = baseMapper.selectUserOidByEmployeeIdAndTenantId(employeeId, OrgInformationUtil.getCurrentTenantId());
                if(userOid != null){
                    tempDomain.setUserOid(userOid);
                }else {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "工号对应员工不存在！");
                }
            }
            String firstName=tempDomain.getFirstName();
            if(StringUtil.isNullOrEmpty(firstName)) {
                tempDomain.setLastName("");
            }
            //国籍校验
            String nationality=tempDomain.getNationalityCode();
            if(!StringUtil.isNullOrEmpty(nationality)){
                SysCodeValueCO countryCodeDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.NATIONALITY.getId().toString(),nationality);
                if(countryCodeDTO != null){
                    tempDomain.setNationality(countryCodeDTO.getValue());
                }else{
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail()+ "国籍代码不存在!");
                }
            }
            //证件类型代码合法性校验
            String typeCode = tempDomain.getCardTypeCode();

            if(!StringUtil.isNullOrEmpty(typeCode)){
                SysCodeValueCO typeCodeDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.CERTIFICATETYPE.getId().toString(),typeCode);
                if(typeCodeDTO != null){
                    tempDomain.setCardType(Integer.valueOf(typeCodeDTO.getValue()));
                    if(contactCardService.findByUserOidAndCardType(userOid,tempDomain.getCardType()).isPresent()){
                        tempDomain.setErrorFlag(true);
                        tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "该用户下存在相同类型证件！");
                    }
                }else {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "证件类型填写有误！");
                }
            }
            //证件号唯一校验
            String cardNo = tempDomain.getCardNo();
            if(!StringUtil.isNullOrEmpty(cardNo)){
                cardNo = UserInfoEncryptUtil.encrypt(cardNo);
                tempDomain.setCardNo(cardNo);
            }
            if(!StringUtil.isNullOrEmpty(cardNo) && tempDomain.getCardType()!=null){
                if (!contactCardService.checkCardNoExist(cardNo, tempDomain.getCardType())) {
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "存在相同证件号！");
                }
            }
            //证件到期日期校验
            String cardExpiredTimeStr = tempDomain.getCardExpiredTimeStr();
            if(!StringUtil.isNullOrEmpty(cardExpiredTimeStr)){
                String t = cardExpiredTimeStr.replace("/","-");
                try {
                    ZonedDateTime cardExpiredTime = TypeConversionUtils.getStartTimeForDayYYMMDD(t);
                    tempDomain.setCardExpiredTime(cardExpiredTime);
                }catch (Exception e){
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "证件到期日期格式有误!");
                }

            }
            //启用状态
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
            //是否已存在证件
            if(userOid != null){
                List<ContactCard> contactCards = contactCardService.listContactCardsByUserOid(userOid);
                if(contactCards.size()>0){
                    tempDomain.setHasPrimary(true);
                }else{
                    tempDomain.setHasPrimary(false);
                }
            }
            //是否默认
            String primaryStr = tempDomain.getPrimaryStr();
            if(!StringUtil.isNullOrEmpty(primaryStr)){
                if("Y".equals(primaryStr)){
                    if(tempDomain.isHasPrimary()){
                        tempDomain.setErrorFlag(true);
                        tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "该员工下已有默认证件!");
                    }
                    tempDomain.setPrimaryFlag(true);
                }else if("N".equals(primaryStr)){
                    tempDomain.setPrimaryFlag(false);
                }else{
                    tempDomain.setErrorFlag(true);
                    tempDomain.setErrorDetail(tempDomain.getErrorDetail() + "是否默认输入有误!");
                }
            }
            tempDomain.setContactCardOid(UUID.randomUUID());
            tempDomain.setBatchNumber(batchNumber);
        }
    }

    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<ContactCardTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    public void updateExists(String batchNumber) {
        baseMapper.updateCardNoExists(batchNumber);
        List<ContactCardTempDomain> list = selectList(new EntityWrapper<ContactCardTempDomain>()
                .eq("batch_number",batchNumber)
                .eq("has_primary",false)
                .eq("error_flag",false));
        Map<UUID,List<ContactCardTempDomain>> map = list.stream().collect(Collectors.groupingBy(ContactCardTempDomain::getUserOid));
        map.forEach((u,list1) ->{
            boolean exists = false;
            Integer typeExists = 0;
            for(ContactCardTempDomain item:list1){
                if(exists){
                    if(item.isPrimaryFlag()) {
                        item.setErrorFlag(true);
                        item.setErrorDetail(item.getErrorDetail() + "该员工下已有默认证件!");
                        updateById(item);
                    }
                }else {
                    if(item.isPrimaryFlag()){
                        exists = true;
                    }
                }
                if(typeExists.equals(item.getCardType())){
                    if(!item.getErrorFlag()) {
                        item.setErrorFlag(true);
                        item.setErrorDetail(item.getErrorDetail() + "该员工下已有相同类型证件!");
                        updateById(item);
                    }
                }else{
                    typeExists = item.getCardType();
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
        return baseMapper.delete(new EntityWrapper<ContactCardTempDomain>().eq("batch_number", transactionID));
    }

    public boolean confirmImport(String transactionID) {
        baseMapper.confirmImport(transactionID,OrgInformationUtil.getCurrentUserId(),ZonedDateTime.now());
        return true;
    }

    public Boolean varifyBatchNumberExists(String transactionID){
        return baseMapper.varifyBatchNumberExists(transactionID);
    }

    public byte[] exportFailedData(String transactionID) {
        List<ContactCardTempDomain> contactBankAccountTempDomains = selectList(
                new EntityWrapper<ContactCardTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(ContactCardImportCode.IMPORT_ERROR_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = ContactCardImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ContactCardTempDomain bankAccountTempDomain : contactBankAccountTempDomains) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(ContactCardImportCode.ROW_NUMBER_ERROR);
                cell.setCellValue(bankAccountTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(ContactCardImportCode.EMPLOYEE_ID_ERROR);
                cell.setCellValue(bankAccountTempDomain.getEmployeeId());
                cell = row.createCell(ContactCardImportCode.FIRST_NAME_ERROR);
                cell.setCellValue(bankAccountTempDomain.getFirstName());
                cell = row.createCell(ContactCardImportCode.LAST_NAME_ERROR);
                cell.setCellValue(bankAccountTempDomain.getLastName());
                cell = row.createCell(ContactCardImportCode.NATIONALITY_ERROR);
                cell.setCellValue(bankAccountTempDomain.getNationalityCode());
                cell = row.createCell(ContactCardImportCode.CARD_TYPE_CODE_ERROR);
                cell.setCellValue(bankAccountTempDomain.getCardTypeCode());
                cell = row.createCell(ContactCardImportCode.CARD_NO_ERROR);
                cell.setCellValue(UserInfoEncryptUtil.detrypt(bankAccountTempDomain.getCardNo()));
                cell = row.createCell(ContactCardImportCode.CARD_EXPIRED_TIME_ERROR);
                cell.setCellValue(bankAccountTempDomain.getCardExpiredTimeStr());
                cell = row.createCell(ContactCardImportCode.ENABLED_STR_ERROR);
                cell.setCellValue(bankAccountTempDomain.getEnabledStr());
                cell = row.createCell(ContactCardImportCode.PRIMARY_STR_ERROR);
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
