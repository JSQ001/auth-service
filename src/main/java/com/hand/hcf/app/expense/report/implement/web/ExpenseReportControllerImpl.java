package com.hand.hcf.app.expense.report.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ExpenseApportionQueryCO;
import com.hand.hcf.app.common.co.ExpenseApportionQueryParamCO;
import com.hand.hcf.app.common.co.ExpensePaymentScheduleCO;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.service.ExpenseReportDistService;
import com.hand.hcf.app.expense.report.service.ExpenseReportHeaderService;
import com.hand.hcf.app.expense.report.service.ExpenseReportPaymentScheduleService;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/3/29 15:50
 * @version: 1.0.0
 */
@RestController
public class ExpenseReportControllerImpl {

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    @Autowired
    private ExpenseReportDistService expenseReportDistService;

    @Autowired
    private ExpenseReportPaymentScheduleService expenseReportPaymentScheduleService;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private MessageService messageService;

    /**
     * 更新报账单状态
     * @param headerId     申请单头ID
     * @param status       状态
     */
    public void updateDocumentStatus(@RequestParam("headerId") Long headerId,
                                     @RequestParam("status") Integer status,
                                     @RequestParam(value = "approvalText",required = false) String approvalText) {
        expenseReportHeaderService.updateDocumentStatus(headerId, status, approvalText);
    }

    public boolean checkCreateVoucher(@RequestParam("headerId") Long headerId) {
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(headerId);
        if (null == expenseReportHeader){
            return false;
        }
        return expenseReportHeader.getJeCreationStatus() == null ? false : expenseReportHeader.getJeCreationStatus();
    }

    public List<ExpensePaymentScheduleCO> getExpPublicReportScheduleByIds(@RequestBody List<Long> ids){
        return expenseReportPaymentScheduleService.getExpPublicReportScheduleByIds(ids);
    }


    public Page<ExpensePaymentScheduleCO> getExpPublicReportScheduleByContractHeaderId(List<Long> ids,int page, int size) {
      Page pageInfo= PageUtil.getPage(page,size);
        return expenseReportPaymentScheduleService.getExpPublicReportScheduleByContractHeaderId(ids,pageInfo);
    }


    public List<ExpenseApportionQueryCO> listExpenseReportByDocumentId(List<ExpenseApportionQueryParamCO> parameteParamList) {
        List<ExpenseApportionQueryCO> expenseApportionQueryCOList = new ArrayList<>();
        parameteParamList.stream().forEach(param->{
            ExpenseApportionQueryCO expenseApportionQueryCO = new ExpenseApportionQueryCO();
            ExpenseReportHeader header = expenseReportHeaderService.selectById(param.getDocumentHeaderId());
            if(header != null) {
                String auditFlag = header.getAuditFlag();
                ExpenseReportType expenseReportType = expenseReportTypeService.selectById(header.getDocumentTypeId());
                if(expenseReportType != null) {
                    expenseApportionQueryCO.setDocumentTypeName(expenseReportType.getReportTypeName());
                }
                expenseApportionQueryCO.setRequisitionNumber(header.getRequisitionNumber());
                expenseApportionQueryCO.setAuditFlag(header.getAuditFlag());
                if(header.getAuditFlag() != null){
                    if (auditFlag != null) {
                        if (auditFlag.equals("N")){
                            auditFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_APPROVING);
                        }else{
                            auditFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_APPROVED);
                        }
                        expenseApportionQueryCO.setAuditFlag(auditFlag);
                    }
                }
                expenseApportionQueryCO.setStatus(header.getStatus());
                expenseApportionQueryCO.setRequisitionDate(header.getRequisitionDate());
                expenseApportionQueryCO.setDescription(header.getDescription());
            }
            ExpenseReportDist dist = expenseReportDistService.selectById(param.getDocumentLineId());
            if(dist != null){
                expenseApportionQueryCO.setDocumentLineId(dist.getId());
            }
            expenseApportionQueryCOList.add(expenseApportionQueryCO);
        });

        return expenseApportionQueryCOList;
    }

    /**
     * 根据单据类型id查询单据类型名称
     *
     * @param id
     * @return
     */

    public String getFormTypeNameByFormTypeId(Long id) {
        ExpenseReportType reportType = expenseReportTypeService.selectById(id);
        if (reportType != null) {
            return reportType.getReportTypeName();
        } else {
            return null;
        }
    }

    /**
     * 根据头ID，查询计划付款行的id-序号的map
     * @param headerId
     * @return
     */

    public Map<Long,Integer> getExpPublicReportScheduleMapByHeaderId(Long headerId) {
        return expenseReportPaymentScheduleService.getExpPublicReportScheduleMapByHeaderId(headerId);
    }
}
