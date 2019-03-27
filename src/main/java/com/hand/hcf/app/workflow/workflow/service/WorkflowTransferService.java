package com.hand.hcf.app.workflow.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.workflow.domain.WorkflowTransfer;
import com.hand.hcf.app.workflow.workflow.dto.WorkflowTransferDTO;
import com.hand.hcf.app.workflow.workflow.persistence.WorkflowTransferMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.TypeConversionUtils;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/21 9:24
 * @version: 1.0.0
 */
@Service
public class WorkflowTransferService extends BaseService<WorkflowTransferMapper,WorkflowTransfer> {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private MapperFacade mapper;

    /**
     * 新增转交
     * @param workflowTransfer
     * @return
     */
    @Transactional
    public WorkflowTransfer insertWorkflowTransfer(WorkflowTransfer workflowTransfer) {
        //点击保存时做校验，若将同一条审批流授权给不同用户且授权时间有重叠，则保存失败，
        // 系统提示“当前审批流已被授权，不允许重复授权”
        if(baseMapper.selectList(
                new EntityWrapper<WorkflowTransfer>()
                        .eq("workflow_id",workflowTransfer.getWorkflowId())
                        .eq("start_date",workflowTransfer.getStartDate()))
                .size() > 0){
            throw new BizException(RespCode.WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT);
        };
        workflowTransfer.setAuthorizerId(OrgInformationUtil.getCurrentUserId());
        workflowTransfer.setTenantId(OrgInformationUtil.getCurrentTenantId());
        workflowTransfer.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
        baseMapper.insert(workflowTransfer);
        return workflowTransfer;
    }

    /**
     * 分页查询获取当前用户转交信息
     * @param documentCategory  单据大类
     * @param workflowId 工作流Id
     * @param tab agent：代理  authorizer：转交
     * @param agentId 代理人Id
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param authorizationNotes 备注
     * @param mybaitsPage
     * @return
     */
    public List<WorkflowTransferDTO> pageWorkflowTransferByCond(String documentCategory,
                                                                Long workflowId,
                                                                Long authorizerId,
                                                                Long agentId,
                                                                String startDate,
                                                                String endDate,
                                                                String authorizationNotes,
                                                                String tab,
                                                                Page mybaitsPage) {
        Long userId = OrgInformationUtil.getCurrentUserId();
        Wrapper wrapper =  new EntityWrapper<WorkflowTransfer>()
                .eq(StringUtils.isNotEmpty(documentCategory),"document_category",documentCategory)
                .eq(workflowId != null, "workflow_id",workflowId)
                .ge(StringUtils.isNotEmpty(startDate),"start_date", TypeConversionUtils.getStartTimeForDayYYMMDD(startDate))
                .le(StringUtils.isNotEmpty(endDate),"end_date",TypeConversionUtils.getEndTimeForDayYYMMDD(endDate))
                .like(StringUtils.isNotEmpty(authorizationNotes),"authorization_notes",authorizationNotes)
                .orderBy("start_date");
        if(tab.equals("agent")){
            //当前代理人为当前用户
             wrapper.eq("agent_id",userId)
                    .eq(authorizerId != null,"authorizer_id",authorizerId);
        }else {
            //当前授权人
             wrapper.eq("authorizer_id",userId)
                    .eq(agentId != null,"agent_id",agentId);
        }
        return toDTO(baseMapper.selectPage(mybaitsPage,wrapper));

    }

    /**
     *  转换封装数据 DTO处理类
     * @param workflowTransferList
     * @return
     */
    public List<WorkflowTransferDTO> toDTO(List<WorkflowTransfer> workflowTransferList) {
        List<WorkflowTransferDTO> workflowTransferDTOS = mapper.mapAsList(workflowTransferList,WorkflowTransferDTO.class);
        workflowTransferDTOS.stream().forEach(workflowTransfer ->{
            String documentCategory = workflowTransfer.getDocumentCategory();
            Long workflowId = workflowTransfer.getWorkflowId();
            //代理人
            ContactCO agent= baseClient.getUserById(workflowTransfer.getAgentId());
            //授权人
            ContactCO authorizer = baseClient.getUserById(workflowTransfer.getAuthorizerId());
            workflowTransfer.setAuthorizerName(authorizer.getFullName());
            workflowTransfer.setAuthorizerCode(authorizer.getEmployeeCode());
            workflowTransfer.setAgentName(agent.getFullName());
            workflowTransfer.setAgentCode(agent.getEmployeeCode());
            //设置审批流名称
            if(workflowId != null){
                ApprovalForm approvalForm = approvalFormService.selectById(workflowId);
                if(approvalForm != null){
                    workflowTransfer.setWorkflowName(approvalForm.getFormName());
                }
            }
            if(documentCategory != null ){
               SysCodeValueCO sysCodeValueCO = baseClient.getSysCodeValueByCodeAndValue("SYS_APPROVAL_FORM_TYPE",documentCategory);
               if(sysCodeValueCO != null){
                   workflowTransfer.setDocumentCategoryName(sysCodeValueCO.getName());
               }
            }
        });
        return workflowTransferDTOS;
    }

    /**
     *  编辑转交
     * @param workflowTransfer
     * @return
     */
    public WorkflowTransfer updateWorkflowTransfer(WorkflowTransfer workflowTransfer) {
        if(baseMapper.selectById(workflowTransfer.getId()) == null){
            throw new BizException(RespCode.WORKFLOW_TRANSFER_NOT_EXIST);
        }
        baseMapper.updateAllColumnById(workflowTransfer);
        return workflowTransfer;
    }

}
