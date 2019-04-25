package com.hand.hcf.app.payment.web;


import com.hand.hcf.app.payment.service.PaymentRequisitionLineService;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionLineWebDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 12:02 2018/1/24
 * @Modified by
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/acp/requisition/line")
public class PaymentRequisitionLineController {
    private final PaymentRequisitionLineService service;


    /**
     * @api {DELETE} {{payment-service_url}}/api/acp/requisition/line/query/{id}
     * @apiGroup PaymentService
     * @apiDescription 根据付款申请单行ID获取付款申请行
     * @apiParam (paymentRequisitionLineDTO) {Long} id  付款申请单行ID
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<PaymentRequisitionLineWebDTO> queryLineById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(service.selectByLineId(id));
    }
}

