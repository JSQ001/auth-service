package com.hand.hcf.app.prepayment.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.implement.web.ApplicationControllerImpl;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.externalApi.ExpenseModuleInterface;
import com.hand.hcf.app.prepayment.externalApi.PrepaymentHcfOrganizationInterface;
import com.hand.hcf.app.prepayment.persistence.CashPaymentRequisitionLineMapper;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionHeadDto;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionLineAssoReqDTO;
import com.hand.hcf.app.core.service.BaseService;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by cbc on 2017/10/26.
 */
@Service
@AllArgsConstructor
public class CashPaymentRequisitionLineService extends BaseService<CashPaymentRequisitionLineMapper, CashPaymentRequisitionLine> {
    @Autowired
    private MapperFacade mapper;
    @Autowired
    private ApplicationControllerImpl expenseApplicationClient;
    @Autowired
    private PrepaymentHcfOrganizationInterface hcfOrganizationInterface;
    @Autowired
    private ExpenseModuleInterface expenseModuleInterface;

    @Transactional
    @Override
    public boolean updateAllColumnById(CashPaymentRequisitionLine entity) {

        return super.updateAllColumnById(entity);
    }

    public List<CashPaymentRequisitionLineCO> getLineByHeadID(@RequestParam(value = "headId") Long headId) {
        List<CashPaymentRequisitionLine> list = baseMapper.selectList(new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
                .orderBy("created_date")
        );
        List<CashPaymentRequisitionLineCO> lineDTOS = new ArrayList<>();
        list.stream().forEach(cashPaymentRequisitionLine -> {
            lineDTOS.add(mapper.map(cashPaymentRequisitionLine, CashPaymentRequisitionLineCO.class));
        });
        return lineDTOS;
    }

    public List<CashPaymentRequisitionLine> getLinesByHeadID(@RequestParam(value = "headId") Long headId) {
        return baseMapper.selectList(new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
        );
    }

    public Page<CashPaymentRequisitionHeadDto> getLineByQueryfromApplication(Page page, String requisitionNumber, String documentNumber, Long typeId, Long reptypeId) {

        // 首先 先去 费用模块查询出来 该对应的申请单相关联的预付款单号。 getPrepaymentByDocumentNumber
        List<PrepaymentRequisitionReleaseCO> prepaymentRequisitionReleaseCOS = expenseApplicationClient.getPrepaymentByDocumentNumber(requisitionNumber);

        List<Long> hids = prepaymentRequisitionReleaseCOS.stream().map(PrepaymentRequisitionReleaseCO::getRelatedDocumentId).collect(Collectors.toList());
        List<CashPaymentRequisitionHeadDto> cashPaymentRequisitionLineDtos = baseMapper.getLineByQueryfromApplication(page, new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq(reptypeId != null, "l.csh_transaction_class_id", reptypeId)
                        .in(hids != null, "l.payment_requisition_header_id", hids)
                , documentNumber, typeId);


        // 公司
        Set<Long> ids = cashPaymentRequisitionLineDtos.stream().map(CashPaymentRequisitionHeadDto::getCompanyId).collect(Collectors.toSet());
        List<CompanyCO> companySumCO = hcfOrganizationInterface.listCompanyById(new ArrayList<>(ids));
        Map<Long, String> companyMap = companySumCO.stream().collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
        // 部门
        ids = cashPaymentRequisitionLineDtos.stream().map(CashPaymentRequisitionHeadDto::getUnitId).collect(Collectors.toSet());
        List<DepartmentCO> departments = hcfOrganizationInterface.getDepartmentByDepartmentIds(new ArrayList<>(ids));
        Map<Long, String> unitMap = departments.stream().collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));

        // 员工
        Set<Long> empIds = cashPaymentRequisitionLineDtos.stream().map(CashPaymentRequisitionHeadDto::getEmployeeId).collect(Collectors.toSet());
        ids = cashPaymentRequisitionLineDtos.stream().map(CashPaymentRequisitionHead::getCreatedBy).collect(Collectors.toSet());
        ids.addAll(empIds);
        List<ContactCO> users = hcfOrganizationInterface.listByUserIdsConditionByKeyWord(new ArrayList<>(ids), null);
        Map<Long, String> empMap = users.stream().collect(Collectors.toMap(ContactCO::getId, ContactCO::getFullName, (k1, k2) -> k1));

        for (CashPaymentRequisitionHeadDto cashPaymentRequisitionLineDto : cashPaymentRequisitionLineDtos
        ) {
            prepaymentRequisitionReleaseCOS.stream().forEach(e -> {
                if (cashPaymentRequisitionLineDto.getLineId() == e.getRelatedDocumentLineId()) {
                    cashPaymentRequisitionLineDto.setRelevancyAmount(e.getAmount());
                }
            });

        }

        page.setRecords(cashPaymentRequisitionLineDtos);
        return page;
    }

    public List<CashPaymentRequisitionLineAssoReqDTO> pageCashPaymentRequisitionLineAssoReqByCond(Long prepaymentHeaderId,
                                                                                                  String documentNumber,
                                                                                                  String typeName,
                                                                                                  Page page) {
        List<CashPaymentRequisitionLine> lineList = baseMapper.selectPage(
                page,
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", prepaymentHeaderId)
                        .like(StringUtils.hasText(documentNumber), "ref_document_code", documentNumber)
        );
        List<CashPaymentRequisitionLineAssoReqDTO> result = new ArrayList<>();
        lineList.stream().forEach(line -> {
            CashPaymentRequisitionLineAssoReqDTO dto = mapper.map(line, CashPaymentRequisitionLineAssoReqDTO.class);
            ApplicationCO applicationCO = expenseModuleInterface.getApplicationByDocumentId(line.getRefDocumentId());
            if (applicationCO != null && applicationCO.getApplicationHeader() != null) {
                if (applicationCO.getApplicationHeader().getEmployeeId() != null) {
                    ContactCO contactCO = hcfOrganizationInterface.getUserById(applicationCO.getApplicationHeader().getEmployeeId());
                    if (contactCO != null) {
                        dto.setApplyName(contactCO.getFullName());
                    }
                }
                dto.setTypeName(applicationCO.getApplicationHeader().getTypeName());
                dto.setRequisitionDate(applicationCO.getApplicationHeader().getRequisitionDate());
                dto.setCurrencyCode(applicationCO.getApplicationHeader().getCurrencyCode());
                dto.setReqAmount(applicationCO.getApplicationHeader().getAmount());
            }
            if (StringUtils.hasText(typeName)) {
                if (dto.getTypeName().contains(typeName)) {
                    result.add(dto);
                }
            } else {
                result.add(dto);
            }
        });
        return result;
    }
}
