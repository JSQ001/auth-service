package com.hand.hcf.app.workflow.brms.web;

import com.google.gson.Gson;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.*;
import com.hand.hcf.app.workflow.brms.service.DroolsService;
import com.hand.hcf.app.workflow.brms.service.RuleConditionService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.dto.FormFieldDTO;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/rule")
public class RuleController {
    private final Logger log = LoggerFactory.getLogger(RuleController.class);

    @Inject
    RuleService ruleService;
    @Autowired
    private RuleConditionService ruleConditionService;

    @Inject
    DroolsService droolsService;

    //TODO 初始化现有公司审批链

    //审批链
    @RequestMapping(value = "/approval/chains",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批链")
    public ResponseEntity<RuleApprovalChainDTO> ctreateApprovalChain(@RequestBody RuleApprovalChainDTO ruleApprovalChain,
                                                                     @RequestParam(value = "companyOid", required = false) UUID companyOid) {
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        return ResponseEntity.ok().body(ruleService.createRuleApprovalChain(ruleApprovalChain));
    }

    @RequestMapping(value = "/approval/chains",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("修改审批链")
    public ResponseEntity<RuleApprovalChainDTO> updateApprovalChain(@RequestBody RuleApprovalChainDTO ruleApprovalChain) {
        return ResponseEntity.ok().body(ruleService.updateRuleApprovalChain(ruleApprovalChain, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/chains/{ruleApprovalChainOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询审批链")
    public ResponseEntity<RuleApprovalChainDTO> getApprovalChain(
            @PathVariable UUID ruleApprovalChainOid,
            @RequestParam(required = false) boolean cascadeApprovalNode,
            @RequestParam(required = false) boolean cascadeApprover,
            @RequestParam(required = false) boolean cascadeCondition) {
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        return ResponseEntity.ok().body(ruleService.findRuleApprovalChain(ruleApprovalChainOid, userOid, cascadeApprovalNode, cascadeApprover, cascadeCondition));
    }

    @RequestMapping(value = "/approval/chains/form",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("根据表单Oid查询审批链")
    public ResponseEntity<RuleApprovalChainDTO> getApprovalChainByFormOid(
            @RequestParam UUID formOid,
            @RequestParam(required = false) boolean cascadeApprovalNode,
            @RequestParam(required = false) boolean cascadeApprover,
            @RequestParam(required = false) boolean cascadeCondition) {
        return ResponseEntity.ok().body(ruleService.getApprovalChainByFormOid(formOid, null, cascadeApprovalNode, cascadeApprover, cascadeCondition));
    }

    @RequestMapping(value = "/approval/chains/copy",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("复制审批链")
    public ResponseEntity<RuleApprovalChainDTO> copyApprovalChain(@RequestParam UUID sourceFormOid, @RequestParam UUID targetFormOid) {
        return ResponseEntity.ok().body(ruleService.copyApprovalChain(sourceFormOid, targetFormOid));
    }

    @RequestMapping(value = "/approval/chains/copy/v2",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("复制审批链-新中控")
    public ResponseEntity<RuleApprovalChainDTO> copyApprovalChainV2(@RequestBody RuleApprovalChainCopyDTO ruleApprovalChainCopyDTO) {
        return ResponseEntity.ok().body(ruleService.copyApprovalChain(ruleApprovalChainCopyDTO.getSourceFormOid(), ruleApprovalChainCopyDTO.getTargetFormOid()));
    }

    @RequestMapping(value = "/approval/chains/status",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("启用/禁用审批链")
    public ResponseEntity<Integer> updateApprovalChainStatus(
            @RequestParam UUID ruleApprovalChainOid,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok().body(ruleService.updateApprovalChainStatus(ruleApprovalChainOid, enabled, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/chains/status/v2",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("启用/禁用审批链-新中控")
    public ResponseEntity<Integer> updateApprovalChainStatusV2(@RequestBody ApprovalChainStatusDTO approvalChainStatusDTO) {
        return ResponseEntity.ok().body(ruleService.updateApprovalChainStatus(approvalChainStatusDTO.getRuleApprovalChainOid(), approvalChainStatusDTO.isEnabled(), OrgInformationUtil.getCurrentUserOid()));
    }

    //创建审批节点
    @RequestMapping(value = "/approval/nodes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批节点")
    public ResponseEntity<RuleApprovalNodeDTO> ctreateApprovalNode(@RequestBody RuleApprovalNodeDTO ruleApprovalNode) {

        return ResponseEntity.ok().body(ruleService.createRuleApprovalNodeMapping(ruleApprovalNode, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/nodes/{ruleApprovalNodeOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询审批节点")
    public ResponseEntity<RuleApprovalNodeDTO> getApprovalNode(
            @PathVariable UUID ruleApprovalNodeOid,
            @RequestParam(required = false) boolean cascadeApprover,
            @RequestParam(required = false) boolean cascadeCondition) {
        return ResponseEntity.ok().body(ruleService.getRuleApprovalNode(ruleApprovalNodeOid, OrgInformationUtil.getCurrentUserOid(), cascadeApprover, cascadeCondition));
    }

    @RequestMapping(value = "/approval/nodes/{ruleApprovalNodeOid}/{userOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询审批节点")
    public ResponseEntity<RuleApprovalNodeDTO> getApprovalNodeByRuleApprovalNodeOidAndUserOid(
            @PathVariable UUID ruleApprovalNodeOid,
            @PathVariable UUID userOid,
            @RequestParam(required = false) boolean cascadeApprover,
            @RequestParam(required = false) boolean cascadeCondition) {
        return ResponseEntity.ok().body(ruleService.getRuleApprovalNode(ruleApprovalNodeOid, userOid, cascadeApprover, cascadeCondition));
    }

    @RequestMapping(value = "/approval/nodes/{ruleApprovalNodeOid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("删除审批节点")
    public ResponseEntity<Integer> deteleApprovalNode(@PathVariable UUID ruleApprovalNodeOid) {
        return ResponseEntity.ok().body(ruleService.deleteRuleApprovalNode(ruleApprovalNodeOid, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/nodes",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("更新审批节点")
    public ResponseEntity<RuleApprovalNodeDTO> updateApprovalNode(@RequestBody RuleApprovalNodeDTO ruleApprovalNode) {
        return ResponseEntity.ok().body(ruleService.updateRuleApprovalNode(ruleApprovalNode, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/nodes/move",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("移动审批节点")
    public ResponseEntity<RuleApprovalNodeDTO> moveApprovalNode(@RequestParam UUID ruleApprovalNodeOid, @RequestParam(required = false) UUID nextRuleApprovalNodeOid) {
        return ResponseEntity.ok().body(ruleService.moveRuleApprovalNode(ruleApprovalNodeOid, nextRuleApprovalNodeOid, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approval/nodes/move/v2",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("移动审批节点-新中控")
    public ResponseEntity<RuleApprovalNodeDTO> moveApprovalNodeV2(@RequestBody MoveApprovalNodeDTO moveApprovalNodeDTO) {
        return ResponseEntity.ok().body(ruleService.moveRuleApprovalNode(moveApprovalNodeDTO.getRuleApprovalNodeOid(), moveApprovalNodeDTO.getNextRuleApprovalNodeOid(), OrgInformationUtil.getCurrentUserOid()));
    }

    //审批者
    @RequestMapping(value = "/approvers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批者")
    public ResponseEntity<RuleApproverDTO> ctreateApprover(@RequestBody RuleApproverDTO ruleApprover) {
        return ResponseEntity.ok().body(ruleService.createRuleApprover(ruleApprover, OrgInformationUtil.getCurrentUserOid()));
    }

    //审批者
    @RequestMapping(value = "/approvers/batch",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批者")
    public ResponseEntity<List<RuleApproverDTO>> ctreateApprover(@RequestBody List<RuleApproverDTO> ruleApproverList) {
        return ResponseEntity.ok().body(ruleService.createRuleApprover(ruleApproverList, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approvers",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("修改审批者")
    public ResponseEntity<RuleApproverDTO> updateApprover(@RequestBody RuleApproverDTO ruleApprover) {
        return ResponseEntity.ok().body(ruleService.updateRuleApprover(ruleApprover, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approvers/batch",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("修改审批者")
    public ResponseEntity<List<RuleApproverDTO>> updateApprover(@RequestBody List<RuleApproverDTO> ruleApproverList) {
        return ResponseEntity.ok().body(ruleService.updateRuleApprover(ruleApproverList, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/approvers/{ruleApproverOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询审批者")
    public ResponseEntity<RuleApproverDTO> getApprover(
            @PathVariable UUID ruleApproverOid,
            @RequestParam(required = false) boolean cascadeCondition) {
        return ResponseEntity.ok().body(ruleService.getRuleApprover(ruleApproverOid, OrgInformationUtil.getCurrentUserOid(), cascadeCondition));
    }

    @RequestMapping(value = "/approvers/{ruleApproverOid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("删除审批者")
    public ResponseEntity<Integer> deleteApprover(@PathVariable UUID ruleApproverOid) {
        return ResponseEntity.ok().body(ruleService.deleteRuleApprover(ruleApproverOid, OrgInformationUtil.getCurrentUserOid()));
    }

    //条件
    @RequestMapping(value = "/conditions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批条件")
    public ResponseEntity<RuleConditionDTO> ctreateCondition(@RequestBody RuleConditionDTO ruleCondition) {
        return ResponseEntity.ok().body(ruleConditionService.createRuleCondition(ruleCondition));
    }

    //条件
    @RequestMapping(value = "/conditions/batch",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("创建审批条件")
    public ResponseEntity<List<RuleConditionDTO>> ctreateCondition(@RequestBody List<RuleConditionDTO> ruleConditionList) {
        return ResponseEntity.ok().body(ruleConditionService.createRuleCondition(ruleConditionList));
    }

    @RequestMapping(value = "/conditions",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("修改审批条件")
    public ResponseEntity<RuleConditionDTO> updateCondition(@RequestBody RuleConditionDTO ruleCondition) {
        return ResponseEntity.ok().body(ruleConditionService.updateRuleCondition(ruleCondition, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/conditions/batch",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("修改审批条件")
    public ResponseEntity<List<RuleConditionDTO>> updateCondition(@RequestBody List<RuleConditionDTO> ruleConditionList) {
        return ResponseEntity.ok().body(ruleConditionService.updateRuleCondition(ruleConditionList, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/conditions/{ruleConditionOid}",
            method = RequestMethod.GET)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询审批条件")
    public ResponseEntity<RuleConditionDTO> getCondition(@PathVariable UUID ruleConditionOid) {
        return ResponseEntity.ok().body(ruleConditionService.getRuleCondition(ruleConditionOid, OrgInformationUtil.getCurrentUserOid()));
    }


    @RequestMapping(value = "/conditions/{ruleConditionOid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("删除审批条件")
    public ResponseEntity<Integer> deleteCondition(@PathVariable UUID ruleConditionOid) {
        return ResponseEntity.ok().body(ruleConditionService.deleteRuleCondition(ruleConditionOid, OrgInformationUtil.getCurrentUserOid()));
    }

    @RequestMapping(value = "/conditions/batch",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("批量删除审批条件")
    public ResponseEntity<Integer> deleteConditionBatch(@RequestBody List<UUID> ruleConditionOidList) {
        return ResponseEntity.ok().body(ruleConditionService.deleteRuleConditionBatch(ruleConditionOidList, OrgInformationUtil.getCurrentUserOid()));
    }

    //表单列表
    @RequestMapping(value = "/custom/forms",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation("查询自定义表单关联审批链")
    public ResponseEntity<List<ApprovalFormDTO>> getAllCustomForm(@RequestParam(value = "fromType", required = false) String fromType,
                                                                  @RequestParam(value = "roleType", required = false) String roleType,
                                                                  @RequestParam(value = "booksID", required = false) String booksID,
                                                                  @RequestParam(value = "formName",required = false) String formName,
                                                                  @RequestParam(value = "documentCategory",required = false) Long formTypeId) {
        return ResponseEntity.ok(ruleService.listAllForm(true, fromType, roleType,formName,formTypeId));
    }

    //表单列表
    @RequestMapping(value = "/custom/forms/init",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation("初始化公司下未配置的表单")
    public ResponseEntity<List<ApprovalFormDTO>> initSpecificAllCustomForm() {
        List<ApprovalFormDTO> approvalFormDTOList = ruleService.getAllUnInitialCustomForm(true);
        ruleService.additionalOperation(approvalFormDTOList.stream().map(c -> c.getFormOid()).collect(Collectors.toList()));
        return ResponseEntity.ok(approvalFormDTOList);
    }


    @RequestMapping(value = "/custom/form/fields",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, List<FormFieldDTO>>> getCustomFormField(@RequestParam UUID formOid, @RequestParam(required = false) String roleType) {
        if (StringUtils.isEmpty(roleType)) {
            roleType = Constants.ROLE_COMPANY;
        }
        return ResponseEntity.ok(ruleService.getCustomFormField(formOid, OrgInformationUtil.getCurrentUserOid(), roleType));
    }


    //constants
    /*@RequestMapping(value = "/approval/actions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("常量查询-审批操作")
    public ResponseEntity<Map<Integer, String>> getApprovalAction() {
        return ResponseEntity.ok().body(RuleConstants.actionMap);
    }*/

    @RequestMapping(value = "/approval/symbols",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("常量查询-审批操作符")
    public ResponseEntity<List<RuleEnumDTO>> getApprovalSymbol() {
        return ResponseEntity.ok().body(RuleConstants.symbols);
    }

    @RequestMapping(value = "/approver/types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("常量查询-审批者类型")
    public ResponseEntity<Map<RuleEnumDTO, List<RuleEnumDTO>>> getApproverType() {
        return ResponseEntity.ok().body(ruleService.getRuleApprovalRole(OrgInformationUtil.getCurrentCompanyOid()));
    }

    @RequestMapping(value = "/approval/modes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("常量查询-审批模式")
    @Deprecated
    public ResponseEntity<List<RuleEnumDTO>> getApproverMode() {
        return ResponseEntity.ok().body(RuleConstants.approvalModes);
    }

    @RequestMapping(value = "/approval/modes/{language}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("常量查询-审批模式")
    public ResponseEntity<List<RuleEnumDTO>> getApproverMode(@PathVariable String language) {
        List<RuleEnumDTO> modes = ruleService.getApprovalModesByLanguage(language);
        return ResponseEntity.ok().body(modes);
    }

    @RequestMapping(value = "/drools/approvalNode/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("通过表单和审批者执行规则引擎")
    public ResponseEntity<RuleApprovalNodeDTO> invokeDroolsRuleForApprovalNode(@RequestBody DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        log.info("post approvalNode request: " + droolsRuleApprovalNodeDTO.toString());

        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        RuleApprovalNodeDTO ruleApprovalNodeDTO = droolsService.invokeDroolsRuleForApprovalNode(droolsRuleApprovalNodeDTO, userOid);
        List<RuleApproverDTO> ruleApproverDTOS = ruleApprovalNodeDTO.getRuleApprovers();
        droolsRuleApprovalNodeDTO.setRuleApproverDTOs(ruleApproverDTOS);
        RuleApproverUserOidsDTO ruleApproverUserOidsDTO = ruleService.getRuleApproverUserOIDs(droolsRuleApprovalNodeDTO);
        ruleApprovalNodeDTO.setRuleApproverMap(ruleApproverUserOidsDTO.getRuleApproverMap());

        return ResponseEntity
                .ok()
                .body(ruleApprovalNodeDTO);
    }

    @RequestMapping(value = "/drools/details/{droolsRuleDetailOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询规则引擎结果")
    public ResponseEntity<DroolsRuleDetailResultDTO> getDroolsRuleDetailResultByOid(@PathVariable UUID droolsRuleDetailOid) {
        DroolsRuleDetailResultDTO droolsRuleDetailResultDTO = droolsService.getDroolsRuleDetailResultByOid(droolsRuleDetailOid);
        return ResponseEntity.ok().body(droolsRuleDetailResultDTO);
    }

    @RequestMapping(value = "/next/approval/node",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("通过表单和审批者执行规则引擎")
    public ResponseEntity<RuleNextApproverResult> getNextApprovalNode(@RequestBody DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        log.info("execute_getNextApprovalNode_request: " + new Gson().toJson(droolsRuleApprovalNodeDTO));
        RuleNextApproverResult result = ruleService.getNextApprovalNode(droolsRuleApprovalNodeDTO);
        log.info("execute_getNextApprovalNode_response: " + new Gson().toJson(result));
        return ResponseEntity
                .ok()
                .body(result);
    }

    @RequestMapping(value = "/init/company/{companyOid}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("通过表单和审批者执行规则引擎")
    public ResponseEntity<Void> initCompanyRule(@PathVariable UUID companyOid, @RequestParam Integer approvalType, @RequestParam UUID userOid) {
        if (userOid == null) {
            userOid = OrgInformationUtil.getCurrentUserOid();
        }
        ruleService.initRule( approvalType);
        return ResponseEntity
                .ok().build();
    }

    @RequestMapping(value = "/custom/form/approval/mode",
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    //@ApiOperation("查询表单审批模式")
    public ResponseEntity<CustomFormApprovalModeDTO> getCustomFormApproverMode() {
        return ResponseEntity.ok().body(ruleService.getCustomFormApproverMode());
    }
}
