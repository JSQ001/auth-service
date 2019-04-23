package com.hand.hcf.app.prepayment.excel.controllers;




import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionHeadService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "/api")
@AllArgsConstructor
@Slf4j
public class ExcelController {

    private final ExcelExportService excelService;
    private final CashPaymentRequisitionHeadService cashPaymentRequisitionHeadService;

    /**
     * 通用导出方法
     * @throws IOException
     */
    @RequestMapping(value = "/export")
    public void createXLS(HttpServletRequest request, @RequestBody ExportConfig exportConfig,
                          HttpServletResponse response,
                          @RequestParam(value = "companyId",required = false) Long companyId,
                          @RequestParam(value = "requisitionNumber",required = false) String requisitionNumber,
                          @RequestParam(value = "typeId",required = false)Long typeId,
                          @RequestParam(value = "status",required = false)Integer status,
                          @RequestParam(value = "unitId",required = false)Long unitId,
                          @RequestParam(value = "applyId",required = false)Long applyId,
                          @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                          @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                          @RequestParam(value = "amountFrom",required = false)Double amountFrom,
                          @RequestParam(value = "amountTo",required = false)Double amountTo,
                          @RequestParam(value = "noWriteAmountFrom",required = false)Double noWriteAmountFrom,
                          @RequestParam(value = "noWriteAmountTo",required = false)Double noWriteAmountTo,
                          @RequestParam(value = "remark",required = false)String remark
                          ) throws IOException {
        /*//string转datetime
        ZonedDateTime dateFrom=null;
        ZonedDateTime dateTo=null;

        if(!org.springframework.util.StringUtils.isEmpty(applyDateFrom)){
            dateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        }
        //结束日期不为空，则+1
        if(!org.springframework.util.StringUtils.isEmpty(applyDateTo)){
            dateTo = DateUtil.string2ZonedDateTimeAddOne(applyDateTo);
        }

        List<CashWriteOffDocumentAmountCO> documentAmountDTOList = PaymentModuleInterface.getCashWriteOffDocumentAmountDTOByInput(noWriteAmountFrom, noWriteAmountTo, null,OrgInformationUtil.getCurrentTenantID());
        Map<Long, CashWriteOffDocumentAmountCO> writeOffDocumentAmountDTOMap = documentAmountDTOList.stream().collect(Collectors.toMap(CashWriteOffDocumentAmountCO::getDocumentHeaderId, (p) -> p));

        Wrapper<CashPaymentRequisitionHead> wrapper = new EntityWrapper<CashPaymentRequisitionHead>()
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantID())
                .eq(companyId != null, "company_id", companyId)
                .like(StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .eq(typeId != null, "payment_req_type_id", typeId)
                .eq(status != null, "status", status)
                .eq(unitId != null, "unit_id", unitId)
                .eq(applyId != null, "employee_id", applyId)
                .ge(applyDateFrom != null, "requisition_date", dateFrom)
                .le(applyDateTo != null, "requisition_date", dateTo)
                .ge(amountFrom != null, "advance_payment_amount", amountFrom)
                .le(amountTo != null, "advance_payment_amount", amountTo)
                .like(StringUtils.isNotEmpty(remark), "description", remark);

        List<Integer> statusInteger = new ArrayList<>();
        statusInteger.add(DocumentOperationEnum.GENERATE.getId());
        statusInteger.add(DocumentOperationEnum.APPROVAL.getId());
        statusInteger.add(DocumentOperationEnum.WITHDRAW.getId());
        statusInteger.add(DocumentOperationEnum.APPROVAL_REJECT.getId());
        if(noWriteAmountFrom == null && noWriteAmountTo == null){
            wrapper = wrapper
                    .orderBy("requisition_number", false);
        }else {
            // 获取不满足条件的单据信息
            String ids = "";
            List<Long> excludeDocumentList = PaymentModuleInterface.getExcludeDocumentCashWriteOffAmountDTOByInput(noWriteAmountFrom, noWriteAmountTo, null,OrgInformationUtil.getCurrentTenantID());
            if(CollectionUtils.isNotEmpty(excludeDocumentList)){
                ids = StringUtils.join(excludeDocumentList, ",");
            }
            wrapper = wrapper
//                    .in("id", documentAmountDTOList.stream().map(CashWriteOffDocumentAmountCO::getDocumentHeaderId).collect(Collectors.toList()))
//                    .orNew()
//                    .in("status",statusInteger)
//                    .and()
                    .notExists(! "".equals(ids),"select 1 from dual where id in (" + ids + ")")
                    .ge(noWriteAmountFrom!=null,"advance_payment_amount",noWriteAmountFrom)
                    .le(noWriteAmountTo!=null,"advance_payment_amount",noWriteAmountTo)
                    .eq(status!=null,"status",status)
                    .orderBy("requisition_number", false)
            ;
        }

        Map<String,Object> param = new HashMap<>();
        param.put("ew",wrapper);

        //处理导出逻辑
        excelService.doExcel(exportConfig,wrapper, request, httpServletResponse,writeOffDocumentAmountDTOMap,companyId,typeId,applyId);
*/

        // 如果涉及多效率问题可以重新编写涉及三方接口的调用过程。此处直接使用查询的方法即可


        //string转datetime
        ZonedDateTime dateFrom = null;
        ZonedDateTime dateTo = null;
        if (!org.springframework.util.StringUtils.isEmpty(applyDateFrom)) {
            dateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        }
        //结束日期不为空，则+1
        if (!org.springframework.util.StringUtils.isEmpty(applyDateTo)) {
            dateTo = DateUtil.string2ZonedDateTimeAddOne(applyDateTo);
        }

        Page<CashPaymentRequisitionHead> headByQuery = cashPaymentRequisitionHeadService.getHeadByQuery(companyId,
                requisitionNumber,
                typeId,
                status,
                unitId,
                applyId,
                dateFrom,
                dateTo,
                amountFrom,
                amountTo,
                noWriteAmountFrom,
                noWriteAmountTo,
                remark,
                new Page<CashPaymentRequisitionHead>(1, 0));
        int total = TypeConversionUtils.parseInt(headByQuery.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        ZonedDateTime finalDateFrom = dateFrom;
        ZonedDateTime finalDateTo = dateTo;
        excelService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<CashPaymentRequisitionHead, CashPaymentRequisitionHead>() {
            @Override
            public int getTotal() {
                return total;
            }
            @Override
            public List<CashPaymentRequisitionHead> queryDataByPage(Page page) {

                return cashPaymentRequisitionHeadService.getHeadByQuery(
                        companyId, requisitionNumber, typeId, status, unitId, applyId, finalDateFrom, finalDateTo, amountFrom, amountTo, noWriteAmountFrom, noWriteAmountTo, remark, page
                ).getRecords();
            }

            @Override
            public CashPaymentRequisitionHead toDTO(CashPaymentRequisitionHead head) {
                // 由于原查询状态是由前端赋值，所以在此赋值
                head.setStringRequisitionDate(DateUtil.ZonedDateTimeToString(head.getRequisitionDate()));
                detailStatus(head);
                return head;
            }

            @Override
            public Class<CashPaymentRequisitionHead> getEntityClass() {
                return CashPaymentRequisitionHead.class;
            }
        },threadNumber, request, response);


    }


    private void detailStatus(CashPaymentRequisitionHead headerDTO){
        switch (headerDTO.getStatus()){
            case (1001):
                headerDTO.setStatusName("编辑中");
                break;
            case (1002):
                headerDTO.setStatusName("审批中");
                break;
            case (1003):
                headerDTO.setStatusName("撤回");
                break;
            case (1004):
                headerDTO.setStatusName("审批通过");
                break;
            case (1005):
                headerDTO.setStatusName("审批驳回");
                break;
            case (2001):
                headerDTO.setStatusName("审核驳回");
                break;
            case (2002):
                headerDTO.setStatusName("审核通过");
                break;
            case (2003):
                headerDTO.setStatusName("支付中");
                break;
            case (2004):
                headerDTO.setStatusName("支付成功");
                break;
            default:
                headerDTO.setStatusName("未知");
                break;
        }
    }
}
