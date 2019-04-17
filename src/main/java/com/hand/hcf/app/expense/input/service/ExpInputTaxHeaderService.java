package com.hand.hcf.app.expense.input.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxLine;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxHeaderDTO;
import com.hand.hcf.app.expense.input.persistence.ExpInputTaxHeaderMapper;
import com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.TypeConversionUtils;;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Service
public class ExpInputTaxHeaderService extends BaseService<ExpInputTaxHeaderMapper, ExpInputTaxHeader> {
    @Autowired
    private ExpInputTaxHeaderMapper expInputTaxHeaderMapper;

    @Autowired
    private ExpInputTaxLineService expInputTaxLineService;
    @Autowired
    private ExpInputTaxDistService expInputTaxDistService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private CommonService commonService;

    public Page<ExpInputTaxHeaderDTO> queryHeader(Long applicantId, String transferType, String useType, String transferDateFrom, String transferDateTo, String status, BigDecimal amountFrom, BigDecimal amountTo, String description, String documentNumber, Long companyId, Long departmentId, Page page) {
        List<ExpInputTaxHeader> expInputTaxHeaders = expInputTaxHeaderMapper.selectPage(page, new EntityWrapper<ExpInputTaxHeader>()
                .eq("applicant_id", applicantId != null ? applicantId : OrgInformationUtil.getUser().getId())
                .eq(transferType != null, "transfer_type", transferType)
                .eq(useType != null, "use_type", useType)
                .eq(status != null, "status", status)
                .like(description != null, "description", description)
                .ge(transferDateFrom != null, "transfer_date", TypeConversionUtils.getStartTimeForDayYYMMDD(transferDateFrom))
                .le(transferDateTo != null, "transfer_date", TypeConversionUtils.getEndTimeForDayYYMMDD(transferDateTo))
                .ge(amountFrom != null, "amount", amountFrom)
                .le(amountTo != null, "amount", amountTo)
                .like(documentNumber != null, "document_number", documentNumber)
                .eq(companyId != null, "company_id", companyId)
                .eq(departmentId != null, "department_id", departmentId)
        );
        List<ExpInputTaxHeaderDTO> headers = mapperFacade.mapAsList(expInputTaxHeaders, ExpInputTaxHeaderDTO.class);
        //设置 名称（公司，部门，员工）
        setDesc(headers);
        //设置 值列表的 name
        setSysCodeValue(headers);
       /* for (ExpInputTaxHeaderDTO header : headers) {
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("transferType", header.getTransferType());
            if (sysCodeValueCO != null) {
                header.setTransferTypeName(sysCodeValueCO.getName());
            }

            SysCodeValueCO sysCodeValueCO1 = organizationService.getSysCodeValueByCodeAndValue("useType", header.getUseType());
            if (sysCodeValueCO1 != null) {
                header.setUseTypeName(sysCodeValueCO1.getName());
            }
        }*/

        page.setRecords(headers);
        return page;
    }


    public ExpInputTaxHeaderDTO queryById(Long id) {
        ExpInputTaxHeader expInputTaxHeader = expInputTaxHeaderMapper.selectById(id);
        ExpInputTaxHeaderDTO header = mapperFacade.map(expInputTaxHeader, ExpInputTaxHeaderDTO.class);
        setAttachments(header);

        //设置描述
        header.setFullName(organizationService.getUserById(header.getApplicantId()).getFullName());
        header.setDepartmentName(organizationService.getDepartmentById(header.getDepartmentId()).getName());
        header.setCompanyName(organizationService.getCompanyById(header.getCompanyId()).getName());

        //设置 值列表的 name
        setSysCodeValue(header);
        /*SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("transferType", header.getTransferType());
        if (sysCodeValueCO != null) {
            header.setTransferTypeName(sysCodeValueCO.getName());
        }
        SysCodeValueCO sysCodeValueCO1 = organizationService.getSysCodeValueByCodeAndValue("useType", header.getUseType());
        if (sysCodeValueCO1 != null) {
            header.setUseTypeName(sysCodeValueCO1.getName());
        }*/
        return header;
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpInputTaxHeader insertOrUpdateHeader(ExpInputTaxHeader expInputTaxHeader) {
        PrincipalLite user = OrgInformationUtil.getUser();
        expInputTaxHeader.setLastUpdatedBy(user.getId());
        expInputTaxHeader.setLastUpdatedDate(ZonedDateTime.now());
        if (expInputTaxHeader.getId() != null) {
            expInputTaxHeaderMapper.updateById(expInputTaxHeader);
        } else {
            //新建保存时设置默认值
            expInputTaxHeader.setTenantId(user.getTenantId());
            expInputTaxHeader.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
            expInputTaxHeader.setApplicantId(user.getId());
            expInputTaxHeader.setBaseAmount(new BigDecimal(0));
            expInputTaxHeader.setBaseFunctionAmount(new BigDecimal(0));
            expInputTaxHeader.setAmount(new BigDecimal(0));
            expInputTaxHeader.setFunctionAmount(new BigDecimal(0));
            expInputTaxHeader.setStatus(expInputTaxHeader.getStatus() != null ? expInputTaxHeader.getStatus() : "1001");
            expInputTaxHeader.setReverseFlag("N");
            expInputTaxHeader.setAuditStatus("N");
            expInputTaxHeader.setCreatedBy(user.getId());
            expInputTaxHeader.setCreatedDate(ZonedDateTime.now());
            expInputTaxHeader.setDocumentNumber(commonService.getCoding(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getCategory(), expInputTaxHeader.getCompanyId(), null));
            expInputTaxHeaderMapper.insert(expInputTaxHeader);
        }
        return expInputTaxHeader;
    }

    public void setDesc(List<ExpInputTaxHeaderDTO> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> userIds = new HashSet<>();
            headers.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getDepartmentId());
                userIds.add(e.getApplicantId());
            });
            // 查询公司
            Map<Long, CompanyCO> companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
            // 查询部门
            Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(userIds));
            headers
                    .stream()
                    .forEach(e -> {
                        if (companyMap.containsKey(e.getCompanyId())) {
                            e.setCompanyName(companyMap.get(e.getCompanyId()).getName());
                        }
                        if (departmentMap.containsKey(e.getDepartmentId())) {
                            e.setDepartmentName(departmentMap.get(e.getDepartmentId()).getName());
                        }
                        if (usersMap.containsKey(e.getApplicantId())) {
                            e.setFullName(usersMap.get(e.getApplicantId()).getFullName());
                        }
                    });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long id, int status, String desc) {
        ExpInputTaxHeader header = expInputTaxHeaderMapper.selectById(id);
        //提交时，暂时只用 status
        header.setLastUpdatedDate(ZonedDateTime.now());
        header.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
        CommonApprovalHistoryCO commonApprovalHistoryCO = new CommonApprovalHistoryCO();
        commonApprovalHistoryCO.setEntityType(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getKey());
        commonApprovalHistoryCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
        commonApprovalHistoryCO.setOperation(status);

        if (DocumentOperationEnum.APPROVAL.getId() == status) {
            if (header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString()) || header.getStatus().equals(DocumentOperationEnum.APPROVAL_PASS.getId().toString())) {
                return false;
            }
            //因为oid只有在提交用到，所以在第一次提交的时候生成。
            UUID docOid = null;
            if (header.getDocumentOid() == null) {
                docOid = UUID.randomUUID();
                header.setDocumentOid(docOid.toString());
            } else {
                docOid = UUID.fromString(header.getDocumentOid());
            }
            //尝试插入工作流历史
            commonApprovalHistoryCO.setEntityOid(docOid);
            commonApprovalHistoryCO.setOperationDetail("单据提交" + (desc != null ? ":" + desc : ""));
        } else if (DocumentOperationEnum.APPROVAL_PASS.getId() == status) {
            header.setAuditDate(ZonedDateTime.now());
            if (!header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString())) {
                return false;
            }
            commonApprovalHistoryCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
            commonApprovalHistoryCO.setOperationDetail("单据通过" + (desc != null ? ":" + desc : ""));
        } else if (DocumentOperationEnum.APPROVAL_REJECT.getId() == status) {
            header.setAuditDate(ZonedDateTime.now());
            if (!header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString())) {
                return false;
            }
            commonApprovalHistoryCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
            commonApprovalHistoryCO.setOperationDetail("单据拒绝" + (desc != null ? ":" + desc : ""));
        }
        header.setStatus(String.valueOf(status));
        expInputTaxHeaderMapper.updateById(header);
        organizationService.saveHistory(commonApprovalHistoryCO);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        List<ExpInputTaxLine> expInputTaxLines = expInputTaxLineService.queryByHeaderId(id);
        for (ExpInputTaxLine line : expInputTaxLines) {
            expInputTaxDistService.deleteByLineId(line.getId());
        }
        expInputTaxLineService.deleteByHeaderId(id);
        expInputTaxHeaderMapper.deleteById(id);
        return true;
    }

    private void setAttachments(ExpInputTaxHeaderDTO dto) {
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

    public List<ExpInputTaxHeaderDTO> queryExpInputFinance(Page page, Long companyId, Long unitId, Long applyId, Long status,
                                                           String transferType, String useType, String currencyCode,
                                                           BigDecimal amountFrom, BigDecimal amountTo, String reverseFlag, String remark,
                                                           ZonedDateTime creatDateFrom, ZonedDateTime creatDateTo, ZonedDateTime auditDateFrom,
                                                           ZonedDateTime auditDateTo, Long tenantId, String documentNumber) {

        Wrapper<ExpInputTaxHeader> wrapper = getQueryExpInputFinance(companyId,unitId,applyId,status,transferType,useType,currencyCode,amountFrom,amountTo,
                reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom,auditDateTo,tenantId,documentNumber);
        List<ExpInputTaxHeaderDTO> expInputFinances=baseMapper.queryExpInputFinance(page,wrapper);
        setDesc(expInputFinances);
        setSysCodeValue(expInputFinances);
        /*for (ExpInputTaxHeaderDTO header : expInputFinances) {
            SysCodeValueCO sysCodeValueCO = getSysCodeValueByCodeAndValue("transferType",header.getTransferType());
            if (sysCodeValueCO != null) {
                header.setTransferTypeName(sysCodeValueCO.getName());
            }

            SysCodeValueCO sysCodeValueCO1 =getSysCodeValueByCodeAndValue("useType",header.getUseType());
            if (sysCodeValueCO1 != null) {
                header.setUseTypeName(sysCodeValueCO1.getName());
            }
        }*/
        //setCompanyAndDepartmentAndEmployee1(expInputFinances);
        return  expInputFinances;
    }
    //获取业务大类和用途类型值列表
    public void setSysCodeValue(List<ExpInputTaxHeaderDTO> headerDTOS){
        if(!CollectionUtils.isEmpty(headerDTOS)){
            headerDTOS.stream().forEach(e->{
                setSysCodeValue(e);
            });
        }
    }
    public void setSysCodeValue(ExpInputTaxHeaderDTO headerDTO){
        if(headerDTO!=null){
                SysCodeValueCO sysCodeValueCO = getSysCodeValueByCodeAndValue("transferType",headerDTO.getTransferType());
                if (sysCodeValueCO != null) {
                    headerDTO.setTransferTypeName(sysCodeValueCO.getName());
                }

                SysCodeValueCO sysCodeValueCO1 =getSysCodeValueByCodeAndValue("useType",headerDTO.getUseType());
                if (sysCodeValueCO1 != null) {
                    headerDTO.setUseTypeName(sysCodeValueCO1.getName());
                }

        }
    }


    //获取系统代码值列表
    public SysCodeValueCO getSysCodeValueByCodeAndValue(String code,String type){
        SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue(code, type);

        return sysCodeValueCO ;

    }

    public void exportFormExcel(Long companyId,
                                Long unitId,
                                Long applyId,
                                Long status,
                                String transferType,
                                String useType,
                                String currencyCode,
                                BigDecimal amountFrom,
                                BigDecimal amountTo,
                                String reverseFlag,
                                String remark,
                                ZonedDateTime creatDateFrom,
                                ZonedDateTime creatDateTo,
                                ZonedDateTime auditDateFrom,
                                ZonedDateTime auditDateTo,
                                Long tenantId,
                                String documentNumber,
                                HttpServletResponse response,
                                HttpServletRequest request,
                                ExportConfig exportConfig)throws IOException {
        //获取查询的Sql
        Wrapper<ExpInputTaxHeader> wrapper = getQueryExpInputFinance(companyId,unitId,applyId,status,transferType,useType,currencyCode,amountFrom,amountTo,
                reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom,auditDateTo,tenantId,documentNumber);
        //获取 查询的总数
        int total = baseMapper.getCountByCondition(wrapper);
        int availProcessors = Runtime.getRuntime().availableProcessors() / 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpInputTaxHeaderDTO,ExpInputTaxHeaderDTO>(){
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpInputTaxHeaderDTO> queryDataByPage(Page page) {
                return ListExpInputTaxHeaderFianicen(page,wrapper);
            }

            @Override
            public ExpInputTaxHeaderDTO toDTO(ExpInputTaxHeaderDTO t) {
                return t;
            }

            @Override
            public Class<ExpInputTaxHeaderDTO> getEntityClass() {
                return ExpInputTaxHeaderDTO.class;
            }
        }, availProcessors, request, response);
    }
    public List<ExpInputTaxHeaderDTO> ListExpInputTaxHeaderFianicen(Page page,
                                                                    Wrapper<ExpInputTaxHeader> wrapper){
        List<ExpInputTaxHeaderDTO>headers = baseMapper.queryExpInputFinance(page,wrapper);
        setDesc(headers);
        setSysCodeValue(headers);
        return headers;
    }
    public  Wrapper<ExpInputTaxHeader> getQueryExpInputFinance (Long companyId,
                                                                Long unitId,
                                                                Long applyId,
                                                                Long status,
                                                                String transferType,
                                                                String useType,
                                                                String currencyCode,
                                                                BigDecimal amountFrom,
                                                                BigDecimal amountTo,
                                                                String reverseFlag,
                                                                String remark,
                                                                ZonedDateTime creatDateFrom,
                                                                ZonedDateTime creatDateTo,
                                                                ZonedDateTime auditDateFrom,
                                                                ZonedDateTime auditDateTo,
                                                                Long tenantId,
                                                                String documentNumber){
        Wrapper<ExpInputTaxHeader> wrapper = new EntityWrapper<ExpInputTaxHeader>()
                .eq(companyId!=null,"t.company_id",companyId)
                .eq(unitId!=null,"t.department_id",unitId)
                .eq(applyId!=null,"t.applicant_id",applyId)
                .eq(status!=null,"t.status",status)
                .eq(StringUtils.hasText(transferType),"t.transfer_type",transferType)
                .eq(StringUtils.hasText(useType),"t.use_type",useType)
                .eq(StringUtils.hasText(currencyCode),"t.currency_code",currencyCode)
                .gt(amountFrom!=null,"t.amount",amountFrom)
                .lt(amountTo!=null,"t.amount",amountTo)
                .eq(StringUtils.hasText(reverseFlag),"t.reverse_flag",reverseFlag)
                .like(StringUtils.hasText(remark),"t.description",remark)
                .gt(creatDateFrom!=null,"t.created_date",creatDateFrom)
                .lt(creatDateTo!=null,"t.created_date",creatDateTo)
                .gt(auditDateFrom!=null,"t.audit_date",auditDateFrom)
                .lt(auditDateTo!=null,"t.audit_date",auditDateTo)
                .eq(tenantId!=null,"t.tenant_id",tenantId)
                .like(StringUtils.hasText(documentNumber),"t.document_number",documentNumber)
                .orderBy("t.document_number",false);
        return wrapper ;
    }
}
