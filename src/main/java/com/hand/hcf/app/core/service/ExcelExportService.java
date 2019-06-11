package com.hand.hcf.app.core.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.annotation.ExcelDomainField;
import com.hand.hcf.app.core.domain.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.ReflectionUtil;
import com.hand.hcf.app.core.util.RespCode;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * excel 导出
 */
@Service
public class ExcelExportService {

    private static final String ENC = "UTF-8";
    private static final String EXCEL2007 = "xlsx";


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    /**
     * 通过sqlID查询
     * @param exportInfo
     * @param outputStream
     * @param rowMaxNumber
     * @param dtoService
     * @throws Exception
     */
    private void exportExcel2007(ExportConfigBySQLId exportInfo, OutputStream outputStream, int rowMaxNumber, Function<Object, Object> dtoService)
            throws Exception {
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        final Sheet[] sheet = {wb.createSheet()};
        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportInfo.getClazz());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            sqlSession.select(exportInfo.getSqlId(), exportInfo.getParam(), (resultContext -> {
                Object object = dtoService.apply(resultContext.getResultObject());
                sheet[0] = createSheet(wb, sheet[0], object, count, rowIndex, rowMaxNumber, exportInfo,excelHeaderColumn.getQueryColumn(), styles);
            }));
            wb.write(outputStream);
        } finally {
            wb.close();
            wb.dispose();
        }
    }


    /**
     * 通过sqlID查询
     * @param exportInfo
     * @param outputStream
     * @param rowMaxNumber
     * @param dtoService
     * @throws Exception
     */
    private void exportExcel2003(ExportConfigBySQLId exportInfo, OutputStream outputStream, int rowMaxNumber, Function<Object, Object> dtoService)
            throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        final Sheet[] sheet = {wb.createSheet()};
        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportInfo.getClazz());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            sqlSession.select(exportInfo.getSqlId(), exportInfo.getParam(), (resultContext -> {
                Object object = dtoService.apply(resultContext.getResultObject());
                sheet[0] = createSheet(wb, sheet[0], object, count, rowIndex, rowMaxNumber, exportInfo,excelHeaderColumn.getQueryColumn(), styles);
            }));
            wb.write(outputStream);
        } finally {
            wb.close();
        }
    }


    /**
     * 通过查询出来的总集合查询
     * @param exportInfo
     * @param outputStream
     * @param rowMaxNumber
     * @throws Exception
     */
    private void exportExcel2007(ExportConfigByList exportInfo, OutputStream outputStream, int rowMaxNumber)
            throws Exception {
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        final Sheet[] sheet = {wb.createSheet()};
        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportInfo.getClazz());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());

        try {
            exportInfo.getListDTO().stream().forEach(object -> {
                sheet[0] = createSheet(wb, sheet[0], object, count, rowIndex, rowMaxNumber, exportInfo,excelHeaderColumn.getQueryColumn(), styles);
            });
            wb.write(outputStream);
        } finally {
            wb.close();
            wb.dispose();
        }
    }


    /**
     * 通过查询出来的集合导出
     * @param exportInfo
     * @param outputStream
     * @param rowMaxNumber
     * @throws Exception
     */
    private void exportExcel2003(ExportConfigByList exportInfo, OutputStream outputStream, int rowMaxNumber)
            throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        final Sheet[] sheet = {wb.createSheet()};

        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportInfo.getClazz());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());
        try {
            exportInfo.getListDTO().stream().forEach(object -> {
                sheet[0] = createSheet(wb, sheet[0], object, count, rowIndex, rowMaxNumber, exportInfo, excelHeaderColumn.getQueryColumn(), styles);
            });
            wb.write(outputStream);
        } finally {
            wb.close();
        }
    }

    private <S, D> void exportExcel2007(ExportConfig exportInfo,
                                        ExcelExportHandler<S, D> exportHandler,
                                        int nThreads,
                                        OutputStream outputStream,
                                        int rowMaxNumber) throws Exception {
        long start = System.currentTimeMillis();
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        final Sheet[] sheet = {wb.createSheet()};

        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportHandler.getEntityClass());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());

        // 获取总数，和总页数
        int total = exportHandler.getTotal();
        logger.info("-------> 导出的总条数为： {}", total);
        int pageSize = exportHandler.getPageSize();
        logger.info("-------> 每页查询的条数为{}条", pageSize);
        int totalPage = (total - 1) / pageSize + 1;
        logger.info("-------> 总共需要查询{}页", totalPage);

        List<Future<List<S>>> result = new ArrayList<>();
        asyncQueryData(nThreads, exportHandler, totalPage, pageSize, result);
        try {
            getAndWriteData(wb, sheet, count, rowIndex, rowMaxNumber,exportInfo, excelHeaderColumn.getQueryColumn(), styles, result, exportHandler);
            logger.info("-------> 查询和写入文件共计{}毫秒",System.currentTimeMillis() - start);
            wb.write(outputStream);
        } finally {
            wb.close();
            wb.dispose();
        }
    }


    private <S, D> void exportExcel2003(ExportConfig exportInfo,
                                        ExcelExportHandler<S, D> exportHandler,
                                        int nThreads,
                                        OutputStream outputStream,
                                        int rowMaxNumber) throws Exception {
        long start = System.currentTimeMillis();
        HSSFWorkbook wb = new HSSFWorkbook();
        final Sheet[] sheet = {wb.createSheet()};
        // 初始化导出列属性类型
        initColumnType(exportInfo.getColumnsInfo(), exportHandler.getEntityClass());
        // 创建表头(可以是多级表头)
        ExcelHeaderColumn excelHeaderColumn = createHeaderRow(wb, sheet[0], exportInfo.getColumnsInfo());
        // 总row计数器
        final AtomicInteger count = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // sheet页row计数器
        final AtomicInteger rowIndex = new AtomicInteger(excelHeaderColumn.getHeaderDeep() + 1);
        // 创建总列数的格式
        List<CellStyle> styles = getCellStyles(wb, excelHeaderColumn.getQueryColumn());

        // 获取总数，和总页数
        int total = exportHandler.getTotal();
        logger.info("-------> 导出的总条数为： {}", total);
        int pageSize = exportHandler.getPageSize();
        logger.info("-------> 每页查询的条数为{}条", pageSize);
        int totalPage = (total - 1) / pageSize + 1;
        logger.info("-------> 总共需要查询{}页", totalPage);
        List<Future<List<S>>> result = new ArrayList<>();
        asyncQueryData(nThreads, exportHandler, totalPage, pageSize, result);
        try {
            getAndWriteData(wb, sheet, count, rowIndex, rowMaxNumber, exportInfo, excelHeaderColumn.getQueryColumn(), styles, result, exportHandler);
            logger.info("-------> 查询和写入文件共计{}毫秒",System.currentTimeMillis() - start);
            wb.write(outputStream);
        } finally {
            wb.close();
        }
    }

    private void exportAndDownloadExcel(ExportConfigBySQLId exportConfig, HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse, int rowMaxNumber, Function<Object, Object> excelToDTOService) throws IOException {
        String name;
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xlsx";
        } else {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xls";
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent.contains("Firefox")) {
            name = new String(name.getBytes(ENC), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, ENC);
        }
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + ENC);
        httpServletResponse.setHeader("Accept-Ranges", "bytes");


        try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
            if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
                exportExcel2007(exportConfig, outputStream, rowMaxNumber, excelToDTOService);
            } else {
                exportExcel2003(exportConfig, outputStream, rowMaxNumber, excelToDTOService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_SYSTEM_ERROR);
        }
    }


    /**
     *  通过sqlID导出
     *
     * @param exportConfig        导出的配置类
     * @param httpServletRequest
     * @param httpServletResponse
     * @param excelToDTOService
     * @throws IOException
     */
    public void exportAndDownloadExcel(ExportConfigBySQLId exportConfig, HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse, Function<Object, Object> excelToDTOService) throws IOException {
        if (exportConfig == null) {
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_CONFIG_IS_NULL);
        }
        if (exportConfig.getClazz() == null) {
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_GET_ENTITY_INSTANCE_FAILED);
        }
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            exportAndDownloadExcel(exportConfig, httpServletRequest, httpServletResponse, 1000000, excelToDTOService);
        } else {
            exportAndDownloadExcel(exportConfig, httpServletRequest, httpServletResponse, 65535, excelToDTOService);
        }
    }

    private void exportAndDownloadExcel(ExportConfigByList exportConfig, HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse, int rowMaxNumber) throws IOException {
        String name;
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xlsx";
        } else {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xls";
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent.contains("Firefox")) {
            name = new String(name.getBytes(ENC), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, ENC);
        }
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + ENC);
        httpServletResponse.setHeader("Accept-Ranges", "bytes");

        try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
            if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
                exportExcel2007(exportConfig, outputStream, rowMaxNumber);
            } else {
                exportExcel2003(exportConfig, outputStream, rowMaxNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_SYSTEM_ERROR);
        }
    }

    /**
     * 通过查询出来的集合导出
     *
     * @param exportConfig        导出的配置类
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws IOException
     */
    public void exportAndDownloadExcel(ExportConfigByList exportConfig, HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse) throws IOException {
        if (exportConfig == null) {
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_CONFIG_IS_NULL);
        }
        if (exportConfig.getClazz() == null) {
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_GET_ENTITY_INSTANCE_FAILED);
        }
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            exportAndDownloadExcel(exportConfig, httpServletRequest, httpServletResponse, 1000000);
        } else {
            exportAndDownloadExcel(exportConfig, httpServletRequest, httpServletResponse, 65535);
        }
    }


    private <S, D> void exportAndDownloadExcel(ExportConfig exportConfig, ExcelExportHandler<S, D> exportHandler, int nThreads, HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse, int rowMaxNumber) throws IOException {
        String name;
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xlsx";
        } else {
            name = (exportConfig.getFileName() == null ? "excel" : exportConfig.getFileName()) + ".xls";
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent.contains("Firefox")) {
            name = new String(name.getBytes(ENC), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, ENC);
        }
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + ENC);
        httpServletResponse.setHeader("Accept-Ranges", "bytes");

        try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
            if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
                exportExcel2007(exportConfig, exportHandler, nThreads, outputStream, rowMaxNumber);
            } else {
                exportExcel2003(exportConfig, exportHandler, nThreads, outputStream, rowMaxNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_SYSTEM_ERROR);
        }
    }

    /**
     *
     * 通过多线程分页查询导出
     * @param exportConfig
     * @param exportHandler
     * @param nThreads
     * @param httpServletRequest
     * @param httpServletResponse
     * @param <S>
     * @param <D>
     * @throws IOException
     */
    public <S, D> void exportAndDownloadExcel(ExportConfig exportConfig, ExcelExportHandler<S, D> exportHandler, int nThreads, HttpServletRequest httpServletRequest,
                                              HttpServletResponse httpServletResponse) throws IOException {
        if (exportConfig == null) {
            throw new BizException(RespCode.SYS_EXCEL_EXPORT_CONFIG_IS_NULL);
        }
        long start = System.currentTimeMillis();
        if (exportConfig.getExcelType() == null || EXCEL2007.equals(exportConfig.getExcelType())) {
            logger.info("-------> 开始导出excel, excel版本为2007");
            exportAndDownloadExcel(exportConfig, exportHandler, nThreads, httpServletRequest, httpServletResponse, 1000000);
        } else {
            logger.info("-------> 开始导出excel, excel版本为2003");
            exportAndDownloadExcel(exportConfig, exportHandler, nThreads, httpServletRequest, httpServletResponse, 65535);
        }
        logger.info("-------> 导出结束，共计花费：{}毫秒", System.currentTimeMillis() - start);
    }



    /**
     * 生成要导出对象的columnInfo
     *
     * @param clazz
     * @param excludes
     * @return
     */
    public List<ColumnInfo> getColumnInfosByClass(Class<?> clazz, List<String> excludes) {
        // 获取所有的属性
        List<Field> declaredFields = ReflectionUtil.getFieldList(clazz);

        if (!CollectionUtils.isEmpty(excludes)) {
            declaredFields = declaredFields.stream().filter(i -> !excludes.contains(i.getName())).collect(Collectors.toList());
        }
        List<ColumnInfo> columnInfos = new ArrayList<>();
        if (CollectionUtils.isEmpty(declaredFields)) {
            logger.info("class no  Field {}",  clazz.getName());
        }else{
            for (Field field : declaredFields) {
                ExcelDomainField excelDomainField = field.getAnnotation(ExcelDomainField.class);
                ColumnInfo columnInfo = new ColumnInfo();
                if (excelDomainField != null) {
                    columnInfo.setDataFormat(excelDomainField.dataFormat());
                    columnInfo.setAlign(excelDomainField.align());
                    columnInfo.setWidth(excelDomainField.width());
                    columnInfo.setName(field.getName());
                    columnInfo.setTitle(StringUtils.hasText(excelDomainField.title()) ? excelDomainField.title() : field.getName());
                } else {
                    columnInfo.setDataFormat(null);
                    columnInfo.setTitle(field.getName());
                    columnInfo.setAlign("left");
                    columnInfo.setWidth(80);
                    columnInfo.setName(field.getName());
                }
                columnInfos.add(columnInfo);
            }
        }

        return columnInfos;
    }


    /**
     * 多线程查询
     * @param nThreads
     * @param exportHandler
     * @param totalPage
     * @param pageSize
     * @param result
     * @param <S>
     * @param <D>
     */
    private <S, D> void asyncQueryData(int nThreads,
                                       ExcelExportHandler<S, D> exportHandler,
                                       int totalPage,
                                       int pageSize,
                                       List<Future<List<S>>> result) {
        // 创建线程池
        nThreads = nThreads > 1 ? nThreads : 1;
        // 如果线程数大于运行的cup核心数，就为cpu核心数，读的速度快，防止结果集合存放过多数据
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        nThreads = nThreads > availableProcessors ? availableProcessors : nThreads;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024));
        logger.info("-------> 创建的核心线程数为：{}", nThreads);

        for (int i = 0; i < totalPage; i++) {
            Pageable pageable = PageRequest.of(i, pageSize);
            Page page = PageUtil.getPage(pageable);
            page.setSearchCount(false);
            Future<List<S>> future = executor.submit(() -> {
                logger.info("-------> 提交查询第{}页的数据......", page.getCurrent());
                List<S> queryData = exportHandler.queryDataByPage(page);
                logger.info("-------> 查询第{}页的数据完成......", page.getCurrent());
                return queryData;
            });
            result.add(future);
        }
        executor.shutdown();
    }


    /**
     * 读取线程返回的数据并且输出到流里
     * @param wb
     * @param sheet
     * @param count
     * @param rowIndex
     * @param rowMaxNumber
     * @param exportConfig
     * @param columnInfo
     * @param styles
     * @param result
     * @param exportHandler
     * @param <S>
     * @param <D>
     */
    private <S, D> void getAndWriteData(Workbook wb, Sheet[] sheet, AtomicInteger count,
                                        AtomicInteger rowIndex, int rowMaxNumber,
                                        ExportConfig exportConfig,
                                        List<ColumnInfo> columnInfo, List<CellStyle> styles,
                                        List<Future<List<S>>> result,
                                        ExcelExportHandler<S, D> exportHandler) {
        Iterator<Future<List<S>>> iterator = result.iterator();
        // 该变量为日志记录
        int i = 1;
        while (iterator.hasNext()){
            try {
                iterator.next().get().stream().forEach(object -> {
                    D dto = exportHandler.toDTO(object);
                    sheet[0] = createSheet(wb, sheet[0], dto, count, rowIndex, rowMaxNumber, exportConfig, columnInfo, styles);
                });
                logger.info("-------> 第{}页数据写入到excel文件中完成......", i);
                i++;
                iterator.remove();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 创建一个sheet
     * @param wb
     * @param sheet
     * @param object
     * @param count
     * @param rowIndex
     * @param rowMaxNumber
     * @param exportConfig
     * @param columnInfo
     * @param styles
     * @return
     */
    private Sheet createSheet(Workbook wb, Sheet sheet, Object object, AtomicInteger count,
                              AtomicInteger rowIndex, int rowMaxNumber, ExportConfig exportConfig,
                              List<ColumnInfo> columnInfo, List<CellStyle> styles) {
        if (count.get() % rowMaxNumber == 0) {
            sheet = wb.createSheet();
            ExcelHeaderColumn headerRow = createHeaderRow(wb, sheet, exportConfig.getColumnsInfo());
            rowIndex.set(headerRow.getHeaderDeep() + 1);
            count.set(count.intValue() + headerRow.getHeaderDeep() + 1);
        }
        count.getAndIncrement();
        Row row = sheet.createRow(rowIndex.getAndIncrement());
        createRow(columnInfo, object, row, styles);
        return sheet;
    }

    /**
     * 创建行
     * @param columnInfos
     * @param object
     * @param row
     * @param styles
     */
    private void createRow(List<ColumnInfo> columnInfos, Object object, Row row, List<CellStyle> styles) {

        for (int i = 0; i < columnInfos.size(); i++) {
            Object fieldObject = null;
            try {
                fieldObject = PropertyUtils.getProperty(object, columnInfos.get(i).getName());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            String type = columnInfos.get(i).getType();
            Cell cell = row.createCell(i);
            cell.setCellStyle(styles.get(i));
            if (null == fieldObject) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue((String) null);
            } else {
                switch (type.toUpperCase()) {
                    case "NUMBER":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseFloat(fieldObject));
                        break;
                    case "FLOAT":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseFloat(fieldObject));
                        break;
                    case "DOUBLE":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseDouble(fieldObject));
                        break;
                    case "BIGDECIMAL":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseDouble(fieldObject));
                        break;
                    case "INT":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseLong(fieldObject));
                        break;
                    case "INTEGER":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(TypeConversionUtils.parseLong(fieldObject));
                        break;
                    case "DATE":
                        cell.setCellValue((Date) fieldObject);
                        break;
                    case "BOOLEAN":
                        cell.setCellType(CellType.BOOLEAN);
                        cell.setCellValue(TypeConversionUtils.parseBoolean(fieldObject));
                        break;
                    case "ZONEDDATETIME":
                        cell.setCellValue(Date.from(((ZonedDateTime) fieldObject).toInstant()));
                        break;
                    default:
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(TypeConversionUtils.parseString(fieldObject));
                        break;
                }
            }
        }
    }

    /**
     * @return
     * @Description: 初始化例属性
     * @param: columnInfos
     * @param: object
     * @Date: Created in 2018/5/28 16:14
     * @Modified by
     */
    private void initColumnType(List<ColumnInfo> columnInfos, Class<?> clazz) {
        for (ColumnInfo columnInfo : columnInfos) {
            // 当为最末级才去设置
            if (CollectionUtils.isEmpty(columnInfo.getColumnsInfo())) {
                Field field = ReflectionUtils.findField(clazz, columnInfo.getName());
                if (field != null) {
                    columnInfo.setType(field.getType().getSimpleName());
                    ExcelDomainField excelDomainField = field.getAnnotation(ExcelDomainField.class);
                    boolean hasTitle = StringUtils.hasText(columnInfo.getTitle());
                    if (excelDomainField != null) {
                        columnInfo.setDataFormat(excelDomainField.dataFormat());
                        columnInfo.setAlign(excelDomainField.align());
                        columnInfo.setWidth(excelDomainField.width());
                        if (!hasTitle) {
                            if (StringUtils.hasText(excelDomainField.title())) {
                                columnInfo.setTitle(excelDomainField.title());
                            } else {
                                columnInfo.setTitle(columnInfo.getName());
                            }
                        }
                    } else {
                        columnInfo.setDataFormat(null);
                        if (!hasTitle) {
                            columnInfo.setTitle(columnInfo.getName());
                        }
                    }
                }
            }else {
                initColumnType(columnInfo.getColumnsInfo(),clazz);
            }
        }
    }

    /**
     * 创建excel表头
     * @param wb
     * @param sheet
     * @param defaultColumnInfo
     * @return
     */
    private ExcelHeaderColumn createHeaderRow(Workbook wb, Sheet sheet, List<ColumnInfo> defaultColumnInfo){
        ExcelHeaderColumn excelHeaderColumn = new ExcelHeaderColumn(defaultColumnInfo);
        excelHeaderColumn.createHeaderRow(wb, sheet);
        return excelHeaderColumn;
    }

    /**
     * 获取样式
     * @param wb
     * @param columnInfoList
     * @return
     */
    private List<CellStyle> getCellStyles(Workbook wb, List<ColumnInfo> columnInfoList){
        List<CellStyle> styles = new ArrayList<>();
        for (int i = 0; i < columnInfoList.size(); i++) {
            // 格式
            String dataFormat = columnInfoList.get(i).getDataFormat();
            // 对齐方式
            HorizontalAlignment align = HorizontalAlignment.valueOf(columnInfoList.get(i).getAlign().toUpperCase());
            CellStyle style = wb.createCellStyle();
            style.setAlignment(align);
            if (dataFormat != null) {
                style.setDataFormat(wb.createDataFormat().getFormat(dataFormat));
            }
            Font font = wb.createFont();
            font.setFontName("宋体");
            //设置字体大小
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);
            styles.add(style);
        }
        return styles;
    }

}
