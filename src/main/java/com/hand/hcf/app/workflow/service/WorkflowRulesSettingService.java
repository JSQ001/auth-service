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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing WorkflowRulesSetting.
 */
@Service
@Transactional
public class WorkflowRulesSettingService extends BaseService<WorkFlowRuleSettingMapper,WorkflowRulesSetting> {

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
        WorkflowRulesSetting workflowRulesSetting =workflowRulesSettingDTOToWorkflowRulesSetting(workflowRulesSettingDTO);
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
        mapper.map(workflowRulesSetting,result);
        List<WorkflowRoleDTO> rs = new ArrayList<>();
        mapper.map(workflowRoleList,rs);

        result.setRoleList(rs);
        return result;
    }
    /**
     * Get all the workflowRulesSettings.
     *
     * @param page the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public com.baomidou.mybatisplus.plugins.Page<WorkflowRulesSetting> findAll(com.baomidou.mybatisplus.plugins.Page page) {
        log.debug("Request to get all WorkflowRulesSettings");

        com.baomidou.mybatisplus.plugins.Page<WorkflowRulesSetting> result = this.selectPage(page);
        return result;
    }

    /**
     * Get one workflowRulesSetting by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public WorkflowRulesSettingDTO findOne(Long id) {
        log.debug("Request to get WorkflowRulesSetting : {}", id);
        WorkflowRulesSetting workflowRulesSetting = this.selectById(id);
        WorkflowRulesSettingDTO workflowRulesSettingDTO = new WorkflowRulesSettingDTO();
        mapper.map(workflowRulesSetting,workflowRulesSettingDTO);
        //WorkflowRulesSettingDTO workflowRulesSettingDTO = workFlowRuleSettingMapper.workflowRulesSettingToWorkflowRulesSettingDTO(workflowRulesSetting);
        return workflowRulesSettingDTO;
    }

    @Transactional(readOnly = true)
    public WorkflowRulesSettingDTO findByWorkflowRulesSettingOid(UUID workflowRulesSettingOid) {
        log.debug("Request to get WorkflowRulesSetting : {}", workflowRulesSettingOid);
        //1.根据workflowRulesSettingOid获取对象
        List<WorkflowRulesSetting> workflowRulesSettings = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
            .eq("workflow_rules_setting_oid",workflowRulesSettingOid)
            .eq("created_by",OrgInformationUtil.getCurrentUserOid()));
        //WorkflowRulesSetting workflowRulesSetting = workflowRulesSettingRepository.findByWorkflowRulesSettingOidAndCreatedBy(workflowRulesSettingOid, );
        WorkflowRulesSetting workflowRulesSetting = null;
        if(workflowRulesSettings != null && workflowRulesSettings.size() > 0){
            workflowRulesSetting = workflowRulesSettings.get(0);
        }
        if (workflowRulesSetting == null) {
            return new WorkflowRulesSettingDTO();
        }
        //1.转化为dto对象
        WorkflowRulesSettingDTO workflowRulesSettingDTO = new WorkflowRulesSettingDTO();
        mapper.map(workflowRulesSetting,workflowRulesSettingDTO);
        //2.获取审批流程
        if (workflowRulesSetting != null) {
            //List<WorkflowRoleDTO> workflowRoleDTOList = WorkflowRoleMapper.workflowRolesToWorkflowRoleDTOs(workflowRulesSetting.getRoleList());
            List<WorkflowRole> workflowRoleList = workFlowRoleMapper.selectList(new EntityWrapper<WorkflowRole>().eq("workflow_rules_setting_oid",workflowRulesSetting.getWorkflowRulesSettingOid()));

            if(workflowRoleList != null){
                List<WorkflowRoleDTO> workflowRoleDTOList = new ArrayList<>();
                mapper.map(workflowRoleList,workflowRoleDTOList);
                workflowRulesSettingDTO.setRoleList(workflowRoleDTOList);
            }
        }
        return workflowRulesSettingDTO;
    }

    /**
     * 获取匹配的审批规则设置
     *
     * @param costCenterItemOID
     * @param departmentOID
     * @return
     */
    @Deprecated
    private WorkflowRulesSetting geExistWorkflowRulesSetting(UUID costCenterItemOID, UUID departmentOID) {
        UUID companyOID = OrgInformationUtil.getCurrentCompanyOid();

        //List<WorkflowRulesSetting> workflowRulesSettingList = workflowRulesSettingRepository.findByDepartmentOIDAndCostCenterItemOID(departmentOID, costCenterItemOID);
        List<WorkflowRulesSetting> workflowRulesSettingList = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                    .eq(departmentOID != null ,"department_oid",departmentOID)
                    .eq(costCenterItemOID != null,"cost_center_item_oid",costCenterItemOID));

        if (CollectionUtils.isEmpty(workflowRulesSettingList) && workflowRulesSettingList.size() == 0) {
            //workflowRulesSettingList = workflowRulesSettingRepository.findByTypeAndCompanyOID(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOID);
            workflowRulesSettingList =this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                    .eq("rule_type",WorkflowSettingType.NO_CONDITION.getId().toString())
                    .eq("company_oid",companyOID));
        }
        if (CollectionUtils.isEmpty(workflowRulesSettingList) && workflowRulesSettingList.size() == 0) {
            //workflowRulesSettingList = workflowRulesSettingRepository.findByTypeAndCompanyOID(WorkflowSettingType.OWNER_CONDITION.getId().toString(), companyOID);
            workflowRulesSettingList =this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                    .eq("rule_type",WorkflowSettingType.OWNER_CONDITION.getId().toString())
                    .eq("company_oid",companyOID));
        }
        /*if (CollectionUtils.isNotEmpty(workflowRulesSettingList) && workflowRulesSettingList.size() > 0 && workflowRulesSettingList.get(0) != null && CollectionUtils.isNotEmpty(workflowRulesSettingList.get(0).getRoleList())) {
            return workflowRulesSettingList.get(0);
        }*/
        if (CollectionUtils.isNotEmpty(workflowRulesSettingList) && workflowRulesSettingList.size() > 0 && workflowRulesSettingList.get(0) != null) {
            return workflowRulesSettingList.get(0);
        }
        return null;
    }



    /**
     * 例;金额50 金额区间[20,30,60]
     * 0     金额<20
     * 1 20<=金额<30
     * 2 30<=金额<60
     * 3 60<=金额
     * 金额50 在30<=金额<60 返回 2
     * 获取金额所在区间对应的审批步骤金额序列
     *
     * @param amount     金额
     * @param amountList 金额区间
     * @return
     */
    private int getAmountSequenceByAmount(Double amount, List<Double> amountList) {
        int seq = -1;
        for (int j = 0; j < amountList.size() + 1 && amountList.size() > 0; j++) {
            Double upperBound = 999999999D;
            Double lowerBound = -1D;
            if (j >= 1) {
                lowerBound = amountList.get(j - 1);
            }
            if (j < amountList.size()) {
                upperBound = amountList.get(j);
            }
            if (upperBound > amount.intValue() && amount >= lowerBound) {
                seq = j;
                break;
            }
        }
        return seq;
    }




    /**
     * Delete the  workflowRulesSetting by id.
     *
     * @param id the id of the entity
     */
    public void deleteByworkflowRulesSettingOid(Long id) {
        log.debug("Request to deleteByworkflowRulesSettingOid WorkflowRulesSetting : {}", id);
        this.deleteById(id);
    }

    public List<WorkflowRulesSetting> findByTypeAndCompanyOID(String type, UUID companyOID) {
        List<WorkflowRulesSetting> list = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                .eq("rule_type",type)
                .eq("company_oid",companyOID));
        return list;
    }

    public com.baomidou.mybatisplus.plugins.Page<WorkflowRulesSetting> findByTypeAndCompanyOID(String type, UUID companyOID, com.baomidou.mybatisplus.plugins.Page pageable) {
        com.baomidou.mybatisplus.plugins.Page<WorkflowRulesSetting> list = this.selectPage(pageable,new EntityWrapper<WorkflowRulesSetting>()
                .eq("rule_type",type)
                .eq("company_oid",companyOID));
        return list;
    }

    /**
     * 更新
     *
     * @param workflowRulesSettingDTO
     * @return
     */
    @Transactional()
    public WorkflowRulesSettingDTO update(WorkflowRulesSettingDTO workflowRulesSettingDTO) {
        List<WorkflowRulesSetting> workflowRulesSettings = this.selectList(new EntityWrapper<WorkflowRulesSetting>().eq("workflow_rules_setting_oid",workflowRulesSettingDTO.getWorkflowRulesSettingOid()));
        WorkflowRulesSetting workflowRulesSetting = null;
        if (workflowRulesSettings != null) {//更新
            workflowRulesSetting = workflowRulesSettings.get(0);
            Map<String,Object> map = Maps.newHashMap();
            map.put("workflow_rules_setting_oid",workflowRulesSetting.getWorkflowRulesSettingOid());
            workFlowRoleMapper.deleteByMap(map);
            //List<Double> amount = workflowRulesSettingDTO.getAmount();
            //workflowRulesSetting.setAmount(Joiner.on(",").join(amount.stream().sorted(Double::compareTo).collect(Collectors.toList())));//金额
        }
        // workflowRulesSetting.getRoleList().clear();//删除引用
        return save(workflowRulesSetting, workflowRulesSettingDTO.getRoleList());
    }

    /**
     * 删除
     *
     * @param workflowRulesSettingOid
     */
    public void deleteByWorkflowRulesSettingOid(UUID workflowRulesSettingOid) {
        log.debug("Request to deleteByworkflowRulesSettingOid WorkflowRulesSetting : {}", workflowRulesSettingOid);
        List<WorkflowRulesSetting> rs = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                .eq("workflow_rules_setting_oid",workflowRulesSettingOid)
                .eq("created_by",OrgInformationUtil.getCurrentUserOid()));
        if (rs != null) {
            this.deleteById(rs.get(0).getId());
        }
    }

    public List<WorkflowRulesSetting> findByDepartmentOIDAndCostCenterItemOID(UUID departmentOID, UUID costCenterItemOID) {
       // return workflowRulesSettingRepository.findByDepartmentOIDAndCostCenterItemOID(departmentOID, costCenterItemOID);
        return this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                .eq("department_oid",departmentOID)
                .eq("cost_center_item_oid",costCenterItemOID));
    }

    /**
     * 创建默认自审批规则
     *
     * @param userId    用户oid
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
        mapper.map(workflowRulesSetting,dto);
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
                .eq("rule_type",WorkflowSettingType.NO_CONDITION.getId().toString())
                .eq("company_oid",companyOID));

        if (CollectionUtils.isNotEmpty(list)) {
            //workflowRulesSettingRepository.deleteAll(list);
            List<Long> ids = list.stream().map(m -> {return m.getId();}).collect(Collectors.toList());
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
        mapper.map(workflowRulesSetting,dto);
        this.save(dto);
    }

    /**
     * 获取无条件审批规则
     *
     * @param companyOID
     * @return
     */
    public WorkflowRulesSetting getWorkflowRulesSettingRoleListNoCondition(UUID companyOID) {
        //workflowRulesSetting = workflowRulesSettingRepository.findOneByTypeAndCompanyOID(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOID);
        List<WorkflowRulesSetting> workflowRulesSetting = this.selectList(new EntityWrapper<WorkflowRulesSetting>()
                .eq("rule_type",WorkflowSettingType.NO_CONDITION.getId().toString())
                .eq("company_oid",companyOID));
        if(workflowRulesSetting == null || workflowRulesSetting.size() == 0){
            return null;
        }
        return workflowRulesSetting.get(0);
    }


    /**
     * 获取不分条件审批规则
     *
     * @param companyOID           公司OID
     * @param chooseDepartment     所选部门
     * @param chooseCostCenterItem 所有成本中心
     * @param userOid        用户所在部门
     * @param approvalOIDs         所选审批人
     * @return
     */
    public List<String> getWorkflowRulesSettingRoleListNoCondition(UUID companyOID, UUID chooseDepartment, UUID chooseCostCenterItem, UUID userOid, String approvalOIDs, Integer entityTypeEnum) {
        try {
            Wrapper wrapper = new EntityWrapper<WorkflowRulesSetting>();
            wrapper.eq("rule_type",WorkflowSettingType.NO_CONDITION.getId().toString())
                    .eq(companyOID != null,"company_oid",companyOID)
                    .eq(entityTypeEnum != null,"entity_type",entityTypeEnum);
            WorkflowRulesSetting workflowRulesSetting = this.selectOne(wrapper);
            if (workflowRulesSetting == null) {//规则不存在
                throw new ValidationException(new ValidationError("workflowRulesSetting.not.exist!", "workflowRulesSetting.not.exist!"));
            }
            HashMap<Integer, DepartmentCO> departmentHashMap = baseClient.getAllDepartment(chooseDepartment,userOid);
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
                } else if (wr.getRuleType().equals(WorkflowRoleType.COST_CENTER_ITEM_MANAGER.getId().toString())) {
                   /* CostCenterItem costCenterItem = costCenterItemRepository.findByCostCenterItemOID(chooseCostCenterItem);
                    if (costCenterItem == null || costCenterItem.getManager() == null || costCenterItem.getManager().getUserOid() == null) {
                        throw new ValidationException(new ValidationError("WorkflowRoleType.COST_CENTER_ITEM_MANAGER", "WorkflowRoleType.COST_CENTER_ITEM_MANAGER.empty"));
                    }
                    dealWithResult(list, costCenterItem.getManager().getUserOid().toString());*/
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



    public List<WorkflowRulesSetting> createNoConditionDefaultRules(Integer defaultType, UUID userOid, UUID companyOID) {
        List<WorkflowRulesSetting> delWorkflowRulesSettings = new ArrayList<>();

        //1.费用审批申请流
       /* WorkflowRulesSetting application = workflowRulesSettingRepository.findOneByTypeAndCompanyOIDAndEntityType(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOID, EntityTypeEnum.APPLICATION.getId());
        if (application != null) {
            delWorkflowRulesSettings.add(application);
        }
        //2.报销单审批申请流
        WorkflowRulesSetting expenseReport = workflowRulesSettingRepository.findOneByTypeAndCompanyOIDAndEntityType(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOID, EntityTypeEnum.EXPENSE_REPORT.getId());
        if (application != null) {
            delWorkflowRulesSettings.add(expenseReport);
        }

        if (CollectionUtils.isNotEmpty(delWorkflowRulesSettings)) {//有存在的审批流程
            workflowRulesSettingRepository.deleteAll(delWorkflowRulesSettings);
        }
        List<WorkflowRulesSetting> saveWorkflowRulesSettings = new ArrayList<>();
        WorkflowRulesSetting saveApplication = getDefaultWorkflowSetting(defaultType, userOid, companyOID, EntityTypeEnum.APPLICATION);
        WorkflowRulesSetting saveExpenseReport = getDefaultWorkflowSetting(defaultType, userOid, companyOID, EntityTypeEnum.EXPENSE_REPORT);
        saveWorkflowRulesSettings.add(saveApplication);
        saveWorkflowRulesSettings.add(saveExpenseReport);
        if (CollectionUtils.isNotEmpty(saveWorkflowRulesSettings)) {
            workflowRulesSettingRepository.saveAll(saveWorkflowRulesSettings);
        }
        return saveWorkflowRulesSettings;*/
       return null;
    }

    private WorkflowRulesSetting getDefaultWorkflowSetting(Integer defaultType, Long userId, UUID companyOID, Integer entityTypeEnum) {
        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());
        workflowRulesSetting.setCreatedBy(userId);//创建人
        workflowRulesSetting.setCompanyOid(companyOID);//公司
        workflowRulesSetting.setRuleType(WorkflowSettingType.NO_CONDITION.getId().toString());//类型自审批
        List<WorkflowRole> roleList = new ArrayList<>();
        WorkflowRole workflowRole = new WorkflowRole();
        workflowRole.setSequenceNumber(0);
        if (WorkflowSettingDefaultRulesEnum.USER.getId().intValue() == defaultType) {
            workflowRole.setRuleType(WorkflowRoleType.USER_PICK.getId().toString());
        } else {
            workflowRole.setRuleType(WorkflowRoleType.DEPARTMENT_MANAGER.getId().toString());
        }
        workflowRulesSetting.setEntityType(entityTypeEnum);
        workflowRole.setNumbers(1);
        workflowRole.setWorkflowRoleOid(UUID.randomUUID());
        workflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
        roleList.add(workflowRole);
        workflowRulesSetting.setRoleList(roleList);
        return workflowRulesSetting;
    }

    private WorkflowRulesSetting getDefaultWorkflowSetting(Integer defaultType, Long userId, UUID companyOID, Integer entityTypeEnum, Integer number) {

        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());
        workflowRulesSetting.setCreatedBy(userId);//创建人
        workflowRulesSetting.setCompanyOid(companyOID);//公司
        workflowRulesSetting.setRuleType(WorkflowSettingType.NO_CONDITION.getId().toString());//类型自审批
        List<WorkflowRole> roleList = new ArrayList<>();
        WorkflowRole workflowRole = new WorkflowRole();
        workflowRole.setSequenceNumber(0);
        if (WorkflowSettingDefaultRulesEnum.USER.getId().intValue() == defaultType) {
            workflowRole.setRuleType(WorkflowRoleType.USER_PICK.getId().toString());
        } else {
            workflowRole.setRuleType(WorkflowRoleType.DEPARTMENT_MANAGER.getId().toString());
        }
        workflowRulesSetting.setEntityType(entityTypeEnum);
        workflowRole.setNumbers(number);
        workflowRole.setWorkflowRoleOid(UUID.randomUUID());
        workflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
        roleList.add(workflowRole);
        workflowRulesSetting.setRoleList(roleList);
        return workflowRulesSetting;
    }


    public List<WorkflowRulesSetting> findByTypeAndCompanyOIDAndEntityType(String type, UUID companyOID, Integer entityType) {
        //List<WorkflowRulesSetting> list = workflowRulesSettingRepository.findByTypeAndCompanyOIDAndEntityType(type, companyOID, entityType);
        Wrapper wrapper = new EntityWrapper<WorkflowRulesSetting>();
        wrapper.eq("rule_type",WorkflowSettingType.NO_CONDITION.getId().toString())
                .eq(companyOID != null,"company_oid",companyOID)
                .eq(entityType != null,"entity_type",entityType);
        List<WorkflowRulesSetting> list = this.selectList(wrapper);
        return list;
    }

    /**
     * 为没有审批规则的公司添加报销单规则和申请规则
     * 1.有报销单规则或申请规则 拷贝
     * 2.报销单和申请都没有 从公司配置取 生成规则
     * 3.公司配置没有 使用默认设置生成
     */
    public void batchAddWorkflowRulesSettings() {
        List<WorkflowRulesSetting> saveWorkflowRulesSettings = new ArrayList<>();
        //1.获取所有公司
        //List<Company> companys = companyService.findAll();
        //List<UUID> companyOIDs = companys.stream().map(Company::getCompanyOid).collect(Collectors.toList());
        //2.获取无条件报销和申请所有审批规则
        /*List<WorkflowRulesSetting> workflowRulesSettings = workflowRulesSettingRepository.findByTypeAndCompanyOIDInAndEntityTypeIn(WorkflowSettingType.NO_CONDITION.getId().toString(), companyOIDs, Arrays.asList(EntityTypeEnum.APPLICATION.getId(), EntityTypeEnum.EXPENSE_REPORT.getId()));*/
        /*
        //3.处理公司对应的审批规则
        HashMap<UUID, HashMap<Integer, WorkflowRulesSetting>> hashMapWorkflowRulesSetting = new HashMap<UUID, HashMap<Integer, WorkflowRulesSetting>>();
        for (WorkflowRulesSetting workflowRulesSetting : workflowRulesSettings) {
            HashMap<Integer, WorkflowRulesSetting> hashMap = hashMapWorkflowRulesSetting.get(workflowRulesSetting.getCompanyOid());
            if (hashMap == null) {
                hashMap = new HashMap<Integer, WorkflowRulesSetting>();
            }
            hashMap.put(workflowRulesSetting.getEntityType(), workflowRulesSetting);
            hashMapWorkflowRulesSetting.put(workflowRulesSetting.getCompanyOid(), hashMap);
        }

        //4.处理没有审批规则的公司
        for (UUID companyOID : companyOIDs) {
            HashMap<Integer, WorkflowRulesSetting> hashMapType = hashMapWorkflowRulesSetting.get(companyOID);
            if (hashMapType == null) {//没有任何审批规则 取公司配置生成审批规则
                Optional<CompanyConfiguration> optional = companyConfigurationRepository.findOneByCompanyOID(companyOID);

                if (optional.isPresent()) {//有公司配置
                    CompanyConfiguration companyConfiguration = optional.get();
                    Integer number = WorkflowSettingConstants.NUMBER;//选人审批数
                    Integer workflowSettingDefaultRulesEnum = WorkflowSettingDefaultRulesEnum.DEPARTMENT.getId();//部门审批

                    if (companyConfiguration.getConfiguration().getApprovalRule().getApprovalMode().intValue() == ApprovalMode.USER_PICK.getId()) {//选人审批
                        number = companyConfiguration.getConfiguration().getApprovalRule().getMaxApprovalChain();
                        workflowSettingDefaultRulesEnum = WorkflowSettingDefaultRulesEnum.USER.getId();
                    }
                    WorkflowRulesSetting saveApplication = getDefaultWorkflowSetting(workflowSettingDefaultRulesEnum, null, companyOID, EntityTypeEnum.APPLICATION, number);
                    WorkflowRulesSetting saveExpenseReport = getDefaultWorkflowSetting(workflowSettingDefaultRulesEnum, null, companyOID, EntityTypeEnum.EXPENSE_REPORT, number);
                    saveWorkflowRulesSettings.add(saveApplication);
                    saveWorkflowRulesSettings.add(saveExpenseReport);
                } else {//无公司配置
                    WorkflowRulesSetting saveApplication = getDefaultWorkflowSetting(WorkflowSettingDefaultRulesEnum.DEPARTMENT.getId(), null, companyOID, EntityTypeEnum.APPLICATION);
                    WorkflowRulesSetting saveExpenseReport = getDefaultWorkflowSetting(WorkflowSettingDefaultRulesEnum.DEPARTMENT.getId(), null, companyOID, EntityTypeEnum.EXPENSE_REPORT);
                    saveWorkflowRulesSettings.add(saveApplication);
                    saveWorkflowRulesSettings.add(saveExpenseReport);
                }
            } else {
                WorkflowRulesSetting application = hashMapType.get(EntityTypeEnum.APPLICATION.getId());//费用差旅申请审批规则
                WorkflowRulesSetting expenseReport = hashMapType.get(EntityTypeEnum.EXPENSE_REPORT.getId());//报销单审批规则
                if (application != null && expenseReport != null) {//申请规则与报销规则都存在跳过
                    continue;
                }

                WorkflowRulesSetting copy = copyWorkflowRulesSetting(application, expenseReport);
                saveWorkflowRulesSettings.add(copy);
            }
        }
        workflowRulesSettingRepository.saveAll(saveWorkflowRulesSettings);//保存*/
    }

    /**
     * 申请和审批规则相互拷贝
     *
     * @param application
     * @param expenseReport
     * @return
     */
    private WorkflowRulesSetting copyWorkflowRulesSetting(WorkflowRulesSetting application, WorkflowRulesSetting expenseReport) {
        WorkflowRulesSetting workflowRulesSetting = new WorkflowRulesSetting();
        UUID compayOID = application == null ? expenseReport.getCompanyOid() : application.getCompanyOid();//公司
        List<WorkflowRole> workflowRoles = application == null ? expenseReport.getRoleList() : application.getRoleList();//角色
        workflowRulesSetting.setRuleType(WorkflowSettingType.NO_CONDITION.getId().toString());//类型
        workflowRulesSetting.setWorkflowRulesSettingOid(UUID.randomUUID());//UUID
        workflowRulesSetting.setCreatedDate(ZonedDateTime.now());//创建时间
        workflowRulesSetting.setCompanyOid(compayOID);//公司OID



        List<WorkflowRole> newWorkflowRoles = new ArrayList<>();
        for (WorkflowRole workflowRole : workflowRoles) {
            WorkflowRole newWorkflowRole = new WorkflowRole();
            BeanUtils.copyProperties(workflowRole, newWorkflowRole);

            newWorkflowRole.setId(null);//主键不拷贝
            newWorkflowRole.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
            newWorkflowRole.setWorkflowRoleOid(UUID.randomUUID());
            newWorkflowRole.setCreatedDate(ZonedDateTime.now());
            newWorkflowRoles.add(newWorkflowRole);
        }
        workflowRulesSetting.setRoleList(newWorkflowRoles);
        return workflowRulesSetting;
    }


    public  WorkflowRulesSettingDTO workflowRulesSettingToWorkflowRulesSettingDTO(WorkflowRulesSetting workflowRulesSetting) {
        WorkflowRulesSettingDTO dto = new WorkflowRulesSettingDTO();
        dto.setWorkflowRulesSettingOid(workflowRulesSetting.getWorkflowRulesSettingOid());
        dto.setType(workflowRulesSetting.getRuleType());
        if (workflowRulesSetting.getAmount() != null) {
            String[] rs = workflowRulesSetting.getAmount().split(",");
            List<String> list = Arrays.asList(rs);
            List<Double> amlist = list.stream().map(s -> Double.valueOf(s)).sorted(Double::compareTo).collect(Collectors.toList());
            dto.setAmount(amlist);
        }
        dto.setDepartmentOid(workflowRulesSetting.getDepartmentOid());
        dto.setCostCenterItemOid(workflowRulesSetting.getCostCenterItemOid());
        dto.setCostCenterOid(workflowRulesSetting.getCostCenterOid());
        dto.setCostCenterItemName(workflowRulesSetting.getCostCenterItemOid() != null ? workflowRulesSetting.getCostCenterItemOid().toString() : "");
        dto.setDepartmentName(workflowRulesSetting.getDepartmentOid() != null ? workflowRulesSetting.getDepartmentOid().toString() : "");
        dto.setEntityType(workflowRulesSetting.getEntityType());
        return dto;
    }

    public  List<WorkflowRulesSettingDTO> workflowRulesSettingsToWorkflowRulesSettingDTOs(List<WorkflowRulesSetting> workflowRulesSettings) {
        List<WorkflowRulesSettingDTO> list = new ArrayList<WorkflowRulesSettingDTO>();
        for (WorkflowRulesSetting workflowRulesSetting : workflowRulesSettings) {
            WorkflowRulesSettingDTO tmp = workflowRulesSettingToWorkflowRulesSettingDTO(workflowRulesSetting);
            tmp.setRoleList(workflowRolesToWorkflowRoleDTOs(workflowRulesSetting.getRoleList()));
            list.add(tmp);
        }
        return list;
    }

    public  WorkflowRulesSetting workflowRulesSettingDTOToWorkflowRulesSetting(WorkflowRulesSettingDTO workflowRulesSettingDTO) {
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

    public  WorkflowRoleDTO workflowRoleToWorkflowRoleDTO(WorkflowRole workflowRole) {
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
        if(rs.getUserOid()!=null){
            rs.setUserName(baseClient.getUserByUserOid(rs.getUserOid()).getFullName());
        }

        if (workflowRole.getRuleType().equals(WorkflowRoleType.USER.getId().toString())) {
            rs.setUserOid(workflowRole.getUserOid());
        } else if (workflowRole.getRuleType().equals(WorkflowRoleType.URL.getId().toString())) {
            rs.setUrl(workflowRole.getUrl());//地址
        }
        return rs;
    }

    public  List<WorkflowRoleDTO> workflowRolesToWorkflowRoleDTOs(List<WorkflowRole> workflowRoles) {
        List<WorkflowRoleDTO> result = new ArrayList<WorkflowRoleDTO>();
        workflowRoles.stream().sorted((r1, r2) -> {
            if (r1.getAmountSequence() == null || r2.getAmountSequence() == null) {
                return r1.getSequenceNumber() - r2.getSequenceNumber();
            }
            if (r1.getAmountSequence().intValue() == r2.getAmountSequence().intValue()) {
                return r1.getSequenceNumber() - r2.getSequenceNumber();
            } else {
                return r1.getAmountSequence() - r2.getAmountSequence();
            }
        }).forEach(rs -> {
            result.add(workflowRoleToWorkflowRoleDTO(rs));
        });
        return result;
    }

    /**
     * workflowRoleDTO 对象转为 workflowRole
     *
     * @param workflowRoleDTO
     * @return
     */
    public  WorkflowRole workflowRoleDTOToWorkflowRole(WorkflowRoleDTO workflowRoleDTO) {
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
    public  List<WorkflowRole> workflowRoleDTOsToWorkflowRoles(List<WorkflowRoleDTO> workflowRoleDTOs) {
        List<WorkflowRole> list = new ArrayList<WorkflowRole>();
        workflowRoleDTOs.stream().forEach(workflowRoleDTO -> list.add(workflowRoleDTOToWorkflowRole(workflowRoleDTO)));
        return list;
    }
}
