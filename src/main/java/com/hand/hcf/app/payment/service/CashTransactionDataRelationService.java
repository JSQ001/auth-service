package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.ExpensePaymentScheduleCO;
import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.payment.domain.CashTransactionData;
import com.hand.hcf.app.payment.externalApi.ExpenseService;
import com.hand.hcf.app.payment.externalApi.PaymentContractService;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.externalApi.SupplierService;
import com.hand.hcf.app.payment.persistence.CashTransactionDataMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportHeaderDTO;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportLineDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:58 2018/4/25
 * @Modified by
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class CashTransactionDataRelationService extends BaseService<CashTransactionDataMapper, CashTransactionData> {

    private final SupplierService supplierService;
    private final PaymentOrganizationService organizationService;
    private final ExpenseService expenseService;

//    private final ExpenseReportService expenseReportService;
    private final PaymentContractService contractService;

    /**
     * @Author: bin.xie
     * @Description: 根据单据类型获取通用支付数据报账单信息
     * @param: reportNumber
     * @param: applicationId
     * @param: allType
     * @param: formTypes
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.CashDataPublicReportHeaderDTO>
     * @Date: Created in 2018/4/25 16:59
     * @Modified by
     */
    public List<CashDataPublicReportHeaderDTO> queryReportAssociatedAcp(String reportNumber,
                                                                        Long applicationId,
                                                                        Boolean allType,
                                                                        List<Long> formTypes,
                                                                        Page page,
                                                                         Long documentTypeId) {
        if (allType) {
            formTypes = null;
        }else{
            if (CollectionUtils.isEmpty(formTypes)){
                formTypes = Arrays.asList(-1L);
            }
        }
        List<CashDataPublicReportHeaderDTO> result = baseMapper.queryReportAssociatedAcp(reportNumber, applicationId,
                formTypes, page,  documentTypeId);
        Map<Long, String> venMap = new HashMap<>();
        Map<Long, String> empMap = new HashMap<>();
        //报账单相关信息
        List<CashDataPublicReportLineDTO> collect = result.stream().flatMap(headerDTO -> headerDTO.getLineList().stream()).collect(toList());

        // 获取所有报账单计划付款行ID
        List<Long> lineIds = collect.stream().map(CashDataPublicReportLineDTO::getScheduleLineId).collect(toList());

        // 通过报账单计划付款行ID集合查询报账单计划付款行信息
        List<ExpensePaymentScheduleCO> expensePaymentScheduleDTOS = expenseService.getExpPublicReportScheduleByIds(lineIds);
        collect.forEach(relationLine -> {
            // 设置报账单行信息
            List<ExpensePaymentScheduleCO> expensePaymentScheduleDTOList = expensePaymentScheduleDTOS
                    .stream()
                    .filter(e -> e.getId().equals(relationLine.getScheduleLineId()))
                    .collect(toList());
            if (CollectionUtils.isEmpty(expensePaymentScheduleDTOList)){
                throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_EXPREPORT_NOT_EXISTS);
            }

            ExpensePaymentScheduleCO expensePaymentScheduleCO = expensePaymentScheduleDTOList.get(0);
//            relationLine.setScheduleLineNumber(TypeConversionUtils.parseInt(expensePaymentScheduleCO.getScheduleLineNumber())); //行号不再需要
            relationLine.setContractLineId(TypeConversionUtils.parseLong(expensePaymentScheduleCO.getConPaymentScheduleLineId()));
            relationLine.setFormName(TypeConversionUtils.parseString(expensePaymentScheduleCO.getReportTypeName()));
            relationLine.setSchedulePaymentDate(expensePaymentScheduleCO.getPaymentScheduleDate());
            String partnerName = null;

            //获取员工或供应商名字
            if (relationLine.getPayeeCategory().equals("EMPLOYEE")) {
                if (!empMap.containsKey(relationLine.getPayeeId())) {

                    List<ContactCO> partner = organizationService.listByUserIds(
                            Arrays.asList(relationLine.getPayeeId()));

                    empMap.put(relationLine.getPayeeId(),partner.get(0).getFullName());
                }
                partnerName = empMap.get(relationLine.getPayeeId());
            } else {

                if (!venMap.containsKey(relationLine.getPayeeId())) {

                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(relationLine.getPayeeId().toString());

                    venMap.put(relationLine.getPayeeId(),venInfoCO.getVenNickname());
                }
                partnerName = venMap.get(relationLine.getPayeeId());
            }
            //收款方名称
            relationLine.setPayeeName(partnerName);
            // 取合同信息
            //bo.liu 合同
            /*if (relationLine.getContractLineId() != null) {

                ContractHeaderLineCO contractLine = contractService.getContractLine(relationLine.getContractHeaderId(),
                        relationLine.getContractLineId());
                if (contractLine == null) {
                    throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_CONTRACT_NOT_EXISTS);
                }
                // 设置合同编号
                relationLine.setContractNumber(TypeConversionUtils.parseString(contractLine.getContractNumber()));
                // 设置合同计划付款日期
                relationLine.setContractDueDate(TypeConversionUtils.parseString(contractLine.getDueDate()));
                // 设置计划付款行号
                relationLine.setContractLineNumber(TypeConversionUtils.parseInt(contractLine.getLineNumber()));
            }*/
        });


        List<Long> userIds = result.stream().map(CashDataPublicReportHeaderDTO::getEmployeeId).collect(toList());
        List<ContactCO> userInfoCOs = organizationService.listByUserIds(userIds);
        result.forEach( u -> {
            // TODO springCloud改造后再添加批量
            // ExpenseReportHeaderDTO expHeader = expenseReportService.getExpPublicReportHeadById(u.getReportHeadId());

            List<ExpensePaymentScheduleCO> expHeader = expensePaymentScheduleDTOS
                    .stream()
                    .filter(e -> e.getExpReportHeaderId().equals(u.getReportHeadId()))
                    .collect(toList());
            if (expHeader == null){
                throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_EXPREPORT_NOT_EXISTS);
            }
            // 设置申请日期
            u.setRequisitionDate(expHeader.get(0).getRequisitionDate());

            u.setReportTypeName(u.getLineList().get(0).getFormName());
            List<ContactCO> userFilter = userInfoCOs
                    .stream()
                    .filter(user -> user.getId().equals(u.getEmployeeId()))
                    .collect(toList());
            if (CollectionUtils.isEmpty(userFilter)){
                throw new BizException(RespCode.SYS_USER_INFO_NOT_EXISTS);
            }
            u.setEmployeeName(userFilter.get(0).getFullName());
        });
        return result;
    }

}
