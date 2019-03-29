package com.hand.hcf.app.expense.application.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.domain.ApplicationLine;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.application.persistence.ApplicationLineMapper;
import com.hand.hcf.app.expense.application.web.dto.ApplicationLineWebDTO;
import com.hand.hcf.app.expense.common.domain.enums.DocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.CurrencyAmountDTO;
import com.hand.hcf.app.expense.common.dto.DocumentLineDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.TypeConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
@Service
public class ApplicationLineService extends BaseService<ApplicationLineMapper, ApplicationLine> {

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseFieldService expenseFieldService;



    /**
     * 根据行对象获取维度信息
     * @param dimensionList
     * @param applicationLine
     */
    public void getDimensionList(List<ExpenseDimension> dimensionList, ApplicationLine applicationLine){
        dimensionList.forEach(e -> {
            Field field = ReflectionUtils.findField(ApplicationLine.class, e.getDimensionField());
            if (field == null){
                throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
            }
            field.setAccessible(true);
            try {
                Object value = field.get(applicationLine);
                e.setValue(TypeConversionUtils.parseLong(value));
            } catch (IllegalAccessException e1) {
                throw new BizException(RespCode.EXPENSE_DIMENSION_DEFAULT_VALUE_IS_NULL);
            }
        });
    }

    /**
     * 创建行
     * @param header
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void createLine(ApplicationHeader header, ApplicationLineWebDTO dto) {
        ApplicationLine line = new ApplicationLine();

        ExpenseType expenseType = expenseTypeService.selectById(dto.getExpenseTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.EXPENSE_TYPE_NOT_FOUND_SOURCE_TYPE);
        }
        initApplicationLine(header, expenseType, line, dto);
        if (!CollectionUtils.isEmpty(dto.getDimensions())){
            DimensionUtils.setDimensionId(dto.getDimensions(), line, ApplicationLine.class, true);
        }
        this.insert(line);
        if (!CollectionUtils.isEmpty(dto.getFields())){
            saveField(line, header, dto.getFields(), expenseType,true);
        }
    }

    /**
     * 根据申请类型和单据头信息设置相关字段的值
     * @param header
     * @param expenseType
     * @param line
     * @param dto
     */
    private void initApplicationLine(ApplicationHeader header,
                                     ExpenseType expenseType,
                                     ApplicationLine line,
                                     ApplicationLineWebDTO dto){
        line.setClosedFlag(ClosedTypeEnum.NOT_CLOSED);
        line.setHeaderId(header.getId());
        line.setCompanyId(dto.getCompanyId());
        line.setDepartmentId(dto.getDepartmentId());
        line.setContractHeaderId(header.getContractHeaderId());
        line.setCurrencyCode(header.getCurrencyCode());
        line.setExchangeRate(header.getExchangeRate());
        line.setTenantId(header.getTenantId());
        line.setSetOfBooksId(header.getSetOfBooksId());
        line.setExpenseTypeId(expenseType.getId());
        line.setResponsibilityCenterId(dto.getResponsibilityCenterId());
        // 是否是单价输入
        if (expenseType.getEntryMode()){
            line.setPrice(dto.getPrice());
            line.setQuantity(dto.getQuantity());
            line.setPriceUnit(expenseType.getPriceUnit());
            if (dto.getQuantity() == null || dto.getPrice() == null){
                throw new BizException(RespCode.EXPENSE_APPLICATION_LINE_PRICE_IS_NULL);
            }
            line.setAmount(TypeConversionUtils.roundHalfUp(line.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()))));
        }else{
            line.setPrice(null);
            line.setPriceUnit(null);
            line.setQuantity(null);
            if (dto.getAmount() == null){
                throw new BizException(RespCode.EXPENSE_APPLICATION_LINE_AMOUNT_IS_NULL);
            }
            line.setAmount(dto.getAmount());
        }
        // 汇率日期 默认为单据头的创建日期
        line.setExchangeDate(header.getCreatedDate());
        line.setFunctionalAmount(TypeConversionUtils.roundHalfUp(line.getAmount().multiply(line.getExchangeRate())));
        line.setRequisitionDate(dto.getRequisitionDate());
        line.setRemarks(dto.getRemarks());
    }

    /**
     * 保存控件field
     * @param line
     * @param header
     * @param fields
     * @param isNew
     */
    private void saveField(ApplicationLine line,
                           ApplicationHeader header,
                           List<ExpenseFieldDTO> fields,
                           ExpenseType expenseType,
                           Boolean isNew){
        if(!isNew){
            // 编辑时先删除
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id",header.getId())
                    .eq("line_id", line.getId())
                    .eq("document_type", DocumentTypeEnum.EXP_REQUISITION));

        }
        List<ExpenseDocumentField> documentFields = adaptExpenseFields(fields, line, header, expenseType);
        documentFieldService.insertBatch(documentFields);
    }

    private List<ExpenseDocumentField> adaptExpenseFields(List<ExpenseFieldDTO> fields,
                                                          ApplicationLine line,
                                                          ApplicationHeader header,
                                                          ExpenseType expenseType){
        List<ExpenseDocumentField> documentFields = fields.stream().map(e -> {
            ExpenseDocumentField field = ExpenseDocumentField.builder()
                    .commonField(e.getCommonField())
                    .fieldDataType(e.getFieldDataType())
                    .fieldOid(e.getFieldOid())
                    .customEnumerationOid(e.getCustomEnumerationOid())
                    .defaultValueConfigurable(e.getDefaultValueConfigurable())
                    .defaultValueKey(e.getDefaultValueKey())
                    .defaultValueMode(e.getDefaultValueMode())
                    .documentType(DocumentTypeEnum.EXP_REQUISITION)
                    .editable(e.getEditable())
                    .expenseTypeId(expenseType.getId())
                    .headerId(header.getId())
                    .lineId(line.getId())
                    .mappedColumnId(e.getMappedColumnId())
                    .messageKey(e.getMessageKey())
                    .name(e.getName())
                    .printHide(e.getPrintHide())
                    .reportKey(e.getReportKey())
                    .required(e.getRequired())
                    .sequence(e.getSequence())
                    .showOnList(e.getShowOnList())
                    .value(e.getValue()).build();
            field.setId(null);
            field.setFieldType(e.getFieldType());
            return field;
        }).collect(Collectors.toList());
        return documentFields;
    }

    private List<ExpenseFieldDTO> adaptExpenseDocumentField(List<ExpenseDocumentField> fields){
        if (CollectionUtils.isEmpty(fields)){
            return new ArrayList<>();
        }else {
            List<ExpenseFieldDTO> expenseFieldDTOS = fields.stream().map(e -> {
                ExpenseFieldDTO fieldDTO = ExpenseFieldDTO.builder()
                        .commonField(e.getCommonField())
                        .customEnumerationOid(e.getCustomEnumerationOid())
                        .defaultValueConfigurable(e.getDefaultValueConfigurable())
                        .defaultValueKey(e.getDefaultValueKey())
                        .defaultValueMode(e.getDefaultValueMode())
                        .fieldDataType(e.getFieldDataType())
                        .fieldOid(e.getFieldOid())
                        .fieldType(e.getFieldType())
                        .mappedColumnId(e.getMappedColumnId())
                        .messageKey(e.getMessageKey())
                        .name(e.getName())
                        .id(e.getId())
                        .printHide(e.getPrintHide())
                        .reportKey(e.getReportKey())
                        .required(e.getRequired())
                        .sequence(e.getSequence())
                        .value(e.getValue())
                        .editable(e.getEditable())
                        .showOnList(e.getShowOnList())
                        .showValue(null)
                        .build();
                if (FieldType.CUSTOM_ENUMERATION.getId().equals(e.getFieldTypeId())){
                    List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(e.getCustomEnumerationOid());
                    // 为值列表，则设置值列表的相关值
                    List<OptionDTO> options = sysCodeValueCOS.stream().map(OptionDTO::createOption).collect(Collectors.toList());
                    fieldDTO.setOptions(options);
                }
                return fieldDTO;
            }).collect(Collectors.toList());
            return expenseFieldDTOS;
        }
    }

    public ApplicationLine getTotalAmount(Long headerId) {
        return baseMapper.getTotalAmount(headerId);
    }

    /**
     * 设置公司部门名称
     * @param dtos
     */
    public void setOtherInfo(List<ApplicationLineWebDTO> dtos) {
        if (!CollectionUtils.isEmpty(dtos)){
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            dtos.forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getDepartmentId());
            });

            // 查询公司
            List<CompanyCO> companies = organizationService.listCompaniesByIds(new ArrayList<>(companyIds));
            Map<Long, String> companyMap = companies
                    .stream()
                    .collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
            // 查询部门
            List<DepartmentCO> departments = organizationService.listDepartmentsByIds(new ArrayList<>(departmentIds));

            Map<Long, String> departmentMap = departments
                    .stream()
                    .collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));

            dtos
                    .forEach(e ->{
                        if (companyMap.containsKey(e.getCompanyId())){
                            e.setCompanyName(companyMap.get(e.getCompanyId()));
                        }
                        if (departmentMap.containsKey(e.getDepartmentId())){
                            e.setDepartmentName(departmentMap.get(e.getDepartmentId()));
                        }
                    });
        }

    }

    /**
     * 获取申请类型的fields
     * @param line
     * @return
     */
    public List<ExpenseFieldDTO> getFields(ApplicationLine line) {
        List<ExpenseDocumentField> expenseDocumentFields = documentFieldService.selectList(
                new EntityWrapper<ExpenseDocumentField>()
                        .eq("header_id", line.getHeaderId())
                        .eq("line_id", line.getId())
                        .eq("document_type", DocumentTypeEnum.EXP_REQUISITION)
                        .orderBy("sequence", true));
        return adaptExpenseDocumentField(expenseDocumentFields);
    }

    @Transactional(rollbackFor =  Exception.class)
    public void updateLine(ApplicationHeader header, ApplicationLineWebDTO dto) {
        ApplicationLine line = this.selectById(dto.getId());
        if (line == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        // 版本不一致
        if (!line.getVersionNumber().equals(dto.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_IS_ERROR);
        }
        ExpenseType expenseType = expenseTypeService.selectById(dto.getExpenseTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.EXPENSE_TYPE_NOT_FOUND_SOURCE_TYPE);
        }
        initApplicationLine(header, expenseType, line, dto);
        if (!CollectionUtils.isEmpty(dto.getDimensions())){
            DimensionUtils.setDimensionId(dto.getDimensions(), line, ApplicationLine.class, false);
        }
        this.updateById(line);
        if (!CollectionUtils.isEmpty(dto.getFields())){
            saveField(line, header, dto.getFields(), expenseType,false);
        }
    }

    /**
     * 根据单据头ID获取单据行信息
     * @param headerId
     * @param page
     * @return
     */
    public DocumentLineDTO<ApplicationLineWebDTO> getLinesByHeaderId(Long headerId, Page page) {
        List<ApplicationLineWebDTO> dtoList = baseMapper.getLinesByHeaderId(headerId, page);

        if (CollectionUtils.isEmpty(dtoList)){
            return new DocumentLineDTO<>();
        }else{
            setOtherInfo(dtoList);
            Set<Long> dimensionValueList = new HashSet<>();
            dtoList.forEach(e -> {
                List<Long> valueIdList = DimensionUtils.getDimensionId(e, ApplicationLineWebDTO.class);
                if (!CollectionUtils.isEmpty(valueIdList)){
                    dimensionValueList.addAll(valueIdList);
                }
                if(e.getResponsibilityCenterId() != null){
                   ResponsibilityCenterCO responsibilityCenterCO = organizationService.getResponsibilityCenterById(e.getResponsibilityCenterId());
                   if(responsibilityCenterCO != null) {
                       e.setResponsibilityCenterCodeName(responsibilityCenterCO.getResponsibilityCenterCodeName());
                   }
                }
            });
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(new ArrayList<>(dimensionValueList));

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));
            for (int i = 1; i <= dtoList.size(); i++) {
                ApplicationLineWebDTO dto = dtoList.get(i - 1);
                DimensionUtils.setDimensionCodeOrName("Name", dto, ApplicationLineWebDTO.class, valueMap);
                dto.setIndex((page.getCurrent() -1) * page.getSize() + i );
            }
            CurrencyAmountDTO currencyAndAmount = baseMapper.getCurrencyAndAmount(headerId);
            return new DocumentLineDTO<>(currencyAndAmount, dtoList);
        }
    }

    /**
     * 根据单据头ID获取单据行信息
     * @param headerId
     * @return
     */
    public List<ApplicationLine> getLinesByHeaderId(Long headerId) {
       return this.selectList(new EntityWrapper<ApplicationLine>().eq("header_id", headerId).orderBy("id",true));
    }


}
