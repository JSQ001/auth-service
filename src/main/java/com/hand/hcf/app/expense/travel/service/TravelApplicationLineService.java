package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.domain.ApplicationLine;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationHeader;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLine;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import com.hand.hcf.app.expense.travel.domain.TravelAssociatePeople;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationLineMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineDetailWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelPeopleDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
public class TravelApplicationLineService extends BaseService<TravelApplicationLineMapper, TravelApplicationLine> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private TravelApplicationLineDetailService lineDetailService;

    @Autowired
    private TravelAssociatePeopleService associatePeopleService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private TravelApplicationHeaderService headerService;



    /**
     * 根据行对象获取维度信息
     * @param dimensionList
     * @param travelApplicationLine
     */
    public void getDimensionList(List<ExpenseDimension> dimensionList, TravelApplicationLine travelApplicationLine){
        dimensionList.forEach(e -> {
            Field field = ReflectionUtils.findField(ApplicationLine.class, e.getDimensionField());
            if (field == null){
                throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
            }
            field.setAccessible(true);
            try {
                Object value = field.get(travelApplicationLine);
                e.setValue(TypeConversionUtils.parseLong(value));
            } catch (IllegalAccessException e1) {
                throw new BizException(RespCode.EXPENSE_DIMENSION_DEFAULT_VALUE_IS_NULL);
            }
        });
    }

    /**
     * 设置公司部门名称
     * @param dtos
     */
    public void setOtherInfo(List<TravelApplicationLineWebDTO> dtos) {
        if (!CollectionUtils.isEmpty(dtos)){
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            dtos.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getUnitId());
                employeeIds.add(e.getBookerId());
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

            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));

            dtos
                    .stream()
                    .forEach(e ->{
                        if (companyMap.containsKey(e.getCompanyId())){
                            e.setCompanyName(companyMap.get(e.getCompanyId()));
                        }
                        if (departmentMap.containsKey(e.getUnitId())){
                            e.setDepartmentName(departmentMap.get(e.getUnitId()));
                        }
                        if (usersMap.containsKey(e.getBookerId())) {
                            e.setBookerName(usersMap.get(e.getBookerId()).getFullName());
                        }
                        if(e.getResponsibilityCenterId() != null){
                           ResponsibilityCenterCO responsibilityCenterCO = organizationService
                                   .getResponsibilityCenterById(e.getResponsibilityCenterId());
                           if(responsibilityCenterCO != null) {
                               e.setResponsibilityCenterCodeName(responsibilityCenterCO.getResponsibilityCenterCodeName());
                           }
                        }
                    });
        }
    }

    private List<ExpenseFieldDTO> adaptExpenseDocumentField(List<ExpenseDocumentField> fields){
        if (org.springframework.util.CollectionUtils.isEmpty(fields)){
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

    /**
     * 获取申请类型的fields
     * @param line
     * @return
     */
    public List<ExpenseFieldDTO> getFields(TravelApplicationLine line) {
        List<ExpenseDocumentField> expenseDocumentFields = documentFieldService.selectList(
                new EntityWrapper<ExpenseDocumentField>()
                        .eq("header_id", line.getRequisitionHeaderId())
                        .eq("line_id", line.getId())
                        .eq("document_type", ExpenseDocumentTypeEnum.TRAVEL_APPLICATION)
                        .orderBy("sequence", true));
        return adaptExpenseDocumentField(expenseDocumentFields);
    }

    /**
     * 根据申请类型和单据头信息设置相关字段的值
     * @param header
     * @param expenseType
     * @param line
     * @param dto
     */
    private void initApplicationLine(TravelApplicationHeader header,
                                     ExpenseType expenseType,
                                     TravelApplicationLine line,
                                     TravelApplicationLineWebDTO dto){
        line.setClosedFlag(ClosedTypeEnum.NOT_CLOSED);
        line.setRequisitionHeaderId(header.getId());
        line.setCompanyId(dto.getCompanyId());
        line.setUnitId(dto.getUnitId());
        line.setTenantId(header.getTenantId());
        line.setSetOfBooksId(header.getSetOfBooksId());
        line.setRequisitonTypeId(expenseType.getId());
        line.setRequisitionDate(dto.getRequisitionDate());
        line.setDescription(dto.getDescription());
        line.setResponsibilityCenterId(dto.getResponsibilityCenterId());
        //订票状态-未订票
        line.setUseFlag("N");
        if ("1".equals(header.getOrderMode())) {
            line.setBookerId(header.getOrderer());
        }
    }

    private List<ExpenseDocumentField> adaptExpenseFields(List<ExpenseFieldDTO> fields,
                                                          TravelApplicationLine line,
                                                          TravelApplicationHeader header,
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
                    .documentType(ExpenseDocumentTypeEnum.TRAVEL_APPLICATION)
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

    /**
     * 保存控件field
     * @param line
     * @param header
     * @param fields
     * @param isNew
     */
    private void saveField(TravelApplicationLine line,
                           TravelApplicationHeader header,
                           List<ExpenseFieldDTO> fields,
                           ExpenseType expenseType,
                           Boolean isNew){
        if(!isNew){
            // 编辑时先删除
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id",header.getId())
                    .eq("line_id", line.getId())
                    .eq("document_type", ExpenseDocumentTypeEnum.TRAVEL_APPLICATION));

        }
        List<ExpenseDocumentField> documentFields = adaptExpenseFields(fields, line, header, expenseType);
        documentFieldService.insertBatch(documentFields);
    }

    /**
     * 根据差旅申请单单据行信息生成差旅申请单行关联人员表数据
     *
     * @param dto                  单据dto
     */
    private void generateTravelLineAssociatePeople(TravelApplicationLineWebDTO dto) {
        if(dto.getId() != null) {
            //关联人员表数据
            associatePeopleService.delete(new EntityWrapper<TravelAssociatePeople>()
                    .eq("asso_type","L")
                    .eq("asso_pk_id",dto.getId())
            );
            if(CollectionUtils.isNotEmpty(dto.getTravelPeopleDTOList())) {
                dto.getTravelPeopleDTOList().stream().forEach(e -> {
                    TravelAssociatePeople travelAssociatePeople = new TravelAssociatePeople();
                    travelAssociatePeople.setAssoType("L");
                    travelAssociatePeople.setAssoPkId(dto.getId());
                    travelAssociatePeople.setComPeopleId(e.getEmployeeId());
                    associatePeopleService.insert(travelAssociatePeople);
                });

            }
        }
    }
    /**
     * 创建行
     * @param header
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void createLine(TravelApplicationHeader header, TravelApplicationLineWebDTO dto) {
        TravelApplicationLine line = new TravelApplicationLine();

        ExpenseType expenseType = expenseTypeService.selectById(dto.getRequisitonTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.EXPENSE_TYPE_NOT_FOUND_SOURCE_TYPE);
        }
        initApplicationLine(header, expenseType, line, dto);
        if (!CollectionUtils.isEmpty(dto.getDimensions())){
            DimensionUtils.setDimensionId(dto.getDimensions(), line, ApplicationLine.class, true);
        }
        this.insert(line);
        dto.setId(line.getId());
        generateTravelLineAssociatePeople(dto);
        if (!CollectionUtils.isEmpty(dto.getFields())){
            saveField(line, header, dto.getFields(), expenseType,true);
        }

        //插入明细行
        createTravelLineDetailsBatch(line);
    }

    @Transactional(rollbackFor =  Exception.class)
    public void updateLine(TravelApplicationHeader header, TravelApplicationLineWebDTO dto) {
        TravelApplicationLine line = this.selectById(dto.getId());
        if (line == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        // 版本不一致
        if (!line.getVersionNumber().equals(dto.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_IS_ERROR);
        }
        ExpenseType expenseType = expenseTypeService.selectById(dto.getRequisitonTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.EXPENSE_TYPE_NOT_FOUND_SOURCE_TYPE);
        }
        initApplicationLine(header, expenseType, line, dto);
        if (!CollectionUtils.isEmpty(dto.getDimensions())){
            DimensionUtils.setDimensionId(dto.getDimensions(), line, ApplicationLine.class, false);
        }
        this.updateById(line);
        generateTravelLineAssociatePeople(dto);
        if (!CollectionUtils.isEmpty(dto.getFields())){
            saveField(line, header, dto.getFields(), expenseType,false);
        }

        //更新明细行
        deleteTravelLineDetailsBatch(line);
        createTravelLineDetailsBatch(line);
    }

    /**
     * 批量创建明细行
     * @param line
     */
    private void createTravelLineDetailsBatch(TravelApplicationLine line) {
        TravelApplicationHeader header = headerService.selectById(line.getRequisitionHeaderId());

        List<Long> userList = associatePeopleService.selectList(
                new EntityWrapper<TravelAssociatePeople>()
                        .eq("asso_type","L")
                        .eq(line.getId() != null,"asso_pk_id", line.getId())
        ).stream().map(TravelAssociatePeople::getComPeopleId).distinct().filter(Objects::nonNull).collect(Collectors.toList());

        List<TravelApplicationLineDetail> detailList = new ArrayList<>();
        userList.stream().forEach(id -> {
            TravelApplicationLineDetail detail = mapperFacade.map(line, TravelApplicationLineDetail.class);
            detail.setRequisitionLineId(line.getId());
            detail.setId(null);
            detail.setComPeopleId(id);
            detail.setDescription(null);
            if ("1".equals(header.getOrderMode())) {
                //统一订票
                detail.setBookerId(header.getOrderer());
            } else if ("2".equals(header.getOrderMode())) {
                //分别订票
                detail.setBookerId(id);
            }
            //行程行公司和部门为空时，行程明细行默认为出行人所在公司和部门
            if (detail.getCompanyId() == null || detail.getUnitId() == null) {
                ContactCO contactCO = organizationService.getUserById(detail.getComPeopleId());
                DepartmentCO departmentCO = organizationService.getDepartementCOByUserOid(contactCO.getUserOid());
                if (contactCO == null || departmentCO == null) {
                    throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
                }
                if (detail.getCompanyId() == null) {
                    detail.setCompanyId(contactCO.getCompanyId());
                }
                if (detail.getUnitId() == null) {

                    detail.setUnitId(departmentCO.getId());
                }
            }
            detailList.add(detail);
        });

        lineDetailService.insertBatch(detailList);
    }

    /**
     * 批量删除明细行
     * @param line
     */
    private void deleteTravelLineDetailsBatch(TravelApplicationLine line) {
        lineDetailService.delete(
                new EntityWrapper<TravelApplicationLineDetail>()
                        .eq(line.getId() != null,"requisition_line_id", line.getId())
        );

    }

    /**
     * 设置公司部门名称
     * @param dtos
     */
    public void setChildren(List<TravelApplicationLineWebDTO> dtos) {
        if (!CollectionUtils.isEmpty(dtos)) {
            dtos.forEach(e -> {
                List<TravelApplicationLineDetailWebDTO> detailWebDTOS = lineDetailService.getDetailsByLineId(e.getId());
                for (TravelApplicationLineDetailWebDTO dto: detailWebDTOS) {
                    if(dto.getResponsibilityCenterId() != null){
                        ResponsibilityCenterCO responsibilityCenterCO = organizationService
                                .getResponsibilityCenterById(e.getResponsibilityCenterId());
                        if(responsibilityCenterCO != null) {
                            dto.setResponsibilityCenterCodeName(responsibilityCenterCO.getResponsibilityCenterCodeName());
                        }
                    }
                    dto.setExpenseTypeName(e.getExpenseTypeName());
                }
                e.setChildren(detailWebDTOS);
                }
            );
        }
    }
    /**
     * 根据单据头ID获取单据行信息
     * @param headerId
     * @param page
     * @return
     */
    public List<TravelApplicationLineWebDTO> getLinesByHeaderId(Long headerId, Page page) {
        List<TravelApplicationLineWebDTO> dtoList = baseMapper.getLinesByHeaderId(headerId, OrgInformationUtil.getCurrentUserId(), page);

        if (CollectionUtils.isEmpty(dtoList)){
            return new ArrayList<>();
        }else{
            setChildren(dtoList);
            setOtherInfo(dtoList);
            Set<Long> dimensionValueList = new HashSet<>();
            dtoList.forEach(e -> {
                List<Long> valueIdList = DimensionUtils.getDimensionId(e, TravelApplicationLineWebDTO.class);
                if (!CollectionUtils.isEmpty(valueIdList)){
                    dimensionValueList.addAll(valueIdList);
                }
            });
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(new ArrayList<>(dimensionValueList));

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));
            for (int i = 1; i <= dtoList.size(); i++) {
                TravelApplicationLineWebDTO dto = dtoList.get(i - 1);
                DimensionUtils.setDimensionCodeOrName("Name", dto, TravelApplicationLineWebDTO.class, valueMap);
                dto.setTravelPeopleDTOList(associatePeopleService.listTravelPeopleByAssoPkIdAndPosition(dto.getId(), "L"));
                dto.setTravelPeopleStr(dto.getTravelPeopleDTOList().stream().map(TravelPeopleDTO::getEmployeeName).collect(Collectors.joining("、")));
                dto.setIndex((page.getCurrent() -1) * page.getSize() + i );
            }
        }
        return dtoList;
    }
}
