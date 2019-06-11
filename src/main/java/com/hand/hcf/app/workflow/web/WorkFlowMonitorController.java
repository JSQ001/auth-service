package com.hand.hcf.app.workflow.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.monitor.MonitorReturnNodeDTO;
import com.hand.hcf.app.workflow.dto.monitor.WorkFlowMonitorDTO;
import com.hand.hcf.app.workflow.dto.monitor.WorkflowJumpDTO;
import com.hand.hcf.app.workflow.service.WorkFLowMonitorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflow/monitor")
public class WorkFlowMonitorController {
    @Autowired
    private WorkFLowMonitorService workFLowMonitorService;


    @GetMapping(value = "/query")
        public ResponseEntity<List<WorkFlowMonitorDTO>> pageWorkflowMonitorByCond(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId",required = false) Long booksID,
                                                                                  @ApiParam(value = "单据大类") @RequestParam(value = "documentCategory",required = false) Integer documentCategory,
                                                                                  @ApiParam(value = "创建人") @RequestParam(value = "createdBy",required = false) Long createdBy,
                                                                                  @ApiParam(value = "状态") @RequestParam(value = "currentStatus",required = false) Integer status,
                                                                                  @ApiParam(value = "单据编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                                                                  @ApiParam(value = "申请日期至") @RequestParam(value = "startDate",required = false) String startDate,
                                                                                  @ApiParam(value = "申请日期到") @RequestParam(value = "endDate",required = false) String endDate,
                                                                                  @ApiParam(value = "当前审批人") @RequestParam(value = "lastApproverOid",required = false) UUID lastApproverOid,
                                                                                  @ApiParam(value = "工作流名称") @RequestParam(value = "formName",required = false) String formName,
                                                                                  @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                  @ApiParam(value = "每页条数") @RequestParam(value = "size", required = false, defaultValue = "10") int size){

        //日期格式转换
        ZonedDateTime startTimeForDay = null;
        if(StringUtils.isNotEmpty(startDate)){
            startTimeForDay = DateUtil.stringToZonedDateTime(startDate);
        }
        ZonedDateTime endTimeForDay = null;
        if(StringUtils.isNotEmpty(endDate)){
            endTimeForDay= DateUtil.stringToZonedDateTime(endDate);
            endTimeForDay = endTimeForDay.plusDays(1);
        }
        Page mybaitsPage = PageUtil.getPage(page,size);
        List<WorkFlowMonitorDTO> workFlowMonitorList = workFLowMonitorService.pageWorkflowMonitorByCond(booksID, documentCategory, createdBy, status, documentNumber, startTimeForDay, endTimeForDay, lastApproverOid , formName, mybaitsPage);
        HttpHeaders headers = PageUtil.getTotalHeader(mybaitsPage);
        return new ResponseEntity<>(workFlowMonitorList, headers, HttpStatus.OK);
    }

    @PostMapping("/jump")
    public ResponseEntity<ApprovalResDTO> workFlowJump(@RequestBody WorkflowJumpDTO dto){
        ApprovalResDTO approvalResDTO = workFLowMonitorService.workFlowJump(dto.getRuleApprovalNodeOid(), dto.getEntityOid(), dto.getEntityType());
        return ResponseEntity.ok(approvalResDTO);
    }

    @ApiOperation(value = "查询可跳转节点列表", notes = "查询可跳转节点列表", tags = {"query"})
    @GetMapping(value = "/back/nodes")
    public ResponseEntity<MonitorReturnNodeDTO> listApprovalNode(
            @ApiParam(value = "单据类型") @RequestParam Integer entityType,
            @ApiParam(value = "单据oid") @RequestParam UUID entityOid) {
        return ResponseEntity.ok(workFLowMonitorService.listApprovalNode(entityType, entityOid));
    }

    @GetMapping("/preview")
    public void workFLowPreview(){

        return;
    }
}
