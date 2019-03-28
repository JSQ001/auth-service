package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.DepartmentPositionUser;
import com.hand.hcf.app.mdata.department.domain.DepartmentRole;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentRoleEnum;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentTypeEnum;
import com.hand.hcf.app.mdata.department.dto.DepartmentRoleDTO;
import com.hand.hcf.app.mdata.department.persistence.DepartmentRoleMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class DepartmentRoleService extends BaseService<DepartmentRoleMapper, DepartmentRole> {

    private static final Logger log = LoggerFactory.getLogger(DepartmentRoleService.class);

    @Autowired
    private DepartmentRoleMapper departmentRoleMapper;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentPositionUserService departmentPositionUserService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;


    public DepartmentRole getDepartmentRole(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("department_id", departmentId);
        map.put("deleted", false);
        List<DepartmentPositionUser> departmentPositionUserList = departmentPositionUserService.selectByMap(map);
        DepartmentRole departmentRole = departmentPositionUserService.convertPositionUserToRole(departmentPositionUserList);
        DepartmentRole deptRole = this.selectOne(
                new EntityWrapper<DepartmentRole>().eq("department_id", departmentId)
        );
        if (deptRole == null) {
            departmentRole.setId(deptRole.getId());
        }
        return  departmentRole;
    }

    public DepartmentRoleDTO getDepartmentRoleDTOByDepartmentId(Long departmentId) {
        DepartmentRole departmentRole = getDepartmentRole(departmentId);
        DepartmentRoleDTO departmentRoleDTO = departmentRoleToDTO(departmentRole);
        return combineDepartmentRoleInfo(departmentRoleDTO);
    }

    @Transactional
    public DepartmentRole addEmptyDepartmentRole(Long departmentId) {
        DepartmentRole departmentRole = this.selectOne(
                new EntityWrapper<DepartmentRole>().eq("department_id", departmentId)
        );
        if (departmentRole == null) {
            departmentRole = new DepartmentRole();
            departmentRole.setDepartmentId(departmentId);
            departmentRole.setCreatedDate(ZonedDateTime.now());
            departmentRole.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        }

        departmentPositionUserService.saveOrUpdateDepartmentPositionUser(departmentRole,departmentId);
        this.insertOrUpdate(departmentRole);
        return  departmentRole;
    }

    @Transactional
    public DepartmentRoleDTO upsertDepartmentRole(DepartmentRoleDTO departmentRoleDTO) {
        if (departmentRoleDTO.getDepartmentOid() == null) {
            throw new ValidationException(new ValidationError("departmentOid", "departmentOid can't be null"));
        }
        Department department = departmentService.findByDepartmentOidAndStatus(departmentRoleDTO.getDepartmentOid(), DepartmentTypeEnum.FIND_ENABLE.getId());
        DepartmentRole departmentRole = this.selectOne(
                new EntityWrapper<DepartmentRole>().eq("department_id", department.getId())
        );
        if (departmentRole == null) {
            departmentRole = new DepartmentRole();
            departmentRole.setDepartmentId(department.getId());
            departmentRole.setCreatedDate(ZonedDateTime.now());
            departmentRole.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        }
        injectOidsFromDepartmentRoleDTOToDepartmentRole(departmentRole, departmentRoleDTO);
        this.insertOrUpdate(departmentRole);
        departmentPositionUserService.saveOrUpdateDepartmentPositionUser(departmentRole,department.getId());
        DepartmentRoleDTO persistDepartmentRoleDTO = departmentRoleToDTO(departmentRole);
        persistDepartmentRoleDTO.setDepartmentOid(department.getDepartmentOid());
        return combineDepartmentRoleInfo(persistDepartmentRoleDTO);
    }

    @Transactional
    public DepartmentRoleDTO upsertDepartmentManager(Long departmentId, UUID managerOid) {
        DepartmentRole departmentRole = getDepartmentRole(departmentId);
        if (departmentRole == null) {
            departmentRole = new DepartmentRole();
            departmentRole.setDepartmentId(departmentId);
            departmentRole.setCreatedDate(ZonedDateTime.now());
            try {
                departmentRole.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            } catch (Exception e) {

            }
        }
        departmentRole.setManagerOid(managerOid);
        this.insertOrUpdate(departmentRole);
        departmentPositionUserService.saveOrUpdateDepartmentPositionUser(departmentRole,departmentId);
        return combineDepartmentRoleInfo(departmentRoleToDTO(departmentRole));
    }


    public DepartmentRoleDTO combineDepartmentRoleInfo(DepartmentRoleDTO departmentRoleDTO) {
        List<UUID> userOids = getAllOidsFromDepartmentRoleDTO(departmentRoleDTO);
        List<UserDTO> users = contactService.listUserDTOByUserOid(userOids);
        injectUserNameToDepartmentRoleDTO(users, departmentRoleDTO);
        return departmentRoleDTO;
    }

    public void deleteDepartmentRole(Long departmentId) {
        departmentRoleMapper.deleteById(departmentId);
    }

    /**
     * 查找部门某个角色的用户Oid
     *
     * @param department
     * @param departmentRoleEnum
     * @return
     */
    public UUID getRelateDepartmentRole(Department department, DepartmentRoleEnum departmentRoleEnum) {
        return getRelateDepartmentRole(department, departmentRoleEnum, false);
    }

    /**
     * 查找部门某个角色的用户Oid
     *
     * @param department
     * @param departmentRoleEnum
     * @param jumpNullRoleUser   是否在当前部门角色为空时查找其上级部门(是否跳过当前角色为空的情况)，ture=不查找，false=查找，返回null
     * @return
     */
    public UUID getRelateDepartmentRole(Department department, DepartmentRoleEnum departmentRoleEnum, boolean jumpNullRoleUser) {
        UUID userOid = null;
        do {
            DepartmentRole departmentRole = getDepartmentRole(department.getId());
            if (departmentRole != null) {
                String getterName = switchDepartmentRoleEnumToMethodName("get", departmentRoleEnum);
                try {
                    Method getter = DepartmentRole.class.getDeclaredMethod(getterName);
                    Object invokeResult = getter.invoke(departmentRole);
                    userOid = invokeResult != null ? (UUID) invokeResult : null;
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    log.warn(e.getMessage());
                }
            }
            if (!jumpNullRoleUser) {
                if (userOid == null) {
                    department = department.getParent();
                }
            } else {
                return userOid;
            }
        } while (userOid == null && department != null);
        return userOid;
    }

    /**
     * 通过给定一个用户角色数组，来查询当前部门需要查询的用户角色对应的用户
     *
     * @param department
     * @param departmentRoleEnums
     * @param jumpNullRoleUser
     * @return
     */
    public List<UUID> getRelateDepartmentRoles(Department department, List<DepartmentRoleEnum> departmentRoleEnums, boolean jumpNullRoleUser) {
        // 获取部门角色(访问数据库1次)
        DepartmentRole departmentRole = getDepartmentRole(department.getId());
        List<UUID> result = new ArrayList<UUID>();
        for (DepartmentRoleEnum role : departmentRoleEnums) {
            String getterName = switchDepartmentRoleEnumToMethodName("get", role);
            try {
                Method getter = departmentRole.getClass().getDeclaredMethod(getterName.toString());
                // 开启强制反射，虽然getter方法是公开的，但是...
                getter.setAccessible(true);
                Object obj = getter.invoke(departmentRole);
                if (obj != null) {
                    result.add((UUID) obj);
                } else {
                    if (jumpNullRoleUser) {
                        continue;
                    } else {
                        if (department.getParent() != null) {
                            getRelateDepartmentRole(department.getParent(), role, jumpNullRoleUser);
                        } else {
                            log.warn("在查询当前部门的父级部门时发现为空，无法判断是否是顶级部门，请检查程序数据是否正确");
                            continue;
                        }
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.warn(e.getMessage());
                continue;
            }
        }
        return result;
    }


    /**
     * 通过指定前缀来获取其对应的方法名称
     *
     * @param prefix
     * @param role
     * @return
     */
    private String switchDepartmentRoleEnumToMethodName(String prefix, DepartmentRoleEnum role) {
        // 通过反射获取方法的名字
        return switchDepartmentRoleEnumToMethodNameBySuffix(prefix, role, DepartmentRole.SUFFIX);
    }

    /**
     * 通过指定前缀和后缀来获取其对应的方法名称
     *
     * @param prefix
     * @param role
     * @param suffix
     * @return
     */
    private String switchDepartmentRoleEnumToMethodNameBySuffix(String prefix, DepartmentRoleEnum role, String suffix) {
        StringBuilder getterName = new StringBuilder(prefix);
        String[] segment = role.toString().split("_");
        List<String> ignoreUpperCaseList = DepartmentRole.IGNORE_UPPER_CASE_LIST;
        for (String seg : segment) {
            if (ignoreUpperCaseList.contains(seg)) {
                getterName.append(seg);
            } else {
                getterName.append(seg.charAt(0));
                getterName.append(seg.substring(1).toLowerCase());
            }
        }
        getterName.append(suffix);
        return getterName.toString();
    }

    /**
     * 从一个用户集合中向DepartmentRoleDTO注入用户名
     *
     * @param users
     * @param dto
     */
    private void injectUserNameToDepartmentRoleDTO(List<UserDTO> users, DepartmentRoleDTO dto) {
        DepartmentRoleEnum[] roles = DepartmentRoleEnum.values();
        for (DepartmentRoleEnum role : roles) {
            if (role == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodName("get", role);
            String setterName = switchDepartmentRoleEnumToMethodNameBySuffix("set", role, "Name");
            try {
                Method getter = DepartmentRoleDTO.class.getDeclaredMethod(getterName);
                getter.setAccessible(true);
                Object object = getter.invoke(dto);
                if (object != null) {
                    users.stream().forEach(currentUser -> {
                        if (currentUser.getUserOid().equals(object)) {
                            try {
                                Method setter = DepartmentRoleDTO.class.getDeclaredMethod(setterName, String.class);
                                setter.setAccessible(true);
                                setter.invoke(dto, currentUser.getFullName());
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                log.error(e.getMessage());
                            }
                        }
                    });
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage());
                continue;
            }
        }
    }


    /**
     * 通过反射从DepartmentRoleDTO中注入Oid到DepartmentRole中
     *
     * @param role
     * @param dto
     */
    private void injectOidsFromDepartmentRoleDTOToDepartmentRole(DepartmentRole role, DepartmentRoleDTO dto) {
        DepartmentRoleEnum[] roles = DepartmentRoleEnum.values();
        for (DepartmentRoleEnum roleEnum : roles) {
            if (roleEnum == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodName("get", roleEnum);
            String setterName = switchDepartmentRoleEnumToMethodName("set", roleEnum);
            try {
                Object object = DepartmentRoleDTO.class.getDeclaredMethod(getterName).invoke(dto);
                DepartmentRole.class.getDeclaredMethod(setterName, UUID.class).invoke(role, object != null ? (UUID) object : null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 获取全部的UUID集合从一个DepartmentRoleDTO对象中
     *
     * @param departmentRoleDTO
     * @return
     */
    private List<UUID> getAllOidsFromDepartmentRoleDTO(DepartmentRoleDTO departmentRoleDTO) {
        List<UUID> result = new ArrayList<UUID>();
        DepartmentRoleEnum[] roles = DepartmentRoleEnum.values();
        for (DepartmentRoleEnum roleEnum : roles) {
            if (roleEnum == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodName("get", roleEnum);
            try {
                Object object = DepartmentRoleDTO.class.getDeclaredMethod(getterName).invoke(departmentRoleDTO);
                if (object != null) {
                    result.add((UUID) object);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * 反向映射用户Oid为其所在部门的角色
     *
     * @param departmentRole
     * @param userOid
     * @return
     */
    public DepartmentRoleEnum reverseMappingUserOidToDepartmentRole(DepartmentRole departmentRole, UUID userOid) {
        DepartmentRoleEnum[] roleEna = DepartmentRoleEnum.values();
        DepartmentRoleEnum result = null;
        for (DepartmentRoleEnum role : roleEna) {
            if (role.equals(DepartmentRoleEnum.NORMAL)) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodName("get", role);
            try {
                UUID roleOid = (UUID) departmentRole.getClass().getDeclaredMethod(getterName).invoke(departmentRole);
                if (roleOid != null) {
                    if (roleOid.equals(userOid)) {
                        result = role;
                        break;
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("反射错误 : " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 查询部门对应用户所在的部门角色信息
     * @param department：部门对象
     * @param userOid：用户Oid
     * @return
     */
    public List<DepartmentRoleEnum> findDepartmentRoleByUserOidAndDepartmentID(Department department, UUID userOid){
        List<DepartmentRoleEnum> roleEnums = new ArrayList<>();
        // 查询部门角色
        DepartmentRole departmentRole = getDepartmentRole(department.getId());
        for (DepartmentRoleEnum role : DepartmentRoleEnum.values()) {
            if (role == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodName("get", role);
            try {
                UUID roleOid = (UUID) departmentRole.getClass().getDeclaredMethod(getterName).invoke(departmentRole);
                if (null != roleOid && roleOid.equals(userOid)) {
                    roleEnums.add(role);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("反射错误 : " + e.getMessage());
            }
        }
        return roleEnums;
    }

    /**
     * 映射角色枚举为角色名称(晶科)
     * @param role
     * @return
     */
    public String mappingDepartmentRoleToJinkoRoleName(DepartmentRoleEnum role) {
        if (role == null) {
            return "";
        }
        switch (role) {
            case MANAGER:
                return "经理";
            case CHARGE_MANAGER:
                return "部门总监";
            case HRBP:
                return "HR";
            case FINANCIAL_BP:
                return "财务BP";
            case FINANCIAL_AP:
                return "财务AP";
            case LEGAL_REVIEW:
                return "法务审核";
            case ADMINISTRATIVE_REVIEW:
                return "行政审核";
            case FINANCIAL_DIRECTOR:
                return "财务总监";
            case VICE_MANAGER:
                return "副经理";
            case DEPARTMENT_MANAGER:
                return "部门主管";
            case VICE_PRESIDENT:
                return "副总裁";
            case PRESIDENT:
                return "总裁";
            case FINANCIAL_MANAGER:
                return "财务经理";
            case NORMAL:
                return "员工";
        }
        return "";
    }

    public DepartmentRoleDTO departmentRoleToDTO(DepartmentRole departmentRole) {
        DepartmentRoleDTO departmentRoleDTO = new DepartmentRoleDTO();
        if (departmentRole != null) {
            mapperDomainToDTO(departmentRole, departmentRoleDTO);
        }
        return departmentRoleDTO;
    }

    public DepartmentRole departmentRoleDTOToDepartmentRole(DepartmentRoleDTO departmentRoleDTO) {
        DepartmentRole departmentRole = new DepartmentRole();
        mapperDTOToDomain(departmentRole, departmentRoleDTO);
        return departmentRole;
    }

    private void mapperDomainToDTO(DepartmentRole role, DepartmentRoleDTO dto) {
        DepartmentRoleEnum[] roleEnum = DepartmentRoleEnum.values();
        for (DepartmentRoleEnum oneRole : roleEnum) {
            if (oneRole == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodNameBySuffix("get", oneRole, "Oid");
            String setterName = switchDepartmentRoleEnumToMethodNameBySuffix("set", oneRole, "Oid");
            try {
                DepartmentRoleDTO.class.getDeclaredMethod(setterName, UUID.class).invoke(dto, DepartmentRole.class.getDeclaredMethod(getterName).invoke(role));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }

    }

    private void mapperDTOToDomain(DepartmentRole role, DepartmentRoleDTO dto) {
        DepartmentRoleEnum[] roleEnum = DepartmentRoleEnum.values();
        for (DepartmentRoleEnum oneRole : roleEnum) {
            if (oneRole == DepartmentRoleEnum.NORMAL) {
                continue;
            }
            String getterName = switchDepartmentRoleEnumToMethodNameBySuffix("get", oneRole, "Oid");
            String setterName = switchDepartmentRoleEnumToMethodNameBySuffix("set", oneRole, "Oid");
            try {
                DepartmentRole.class.getDeclaredMethod(setterName, UUID.class).invoke(dto, DepartmentRoleDTO.class.getDeclaredMethod(getterName).invoke(role));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }

    }
}
