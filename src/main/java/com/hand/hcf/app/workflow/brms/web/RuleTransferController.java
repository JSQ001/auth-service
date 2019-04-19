package com.hand.hcf.app.workflow.brms.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.RuleTransferDTO;
import com.hand.hcf.app.workflow.brms.service.RuleTransferService;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rule/transfers")
public class RuleTransferController {

    private final Logger log = LoggerFactory.getLogger(RuleTransferController.class);

    @Inject
    RuleTransferService ruleTransferService;

    @RequestMapping(value = "/{ruleTransferOid}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "获取指定审批审批授权")
    public ResponseEntity<RuleTransferDTO> getRuleTransfer(@PathVariable UUID ruleTransferOid) {
        return ResponseEntity.ok().body(ruleTransferService.getRuleTransfer(ruleTransferOid));
    }

    @RequestMapping(value = "/my",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "获取本人所有审批授权")
    public ResponseEntity<List<RuleTransferDTO>> findMyRuleTransfer(Pageable pageable)  {
        Page page= PageUtil.getPage(pageable);
        List<RuleTransferDTO> pages = ruleTransferService.listDTOAllBySourceOid(OrgInformationUtil.getCurrentUserOid(), page);
        return new ResponseEntity<>(pages, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @RequestMapping(value = "",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "创建审批授权")
    public ResponseEntity<RuleTransferDTO> ctreateRuleTransfer(@RequestBody RuleTransferDTO ruleTransferDTO) {
        return ResponseEntity.ok().body(ruleTransferService.ctreateRuleTransfer(ruleTransferDTO, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "更新本人审批授权")
    public ResponseEntity<RuleTransferDTO> updateRuleTransfer(@RequestBody RuleTransferDTO ruleTransferDTO) {
        return ResponseEntity.ok().body(ruleTransferService.update(ruleTransferDTO, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/enabled/{ruleTransferOid}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "启用审批授权")
    public ResponseEntity<Integer> enabledRuleTransfer(@PathVariable UUID ruleTransferOid) {
        return ResponseEntity.ok().body(ruleTransferService.enabledRuleTransfer(ruleTransferOid, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/disabled/{ruleTransferOid}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "禁用审批授权")
    public ResponseEntity<Integer> disabledRuleTransfer(@PathVariable UUID ruleTransferOid) {
        return ResponseEntity.ok().body(ruleTransferService.disabledRuleTransfer(ruleTransferOid, OrgInformationUtil.getCurrentUserOid()));
    }
    @RequestMapping(value = "/{ruleTransferOid}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "删除审批授权")
    public ResponseEntity<Integer> deleteRuleTransfer(@PathVariable UUID ruleTransferOid) {
        return ResponseEntity.ok().body(ruleTransferService.delete(ruleTransferOid));
    }

    @RequestMapping(value = "/form",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation(value = "查询可选表单")
    public ResponseEntity<List<ApprovalFormDTO>> getFormByDate(@RequestParam(name = "startDate") String startDate,
                                                               @RequestParam(name = "endDate", required = false) String endDate) throws URISyntaxException {
        return ResponseEntity.ok().body(ruleTransferService.getFormByDate(startDate,endDate, OrgInformationUtil.getCurrentUserOid()));
    }
}
