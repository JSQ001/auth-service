package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.DepartmentGroupDepartmentCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.enums.EmployeeStatusEnum;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.domain.Department;

import com.hand.hcf.app.mdata.department.domain.DepartmentImportDTO;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentPositionCode;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentTypeEnum;
import com.hand.hcf.app.mdata.department.dto.*;
import com.hand.hcf.app.mdata.department.persistence.DepartmentMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.system.enums.DataSourceTypeEnum;
import com.hand.hcf.app.mdata.utils.PathUtil;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;

import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*import com.hand.hcf.app.mdata.client.department.DepartmentGroupDepartmentCO;*/

/**
 * Service Implementation for managing Department.
 */
@Service
@Transactional
public class DepartmentService extends BaseService<DepartmentMapper, Department> {

    private final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    MapperFacade mapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentRoleService departmentRoleService;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private DepartmentPositionService departmentPositionService;
    @Autowired
    private DepartmentPositionUserService departmentPositionUserService;
    @Autowired
    private ContactService contactService;

    @Autowired
    private DepartmentUserService departmentUserService;
    public final static String CODE_VALIDATION_REGEX = "^[A-Za-z0-9]{1,100}$";   // 部门code验证表达式 只能包含字母和数字
    private final static String DEPARTMENTCODE = "departmentCodeHash";
    private final static String DEPARTMENTCODEPREFIX = "departmentCode:";
    @Autowired
    private DepartmentGroupService departmentGroupService;
    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;


    //TODO Cache add
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO, UUID currentUserOid, Long tenantId) {
        log.debug("Request to create Department : {}", departmentDTO);
        Department department = new Department();
        if (departmentDTO.getParentDepartmentOid() != null) {

            department.setParent(findByDepartmentOidAndStatus(departmentDTO.getParentDepartmentOid(), DepartmentTypeEnum.FIND_ENABLE.getId()));
        }
        if (departmentDTO.getManagerOid() != null) {
            department.setManager(contactService.getUserDTOByUserOid(departmentDTO.getManagerOid()));
        }
        if (StringUtils.isEmpty(departmentDTO.getName()) || departmentDTO.getName().length() > 50) {
            throw new BizException(RespCode.DEPARTMENT_NAME_EMPTY_OR_MORE_THEN_50_CHARACTERS);
        }
        // 判断部门编码是否为空
//        if(StringUtils.isEmpty(departmentDTO.getDepartmentCode())){
//            throw new BizException("6044010");
//        }
        department.setName(departmentDTO.getName());
        department.setPath((department.getParent() != null) ? department.getParent().getPath() + Constants.DEPARTMENT_SPLIT + departmentDTO.getName() : departmentDTO.getName());
        department.setDepartmentOid(UUID.randomUUID());
        department.setStatus(DepartmentTypeEnum.ENABLE.getId());
        department.setTenantId(tenantId);
        department.setDataSource(DataSourceTypeEnum.WEB.getDataSourceType());
        Department parent = this.selectOne(new EntityWrapper<Department>().eq("department_oid", departmentDTO.getParentDepartmentOid()));
        if (parent != null) {
            department.setParentId(parent.getId());
        }
        String departmentCode = departmentDTO.getDepartmentCode();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(departmentCode)) {
            checkDepartmentCode(tenantId, departmentCode);
            department.setDepartmentCode(departmentCode);
        }
        //Department departmentExists = findByPathAndCompanyCompanyOid(department.getPath(), departmentDTO.getCompanyOid(),DepartmentTypeEnum.FIND_ENABLE.getId());

        Department departmentExists = findByPathAndTenantId(department.getPath(), tenantId, DepartmentTypeEnum.FIND_ENABLE.getId());
        if (departmentExists != null && DepartmentTypeEnum.ENABLE.getId().equals(departmentExists.getStatus())) {
//            throw new ValidationException(new ValidationError("department", "department.exists"));
            throw new BizException(RespCode.DEPARTMENT_ALREADY_EXISTS);
        }
        this.insertOrUpdate(department);
        baseI18nService.insertOrUpdateI18n(departmentDTO.getI18n(), department.getClass(), department.getId());
        //2017-10-26  部门信息更改供应商同步
//        integrationService.syncDepartment(OrgInformationUtil.getCurrentCompanyOid(), department);
        if (departmentDTO.getDepartmentRole() != null) {
            departmentDTO.getDepartmentRole().setDepartmentOid(department.getDepartmentOid());
            departmentRoleService.upsertDepartmentRole(departmentDTO.getDepartmentRole());
        }
        //新建部门时更新fin财务角色所能查看的部门权限
//        FinanceRole superAud = financeRoleRepository.findByTenantIdAndRoleID(tenantId, "SuperAud");//超级查看权限
//        FinanceRole superFin = financeRoleRepository.findByTenantIdAndRoleID(tenantId, "SuperFin");//超级操作权限
//        superAud.getDepartments().add(department);
//        superFin.getDepartments().add(department);
//        financeRoleRepository.save(superAud);
//        financeRoleRepository.save(superFin);
        DepartmentDTO result = departmentToDepartmentDTO(department);
        String language = OrgInformationUtil.getCurrentLanguage();
        //记录创建部门操作日志
//        dataOperationService.save(currentUserOid.toString(), result, result.getDepartmentCode() == null ? messageTranslationService.getMessageDetailByCode(language, DataOperationMessageKey.ADD_DEPARTMENT_ID, result.getId()) : messageTranslationService.getMessageDetailByCode(language, DataOperationMessageKey.ADD_DEPARTMENT_CODE, result.getDepartmentCode()), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
        //    dataOperationService.save(currentUserOid,result,result.getDepartmentCode() == null ? "部门id:"+result.getId() : "部门编码:"+result.getDepartmentCode(), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.ADD.getKey(),tenantId);
//        if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//            DepartmentInfo departmentInfo = DepartmentDTOtoDepartmentInfo(result);
//            esDepartmentIndexSerivce.saveDepartmentInfoIndex(departmentInfo);
//        }
        return result;
    }

    public DepartmentDTO createDepartmentForImplement(DepartmentDTO departmentDTO, Long tenantId) {
        log.debug("Request to create Department : {}", departmentDTO);
        Department department = new Department();
        //推送部门使用
        UUID companyOid = departmentDTO.getCompanyOid();
//        Company company = companyRepository.getByCompanyOidCache(departmentDTO.getCompanyOid());
//        if (departmentDTO.getCompanyOid() != null && company != null) {
//            department.setCompany(company);

        if (departmentDTO.getParentDepartmentOid() != null) {
            Department tempDept = new Department();
            tempDept.setDepartmentOid(departmentDTO.getParentDepartmentOid());
            tempDept.setTenantId(tenantId);
            tempDept.setStatus(DepartmentTypeEnum.ENABLE.getId());
            tempDept = departmentMapper.selectOne(tempDept);
            department.setParent(tempDept);
        }
        UserDTO manager = contactService.getUserDTOByUserOid(departmentDTO.getManagerOid());
        if (departmentDTO.getManagerOid() != null && manager != null) {
            department.setManager(manager);
        }
        department.setName(departmentDTO.getName());
        department.setPath((department.getParent() != null) ? department.getParent().getPath() + Constants.DEPARTMENT_SPLIT + departmentDTO.getName() : departmentDTO.getName());
        department.setDepartmentOid(UUID.randomUUID());
        department.setStatus(DepartmentTypeEnum.ENABLE.getId());
        department.setTenantId(tenantId);
        department.setDataSource(DataSourceTypeEnum.IMPLEMENT.getDataSourceType());
        department.setCompany(null); //集团部门
        Department parent = this.selectOne(new EntityWrapper<Department>().eq("department_oid", departmentDTO.getParentDepartmentOid()));
        if (parent != null) {
            department.setParentId(parent.getId());
        }
        String departmentCode = departmentDTO.getDepartmentCode();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(departmentCode)) {
            checkDepartmentCode(tenantId, departmentCode);
            department.setDepartmentCode(departmentCode);
        }
        Department checkDepartment = findByPathAndTenantId(department.getPath(), tenantId, DepartmentTypeEnum.FIND_ENABLE.getId());
        if (checkDepartment != null && DepartmentTypeEnum.ENABLE.getId().equals(checkDepartment.getStatus())) {
            log.debug("Department :" + checkDepartment.getDepartmentOid() + "has been existed in Tenant :" + tenantId);
            return departmentToDepartmentDTO(checkDepartment);
        }
        this.insertOrUpdate(department);
//        if(companyOid != null) {
//            integrationService.syncDepartment(companyOid, department);
//        }
        baseI18nService.insertOrUpdateI18n(departmentDTO.getI18n(), department.getClass(), department.getId());
        if (departmentDTO.getDepartmentRole() != null) {
            departmentDTO.getDepartmentRole().setDepartmentOid(department.getDepartmentOid());
            departmentRoleService.upsertDepartmentRole(departmentDTO.getDepartmentRole());

        }
        DepartmentDTO result = departmentToDepartmentDTO(department);
        String language = OrgInformationUtil.getCurrentLanguage();
//        dataOperationService.save(OrgInformationUtil.getCurrentClientId(), result, result.getDepartmentCode() == null ? messageTranslationService.getMessageDetailByCode(language, DataOperationMessageKey.ADD_DEPARTMENT_ID, result.getId()) : messageTranslationService.getMessageDetailByCode(language, DataOperationMessageKey.ADD_DEPARTMENT_CODE, result.getDepartmentCode()), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
        // dataOperationService.save(OrgInformationUtil.getCurrentClientId(),result,result.getDepartmentCode() == null ? "部门id:"+result.getId() : "部门编码:"+result.getDepartmentCode(), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.ADD.getKey(),tenantId);
//        if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//            DepartmentInfo departmentInfo = DepartmentDTOtoDepartmentInfo(result);
//            esDepartmentIndexSerivce.saveDepartmentInfoIndex(departmentInfo);
//        }
        return result;
//        } else {
//            log.debug("companyOid can not be null");
//            return null;
//        }
    }

    private void checkDepartmentCode(Long tenantId, String departmentCode) {
        PatternMatcherUtil.commonCodeCheckReg(departmentCode);

        // 部门编码长度不能超过100位
        if (departmentCode.length() > 100) {
            throw new BizException(RespCode.DEPARTMENT_CODE_LENGTH_CANNOT_EXCEED_100_DIGITS);
        }
        List<Department> departments = departmentMapper.selectList(
                new EntityWrapper<Department>().eq("department_code", departmentCode).eq("tenant_id", tenantId)
        );
        if (departments.size() > 0) {
            throw new BizException(RespCode.DEPARTMENT_CODE_ALREADY_EXISTS);
        }
    }

    public List<DepartmentDTO> createDepartmentBatch(List<DepartmentDTO> departmentDTOs) {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        for (DepartmentDTO departmentDTO : departmentDTOs) {
            Long tenantId = companyService.getByCompanyOid(departmentDTO.getCompanyOid()).getTenantId();
//            Long tenantId = tenantService.findTenantIdByCompanyOid(departmentDTO.getCompanyOid());
            Department departmentExists = findByPathAndTenantId(departmentDTO.getPath(), tenantId, DepartmentTypeEnum.FIND_ENABLE.getId());
            if (departmentExists != null && DepartmentTypeEnum.ENABLE.getId().equals(departmentExists.getStatus())) {
//                throw new ValidationException(new ValidationError("department", "department.exists"));
                throw new BizException(RespCode.DEPARTMENT_ALREADY_EXISTS);
            }
            departmentDTO.setStatus(DepartmentTypeEnum.ENABLE.getId());
            departmentDTO = this.createDepartmentForImplement(departmentDTO, tenantId);
            departmentDTOList.add(departmentDTO);
        }
//        if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//            departmentDTOList.stream().forEach(u -> {
//                DepartmentInfo departmentInfo = DepartmentDTOtoDepartmentInfo(u);
//                esDepartmentIndexSerivce.saveDepartmentInfoIndex(departmentInfo);
//            });
//
//        }
        return departmentDTOList;
    }

    //按部门路径创建暂时未加tenantId,openapi已弃用
    public List<DepartmentDTO> createDepartmentByList(List<String> departmentPaths, UUID companyOid) {
        Company company = companyService.getByCompanyOidCache(companyOid);
        if (company == null) {
            throw new ObjectNotFoundException(Company.class, companyOid);
        }
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        if (departmentPaths.size() > 0) {
            for (String departmentPath : departmentPaths) {
                if (departmentPath != null && !StringUtils.isEmpty(departmentPath)) {
                    List<Department> departments = companyService.saveDepartmentAndGetAll(departmentPath, company, null, null);
                    for (Department department : departments) {
                        departmentDTOList.add(departmentToDepartmentDTO(department));
                    }
                }
            }
        }
        return departmentDTOList;

    }


    /**
     * Save a department.
     *
     * @return the persisted entity
     */
    @Transactional
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, boolean isControl, Long tenantId) {
//        Company company = companyService.getByCompanyOidCache(departmentDTO.getCompanyOid());
//        if (company == null) {
//            throw new ObjectNotFoundException(Company.class, departmentDTO.getCompanyOid());
//        }
        Department oldDepartment = new Department();
        Department newDepartment = new Department();

        Department department = findByDepartmentOidAndStatus(departmentDTO.getDepartmentOid(), DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (department == null) {
            throw new IllegalArgumentException();
        }
//        String oldName = department.getName();
        Department parentDepartment = findByDepartmentOidAndStatus(departmentDTO.getParentDepartmentOid(), DepartmentTypeEnum.FIND_ALL.getId());
        BeanUtils.copyProperties(department, oldDepartment);
        String oldPath = department.getPath();
        String newPath = (parentDepartment != null) ? parentDepartment.getPath() + Constants.DEPARTMENT_SPLIT + departmentDTO.getName() : departmentDTO.getName();
        if (!departmentDTO.getName().equals(department.getName())) {
            Department checkDepartment = findByPathAndTenantId(newPath, tenantId, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
            if (checkDepartment != null && DepartmentTypeEnum.ENABLE.getId().equals(checkDepartment.getStatus())) {
//                throw new ValidationException(new ValidationError("operation.error", "department already exists of this path :" + newPath + " in company :" + departmentDTO.getCompanyOid().toString()));
                throw new BizException(RespCode.THIS_DEPARTMENT_PATH_ALREADY_EXISTS);
            }
        }
        if (departmentDTO.getManagerOid() != null) {
            department.setManager(contactService.getUserDTOByUserOid(departmentDTO.getManagerOid()));
        } else {
            department.setManager(null);
        }

        List<DepartmentPositionDTO> departmentPositionDTOList = departmentDTO.getDepartmentPositionDTOList();
        if (CollectionUtils.isNotEmpty(departmentPositionDTOList)) {
            Optional<DepartmentPositionDTO> departmentManager = departmentPositionDTOList.stream().
                    filter(departmentPositionDTO -> DepartmentPositionCode.DEPARTMENT_POSITION_MANAGER.equals(departmentPositionDTO.getPositionCode()))
                    .findFirst();
            if (departmentManager.isPresent()) {
                // modify by mh.z 20190305 更新部门的managerId值
                //department.setManager(contactService.getUserDTOByUserOid(departmentManager.get().getUserOid()));
                UUID userOid = departmentManager.get().getUserOid();
                if (userOid != null) {
                    UserDTO userDTO = contactService.getUserDTOByUserOid(userOid);
                    department.setManagerId(userDTO.getId());
                    department.setManager(userDTO);
                } else {
                    department.setManagerId(null);
                    department.setManager(null);
                }
                // END modify by mh.z

//                //部门经理修改添加日志
//                if(isControl){
//                    dataOperationService.save(OrgInformationUtil.getCurrentUserOid(),"修改部门经理"+department.getManager().getContact().getFullName() + "->" + departmentManager.get().getUserName(),
//                        OperationEntityTypeEnum.DEPARTMENT_POSITION.getKey(),OperationTypeEnum.UPDATE.getKey(),tenantId);
//                }else{
//                    dataOperationService.save(OrgInformationUtil.getCurrentClientId(),"修改部门经理"+department.getManager().getContact().getFullName() + "->" + departmentManager.get().getUserName(),
//                        OperationEntityTypeEnum.DEPARTMENT_POSITION.getKey(),OperationTypeEnum.UPDATE.getKey(),tenantId);
//                }
            }
        }

        department.setName(departmentDTO.getName());
        department.setPath(newPath);
        department.setParent(parentDepartment);
        department.setLastUpdatedDate(ZonedDateTime.now());
        Department parent = this.selectOne(new EntityWrapper<Department>().eq("department_oid", departmentDTO.getParentDepartmentOid()));
        if (parent != null) {
            department.setParentId(parent.getId());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(departmentDTO.getDepartmentCode())) {
            if (!departmentDTO.getDepartmentCode().equals(oldDepartment.getDepartmentCode())) {
                String departmentCode = departmentDTO.getDepartmentCode();
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(departmentCode)) {
                    checkDepartmentCode(tenantId, departmentCode);
                    department.setDepartmentCode(departmentCode);
                }
            }
            department.setDepartmentCode(departmentDTO.getDepartmentCode());
        } else {
            department.setDepartmentCode(null);
        }
        this.insertOrUpdate(department);
        BeanUtils.copyProperties(department, newDepartment);
        baseI18nService.insertOrUpdateI18n(departmentDTO.getI18n(), department.getClass(), department.getId());
        //2017-10-26  部门信息更改供应商同步
//        if(OrgInformationUtil.getCurrentCompanyOid()==null){
//            integrationService.syncDepartment(departmentDTO.getCompanyOid(), department);
//        }
//        else{
//            integrationService.syncDepartment(OrgInformationUtil.getCurrentCompanyOid(), department);
//        }
        if (!oldPath.equals(newPath)) {
            departmentMapper.updateChildrenDepartmentPath(newPath, oldPath + "|", oldPath.length() + 1, department.getTenantId());
        }
        DepartmentDTO departmentDTO1 = departmentToDepartmentDTO(department);
        if (departmentDTO.getDepartmentRole() != null) {
            departmentDTO.getDepartmentRole().setDepartmentOid(department.getDepartmentOid());
            departmentDTO1.setDepartmentRole(departmentRoleService.upsertDepartmentRole(departmentDTO.getDepartmentRole()));
        }

        if (CollectionUtils.isNotEmpty(departmentPositionDTOList)) {
            DepartmentRoleDTO departmentRoleDTO = departmentPositionUserService.convert(departmentPositionDTOList, department.getDepartmentOid());
            departmentRoleService.upsertDepartmentRole(departmentRoleDTO);
            departmentPositionUserService.saveOrUpdate(departmentPositionDTOList, department.getId(), OrgInformationUtil.getCurrentTenantId(), isControl);
        }
//        if (isControl) {
//            dataOperationService.save(OrgInformationUtil.getCurrentUserId(), departmentToDepartmentDTO(oldDepartment), departmentToDepartmentDTO(newDepartment), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId, newDepartment.getName());
//        } else {
//            dataOperationService.save(OrgInformationUtil.getCurrentClientId(), departmentToDepartmentDTO(oldDepartment), departmentToDepartmentDTO(newDepartment), OperationEntityTypeEnum.DEPARTMENT.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId, newDepartment.getName());
//        }
//        if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//            DepartmentInfo departmentInfo = DepartmentDTOtoDepartmentInfo(departmentDTO1);
//            esDepartmentIndexSerivce.saveDepartmentInfoIndex(departmentInfo);
//        }
        return departmentDTO1;
    }

    /**
     * 查询根部门1002查启用部门
     *
     * @param companyOid
     * @param flag       1001查全部部門，1002查启用部门
     * @return
     */
    public List<DepartmentDTO> getCompanyRootDepartments(UUID companyOid, int flag) {
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        List<Department> departmentList;
        if (flag == 1001) {
            departmentList = departmentMapper.selectList(
                    new EntityWrapper<Department>()
                            .eq("tenant_id", tenantId)
                            .isNull("parent_id")
                            .ne("status", DepartmentTypeEnum.DELETE.getId())
            );
        } else if (flag == 1002) {
            departmentList = departmentMapper.selectList(
                    new EntityWrapper<Department>()
                            .eq("tenant_id", tenantId)
                            .isNull("parent_id")
                            .eq("status", DepartmentTypeEnum.ENABLE.getId())
            );
        } else {
            departmentList = departmentMapper.selectList(
                    new EntityWrapper<Department>()
                            .eq("tenant_id", tenantId)
                            .isNull("parent_id")
                            .eq("status", DepartmentTypeEnum.DISABLE.getId())
            );
        }
        return departmentList.stream()
                .map(department -> {
                    DepartmentDTO dto = departmentToDepartmentDTO(department);
//                dto.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
                    if (flag == 1002) {
                        if (department.getChildren() != null){
                            dto.setHasChildrenDepartments(department.getChildren().stream().filter(department1 -> department1.getStatus().equals(DepartmentTypeEnum.ENABLE.getId())).collect(Collectors.toList()).size() > 0);
                        }
                    } else {
                        if(department.getChildren() != null) {
                            dto.setHasChildrenDepartments(
                                    department.getChildren().stream().filter(department1 -> !department1.getStatus().equals(DepartmentTypeEnum.DELETE.getId())).collect(Collectors.toList()).size() > 0);

                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DepartmentDTO> getCompanyRootDepartmentsV2(long companyId, Long tenantId, int flag, boolean isCompany) {
        List<DepartmentDTO> departmentDtoList;
        DepartmentQueryDTO departmentQueryDTO = new DepartmentQueryDTO();
        departmentQueryDTO.setCompanyId(companyId);
        departmentQueryDTO.setCompanyScope(isCompany);
        departmentQueryDTO.setTenantId(tenantId);
        if (flag == 1001) {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.ENABLE.getId());
            departmentQueryDTO.setStatusNotEquals(false);
        } else if (flag == 1002) {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.ENABLE.getId());
        } else {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.DISABLE.getId());
        }
        departmentDtoList = departmentMapper.selectRootDepartment(departmentQueryDTO);
        departmentDtoList.stream().forEach(departmentDTO -> {
            Department department = new Department();
            BeanUtils.copyProperties(departmentDTO, department);
            Department i18nDepartment = baseI18nService.convertOneByLocale(department);
            departmentDTO.setName(i18nDepartment.getName());
            departmentDTO.setPath(i18nDepartment.getPath());
        });
        return departmentDtoList;
    }

    public Page<DepartmentDTO> getCompanyRootDepartmentsV2(UUID companyOid, Long tenantId, int flag, Pageable pageable) {
        //pageable 默认 从0-第一页开始，而mybatisplus的page从1开始
        com.baomidou.mybatisplus.plugins.Page page = new com.baomidou.mybatisplus.plugins.Page(pageable.getPageNumber() + 1, pageable.getPageSize());

        List<DepartmentDTO> departmentDtoList;
        DepartmentQueryDTO departmentQueryDTO = new DepartmentQueryDTO();
        departmentQueryDTO.setCompanyOid(companyOid);
        departmentQueryDTO.setTenantId(tenantId);
        if (flag == 1001) {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.DELETE.getId());
            departmentQueryDTO.setStatusNotEquals(true);
        } else if (flag == 1002) {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.ENABLE.getId());
        } else {
            departmentQueryDTO.setStatus(DepartmentTypeEnum.DISABLE.getId());
        }
        departmentDtoList = departmentMapper.selectRootDepartment(page, departmentQueryDTO);
        return new PageImpl<>(departmentDtoList, pageable, page.getTotal());
    }

    /**
     * get one department by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public DepartmentDTO findOne(UUID departmentOid) {
        log.debug("Request to get Department : {}", departmentOid);
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ALL.getId());
        if (department == null) {
            throw new ObjectNotFoundException(Department.class, departmentOid);
        }

        Department departmentI18n = baseI18nService.convertOneByLocale(department);
        department.setName(departmentI18n.getName());
        department.setPath(departmentI18n.getPath());
        DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
//        departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));

        int userSize = contactService.findByDepartmentOid(department.getDepartmentOid()).size();
        departmentDTO.setHasChildrenDepartments(department.getChildren().size() > 0);
        departmentDTO.setHasUsers(userSize > 0);

        Department i18nDepartment = baseI18nService.selectOneBaseTableInfoWithI18n(department.getId(), Department.class);
        departmentDTO.setI18n(i18nDepartment.getI18n());

        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<DepartmentPositionDTO> departmentPositionDTOList = new ArrayList<>();
        List<DepartmentPosition> departmentPositionList = departmentPositionService.listByTenantIdAndEnabled(tenantId, true);
        if (CollectionUtils.isNotEmpty(departmentPositionList)) {
            departmentPositionList = baseI18nService.convertListByLocale(departmentPositionList);
            departmentPositionList.stream().forEach(departmentPosition -> {
                DepartmentPositionDTO departmentPositionDTO = new DepartmentPositionDTO();
                departmentPositionDTO.setId(departmentPosition.getId());
                departmentPositionDTO.setPositionCode(departmentPosition.getPositionCode());
                departmentPositionDTO.setPositionName(departmentPosition.getPositionName());
                departmentPositionDTO.setTenantId(departmentPosition.getTenantId());
                UserDTO user = departmentPositionUserService.getUser(department.getId(), departmentPosition.getId());
                if (user != null) {
                    departmentPositionDTO.setUserName(user.getFullName());
                    departmentPositionDTO.setUserOid(user.getUserOid());
                }

                departmentPositionDTOList.add(departmentPositionDTO);
            });
        }
        departmentDTO.setDepartmentPositionDTOList(departmentPositionDTOList);
        return departmentDTO;
    }


    /**
     * get department by currentUser who is manager
     */
    public List<Department> getDepartmentManagedByMe() {

        List<Department> departments = departmentMapper.selectList(
                new EntityWrapper<Department>()
                        .eq("manager_id", OrgInformationUtil.getCurrentUserId())
                        .eq("status", 101)
        );
//        departmentRepository.findByManagerIsCurrentUser();
        departments = baseI18nService.convertListByLocale(departments);
        return departments;
    }


    public List<DepartmentDTO> getChildrenDepartment(UUID parentDepartmentOid, int flag, boolean isCompany) {
        List<Department> departments;
        if (flag == 1001) {
            departments = departmentMapper.selectByParentDepartmentOidAndStatusNot(parentDepartmentOid, DepartmentTypeEnum.DELETE.getId());
        } else if (flag == 1002) {
            departments = departmentMapper.selectByParentDepartmentOidAndStatus(parentDepartmentOid, DepartmentTypeEnum.ENABLE.getId());
        } else {
            departments = departmentMapper.selectByParentDepartmentOidAndStatus(parentDepartmentOid, DepartmentTypeEnum.DISABLE.getId());
        }
        departments = baseI18nService.convertListByLocale(departments);
        return departments.stream().map(
                department -> {
                    DepartmentDTO dto = departmentToDepartmentDTO(department);
//                dto.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
                    int userSize = 0;
                    if (isCompany) {
                        com.baomidou.mybatisplus.plugins.Page page = new com.baomidou.mybatisplus.plugins.Page(0, Integer.MAX_VALUE);
                        userSize = contactService.findByDepartmentOidAndCompanyId(dto.getDepartmentOid(), OrgInformationUtil.getCurrentCompanyId(), page).size();
                    } else {
                        userSize = contactService.findByDepartmentOid(dto.getDepartmentOid()).size();
                    }
                    dto.setHasUsers(userSize > 0);
                    dto.setI18n(department.getI18n());
                    if (flag == 1002) {
                        dto.setHasChildrenDepartments(department.getChildren().stream().filter(department1 -> department1.getStatus().equals(DepartmentTypeEnum.ENABLE.getId())).collect(Collectors.toList()).size() > 0);
                    } else {
                        dto.setHasChildrenDepartments(department.getChildren().stream().filter(department1 -> !department1.getStatus().equals(DepartmentTypeEnum.DELETE.getId())).collect(Collectors.toList()).size() > 0);
                    }
                    return dto;
                }
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Department> searchDepartment(String keyword, Pageable pageable) {
        com.baomidou.mybatisplus.plugins.Page page = PageUtil.getPage(pageable);
        List<Department> departments = departmentMapper.selectPage(page,
                new EntityWrapper<Department>()
                        .like("name", keyword)
                        .eq("status", 101)
        );
        List<Department> departmentList = baseI18nService.convertListByLocale(departments);
        return new PageImpl<>(departmentList, pageable, page.getTotal());
    }

    public void delete(UUID departmentOid, UUID currentUserOid) throws Exception {
        UserDTO user = contactService.getUserDTOByUserOid(currentUserOid);
        if (user == null || user.getCompanyId() == null) {
//            throw new ValidationException(new ValidationError("user.not.listDTOByQO", "user not listDTOByQO"));
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (department == null) {
            throw new ObjectNotFoundException(Department.class, departmentOid);
        }
        if (department.getTenantId() == null || !user.getTenantId().equals(department.getTenantId())) {
            throw new ValidationException(new ValidationError("department.not.Valid", "department.not.Valid"));
        }
        List<Department> child = department.getChildren();
        if (CollectionUtils.isNotEmpty(child)) {
            for (Department childDepartment : child) {
                //禁用下父部门仍然能删除
                if (DepartmentTypeEnum.ENABLE.getId().equals(childDepartment.getStatus())) {
                    //if(childDepartment.getStatus()!=DepartmentTypeEnum.DELETE.getId() ){
//                    throw new ValidationException(new ValidationError("department.children", "has children"));
                    throw new BizException(RespCode.DEPARTMENT_HAS_SUB_DEPARTMENTS);
                }
            }
        }
//        Set<User> users = department.getUsers();
        Set<UserDTO> users = departmentUserService.findUsersByDepartmentId(department.getId());
        if (CollectionUtils.isEmpty(users)) {//部门下没有用户直接删除
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DELETE.getId());
            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
            //2017-10-26  部门信息更改供应商同步
//            integrationService.syncDepartment(OrgInformationUtil.getCurrentCompanyOid(), department);
        } else {
            long count = users.stream().filter(u ->
                    EmployeeStatusEnum.parse(Integer.valueOf(u.getStatus())) != EmployeeStatusEnum.LEAVED
            ).count();
            if (count > 0) {//部门下有未删除用户
//                throw new ValidationException(new ValidationError("department.has.user", "department.has.user"));
                throw new BizException(RespCode.DEPARTMENT_HAS_EMPLOYEES);
            }
//            departmentDAO.unassociateUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId());
            departmentUserService.removeUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId(), currentUserOid.toString());
            departmentRoleService.deleteDepartmentRole(department.getId());
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DELETE.getId());
            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
            //2017-10-26  部门信息更改供应商同步
//            integrationService.syncDepartment(OrgInformationUtil.getCurrentCompanyOid(), department);


        }
//        if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//            esDepartmentIndexSerivce.deleteDepartmentInfoIndex(department.getId());
//        }
    }

    public void deleteDepartment(UUID departmentOid, UUID companyOid, String currentUserOid) throws Exception {
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (department == null) {
            throw new ObjectNotFoundException(Department.class, departmentOid);
        }
        Company company = companyService.getByCompanyOidCache(companyOid);
        if (company == null) {
            throw new ObjectNotFoundException(Company.class, companyOid);
        }
        if (!department.getTenantId().equals(company.getTenantId())) {
//            throw new ValidationException(new ValidationError("operation.error", "Department:" + departmentOid.toString() + " does not exist in Company :" + companyOid.toString()));
            throw new BizException(RespCode.THIS_DEPARTMENT_NOT_EXIST);
        }
        List<Department> child = department.getChildren();
        if (CollectionUtils.isNotEmpty(child)) {
            for (Department childDepartment : child) {
                //禁用下父部门仍然能删除
                if (DepartmentTypeEnum.ENABLE.getId().equals(childDepartment.getStatus())) {
                    // if(childDepartment.getStatus()!=DepartmentTypeEnum.DELETE.getId()){
//                    throw new ValidationException(new ValidationError("department.children", "has children"));
                    throw new BizException(RespCode.DEPARTMENT_HAS_SUB_DEPARTMENTS);
                }
            }
        }
//        Set<User> users = department.getUsers();
        Set<UserDTO> users = departmentUserService.findUsersByDepartmentId(department.getId());
        if (CollectionUtils.isEmpty(users)) {//部门下没有用户直接删除
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DELETE.getId());
            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
//            if(companyOid != null){
//                integrationService.syncDepartment(companyOid,department);
//            }
        } else {
            long count = users.stream().filter(u ->
                    EmployeeStatusEnum.parse(Integer.valueOf(u.getStatus())) != EmployeeStatusEnum.LEAVED
            ).count();
            if (count > 0) {//部门下有未删除用户
//                throw new ValidationException(new ValidationError("department.has.user", "department.has.user"));
                throw new BizException(RespCode.DEPARTMENT_HAS_EMPLOYEES);
            }
//            departmentDAO.unassociateUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId());
            departmentUserService.removeUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId(), currentUserOid);
            departmentRoleService.deleteDepartmentRole(department.getId());
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DELETE.getId());
            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
//            if(companyOid != null) {
//                integrationService.syncDepartment(companyOid, department);
//            }
            //           es信息同步
//            if (esDepartmentIndexSerivce.isElasticSearchEnable()) {
//                // DepartmentInfo departmentInfo = DepartmentDTOtoDepartmentInfo(departmentDTO1);
//                esDepartmentIndexSerivce.deleteDepartmentInfoIndex(department.getId());
//            }
        }

    }

    public void deleteByDepartmentOids(List<UUID> departmentOids, UUID companyOid, String currentUserOid) throws Exception {
        for (UUID departmentOid : departmentOids) {

            try {
                this.deleteDepartment(departmentOid, companyOid, currentUserOid);
            } catch (ObjectNotFoundException e) {
                //与全喜讨论 此处错误可以忽略
                log.error("Department not found", e.getMessage());
            }
        }
    }

    public List<DepartmentDTO> correctDepartments(List<String> departmentOids) {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        List<UUID> departmentOidList = new ArrayList<>();
        for (String str : departmentOids) {
            UUID departmentOid = UUID.fromString(str);
            departmentOidList.add(departmentOid);
        }
        List<Department> departmentList = findByDepartmentOidIn(departmentOidList, DepartmentTypeEnum.FIND_ALL.getId());
        for (Department department : departmentList) {
            Department parentDepartment = findByDepartmentOidAndStatus(department.getParent().getDepartmentOid(), DepartmentTypeEnum.FIND_ALL.getId());
            department.setPath(parentDepartment.getPath() + Constants.DEPARTMENT_SPLIT + department.getName());
            department.setLastUpdatedDate(ZonedDateTime.now());
            this.insertOrUpdate(department);
            DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
            departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
            departmentDTO.setI18n(department.getI18n());
            departmentDTOList.add(departmentDTO);
        }
        return departmentDTOList;
    }

    public List<DepartmentDTO> findDepartmentByOids(List<UUID> departmentOids) {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        List<Department> departmentList = findByDepartmentOidIn(departmentOids, DepartmentTypeEnum.FIND_ALL.getId());
        for (Department department : departmentList) {
            DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
            departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
            int userSize = contactService.findByDepartmentOid(department.getDepartmentOid()).size();
            departmentDTO.setHasChildrenDepartments(department.getChildren().size() > 0);
            departmentDTO.setHasUsers(userSize > 0);
            departmentDTO.setI18n(department.getI18n());
            departmentDTOList.add(departmentDTO);
        }
        return departmentDTOList;
    }

    public List<DepartmentDTO> findDepartmentByOidsSimple(List<UUID> departmentOids) {
        List<Department> departments = departmentMapper.selectByDepartmentOidInAndStatusSimple(departmentOids, null);
        return departments.stream().map(u -> departmentToDepartmentDTO(u)).collect(Collectors.toList());
    }


    public Page<DepartmentDTO> findByCompanyOid(UUID companyOid, Pageable pageable) {
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        Page<Department> page = this.selectByTenantIdAndStatusNot(tenantId, pageable, DepartmentTypeEnum.DELETE.getId());
        List<Department> departmentList = baseI18nService.convertListByLocale(page.getContent());
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        if (page.getContent() != null && page.getContent().size() > 0) {
            for (Department department : departmentList) {
                DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
                departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
                int userSize = contactService.findByDepartmentOid(department.getDepartmentOid()).size();
                departmentDTO.setHasChildrenDepartments(department.getChildren().size() > 0);
                departmentDTO.setHasUsers(userSize > 0);
                departmentDTO.setI18n(department.getI18n());
                departmentDTOList.add(departmentDTO);
            }
        }
        return new PageImpl<DepartmentDTO>(departmentDTOList, pageable, page.getTotalElements());
    }

    public List<Department> findDepartmentByIds(List<Long> departmentIds) {
        List<Department> departments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(departmentIds)) {
            departmentIds.forEach(
                    id -> {
                        Department one = selectById(id);
                        departments.add(one);
                    }
            );
        }
        return departments;
    }

    /*
     *  根据部门全路径和公司名称 获取该路径上的所有部门信息
     * */
    public List<DepartmentDTO> getDepartmentsByPathAndTenantId(String departmentPath, Long tenantId) {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        List<DepartmentDTO> parentDepartments = null;
        Department department = findByPathAndTenantId(departmentPath, tenantId, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        department = baseI18nService.convertOneByLocale(department);
        DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
        departmentDTO.setI18n(department.getI18n());
        departmentDTOList.add(departmentDTO);
        if (departmentPath.contains(Constants.DEPARTMENT_SPLIT)) {
            String parentPath = departmentPath.substring(0, departmentPath.lastIndexOf(Constants.DEPARTMENT_SPLIT));
            parentDepartments = getDepartmentsByPathAndTenantId(parentPath, tenantId);
            departmentDTOList.addAll(parentDepartments);
        }
        return departmentDTOList;
    }

//    public List<DepartmentDTO> searchByName(UUID companyOid, String name, int flag) {
//        return searchByName(companyOid, name, flag, true);
//    }

    /**
     * 模糊查询部门
     *
     * @param name
     * @param companyOid
     * @return
     */
    public List<DepartmentDTO> searchByName(UUID companyOid, String name, int flag, boolean hasChildren) {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        List<Department> departmentList;
        if (flag == 1001) {
            departmentList = departmentMapper.selectList(
                    new EntityWrapper<Department>()
                            .eq("tenant_id", tenantId)
                            .ne("status", DepartmentTypeEnum.DELETE.getId())
                            .like("name", name)
            );
        } else {
//            departmentList = departmentRepository.findByTenantIdAndNameLikeAndStatus(tenantId,"%"+name+"%",DepartmentTypeEnum.ENABLE.getId());
            departmentList = departmentMapper.selectList(
                    new EntityWrapper<Department>()
                            .eq("tenant_id", tenantId)
                            .eq("status", DepartmentTypeEnum.ENABLE.getId())
                            .like("name", name)
            );
        }
        departmentList = baseI18nService.convertListByLocale(departmentList);
        for (Department department : departmentList) {
            DepartmentDTO departmentDTO = departmentToDepartmentDTO(department);
            departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
            int userSize = contactService.findByDepartmentOid(department.getDepartmentOid()).size();
            if (hasChildren && CollectionUtils.isNotEmpty(department.getChildren())) {
                if (department.getChildren().stream().filter(d -> d.getStatus().equals(DepartmentTypeEnum.ENABLE.getId())).count() > 0) {
                    departmentDTO.setHasChildrenDepartments(true);
                }
            } else {
                departmentDTO.setHasChildrenDepartments(false);
            }
            departmentDTO.setHasUsers(userSize > 0);
            departmentDTO.setI18n(department.getI18n());
            departmentDTOList.add(departmentDTO);
        }
        return departmentDTOList;
    }

    public void departmentEnable(UUID departmentOid, UUID currentUserOid, UUID companyOid, Long tenantId) {
        UserDTO user = contactService.getUserDTOByUserOid(currentUserOid);
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (user == null || user.getCompanyId() == null) {
//            throw new ValidationException(new ValidationError("user.not.listDTOByQO", "user not listDTOByQO"));
            throw new BizException(RespCode.USER_NOT_EXIST);
        } else if (department == null) {
            throw new ObjectNotFoundException(Department.class, departmentOid);
        } else if (department.getTenantId() == null || !user.getTenantId().equals(department.getTenantId())) {
            throw new ValidationException(new ValidationError("department.not.Valid", "department not Valid"));
        } else if (findByPathAndTenantId(department.getPath(), tenantId, DepartmentTypeEnum.FIND_ENABLE.getId()) != null) {
//            throw new ValidationException(new ValidationError("department.path.exist", "department path exist"));
            throw new BizException(RespCode.THIS_DEPARTMENT_PATH_ALREADY_EXISTS);
        } else {
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.ENABLE.getId());
            this.insertOrUpdate(department);
        }
//        if(companyOid != null) {
//            integrationService.syncDepartment(companyOid, department);
//        }
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.ENABLE_DEPARTMENT, department.getName()), OperationEntityTypeEnum.DEPARTMENT.getKey(),
//                OperationTypeEnum.ENABLE.getKey(), tenantId);
    }

    public void departmentDisable(UUID departmentOid, UUID currentUserOid) {
        UserDTO user = contactService.getUserDTOByUserOid(currentUserOid);
        if (user == null || user.getCompanyId() == null) {
//            throw new ValidationException(new ValidationError("user.not.listDTOByQO", "user not listDTOByQO"));
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (department == null) {
            throw new ObjectNotFoundException(Department.class, departmentOid);
        }
        if (department.getTenantId() == null || !user.getTenantId().equals(department.getTenantId())) {
            throw new ValidationException(new ValidationError("department.not.Valid", "department.not.Valid"));
        }
        List<Department> child = department.getChildren();
        for (Department childDepartment : child) {
            if (DepartmentTypeEnum.ENABLE.getId().equals(childDepartment.getStatus())) {
//                throw new ValidationException(new ValidationError("children.not.disable", "children not disable"));
                throw new BizException(RespCode.DEPARTMENT_HAS_SUB_DEPARTMENTS);
            } else {
//                Set<User> users = childDepartment.getUsers();
                Set<UserDTO> users = departmentUserService.findUsersByDepartmentId(childDepartment.getId());
                if (!CollectionUtils.isEmpty(users)) {
//                    throw new ValidationException(new ValidationError("department.has.user", "department.has.user"));
                    throw new BizException(RespCode.DEPARTMENT_HAS_EMPLOYEES);
                }
            }
        }
//        Set<User> users = department.getUsers();
        Set<UserDTO> users = departmentUserService.findUsersByDepartmentId(department.getId());
        if (CollectionUtils.isEmpty(users)) {
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DISABLE.getId());
//            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
        } else {
            long count = users.stream().filter(u ->
                    //部门员工表有，但员工表么有，set集合会存在空值，作如下处理
                    EmployeeStatusEnum.parse(Integer.valueOf(u == null ? EmployeeStatusEnum.LEAVED.getId() : u.getStatus())) != EmployeeStatusEnum.LEAVED
            ).count();
            if (count > 0) {
//                throw new ValidationException(new ValidationError("department.has.user", "department.has.user"));
                throw new BizException(RespCode.DEPARTMENT_HAS_EMPLOYEES);
            }
//            departmentDAO.unassociateUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId());
            departmentUserService.removeUserDepartment(users.stream().map(u -> u.getId()).collect(Collectors.toList()), department.getId(), currentUserOid.toString());
            departmentRoleService.deleteDepartmentRole(department.getId());
            department.setLastUpdatedDate(ZonedDateTime.now());
            department.setStatus(DepartmentTypeEnum.DISABLE.getId());
//            department.setDepartmentCode(null);
            this.insertOrUpdate(department);
        }
//        integrationService.syncDepartment(OrgInformationUtil.getCurrentCompanyOid(), department);
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.DISABLE_DEPARTMENT, department.getName()), OperationEntityTypeEnum.DEPARTMENT.getKey(),
//                OperationTypeEnum.DISABLE.getKey(), department.getTenantId());
    }


    public Department findByDepartmentOidAndStatus(UUID departmentOid, int flag) {
        Department department;
        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
            department = departmentMapper.selectByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.ENABLE.getId());
        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
            department = departmentMapper.selectByDepartmentOidAndStatusNot(departmentOid, DepartmentTypeEnum.DELETE.getId());
        } else {
            department = departmentMapper.selectByDepartmentOidAndStatus(departmentOid, null);
        }
        return department;
    }

    public List<Department> findByDepartmentOidIn(List<UUID> departmentOids, int flag) {
        List<Department> departmentList;
        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatus(departmentOids, DepartmentTypeEnum.ENABLE.getId());
        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatusNot(departmentOids, DepartmentTypeEnum.DELETE.getId());
        } else {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatus(departmentOids, null);
        }
        departmentList = baseI18nService.convertListByLocale(departmentList);
        return departmentList;
    }


    public List<Department> findByDepartmentOidIn(Set<UUID> departmentOids, int flag) {
        List<UUID> departmentOidList = departmentOids.stream().collect(Collectors.toList());
        List<Department> departmentList;
        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatus(departmentOidList, DepartmentTypeEnum.ENABLE.getId());
        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatusNot(departmentOidList, DepartmentTypeEnum.DELETE.getId());
        } else {
            departmentList = departmentMapper.selectByDepartmentOidInAndStatus(departmentOidList, null);
        }
        return departmentList;
    }

    public Set<UUID> findOidByDepartmentOids(Long companyId, Set<UUID> departmentOids) {
        List<UUID> departmentOidList;
        departmentOidList = departmentMapper.findOidByDepartmentOids(companyId, departmentOids.stream().collect(Collectors.toList()));
        return departmentOidList.stream().collect(Collectors.toSet());
    }

    //使用mybatis重构方法替代，这个迭代删除
//    @Deprecated
//    public List<Department> findByPathLike(String path, int flag) {
//        List<Department> departmentList;
//        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
//            departmentList = departmentRepository.findByPathLikeAndStatus(path, DepartmentTypeEnum.ENABLE.getId());
//        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
//            departmentList = departmentRepository.findByPathLikeAndStatusNot(path, DepartmentTypeEnum.DELETE.getId());
//        } else {
//            departmentList = departmentRepository.findByPathLike(path);
//        }
//        departmentList = baseI18nService.convertListByLocale(departmentList);
//        return departmentList;
//    }

    public Department findByPathAndCompanyCompanyOid(String path, UUID companyOid, int flag) {
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        Department department;
        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
            department = departmentMapper.selectByPathAndTenantIdAndStatus(path, tenantId, DepartmentTypeEnum.ENABLE.getId());
        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
            department = departmentMapper.selectByPathAndTenantIdAndStatusNot(path, tenantId, DepartmentTypeEnum.DELETE.getId());
        } else {
            department = departmentMapper.selectByPathAndTenantIdAndStatus(path, tenantId, null);
        }
        if (department != null) {
            department = baseI18nService.convertOneByLocale(department);
        }
        return department;
    }

    public Department findByPathAndTenantId(String path, Long tenantId, int flag) {
        Department department;
        if (flag == DepartmentTypeEnum.FIND_ENABLE.getId()) {
            department = departmentMapper.selectByPathAndTenantIdAndStatus(path, tenantId, DepartmentTypeEnum.ENABLE.getId());
        } else if (flag == DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId()) {
            department = departmentMapper.selectByPathAndTenantIdAndStatusNot(path, tenantId, DepartmentTypeEnum.DELETE.getId());
        } else {
            department = departmentMapper.selectByPathAndTenantIdAndStatus(path, tenantId, null);
        }
        if (department != null) {
            department = baseI18nService.convertOneByLocale(department);
        }
        return department;
    }

    public List<Department> findByCompanyCompanyOidAndStatusAndPathIn(UUID companyOid, Integer status, List<String> paths) {
        Company company = companyService.getByCompanyOidCache(companyOid);
        return departmentMapper.selectList(
                new EntityWrapper<Department>()
                        .eq("status", status)
                        .eq("company_id", company.getId())
                        .in("path", paths)
        );
    }

    //查询财务角色所拥有的部门根部门
    public List<DepartmentDTO> getFinanceRoleRootDepartment(UUID financeRoleOid, Long tenantId) {
        if (financeRoleOid == null) {
            return null;
        }
//        FinanceRole financeRole = financeRoleRepository.findByTenantIdAndRoleOid(tenantId, financeRoleOid);
//        if (financeRole == null) {
//            return null;
//        }
        List<Department> rootDepartmentList = new ArrayList<>();
        List<Department> childDepartmentList;
//        Set<Department> departments = financeRole.getDepartments();
//        if (CollectionUtils.isNotEmpty(departments)) {
//            rootDepartmentList = departments.stream().filter(v -> v.getParent() == null).collect(Collectors.toList());
//        }
//        //角色下没有root部门,找下一级部门当做root部门
//        if (CollectionUtils.isNotEmpty(departments)) {
//            childDepartmentList = departments.stream().filter(v -> v.getParent() != null).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(childDepartmentList)) {
//                for (Department v : childDepartmentList) {
//                    findParentDepartment(v, rootDepartmentList);
//                }
//            }
//        }
        Collections.sort(rootDepartmentList, new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                return o1.getId() > o2.getId() ? 1 : -1;
            }
        });
        return rootDepartmentList.stream()
                .map(department -> {
                    DepartmentDTO dto = departmentToDepartmentDTO(department);
                    //dto.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
                    dto.setHasChildrenDepartments(department.getChildren().stream().collect(Collectors.toList()).size() > 0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //获取财务角色的子部门
    public List<DepartmentDTO> getFinanceRoleChildDepartment(UUID parentDepartmentOid, UUID financeRoleOid, Long tenantId) {
//        FinanceRole financeRole = financeRoleRepository.findByTenantIdAndRoleOid(tenantId, financeRoleOid);
        List<Department> departments = departmentMapper.selectByParentDepartmentOidAndStatusNot(parentDepartmentOid, DepartmentTypeEnum.DELETE.getId());//父部门下所有的子部门，包括禁用删除的
        List<Department> childDepartments = new ArrayList<>();
//        Set<Department> financeDepartment = financeRole.getDepartments();
//        if (CollectionUtils.isNotEmpty(financeDepartment)) {
//            childDepartments = departments.stream().filter(v -> financeDepartment.contains(v)).collect(Collectors.toList());
//        }
//        if (CollectionUtils.isNotEmpty(financeDepartment)) {
//            for (Department v : departments) {
//                findChildDepartment(v, childDepartments, financeDepartment);
//            }
//        }
        Collections.sort(childDepartments, new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                return o1.getId() > o2.getId() ? 1 : -1;
            }
        });
        return childDepartments.stream().map(v -> {
            DepartmentDTO dto = departmentToDepartmentDTO(v);
            return dto;
        }).collect(Collectors.toList());
    }

    public boolean findParentDepartment(Department department, List<Department> rootDepartmentList) {
        boolean flag = true;
        while (flag) {
            Department result = department.getParent();
            if (result.getParent() == null) {
                if (!rootDepartmentList.contains(result)) {
                    rootDepartmentList.add(result);
                }
                flag = false;
            } else {
                flag = findParentDepartment(result, rootDepartmentList);
            }
        }
        return flag;
    }

    public boolean findChildDepartment(Department department, List<Department> childDepartments, Set<Department> financeDepartment) {
        boolean flag = true;
        while (flag) {
            List<Department> departments = departmentMapper.selectByParentDepartmentOidAndStatusNot(department.getDepartmentOid(), DepartmentTypeEnum.DELETE.getId());//父部门下所有的子部门，包括禁用删除的
            List<Department> result = departments.stream().filter(v -> financeDepartment.contains(v)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(result)) {
                if (!childDepartments.contains(department)) {
                    childDepartments.add(department);
                }
                flag = false;
            } else if (CollectionUtils.isNotEmpty(departments)) {
                for (Department v : departments) {
                    flag = findChildDepartment(v, childDepartments, financeDepartment);
                }
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 根据部门Oid集合查询部门名称
     *
     * @param departmentOids：部门Oid集合
     * @return map key：部门Oid value：部门名称
     */
    public Map<UUID, String> findNameByDepartmentOids(List<UUID> departmentOids) {
        Map<UUID, String> result = new HashMap<>();
        List<Department> departments = selectList(
                new EntityWrapper<Department>()
                        .in("department_oid", departmentOids)
        );
        result = departments.stream().collect(Collectors.toMap(Department::getDepartmentOid, Department::getName));
        return result;
    }

    public Department findOneByDepartmentOid(UUID departmentOid) {
        return departmentMapper.selectByDepartmentOidAndStatus(departmentOid, null);
    }

    /**
     * 查询部门基建信息
     *
     * @param departmentOid
     * @return
     */
    public Department getByDepartmentOid(UUID departmentOid) {
        return selectOne(new EntityWrapper<Department>().eq("department_oid",departmentOid));
    }

    public List<Department> findByCompanyCompanyOidAndPathLikeAndStatus(UUID companyOid, String path, Integer flag) {
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        return this.selectByTenantIdAndPathLikeAndStatus(tenantId, path, flag);
    }

    /**
     * 判断部门是否在这个公司
     *
     * @param departmentOid
     * @param companyOid
     * @return
     */
    public Boolean findByDepartmentOidAndCompanyOid(UUID departmentOid, UUID companyOid) {
        Long tenantId = companyService.getByCompanyOid(companyOid).getTenantId();
        Department department = this.selectByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.ENABLE.getId());
        if (department.getTenantId() != null && department.getTenantId().equals(tenantId)) {
            return true;
        }
        return false;
    }


    /**
     * 根据部门id获取部门
     *
     * @param departmentId：部门id
     * @return
     */
    public Department findOneByDepartmentId(Long departmentId) {
        return departmentMapper.selectById(departmentId);
    }

    /**
     * 在当前所有部门中获取根部门列表，
     * 为构建部门树提供递归开始节点
     *
     * @param deps
     * @return
     */
    public List<DepartmentTreeDTO> getRootDepartments(List<DepartmentTreeDTO> deps) {
        Optional<DepartmentTreeDTO> minPathDep = deps.stream().min(new Comparator<DepartmentTreeDTO>() {
            @Override
            public int compare(DepartmentTreeDTO o1, DepartmentTreeDTO o2) {
                return o1.getPathDepth() - o2.getPathDepth();
            }
        });
        if (minPathDep.isPresent()) {
            DepartmentTreeDTO root = minPathDep.get();
            return deps.stream().filter(u -> u.getPathDepth().equals(root.getPathDepth())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 递归构建部门树
     *
     * @param deps
     * @param node
     */
    public void tree(List<DepartmentTreeDTO> deps, DepartmentTreeDTO node) {
        List<DepartmentTreeDTO> childList = getChildren(deps, node.getId());
        if (CollectionUtils.isNotEmpty(childList)) {
            node.setChildrenDepartment(childList);
        }
        for (DepartmentTreeDTO child : childList) {
            tree(deps, child);
        }
    }

    ;


    /**
     * 获取子部门
     *
     * @param deps
     * @param currentDepartemntId
     * @return
     */
    public List<DepartmentTreeDTO> getChildren(List<DepartmentTreeDTO> deps, Long currentDepartemntId) {
        List<DepartmentTreeDTO> children = new ArrayList<>();
        for (DepartmentTreeDTO dep : deps) {
            if (dep.getParentId() != null && dep.getParentId().equals(currentDepartemntId)) {
                children.add(dep);
            }
        }
        return children;
    }

    /**
     * 查询财务可选的部门
     *
     * @param userOid
     * @param tenantId
     * @param keyword
     * @return
     */
    public List<DepartmentTreeDTO> getDepartmentTreeByFinanceRole(UUID userOid, Long tenantId, String language, String keyword) {
        List<DepartmentTreeDTO> allDeps = departmentMapper.selectFinanceRoleAvailableDepartments(userOid, tenantId, keyword, language);
        allDeps.stream().forEach(u -> {
            u.setAvailable(true);
        });
        return getDepartmentTreeFromLeaf(allDeps);
    }


    /**
     * @param allDeps
     * @return
     */
    public List<DepartmentTreeDTO> getDepartmentTreeFromLeaf(List<DepartmentTreeDTO> allDeps) {
        //获取递归开始节点
        allDeps.parallelStream().forEach(u -> {
            if (u.getParentId() == null) {
                u.setRootFlag(true);
            } else {
                DepartmentTreeDTO parent = getParentNode(allDeps, u);
                if (parent == null) {
                    u.setRootFlag(true);
                } else {
                    parent.getChildrenDepartment().add(u);
                }
            }

        });
        return allDeps.stream().filter(u -> u.isRootFlag()).collect(Collectors.toList());
    }

    /**
     * 获取上级部门
     *
     * @param deps
     * @param curentDep
     * @return
     */
    public DepartmentTreeDTO getParentNode(List<DepartmentTreeDTO> deps, DepartmentTreeDTO curentDep) {
        for (DepartmentTreeDTO dep : deps) {
            if (dep.getId().equals(curentDep.getParentId())) {
                return dep;
            }
        }
        return null;
    }

    public List<DepartmentTreeDTO> getTenantDepartmentAll(String code, String name, Long currentTenantID, Integer status, String language) {
        List<DepartmentTreeDTO> departmentTrees = departmentMapper.findTenantAllDepartment(code, name, currentTenantID, status, language);
        return departmentTrees;
    }


    public List<DepartmentTreeDTO> getTenantDepartmentAllKeyWords(String code, String name, Long currentTenantID, Integer status, String language) {
        return departmentMapper.findTenantAllDepartmentKeyWords(code, name, currentTenantID, status, language);
    }
    //------------------------------------ department mybatis refactor begin---------------------------------------//

    public Department selectByDepartmentOid(UUID departmentOid) {
        Department department = departmentMapper.selectByDepartmentOidAndStatus(departmentOid, null);
        if (department != null) {
            department = baseI18nService.convertOneByLocale(department);
        }
        return department;
    }

    public Department selectOnebyId(Long departmentId) {
        Department department = departmentMapper.selectOneSimpleById(departmentId);
        if (department != null) {
            department = baseI18nService.convertOneByLocale(department);
        }
        return department;
    }

    /**
     * 根据第一个参数和第二个状态查询结果集，状态可不传，默认按第一个参数查询，以下所有方法通用（注：xxxStatusNot 方法状态必传）
     *
     * @param departmentOid
     * @param flag
     * @return
     */
    public Department selectByDepartmentOidAndStatus(UUID departmentOid, Integer flag) {
        Department department = departmentMapper.selectByDepartmentOidAndStatus(departmentOid, flag);
        return department;
    }


    public List<Department> selectByTenantIdAndPathLikeAndStatus(Long tenantId, String path, Integer flag) {
        return departmentMapper.selectByTenantIdAndPathLikeAndStatus(tenantId, path, flag);
    }

    public Long countByTenantId(Long tenantId) {
        return departmentMapper.selectTenantDepartmentCount(tenantId);
    }

    public Page<Department> selectByTenantIdAndStatusNot(Long tenantId, Pageable pageable, Integer status) {
        com.baomidou.mybatisplus.plugins.Page myBatisPage = PageUtil.getPage(pageable);
        List<Department> list = departmentMapper.selectByTenantIdAndStatusNotPage(tenantId, status, myBatisPage);
        Page page = new PageImpl(list, pageable, myBatisPage.getTotal());
        return page;
    }

    public List<Department> selectByTenantIdAndStatus(Long tenantId, Integer status) {
        List<Department> departments = departmentMapper.selectByTenantIdAndStatus(tenantId, status);
        return departments;
    }

    public List<Department> selectByTenantIdAndStatusNot(Long tenantId, Integer status) {
        List<Department> departments = departmentMapper.selectByTenantIdAndStatusNot(tenantId, status);
        return departments;
    }

    //------------------------------------ department mybatis refactor end---------------------------------------//


    public Boolean checkIfChildDepartments(List<Long> parentIds, Long childrenId) {
        Department childDepartment = this.selectOnebyId(childrenId);
        String childPath = childDepartment.getPath();
        return parentIds.stream().anyMatch(p -> {
            Department parentDepartment = this.selectOnebyId(p);
            return this.checkIfChildDepartment(parentDepartment.getPath(), childPath);
        });

    }

    public Boolean checkIfChildDepartment(String parentPath, String childPath) {
//        Boolean flag = Boolean.TRUE;
//        if (childPath.equals(parentPath)) {
//            return Boolean.TRUE;
//        } else if (childPath.length() < parentPath.length()) {
//            return Boolean.FALSE;
//        } else {
//            int i = childPath.lastIndexOf(PaymentConstants.DEPARTMENT_SPLIT);
//            if (i == -1) {
//                return Boolean.FALSE;
//            } else {
//                flag = this.checkIfChildDepartment(parentPath, childPath.substring(0, i));
//            }
//        }
//        return flag;
        if (childPath.equals(parentPath)) {
            return Boolean.TRUE;
        }
        int childLevel = org.apache.commons.lang3.StringUtils.countMatches(childPath, Constants.DEPARTMENT_SPLIT);
        int parentLevel = org.apache.commons.lang3.StringUtils.countMatches(parentPath, Constants.DEPARTMENT_SPLIT);
        return childPath.startsWith(parentPath) && (childLevel != parentLevel);
    }

//    public List<DepartmentPersonDTO> getDepartmentEmployees(String departmentPath) {
//        Long tenantId = OrgInformationUtil.getCurrentTenantId();
//        List<DepartmentPersonDTO> departmentEmployees = esUserInfoSerivce.getDepartmentEmployees(departmentPath);
//        for (DepartmentPersonDTO departmentPersonDTO : departmentEmployees) {
//            Department department = selectByPathAndTenantIdAndStatus(departmentPersonDTO.getPath(), tenantId, DepartmentTypeEnum.ENABLE.getId());
//            departmentPersonDTO.setDepartmentOid(department == null ? null : department.getDepartmentOid());
//        }
//        return departmentEmployees;
//    }

    public void initDepartmentCode() {
        List<DepartmentDTO> allDepartments = departmentMapper.selectDepartmentCode(OrgInformationUtil.getCurrentTenantId());
        allDepartments.parallelStream().forEach(dp -> dp.setDepartmentCode(null));
        //一级部门编码
        List<DepartmentDTO> departments = allDepartments.parallelStream().filter(dp -> StringUtils.isEmpty(dp.getParentDepartmentId())).collect(Collectors.toList());
        Multimap<Long, DepartmentDTO> fatherMultimap = ArrayListMultimap.create();
        departments.forEach(department -> {
            Long tenantId = department.getTenantId();
            int size = fatherMultimap.get(tenantId).size() + 1;
            department.setDepartmentCode(PathUtil.intToString(size, 3));
            fatherMultimap.put(tenantId, department);
        });
        //二级到n级部门编码
        List<DepartmentDTO> remainDepartments = allDepartments.parallelStream().filter(dp -> StringUtils.isEmpty(dp.getDepartmentCode())).collect(Collectors.toList());
        Collection<DepartmentDTO> values = fatherMultimap.values();
        int codeSize = values.size();
        do {
            Map<Long, String> sonCodeMultimap = Maps.newHashMap();
            values.forEach(department -> sonCodeMultimap.put(department.getId(), department.getDepartmentCode()));
            Multimap<Long, DepartmentDTO> sonMultimap = ArrayListMultimap.create();
            remainDepartments.forEach(department -> {
                if (sonCodeMultimap.get(department.getParentDepartmentId()) != null) {
                    String father = sonCodeMultimap.get(department.getParentDepartmentId());
                    int size = sonMultimap.get(department.getParentDepartmentId()).size() + 1;
                    department.setDepartmentCode(father + PathUtil.intToString(size, 3));
                    sonMultimap.put(department.getParentDepartmentId(), department);
                }
            });
            values = sonMultimap.values();
            codeSize += values.size();
            remainDepartments = allDepartments.stream().filter(dp -> StringUtils.isEmpty(dp.getDepartmentCode())).collect(Collectors.toList());
        } while (remainDepartments.size() > 0 && values.size() > 0);
        log.info(codeSize == allDepartments.size() ? "perfect!!!" : "not code size is:" + (allDepartments.size() - codeSize));
        for (DepartmentDTO department : allDepartments) {
            departmentMapper.updateDepartmentCode(department.getId(), department.getDepartmentCode());
        }
    }

    public Department findByDepartmentCodeAndTenantId(String departmentCode, Long tenantId, Integer status) {
        return departmentMapper.findByDepartmentCodeAndTenantId(departmentCode, tenantId, status);
    }

    public List<DepartmentDTO> selectDepartmentByManageId(Long currentUserId) {
        List<Department> departments = departmentMapper.selectDepartmentByManageId(currentUserId, DepartmentTypeEnum.ENABLE.getId());
        return departments.stream().map(department -> departmentToDepartmentDTO(department)).collect(Collectors.toList());
    }

    public Department findRootDepartmentByCodeAndTenantId(String departmentCode, Long tenantId, Integer status) {
        Department department = departmentMapper.findByDepartmentCodeAndTenantId(departmentCode, tenantId, null);
        if (department != null && department.getParentId() != null) {
            findRootDepartmentByCodeAndTenantId(department.getDepartmentCode(), tenantId, status);
        } else if (department != null) {
            return status == null || department.getStatus().equals(status) ? department : null;
        }
        return department;
    }

    public List<Department> findByParentIdAndStatusNotAndNameLike(Long parentId, Integer status, String name) {
        return departmentMapper.selectList(
                new EntityWrapper<Department>()
                        .eq("parent_id", parentId)
                        .ne("status", status)
                        .like("name", name)
        );
    }

    public List<DepartmentInfo> getAllDepartment(com.baomidou.mybatisplus.plugins.Page<DepartmentInfo> page) {
        return departmentMapper.getAllDepartment(page);
    }

    /**
     * 通过租户查询部门信息
     *
     * @param tenantId
     * @param page
     * @return
     */
    public List<Department> getDepartmentInfoByTenantId(Long tenantId,
                                                               String keyWord,
                                                               com.baomidou.mybatisplus.plugins.Page page) {
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .eq("tenant_id", tenantId)
                .ne("status", DepartmentTypeEnum.DELETE.getId())
                .orderBy("department_code");
        if (!StringUtils.isEmpty(keyWord)) {
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        return  baseMapper.selectPage(page, departmentWrapper);
    }


    /**
     * 获取下属部门信息
     * @param unitId   部门ID
     * @param includeOwn 是否包含本部门
     * @param page
     * @return
     */
    public List<Department> getUnitChildrenByUnitId(Long unitId, String keyWord, boolean includeOwn, com.baomidou.mybatisplus.plugins.Page page){
        Set<Long> unitChildrenIdByUnitId = listDepartmentChildrenIdById(unitId);
        if(includeOwn){
            unitChildrenIdByUnitId.add(unitId);
        }
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(unitChildrenIdByUnitId)){
            return new ArrayList<>();
        }
        List<Department> departments;
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in("id", unitChildrenIdByUnitId).orderBy("department_code");
        if(! org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        if(page != null){
            departments =selectList(departmentWrapper);
        }else{
            departments = baseMapper.selectPage(page,departmentWrapper);
        }
        return departments;
    }


    /**
     * 根据部门ID获取下属部门ID
     * @param unitId
     * @return
     */
    public Set<Long> listDepartmentChildrenIdById(Long unitId){
        Set<Long> ids = new HashSet<>();
        ids.add(unitId);
        return getUnitChildrenIdByUnitIds(ids,null);
    }

    public Department getByUserOid(UUID userOid){
      List<Department> departments=  baseMapper.listByUserOid(userOid);
      if (departments!=null || departments.size()>0){
          return departments.get(0);
      }
      return null;
    }

    /**
     * 根据部门ID获取下属部门ID
     * @param unitIds    部门ID
     * @param summaryIds
     * @return
     */
    private Set<Long> getUnitChildrenIdByUnitIds(Set<Long> unitIds,Set<Long> summaryIds){
        if(summaryIds == null){
            summaryIds = new HashSet<>();
        }
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(unitIds)){
            return summaryIds;
        }
        // 获取子部门
        Set<Long> unitChildrenIdByUnitIds = baseMapper.getUnitChildrenIdByUnitIds(unitIds);
        // 当子部门集合不为空
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(unitChildrenIdByUnitIds)){
            // 添加本次查询的
            boolean b = summaryIds.addAll(unitChildrenIdByUnitIds);
            if(b){
                getUnitChildrenIdByUnitIds(unitChildrenIdByUnitIds,summaryIds);
            }
        }
        return summaryIds;
    }

    public DepartmentInfo DepartmentDTOtoDepartmentInfo(DepartmentDTO departmentDTO) {
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo.setId(departmentDTO.getId());
        departmentInfo.setDepartmentOid(departmentDTO.getDepartmentOid().toString());
        departmentInfo.setParentId(departmentDTO.getParentDepartmentId());
        departmentInfo.setName(departmentDTO.getName());
        departmentInfo.setPath(departmentDTO.getPath());
        departmentInfo.setStatus(departmentDTO.getStatus());
        departmentInfo.setDepartmentCode(departmentDTO.getDepartmentCode());
        departmentInfo.setTenantId(departmentDTO.getTenantId());
        departmentInfo.setDataSource(departmentDTO.getDataSource());
        return departmentInfo;
    }

    public DepartmentDTO departmentToDepartmentDTO(Department department) {
        DepartmentDTO departmentDTO = mapperFacade.map(department, DepartmentDTO.class);
        if(department.getCompany() != null) {
            departmentDTO.setCompanyOid(department.getCompany().getCompanyOid());
            departmentDTO.setCompanyName(department.getCompany().getName());
        }
        if (department.getParentId() != null) {
            departmentDTO.setParentDepartmentId(department.getParentId());
        } else if(department.getParent() != null) {
            departmentDTO.setParentDepartmentId(department.getParent().getId());
            departmentDTO.setParentDepartmentOid(department.getParent().getDepartmentOid());
        }
        if(department.getManager() != null) {
            departmentDTO.setManagerOid(department.getManager().getUserOid());
            departmentDTO.setFullName(department.getManager().getFullName());
        }
        return departmentDTO;
    }

    public Department departmentDTOToDepartment(DepartmentDTO departmentDTO) {
        Department department = new Department();
        department.setCompany(companyService.getByCompanyOidCache(departmentDTO.getCompanyOid()));
        UserDTO manager = contactService.getUserDTOByUserOid(departmentDTO.getManagerOid());
        department.setManager(manager);
        return department;
    }

    /**
     * 查询部门下的所有用户 部门控件
     * @param departmentOid
     * @return
     */
    public List<DepartmentUserSummaryDTO> getDepartmentUser(Long tenantId, UUID departmentOid){
        return departmentMapper.getDepartmentUsers(tenantId,departmentOid);
    }

    /******************************* 以下为对外接口 **********************************/
    public DepartmentCO getDepartmentById(Long id) {
        Department department = baseMapper.selectById(id);
        return mapperFacade.map(department,DepartmentCO.class);
    }

    public DepartmentCO getDepartmentByCodeAndTenantId(String departmentCode) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Department department = selectOne(new EntityWrapper<Department>()
                .eq(StringUtils.hasText(departmentCode), "department_code", departmentCode)
                .eq("tenant_id", tenantId));
        return mapperFacade.map(department,DepartmentCO.class);
    }

    public List<DepartmentCO> listDepartmentsByIds(List<Long> departmentIds, String keyWord) {
        if (CollectionUtils.isEmpty(departmentIds)){
            return new ArrayList<>();
        }
        Wrapper<Department> wrapper = this.getWrapper().in("id", departmentIds);
        if(StringUtils.hasText(keyWord)){
            wrapper = wrapper.andNew()
                    .like(StringUtils.hasText(keyWord), "department_code", keyWord)
                    .or()
                    .like(StringUtils.hasText(keyWord), "name", keyWord);
        }

        List<Department> departments = baseMapper.selectList(wrapper);
        return mapperFacade.mapAsList(departments ,DepartmentCO.class);
    }

    public List<DepartmentCO> listPathByIds(List<Long> ids) {
        List<Department> list = baseMapper.selectList(new EntityWrapper<Department>()
                .in("id", ids));
        return mapperFacade.mapAsList(list,DepartmentCO.class);
    }

    public DepartmentCO getDepartmentByEmpOid(String empOid){
        DepartmentCO departmentCO = new DepartmentCO();
        DepartmentGroupDepartmentCO dto = departmentGroupService.selectByEmpOid( empOid);
        if(dto != null){
            departmentCO.setId(dto.getDepartmentId());
            departmentCO.setDepartmentCode(dto.getDepartmentCode());
            departmentCO.setDepartmentOid(UUID.fromString(dto.getDepartmentOid()));
            departmentCO.setName(dto.getName());
        }
        return departmentCO;
    }
    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentByCompanyIdAndTenantId(Long companyId,
                                                                                                    String deptCode,
                                                                                                    String deptName,
                                                                                                    com.baomidou.mybatisplus.plugins.Page<DepartmentCO> mybatisPage){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> wrapper = new EntityWrapper<Department>()
                .eq("company_id", companyId)
                .like(StringUtils.hasText(deptCode), "department_code", deptCode)
                .like(StringUtils.hasText(deptName), "name", deptName)
                .eq("tenant_id", tenantId);
        List<Department> result = baseMapper.selectPage(mybatisPage,wrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(result,DepartmentCO.class));
        return mybatisPage;
    }

    public List<DepartmentCO> listDepartmentChildrenAndOwnById(Long unitId, String keyWord, boolean includeOwn){
        Set<Long> unitChildrenIdByUnitId = listDepartmentChildrenIdById(unitId);
        if(includeOwn){
            unitChildrenIdByUnitId.add(unitId);
        }
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(unitChildrenIdByUnitId)){
            return new ArrayList<>();
        }
        List<Department> departments;
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in("id", unitChildrenIdByUnitId).orderBy("department_code");
        if(! org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        departments = baseMapper.selectList(departmentWrapper);
        return mapperFacade.mapAsList(departments,DepartmentCO.class);
    }

    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentChildrenById(Long unitId,
                                                                                          String keyWord,
                                                                                          boolean includeOwn,
                                                                                          com.baomidou.mybatisplus.plugins.Page<DepartmentCO> mybatisPage){
        Set<Long> unitChildrenIdByUnitId = listDepartmentChildrenIdById(unitId);
        if(includeOwn){
            unitChildrenIdByUnitId.add(unitId);
        }
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(unitChildrenIdByUnitId)){
            return mybatisPage.setRecords(new ArrayList<>());
        }
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in("id", unitChildrenIdByUnitId).orderBy("department_code");
        if(! org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> result = baseMapper.selectPage(mybatisPage,departmentWrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(result,DepartmentCO.class));
        return mybatisPage;
    }

    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentInfoByTenantId(String keyWord,
                                                                                            com.baomidou.mybatisplus.plugins.Page page){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .eq("tenant_id", tenantId)
                .ne("status", DepartmentTypeEnum.DELETE.getId())
                .orderBy("department_code");
        if(!org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> result = baseMapper.selectPage(page,departmentWrapper);
        page.setRecords(result);
        return page;
    }

    public DepartmentCO getDepartmentByOid(String oid){
        Department department = selectOne(new EntityWrapper<Department>()
                .eq("department_oid", oid));
        return mapperFacade.map(department,DepartmentCO.class);
    }

    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentsByIdsResultPage(List<Long> ids,
                                                                                              String keyWord,
                                                                                              com.baomidou.mybatisplus.plugins.Page page){
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in("id", ids)
                .ne("status", DepartmentTypeEnum.DELETE.getId())
                .orderBy("department_code");
        if(!org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> result = baseMapper.selectPage(page,departmentWrapper);
        page.setRecords(mapperFacade.mapAsList(result,DepartmentCO.class));
        return page;
    }

    public List<DepartmentCO> listDepartmentByStatus(Boolean enabled){
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> departmentWrapper;
        if(enabled != null){
            if(enabled){
                departmentWrapper = new EntityWrapper<Department>()
                        .eq("status", 101)
                        .eq("tenant_id",currentTenantId)
                        .orderBy("department_code");
            }else{
                departmentWrapper = new EntityWrapper<Department>()
                        .eq("status", 102)
                        .eq("tenant_id",currentTenantId)
                        .orderBy("department_code");
            }
        }else{
            departmentWrapper = new EntityWrapper<Department>()
                    .ne("status", 103)
                    .eq("tenant_id",currentTenantId)
                    .orderBy("department_code");
        }
        List<Department> departments = baseMapper.selectList(departmentWrapper);
        return mapperFacade.mapAsList(departments,DepartmentCO.class);
    }

    /**
     * 根据员工ID查询部门信息
     * @param empId
     * @return
     */
    public DepartmentCO getDepartmentByEmployeeId(Long empId){
        DepartmentCO departmentCO = new DepartmentCO();
        DepartmentGroupDepartmentCO dto = departmentGroupService.selectByEmployeeId(empId);
        if(dto != null){
            departmentCO.setId(dto.getDepartmentId());
            departmentCO.setDepartmentCode(dto.getDepartmentCode());
            departmentCO.setDepartmentOid(UUID.fromString(dto.getDepartmentOid()));
            departmentCO.setName(dto.getName());
        }
        return departmentCO;
    }

    public DepartmentUserSummaryDTO getDepartmentsAndUsersBykeywords(Long currentTenantID, String keyword, Boolean needEmployeeId, Integer departmentStatus) {
        List<UserDTO> users = contactService.listUsersByKeyword(currentTenantID, keyword, needEmployeeId);
        //TODO esUserInfoSerivce未迁移，暂时注释
        //users = esUserInfoSerivce.findUserByKeyword(currentTenantID, keyword, needEmployeeId);
        users = users.stream().map(u -> {
            Contact contact = contactService.getContactByUserOid(u.getDirectManager());
            if (contact != null) {
                u.setDirectManagerId(contact.getEmployeeId());
                u.setDirectManagerName(contact.getFullName());
            }
            return u;
        }).collect(Collectors.toList());
        List<Department> departments = departmentMapper.findDepartmentsByKeyword(currentTenantID, keyword, departmentStatus);
        departments = baseI18nService.convertListByLocale(departments);
        DepartmentUserSummaryDTO departmentUserSummaryDTO = new DepartmentUserSummaryDTO();
        departmentUserSummaryDTO.setDepartments(departments);
        departmentUserSummaryDTO.setUsers(users);
        return departmentUserSummaryDTO;
    }
    /******************************* 为对外接口end **********************************/



   private DepartmentCO toCO(Department department){
       DepartmentCO departmentCO=new DepartmentCO();
       departmentCO.setId(department.getId());
       departmentCO.setDepartmentOid(department.getDepartmentOid());
       departmentCO.setDepartmentCode(department.getDepartmentCode());
       departmentCO.setName(department.getName());
       departmentCO.setPath(department.getPath());

       return departmentCO;
   }

    //根据部门oids和层级，获取该部门的第 {level} 层父部门
    public List<DepartmentCO> listByOidsAndLevel(List<UUID> departmentOids, Integer level) {
        List<DepartmentCO> departments = new ArrayList<>();
        Department department = null;
        if (CollectionUtils.isNotEmpty(departmentOids)) {
            List<Department> departmentList = findByDepartmentOidIn(departmentOids, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
            if (CollectionUtils.isNotEmpty(departmentList)) {
                for (Department departmentResult : departmentList) {
                    //查询层级部门
                    if (level != null && level > 1) {
                        for (int i = 1; i < level.intValue(); i++) {
                            if (departmentResult == null || departmentResult.getParent() == null) {
                                department = null;
                                break;
                            }
                            department = departmentResult.getParent();
                            departmentResult = department;
                        }
                        if (department != null) {
                            departments.add(toCO(department));
                        }
                    } else {
                        //层级为 0，或为空时，即是当前部门
                        departments.add(toCO(departmentResult));
                    }
                }
            }
        }
        return departments;
    }

    public List<String> listtDepartmentPath(UUID userOid,UUID departmentOid,int departmentLevel)
    {
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        List<String> result = new ArrayList<>();
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLE.getId());
        if (department == null) {//未传部门
            LinkedHashSet<UUID> list = new LinkedHashSet<>();
            getDepartmentManagerOid(departmentUserService.getDepartmentByUserId(user.getId()).get(), list);//获取用户所再部门到顶层部门主管
            result.addAll(list.stream().map(UUID::toString).collect(Collectors.toList()));
        } else {//传部门 取所选部门到第departmentLevel层主管
            LinkedList<UserDTO> users = new LinkedList<>();
            getDepartmentManager(department, users);
            for (int i = 0; i <= departmentLevel - 1 && i < users.size(); i++) {
                UserDTO manager = users.get(i);
                if (manager != null && manager.getUserOid() != null && org.apache.commons.lang.StringUtils.isNotBlank(manager.getUserOid().toString())) {
                    result.add(manager.getUserOid().toString());
                }
            }
        }
        return result;
    }

    private void getDepartmentManagerOid(Department department, LinkedHashSet<UUID> list) {
        if (department != null) {
            UserDTO user = departmentUserService.getDepartmentManager(department.getId());
            if (user != null) {
                list.add(user.getUserOid());
            }
            if (department.getParent() != null) {
                getDepartmentManagerOid(department.getParent(), list);
            }
        }
    }

    private void getDepartmentManager(Department department, LinkedList<UserDTO> users) {
        if (department != null) {
//            User user = department.getManager();
            UserDTO user = departmentUserService.getDepartmentManager(department.getId());
            users.add(user);
            if (department.getParent() != null) {
                getDepartmentManager(department.getParent(), users);
            }
        }
    }




    public UserDTO getDepartmentManager(Long departmentId, UUID userOid) {
        Department department = selectOnebyId(departmentId);

        if (department != null) {
            //若有部门经理 并且不是申请人，则返回经理，否则查询父部门的经理
            UserDTO manager = departmentUserService.getDepartmentManager(department.getId());
            if (manager != null && !userOid.equals(manager.getUserOid())) {
                //返回部门经理
                return manager;
            } else {
                if (department.getParentId() != null) {
                    //找父部门经理
                    return getDepartmentManager(department.getParentId(), userOid);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }

    }

    public UUID getLastDepartmentManagerByUserOid(UUID userOid, boolean isContainPeerLevel) {
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        if (user == null) {
            throw new ObjectNotFoundException(UserDTO.class, userOid);
        }
        Department department = departmentUserService.getDepartmentByUserId(user.getId()).get();
        if (department == null) {
            throw new ValidationException(new ValidationError("operation.error", "user not have department"));
        }

        UserDTO departmentManager = null;
        if (isContainPeerLevel) {
            departmentManager = getDepartmentManager(department.getId(), userOid);
        } else {
            if (department != null && department.getParentId() != null) {
                departmentManager = getDepartmentManager(department.getParentId(), userOid);
            }
        }
        if (departmentManager != null) {
            return departmentManager.getUserOid();
        } else {
            return null;
        }
    }


    public HashMap<Integer, DepartmentCO> listAllDepartmentByOidAndUserOid(UUID departmentOid, UUID userOid) {
        HashMap<Integer, DepartmentCO> departmentHashMap = new HashMap<>();
        Department oneDepartment=departmentUserService.getDepartmentByUserId(contactService.getUserDTOByUserOid(userOid).getId()).get();
        Department department = findByDepartmentOidAndStatus(departmentOid, DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        getAllDepartment(1, department == null ? oneDepartment : department, departmentHashMap);//获取本级部门以上的所有部门
        return departmentHashMap;
    }

    private void getAllDepartment(Integer i, Department department, HashMap<Integer, DepartmentCO> departmentHashMap) {
        if (department != null) {
            DepartmentCO departmentCO=new DepartmentCO();
            departmentCO.setId(department.getId());
            departmentHashMap.put(i, departmentCO);
            if (department.getParent() != null) {
                i++;
                getAllDepartment(i, department.getParent(), departmentHashMap);
            }
        }
    }

    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentByTenantId(String deptCode,
                                                                                                    String deptName,String deptCodeFrom,String deptCodeTo,
                                                                                                    com.baomidou.mybatisplus.plugins.Page<DepartmentCO> mybatisPage){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> wrapper = new EntityWrapper<Department>()
                .like(StringUtils.hasText(deptCode), "department_code", deptCode)
                .like(StringUtils.hasText(deptName), "name", deptName)
                .ge(StringUtils.hasText(deptCodeFrom),"department_code",deptCodeFrom)
                .le(StringUtils.hasText(deptCodeTo),"department_code",deptCodeTo)
                .eq("tenant_id", tenantId)
                .eq("status", DepartmentTypeEnum.ENABLE.getId());
        List<Department> result = baseMapper.selectPage(mybatisPage,wrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(result,DepartmentCO.class));
        return mybatisPage;
    }

    public List<DepartmentCO> listDepartmentByTenantId(String deptCode,
                                                                                        String deptName,String deptCodeFrom,String deptCodeTo){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> wrapper = new EntityWrapper<Department>()
                .like(StringUtils.hasText(deptCode), "department_code", deptCode)
                .like(StringUtils.hasText(deptName), "name", deptName)
                .ge(StringUtils.hasText(deptCodeFrom),"department_code",deptCodeFrom)
                .le(StringUtils.hasText(deptCodeTo),"department_code",deptCodeTo)
                .eq("tenant_id", tenantId)
                .eq("status", DepartmentTypeEnum.ENABLE.getId());
        List<Department> result = baseMapper.selectList(wrapper);
        return mapperFacade.mapAsList(result,DepartmentCO.class);
    }

    /**
     * 条件查询租户下的部门信息 - 分页
     * @param departmentCode
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param ids
     * @param keyWord
     * @return
     */
    public com.baomidou.mybatisplus.plugins.Page<DepartmentCO> pageDepartmentsByCond(String departmentCode,
                                                    String codeFrom,
                                                    String codeTo,
                                                    String name,
                                                    List<Long> ids,
                                                    String keyWord,
                                                    com.baomidou.mybatisplus.plugins.Page<DepartmentCO> mybatisPage){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> wrapper = new EntityWrapper<Department>()
                 .eq("tenant_id", tenantId)
                 .eq("status", DepartmentTypeEnum.ENABLE.getId())
                 .in(ids != null && ids.size() > 0,"id",ids)
                 .like(StringUtils.hasText(departmentCode),"department_code", departmentCode)
                 .ge(StringUtils.hasText(codeFrom),"department_code",codeFrom)
                 .le(StringUtils.hasText(codeTo),"department_code",codeTo)
                 .like(StringUtils.hasText(name),"name",name);
        if(StringUtils.hasText(keyWord)){
            wrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> departments = baseMapper.selectPage(mybatisPage, wrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(departments,DepartmentCO.class));
        return mybatisPage;
    }

    /**
     * 条件查询租户下的部门信息
     * @param departmentCode
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param ids
     * @param keyWord
     * @return
     */
    public List<DepartmentCO> listDepartmentsByCond(String departmentCode,
                                                    String codeFrom,
                                                    String codeTo,
                                                    String name,
                                                    List<Long> ids,
                                                    String keyWord){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<Department> wrapper = new EntityWrapper<Department>()
                .eq("tenant_id", tenantId)
                .eq("status", DepartmentTypeEnum.ENABLE.getId())
                .in(ids != null && ids.size() > 0,"id",ids)
                .like(StringUtils.hasText(departmentCode),"department_code", departmentCode)
                .ge(StringUtils.hasText(codeFrom),"department_code",codeFrom)
                .le(StringUtils.hasText(codeTo),"department_code",codeTo)
                .like(StringUtils.hasText(name),"name",name);
        if(StringUtils.hasText(keyWord)){
            wrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> departments = baseMapper.selectList(wrapper);
        return mapperFacade.mapAsList(departments,DepartmentCO.class);
    }

    public Map<String,String> importDepartment(List<DepartmentImportDTO> list) {
        StringBuilder message = new StringBuilder();
        Map<String,String> map = new HashMap<>(2);
        List<Department> departmentList = new ArrayList<>();
        list.forEach(item -> {
            String errorMessage = "";
            long tenantId = OrgInformationUtil.getCurrentTenantId();
            if(StringUtils.isEmpty(item.getDepartmentCode())){
                errorMessage += "第"+item.getRowNumber()+"行，部门代码为空\r\n";
            }else{
                List<Department> departments = baseMapper.selectList(new EntityWrapper<Department>()
                        .eq("department_code", item.getParentCode()));
                if(CollectionUtils.isNotEmpty(departments)){
                    errorMessage += "第"+item.getRowNumber()+"行，该部门已存在\r\n";
                }
            }
            if(StringUtils.isEmpty(item.getName())){
                errorMessage += "第"+item.getRowNumber()+"行，部门名称为空\r\n";
            }
            if(StringUtils.isEmpty(item.getStatus())){
                errorMessage += "第"+item.getRowNumber()+"行，部门状态为空\r\n";
            }
            Department parentDepartment = null;
            if(!StringUtils.isEmpty(item.getParentCode())){
                parentDepartment = baseMapper.findByDepartmentCodeAndTenantId(item.getParentCode(),tenantId,101);
                if(ObjectUtils.isEmpty(parentDepartment)){
                    errorMessage += "第"+item.getRowNumber()+"行，当前租户下下上级部门不存在\r\n";
                }
            }
            if("".equals(errorMessage)){
                Department department = new Department();
                department.setTenantId(tenantId);
                department.setDepartmentCode(item.getDepartmentCode());
                department.setName(item.getName());
                department.setParentId(parentDepartment.getId());
                department.setStatus(Integer.valueOf(item.getStatus()));
                departmentList.add(department);

            }else{
                message.append(errorMessage);
            }
        });
        if ("".equals(message.toString())) {
            for(Department department : departmentList){
                baseMapper.insert(department);
            }
            map.put("导入成功","success");
            return map;
        } else {
            map.put("导入失败","fail");
            map.put("message",message.toString());
            return map;
        }
    }

    /**
     *  根据部门code 部门名称 模糊查询部门信息
     * @param id       账户当前部门id
     * @param ids      手动选择id
     * @param keyWord  关键词
     * @return         部门信息
     */
    public List<Department> selectByEmpOidKeyWord(String id,List<Long> ids, String keyWord) {
        if (id != null) {
            ids = Collections.singletonList(departmentGroupService.selectByEmpOid(id).getDepartmentId());
        }
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in(ids != null ,"id", ids)
                .eq("deleted", false);
        if (!org.apache.commons.lang3.StringUtils.isEmpty(keyWord)) {
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        return departmentMapper.selectList(departmentWrapper);
    }

    /**
     * 通过部门id查询部门及子部门
     * @param unitId       部门id
     * @param keyWord      关键词
     * @param includeOwn   是否包含本级部门
     * @param mybatisPage  分页
     * @return             部门信息
     */
    public com.baomidou.mybatisplus.plugins.Page<Department> departmentChildrenById(Long unitId,
                                                   String keyWord,
                                                   boolean includeOwn,
                                                   com.baomidou.mybatisplus.plugins.Page<Department> mybatisPage) {
        Set<Long> unitChildrenIdByUnitId = listDepartmentChildrenIdById(unitId);
        if(includeOwn){
            unitChildrenIdByUnitId.add(unitId);
        }
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(unitChildrenIdByUnitId)){
            return mybatisPage.setRecords(new ArrayList<>());
        }
        Wrapper<Department> departmentWrapper = new EntityWrapper<Department>()
                .in("id", unitChildrenIdByUnitId).orderBy("department_code");
        if(! org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            departmentWrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<Department> result = baseMapper.selectPage(mybatisPage,departmentWrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(result,Department.class));
        return mybatisPage;
    }

    public List<DepartmentTreeDTO> getSubsidiaryDepartmentAllKeyWords(String code, String name, Long currentTenantId, Integer status, String language) {
        if (!StringUtils.isEmpty(code) || !StringUtils.isEmpty(name)) {
            return departmentMapper.findTenantAllDepartmentKeyWords(code, name, currentTenantId, status, language);
        }
        List<DepartmentTreeDTO> departmentTreeDTOS = departmentMapper.findTenantAllDepartmentKeyWords(code, name, currentTenantId, status, language);
        return getTreeCategory(departmentTreeDTOS);
    }

    private List<DepartmentTreeDTO> getTreeCategory(List<DepartmentTreeDTO> companys) {
        //过滤顶级分类
        List<DepartmentTreeDTO> top = companys.stream().filter(x -> StringUtils.isEmpty(x.getParentId())).collect(Collectors.toList());
        //过滤子分类
        List<DepartmentTreeDTO> children = companys.stream().filter(x -> !StringUtils.isEmpty(x.getParentId())).collect(Collectors.toList());
        //parentId作为key，子级作为value组成的map
        Map<Long, List<DepartmentTreeDTO>> allMap = children.stream().collect(Collectors.groupingBy(DepartmentTreeDTO::getParentId));
        //递归查询
        return treeCategoryData(top, allMap);
    }

    private List<DepartmentTreeDTO> treeCategoryData(List<DepartmentTreeDTO> top, Map<Long, List<DepartmentTreeDTO>> allMap) {
        //遍历
        top.forEach(category -> {
            List<DepartmentTreeDTO> temp = allMap.get(category.getId());
            if (temp != null && !temp.isEmpty()) {
                category.setChildren(temp);
                treeCategoryData(category.getChildren(), allMap);
            } else {
                category.setChildren(new ArrayList<>());
            }
        });
        return top;
    }
}

