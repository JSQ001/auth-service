package com.hand.hcf.app.workflow.web;

import com.hand.hcf.app.workflow.dto.form.ApprovalFormPropertyRuleDTO;
import com.hand.hcf.app.workflow.service.ApprovalFormPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api")
public class ApprovalFormPropertyController {

    @Autowired
    private ApprovalFormPropertyService approvalFormPropertyService;

    /**
     * 查询表单属性规则
     *
     * @param customFormOid
     * @return
     */
    @RequestMapping(value = "/customForm/property/{customFormOid}", method = RequestMethod.GET)
    public ResponseEntity<ApprovalFormPropertyRuleDTO> getOutCustomEnumerations(@PathVariable UUID customFormOid) {
        return ResponseEntity.ok(approvalFormPropertyService.selectByFormOid(customFormOid));
    }

    /**
     * 新建表单属性规则
     *
     * @param approvalFormPropertyRuleDTO
     * @return
     */
    @RequestMapping(value = "/customForm/property", method = RequestMethod.POST)
    public ResponseEntity<ApprovalFormPropertyRuleDTO> createCustomFormProperty(@RequestBody ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO) {
        approvalFormPropertyService.createCustomFormPropertyRule(approvalFormPropertyRuleDTO);
        return ResponseEntity.ok(approvalFormPropertyService.selectByFormOid(approvalFormPropertyRuleDTO.getFormOid()));
    }

    /**
     * 更新表单属性规则
     *
     * @param approvalFormPropertyRuleDTO
     * @return
     */
    @RequestMapping(value = "/customForm/property", method = RequestMethod.PUT)
    public ResponseEntity<ApprovalFormPropertyRuleDTO> updateCustomFormProperty(@RequestBody ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO) {
        approvalFormPropertyService.updateCustomFormPropertyRule(approvalFormPropertyRuleDTO);
        return ResponseEntity.ok(approvalFormPropertyService.selectByFormOid(approvalFormPropertyRuleDTO.getFormOid()));
    }
}
