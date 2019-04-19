package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.DimensionDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SyncLockPrefix;
import com.hand.hcf.app.expense.travel.domain.*;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationHeaderMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationHeaderWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhu.zhao
 * @date 2019/3/11
 */

@Service
@Slf4j
public class TravelApplicationHeaderService extends BaseService<TravelApplicationHeaderMapper, TravelApplicationHeader> {

    @Autowired
    private TravelApplicationTypeService typeService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ExpenseDimensionService expenseDimensionService;

    @Autowired
    private TravelApplicationLineService lineService;

    @Autowired
    private TravelApplicationLineDetailService lineDetailService;
    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private TravelHeaderAssociatePlaceService associatePlaceService;

    @Autowired
    private TravelAssociatePeopleService associatePeopleService;

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private WorkflowControllerImpl workflowClient;

    //@Value("${spring.application.name:}")
    private String applicationName;

    /**
     * 校验单据头信息
     *
     * @param dto                  单据dto
     * @param dimensions           单据类型所有维度
     */
    private void checkCreateHeader(TravelApplicationHeaderWebDTO dto,
                                   List<ExpenseDimension> dimensions) {}


    /**
     * 根据差旅申请单单据头信息生成差旅申请单头关联地点表数据和人员表数据
     *
     * @param dto                  单据dto
     */
    private void generateTravelHeaderAssociatePlaceAndPeople(TravelApplicationHeaderWebDTO dto) {
        if(dto.getId() != null){
            //关联地点表数据
            associatePlaceService.delete(new EntityWrapper<TravelHeaderAssociatePlace>()
                    .eq("requisition_header_id",dto.getId())
            );
            if(CollectionUtils.isNotEmpty(dto.getTravelFromPlaceDTOS())){
                dto.getTravelFromPlaceDTOS().forEach(e -> {
                    TravelHeaderAssociatePlace fromPlace =  new TravelHeaderAssociatePlace();
                    fromPlace.setPlaceType("F");
                    fromPlace.setPlaceId(e.getPlaceId());
                    fromPlace.setRequisitionHeaderId(dto.getId());
                    associatePlaceService.insert(fromPlace);
                });
            }
            if(CollectionUtils.isNotEmpty(dto.getTravelToPlaceDTOS())){
                dto.getTravelToPlaceDTOS().forEach(e -> {
                    TravelHeaderAssociatePlace toPlace =  new TravelHeaderAssociatePlace();
                    toPlace.setPlaceType("T");
                    toPlace.setPlaceId(e.getPlaceId());
                    toPlace.setRequisitionHeaderId(dto.getId());
                    associatePlaceService.insert(toPlace);
                });
            }
            //关联人员表数据
            associatePeopleService.delete(new EntityWrapper<TravelAssociatePeople>()
                    .eq("asso_type","H")
                    .eq("asso_pk_id",dto.getId())
            );
            if(CollectionUtils.isNotEmpty(dto.getTravelPeopleDTOList())) {
                dto.getTravelPeopleDTOList().stream().forEach(e -> {
                    TravelAssociatePeople travelAssociatePeople = new TravelAssociatePeople();
                    travelAssociatePeople.setAssoType("H");
                    travelAssociatePeople.setAssoPkId(dto.getId());
                    travelAssociatePeople.setComPeopleId(e.getEmployeeId());
                    associatePeopleService.insert(travelAssociatePeople);
                });

            }
        }


    }
    /**
     * 创建单据头
     *
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationHeader createHeader(TravelApplicationHeaderWebDTO dto) {
        // 先查询类型 和维度
        TravelApplicationTypeDimensionDTO applicationTypeDimensionDTO = typeService.queryTypeAndDimensionById(dto.getDocumentTypeId(), false);
        //Boolean contractRequireInput = applicationTypeDimensionDTO.getRequireInput();
        DepartmentCO units = organizationService.getDepartmentById(dto.getUnitId());
        if (units == null) {
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        List<ExpenseDimension> dimensions = applicationTypeDimensionDTO.getDimensions();
        //ContactCO userCO = organizationService.getUserById(dto.getEmployeeId());
        //dto.setApplicationOid(userCO != null ? userCO.getUserOid() : OrgInformationUtil.getCurrentUserOid().toString());

        checkCreateHeader(dto, dimensions);
        dto.setId(null);

//        if (!applicationTypeDimensionDTO.getAssociateContract()) {
//            dto.setContractHeaderId(null);
//        }
        // 设置默认值
        dto.setClosedFlag(ClosedTypeEnum.NOT_CLOSED);
        dto.setTotalAmount(BigDecimal.ZERO);
        dto.setFunctionalAmount(BigDecimal.ZERO);
        //dto.setBudgetStatus(false);
        dto.setTenantId(dto.getTenantId() != null ? dto.getTenantId() : OrgInformationUtil.getCurrentTenantId());
        dto.setSetOfBooksId(dto.getSetOfBooksId() != null ? dto.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
        // 取自单据类型，后续不可修改
        //dto.setRequireInput(contractRequireInput);
        //dto.setBudgetFlag(applicationTypeDimensionDTO.getBudgetFlag());
        //dto.setAssociateContract(applicationTypeDimensionDTO.getAssociateContract());
        dto.setEmployeeId(dto.getEmployeeId() != null ? dto.getEmployeeId() : OrgInformationUtil.getCurrentUserId());
        // 设置单据一些信息
        dto.setStatus(DocumentOperationEnum.GENERATE.getId());
        dto.setDocumentType(ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getKey());
        //dto.setFormOid(applicationTypeDimensionDTO.getFormOid());
        dto.setDocumentOid(UUID.randomUUID().toString());
        dto.setRequisitionNumber(commonService.getCoding(ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getCategory(), dto.getCompanyId(), null));
        TravelApplicationHeader travelApplicationHeader = new TravelApplicationHeader();
        BeanUtils.copyProperties(dto, travelApplicationHeader);
        //travelApplicationHeader.setDepartmentOid(units.getDepartmentOid().toString());
        this.insert(travelApplicationHeader);
        //生成差旅申请单头关联地点表数据和关联人员表数据
        dto.setId(travelApplicationHeader.getId());
        generateTravelHeaderAssociatePlaceAndPeople(dto);
        if (CollectionUtils.isNotEmpty(dimensions)) {
            dimensions.stream().forEach(e -> {
                e.setHeaderId(travelApplicationHeader.getId());
                e.setId(null);
                e.setDocumentType(ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getKey());
            });
            expenseDimensionService.insertBatch(dimensions);
        }
        return travelApplicationHeader;
    }

    private void setCompanyAndDepartmentAndEmployee(List<TravelApplicationHeaderWebDTO> headers,
                                                    boolean isSetEmployee) {
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            headers.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getUnitId());
                employeeIds.add(e.getEmployeeId());
                employeeIds.add(e.getCreatedBy());
                employeeIds.add(e.getOrderer());
            });
            // 查询公司
            Map<Long, CompanyCO> companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
            // 查询部门
            Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            // 查询员工
            Map<Long, ContactCO> usersMap = new HashMap<>(16);
            if (isSetEmployee) {
                usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));
            }

            Map<Long, ContactCO> finalUsersMap = usersMap;
            headers
                    .stream()
                    .forEach(e -> {
                        if (companyMap.containsKey(e.getCompanyId())) {
                            e.setCompanyName(companyMap.get(e.getCompanyId()).getName());
                        }
                        if (departmentMap.containsKey(e.getUnitId())) {
                            e.setDepartmentName(departmentMap.get(e.getUnitId()).getName());
                        }
                        if (isSetEmployee) {
                            if (finalUsersMap.containsKey(e.getEmployeeId())) {
                                e.setEmployeeName(finalUsersMap.get(e.getEmployeeId()).getFullName());
                            }
                            if (finalUsersMap.containsKey(e.getCreatedBy())) {
                                e.setCreatedName(finalUsersMap.get(e.getCreatedBy()).getFullName());
                            }
                            if (finalUsersMap.containsKey(e.getOrderer())) {
                                e.setOrderName(finalUsersMap.get(e.getOrderer()).getFullName());
                            }
                        }
                    });
        }
    }

    private void setAttachments(TravelApplicationHeaderWebDTO dto) {
        if (dto != null) {
            if (StringUtils.hasText(dto.getAttachmentOid())) {
                String[] strings = dto.getAttachmentOid().split(",");
                List<String> attachmentOidList = Arrays.asList(strings);
                List<AttachmentCO> attachments = organizationService.listAttachmentsByOids(attachmentOidList);
                dto.setAttachmentOidList(attachmentOidList);
                dto.setAttachments(attachments);
            }
        }
    }

    /**
     * 根据ID查询单据头信息含头的维度信息
     *
     * @param id
     * @return
     */
    public TravelApplicationHeaderWebDTO getHeaderInfoById(Long id) {
        TravelApplicationHeaderWebDTO travelApplicationHeaderWebDTO = baseMapper.getHeaderWebDTOById(id, 1);
        //设置typeName
        TravelApplicationType travelApplicationType = typeService.selectById(travelApplicationHeaderWebDTO.getDocumentTypeId());
        travelApplicationHeaderWebDTO.setTypeName(null != travelApplicationType ? travelApplicationType.getName() : null);
        // 编辑时设置保存的值，如果保存的值不存在，则为空，同时设置可以选到的维值
        commonService.setDimensionValueNameAndOptions(
                travelApplicationHeaderWebDTO.getDimensions(),
                travelApplicationHeaderWebDTO.getCompanyId(),
                travelApplicationHeaderWebDTO.getUnitId(),
                travelApplicationHeaderWebDTO.getEmployeeId()
        );
        setCompanyAndDepartmentAndEmployee(Arrays.asList(travelApplicationHeaderWebDTO), true);
        travelApplicationHeaderWebDTO.setTravelPeopleDTOList(associatePeopleService.listTravelPeopleByAssoPkIdAndPosition(travelApplicationHeaderWebDTO.getId(), "H"));
        travelApplicationHeaderWebDTO.setTravelFromPlaceDTOS(associatePlaceService.listTravelFromPlaceByTypeAndId(travelApplicationHeaderWebDTO.getId(),"F"));
        travelApplicationHeaderWebDTO.setTravelToPlaceDTOS(associatePlaceService.listTravelFromPlaceByTypeAndId(travelApplicationHeaderWebDTO.getId(),"T"));
        setAttachments(travelApplicationHeaderWebDTO);
        return travelApplicationHeaderWebDTO;
    }

    public List<TravelApplicationHeaderWebDTO> listHeaderDTOsByCondition(Page page,
                                                                   String documentNumber,
                                                                   Long typeId,
                                                                   ZonedDateTime requisitionDateFrom,
                                                                   ZonedDateTime requisitionDateTo,
                                                                   BigDecimal amountFrom,
                                                                   BigDecimal amountTo,
                                                                   Integer status,
                                                                   String currencyCode,
                                                                   String remarks,
                                                                   Long employeeId) {
        Wrapper<TravelApplicationHeader> wrapper = new EntityWrapper<TravelApplicationHeader>()
                .eq(typeId != null, "t.document_type_id", typeId)
                .eq(status != null, "t.status", status)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(amountFrom != null, "t.total_amount", amountFrom)
                .le(amountTo != null, "t.total_amount", amountTo)
                .ge(requisitionDateFrom != null, "t.requisition_date", requisitionDateFrom)
                .lt(requisitionDateTo != null, "t.requisition_date", requisitionDateTo)
                .like(StringUtils.hasText(documentNumber), "t.requisition_number", documentNumber)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.description", remarks)
                .orderBy("t.id", false);
        List<TravelApplicationHeaderWebDTO> headers = baseMapper.listByCondition(page, wrapper, OrgInformationUtil.getCurrentUserId());
        setCompanyAndDepartmentAndEmployee(headers, true);
        return headers;
    }

    /**
     * 校验单据状态
     *
     * @param operateType 操作类型
     * @param status      单据状态
     */
    private void checkDocumentStatus(Integer operateType, Integer status) {
        switch (operateType) {
            //点击删除
            case -1:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            //更改
            case 0:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())
                        && !status.equals(DocumentOperationEnum.APPROVAL_PASS.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 提交 至审核中
            case 1002:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())
                        && !status.equals(DocumentOperationEnum.HOLD.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 审核
            case 1004:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;

            // 撤回
            case 1003:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 审批驳回
            case 1005:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            default:
                break;
        }
    }

    private void checkUpdateHeader(TravelApplicationHeader queryData,
                                   TravelApplicationHeaderWebDTO dto,
                                   List<ExpenseDimension> dimensions) {
        // 如果维度不为空 将前端的维度值赋值给查询出来的
        if (!CollectionUtils.isEmpty(dimensions)) {
            List<ExpenseDimension> dtoDimensions = dto.getDimensions();
            if (dimensions.size() != dtoDimensions.size()) {
                throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
            }
            Map<Long, ExpenseDimension> valueMap = dtoDimensions
                    .stream()
                    .collect(Collectors.toMap(ExpenseDimension::getDimensionId, e -> e));
            dimensions.forEach(e -> {
                e.setValue(valueMap.get(e.getDimensionId()).getValue());
            });
        }
        // 币种不可以修改
        // 设置传过来的公司，部门 备注、附件
        queryData.setCompanyId(dto.getCompanyId());
        queryData.setUnitId(dto.getUnitId());
        queryData.setAttachmentOid(dto.getAttachmentOid());
        queryData.setDescription(dto.getDescription());
    }

    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationHeader updateHeader(TravelApplicationHeaderWebDTO dto) {
        // 先查询。然后将前端的值赋值给查询出来的，再更新
        if (dto.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        TravelApplicationHeader queryData = this.selectById(dto.getId());
        // 版本不一致
        if (!queryData.getVersionNumber().equals(dto.getVersionNumber())) {
            throw new BizException(RespCode.SYS_VERSION_IS_ERROR);
        }
        checkDocumentStatus(0, queryData.getStatus());
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(dto.getId(), dto.getDocumentType(), true);
        checkUpdateHeader(queryData, dto, dimensions);
        DepartmentCO units = organizationService.getDepartmentById(dto.getUnitId());
        if (units == null) {
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        //queryData.setDepartmentOid(units.getDepartmentOid().toString());
        this.updateById(queryData);
        generateTravelHeaderAssociatePlaceAndPeople(dto);
        if (!CollectionUtils.isEmpty(dimensions)) {
            expenseDimensionService.updateBatchById(dimensions);
        }
        return queryData;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteHeader(Long id) {

        TravelApplicationHeader travelApplicationHeader = this.selectById(id);
        if (null == travelApplicationHeader) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        checkDocumentStatus(-1, travelApplicationHeader.getStatus());

        //先删除明细行再删除行
        lineDetailService.delete(new EntityWrapper<TravelApplicationLineDetail>()
                .where("requisition_line_id in\n" +
                "          (select l.id\n" +
                "             from exp_travel_app_line l\n" +
                "            where l.requisition_header_id = " + id + ")")
        );
        //删除行关联人员
        associatePeopleService.delete(new EntityWrapper<TravelAssociatePeople>()
                .where("asso_type = 'L' and  asso_pk_id in\n" +
                        "          (select l.id\n" +
                        "             from exp_travel_app_line l\n" +
                        "            where l.requisition_header_id = " + id + ")")
        );
        // 删除行
        lineService.delete(new EntityWrapper<TravelApplicationLine>().eq("requisition_header_id", id));
        // 删除field
        documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                .eq("header_id", id)
                .eq("document_type", travelApplicationHeader.getDocumentType()));
        // 删除维度
        expenseDimensionService.delete(new EntityWrapper<ExpenseDimension>()
                .eq("header_id", id)
                .eq("document_type", travelApplicationHeader.getDocumentType()));
        // 判断是否存在附件
        if (StringUtils.hasText(travelApplicationHeader.getAttachmentOid())) {
            String[] strings = travelApplicationHeader.getAttachmentOid().split(",");
            organizationService.deleteAttachmentsByOids(Arrays.asList(strings));
        }
        //删除头关联地点(出发地和目的地)
        associatePlaceService.delete(new EntityWrapper<TravelHeaderAssociatePlace>()
                .eq("requisition_header_id",id)
        );
        //删除头出行人员
        associatePeopleService.delete(new EntityWrapper<TravelAssociatePeople>()
                        .eq("asso_type","H")
                        .eq("asso_pk_id",id)
        );
        // 删除头信息
        this.deleteById(id);
        return true;
    }
    /**
     * 创建/更新单据行时查询维度、field信息
     *
     * @param headerId
     * @param id
     * @param isNew
     * @return
     */
    public TravelApplicationLineWebDTO queryLineInfo(Long headerId, Long id, Boolean isNew) {
        TravelApplicationLineWebDTO lineDto = new TravelApplicationLineWebDTO();
        TravelApplicationHeaderWebDTO headerDTO = baseMapper.getHeaderWebDTOById(headerId, null);
        List<ExpenseDimension> dimensions = headerDTO.getDimensions();
        if (isNew) {
            setCompanyAndDepartmentAndEmployee(Arrays.asList(headerDTO), false);
            lineDto.setCompanyName(headerDTO.getCompanyName());
            lineDto.setCompanyId(headerDTO.getCompanyId());
            lineDto.setDepartmentName(headerDTO.getDepartmentName());
            lineDto.setUnitId(headerDTO.getUnitId());
            lineDto.setFields(new ArrayList<>());
            lineDto.setChildren(new ArrayList<>());
        } else {
            if (id == null) {
                throw new BizException(RespCode.SYS_ID_IS_NULL);
            }
            TravelApplicationLine line = lineService.selectById(id);
            BeanUtils.copyProperties(line, lineDto);
            lineService.setOtherInfo(Arrays.asList(lineDto));
            lineService.getDimensionList(dimensions, line);
            ExpenseType expenseType = expenseTypeService.selectById(line.getRequisitonTypeId());
            lineDto.setExpenseTypeName(null != expenseType ? expenseType.getName() : null);
            List<ExpenseFieldDTO> fields = lineService.getFields(line);
            lineDto.setFields(fields);
        }
        commonService.setDimensionValueNameAndOptions(dimensions,
                isNew || lineDto.getCompanyId() == null ? headerDTO.getCompanyId() : lineDto.getCompanyId(),
                isNew || lineDto.getUnitId() == null  ? headerDTO.getUnitId() : lineDto.getUnitId(),
                headerDTO.getEmployeeId()
        );
        lineDto.setDimensions(dimensions);
        lineDto.setTravelPeopleDTOList(associatePeopleService.listTravelPeopleByAssoPkIdAndPosition(lineDto.getId(), "L"));
        return lineDto;
    }

    private TravelApplicationHeader checkLineInfo(TravelApplicationLineWebDTO dto) {
        if (dto.getRequisitionHeaderId() == null) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_ID_IS_NULL);
        }
        TravelApplicationHeader header = this.selectById(dto.getRequisitionHeaderId());
        if (header == null) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_ID_IS_NULL);
        }
        checkDocumentStatus(0, header.getStatus());
        return header;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createLine(TravelApplicationLineWebDTO dto) {
        TravelApplicationHeader header = checkLineInfo(dto);
        // 创建行
        lineService.createLine(header, dto);
        // 更新头金额
        //updateHeaderAmount(header);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLine(TravelApplicationLineWebDTO dto) {
        TravelApplicationHeader header = checkLineInfo(dto);
        // 更新行
        lineService.updateLine(header, dto);
        // 更新头金额
        //updateHeaderAmount(header);
        return true;
    }

    /**
     * 根据ID删除单据行
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLineByLineId(Long id) {
        TravelApplicationLine line = lineService.selectById(id);
        if (line == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
       TravelApplicationHeader applicationHeader = this.selectById(line.getRequisitionHeaderId());
        checkDocumentStatus(-1, applicationHeader.getStatus());
        //先删除明细行再删除行
        lineDetailService.delete(new EntityWrapper<TravelApplicationLineDetail>().eq("requisition_line_id",id));
        //删除行
        lineService.deleteById(id);
        // 删除fields
        documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                .eq("header_id", applicationHeader.getId())
                .eq("line_id", line.getId())
                .eq("document_type", applicationHeader.getDocumentType()));
        //updateHeaderAmount(applicationHeader);
        return true;
    }

    /**
     * 根据ID查询单据头详情
     *
     * @param id 单据头id
     * @return
     */
    public TravelApplicationHeaderWebDTO getHeaderDetailInfo(Long id) {

        TravelApplicationHeaderWebDTO dto = baseMapper.getHeaderWebDTOById(id, 1);
        if (dto == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        //设置typeName
        TravelApplicationType applicationType = typeService.selectById(dto.getDocumentTypeId());
        dto.setTypeName(null != applicationType ? applicationType.getName() : null);
        commonService.setDimensionValueName(dto.getDimensions(),dto.getCompanyId(),dto.getUnitId(),dto.getEmployeeId());
        setCompanyAndDepartmentAndEmployee(Arrays.asList(dto), true);
        dto.setTravelPeopleDTOList(associatePeopleService.listTravelPeopleByAssoPkIdAndPosition(dto.getId(), "H"));
        setAttachments(dto);
        return dto;
    }

    public List<TravelApplicationLineWebDTO> getLinesByHeaderId(Long id, Page page) {
        return lineService.getLinesByHeaderId(id, page);
    }

    public List<DimensionDTO> queryDimensionColumn(Long id) {
        List<ExpenseDimension> dimensions = expenseDimensionService.listDimensionByHeaderIdAndType(id, ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getKey(), null);
        if (org.springframework.util.CollectionUtils.isEmpty(dimensions)) {
            return new ArrayList<>();
        }else{
            List<Long> ids = dimensions.stream().map(ExpenseDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionCO> dimensionCOS = organizationService.listDimensionsByIds(ids);
            Map<Long, String> map = dimensionCOS.stream().collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName));
            return dimensions.stream().map(e -> {
                DimensionDTO dto = new DimensionDTO();
                dto.setTitle(map.get(e.getDimensionId()));
                String[] split = e.getDimensionField().split("Id");
                dto.setDataIndex(split[0] + "Name");
                return dto;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 提交数据发送工作流模块
     *
     * @param header              单据头信息
     * @param type                单据类型
     * @param workFlowDocumentRef 提交信息
     */
    private void sendWorkflow(TravelApplicationHeader header,
                              TravelApplicationType type,
                              WorkFlowDocumentRefCO workFlowDocumentRef) {
        String documentOidStr = header.getDocumentOid();
        UUID documentOid = documentOidStr != null ? UUID.fromString(documentOidStr) : null;
        //ID转成OID传给工作流
        ContactCO contactCO = organizationService.getUserById(header.getEmployeeId());
        UUID applicantOid = contactCO != null ? UUID.fromString(contactCO.getUserOid()) : null;
        DepartmentCO departmentCO = organizationService.getDepartmentById(header.getUnitId());
        UUID unitOid = departmentCO != null ? departmentCO.getDepartmentOid() : null;
        ApprovalFormCO approvalFormCO = organizationService.getApprovalFormById(type.getFormId());
        UUID formOid = approvalFormCO != null ? approvalFormCO.getFormOid() : null;

        // 设置调用提交工作流方法的参数
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(header.getId()); // 单据id
        submitData.setDocumentOid(documentOid); // 单据oid
        submitData.setDocumentNumber(header.getRequisitionNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getKey()); // 单据类别
        submitData.setDocumentTypeId(type.getId()); // 单据类型id
        submitData.setDocumentTypeCode(type.getCode()); // 单据类型代码
        submitData.setDocumentTypeName(type.getName()); // 单据类型名称
        submitData.setCurrencyCode(header.getCurrencyCode()); // 币种
        submitData.setAmount(header.getTotalAmount()); // 原币金额
        submitData.setFunctionAmount(header.getFunctionalAmount()); // 本币金额
        submitData.setCompanyId(header.getCompanyId()); // 公司id
        submitData.setUnitOid(unitOid); // 部门oid
        submitData.setApplicantOid(applicantOid); // 申请人oid
        submitData.setApplicantDate(header.getCreatedDate()); // 申请日期
        submitData.setRemark(header.getDescription()); // 备注
        submitData.setSubmittedBy(OrgInformationUtil.getCurrentUserOid()); // 提交人
        submitData.setFormOid(formOid); // 表单oid
        submitData.setDestinationService(applicationName); // 注册到Eureka中的名称

        // 调用工作流的三方接口进行提交
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())){
            Integer approvalStatus = submitResult.getStatus();

            if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                // 单据审批中
                updateById(header);
            } else {
                // 单据通过/驳回
                updateDocumentStatus(header.getId(), approvalStatus, "");
            }
        } else {
            throw new BizException(submitResult.getError());
        }
    }

    /**
     * 校验行程行的时间、地点是否在单据头的时间、地点范围内
     * @param header
     */
    private void checkTimeAndLocation(TravelApplicationHeader header) {
        TravelApplicationType travelApplicationType = typeService.selectById(header.getDocumentTypeId());
        if (travelApplicationType == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        if (travelApplicationType.getRoute()) {
            //审批前提交，无需校验
            return;
        } else {
            //todo 校验行程行
        }
    }

    /**
     * 差旅申请单提交
     */
    @Transactional(rollbackFor = Exception.class)
    //@LcnTransaction
   //@SyncLock(lockPrefix = SyncLockPrefix.EXP_APPLICATION)
    public Boolean submit( WorkFlowDocumentRefCO workFlowDocumentRef) {
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(workFlowDocumentRef.getDocumentId());

        long start = System.currentTimeMillis();
        TravelApplicationHeader header = this.selectById(workFlowDocumentRef.getDocumentId());
        if (header == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        checkTimeAndLocation(header);
        //校验状态
        checkDocumentStatus(DocumentOperationEnum.APPROVAL.getId(), header.getStatus());
        // 提交设置申请日期为当前日期
        header.setRequisitionDate(ZonedDateTime.now());
        header.setStatus(DocumentOperationEnum.APPROVAL.getId());
        // 只有提交时才把form_oid 更新为单据类型新的form_oid, 预算管控标识也更新
        TravelApplicationType applicationType = typeService.selectById(header.getDocumentTypeId());

        sendWorkflow(header, applicationType, workFlowDocumentRef);
        log.info("申请单整体提交,耗时:{}ms", System.currentTimeMillis() - start);
        return true;
    }

    /**
     * 给指定单据加上排他锁
     *
     * @param documentId 单据id
     */
    public void lockByDocumentId(Long documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId null");
        }

        EntityWrapper<TravelApplicationHeader> wrapper = new EntityWrapper<TravelApplicationHeader>();
        wrapper.eq("id", documentId);
        updateForSet("version_number = version_number", wrapper);
    }

    public TravelApplicationLineDetail updateLineDetail(TravelApplicationLineDetail newLineDetail) {
        if (newLineDetail.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        TravelApplicationLineDetail oldLineDetail = lineDetailService.selectById(newLineDetail.getId());
        if (oldLineDetail == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        oldLineDetail.setCompanyId(newLineDetail.getCompanyId());
        oldLineDetail.setUnitId(newLineDetail.getUnitId());
        lineDetailService.updateById(oldLineDetail);
        return oldLineDetail;
    }

    /**
     * @param headerId     申请单头ID
     * @param status       状态
     * @param approvalText 审批意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatus(Long headerId, Integer status, String approvalText) {
        TravelApplicationHeader header = this.selectById(headerId);
        if (header == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }

        Long documentId = header.getId();
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(documentId);
        // 这里重新通过id获取单据是为了保证接下来修改的是最新版本记录
        header = selectById(documentId);

        header.setStatus(status);
        // 保存
        this.updateById(header);
        if (DocumentOperationEnum.WITHDRAW.getId().equals(status) || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)) {
            //审批拒绝  撤回 如果有预算需要释放，
//            rollBackBudget(headerId);
        }
    }
}
