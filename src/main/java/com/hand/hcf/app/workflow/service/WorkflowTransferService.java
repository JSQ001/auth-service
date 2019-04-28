package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.TransferDTO;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkflowTransfer;
import com.hand.hcf.app.workflow.dto.WorkflowTransferDTO;
import com.hand.hcf.app.workflow.persistence.WorkflowTransferMapper;
import com.hand.hcf.app.workflow.util.ExceptionCode;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

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

    @Inject
    private ApprovalChainService approvalChainService;

    @Inject
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private ContactControllerImpl contactClient;
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
            throw new BizException(ExceptionCode.WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT);
        };
        workflowTransfer.setAuthorizerId(OrgInformationUtil.getCurrentUserId());
        workflowTransfer.setTenantId(OrgInformationUtil.getCurrentTenantId());
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
            ContactCO  agent= baseClient.getUserById(workflowTransfer.getAgentId());
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
            throw new BizException(ExceptionCode.WORKFLOW_TRANSFER_NOT_EXIST);
        }
        baseMapper.updateAllColumnById(workflowTransfer);
        return workflowTransfer;
    }

    /**
     * 转交单据
     * @param tenantId
     * @param userOid
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void transferDeliver(Long tenantId, UUID userOid, TransferDTO dto){
        Assert.notNull(dto.getEntityOid(), "dto.entityOid null");
        Assert.notNull(dto.getEntityType(), "dto.entityType null");
        Assert.notNull(dto.getUserOid(), "dto.userOid null");

        //加载审批任务
        WorkflowUser user = new WorkflowUser(userOid);
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(dto.getEntityOid(), dto.getEntityType());

        WorkflowTask task = workflowBaseService.findTask(new WorkflowInstance(workFlowDocumentRef), user);
        if(task == null){
            throw new BizException(ExceptionCode.WORKFLOW_TRANSFER_NOT_EXIST);
        }

        WorkflowNode node = task.getNode();
        WorkflowRule rule = node.getRule();

        //判断节点是否可以转交
        if (!Boolean.TRUE.equals(rule.getTransferFlag())) {
            throw new BizException(ExceptionCode.WORKFLOW_NODE_CANNOT_BE_FORWARDED);
        }

        //设置当前审批任务为无效
        task.setStatus(WorkflowTask.STATUS_INVALID);
        workflowBaseService.updateTask(task);

       //创建新的审批任务
        UUID userOidDeliver = dto.getUserOid();
        String remark = dto.getRemark();

        WorkflowUser deliverUser = new WorkflowUser(userOidDeliver);

        workflowBaseService.saveTask(node,deliverUser);

        //ContactCO contactCO = contactClient.getByUserOid(userOidDeliver.toString());
        //jiu.zhao 修改三方接口
        ContactCO contactCO = contactClient.getByUserOid(userOidDeliver);
        if(!contactCO.getTenantId().equals(tenantId)){
            throw new BizException(ExceptionCode.WORKFLOW_TENANT_NOT_OPENING);
        }

        //保存历史
        workflowBaseService.saveHistory(task, ApprovalOperationEnum.APPROVAL_TRANSFER.getId().toString(), deliverRemark(contactCO.getFullName(), contactCO.getEmployeeCode(), remark));
    }

    /**
     * 返回转交历史记录
     * @return
     */
    public String deliverRemark(String fullName, String employeeCode, String remark){
        return "转交至 " + fullName+"-"+employeeCode + "，理由 " + remark;
    }

}
