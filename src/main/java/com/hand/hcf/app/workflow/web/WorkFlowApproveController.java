package com.hand.hcf.app.workflow.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRejectService;
import com.hand.hcf.app.workflow.approval.service.WorkflowWithdrawService;
import com.hand.hcf.app.workflow.dto.ApprovalDashboardDetailDTO;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentDTO;
import com.hand.hcf.app.workflow.dto.ApprovalHistoryDTO;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.CounterSignDTO;
import com.hand.hcf.app.workflow.dto.NotifyDTO;
import com.hand.hcf.app.workflow.dto.SendBackDTO;
import com.hand.hcf.app.workflow.dto.TransferDTO;
import com.hand.hcf.app.workflow.dto.WebApprovalHistoryDTO;
import com.hand.hcf.app.workflow.dto.WorkFlowDocumentRefDTO;
import com.hand.hcf.app.workflow.dto.WorkflowDocumentDTO;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.WorkFlowApprovalService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/11.
 * 工作流方法调用 统一入口方法
 */
@RestController
@Api(value = "审批api")
@RequestMapping("/api/workflow")
public class WorkFlowApproveController {
    @Autowired
    private WorkFlowApprovalService workFlowApprovalService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowPassService approvalPassService;

    @Autowired
    private WorkflowRejectService approvalRejectService;

    @Autowired
    private WorkflowWithdrawService approvalWithdrawService;


    @ApiOperation(value = "审批通过", notes = "通过待审批单据", tags = {"approval"})
    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> passWorkflow(
            @ApiParam(value = "待审批单据") @Valid @RequestBody ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = approvalPassService.passWorkflow(OrgInformationUtil.getCurrentUserOid(), approvalReqDTO);
        return ResponseEntity.ok(approvalResDTO);
    }


    @ApiOperation(value = "审批驳回", notes = "驳回待审批单据", tags = {"approval"})
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> rejectWorkflow(
            @ApiParam(value = "待驳回单据") @Valid @RequestBody ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = approvalRejectService.rejectWorkflow(OrgInformationUtil.getCurrentUserOid(), approvalReqDTO);
        return ResponseEntity.ok(approvalResDTO);
    }

    @ApiOperation(value = "申请人撤回单据", notes = "申请人撤回待审批单据", tags = {"approval"})
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> withdrawWorkflow(
            @ApiParam(value = "待撤回单据")  @Valid @RequestBody ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = approvalWithdrawService.withdrawWorkflow(OrgInformationUtil.getCurrentUserOid(), approvalReqDTO);
        return ResponseEntity.ok(approvalResDTO);
    }

    @ApiOperation(value = "审批加签", notes = "审批加签", tags = {"approval"})
    @RequestMapping(value = "/countersign", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> counterSign(
            @ApiParam(value = "单据加签信息") @Valid @RequestBody CounterSignDTO counterSignDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        //todo
        return ResponseEntity.ok(approvalResDTO);
    }

    @ApiOperation(value = "审批转交", notes = "审批转交", tags = {"approval"})
    @RequestMapping(value = "/deliver", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> deliver(
            @ApiParam(value = "单据转交信息") @Valid @RequestBody TransferDTO transferDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        //todo
        return ResponseEntity.ok(approvalResDTO);
    }


    @ApiOperation(value = "审批退回指定节点", notes = "审批退回指定节点", tags = {"approval"})
    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> sendBack(
            @ApiParam(value = "单据退回节点") @Valid @RequestBody SendBackDTO sendBackDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        //todo
        return ResponseEntity.ok(approvalResDTO);
    }


    @ApiOperation(value = "审批节点通知", notes = "审批节点通知", tags = {"approval"})
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<ApprovalResDTO> notify(
            @ApiParam(value = "节点通知信息") @Valid @RequestBody NotifyDTO notifyDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        //todo
        return ResponseEntity.ok(approvalResDTO);
    }

    /**
     * 【仪表盘】-我的单据
     *
     * @param tabNumber
     * @return
     */
    @RequestMapping(value = "/my/document/{tabNumber}", method = RequestMethod.GET)
    public ResponseEntity<List<WorkflowDocumentDTO>> listMyDocument(@PathVariable Integer tabNumber) {
        return ResponseEntity.ok(workFlowApprovalService.listMyDocument(tabNumber));
    }


    /**
     * 【仪表盘-】获取当前用户所有待审批的单据
     *
     * @return
     */
    @RequestMapping(value = "/approvals/batchfilters", method = RequestMethod.GET)
    public ResponseEntity getApprovalDashboardDetailDTOList() {
        return ResponseEntity.ok(workFlowApprovalService.getApprovalDashboardDetailDTOList());
    }

    /**
     * @param entityType 单据类型
     * @param entityOid  单据Oid
     * @return
     * @api {get} /api/workflow/approval/history
     * @apiDescription 获取审批历史
     * @apiName listApprovalHistory
     * @apiGroup Approval
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "operation": 9001,
     * "operationType": 1000,
     * "lastModifiedDate": "2018-10-24 11:20:47",
     * "employeeId": "et1",
     * "employeeName": "et1",
     * "operationDetail": "支付金额：5",
     * "countersignType": null,
     * "operationRemark": null,
     * "approvalNodeName": null
     * }
     * ]
     * @Author mh.z
     * @Date 2019/01/23
     * @Description 获取审批历史
     */
    @RequestMapping(value = "/approval/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WebApprovalHistoryDTO>> listApprovalHistory(@RequestParam("entityType") Integer entityType, @RequestParam("entityOid") UUID entityOid) {
        List<WebApprovalHistoryDTO> list = new ArrayList<WebApprovalHistoryDTO>();

        // 获取审批历史
        List<ApprovalHistoryDTO> approvalHistoryDtoList = approvalHistoryService.listApprovalHistory(entityType, entityOid);
        // 确定审批动作名称
        approvalHistoryService.approvalAction(list, approvalHistoryDtoList, approvalHistoryService);

        return ResponseEntity.ok(list);
    }

    /**
     * @api {get} /api/workflow/document/approvals/filters/{entityType}
     * @apiDescription 获取已审批未审批的单据
     * @apiName pageApprovalDocument
     * @apiGroup Approval
     * @apiParam {String} [documentNumber] 单据编号
     * @apiParam {String} [documentName] 单据名称
     * @apiParam {Long} [documentTypeId] 单据类型id
     * @apiParam {String} [currencyCode] 币种
     * @apiParam {String} [amountFrom] 最小金额
     * @apiParam {Double} [amountTo] 最大金额
     * @apiParam {String} [applicantOid] 申请人oid
     * @apiParam {String} [beginDate] 最小提交日期
     * @apiParam {String} [endDate] 最大提交日期
     * @apiParam {String} [applicantDateFrom] 最小申请日期
     * @apiParam {String} [applicantDateTo] 最大申请日期
     * @apiParam {String} [description] 备注
     * @apiParam {boolean} finished true已审批，false未审批
     * @author mh.z
     * @date 2019/03/06
     * @description 获取已审批未审批的单据
     */
    @RequestMapping(value = "/document/approvals/filters/{entityType}", method = RequestMethod.GET)
    public ResponseEntity<List<ApprovalDocumentDTO>> pageApprovalDocument(@PathVariable(value = "entityType", required = true) Integer entityType,
                                                                          @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                                          @RequestParam(value = "documentName", required = false) String documentName,
                                                                          @RequestParam(value = "documentTypeId", required = false) Long documentTypeId,
                                                                          @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                                                          @RequestParam(value = "amountFrom", required = false) Double amountFrom,
                                                                          @RequestParam(value = "amountTo", required = false) Double amountTo,
                                                                          @RequestParam(value = "applicantOid", required = false) String applicantOidStr,
                                                                          @RequestParam(value = "beginDate", required = false) String beginDateStr,
                                                                          @RequestParam(value = "endDate", required = false) String endDateStr,
                                                                          @RequestParam(value = "applicantDateFrom", required = false) String applicantDateFromStr,
                                                                          @RequestParam(value = "applicantDateTo", required = false) String applicantDateToStr,
                                                                          @RequestParam(value = "description", required = false) String description,
                                                                          @RequestParam(value = "finished", required = true) boolean finished,
                                                                          Pageable pageable) {
        documentNumber = StringUtils.isEmpty(documentNumber) ? null : documentNumber;
        documentName = StringUtils.isEmpty(documentName) ? null : documentName;
        currencyCode = StringUtils.isEmpty(currencyCode) ? null : currencyCode;
        applicantOidStr = StringUtils.isEmpty(applicantOidStr) ? null : applicantOidStr;
        beginDateStr = StringUtils.isEmpty(beginDateStr) ? null : beginDateStr;
        endDateStr = StringUtils.isEmpty(endDateStr) ? null : endDateStr;
        description = StringUtils.isEmpty(description) ? null : description;

        // 最小提交日期
        ZonedDateTime beginDate = null;
        if (StringUtils.isNotEmpty(beginDateStr)) {
            beginDate = DateUtil.stringToZonedDateTime(beginDateStr);
        }

        // 最大提交日期
        ZonedDateTime endDate = null;
        if (StringUtils.isNotEmpty(endDateStr)) {
            endDate = DateUtil.stringToZonedDateTime(endDateStr);
            endDate = endDate.plusDays(1);
        }

        ZonedDateTime applicantDateFrom = null;
        if (StringUtils.isNotEmpty(applicantDateFromStr)) {
            applicantDateFrom = DateUtil.stringToZonedDateTime(applicantDateFromStr);
        }

        ZonedDateTime applicantDateTo = null;
        if (StringUtils.isNotEmpty(applicantDateToStr)) {
            applicantDateTo = DateUtil.stringToZonedDateTime(applicantDateToStr);
            applicantDateTo = applicantDateTo.plusDays(1);
        }

        // 申请人
        UUID applicantOid = null;
        if (StringUtils.isNotEmpty(applicantOidStr)) {
            applicantOid = UUID.fromString(applicantOidStr);
        }

        // 当前用户就是审批人
        UUID approverOid = OrgInformationUtil.getCurrentUserOid();

        Page page = PageUtil.getPage(pageable);
        // 查询未审批已审批单据
        List<ApprovalDocumentDTO> list = workFlowDocumentRefService.pageApprovalDocument(approverOid, entityType, documentNumber, documentName, documentTypeId,
                currencyCode, amountFrom, amountTo, applicantOid, beginDate, endDate,
                applicantDateFrom, applicantDateTo, description, finished, page);

        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }

    /**
     * 待办事项-待审批单据-单据列表
     *
     * @param documentCategory 单据大类
     * @param documentTypeId   单据类型id
     * @param beginDateStr     提交日期从
     * @param endDateStr       提交日期至
     * @param amountFrom       本币金额从
     * @param amountTo         本币金额至
     * @param remark           备注
     * @param documentNumber   单据编号
     * @param pageable         分页信息
     * @return
     */
    @GetMapping("/getApprovalToPendList")
    public ResponseEntity<List<WorkFlowDocumentRefDTO>> getApprovalToPendList(@RequestParam(value = "documentCategory", required = false) Integer documentCategory,
                                                                              @RequestParam(value = "documentTypeId", required = false) Long documentTypeId,
                                                                              @RequestParam(value = "applicantName", required = false) String applicantName,
                                                                              @RequestParam(value = "beginDate", required = false) String beginDateStr,
                                                                              @RequestParam(value = "endDate", required = false) String endDateStr,
                                                                              @RequestParam(value = "amountFrom", required = false) Double amountFrom,
                                                                              @RequestParam(value = "amountTo", required = false) Double amountTo,
                                                                              @RequestParam(value = "remark", required = false) String remark,
                                                                              @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                                              Pageable pageable) {

        // 最小提交日期
        ZonedDateTime beginDate = null;
        if (StringUtils.isNotEmpty(beginDateStr)) {
            beginDate = DateUtil.stringToZonedDateTime(beginDateStr);
        }

        // 最大提交日期
        ZonedDateTime endDate = null;
        if (StringUtils.isNotEmpty(endDateStr)) {
            endDate = DateUtil.stringToZonedDateTime(endDateStr);
            endDate = endDate.plusDays(1);
        }

        Page mybatisPage = PageUtil.getPage(pageable);
        //获取待审批列表
        List<WorkFlowDocumentRefDTO> workFlowDocumentRefDTOListList = workFlowApprovalService.getApprovalToPendDeatil(documentCategory, documentTypeId, applicantName, beginDate, endDate, amountFrom, amountTo, remark, documentNumber, mybatisPage);

        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(workFlowDocumentRefDTOListList, httpHeaders, HttpStatus.OK);
    }

    /**
     * 待办事项-待审批单据-分类信息
     *
     * @param documentCategory 单据大类
     * @param documentTypeId   单据类型id
     * @param beginDateStr     提交日期从
     * @param endDateStr       提交日期至
     * @param amountFrom       本币金额从
     * @param amountTo         本币金额至
     * @param remark           备注
     * @param documentNumber   单据编号
     * @return
     */
    @GetMapping("/getApprovalToPendTotal")
    public List<ApprovalDashboardDetailDTO> getApprovalToPendTotal(@RequestParam(value = "documentCategory", required = false) Integer documentCategory,
                                                                   @RequestParam(value = "documentTypeId", required = false) Long documentTypeId,
                                                                   @RequestParam(value = "applicantName", required = false) String applicantName,
                                                                   @RequestParam(value = "beginDate", required = false) String beginDateStr,
                                                                   @RequestParam(value = "endDate", required = false) String endDateStr,
                                                                   @RequestParam(value = "amountFrom", required = false) Double amountFrom,
                                                                   @RequestParam(value = "amountTo", required = false) Double amountTo,
                                                                   @RequestParam(value = "remark", required = false) String remark,
                                                                   @RequestParam(value = "documentNumber", required = false) String documentNumber) {

        // 最小提交日期
        ZonedDateTime beginDate = null;
        if (StringUtils.isNotEmpty(beginDateStr)) {
            beginDate = DateUtil.stringToZonedDateTime(beginDateStr);
        }

        // 最大提交日期
        ZonedDateTime endDate = null;
        if (StringUtils.isNotEmpty(endDateStr)) {
            endDate = DateUtil.stringToZonedDateTime(endDateStr);
            endDate = endDate.plusDays(1);
        }

        //获取单据类别和数量列表
        List<ApprovalDashboardDetailDTO> approvalDashboardDetailDTOList = workFlowApprovalService.getApprovalToPendTotal(documentCategory, documentTypeId, applicantName, beginDate, endDate, amountFrom, amountTo, remark, documentNumber);
        return approvalDashboardDetailDTOList;
    }


    /**
     * 待办事项-被退回单据/未完成单据
     *
     * @param tabNumber          tabNumber=1(被退回的单据) tabNumber=2(未完成的单据)
     * @param documentCategory   单据大类
     * @param documentTypeId     单据类型id
     * @param beginDateStr       提交日期从
     * @param endDateStr         提交日期至
     * @param amountFrom         本币金额从
     * @param amountTo           本币金额至
     * @param lastApproverOidStr 当前审批人oid
     * @param approvalNodeName   当前审批节点名称
     * @param remark             备注
     * @param documentNumber     单据编号
     * @return
     */
    @GetMapping("/my/document/detail/{tabNumber}")
    public ResponseEntity<List<WorkFlowDocumentRefDTO>> listMyDocumentDetail(@PathVariable Integer tabNumber,
                                                                             @RequestParam(value = "documentCategory", required = false) Integer documentCategory,
                                                                             @RequestParam(value = "documentTypeId", required = false) Long documentTypeId,
                                                                             @RequestParam(value = "applicantName", required = false) String applicantName,
                                                                             @RequestParam(value = "beginDate", required = false) String beginDateStr,
                                                                             @RequestParam(value = "endDate", required = false) String endDateStr,
                                                                             @RequestParam(value = "amountFrom", required = false) Double amountFrom,
                                                                             @RequestParam(value = "amountTo", required = false) Double amountTo,
                                                                             @RequestParam(value = "lastApproverOid", required = false) String lastApproverOidStr,
                                                                             @RequestParam(value = "approvalNodeName", required = false) String approvalNodeName,
                                                                             @RequestParam(value = "remark", required = false) String remark,
                                                                             @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                                             @RequestParam(value = "size", defaultValue = "10") int size) {


        // 最小提交日期
        ZonedDateTime beginDate = null;
        if (StringUtils.isNotEmpty(beginDateStr)) {
            beginDate = DateUtil.stringToZonedDateTime(beginDateStr);
        }

        // 最大提交日期
        ZonedDateTime endDate = null;
        if (StringUtils.isNotEmpty(endDateStr)) {
            endDate = DateUtil.stringToZonedDateTime(endDateStr);
            endDate = endDate.plusDays(1);
        }

        // 驳回人
        UUID lastApproverOid = null;
        if (StringUtils.isNotEmpty(lastApproverOidStr)) {
            lastApproverOid = UUID.fromString(lastApproverOidStr);
        }

        Page mybatisPage = PageUtil.getPage(page, size);

        //获取待审批列表
        List<WorkFlowDocumentRefDTO> list = workFlowApprovalService.listMyDocumentDetail(tabNumber, documentCategory, documentTypeId, applicantName, beginDate, endDate, amountFrom, amountTo, lastApproverOid, approvalNodeName, remark, documentNumber, mybatisPage);

        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }

}
