package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLine;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustHeaderMapper;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustDimensionDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustDimensionItemDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustHeaderWebDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustTypeWebDTO;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalResultCO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 *  费用调整单单据头服务
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5
 */
@Service
public class ExpenseAdjustHeaderService extends BaseService<ExpenseAdjustHeaderMapper, ExpenseAdjustHeader> {
    private static final Logger log = LoggerFactory.getLogger(ExpenseAdjustHeaderService.class);
    @Autowired
    private ExpenseAdjustTypeService expenseAdjustTypeService;

    @Autowired
    private CommonService commonService;
    @Autowired
    private ExpenseDimensionService dimensionService;
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseAdjustLineService expenseAdjustLineService;
    @Autowired
    private WorkflowControllerImpl workflowClient;
    @Autowired
    private WorkflowControllerImpl workflowInterface;

    @Value("${spring.application.name:}")
    private  String applicationName;
    /**
     * 费用调整单提交
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public Boolean submit(WorkFlowDocumentRefCO workFlowDocumentRef) {
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(workFlowDocumentRef.getDocumentId());

        ExpenseAdjustHeader head = this.selectById(workFlowDocumentRef.getDocumentId());
        //校验状态
       checkDocumentStatus(1002,head.getStatus());
        List<ExpenseAdjustLine> lines = expenseAdjustLineService.listExpenseAdjustLinesByHeaderId(workFlowDocumentRef.getDocumentId());
        //校验：费用调整单行是空不能提交
        if(lines == null || lines.size() == 0){
            throw new BizException(RespCode.EXPENSE_APPLICATION_LINE_IS_NULL);
        }
        List<BigDecimal> amounts = lines.stream().map(ExpenseAdjustLine::getFunctionalAmount).collect(Collectors.toList());
        BigDecimal lineAmount = BigDecimal.ZERO;
        for (BigDecimal a : amounts) {
            lineAmount = a.add(lineAmount);
        }
        ExpenseAdjustType type = expenseAdjustTypeService.getExpenseAdjustTypeById(workFlowDocumentRef.getDocumentTypeId());
        //将formOID更新
        head.setFormOid(type.getFormOid());
        Long startw = System.currentTimeMillis();

        String applicantOidStr = head.getApplicationOid();
        UUID applicantOid = applicantOidStr != null ? UUID.fromString(applicantOidStr) : null;
        String documentOidStr = head.getDocumentOid();
        UUID documentOid = documentOidStr != null ? UUID.fromString(documentOidStr) : null;
        String unitOidStr = head.getUnitOid();
        UUID unitOid = unitOidStr != null ? UUID.fromString(unitOidStr) : null;
        String formOidStr = type.getFormOid();
        UUID formOid = formOidStr != null ? UUID.fromString(formOidStr) : null;
        // 设置调用提交工作流方法的参数
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(head.getId()); // 单据id
        submitData.setDocumentOid(documentOid); // 单据oid
        submitData.setDocumentNumber(head.getDocumentNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey()); // 单据类别
        submitData.setDocumentTypeId(workFlowDocumentRef.getDocumentTypeId()); // 单据类型id
        submitData.setDocumentTypeCode(type.getExpAdjustTypeCode()); // 单据类型代码
        submitData.setDocumentTypeName(type.getExpAdjustTypeName()); // 单据类型名称
        submitData.setCurrencyCode(head.getCurrencyCode()); // 币种
        submitData.setAmount(head.getTotalAmount()); // 原币金额
        submitData.setFunctionAmount(head.getFunctionalAmount()); // 本币金额
        submitData.setCompanyId(head.getCompanyId()); // 公司id
        submitData.setUnitOid(unitOid); // 部门oid
        submitData.setApplicantOid(applicantOid); // 申请人oid
        submitData.setApplicantDate(head.getAdjustDate()); // 申请日期
        submitData.setRemark(head.getDescription()); // 备注
        submitData.setSubmittedBy(OrgInformationUtil.getCurrentUserOid()); // 提交人
        submitData.setFormOid(formOid); // 表单oid
        submitData.setDestinationService(applicationName); // 注册到Eureka中的名称

        // 调用工作流的三方接口进行提交
        ApprovalResultCO submitResult = workflowInterface.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())){
            Integer approvalStatus = submitResult.getStatus();

            if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                head.setAdjustDate(ZonedDateTime.now());
                head.setStatus(DocumentOperationEnum.APPROVAL.getId());// 修改为审批中
                updateById(head);
            } else {
                updateDocumentStatus(head.getId(), approvalStatus, "");
            }
        } else {
            throw new BizException(submitResult.getError());
        }

        log.info("调整单整体提交,耗时:{}ms", System.currentTimeMillis() - startw);
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

        EntityWrapper<ExpenseAdjustHeader> wrapper = new EntityWrapper<ExpenseAdjustHeader>();
        wrapper.eq("id", documentId);
        updateForSet("version_number = version_number", wrapper);
    }

    /**
     *
     * @param headerId 调整单头ID
     * @param status   状态
     * @param approvalText  审批意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatus(Long headerId,Integer status,String approvalText){
       ExpenseAdjustHeader header = this.selectById(headerId);
        if(header == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_HEADER_IS_NOT_EXISTS);
        }

        Long documentId = header.getId();
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(documentId);
        // 这里重新通过id获取单据是为了保证接下来修改的是最新版本记录
        header = selectById(documentId);

        header.setStatus(status);
        this.updateById(header);// 保存
        if(DocumentOperationEnum.WITHDRAW.getId().equals(status) || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)){
         //审批拒绝  撤回 如果有预算需要释放， 可在这里添逻辑

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpenseAdjustHeader createHeader(ExpenseAdjustHeaderWebDTO dto){
        // 查询单据类型和相关维度， 单据保存后维度不以单据类型分配的维度的增减而增减
        ExpenseAdjustTypeWebDTO adjustTypeWebDTO = expenseAdjustTypeService.getTypeAndDimensions(dto.getExpAdjustTypeId());
        // 获取当前登陆信息
        PrincipalLite userBean = OrgInformationUtil.getUser();
        // 校验
        DepartmentCO departmentCO = organizationService.getDepartmentById(dto.getUnitId());
        if (departmentCO == null){
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        dto.setUnitOid(departmentCO.getDepartmentOid().toString());
        // 设置默认值 外币汇率等
        dto.setTenantId(userBean.getTenantId());
        dto.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
        // 附件
        if (!CollectionUtils.isEmpty(dto.getAttachmentOidList())){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dto.getAttachmentOidList().size(); i++) {
                sb.append(dto.getAttachmentOidList().get(i));
                if (i < dto.getAttachmentOidList().size() - 1) {
                    sb.append(",");
                }
            }
            dto.setAttachmentOid(sb.toString());
        }
        // 设置汇率
        if ("CNY".equals(dto.getCurrencyCode())) {
            dto.setExchangeRate(BigDecimal.valueOf(1));
        }else {
            CurrencyRateCO cny = organizationService.getForeignCurrencyByCode("CNY",
                    dto.getCurrencyCode(),
                    OrgInformationUtil.getCurrentSetOfBookId());
            dto.setExchangeRate(BigDecimal.valueOf(cny.getRate()));
        }
        ContactCO userCO = organizationService.getUserById(dto.getEmployeeId());
        // 类型上的部分字段
        dto.setBudgetFlag(adjustTypeWebDTO.getBudgetFlag() == null ? false : adjustTypeWebDTO.getBudgetFlag());
        dto.setAccountFlag(adjustTypeWebDTO.getAccountFlag() == null ? false : adjustTypeWebDTO.getAccountFlag());
        dto.setAdjustTypeCategory(adjustTypeWebDTO.getAdjustTypeCategory());
        // 保存单据头信息
        ExpenseAdjustHeader expenseAdjustHeader = new ExpenseAdjustHeader();
        BeanUtils.copyProperties(dto, expenseAdjustHeader);
        expenseAdjustHeader.setDocumentOid(UUID.randomUUID().toString());
        expenseAdjustHeader.setApplicationOid(userCO.getUserOid());
        expenseAdjustHeader.setTotalAmount(BigDecimal.ZERO);
        expenseAdjustHeader.setFunctionalAmount(BigDecimal.ZERO);
        expenseAdjustHeader.setAdjustDate(ZonedDateTime.now());
        expenseAdjustHeader.setStatus(DocumentOperationEnum.GENERATE.getId());
        expenseAdjustHeader.setDocumentNumber(commonService.getCoding(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getCategory(), dto.getCompanyId(), null));
        expenseAdjustHeader.setId(null);
        this.insert(expenseAdjustHeader);
        // 保存单据关联维度信息
        if (!CollectionUtils.isEmpty(adjustTypeWebDTO.getDimensions())){
            adjustTypeWebDTO.getDimensions().forEach(e -> {
                e.setHeaderId(expenseAdjustHeader.getId());
                e.setId(null);
            });
            dimensionService.insertBatch(adjustTypeWebDTO.getDimensions());
        }
        return expenseAdjustHeader;
    }

    /**
     *  费用调整单财务查询
     * @param expAdjustHeaderNumber
     * @param expAdjustTypeId
     * @param status
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param amountMin
     * @param amountMax
     * @param employeeId
     * @param description
     * @param adjustTypeCategory
     * @param currencyCode
     * @param unitId
     * @param companyId
     * @param page
     * @return
     */
    public List<ExpenseAdjustHeaderWebDTO> listHeaderWebDTOByCondition(String expAdjustHeaderNumber,
                                                                       Long expAdjustTypeId,
                                                                       String status,
                                                                       ZonedDateTime requisitionDateFrom,
                                                                       ZonedDateTime requisitionDateTo,
                                                                       BigDecimal amountMin, BigDecimal amountMax,
                                                                       Long employeeId,
                                                                       String description,
                                                                       String adjustTypeCategory,
                                                                       String currencyCode,
                                                                       Long unitId,
                                                                       Long companyId,
                                                                       Page page) {

        List<ExpenseAdjustHeaderWebDTO> result = baseMapper.listHeaderWebDTOByCondition(expAdjustHeaderNumber, expAdjustTypeId,
                status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax, employeeId, description, adjustTypeCategory,
                currencyCode, OrgInformationUtil.getCurrentUserId(),unitId,companyId, page);
        //isSetEmployee 暂时设置为 false
        setCompanyAndDepartmentAndEmployee(result, true, true);

        return result;
    }
    public Page<ExpenseAdjustHeaderWebDTO> getExpenseAdjustHeaderWebDTOByCond(String expAdjustHeaderNumber,
                                                                              Long expAdjustTypeId,
                                                                              String status,
                                                                              ZonedDateTime requisitionDateFrom,
                                                                              ZonedDateTime requisitionDateTo,
                                                                              BigDecimal amountMin, BigDecimal amountMax,
                                                                              Long employeeId,
                                                                              String description,
                                                                              String adjustTypeCategory,
                                                                              String currencyCode,
                                                                              Long unitId,
                                                                              Long companyId,
                                                                              Page page ){
        Page<ExpenseAdjustHeaderWebDTO> pageResult = new Page<>();
        List<ExpenseAdjustHeaderWebDTO> result = baseMapper.listHeaderWebDTOByCondition(expAdjustHeaderNumber, expAdjustTypeId,
                status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax, employeeId, description, adjustTypeCategory,
                currencyCode, OrgInformationUtil.getCurrentUserId(),unitId,companyId, page);
        //isSetEmployee 暂时设置为 false
        setCompanyAndDepartmentAndEmployee(result, true, true);
        pageResult.setRecords(result);
        return pageResult;
    }
    private void setCompanyAndDepartmentAndEmployee(List<ExpenseAdjustHeaderWebDTO> headers,
                                                    boolean isSetEmployee,
                                                    boolean isSetCompanyAndUnit) {
        if (!CollectionUtils.isEmpty(headers)){
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            Map<Long, CompanyCO> companyMap = new HashMap<>(16);
            Map<Long, DepartmentCO> departmentMap = new HashMap<>(16);
            headers.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getUnitId());
                employeeIds.add(e.getEmployeeId());
                employeeIds.add(e.getCreatedBy());
            });
            if (isSetCompanyAndUnit) {
                // 查询公司
                companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
                // 查询部门
                departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            }
            // 查询员工
            Map<Long, ContactCO> usersMap = new HashMap<>(16);
            if (isSetEmployee){
                usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));
            }

            Map<Long, ContactCO> finalUsersMap = usersMap;
            Map<Long, CompanyCO> finalCompanyMap = companyMap;
            Map<Long, DepartmentCO> finalDepartmentMap = departmentMap;
            headers
                    .stream()
                    .forEach(e ->{
                        if (finalCompanyMap.containsKey(e.getCompanyId())){
                            e.setCompanyName(finalCompanyMap.get(e.getCompanyId()).getName());
                        }
                        if (finalDepartmentMap.containsKey(e.getUnitId())){
                            e.setUnitName(finalDepartmentMap.get(e.getUnitId()).getName());
                        }
                        if (isSetEmployee){
                            if(finalUsersMap.containsKey(e.getEmployeeId())){
                                e.setEmployeeName(finalUsersMap.get(e.getEmployeeId()).getFullName());
                            }
                            if(finalUsersMap.containsKey(e.getCreatedBy())){
                                e.setCreatedByName(finalUsersMap.get(e.getCreatedBy()).getFullName());
                            }
                        }
                    });
        }
    }

    public ExpenseAdjustHeaderWebDTO getHeaderDTOById(Long expAdjustHeaderId) {
        ExpenseAdjustHeader expenseAdjustHeader = this.selectById(expAdjustHeaderId);
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeService.selectById(expenseAdjustHeader.getExpAdjustTypeId());
        ExpenseAdjustHeaderWebDTO dto = new ExpenseAdjustHeaderWebDTO();
        BeanUtils.copyProperties(expenseAdjustHeader, dto);
        dto.setTypeName(expenseAdjustType.getExpAdjustTypeName());
        setCompanyAndDepartmentAndEmployee(Arrays.asList(dto), true, true);
        setAttachments(dto);
        return dto;
    }

    private void setAttachments(ExpenseAdjustHeaderWebDTO dto){
        if (StringUtils.hasText(dto.getAttachmentOid())){
            String[] split = dto.getAttachmentOid().split(",");
            List<String> stringList = Arrays.asList(split);
            List<AttachmentCO> attachmentCOS = organizationService.listAttachmentsByOids(stringList);
            dto.setAttachmentOidList(stringList);
            dto.setAttachments(attachmentCOS);
        }
    }

    public List<ExpenseAdjustDimensionDTO> queryDimensionDTOByTypeId(Long headerId) {
        ExpenseAdjustHeader expenseAdjustHeader = this.selectById(headerId);
        if (null == expenseAdjustHeader){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseDimension> dimensions = dimensionService.listDimensionByHeaderIdAndType(headerId, ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(), true);
        if (!CollectionUtils.isEmpty(dimensions)){

            List<ExpenseAdjustDimensionDTO> dimensionDTOList = new ArrayList<>();
            List<Long> collect = dimensions.stream().map(ExpenseDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionDetailCO> costCenterDTOS = organizationService.listDimensionsBySetOfBooksIdAndIds(expenseAdjustHeader.getSetOfBooksId(), collect);
            for (DimensionDetailCO detailCO : costCenterDTOS ){

                ExpenseAdjustDimensionDTO expenseAdjustDimensionDTO = new ExpenseAdjustDimensionDTO();
                expenseAdjustDimensionDTO.setId(detailCO.getId());
                expenseAdjustDimensionDTO.setName(detailCO.getDimensionName());
                expenseAdjustDimensionDTO.setSequenceNumber(detailCO.getDimensionSequence());
                List<ExpenseAdjustDimensionItemDTO> itemDTOS = new ArrayList<>();
                detailCO.getSubDimensionItemCOS().forEach(e ->{
                    ExpenseAdjustDimensionItemDTO itemDTO = new ExpenseAdjustDimensionItemDTO();
                    itemDTO.setItemId(e.getId());
                    itemDTO.setItemName(e.getDimensionItemName());
                    itemDTOS.add(itemDTO);
                });
                expenseAdjustDimensionDTO.setItemDTOList(itemDTOS);
                dimensionDTOList.add(expenseAdjustDimensionDTO);
            }
            return dimensionDTOList;
        }else{
            return new ArrayList<>();
        }
    }


    /**
     * 校验单据状态
     * @param operateType
     * @param status
     */
    public void checkDocumentStatus(Integer operateType, Integer status){
        switch (operateType){
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
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())) {
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

    @Transactional(rollbackFor = Exception.class)
    public ExpenseAdjustHeader updateHeaders(ExpenseAdjustHeaderWebDTO dto) {
        ExpenseAdjustHeader expenseAdjustHeader = this.selectById(dto.getId());
        if (null == expenseAdjustHeader){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        checkDocumentStatus(0, expenseAdjustHeader.getStatus());
        // 公司
        expenseAdjustHeader.setCompanyId(dto.getCompanyId());
        // 部门
        DepartmentCO department = organizationService.getDepartmentById(dto.getUnitId());
        if (department == null){
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        expenseAdjustHeader.setUnitId(dto.getUnitId());
        expenseAdjustHeader.setUnitOid(department.getDepartmentOid().toString());
        // 描述
        expenseAdjustHeader.setDescription(dto.getDescription());
        // 附件
        if (!CollectionUtils.isEmpty(dto.getAttachmentOidList())){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dto.getAttachmentOidList().size(); i++) {
                sb.append(dto.getAttachmentOidList().get(i));
                if (i < dto.getAttachmentOidList().size() - 1) {
                    sb.append(",");
                }
            }
            dto.setAttachmentOid(sb.toString());
            expenseAdjustHeader.setAttachmentOid(sb.toString());
        }else{
            expenseAdjustHeader.setAttachmentOid("");
        }
        this.updateById(expenseAdjustHeader);
        return expenseAdjustHeader;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHeaderById(Long id) {
        ExpenseAdjustHeader expenseAdjustHeader = this.selectById(id);
        if (expenseAdjustHeader == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        checkDocumentStatus(-1, expenseAdjustHeader.getStatus());
        // 删除单据头信息
        this.deleteById(id);
        // 删除行信息
        baseMapper.deleteLinesByHeaderId(id);
        // 删除维度信息
        ExpenseDimension dimension = new ExpenseDimension();
        dimension.setHeaderId(id);
        dimension.setDocumentType(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey());
        dimensionService.delete(new EntityWrapper<>(dimension));
        return true;
    }

    public List<ContactCO> listUsersByCreatedAdjustHeaders() {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<ExpenseAdjustHeader>()
                        .eq("created_by", OrgInformationUtil.getCurrentUserId())
        ).stream().map(ExpenseAdjustHeader::getEmployeeId).distinct().filter(e -> e != null).collect(Collectors.toList());
        if (userList.size() == 0) {
            return new ArrayList<>();
        }
        return organizationService.listUsersByIds(userList);
    }
    public List<ExpenseAdjustHeaderWebDTO> listExpenseAdjustApprovals(boolean finished,
                                                                      String documentNumber,
                                                                      Long expAdjustTypeId,
                                                                      String adjustTypeCategory,
                                                                      String fullName,
                                                                      Long employeeId,
                                                                      String beginDate,
                                                                      String endDate,
                                                                      String currencyCode,
                                                                      BigDecimal amountMin,
                                                                      BigDecimal amountMax,
                                                                      String description,
                                                                      Page page){
        // 当前用户就是审批人
        String approverOidStr = OrgInformationUtil.getCurrentUserOid().toString();
        // 获取未审批/已审批的单据
        List<String> documentOidStrList = workflowInterface.listApprovalDocument(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey(),
                approverOidStr, finished, beginDate, endDate);
        // 若没有满足条件的单据则不继续执行代码
        if (documentOidStrList.isEmpty()) {
            return new ArrayList<>();
        }
        ZonedDateTime submitDateFrom = DateUtil.stringToZonedDateTime(beginDate);
        ZonedDateTime submitDateTo = DateUtil.stringToZonedDateTime(endDate);
        if (submitDateTo != null){
            submitDateTo = submitDateTo.plusDays(1);
        }
        List<ExpenseAdjustHeaderWebDTO> results = new ArrayList<>();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(documentOidStrList)){
            List<ExpenseAdjustHeader> adjustHeaders = baseMapper.selectPage(page,new EntityWrapper<ExpenseAdjustHeader>()
                    .in("document_oid",documentOidStrList)
                    .eq(expAdjustTypeId != null,"exp_adjust_type_id",expAdjustTypeId)
                    .eq(StringUtils.hasText(adjustTypeCategory),"adjust_type_category",adjustTypeCategory)
                    .eq(!StringUtils.isEmpty(employeeId),"employee_id",employeeId)
                    .ge(submitDateFrom != null,"adjust_date",submitDateFrom)
                    .le(submitDateTo != null,"adjust_date",submitDateTo)
                    .eq(StringUtils.hasText(currencyCode),"currency_code",currencyCode)
                    .ge(amountMin!= null,"total_amount",amountMin)
                    .le(amountMax !=null,"total_amount",amountMax)
                    .like(StringUtils.hasText(description),"description",description)
                    .like(StringUtils.hasText(documentNumber),"document_number",documentNumber));
            adjustHeaders.forEach(adjustHeader -> {
                ExpenseAdjustType type = expenseAdjustTypeService.getExpenseAdjustTypeById(adjustHeader.getExpAdjustTypeId());
                ExpenseAdjustHeaderWebDTO result = new ExpenseAdjustHeaderWebDTO();
                BeanUtils.copyProperties(adjustHeader,result);
                ContactCO user = organizationService.getUserById(result.getEmployeeId());
                if(user != null){
                    result.setEmployeeName(user.getFullName());
                }
                result.setTypeName(type.getExpAdjustTypeName());
                result.setEntityType(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey());
                results.add(result);
            });
        }
        return results;
    }
}
