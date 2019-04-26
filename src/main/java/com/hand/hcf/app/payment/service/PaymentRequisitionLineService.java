package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.adapter.DomainObjectAdapter;
import com.hand.hcf.app.payment.domain.PaymentRequisitionLine;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.domain.enumeration.PaymentDocumentOperationEnum;
import com.hand.hcf.app.payment.externalApi.PaymentContractService;
import com.hand.hcf.app.payment.externalApi.ExpenseService;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.externalApi.SupplierService;
import com.hand.hcf.app.payment.persistence.CashTransactionClassMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionDataMapper;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionLineMapper;
import com.hand.hcf.app.payment.utils.Constants;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.utils.SpecificationUtil;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportLineDTO;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionLineWebDTO;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionNumberWebDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:33 2018/1/24
 * @Modified by
 */
@Service
@AllArgsConstructor
@Transactional
public class PaymentRequisitionLineService extends BaseService<PaymentRequisitionLineMapper,PaymentRequisitionLine> {

    private final CashTransactionClassMapper cashTransactionClassMapper;
    private final CashTransactionDataMapper dataMapper;
    private final PaymentOrganizationService organizationService;
    private final SupplierService supplierService;
    private final PaymentContractService contractService;


    private final ExpenseService expenseService;

    /**
     * @Author: bin.xie
     * @Description: 新增付款单行信息
     * @param: paymentRequisitionLine
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionLine
     * @Date: Created in 2018/1/24 15:38
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionLine createLine(PaymentRequisitionLine paymentRequisitionLine){
        if (paymentRequisitionLine.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (paymentRequisitionLine.getHeaderId() == null){
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"关联的头信息"});
        }

        //计算本位币，同时四舍五入保留两位小数
        BigDecimal amount  = TypeConversionUtils.roundHalfUp(paymentRequisitionLine.getAmount());
        Double exchangeRate = paymentRequisitionLine.getExchangeRate();
        BigDecimal functionAmount;
        if(exchangeRate == null || exchangeRate.compareTo(1D) == 0){
            exchangeRate = 1D;
            functionAmount = amount;
        }else{
            functionAmount = TypeConversionUtils.roundHalfUp(amount.multiply(BigDecimal.valueOf(exchangeRate)));
        }
        paymentRequisitionLine.setAmount(amount);
        paymentRequisitionLine.setExchangeRate(exchangeRate);
        paymentRequisitionLine.setFunctionAmount(functionAmount);
        baseMapper.insert(paymentRequisitionLine);
        return paymentRequisitionLine;
    }

    /**
     * @Author: bin.xie
     * @Description:  根据头ID查询行信息 分页
     * @param: headerId 头ID
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionLine>
     * @Date: Created in 2018/1/24 16:00
     * @Modified by
     */
    public List<PaymentRequisitionLine> getLinesByHeaderId(Long headerId, Page page){
        return baseMapper.selectPage(page,new EntityWrapper<PaymentRequisitionLine>().eq("header_id",headerId));
    }



    /**
     * @Author: bin.xie
     * @Description:  根据头ID查询行信息 不分页
     * @param: headerId 头ID
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionLine>
     * @Date: Created in 2018/1/24 16:00
     * @Modified by
     */
    public List<PaymentRequisitionLineWebDTO> getLinesByHeaderId(Long headerId, Integer status){
        List<PaymentRequisitionLine> lists =  baseMapper.selectList(new EntityWrapper<PaymentRequisitionLine>().eq("header_id",headerId));
        List<PaymentRequisitionLineWebDTO> paymentRequisitionLineDTOS = toDTO(lists, true);

        // 如果是审核通过，则查询已支付的信息
        if (!CollectionUtils.isEmpty(paymentRequisitionLineDTOS)) {
            if (status.compareTo(PaymentDocumentOperationEnum.APPROVAL_PASS.getId()) == 0) {
                List<Long> lineIds = paymentRequisitionLineDTOS.stream().map(PaymentRequisitionLineWebDTO::getId).collect(Collectors.toList());
                List<PaymentDocumentAmountCO> amountByDocument = dataMapper.listAmountByPrepaymentLineIds(lineIds, SpecificationUtil.ACP_REQUISITION);
                paymentRequisitionLineDTOS.stream().forEach(e ->{

                    List<PaymentDocumentAmountCO> collect = amountByDocument
                            .stream()
                            .filter(v ->
                                    v.getDocumentLineId().equals(e.getId())).collect(Collectors.toList());
                    e.setPayAmount(collect.get(0).getPayAmount());
                    e.setReturnAmount(collect.get(0).getReturnAmount());
                });
            }
        }
        return paymentRequisitionLineDTOS;
    }


    /**
     * @Author: bin.xie
     * @Description: 实体类转DTO
     * @param: paymentRequisitionLine
     * @return: com.hand.hcf.app.payment.PaymentRequisitionLineWebDTO
     * @Date: Created in 2018/1/24 19:36
     * @Modified by
     */
    private List<PaymentRequisitionLineWebDTO> toDTO(List<PaymentRequisitionLine> lists, Boolean isQueryOthers){
        //供应商Map
        Map<Long,String> venMap = new HashMap<>();
        //公司Map
        Map<Long,String> companyMap = new HashMap<>();
        //员工Map
        Map<Long,String> empMap = new HashMap<>();

        List<PaymentRequisitionLineWebDTO> dtoList = new ArrayList<>();

        for (PaymentRequisitionLine paymentRequisitionLine : lists) {
            PaymentRequisitionLineWebDTO paymentRequisitionLineWebDTO = new PaymentRequisitionLineWebDTO();
            BeanUtils.copyProperties(paymentRequisitionLine, paymentRequisitionLineWebDTO);
            DomainObjectAdapter.toDto(paymentRequisitionLineWebDTO, paymentRequisitionLine);
            //设置机构名称
            if (!companyMap.containsKey(paymentRequisitionLine.getCompanyId())) {

                CompanyCO organizationStandardDto = organizationService.getById(
                        paymentRequisitionLine.getCompanyId());

                companyMap.put(paymentRequisitionLine.getCompanyId(),organizationStandardDto.getName());
            }
            paymentRequisitionLineWebDTO.setCompanyName(companyMap.get(paymentRequisitionLine.getCompanyId()));

            //设置计划付款日期
            paymentRequisitionLineWebDTO.setSchedulePaymentDate(paymentRequisitionLine.getSchedulePaymentDate());

            //获取员工或供应商名字
            if (Constants.EMPLOYEE.equals(paymentRequisitionLine.getPartnerCategory())) {
                if (!empMap.containsKey(paymentRequisitionLine.getPartnerId())) {

                    List<ContactCO> partner = organizationService.listByUserIds(
                            Arrays.asList(paymentRequisitionLine.getPartnerId()));

                    empMap.put(paymentRequisitionLine.getPartnerId(),partner.get(0).getFullName());
                }
                paymentRequisitionLineWebDTO.setPartnerName(empMap.get(paymentRequisitionLine.getPartnerId()));
            } else {

                if (!venMap.containsKey(paymentRequisitionLine.getPartnerId())) {

                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(paymentRequisitionLine.getPartnerId().toString());

                    empMap.put(paymentRequisitionLine.getPartnerId(),venInfoCO.getVenNickname());
                }
                paymentRequisitionLineWebDTO.setPartnerName(venMap.get(paymentRequisitionLine.getPartnerId()));
            }


            if (isQueryOthers) {
                /**取报账单行信息开始*/
                List<Long> ids = new ArrayList<>();
                ids.add(paymentRequisitionLine.getRefDocumentLineId());
                List<ExpensePaymentScheduleCO> expensePaymentScheduleCOS = expenseService.getExpPublicReportScheduleByIds(ids);
                if (CollectionUtils.isEmpty(expensePaymentScheduleCOS) || expensePaymentScheduleCOS.size() == 0) {
                    //关联的报账单行信息不存在
                    throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_EXPREPORT_NOT_EXISTS);
                }
                ExpensePaymentScheduleCO expensePaymentScheduleCO = expensePaymentScheduleCOS.get(0);
                //设置关联单据编号
                paymentRequisitionLineWebDTO.setRefDocumentNumber(
                        TypeConversionUtils.parseString(expensePaymentScheduleCO.getDocumentNumber()));
                //设置付款序号
                /*paymentRequisitionLineWebDTO.setScheduleLineNumber(TypeConversionUtils.parseString(expreportByLineDTO.getScheduleLineNumber()));*/
                // 设置报账单计划付款行付款日期
                paymentRequisitionLineWebDTO.setReportPaymentDate(expensePaymentScheduleCO.getRequisitionDate());
                /**取报账单行信息结束*/
                //查询关联的支付明细
                CashDataPublicReportLineDTO expenseReportRelationLineDTO = dataMapper.getRelationalById(
                        paymentRequisitionLine.getCshTransactionId());
                if (expenseReportRelationLineDTO == null) {
                    //关联的报账单行信息不存在
                    throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_EXPREPORT_NOT_EXISTS);
                }
                //设置冻结金额
                paymentRequisitionLineWebDTO.setFreezeAmount(expenseReportRelationLineDTO.getAmount());
                //设置可关联金额
                paymentRequisitionLineWebDTO.setAvailableAmount(expenseReportRelationLineDTO.getAvailableAmount());

                /**现金事务分类名称*/
                paymentRequisitionLineWebDTO.setCshTransactionClassName(cashTransactionClassMapper.selectById(
                        paymentRequisitionLine.getCshTransactionClassId())
                        .getDescription());


                paymentRequisitionLineWebDTO.setPaymentMethodCategoryName(organizationService.getSysCodeValueByCodeAndValue(
                        PaymentSystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                        paymentRequisitionLine.getPaymentMethodCategory()).getName());
                //取合同信息
                //bo.liu 合同
                /*if (paymentRequisitionLine.getContractHeaderId() != null && paymentRequisitionLine.getPaymentScheduleLineId() != null) {
                    ContractHeaderLineCO contractLine = contractService.getContractLine(
                            paymentRequisitionLine.getContractHeaderId(), paymentRequisitionLine.getPaymentScheduleLineId());
                    if (contractLine == null) {
                        throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_CONTRACT_NOT_EXISTS);
                    }
                    // 设置合同编号
                    paymentRequisitionLineWebDTO.setContractNumber(TypeConversionUtils.parseString(contractLine.getContractNumber()));
                    // 设置合同计划付款日期
                    paymentRequisitionLineWebDTO.setContractDueDate(TypeConversionUtils.parseString(contractLine.getDueDate()));
                    // 设置计划付款行号
                    paymentRequisitionLineWebDTO.setContractLineNumber(TypeConversionUtils.parseString(contractLine.getLineNumber()));
                    // 合同名称
                    paymentRequisitionLineWebDTO.setContractName(TypeConversionUtils.parseString(contractLine.getContractName()));
                }*/
            }
            dtoList.add(paymentRequisitionLineWebDTO);
        }
        return dtoList;
    }
    /**
     * @Author: bin.xie
     * @Description: 修改付款申请单行信息
     * @param: paymentRequisitionLine
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionLine
     * @Date: Created in 2018/1/24 19:42
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionLine updateLine(PaymentRequisitionLine paymentRequisitionLine){
        if (paymentRequisitionLine.getId() == null){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //计算本位币，同时四舍五入保留两位小数
        BigDecimal amount  = TypeConversionUtils.roundHalfUp(paymentRequisitionLine.getAmount());
        Double exchangeRate = paymentRequisitionLine.getExchangeRate();
        BigDecimal functionAmount;
        if(exchangeRate == null || exchangeRate.compareTo(1D) == 0){
            exchangeRate = 1D;
            functionAmount = amount;
        }else{
            functionAmount = TypeConversionUtils.roundHalfUp(amount.multiply(BigDecimal.valueOf(exchangeRate)));
        }
        paymentRequisitionLine.setAmount(amount);
        paymentRequisitionLine.setExchangeRate(exchangeRate);
        paymentRequisitionLine.setFunctionAmount(functionAmount);

        this.updateById(paymentRequisitionLine);
        return paymentRequisitionLine;
    }

    /**
     * @Author: bin.xie
     * @Description: 逻辑删除付款申请单行信息
     * @param: id
     * @return: void
     * @Date: Created in 2018/1/24 19:45
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public Long deleteLineById(Long id){
        PaymentRequisitionLine paymentRequisitionLine = this.selectById(id);
        this.deleteById(id);
        return paymentRequisitionLine.getHeaderId();
    }


    /**
     * @Author: bin.xie
     * @Description: 根据付款申请单头ID查询该单据所有行的总金额
     * @param: headerId
     * @return: java.lang.Double
     * @Date: Created in 2018/1/30 15:26
     * @Modified by
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByHeaderId(Long headerId){
        BigDecimal amount = baseMapper.selectAcpRequisitionLineTotalAmount(headerId);

        return TypeConversionUtils.roundHalfUp(amount);
    }

    public PaymentRequisitionLineWebDTO selectByLineId(Long id){

        return toDTO(Arrays.asList(baseMapper.selectById(id)),true).get(0);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据头ID按币种分组求汇总金额
     * @param: headerId
     * @return: java.util.List<com.hand.hcf.app.payment.PaymentRequisitionNumberWebDTO>
     * @Date: Created in 2018/4/24 15:26
     * @Modified by
     */
    public List<PaymentRequisitionNumberWebDTO> sumAmountByCurrency(Long headerId){
        return baseMapper.countAmountByCurrency(headerId);
    }
}
