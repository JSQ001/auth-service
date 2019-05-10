package com.hand.hcf.app.workflow.externalApi;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.mdata.implement.web.CompanyControllerImpl;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.mdata.implement.web.DepartmentControllerImpl;
import com.hand.hcf.app.workflow.constant.LocaleMessageConstants;
import com.hand.hcf.app.workflow.dto.chain.UserApprovalDTO;
import com.hand.hcf.app.workflow.util.CheckUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class BaseClient {
    private CommonControllerImpl organizationClient;
    private CompanyControllerImpl companyClient;
    private ContactControllerImpl contactClient;
    private DepartmentControllerImpl departmentClient;
    private MessageService messageService;

    /**
     * 根据消息代码获取多语言并返回
     * @version 1.0
     * @author mh.z
     * @date 2019/05/04
     *
     * @param messageCode 消息代码
     * @param required true必须要有多语言
     * @param objs 参数
     * @return 多语言
     */
    public String getMessageDetailByCode(String messageCode, boolean required, Object... objs) {
        String messageDetail = messageService.getMessageDetailByCode(messageCode, objs);

        if (required && StringUtils.isEmpty(messageDetail)) {
            throw new BizException(LocaleMessageConstants.NOT_FIND_THE_MESSAGE, new Object[] { messageCode });
        }

        return messageDetail;
    }

    /**
     * 根据用户oid查询员工
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param userOidList 用户oid
     * @return
     */
    public List<ContactCO> listContactCOs(List<UUID> userOidList) {
        CheckUtil.notNull(userOidList, "userOidList null");

        Stream<String> stream = userOidList.stream().map(userOid -> userOid.toString());
        List<String> userOidStrList = stream.collect(Collectors.toList());
        List<ContactCO> userList = contactClient.listByUserOids(userOidStrList);

        return userList;
    }

    public Boolean getApprovalRuleEnabled(UUID companyOid) {
        return true;
        //TODO 需要系统配置
        //Boolean.parseBoolean(parameterClient.getParameterValueByParameterCode(code,sobId,companyId));
    }

    public Integer getApprovalRuleSelfSkip(UUID companyOid) {
        return 0;
    }


    public CompanyCO getCompanyByUserOid(UUID userOid) {
        return companyClient.getByUserOid(userOid);
    }


    public CompanyCO getCompanyByOid(UUID companyOid) {
        return companyClient.getByCompanyOid(companyOid.toString());
    }


    public CompanyCO getCompanyById(Long companyId) {
        return companyClient.getById(companyId);
    }

    public CompanyConfigurationCO getCompanyConfigByCompanyOid(UUID companyOid) {
        return companyClient.getCompanyConfigByCompanyOid(companyOid);
    }


    public List<UserApprovalDTO> listUserByUserGroupOid(UUID userGroupOid) {
        //Page page = new Page(0, 100000); jiu.zhao 三方接口修改 20190327
        return contactClient.pageByUserGroupOid(userGroupOid, 0, 100000).getRecords().stream()
                .map(u -> userCOToUserApprovalDTO(u)
                ).collect(Collectors.toList());
    }

    public UUID getDirectManager(UUID userOid, Integer level) {
        // addded by mh.z 20190222 层级为null调用ContactClient.getDirectManager会报错
        if (level == null) {
            level = 1;
        }

        return contactClient.getDirectManager(userOid, level);
    }


    public UserApprovalDTO getUserByUserOid(UUID userOid) {
        if (userOid == null) {
            return null;
        }

        return userCOToUserApprovalDTO(contactClient.getByUserOid(userOid));
    }

    public List<UserApprovalDTO> listByUserOids(List<UUID> userOids) {
        return contactClient.listByUserOids(userOids.stream().map(u -> u.toString()).collect(Collectors.toList()))
                .stream().map(u -> userCOToUserApprovalDTO(u)).collect(Collectors.toList());
    }

    private UserApprovalDTO userCOToUserApprovalDTO(ContactCO u) {
        // added by mh.z 20190221 参数u可能是null
        if (u == null) {
            return null;
        }

        UserApprovalDTO userApprovalDTO = new UserApprovalDTO();
        userApprovalDTO.setId(u.getId());
        userApprovalDTO.setUserOid(UUID.fromString(u.getUserOid()));
        userApprovalDTO.setEmployeeCode(u.getEmployeeCode());
        userApprovalDTO.setFullName(u.getFullName());
        return userApprovalDTO;
    }

    public UUID getDepartmentManagerOid(Long departmentId) {
        return UUID.fromString(departmentClient.getDepartmentManager(departmentId).getUserOid());
    }

    public UserApprovalDTO getUserByDeparmentOidAndPosition(UUID departmentOid, String positionCode) {
        return userCOToUserApprovalDTO(departmentClient.getUserByDeparmentOidAndPosition(departmentOid, positionCode));
    }

    public List<String> getDepartmentPath(UUID userOid, UUID departmentOid, int departmentLevel) {
        return departmentClient.listDepartmentPath(userOid, departmentOid, departmentLevel);
    }

    public DepartmentCO getDepartmentByUserOid(UUID userOid) {
        return departmentClient.getDepartmentByEmpOid(userOid.toString());
    }

    public List<DepartmentCO> listDepartmentByDepartmentOidsAndLevel(List<UUID> departmentOids, Integer departmentLevel) {
        return departmentClient.listByOidsAndLevel(departmentOids, departmentLevel);
    }


    public DepartmentCO getDepartmentByDepartmentOidAndLevel(UUID departmentOid, Integer departmentLevel) {
        // added by mh.z 20190221 层级为null调用departmentClient.listByOidsAndLevel会报错,
        // 所以departmentLevel为null则设置成0（层级为0或为空时，都代表当前部门）
        if (departmentLevel == null) {
            departmentLevel = 0;
        }

        List<DepartmentCO> departments = departmentClient.listByOidsAndLevel(Arrays.asList(departmentOid), departmentLevel);
        if (departments.size() > 0) {
            return departments.get(0);
        }
        return null;
    }


    public DepartmentCO getDepartmentByDepartmentOid(UUID departmentOid) {
        return departmentClient.getDepartmentByOid(departmentOid.toString());
    }

    public HashMap<Integer, DepartmentCO> getAllDepartment(UUID departmentOid, UUID userOid) {
        return departmentClient.listAllDepartmentByOid(departmentOid, userOid);
    }


    public UUID getLastDepartmentManagerByApplicantOid(UUID userOid, Boolean isContainPeerLevel) {
        return departmentClient.getLastDepartmentManagerByUserOid(userOid, isContainPeerLevel);
    }


    public UUID getLastDepartmentManagerByApplicantOid(UUID userOid) {
        return departmentClient.getLastDepartmentManagerByUserOid(userOid, true);
    }

    public List<DepartmentPositionCO> listDepartmentPosition(UUID companyOid) {
        return departmentClient.listDepartmentPosition(companyOid, null, null, null);
    }

    public List<DepartmentPositionCO> getDepartmentPositionByUserAndDepartment(Long departmentId, UUID userOid) {
        return departmentClient.listDepartmentPosition(null, departmentId, userOid, null);
    }


    public List<DepartmentPositionCO> listDepartmentPosition(Long tenantId) {
        return departmentClient.listDepartmentPosition(null, null, null, tenantId);
    }

    public ContactCO getUserById(Long userId) {
        return contactClient.getById(userId);
    }

    public SysCodeValueCO getSysCodeValueByCodeAndValue(String code, String value) {
        return organizationClient.getSysCodeValueByCodeAndValue(code, value);
    }
}
