package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.constant.WorkflowConstants;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.domain.WorkflowRole;
import com.hand.hcf.app.workflow.domain.WorkflowRulesSetting;
import com.hand.hcf.app.workflow.dto.WorkflowRoleDTO;
import com.hand.hcf.app.workflow.dto.WorkflowRulesSettingDTO;
import com.hand.hcf.app.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.enums.WorkflowRoleType;
import com.hand.hcf.app.workflow.enums.WorkflowSettingDefaultRulesEnum;
import com.hand.hcf.app.workflow.enums.WorkflowSettingType;
import com.hand.hcf.app.workflow.persistence.WorkFlowRoleMapper;
import com.hand.hcf.app.workflow.persistence.WorkFlowRuleSettingMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing WorkflowRulesSetting.
 */
@Service
@Transactional
public class WorkflowRulesSettingService extends BaseService<WorkFlowRuleSettingMapper, WorkflowRulesSetting> {

    private final Logger log = LoggerFactory.getLogger(WorkflowRulesSettingService.class);

    @Autowired
    BaseClient baseClient;

    @Autowired
    private WorkFlowRoleMapper workFlowRoleMapper;

    @Autowired
    private MapperFacade mapper;

    /**
     * Save a workflowRulesSetting.
     *
     * @param workflowRulesSettingDTO the entity to save
     * @return the persisted entity
     */
    public WorkflowRulesSettingDTO save(WorkflowRulesSettingDTO workflowRulesSettingDTO) {
        log.debug("Request to save WorkflowRulesSetting : {}", workflowRulesSettingDTO);
        //1.workflowRulesSettingDTO对象转换为存储对象
        WorkflowRulesSetting workflowRulesSetting = workflowRulesSettingDTOToWorkflowRulesSetting(workflowRulesSettingDTO);
        workflowRulesSetting.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());//uuid
        workflowRulesSetting.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        return save(workflowRulesSetting, workflowRulesSettingDTO.getRoleList());
    }

    public WorkflowRulesSettingDTO save(WorkflowRulesSetting workflowRulesSetting, List<WorkflowRoleDTO> workflowRoleDTOs) {
        //1.List<WorkflowRoleDTO> 转为  List<WorkflowRole>
        List<WorkflowRole> workflowRoleList = workflowRoleDTOsToWorkflowRoles(workflowRoleDTOs);
        for (WorkflowRole workflowRole : workflowRoleList) {
            workflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
            workflowRole.setWorkflowRoleOid(UUID.randomUUID());
            //2.保存
            workFlowRoleMapper.insert(workflowRole);
        }
        this.insert(workflowRulesSetting);
        // List<WorkflowRole> list = workflowRoleRepository.save(workflowRoleList);
        //3.转化为dto前台显示
        WorkflowRulesSettingDTO result = new WorkflowRulesSettingDTO();
        mapper.map(workflowRulesSetting, result);
        List<WorkflowRoleDTO> rs = new ArrayList<>();
        mapper.map(workflowRoleList, rs);

        result.setRoleList(rs);
        return result;
    }




    /**
     * 更新
     *
     * @param workflowRulesSettingDTO
     * @return
     */
    @Transactional()
    public WorkflowRulesSettingDTO update(WorkflowRulesSettingDTO workflowRulesSettingDTO) {
        List<WorkflowRulesSetting> workflowRulesSettings = this.selectList(new EntityWrapper<WorkflowRulesSetting>().eq("workflow_rules_setting_oid", workflowRulesSettingDTO.getWorkflowRulesSettingOid()));
        WorkflowRulesSetting workflowRulesSetting = null;
        if (workflowRulesSettings != null) {//更新
            workflowRulesSetting = workflowRulesSettings.get(0);
            Map<String, Object> map = Maps.newHashMap();
            map.put("workflow_rules_setting_oid", workflowRulesSetting.getWorkflowRulesSettingOid());
            workFlowRoleMapper.deleteByMap(map);
            //List<Double> amount = workflowRulesSettingDTO.getAmount();
            //workflowRulesSetting.setAmount(Joiner.on(",").join(amount.stream().sorted(Double::compareTo).collect(Collectors.toList())));//金额
        }
        // workflowRulesSetting.getRoleList().clear();//删除引用
        return save(workflowRulesSetting, workflowRulesSettingDTO.getRoleList());
    }


    /**
     * 创建默认自审批规则
     *
     * @param userId     用户oid
     * @param companyOID 公司oid
     */
    @Deprecated
    public void createDefaultOwnerWorkflowRulesSetting(Long userId, UUID companyOID) {
        //默认审批规则为自审批
        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());
        workflowRulesSetting.setCreatedBy(userId);//创建人
        workflowRulesSetting.setCompanyOid(companyOID);//公司
        workflowRulesSetting.setRuleType(WorkflowSettingType.OWNER_CONDITION.getId().toString());//类型自审批
        List<WorkflowRole> roleList = new ArrayList<>();
        WorkflowRole workflowRole = new WorkflowRole();
        workflowRole.setSequenceNumber(0);
        workflowRole.setRuleType(WorkflowRoleType.OWNER.getId().toString());
        workflowRole.setWorkflowRoleOid(UUID.randomUUID());
        workflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
        roleList.add(workflowRole);
        workflowRulesSetting.setRoleList(roleList);
        WorkflowRulesSettingDTO dto = new WorkflowRulesSettingDTO();
        mapper.map(workflowRulesSetting, dto);
        this.save(dto);
    }

    /**
     * 根据配置生成审批规则
     *
     * @param approvalMode
     * @param userId
     * @param companyOID
     */
    @Deprecated
    public void createWorkflowRulesSettingByApprovalMode(ApprovalMode approvalMode, Long userId, UUID companyOID) {
        // List<WorkflowRulesSetting> list = workflowRulesSettingRepository.findByTypeAndCompanyOID(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOID);
        List<WorkflowRulesSetting> list = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                .eq("rule_type", WorkflowSettingType.NO_CONDITION.getId().toString())
                .eq("company_oid", companyOID));

        if (CollectionUtils.isNotEmpty(list)) {
            //workflowRulesSettingRepository.deleteAll(list);
            List<Long> ids = list.stream().map(m -> {
                return m.getId();
            }).collect(Collectors.toList());
            this.deleteBatchIds(ids);
        }

        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        WorkflowRole workflowRole = new WorkflowRole();
        switch (approvalMode) {
            case DEPARTMENT:
                workflowRole.setRuleType(WorkflowRoleType.DEPARTMENT_MANAGER.getId().toString());
                break;
            case COST_CENTER:
                workflowRole.setRuleType(WorkflowRoleType.COST_CENTER_ITEM_MANAGER.getId().toString());
                break;
            default:
                return;
        }
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());
        workflowRulesSetting.setCreatedBy(userId);//创建人
        workflowRulesSetting.setCompanyOid(companyOID);//公司
        workflowRulesSetting.setRuleType(WorkflowSettingType.NO_CONDITION.getId().toString());
        List<WorkflowRole> roleList = new ArrayList<>();
        workflowRole.setSequenceNumber(0);
        workflowRole.setWorkflowRoleOid(UUID.randomUUID());
        workflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
        roleList.add(workflowRole);
        workflowRulesSetting.setRoleList(roleList);

        WorkflowRulesSettingDTO dto = new WorkflowRulesSettingDTO();
        mapper.map(workflowRulesSetting, dto);
        this.save(dto);
    }


    /**
     * 获取不分条件审批规则
     *
     * @param companyOID           公司OID
     * @param chooseDepartment     所选部门
     * @param chooseCostCenterItem 所有成本中心
     * @param userOid              用户所在部门
     * @param approvalOIDs         所选审批人
     * @return
     */
    public List<String> getWorkflowRulesSettingRoleListNoCondition(UUID companyOID, UUID chooseDepartment, UUID chooseCostCenterItem, UUID userOid, String approvalOIDs, Integer entityTypeEnum) {
        try {
            Wrapper wrapper = new EntityWrapper<WorkflowRulesSetting>();
            wrapper.eq("rule_type", WorkflowSettingType.NO_CONDITION.getId().toString())
                    .eq(companyOID != null, "company_oid", companyOID)
                    .eq(entityTypeEnum != null, "entity_type", entityTypeEnum);
            WorkflowRulesSetting workflowRulesSetting = this.selectOne(wrapper);
            if (workflowRulesSetting == null) {//规则不存在
                throw new ValidationException(new ValidationError("workflowRulesSetting.not.exist!", "workflowRulesSetting.not.exist!"));
            }
            HashMap<Integer, DepartmentCO> departmentHashMap = baseClient.getAllDepartment(chooseDepartment, userOid);
            List<WorkflowRole> workflowRoles = workflowRulesSetting.getRoleList().stream().sorted(Comparator.comparingInt(WorkflowRole::getSequenceNumber)).collect(Collectors.toList());


            List<String> userPick = null;
            if (StringUtils.isNotBlank(approvalOIDs)) {
                userPick = new ArrayList<>(Arrays.asList(approvalOIDs.split(WorkflowConstants.WORKFLOW_APPROVAL_SPLIT)));
            }
            LinkedList<String> list = new LinkedList<>();

            for (WorkflowRole wr : workflowRoles) {
                if (wr.getRuleType().equals(WorkflowRoleType.USER.getId().toString())) {//用户类型
                    if (wr.getUserOid() == null) {
                        throw new ValidationException(new ValidationError("WorkflowRoleType.USER", "WorkflowRoleType.USER.empty"));
                    }
                    dealWithResult(list, wr.getUserOid().toString());
                } else if (wr.getRuleType().equals(WorkflowRoleType.DEPARTMENT_MANAGER.getId().toString())) {
                    DepartmentCO level = departmentHashMap.get(wr.getNumbers());
                    if (level == null) {//所配部门不存在 跳过
                    } else {
//                        User departmentManager = level.getManager();
                        UUID departmentManager = baseClient.getDepartmentManagerOid(level.getId());
                        if (departmentManager == null) {//部门主管不存在报错!
                            throw new ValidationException(new ValidationError("WorkflowRoleType.DEPARTMENT_MANAGER", "departmentManager is not exist!"));
                        }
                        dealWithResult(list, departmentManager.toString());
                    }
                } else if (wr.getRuleType().equals(WorkflowRoleType.USER_PICK.getId().toString())) {
                    if (CollectionUtils.isNotEmpty(userPick)) {
                        if (userPick.size() > wr.getNumbers()) {
                            throw new ValidationException(new ValidationError("WorkflowRoleType.USER_PICK", "USER_PICK siz error!"));
                        }
                        for (String user : userPick) {
                            dealWithResult(list, user);
                        }
                    }
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_APPROVAL_CHAIN_IS_NULL);
        }

    }

    private void dealWithResult(LinkedList<String> list, String userOid) {
        if (CollectionUtils.isEmpty(list)) {
            list.add(userOid);
        } else {
            String last = list.getLast();
            if (!last.equals(userOid)) {
                list.add(userOid);
            }
        }
    }




    public WorkflowRulesSetting workflowRulesSettingDTOToWorkflowRulesSetting(WorkflowRulesSettingDTO workflowRulesSettingDTO) {
        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        if (workflowRulesSettingDTO.getType() != null) {
            if (workflowRulesSettingDTO.getType().equals(WorkflowSettingType.NO_CONDITION.getId().toString())) {
                workflowRulesSetting.setRuleType(WorkflowSettingType.NO_CONDITION.getId().toString());//无条件
            } else {
                workflowRulesSetting.setRuleType(WorkflowSettingType.HAS_CONDITION.getId().toString());//有条件
                workflowRulesSetting.setDepartmentOid(workflowRulesSettingDTO.getDepartmentOid());//部门id
                workflowRulesSetting.setCostCenterOid(workflowRulesSettingDTO.getCostCenterOid());//成本中心
                workflowRulesSetting.setCostCenterItemOid(workflowRulesSettingDTO.getCostCenterItemOid());//成本中心项目id
                List<Double> amount = workflowRulesSettingDTO.getAmount();
                workflowRulesSetting.setAmount(Joiner.on(",").join(amount.stream().sorted(Double::compareTo).collect(Collectors.toList())));//金额
            }
            workflowRulesSetting.setEntityType(workflowRulesSettingDTO.getEntityType());
        }
        return workflowRulesSetting;
    }

    public WorkflowRoleDTO workflowRoleToWorkflowRoleDTO(WorkflowRole workflowRole) {
        WorkflowRoleDTO rs = new WorkflowRoleDTO();
        rs.setWorkflowRoleOid(workflowRole.getWorkflowRoleOid());
        rs.setWorkflowRulesSettingOid(workflowRole.getWorkflowRulesSettingOid());
        rs.setCreatedBy(workflowRole.getCreatedBy());
        rs.setLastUpdatedBy(workflowRole.getLastUpdatedBy());
        rs.setSequenceNumber(workflowRole.getSequenceNumber());
        rs.setAmountSequence(workflowRole.getAmountSequence());
        rs.setRuleType(workflowRole.getRuleType());
        rs.setUpperBound(workflowRole.getUpperBound());
        rs.setLowerBound(workflowRole.getLowerBound());
        rs.setNumber(workflowRole.getNumbers());
        rs.setDepartmentManagerId(workflowRole.getDepartmentManagerId());
        if (rs.getUserOid() != null) {
            rs.setUserName(baseClient.getUserByUserOid(rs.getUserOid()).getFullName());
        }

        if (workflowRole.getRuleType().equals(WorkflowRoleType.USER.getId().toString())) {
            rs.setUserOid(workflowRole.getUserOid());
        } else if (workflowRole.getRuleType().equals(WorkflowRoleType.URL.getId().toString())) {
            rs.setUrl(workflowRole.getUrl());//地址
        }
        return rs;
    }


    /**
     * workflowRoleDTO 对象转为 workflowRole
     *
     * @param workflowRoleDTO
     * @return
     */
    public WorkflowRole workflowRoleDTOToWorkflowRole(WorkflowRoleDTO workflowRoleDTO) {
        WorkflowRole wr = new WorkflowRole();
        wr.setSequenceNumber(workflowRoleDTO.getSequenceNumber());//序列
        wr.setRuleType(workflowRoleDTO.getRuleType());//类型
        wr.setUpperBound(workflowRoleDTO.getUpperBound());//上界
        wr.setLowerBound(workflowRoleDTO.getLowerBound());//下届
        wr.setAmountSequence(workflowRoleDTO.getAmountSequence());//金额序列
        wr.setNumbers(workflowRoleDTO.getNumber());
        if (workflowRoleDTO.getRuleType().equals(WorkflowRoleType.USER.getId().toString())) {
            wr.setUserOid(workflowRoleDTO.getUserOid());
        } else if (workflowRoleDTO.getRuleType().equals(WorkflowRoleType.URL.getId().toString())) {
            wr.setUrl(workflowRoleDTO.getUrl());//地址
        }
        return wr;
    }

    /**
     * List<WorkflowRoleDTO> 转为  List<WorkflowRole>
     *
     * @param workflowRoleDTOs
     * @return
     */
    public List<WorkflowRole> workflowRoleDTOsToWorkflowRoles(List<WorkflowRoleDTO> workflowRoleDTOs) {
        List<WorkflowRole> list = new ArrayList<WorkflowRole>();
        workflowRoleDTOs.stream().forEach(workflowRoleDTO -> list.add(workflowRoleDTOToWorkflowRole(workflowRoleDTO)));
        return list;
    }
}
