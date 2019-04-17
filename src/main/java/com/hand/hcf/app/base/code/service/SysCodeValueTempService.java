package com.hand.hcf.app.base.code.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.code.domain.SysCodeValueTemp;
import com.hand.hcf.app.base.code.persistence.SysCodeValueTempMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/26
 */
@Service
@Slf4j
public class SysCodeValueTempService extends BaseService<SysCodeValueTempMapper, SysCodeValueTemp> {

    @Transactional(rollbackFor = Exception.class)
    public void checkData(UUID batchNumber, Long customEnumerationId) {
        baseMapper.checkData(batchNumber.toString(),customEnumerationId);
    }

    public ImportResultDTO queryImportResultInfo(String transactionUUID) {
        return baseMapper.queryImportResultInfo(transactionUUID);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemp(String transactionUUID) {
        this.delete(new EntityWrapper<SysCodeValueTemp>().eq("batch_number", transactionUUID));
        return true;
    }

    public byte[] exportFailedData(String path, UUID transactionUUID) {
        List<SysCodeValueTemp> writeErrorData = this.selectList(
                new EntityWrapper<SysCodeValueTemp>()
                        .eq("batch_number", transactionUUID)
                        .eq("error_flag", 1)
                        .orderBy("row_number",true));
        //  创建流
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            //  获取文件流
            inputStream = StreamUtil.getResourceStream(path);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            //  获取sheet页
            XSSFSheet sheet = workbook.getSheetAt(0);

            // code 行
            XSSFRow codeRow = sheet.getRow(1);
            if (CollectionUtils.isNotEmpty(writeErrorData)){
                int lastCellNum = codeRow.getLastCellNum();
                Row row = null;
                Cell cell = null;
                int startRow = 2;
                for (SysCodeValueTemp importDTO : writeErrorData) {
                    row = sheet.createRow(++startRow);
                    for (int i = 0; i < lastCellNum; i++){
                        cell = row.createCell(i, CellType.STRING);
                        // 取属性
                        XSSFCell codeRowCell = codeRow.getCell(i);
                        cell.setCellValue(getErrorValue(importDTO, codeRowCell.getStringCellValue()));
                    }
                    // 输出错误信息
                    cell = row.createCell(lastCellNum, CellType.STRING);
                    cell.setCellValue(importDTO.getErrorDetail());
                }
            }
            //  创建输出流
            bos = new ByteArrayOutputStream();
            //  写入流
            workbook.write(bos);
            //  输出
            bos.flush();
            //  关闭资源
            workbook.close();
            //  返回
            return bos.toByteArray();

        }catch (Exception e){
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }finally {
            //  关闭资源
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (bos != null){
                    bos.close();
                }
            }catch (IOException e){
                log.error(e.getMessage());
            }
        }
    }

    private String getErrorValue(SysCodeValueTemp temp, String fieldName){
        Field field = ReflectionUtils.findField(SysCodeValueTemp.class, fieldName);
        if (null == field){
            return "";
        }
        field.setAccessible(true);
        try {
            Object o = field.get(temp);
            if (o == null){
                return "";
            }else{
                return o.toString();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }
}
