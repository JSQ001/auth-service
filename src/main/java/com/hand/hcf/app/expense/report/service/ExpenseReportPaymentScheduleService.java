package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.OperationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.common.externalApi.ContractService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.externalApi.PaymentService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportPaymentSchedule;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.dto.ExpenseReportPaymentScheduleDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportPaymentScheduleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:38
 * @remark
 */
@Service
public class ExpenseReportPaymentScheduleService extends BaseService<ExpenseReportPaymentScheduleMapper,ExpenseReportPaymentSchedule> {

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;


    /**
     * queryExpenseReportPaymentScheduleByIds : 查询报账单 付款信息表 -根据ID集合查询---付款申请单财务查询使用
     */
    public List<ExpenseReportPaymentScheduleDTO> queryExpenseReportPaymentScheduleByIds(String ids, Long amount, Page mybatisPage){
        if(org.apache.commons.lang3.StringUtils.isBlank(ids)){
            return new ArrayList<ExpenseReportPaymentScheduleDTO>();
        }

        // 报账单付款信息表id集合
        String[] idList = ids.split(",");

        List<ExpenseReportPaymentSchedule> list = baseMapper.selectPage(mybatisPage, new EntityWrapper<ExpenseReportPaymentSchedule>()
                                                .in("id", idList)
                                                .eq(null != amount, "AMOUNT", amount));

        List<ExpenseReportPaymentSchedule> paymentScheduleList = baseMapper.selectPage(mybatisPage, new EntityWrapper<ExpenseReportPaymentSchedule>()
                                                                                                        .in("id", idList).orderBy("created_date"));

        List<ExpenseReportPaymentScheduleDTO> result = new ArrayList<ExpenseReportPaymentScheduleDTO>();
        ExpenseReportPaymentScheduleDTO paymentScheduleDTO ; //报账单的付款信息
        ExpenseReportHeader expenseReportHeader;    //报账单头信息
        int index = PageUtil.getPageStartIndex(mybatisPage) + 1;

        for(ExpenseReportPaymentSchedule payScheduleItem : paymentScheduleList){

            expenseReportHeader = getExpenseReportHeaderById(payScheduleItem.getExpReportHeaderId());
            //jiu.zhao 支付
            /*paymentScheduleDTO = toResource(expenseReportHeader, payScheduleItem);

            //根据报账单头ID，取报账单计划付款行的核销,支付信息集合
            CashWriteOffHistoryAndPaymentAmountCO writeOffAndPaid = paymentService.listCashWriteOffHistoryAll(DocumentTypeEnum.PUBLIC_REPORT.name(),payScheduleItem.getExpReportHeaderId(),null);

            paymentScheduleDTO.setIndex(index);
            index ++;

            //付款行的已付款信息：已付款金额和已退款金额
            List<PublicReportLineAmountCO> paidInfoDTOList =  writeOffAndPaid.getPublicAmounts();
            if(paidInfoDTOList != null && paidInfoDTOList.size() > 0){
                for(PublicReportLineAmountCO amountCO : paidInfoDTOList){
                    if(amountCO.getDocumentLineId().equals(paymentScheduleDTO.getId())){
                        paymentScheduleDTO.setPaidInfo(amountCO);
                    }
                }
            }

            List<CashWriteOffCO> offDtoList = writeOffAndPaid.getCashWriteOffHistories();
            if(offDtoList != null && offDtoList.size() >0){
                for(CashWriteOffCO offCO : offDtoList){
                    if(offCO.getDocumentLineId().equals(paymentScheduleDTO.getId())){
                        if(paymentScheduleDTO.getCashWriteOffMessage() == null){
                            paymentScheduleDTO.setCashWriteOffMessage(new ArrayList<>());
                        }

                        paymentScheduleDTO.getCashWriteOffMessage().add(offCO);

                        if(paymentScheduleDTO.getWriteOffAmount() == null){
                            paymentScheduleDTO.setWriteOffAmount(offCO.getWriteOffAmount() == null ? null : offCO.getWriteOffAmount());
                        }else{
                            paymentScheduleDTO.setWriteOffAmount(paymentScheduleDTO.getWriteOffAmount().add(offCO.getWriteOffAmount()));
                        }
                    }
                }
            }

            result.add(paymentScheduleDTO);*/
        }

        return result;
    }


    /**
     * 根据报账单ID删除计划付款行信息
     * @param headerId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    //@LcnTransaction
    public boolean deleteExpenseReportPaymentScheduleByHeaderId(Long headerId){
        //jiu.zhao 支付
        //paymentService.deleteWriteOffForDocumentMessage(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(),headerId,null);
        return delete(new EntityWrapper<ExpenseReportPaymentSchedule>().eq("exp_report_header_id",headerId));
    }

    /**
     * 删除计划付款行
     * @param id
     * @return
     */
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportPaymentSchedule(Long id){
        ExpenseReportPaymentSchedule expenseReportPaymentSchedule = selectById(id);
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(expenseReportPaymentSchedule.getExpReportHeaderId());
        // 判断单据状态 非编辑中、撤回、拒绝的单据，都不能删除
        if(!(expenseReportHeader.getStatus().equals(DocumentOperationEnum.GENERATE.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.WITHDRAW.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.CANCEL.getId()))){
            throw new BizException(RespCode.EXPENSE_REPORT_CANNOT_DELETED,new String[]{expenseReportHeader.getRequisitionNumber()});
        }
        //jiu.zhao 支付
        //paymentService.deleteWriteOffForDocumentMessage(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(),expenseReportPaymentSchedule.getExpReportHeaderId(),id);
        return deleteById(id);
    }

    /**
     * 更新审核状态
     * @param headerId
     * @param auditFlag
     * @param auditDate
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExpenseReportScheduleAduitStatusByHeaderId(Long headerId,
                                                                   String auditFlag,
                                                                   ZonedDateTime auditDate){
        List<ExpenseReportPaymentSchedule> schedules = selectList(new EntityWrapper<ExpenseReportPaymentSchedule>().eq("exp_report_header_id", headerId));
        if(! CollectionUtils.isEmpty(schedules)){
            schedules.stream().forEach(e -> {
                e.setAuditFlag(auditFlag);
                e.setAuditDate(auditDate);
            });
            return updateAllColumnBatchById(schedules);
        }
        return true;
    }

    /**
     * 创建或修改默认行
     * @param expenseReportHeader
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDefaultPaymentSchedule(ExpenseReportHeader expenseReportHeader){
        List<ExpenseReportPaymentSchedule> paymentScheduleList = selectList(
                new EntityWrapper<ExpenseReportPaymentSchedule>()
                        .eq("exp_report_header_id", expenseReportHeader.getId()));
        if (CollectionUtils.isEmpty(paymentScheduleList) && expenseReportHeader.getTotalAmount().compareTo(BigDecimal.ZERO)!=0) {
            ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeader.getDocumentTypeId());
            ExpenseReportPaymentSchedule expensePaymentSchedule = new ExpenseReportPaymentSchedule();
            String paymentMethod = expenseReportType.getPaymentMethod();
            String paymentType = expenseReportType.getPaymentType();

            expensePaymentSchedule.setExpReportHeaderId(expenseReportHeader.getId());
            expensePaymentSchedule.setTenantId(expenseReportHeader.getTenantId());
            expensePaymentSchedule.setSetOfBooksId(expenseReportHeader.getSetOfBooksId());
            expensePaymentSchedule.setCompanyId(expenseReportHeader.getCompanyId());
            expensePaymentSchedule.setDepartmentId(expenseReportHeader.getDepartmentId());
            expensePaymentSchedule.setApplicantId(expenseReportHeader.getApplicantId());
            expensePaymentSchedule.setDescription(expenseReportHeader.getDescription());
            expensePaymentSchedule.setCurrencyCode(expenseReportHeader.getCurrencyCode());
            expensePaymentSchedule.setExchangeRate(expenseReportHeader.getExchangeRate());
            expensePaymentSchedule.setAmount(expenseReportHeader.getTotalAmount());
            expensePaymentSchedule.setFunctionAmount(OperationUtil.safeMultiply(expensePaymentSchedule.getAmount(),expensePaymentSchedule.getExchangeRate()));
            expensePaymentSchedule.setPaymentScheduleDate(ZonedDateTime.now());
            expensePaymentSchedule.setPaymentMethod(paymentMethod);
            expensePaymentSchedule.setPaymentType(paymentType);

            setDefaultAccountInfo(expensePaymentSchedule,expenseReportHeader,expenseReportType);
            expensePaymentSchedule.setConPaymentScheduleLineId(null);
            expensePaymentSchedule.setFrozenFlag("N");
            insert(expensePaymentSchedule);
        }
        //有修改费用行金额，重置计划付款行第一行金额
        if (paymentScheduleList.size() == 1) {
            Long paymentScheduleId = paymentScheduleList.get(0).getId();
            ExpenseReportPaymentSchedule expensePaymentSchedule = this.selectById(paymentScheduleId);
            expensePaymentSchedule.setAmount(expenseReportHeader.getTotalAmount());
            expensePaymentSchedule.setCurrencyCode(expenseReportHeader.getCurrencyCode());
            expensePaymentSchedule.setExchangeRate(expenseReportHeader.getExchangeRate());
            expensePaymentSchedule.setFunctionAmount(expenseReportHeader.getFunctionalAmount());
            updateById(expensePaymentSchedule);
        }
    }

    /**
     * 设置默认收款信息
     * @param expensePaymentSchedule
     * @param expenseReportHeader
     * @param expenseReportType
     */
    private void setDefaultAccountInfo(ExpenseReportPaymentSchedule expensePaymentSchedule,
                      ExpenseReportHeader expenseReportHeader,
                      ExpenseReportType expenseReportType){
        expensePaymentSchedule.setAccountNumber(expenseReportHeader.getAccountNumber());
        expensePaymentSchedule.setAccountName(expenseReportHeader.getAccountName());
        expensePaymentSchedule.setPayeeCategory(expenseReportHeader.getPayeeCategory());
        expensePaymentSchedule.setPayeeId(expenseReportHeader.getPayeeId());
        String payeeType = expenseReportType.getPayeeType();
        if (expensePaymentSchedule.getPayeeId() == null) {
            expensePaymentSchedule.setPayeeCategory(StringUtils.isEmpty(expenseReportHeader.getPayeeCategory()) && !"VENDER".equals(payeeType) ? "EMPLOYEE":expenseReportHeader.getPayeeCategory());
            if("EMPLOYEE".equals(expensePaymentSchedule.getPayeeCategory())){
                expensePaymentSchedule.setPayeeId(expenseReportHeader.getApplicantId());
                UserBankAccountCO bankAccountDTOS = organizationService.getContactPrimaryBankAccountByUserId(expensePaymentSchedule.getPayeeId());
                if (!ObjectUtils.isEmpty(bankAccountDTOS)) {
                    expensePaymentSchedule.setAccountNumber(bankAccountDTOS.getBankAccountNo());
                    expensePaymentSchedule.setAccountName(bankAccountDTOS.getBankAccountName());
                }
            }/*else if("VENDER".equals(expensePaymentSchedule.getPayeeCategory())){
                expensePaymentSchedule.setPayeeId(expenseReportHeader.getPayeeId());
                List<VendorBankAccountCO> info =  organizationService.listVendorBankAccounts(expenseReportHeader.getPayeeId().toString());
                if (!CollectionUtils.isEmpty(info)) {
                    //默认取主账号
                    info.stream().filter(e -> e.getPrimaryFlag()).forEach(vender ->{
                        expensePaymentSchedule.setAccountNumber(vender.getBankAccount());
                        expensePaymentSchedule.setAccountName(vender.getVenBankNumberName());
                    });
                }
            }*/
        }
    }

    /**
     * 保存计划付款行信息
     * @param expensePaymentScheduleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseReportPaymentScheduleDTO createOrUpdatePaymentSchedule(ExpenseReportPaymentScheduleDTO expensePaymentScheduleDTO) {
        ExpenseReportHeader expenseReportHeader = getExpenseReportHeaderById(expensePaymentScheduleDTO.getExpReportHeaderId());
        ExpenseReportPaymentSchedule expensePaymentSchedule = new ExpenseReportPaymentSchedule();
        BeanUtils.copyProperties(expensePaymentScheduleDTO, expensePaymentSchedule);
        if(expensePaymentSchedule.getId() == null){
            expensePaymentSchedule.setTenantId(expenseReportHeader.getTenantId());
            expensePaymentSchedule.setSetOfBooksId(expenseReportHeader.getSetOfBooksId());
            expensePaymentSchedule.setCompanyId(expenseReportHeader.getCompanyId());
            expensePaymentSchedule.setDepartmentId(expenseReportHeader.getDepartmentId());
            expensePaymentSchedule.setApplicantId(expenseReportHeader.getApplicantId());
        }else{
            if(expensePaymentSchedule.getCshTransactionClassId() == null){
                throw new BizException(RespCode.EXPENSE_REPORT_PAYMENT_SCHEDULE_CSH_TRANSACTION_NULL);
            }
        }
        expensePaymentSchedule.setCurrencyCode(expenseReportHeader.getCurrencyCode());
        expensePaymentSchedule.setExchangeRate(expenseReportHeader.getExchangeRate());
        expensePaymentSchedule.getAmount();
        expensePaymentSchedule.setFunctionAmount(OperationUtil.safeMultiply(expensePaymentSchedule.getAmount(),expensePaymentSchedule.getExchangeRate()));
        expensePaymentSchedule.setWriteOffAmount(BigDecimal.ZERO);
        BigDecimal contractLineAmount = expensePaymentScheduleDTO.getContractLineAmount();
        if(expensePaymentSchedule.getConPaymentScheduleLineId() != null){
            if (contractLineAmount != null) {
                if (contractLineAmount.doubleValue() < expensePaymentScheduleDTO.getAmount().doubleValue()) {
                    throw new BizException(RespCode.EXPENSE_REPORT_PAYMENT_SCHEDULE_AMOUT_TOO_MUCH);
                }
            }
        }
        //jiu.zhao 支付
        /*if(expensePaymentSchedule.getCshTransactionClassId() != null){
            CashDefaultFlowItemCO cashDefaultFlowItemByTransactionClassId = paymentService.getCashDefaultFlowItemByTransactionClassId(expensePaymentSchedule.getCshTransactionClassId());
            if(cashDefaultFlowItemByTransactionClassId != null){
                expensePaymentSchedule.setCashFlowItemId(cashDefaultFlowItemByTransactionClassId.getCashFlowItemId());
            }
        }*/
        insertOrUpdateAllColumn(expensePaymentSchedule);
        Wrapper<ExpenseReportPaymentSchedule> wrapper = new EntityWrapper<ExpenseReportPaymentSchedule>()
                .eq("exp_report_header_id", expenseReportHeader.getId()).groupBy("exp_report_header_id");
        wrapper.setSqlSelect("sum(amount) amount,sum(function_amount) functionAmount");
        ExpenseReportPaymentSchedule expenseReportPaymentScheduleForAmount = selectOne(wrapper);
        if(expenseReportPaymentScheduleForAmount.getAmount().compareTo(expenseReportHeader.getTotalAmount()) == 1){
            throw new BizException(RespCode.EXPENSE_REPORT_PAYMENT_SCHEDULE_AMOUT_GREATER_DOCUMENT_AMOUNT);
        }
        // 处理尾插
        if(expenseReportPaymentScheduleForAmount.getAmount().compareTo(expenseReportHeader.getTotalAmount()) == 0
                && expenseReportPaymentScheduleForAmount.getFunctionAmount().compareTo(expenseReportHeader.getFunctionalAmount()) != 0){
            BigDecimal subtract = expenseReportHeader.getFunctionalAmount().subtract(expenseReportPaymentScheduleForAmount.getFunctionAmount());
            ExpenseReportPaymentSchedule expenseReportPaymentScheduleMax = baseMapper.getExpenseReportPaymentScheduleAmountMax(expenseReportHeader.getId()).get(0);
            expenseReportPaymentScheduleMax.setFunctionAmount(expenseReportPaymentScheduleMax.getFunctionAmount().add(subtract));
        }
        BeanUtils.copyProperties(expensePaymentSchedule,expensePaymentScheduleDTO);
        return expensePaymentScheduleDTO;
    }

    private ExpenseReportHeader getExpenseReportHeaderById(Long expReportHeaderId) {
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(expReportHeaderId);
        if (expenseReportHeader == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        return expenseReportHeader;
    }

    /**
     * 根据id获取一个付款计划行(经中间件翻译)
     *
     * @param id
     * @return ExpensePaymentSchedule
     */
    public ExpenseReportPaymentScheduleDTO getExpensePaymentScheduleById(Long id) {
        ExpenseReportPaymentSchedule expensePaymentSchedule = this.selectById(id);
        if (expensePaymentSchedule == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_PAYMENT_SCHEDULE_IS_NULL);
        }
        //jiu.zhao 支付
        /*ExpenseReportHeader expenseReportHeader = getExpenseReportHeaderById(expensePaymentSchedule.getExpReportHeaderId());
        Map<Long, BigDecimal> writeOffInfo = paymentService.listDocumentWriteOffAmount(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(), expensePaymentSchedule.getExpReportHeaderId(), null);
        ExpenseReportPaymentScheduleDTO expensePaymentScheduleDTO = toResource(expenseReportHeader,expensePaymentSchedule);
        BigDecimal writeOffAmount = writeOffInfo.get(expensePaymentScheduleDTO.getId());
        expensePaymentScheduleDTO.setWriteOffAmount(writeOffAmount == null ? BigDecimal.ZERO : writeOffAmount);
        return expensePaymentScheduleDTO;*/
        return null;
    }

    /**
     * 计划付款行列表查询
     * @param expReportHeaderId
     * @param page
     * @return
     */
    public List<ExpenseReportPaymentScheduleDTO> getExpensePaymentScheduleByCond(Long expReportHeaderId, Page page) {
        ExpenseReportHeader expenseReportHeader = getExpenseReportHeaderById(expReportHeaderId);
        List<ExpenseReportPaymentSchedule> paymentScheduleList = baseMapper.selectPage(page, new EntityWrapper<ExpenseReportPaymentSchedule>()
                .eq("exp_report_header_id", expReportHeaderId).orderBy("created_date"));
        List<ExpenseReportPaymentScheduleDTO> result = paymentScheduleList.stream().map(e -> toResource(expenseReportHeader,e)).collect(toList());
        //jiu.zhao 支付
        /*if(result != null && result.size() > 0){
            //根据报账单头ID，取报账单计划付款行的核销,支付信息集合
            CashWriteOffHistoryAndPaymentAmountCO writeOffAndPaid = paymentService.listCashWriteOffHistoryAll(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(),expReportHeaderId,null);
            int index = PageUtil.getPageStartIndex(page) + 1;
            for(ExpenseReportPaymentScheduleDTO line : result){
                line.setIndex(index);
                index ++;
                //付款行的已付款信息：已付款金额和已退款金额
                List<PublicReportLineAmountCO> paidInfoDTOList =  writeOffAndPaid.getPublicAmounts();
                if(paidInfoDTOList != null && paidInfoDTOList.size() > 0){
                    paidInfoDTOList.stream().filter(e -> e.getDocumentLineId().equals(line.getId())).map(e -> {
                        line.setPaidInfo(e);
                        return line;
                    }).collect(toList());
                }

                List<CashWriteOffCO> offDtoList = writeOffAndPaid.getCashWriteOffHistories();
                if(offDtoList != null && offDtoList.size() >0){
                    offDtoList.stream().filter(e -> e.getDocumentLineId().equals(line.getId())).map(e -> {
                        line.getCashWriteOffMessage().add(e);
                        if(line.getWriteOffAmount() == null){
                            line.setWriteOffAmount(e.getWriteOffAmount() == null ? null : e.getWriteOffAmount());
                        }else{
                            line.setWriteOffAmount(line.getWriteOffAmount().add(e.getWriteOffAmount()));
                        }
                        return line;
                    }).collect(toList());
                }
            }
        }*/
        return result;
    }

    /**
     * domain转换为dto
     * @param expenseReportHeader
     * @param expensePaymentSchedule
     * @return
     */
    private ExpenseReportPaymentScheduleDTO toResource(ExpenseReportHeader expenseReportHeader, ExpenseReportPaymentSchedule expensePaymentSchedule) {
        ExpenseReportPaymentScheduleDTO paymentScheduleDTO = new ExpenseReportPaymentScheduleDTO();
        BeanUtils.copyProperties(expensePaymentSchedule, paymentScheduleDTO);

        //付款方式类型
        if(StringUtils.isNotEmpty(expensePaymentSchedule.getPaymentMethod())){
            SysCodeValueCO paymentMethodSysCode = organizationService.getSysCodeValueByCodeAndValue("2105",expensePaymentSchedule.getPaymentMethod());
            paymentScheduleDTO.setPaymentMethodName(paymentMethodSysCode == null ? null : paymentMethodSysCode.getName());
        }
        //付款方式
        if(StringUtils.isNotEmpty(expensePaymentSchedule.getPaymentType())){
            SysCodeValueCO paymentMethodSysCode = organizationService.getSysCodeValueByCodeAndValue("ZJ_PAYMENT_TYPE",expensePaymentSchedule.getPaymentType());
            paymentScheduleDTO.setPaymentTypeName(paymentMethodSysCode == null ? null : paymentMethodSysCode.getName());
        }

        //现金事物分类、默认现金事物流量项
        //jiu.zhao 支付
        /*if (expensePaymentSchedule.getCshTransactionClassId() != null) {
            CashTransactionClassCO cashTransactionClass = paymentService.getCashTransactionClassById(expensePaymentSchedule.getCshTransactionClassId());
            paymentScheduleDTO.setCshTransactionClassName(cashTransactionClass.getDescription());
        }*/
        //收款对象类型
        if(StringUtils.isNotEmpty(expensePaymentSchedule.getPayeeCategory())) {
            SysCodeValueCO payeeSysCode = organizationService.getSysCodeValueByCodeAndValue("2107", expensePaymentSchedule.getPayeeCategory());
            paymentScheduleDTO.setPayeeCategoryName(payeeSysCode == null ? null : payeeSysCode.getName());
        }
        //收款对象
        String payeeName = null;
        String payCode = null;
        if(expensePaymentSchedule.getPayeeCategory() != null ) {
            if (expensePaymentSchedule.getPayeeCategory().equals("EMPLOYEE")) {
                ContactCO user = organizationService.getUserById(expensePaymentSchedule.getPayeeId());
                payeeName = user.getFullName();
                payCode = user.getEmployeeCode();
            } else if (expensePaymentSchedule.getPayeeCategory().equals("VENDER")) {
                VendorInfoCO info = organizationService.getOneVendorInfoById(expensePaymentSchedule.getPayeeId());
                if (info != null) {
                    payCode = info.getVenderCode();
                    payeeName = info.getVenNickname();
                }
            }
        }
        paymentScheduleDTO.setPayeeCode(payCode);
        paymentScheduleDTO.setPayeeName(payeeName);
        //合同编号
        //jiu.zhao 合同
        /*if(expensePaymentSchedule.getConPaymentScheduleLineId() != null){
            ContractHeaderLineCO contractLine = contractService.getContractLine(expenseReportHeader.getContractHeaderId(), expensePaymentSchedule.getConPaymentScheduleLineId());
            paymentScheduleDTO.setContractHeaderLineMessage(contractLine);
        }*/
        return paymentScheduleDTO;
    }

    public List<ExpenseReportPaymentSchedule> getExpensePaymentScheduleByReportHeaderId(Long reportHeaderId) {
        return this.selectList(new EntityWrapper<ExpenseReportPaymentSchedule>()
                .eq("exp_report_header_id",reportHeaderId));
    }

    public List<ExpensePaymentScheduleCO> getExpPublicReportScheduleByIds(List<Long> ids) {
        List<ExpensePaymentScheduleCO> result = CollectionUtils.isEmpty(ids) ? Arrays.asList() : baseMapper.getExpPublicReportScheduleByIds(ids);
        return result;
    }

    public Page<ExpensePaymentScheduleCO> getExpPublicReportScheduleByContractHeaderId(List<Long> ids, Page page) {
            Page<ExpensePaymentScheduleCO> result= new Page<>();
            List<ExpensePaymentScheduleCO> expensePaymentScheduleCOS =  baseMapper.getExpPublicReportScheduleByContractHeaderId(ids,page);
                 // 将查询出来的单据 根据头id在一次进行查询。
            List<Long>headerIds=expensePaymentScheduleCOS.stream().map(ExpensePaymentScheduleCO::getExpReportHeaderId).collect(Collectors.toList());
            Map<Long,Integer> map = new HashMap<>();
            headerIds.stream().forEach(e->{
                    List<ExpenseReportPaymentSchedule> list = baseMapper.getExpPublicReportScheduleByHeaderId(e);
                    for (int i = 0;i<list.size();i++) {
                        map.put(list.get(i).getId(),i+1);
                    }
                });
                expensePaymentScheduleCOS.stream().forEach(e ->{
                    e.setLineNumber(map.get(e.getId()));
                });
            result.setRecords(expensePaymentScheduleCOS);
            result.setTotal(page.getTotal());
            return  result;
        }

    public Map<Long,Integer> getExpPublicReportScheduleMapByHeaderId(Long headerId)    {
        Map<Long,Integer> map = new HashMap<>();
        List<ExpenseReportPaymentSchedule> list = baseMapper.getExpPublicReportScheduleByHeaderId(headerId);
        for (int i = 0;i<list.size();i++) {
            map.put(list.get(i).getId(),i+1);
        }
        return map;
    }
}
