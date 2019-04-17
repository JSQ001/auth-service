package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLine;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLineTemp;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustLineMapper;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustLineWebDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustLinesBean;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/10
 */
@Service
public class ExpenseAdjustLineService extends BaseService<ExpenseAdjustLineMapper, ExpenseAdjustLine> {

    @Autowired
    private ExpenseAdjustHeaderService headerService;
    @Autowired
    private ExpenseDimensionService expenseDimensionService;
    @Autowired
    private ExpenseAdjustLineTempService tempService;
    @Autowired
    private ExpenseAdjustTypeService typeService;
    @Autowired
    private ExcelImportService excelImportService;
    @Autowired
    private OrganizationService organizationService;


    //  外部导入模板
    private final static String Import_Expense_Adjust_Lines_1001 = "/templates/Expense_Adjust_Lines_1001.xlsx";

    private final static String Import_Expense_Adjust_Lines_1003 = "/templates/Expense_Adjust_Lines_1003.xlsx";
    //  内部导入模板
    private final static String Import_Expense_Adjust_Lines_1002 = "/templates/Expense_Adjust_Lines_1002.xlsx";

    private final static int EXCEL_BASE_ROW = 2;


    public List<ExpenseAdjustLineWebDTO> listExpenseAdjustLinesDTOByHeaderId(Long expAdjustHeaderId, Page page) {
        //  查询
        List<ExpenseAdjustLineWebDTO> linesDTOS = baseMapper.listLineDTOByHeaderId(expAdjustHeaderId,page);
        if (!CollectionUtils.isEmpty(linesDTOS)) {
            // 设置维度、公司、部门、附件
            // 先查询出所有的维度
            List<ExpenseAdjustLine> adjustLines = this.selectList(new EntityWrapper<ExpenseAdjustLine>().eq("exp_adjust_header_id", expAdjustHeaderId));
            Set<Long> dimensionSetId = new HashSet<>();
            adjustLines.stream().forEach(e -> {
                ExpenseAdjustLineWebDTO dto = new ExpenseAdjustLineWebDTO();
                BeanUtils.copyProperties(e, dto);
                dimensionSetId.addAll(DimensionUtils.getDimensionId(dto, ExpenseAdjustLineWebDTO.class));
            });
            Map<Long, String> dimensionMap = new HashMap<>(16);
            if (!CollectionUtils.isEmpty(dimensionSetId)){
                List<DimensionItemCO> dimension = organizationService.listDimensionItemsByIds(new ArrayList<>(dimensionSetId));
                if (!CollectionUtils.isEmpty(dimension)){
                    dimensionMap = dimension.stream().collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1,k2)->k1));
                }
            }

            List<Long> companyIds = baseMapper.getCompanyId(expAdjustHeaderId);

            Map<Long, CompanyCO> comMap = organizationService.getCompanyMapByCompanyIds(companyIds);

            List<Long> departmentIds = baseMapper.getUnitId(expAdjustHeaderId);
            Map<Long, DepartmentCO> unitMap = organizationService.getDepartmentMapByDepartmentIds(departmentIds);

            for (ExpenseAdjustLineWebDTO dto : linesDTOS){
                if (StringUtils.hasText(dto.getAttachmentOid())){
                    String[] split = dto.getAttachmentOid().split(",");
                    List<String> attachmentOidList = Arrays.asList(split);
                    List<AttachmentCO> attachments = organizationService.listAttachmentsByOids(attachmentOidList);
                    dto.setAttachmentOids(attachmentOidList);
                    dto.setAttachments(attachments);
                }
                setDimensionsAndOtherInfo(dto, dimensionMap, comMap, unitMap);
                if (!CollectionUtils.isEmpty(dto.getLinesDTOList())){
                    for (ExpenseAdjustLineWebDTO e : dto.getLinesDTOList()) {
                        setDimensionsAndOtherInfo(e, dimensionMap, comMap, unitMap);
                    }
                }
            }
        }
        return linesDTOS;
    }

    public List<ExpenseAdjustLine> listExpenseAdjustLinesByHeaderId(Long expAdjustHeaderId) {
        return this.selectList(new EntityWrapper<ExpenseAdjustLine>().eq("exp_adjust_header_id", expAdjustHeaderId));
    }

    private void setDimensionsAndOtherInfo(ExpenseAdjustLineWebDTO dto,
                                           Map<Long, String> dimensionMap,
                                           Map<Long, CompanyCO> comMap,
                                           Map<Long, DepartmentCO> unitMap){
        DimensionUtils.setDimensionCodeOrName("Name", dto, ExpenseAdjustLineWebDTO.class, dimensionMap);
        if (comMap.containsKey(dto.getCompanyId())){
            dto.setCompanyName(comMap.get(dto.getCompanyId()).getName());
        }
        if (unitMap.containsKey(dto.getUnitId())){
            dto.setUnitName(unitMap.get(dto.getUnitId()).getName());
        }
    }


    /**
     * 单据行保存
     * @param linesBean
     * @param transactionNumber
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseAdjustLinesBean insertExpenseAdjustLinesList(ExpenseAdjustLinesBean linesBean, String transactionNumber) {
        //  根据单据头ID查询该单据头信息
        ExpenseAdjustHeader header = headerService.selectById(linesBean.getExpAdjustHeaderId());
        ExpenseAdjustLine line = null;
        if (header == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_HEADER_IS_NOT_EXISTS);
        }
        headerService.checkDocumentStatus(0, header.getStatus());

        // 当类型为费用分摊时，需要校验费用分摊行金额不能为0.
        // 当类型为费用补录时，费用行保存时，需要校验金额不能为0
        // 1001 为分摊，1002 为补录
        if("1002".equals(header.getAdjustTypeCategory())){
            if(linesBean.getAmount() == null || linesBean.getAmount().equals(BigDecimal.ZERO)){
                throw new BizException(RespCode.EXPENSE_ADJUST_LINE_AMOUNT_IS_ZERO);
            }
        }
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(header.getId(), ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(), null);
        //  判断新增还是更新
        if (linesBean.getId() == null){ //  新增
            //  设置汇率 取单据头上的汇率
            linesBean.setExchangeRate(header.getExchangeRate());
            linesBean.setCurrencyCode(header.getCurrencyCode());
            //  转化-获取第一页  单据行类型为1001 表示行
            ExpenseAdjustLine expenseAdjustLine = toExpenseAdjustLinesByBean(linesBean);
            //  校验参数
            checkExpenseAdjustLinesDate(expenseAdjustLine);
            //  插入
            this.insert(expenseAdjustLine);
            line = expenseAdjustLine;
            //  拿取id
            linesBean.setId(expenseAdjustLine.getId());
            //  调用方法处理第二页的行数据
            // 1001 为分摊，1002 为补录
            if("1001".equals(header.getAdjustTypeCategory())){
                secondPagesDataProcessor(linesBean, true, transactionNumber, dimensions.size());
            }
        }else { //  更新
            //删除行信息
            List<Long> deleteIds = linesBean.getDeleteIds();
            if(!CollectionUtils.isEmpty(deleteIds)){
                this.deleteBatchIds(deleteIds);
            }

            //  转化-获取第一页  单据行类型为1001
            ExpenseAdjustLine expenseAdjustLine = toExpenseAdjustLinesByBean(linesBean);
            //  更新
            this.updateById(expenseAdjustLine);
            line = expenseAdjustLine;
            //  调用方法处理第二页的行数据
            // 1001 为分摊，1002 为补录
            if("1001".equals(header.getAdjustTypeCategory())){
                secondPagesDataProcessor(linesBean, false, transactionNumber, dimensions.size());
            }
        }
        // 更新单据头的总金额以及分摊金额
        if ("1001".equals(header.getAdjustTypeCategory())) {
            updateLineAmount(line);
        }
        updateHeaderAmount(header);

        //  返回
        return linesBean;
    }

    private void updateLineAmount(ExpenseAdjustLine line) {
        BigDecimal amount = baseMapper.getAmount(line.getExpAdjustHeaderId(), line.getId());
        line.setAmount(amount.multiply(BigDecimal.valueOf(-1)));
        line.setFunctionalAmount(TypeConversionUtils.roundHalfUp(line.getAmount().multiply(line.getExchangeRate())));
        this.updateById(line);
    }

    private BigDecimal updateHeaderAmount(ExpenseAdjustHeader header) {
        BigDecimal amount = baseMapper.getAmount(header.getId(), null);
        header.setTotalAmount(amount);
        header.setFunctionalAmount(TypeConversionUtils.roundHalfUp(header.getTotalAmount().multiply(header.getExchangeRate())));
        headerService.updateById(header);
        return amount;
    }

    private static ExpenseAdjustLine toExpenseAdjustLinesByBean(ExpenseAdjustLinesBean bean){
        ExpenseAdjustLine lines = new ExpenseAdjustLine();
        BeanUtils.copyProperties(bean,lines);
        if (lines.getAmount() == null){
            lines.setAmount(BigDecimal.ZERO);
        }
        if (lines.getFunctionalAmount() == null){
            lines.setFunctionalAmount(BigDecimal.ZERO);
        }else{
            lines.setFunctionalAmount(TypeConversionUtils.roundHalfUp(lines.getAmount().multiply(lines.getExchangeRate())));
        }
        return lines;
    }

    private static void checkExpenseAdjustLinesDate(ExpenseAdjustLine expenseAdjustLine){
        //  公司
        if (expenseAdjustLine.getCompanyId() == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_LINE_COMPANY_IS_NULL);
        }
        //  部门
        if (expenseAdjustLine.getUnitId() == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_LINE_UNIT_IS_NULL);
        }
        //  费用类型
        if (expenseAdjustLine.getExpenseTypeId() == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_LINE_EXPENSE_TYPE_IS_NULL);
        }
    }

    public void secondPagesDataProcessor(ExpenseAdjustLinesBean linesBean ,Boolean isNew, String transactionNumber, int dimensionIdSize){
        //  获取第二页集合(如果是费用分摊list中则存有第二页的行数据)  单据行类型为1002
        List<ExpenseAdjustLine> list = linesBean.getLinesList();
        //  判断集合是否为空
        if (!CollectionUtils.isEmpty(list)){
            //  遍历第二页数据
            for (ExpenseAdjustLine lines : list){
                //  设置汇率
                lines.setCurrencyCode(linesBean.getCurrencyCode());
                lines.setExchangeRate(linesBean.getExchangeRate());
                lines.setFunctionalAmount(TypeConversionUtils.roundHalfUp(lines.getAmount().multiply(lines.getExchangeRate())));
                lines.setAmount(lines.getAmount());
                if (lines.getId() == null){ //  第二页 新增
                    //  建立层级关系
                    lines.setId(null);
                    lines.setAdjustDate(ZonedDateTime.now());
                    lines.setSourceAdjustLineId(linesBean.getId());
                    this.insert(lines);
                }else { //  第二页 更新
                    this.updateById(lines);
                }
            }
        }else{
            // 导入不管
            if (isNew && !StringUtils.hasText(transactionNumber)){
                throw new BizException(RespCode.EXPENSE_ADJUST_LINE_IS_NULL);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLineById(Long id) {
        ExpenseAdjustLine expenseAdjustLine = this.selectById(id);
        if (null == expenseAdjustLine){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        ExpenseAdjustHeader expenseAdjustHeader = headerService.selectById(expenseAdjustLine.getExpAdjustHeaderId());
        if (null == expenseAdjustHeader){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        headerService.checkDocumentStatus(-1, expenseAdjustHeader.getStatus());
        this.deleteById(id);
        updateHeaderAmount(expenseAdjustHeader);
        return true;
    }

    /**
     * 导出模板
     * @param expenseAdjustHeaderId  单据头id
     * @param external 模板标志 (导出外部导入模板还是内部导入模板,true: 外部模板,false 内部模板,默认 导出外部模板)
     * @return
     */
    public byte[] exportExpenseAdjustLinesTemplate(Long expenseAdjustHeaderId,
                                                   boolean external) {
        ExpenseAdjustHeader expenseAdjustHeader = headerService.selectById(expenseAdjustHeaderId);
        if (expenseAdjustHeader == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(expenseAdjustHeaderId, ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(), null);

        return createExcelTemplate(expenseAdjustHeader, dimensions, external, false, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public String importExpenseAdjustLineTemplate(InputStream in, Long expenseAdjustHeaderId, Long sourceAdjustLineId) throws Exception {
        String batchNumber = UUID.randomUUID().toString();
        ExpenseAdjustHeader expenseAdjustHeader = headerService.selectById(expenseAdjustHeaderId);
        if (null == expenseAdjustHeader){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        String adjustLineCategory = "1001";
        if (sourceAdjustLineId != null){
            adjustLineCategory = "1002";
        }
        // 查询所有的维度
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(expenseAdjustHeaderId, ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(), null);

        String finalAdjustLineCategory = adjustLineCategory;
        // 实现导入的接口
        ExcelImportHandler<ExpenseAdjustLineTemp> importHandler = new ExcelImportHandler<ExpenseAdjustLineTemp>() {
            @Override
            public void clearHistoryData() {
                tempService.delete(new EntityWrapper<ExpenseAdjustLineTemp>().le("created_date", ZonedDateTime.now().plusDays(-7L)));
            }

            @Override
            public Class<ExpenseAdjustLineTemp> getEntityClass() {
                return ExpenseAdjustLineTemp.class;
            }

            @Override
            public List<ExpenseAdjustLineTemp> persistence(List<ExpenseAdjustLineTemp> list) {
                list.stream().forEach(e -> {
                    e.setAdjustLineCategory(finalAdjustLineCategory);
                    e.setBatchNumber(batchNumber);
                    e.setErrorFlag(Boolean.FALSE);
                    e.setErrorMsg("");
                    e.setSetOfBooksId(expenseAdjustHeader.getSetOfBooksId());
                    e.setTenantId(expenseAdjustHeader.getTenantId());
                    e.setEmployeeId(expenseAdjustHeader.getEmployeeId());
                    e.setExpAdjustHeaderId(expenseAdjustHeaderId);
                    // 金额格式
                    if ("1002".equals(expenseAdjustHeader.getAdjustTypeCategory())){
                        // 费用补录, 校验非空，同时校验金额
                        checkImportNull(e, dimensions, true);
                    }else{
                        // 费用分摊 校验非空 如果是分摊行导入则校验金额，否则不校验金额
                        if (sourceAdjustLineId != null){
                            // 校验金额
                            checkImportNull(e, dimensions, true);
                        }else{
                            checkImportNull(e, dimensions, false);
                        }
                    }
                });
                tempService.insertBatch(list);
                return list;
            }

            @Override
            public void check(List<ExpenseAdjustLineTemp> list) {
            }
        };
        excelImportService.importExcel(in, false, 2, importHandler, 3);

        checkImportData(expenseAdjustHeader, batchNumber, dimensions);
        return batchNumber;
    }

    private void checkImportData(ExpenseAdjustHeader expenseAdjustHeader, String batchNumber, List<ExpenseDimension> dimensions) {
        // 校验序号
        int count = tempService.selectCount(new EntityWrapper<ExpenseAdjustLineTemp>().eq("batch_number", batchNumber).groupBy("row_index").having("count(0) > 1 or row_index is null"));
        if (count > 0){
            throw new BizException(RespCode.EXPENSE_ADJUST_LINE_IMPORT_INDEX_ERROR);
        }
        // 查询出导入的数据
        List<ExpenseAdjustLineTemp> importTemp = tempService.selectList(new EntityWrapper<ExpenseAdjustLineTemp>().eq("batch_number", batchNumber));

        // 校验费用类型 调用前台新建行时选择的费用类型的LOV查询，分页大小设置为10W
        Page page = new Page(1, 1000000);
        page.setSearchCount(false);
        List<ExpenseType> expenseType = typeService.getExpenseType(expenseAdjustHeader.getExpAdjustTypeId(), null, null, page);

        // 由于费用类型当前账套下代码不能重复，因此 将集合转换为map <String, Long>
        Map<String, Long> expenseTypeMap = expenseType
                .stream()
                .collect(Collectors.toMap(ExpenseType::getCode, ExpenseType::getId, (k1, k2) -> k1));

        // 查询出当前账套的机构
        List<CompanyCO> companies = organizationService.listCompanyBySetOfBooksId(expenseAdjustHeader.getSetOfBooksId(), true);
        Map<String, Long> comMap = companies.stream().collect(Collectors.toMap(CompanyCO::getCompanyCode, CompanyCO::getId, (k1, k2) -> k1));
        // 查询当前账套的部门
        Map<String, Long> departMap = organizationService.listDepartmentByStatus(true)
                .stream()
                .filter(e -> StringUtils.hasText(e.getDepartmentCode()))
                .collect(Collectors.toMap(DepartmentCO::getDepartmentCode, DepartmentCO::getId, (k1, k2) -> k1));
        // 查询已启用的维度信息含维值
        List<Long> dimensionIds = dimensions.stream().map(ExpenseDimension::getDimensionId).collect(Collectors.toList());

        List<DimensionDetailCO> centerDTOS = organizationService.listDimensionsBySetOfBooksIdAndIds(expenseAdjustHeader.getSetOfBooksId(), dimensionIds);
        Map<Long, List<DimensionItemCO>> costItemsMap = centerDTOS
                .stream()
                .collect(Collectors.toMap(DimensionDetailCO::getId, DimensionDetailCO::getSubDimensionItemCOS, (k1, k2) -> k1));

        importTemp.stream().forEach(e -> {
            if (!StringUtils.hasText(e.getErrorMsg())){
                e.setErrorMsg("");
            }
            // 费用类型
            if (expenseTypeMap.containsKey(e.getExpenseTypeCode())){
                e.setExpenseTypeId(expenseTypeMap.get(e.getExpenseTypeCode()));
            }else {
                e.setErrorFlag(Boolean.TRUE);
                e.setErrorMsg(e.getErrorMsg() + "费用类型代码：" + e.getExpenseTypeCode() + "不存在！" );
            }
            // 公司
            if (comMap.containsKey(e.getCompanyCode())){
                e.setCompanyId(comMap.get(e.getCompanyCode()));
            }else {
                e.setErrorFlag(Boolean.TRUE);
                e.setErrorMsg(e.getErrorMsg() + "公司代码：" + e.getCompanyCode() + "不存在！" );
            }
            // 部门
            if (departMap.containsKey(e.getUnitCode())){
                e.setUnitId(departMap.get(e.getUnitCode()));
            }else {
                e.setErrorFlag(Boolean.TRUE);
                e.setErrorMsg(e.getErrorMsg() + "部门代码：" + e.getUnitCode() + "不存在！" );
            }
            // 维度
            setImportDimensionId(e, dimensions, costItemsMap);
        });

        tempService.updateBatchById(importTemp);
    }

    /**
     * 导入数据的非空校验
     * @param temp
     * @param dimensions
     * @param checkAmount
     */
    private void checkImportNull(ExpenseAdjustLineTemp temp, List<ExpenseDimension> dimensions, boolean checkAmount){
        if (checkAmount) {
            if (StringUtils.hasText(temp.getAmount())) {
                try {
                    new BigDecimal(temp.getAmount());
                } catch (NumberFormatException ex) {
                    temp.setErrorMsg("金额格式不正确！");
                    temp.setErrorFlag(Boolean.TRUE);
                }
            } else {
                temp.setErrorMsg("金额不允许为空！");
                temp.setErrorFlag(Boolean.TRUE);
            }
        }else{
            temp.setAmount("0");
        }
        // 公司代码
        if (!StringUtils.hasText(temp.getCompanyCode())){
            temp.setErrorFlag(Boolean.TRUE);
            temp.setErrorMsg(temp.getErrorMsg() + "公司代码不允许为空！");
        }
        // 部门代码
        if (!StringUtils.hasText(temp.getUnitCode())){
            temp.setErrorFlag(Boolean.TRUE);
            temp.setErrorMsg(temp.getErrorMsg() + "部门代码不允许为空！");
        }

        // 费用类型代码
        if (!StringUtils.hasText(temp.getExpenseTypeCode())){
            temp.setErrorFlag(Boolean.TRUE);
            temp.setErrorMsg(temp.getErrorMsg() + "费用类型代码不允许为空！");
        }
        // 维度
        dimensions.stream().forEach(e -> {
            String importDimensionCode = getImportDimensionCode(temp, e.getDimensionField().replace("Id", "Code"));
            if (!StringUtils.hasText(importDimensionCode)){
                temp.setErrorFlag(Boolean.TRUE);
                temp.setErrorMsg(temp.getErrorMsg() + e.getName() + "不允许为空！");
            }
        });
    }

    /**
     * 获取导入数据的维度代码
     * @param temp
     * @param fieldName
     * @return
     */
    private String getImportDimensionCode(ExpenseAdjustLineTemp temp, String fieldName){
        Field field = ReflectionUtils.findField(ExpenseAdjustLineTemp.class, fieldName);
        if (field != null){
            field.setAccessible(true);
            try {
                Object o = field.get(temp);
                return o.toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void setImportDimensionId(ExpenseAdjustLineTemp temp,
                                      List<ExpenseDimension> dimensions,
                                      Map<Long, List<DimensionItemCO>> costItemsMap){
        dimensions.stream().forEach(e -> {
            String code = getImportDimensionCode(temp, e.getDimensionField().replace("Id", "Code"));
            if (costItemsMap.containsKey(e.getDimensionId())){
                Map<String, Long> dimensionMap = costItemsMap.get(e.getDimensionId())
                        .stream()
                        .collect(Collectors.toMap(DimensionItemCO::getDimensionItemCode, DimensionItemCO::getId, (k1, k2) -> k1));
                if (dimensionMap.containsKey(code)){
                    Field field = ReflectionUtils.findField(ExpenseAdjustLineTemp.class, e.getDimensionField());
                    if (field != null){
                        // 维度存在
                        field.setAccessible(true);
                        try {
                            field.set(temp, dimensionMap.get(code));
                        } catch (IllegalAccessException exception) {
                            // 异常默认为维度不存在
                            temp.setErrorFlag(Boolean.TRUE);
                            temp.setErrorMsg(temp.getErrorMsg() + "维度" + e.getName() + "的代码：" + code + "不存在！");
                        }
                    }else{
                        // field不存在 则默认维度不存在
                        temp.setErrorFlag(Boolean.TRUE);
                        temp.setErrorMsg(temp.getErrorMsg() + "维度" + e.getName() + "的代码："  + code + "不存在！");
                    }
                }else{
                    temp.setErrorFlag(Boolean.TRUE);
                    temp.setErrorMsg(temp.getErrorMsg() + "维度" + e.getName() + "的代码："  + code + "不存在！");
                }
            }else{
                temp.setErrorFlag(Boolean.TRUE);
                temp.setErrorMsg(temp.getErrorMsg() + "维度" + e.getName() + "的代码："  + code + "不存在！");
            }
        });
    }

    public ImportResultDTO queryImportResultInfo(String transactionUUID) {
        return tempService.queryImportResultInfo(transactionUUID);
    }

    public byte[] exportFailedData(Long headerId, String transactionID, Boolean external) {
        List<ExpenseAdjustLineTemp> expenseAdjustLinesTemps = tempService.selectList(
                new EntityWrapper<ExpenseAdjustLineTemp>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1)
                        .orderBy("row_index",true));
        ExpenseAdjustHeader expenseAdjustHeader = headerService.selectById(headerId);
        if (expenseAdjustHeader == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(headerId, ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(), null);

        return createExcelTemplate(expenseAdjustHeader, dimensions, external, true, expenseAdjustLinesTemps);
    }

    private byte[] createExcelTemplate(ExpenseAdjustHeader expenseAdjustHeader,
                                       List<ExpenseDimension> dimensions,
                                       boolean external,
                                       boolean writeErrorData,
                                       List<ExpenseAdjustLineTemp> errorDataList){
        //  创建流
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            //  获取文件流
            if (external){
                if ("1001".equals(expenseAdjustHeader.getAdjustTypeCategory())) {
                    inputStream = StreamUtil.getResourceStream(Import_Expense_Adjust_Lines_1001);
                }else{
                    inputStream = StreamUtil.getResourceStream(Import_Expense_Adjust_Lines_1003);
                }
            }else {
                inputStream = StreamUtil.getResourceStream(Import_Expense_Adjust_Lines_1002);
            }
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            //  获取sheet页
            XSSFSheet sheet = workbook.getSheetAt(0);

            // code 行
            XSSFRow codeRow = sheet.getRow(1);
            //  获取标题行
            XSSFRow titleRow = sheet.getRow(2);
            Font font = workbook.createFont();
            font.setFontName("宋体");
            font.setColor(IndexedColors.RED.index);
            font.setBold(true);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
            cellStyle.setFont(font);
            //  遍历维度List
            dimensions.forEach( u -> {
                sheet.setColumnWidth(codeRow.getLastCellNum(), 60 * 80);
                // 创建code 列
                XSSFCell codeCell = codeRow.createCell(codeRow.getLastCellNum(), CellType.STRING);
                codeCell.setCellStyle(cellStyle);
                codeCell.setCellType(CellType.STRING);
                //  赋值 维度Name
                codeCell.setCellValue(u.getDimensionField().replace("Id","Code"));
                //  创建 标题列
                XSSFCell titleCell = titleRow.createCell(titleRow.getLastCellNum(), CellType.STRING);
                titleCell.setCellStyle(cellStyle);
                titleCell.setCellType(CellType.STRING);
                //  赋值 维度Name
                titleCell.setCellValue(u.getName() + "（必填）");
            });
            if (writeErrorData){
                // 错误信息模板
                Font errorFont = workbook.createFont();
                errorFont.setFontName("宋体");
                errorFont.setColor(IndexedColors.BLACK.index);
                errorFont.setBold(true);
                CellStyle errorCellStyle = workbook.createCellStyle();
                errorCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
                errorCellStyle.setFont(errorFont);
                sheet.setColumnWidth(titleRow.getLastCellNum(), 60 * 120);
                XSSFCell titleCell = titleRow.createCell(titleRow.getLastCellNum(), CellType.STRING);
                titleCell.setCellStyle(errorCellStyle);
                titleCell.setCellType(CellType.STRING);
                titleCell.setCellValue("错误信息");
                int startRow = EXCEL_BASE_ROW;
                Row row = null;
                Cell cell = null;
                int lastCellNum = codeRow.getLastCellNum();
                for (ExpenseAdjustLineTemp importDTO : errorDataList) {
                    row = sheet.createRow(++startRow);
                    for (int i = 0; i < lastCellNum; i++){
                        cell = row.createCell(i, CellType.STRING);
                        // 取属性
                        XSSFCell codeRowCell = codeRow.getCell(i);
                        cell.setCellValue(getErrorValue(importDTO, codeRowCell.getStringCellValue()));
                    }
                    // 输出错误信息
                    cell = row.createCell(lastCellNum, CellType.STRING);
                    cell.setCellValue(importDTO.getErrorMsg());
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
            throw new BizException("file", "read file failed");
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
                throw new BizException("file", "read file failed");
            }
        }
    }

    private String getErrorValue(ExpenseAdjustLineTemp temp, String fieldName){
        Field field = ReflectionUtils.findField(ExpenseAdjustLineTemp.class, fieldName);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteImportData(String transactionID) {
        return tempService.delete(new EntityWrapper<ExpenseAdjustLineTemp>().eq("batch_number", transactionID));
    }

    @Transactional(rollbackFor = Exception.class)
    public BigDecimal confirmImport(String transactionID, Long headerId) {
        List<ExpenseAdjustLineTemp> expenseAdjustLinesTemps = tempService.selectList(
                new EntityWrapper<ExpenseAdjustLineTemp>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 0)
                        .orderBy("row_index",true));
        if (CollectionUtils.isEmpty(expenseAdjustLinesTemps)){
            return BigDecimal.ZERO;
        }
        ExpenseAdjustHeader header = headerService.selectById(headerId);
        if (null == header){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseAdjustLine> lines = new ArrayList<>();
        expenseAdjustLinesTemps.forEach(e -> {
            ExpenseAdjustLine line = new ExpenseAdjustLine();
            BeanUtils.copyProperties(e, line, "amount","id","createdDate","createdBy","lastUpdatedBy", "versionNumber", "lastUpdatedDate");
            line.setAmount(TypeConversionUtils.roundHalfUp(new BigDecimal(e.getAmount())));
            line.setCurrencyCode(header.getCurrencyCode());
            line.setExchangeRate(header.getExchangeRate());
            line.setAdjustDate(ZonedDateTime.now());
            line.setFunctionalAmount(TypeConversionUtils.roundHalfUp(line.getAmount().multiply(line.getExchangeRate())));
            lines.add(line);
        });
        this.insertBatch(lines);
        BigDecimal totalAmount = updateHeaderAmount(header);
        tempService.delete(new EntityWrapper<ExpenseAdjustLineTemp>().eq("batch_number", transactionID));
        return totalAmount;
    }

    public List<ExpenseAdjustLineWebDTO> listTempResult(String transactionNumber) {
        List<ExpenseAdjustLineTemp> expenseAdjustLinesTemps = tempService.listResult(transactionNumber);
        List<ExpenseAdjustLineWebDTO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(expenseAdjustLinesTemps)){
            return result;
        }
        Set<Long> dimensionSetId = new HashSet<>();
        expenseAdjustLinesTemps.stream().forEach(e -> {
            ExpenseAdjustLineWebDTO dto = new ExpenseAdjustLineWebDTO();
            BeanUtils.copyProperties(e, dto, "amount","id","createdDate","createdBy","lastUpdatedBy", "versionNumber", "lastUpdatedDate");
            dimensionSetId.addAll(DimensionUtils.getDimensionId(dto, ExpenseAdjustLineWebDTO.class));
            dto.setAmount(TypeConversionUtils.roundHalfUp(new BigDecimal(e.getAmount())));
            result.add(dto);
        });
        Map<Long, String> dimensionMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(dimensionSetId)){
            List<DimensionItemCO> dimension = organizationService.listDimensionItemsByIds(new ArrayList<>(dimensionSetId));
            if (!CollectionUtils.isEmpty(dimension)){
                dimensionMap = dimension.stream().collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1,k2)->k1));
            }
        }

        Map<Long, CompanyCO> comMap = organizationService.getCompanyMapByCompanyIds(result.stream().map(ExpenseAdjustLineWebDTO::getCompanyId).collect(Collectors.toList()));
        Map<Long, DepartmentCO> unitMap = organizationService.getDepartmentMapByDepartmentIds(result.stream().map(ExpenseAdjustLineWebDTO::getUnitId).collect(Collectors.toList()));

        for (ExpenseAdjustLineWebDTO dto : result){
            setDimensionsAndOtherInfo(dto, dimensionMap, comMap, unitMap);
        }
        tempService.delete(new EntityWrapper<ExpenseAdjustLineTemp>().eq("batch_number", transactionNumber));
        return result;
    }
}
