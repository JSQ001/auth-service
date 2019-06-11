package com.hand.hcf.app.core.service;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.util.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/11/19 09:44
 * @remark excel导入
 */
@Service
@Slf4j
public class ExcelImportService {

    /**
     * 导入
     * @param inputStream 流
     * @param multiSheet   是否是多sheet导入
     * @param columnNameRow 列名称所在行(非空行第N行)
     * @param excelImportHandler excel导入处理类
     */
    public <T> void importExcel(InputStream inputStream,
                                Boolean multiSheet,
                                int columnNameRow,
                                ExcelImportHandler<T> excelImportHandler) throws Exception {
        importExcel(inputStream,multiSheet,columnNameRow,excelImportHandler,Runtime.getRuntime().availableProcessors());
    }

    /**
     *
     * @param excelFile    excel文件
     * @param multiSheet   是否是多sheet导入
     * @param columnNameRow 列名称所在行
     * @param excelImportHandler excel导入处理类
     * @param <T>
     */
    public <T> void importExcel(File excelFile,
                                Boolean multiSheet,
                                int columnNameRow,
                                ExcelImportHandler<T> excelImportHandler) throws Exception {
        importExcel(excelFile,multiSheet,columnNameRow,excelImportHandler,Runtime.getRuntime().availableProcessors());
    }

    /**
     * @param inputStream 流
     * @param multiSheet   是否是多sheet导入
     * @param columnNameRow 列名称所在行(非空行第N行)
     * @param excelImportHandler excel导入处理类
     * @param nThreads  线程池线程数量
     */
    public <T> void importExcel(InputStream inputStream,
                                Boolean multiSheet,
                                int columnNameRow,
                                ExcelImportHandler<T> excelImportHandler,
                                int nThreads) throws Exception {
        try {
            log.info("create Workbook");
            Workbook workbook = WorkbookFactory.create(inputStream);
            // 执行导入的方法
            importWorkbook(workbook, multiSheet,columnNameRow,excelImportHandler,nThreads);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException(e.toString(), e.getMessage());
        } catch (InvalidFormatException e) {
            log.error(e.getMessage(), e);
            throw new BizException(e.toString(), e.getMessage());
        }
    }

    /**
     *
     * @param excelFile    excel文件
     * @param multiSheet   是否是多sheet导入
     * @param columnNameRow 列名称所在行
     * @param excelImportHandler excel导入处理类
     * @param nThreads  线程池线程数量
     * @param <T>
     */
    public <T> void importExcel(File excelFile,
                                Boolean multiSheet,
                                int columnNameRow,
                                ExcelImportHandler<T> excelImportHandler,
                                int nThreads) throws Exception {
        try {
            log.info("create Workbook");
            Workbook workbook = WorkbookFactory.create(excelFile);
            // 执行导入的方法
            importWorkbook(workbook, multiSheet,columnNameRow,excelImportHandler,nThreads);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException(e.toString(), e.getMessage());
        } catch (InvalidFormatException e) {
            log.error(e.getMessage(), e);
            throw new BizException(e.toString(), e.getMessage());
        }
    }

    private <T> void importWorkbook(Workbook workbook,
                                    Boolean isMany,
                                    int columnNameRow,
                                    ExcelImportHandler<T> excelImportHandler,
                                    int nThreads) throws Exception {
        excelImportHandler.clearHistoryData();
        importSheet(workbook, isMany, columnNameRow, excelImportHandler, nThreads);
    }

    private <T> void importSheet(Workbook workbook,
                                 Boolean isMany,
                                 int columnNameRow,
                                 ExcelImportHandler<T> excelImportHandler,
                                 int nThreads) throws Exception {
        Date now = new Date();
        nThreads = nThreads > 1 ? nThreads : 1;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        Lock lock = new ReentrantLock();
        List<Exception> runTimeExceptionList = new ArrayList<Exception>();
        // sheet data
        if (!isMany) {
            log.info("开始单sheet页导入");
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return;
            }
            importSheetData(sheet, columnNameRow, excelImportHandler, threadPoolExecutor, lock, runTimeExceptionList);
        }else{
            log.info("开始多sheet页导入");
            int activeSheetIndex = workbook.getNumberOfSheets();
            for (int i = 0; i < activeSheetIndex; i++){
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    return;
                }
                log.info("开始导入sheet页 {} 的数据", sheet.getSheetName());
                importSheetData(sheet, columnNameRow, excelImportHandler, threadPoolExecutor, lock, runTimeExceptionList);
            }
        }
        log.debug("导入结束！共计{0}毫秒", System.currentTimeMillis()- now.getTime());
    }

    private <T> void importSheetData(Sheet sheet,
                                     int columnNameRow,
                                     ExcelImportHandler<T> excelImportHandler,
                                     ThreadPoolExecutor threadPoolExecutor,
                                     Lock lock,
                                     List<Exception> runTimeExceptionList) throws Exception {
        if(columnNameRow < 0){
            columnNameRow = 0;
        }
        Class<T> entityClass = excelImportHandler.getEntityClass();
        Reflector reflector = new Reflector(entityClass);
        Iterator<Row> sheetIterator = sheet.rowIterator();
        List<String> setablePropertyNames = Arrays.asList(reflector.getSetablePropertyNames());
        Map<Integer,String> columnMap = new HashMap<>();
        List<T> entities = new ArrayList<>();
        List<String> rowNumberCache = new ArrayList<>();
        int rowIndex = 1;
        while (sheetIterator.hasNext()) {
            Row rowX = sheetIterator.next();
            if (rowIndex == columnNameRow) {
                log.info("导入属性行");
                initColumnMap(rowX,setablePropertyNames,columnMap,excelImportHandler);
            }
            if (rowIndex > columnNameRow + 1) {
                log.info("开始导入第{}行的数据", rowIndex);
                T entity = getEntityAndSetValue(rowX,reflector,columnMap,excelImportHandler,rowNumberCache);
                // 当此行所有有效数据全部为空时，默认
                if(entity == null){
                    continue;
                }
                // 数据逐条校验
                if(ExcelImportHandler.ONE_RECORD == excelImportHandler.getCheckFrequency()){
                    List<T> ts = Arrays.asList(entity);
                    asyncCheckAndPersistenceAndClear(ts,excelImportHandler,threadPoolExecutor,lock,runTimeExceptionList);
                }else{
                    entities.add(entity);
                }
            }
            if(ExcelImportHandler.ONE_RECORD != excelImportHandler.getCheckFrequency() && entities.size() >= excelImportHandler.checkBatchAndPersistenceSize()){
                // 异步校验需要重新创建List，并将原容器清空
                List entitiesCopy = new ArrayList(entities);
                entities.clear();
                asyncCheckAndPersistenceAndClear(entitiesCopy, excelImportHandler,threadPoolExecutor,lock,runTimeExceptionList);
            }
            rowIndex++;
        }
        // 尾数处理
        if(ExcelImportHandler.ONE_RECORD != excelImportHandler.getCheckFrequency() && entities.size() > 0){
            // 异步校验需要重新创建List，并将原容器清空
            List entitiesCopy = new ArrayList(entities);
            entities.clear();
            asyncCheckAndPersistenceAndClear(entitiesCopy, excelImportHandler,threadPoolExecutor,lock,runTimeExceptionList);
        }
        threadPoolExecutor.shutdown();
        while(true){
            // 线程池线程执行完毕
            if (threadPoolExecutor.isTerminated()) {
                // 先判断有没有线程发生异常,若有则抛出
                if (CollectionUtils.isNotEmpty(runTimeExceptionList)) {
                    Exception exception = runTimeExceptionList.get(0);
                    log.debug("导入错误：");
                    throw exception;
                }
                if(CollectionUtils.isNotEmpty(entities)){
                    checkAndPersistenceAndClear(entities, excelImportHandler);
                }
                log.debug("导入结束!");
                break;
            }
        }
        System.out.println(rowNumberCache.toString());
    }

    private <T> void asyncCheckAndPersistenceAndClear(List<T> entities,
                                                      ExcelImportHandler<T> excelImportHandler,
                                                      ThreadPoolExecutor threadPoolExecutor,
                                                      Lock lock,
                                                      List<Exception> runTimeExceptionList){
        // 线程池任务队列容量超过2条时，主线程睡眠50ms，防止内存溢出
        while(threadPoolExecutor.getQueue().size() > 2){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!threadPoolExecutor.isShutdown()){
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    excelImportHandler.setThreadLocal();
                    try{
                        checkAndPersistenceAndClear(entities,excelImportHandler);
                    } catch (Exception exception) {
                        // 使用锁机制，防止runTimeExceptionList中放入多个异常信息
                        if (lock.tryLock()) {
                            try {
                                threadPoolExecutor.shutdownNow();
                                runTimeExceptionList.add(exception);
                            } catch (Exception e) {

                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                }
            });
        }
    }

    private <T> void checkAndPersistenceAndClear(List<T> entities, ExcelImportHandler<T> excelImportHandler){
        if(CollectionUtils.isNotEmpty(entities)){
            excelImportHandler.check(entities);
            excelImportHandler.persistence(entities);
        }
        entities.clear();
    }

    /**
     * 初始化列信息
     * @param rowX
     * @param setablePropertyNames
     * @param columnMap
     */
    private <T> void initColumnMap(Row rowX, List<String> setablePropertyNames, Map<Integer,String> columnMap, ExcelImportHandler<T> excelImportHandler){
        for (int i = 0; i < setablePropertyNames.size(); i++) {
            // cell
            Cell cell = rowX.getCell(i);
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                String fieldValue = cell.getStringCellValue();
                if(StringUtils.isNotEmpty(fieldValue)){
                    // 实体类中找不到对应的属性，则不设置相应的值
                    if(setablePropertyNames.contains(fieldValue)){
                        columnMap.put(i,fieldValue);
                        log.info("第{}列的属性值是：{}", i, fieldValue);
                    }
                }
            }
        }
        if(columnMap.size() == 0){
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_COLUMN_ERROR);
        }
        if(! columnMap.containsValue(excelImportHandler.getRowNumberColumnName())){
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_NUMBER_COLUMN_EMPTY);
        }
    }

    /**
     * 获取临时表对象，并设置相应属性的值
     * 要求excel中出现的所有列，对应实体类中都必须为String类型
     * @param rowX
     * @param reflector
     * @param columnMap
     * @param excelImportHandler
     * @param <T>
     * @return
     */
    private <T> T getEntityAndSetValue(Row rowX, Reflector reflector,
                                       Map<Integer,String> columnMap,
                                       ExcelImportHandler<T> excelImportHandler,
                                       List<String> rowNumberCache){
        T entity = excelImportHandler.getEntityInstance();
        Boolean rowNumberIsEmpty = true;
        Boolean otherColumnIsEmpty = true;
        // 通过区间法判断序号是否存在
        for(Integer i : columnMap.keySet()){
            Cell cell = rowX.getCell(i);
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                String fieldValue = cell.getStringCellValue();
                if (StringUtils.isNotEmpty(fieldValue) || StringUtils.isNotEmpty(fieldValue.trim())) {
                    String fieldName = columnMap.get(i);
                    if(fieldName.equals(excelImportHandler.getRowNumberColumnName())){
                        checkRowNumberUnique(fieldValue,rowNumberCache,rowX.getRowNum());
                        rowNumberIsEmpty = false;
                    }else{
                        otherColumnIsEmpty = false;
                    }
                    Class<?> setterType = reflector.getSetterType(fieldName);
                    // 导入列 统一使用String接收，否则不予设置值
                    if (String.class.isAssignableFrom(setterType)) {
                        Invoker setInvoker = reflector.getSetInvoker(columnMap.get(i));
                        try {
                            setInvoker.invoke(entity, new String[]{fieldValue});
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else {
                        log.info("防止excel单元格格式错误，导入列统一使用String类型接收，非String类型默认不赋值！");
                    }
                }
            }
        }
        // 序号列为空,但其他列不为空
        if(rowNumberIsEmpty &&  !otherColumnIsEmpty){
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_NUMBER_NONE,new Object[]{rowX.getRowNum()});
        }else if(otherColumnIsEmpty && !rowNumberIsEmpty){
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_COLUMN_NONE,new Object[]{rowX.getRowNum()});
        }else if(otherColumnIsEmpty && rowNumberIsEmpty){
            return null;
        }
        return entity;
    }

    /**
     * 校验序号是否重复
     * @param rowNumberValue
     * @param rowNumberCache
     * @param lineNumber
     */
    private synchronized List<String> checkRowNumberUnique(String rowNumberValue, List<String> rowNumberCache, Integer lineNumber) {
        Integer rowNumber;
        try {
            rowNumber = Integer.valueOf(rowNumberValue);
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_NUMBER_MUST_NUMBER, new Object[]{lineNumber});
        }
        if(rowNumberCache == null){
            rowNumberCache = new ArrayList<>();
        }
        if(CollectionUtils.isEmpty(rowNumberCache)){
            rowNumberCache.add(rowNumber+","+rowNumber);
            return rowNumberCache;
        }
        int start = 0;
        int end = rowNumberCache.size() - 1;
        int index = 0;
        // 二分法获取rowNumber所在区间
        while (start <= end) {
            int middle = (start + end) / 2;
            String[] split = rowNumberCache.get(middle).split(",");
            Integer middleLowerValue = Integer.valueOf(split[0]);
            Integer middleUpperValue = Integer.valueOf(split[1]);
            if (rowNumber < middleLowerValue) {
                if (middle == 0) {
                    index = middle;
                    break;
                }
                end = middle - 1;
            } else if (rowNumber > middleUpperValue) {
                if (middle < rowNumberCache.size() - 1) {
                    if (rowNumber < Integer.valueOf(rowNumberCache.get(middle + 1).split(",")[0])) {
                        index = middle;
                        break;
                    }
                } else {
                    index = middle;
                    break;
                }
                start = middle + 1;
            } else {
                throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_NUMBER_UNIQUE);
            }
        }
        String rowNumberScope = rowNumberCache.get(index);
        Integer lowerValue = Integer.valueOf(rowNumberScope.split(",")[0]);
        Integer upperValue = Integer.valueOf(rowNumberScope.split(",")[1]);
        // 新增值为最小值
        if (rowNumber < lowerValue) {
            if (rowNumber == lowerValue - 1) {
                if(index != 0){
                    String[] beforeSplit = rowNumberCache.get(index - 1).split(",");
                    if(rowNumber == Integer.valueOf(beforeSplit[1]) + 1){
                        rowNumberCache.set(index - 1,beforeSplit[0]+","+upperValue);
                        rowNumberCache.remove(index);
                        return rowNumberCache;
                    }
                }
                rowNumberCache.set(index, rowNumber + "," + upperValue);
            } else {
                rowNumberCache.add(index, rowNumber + "," + rowNumber);
            }
            // 新增值为最大值
        } else if (rowNumber > upperValue) {
            if (rowNumber == upperValue + 1) {
                if(index != rowNumberCache.size() - 1){
                    String[] afterSplit = rowNumberCache.get(index + 1).split(",");
                    if(rowNumber+1 == Integer.valueOf(afterSplit[0])){
                        rowNumberCache.set(index,lowerValue+","+afterSplit[1]);
                        rowNumberCache.remove(index+1);
                        return rowNumberCache;
                    }
                }
                rowNumberCache.set(index, lowerValue + "," + rowNumber);
            } else {
                if (index == rowNumberCache.size() - 1) {
                    rowNumberCache.add(rowNumber + "," + rowNumber);
                } else {
                    rowNumberCache.add(index+1, rowNumber + "," + rowNumber);
                }
            }
            //新增值在所获取到的区间内，标识序号已重复
        } else {
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_ROW_NUMBER_UNIQUE);
        }
        return rowNumberCache;
    }

}
