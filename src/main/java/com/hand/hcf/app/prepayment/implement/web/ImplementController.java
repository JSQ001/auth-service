package com.hand.hcf.app.prepayment.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeService;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionHeadService;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionLineService;
import com.hand.hcf.app.prepayment.web.dto.CashPrepaymentQueryDTO;
import com.hand.hcf.app.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
//@RequestMapping(value = "/api/implement/prepayment")
//@AllArgsConstructor
public class ImplementController {

    @Autowired
    private CashPaymentRequisitionHeadService cashPaymentRequisitionHeadService;

    @Autowired
    private CashPaymentRequisitionLineService cashPaymentRequisitionLineService;

    @Autowired
    private CashPayRequisitionTypeService cashSobPayReqTypeService;

    @Autowired
    private MapperFacade mapper;

    //根据申请单头id和预付款单头id查询相应金额
//    @GetMapping("/get/amount/by/requisition/and/prepayment")
    public Map<String, Double> getTotalAmountByRequisitionAndPrepayment(
            @RequestParam Long requisitionHeadId
            , @RequestParam(value = "prepaymentHeadId", required = false) Long prepaymentHeadId
            , @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId
    ) {
        return cashPaymentRequisitionHeadService.getAmountByRequisitionAndPrepayment(requisitionHeadId, prepaymentHeadId, setOfBooksId);
    }

    /**
     * 根据预付款行ID查询行信息
     *
     * @param id
     * @return
     */
//    @GetMapping("/get/cash/prepayment/requisition/line/{id}")
    public CashPaymentRequisitionLineCO getCashPaymentRequisitionLineById(@PathVariable Long id) {
        CashPaymentRequisitionLine cashPaymentRequisitionLine = cashPaymentRequisitionLineService.selectById(id);
        if (cashPaymentRequisitionLine != null) {
            return mapper.map(cashPaymentRequisitionLine, CashPaymentRequisitionLineCO.class);
        }
        return null;
    }

    /**
     * 根据预付款头ID查询头信息
     *
     * @param id
     * @return
     */
//    @GetMapping("/get/cash/prepayment/requisition/head/{id}")
    public CashPaymentRequisitionHeaderCO getCashPaymentRequisitionHeadById(@PathVariable Long id) {
        CashPaymentRequisitionHead cashPaymentRequisitionHead = cashPaymentRequisitionHeadService.selectById(id);
        if (cashPaymentRequisitionHead != null) {
            return mapper.map(cashPaymentRequisitionHead, CashPaymentRequisitionHeaderCO.class);
        }
        return null;
    }

    /**
     * 根据预付款行ID批量查询行信息
     *
     * @param ids
     * @return
     */
//    @PostMapping("/get/cash/prepayment/requisition/lines")
    public List<CashPaymentRequisitionLineCO> listCashPaymentRequisitionLineById(@RequestBody List<Long> ids) {
        List<CashPaymentRequisitionLine> cashPaymentRequisitionLines = cashPaymentRequisitionLineService.selectBatchIds(ids);
        List<CashPaymentRequisitionLineCO> cashPaymentRequisitionLineCOS = new ArrayList<CashPaymentRequisitionLineCO>();

        for (CashPaymentRequisitionLine cashPaymentRequisitionLine : cashPaymentRequisitionLines) {
            cashPaymentRequisitionLineCOS.add(mapper.map(cashPaymentRequisitionLine, CashPaymentRequisitionLineCO.class));
        }

        return cashPaymentRequisitionLineCOS;
    }

    //    @PostMapping("/get/header/and/line/by/line")
    public List<CashPaymentRequisitionLineCO> listHeadAndLineByLineId(@RequestBody List<Long> lineIds) {
        List<CashPaymentRequisitionLineCO> cashPaymentRequisitionLineCOS = new ArrayList<>();
        List<CashPaymentRequisitionLine> headerAndLineByLine = cashPaymentRequisitionHeadService.getHeaderAndLineByLine(lineIds);
        for (CashPaymentRequisitionLine cashPaymentRequisitionLine : headerAndLineByLine) {
            cashPaymentRequisitionLineCOS.add(mapper.map(cashPaymentRequisitionLine, CashPaymentRequisitionLineCO.class));
        }
        return cashPaymentRequisitionLineCOS;
    }

    //    @GetMapping("/cash/pay/requisition/type/{id}")
    public CashPayRequisitionTypeSummaryCO getCashPayRequisitionTypeById(@PathVariable Long id) {
        CashPayRequisitionTypeSummaryCO dto = mapper.map(cashSobPayReqTypeService.getCashPayRequisitionType(id), CashPayRequisitionTypeSummaryCO.class);
        return dto;
    }

    /*
     *根据合同ID查询预付款单头行信息--合同模块需要的接口
     * */
    //@GetMapping("/get/by/contract/number")

    public List<CashPaymentParamCO> listPrepaymentByContractId(@RequestParam(required = false) Long contractId) {
        List<CashPaymentParamCO> list = new ArrayList<>();
        List<CashPaymentParamCO> cashPaymentParamDTOS = cashPaymentRequisitionHeadService.getByContractId(contractId);
        return mapper.map(cashPaymentParamDTOS, list.getClass());
    }

    public void updatePrepaymentRequisitionStatusByOid(@RequestParam("oid") String oid,
                                       @RequestParam("status") Integer status,
                                       @RequestParam("userId") Long userId,
                                       @RequestParam(value = "isWorkflow", required = false) Boolean isWorkflow){
        if (isWorkflow == null) {
            isWorkflow = false;
        }
        cashPaymentRequisitionHeadService.updateDocumentStatusByOid(oid, status, userId, isWorkflow);
        return ;
    }

    public CashPaymentRequisitionHeaderCO getCashPaymentRequisitionHeaderByOid(@RequestParam @NotNull String oid) {
        return mapper.map(cashPaymentRequisitionHeadService.selectByOid(oid), CashPaymentRequisitionHeaderCO.class);
    }

    public List<CashPaymentRequisitionHeaderCO> listPrepaymentRequisitionHeadersByDocumentOid(@RequestBody List<UUID> oids) {
        return cashPaymentRequisitionHeadService.selectHeadersByDocumentOids(oids);
    }

    public Page<CashPaymentRequisitionHeaderCO> pagePrepaymentRequisitionHeadersByInput(@RequestBody PaymentRequisitionQueryCO cashPrepaymentQueryCO,
                                                                                                      @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                                                      @RequestParam(value = "size", required = false,defaultValue = "10") int size)throws ParseException {
        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<CashPaymentRequisitionHeaderCO> res = cashPaymentRequisitionHeadService.selectHeadersByInput(mapper.map(cashPrepaymentQueryCO, CashPrepaymentQueryDTO.class), mybatisPage);
        return res;
    }

    public Boolean getPrepaymentRequisitionHeadHasLine(@RequestParam(value = "oid") String oid) {
        return cashPaymentRequisitionHeadService.HeadHasLine(oid);
    }

    public Boolean saveToPaymentByPrepaymentRequisitionHeadId(@RequestParam(value = "headId")Long headId) {
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadService.selectById(headId);
        return cashPaymentRequisitionHeadService.pushToPayment(head);
    }

    public CashPayRequisitionTypeCO getPaymentRequisitionTypeById(@RequestParam(value = "id") Long id) {
        return  mapper.map(cashSobPayReqTypeService.selectById(id), CashPayRequisitionTypeCO.class);
    }


    private <S, D> Page<D> mapAsPage(Page<S> source, Class<D> destinationClass){
        Page<D> result = new Page<>();
        List<S> sourceRecords = source.getRecords();
        source.setRecords(new ArrayList<>());
        mapper.map(source, result);
        List<D> resultRecords = mapper.mapAsList(sourceRecords, destinationClass);
        result.setRecords(resultRecords);
        return result;
    }
    public List<CashPaymentRequisitionLineCO> listPrepaymentRequisitionLineByHeadID(@RequestParam(value = "headId") Long headId){
        List<CashPaymentRequisitionLine> list = cashPaymentRequisitionLineService.selectList(new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
                .orderBy("created_date")
        );
        List<CashPaymentRequisitionLineCO> lineDTOS = new ArrayList<>();
        list.stream().forEach(cashPaymentRequisitionLine -> {
            lineDTOS.add(mapper.map(cashPaymentRequisitionLine,CashPaymentRequisitionLineCO.class));
        });
        return lineDTOS;
    }

    /**
     * 根据单据头ID提交预付款单
     *
     * @param requisitionHeaderId
     * @param status
     * @return
     */
    public Boolean submitCashPaymentRequisition(@RequestParam(value = "requisitionHeaderId") Long requisitionHeaderId,
                                                @RequestParam(value = "status") Integer status) {
        return cashPaymentRequisitionHeadService.submitCashPaymentRequisitionByRequisitionNumber(requisitionHeaderId, status);
    }

    /**
     * 更新单据状态
     *
     * @param requisitionHeaderId
     * @param status
     * @return
     */
    public Boolean updateCashPaymentRequisitionStatus(@RequestParam(value = "requisitionHeaderId") Long requisitionHeaderId,
                                                      @RequestParam(value = "status") Integer status) {
        return cashPaymentRequisitionHeadService.updateCashPaymentRequisitionStatus(requisitionHeaderId, status);
    }

    /**
     * 根据申请单头ID获取关联的预付款单头
     *
     * @param applicationHeadId
     * @return
     */

    public CashPaymentRequisitionHeaderCO getCashPaymentRequisitionHeaderByApplicationHeaderId(
            @RequestParam(value = "applicationHeadId") Long applicationHeadId) {
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadService
                .getCashPaymentRequisitionHeaderByApplicationHeaderId(applicationHeadId);
        if (head == null) {
            return null;
        } else {
            return mapper.map(head, CashPaymentRequisitionHeaderCO.class);
        }
    }

    public String getFormTypeNameByFormTypeId(@RequestParam("id") Long id) {
        CashPayRequisitionType requisitionType = cashSobPayReqTypeService.selectById(id);
        return requisitionType != null ? requisitionType.getTypeName() : null;
    }



    /**
     * 根据费用申请单头id ，查询从费用申请单详情页面新建的预付款单行
     * @param refDocumentId
     * @param page
     * @param size
     * @return
     */
//    @GetMapping(value = "/api/implement/prepayment/query/prepayment/line/by/refDocumentId")

    public Page<CashPaymentRequisitionLineCO> pagePrepaymentLineByRefDocumentId(@RequestParam Long refDocumentId,
                                                                         @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                         @RequestParam(value = "size", required = false,defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<CashPaymentRequisitionLineCO> result = cashPaymentRequisitionHeadService.pagePrepaymentLineByRefDocumentId(refDocumentId,mybatisPage);
        return result;
    }
}
