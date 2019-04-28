package com.hand.hcf.app.mdata.contact.dto;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ContactQO {
    private Long id;
    private Long userId;
    private UUID userOid;
    private List<UUID> userOids;
    private List<Long> userIds;
    private List<Long> contactIds;
    private Long tenantId = OrgInformationUtil.getCurrentTenantId();
    private Long companyId;
    private List<Long> companyIds;
    private UUID companyOid;
    private List<UUID> companyOids;
    private Long setOfBooksId;
    private Long departmentId;
    private UUID departmentOid;
    private List<UUID> departmentOids;
    private UUID corporationOid;
    private List<UUID> legalEntityOids;
    private Long userGroupId;
    private UUID userGroupOid;
    private UUID costCenterItemOid;
    private Integer status;
    private Boolean statusAvailable;//status in (1001,1002)
    private Boolean exLeaved; //排除已离职status!=1003
    private List<String> employeeIds;
    private String employeeId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String title;
    private Boolean isPrimaryPhone;//是否主手机号
    private String keyContact;//联系人姓名/工号模糊查询
    /**
     * 公司代码/名称 模糊查询
     */
    private String keyCompany;
    /**
     * 部门代码/名称模糊查询
     */
    private String keyDepartment;
    private String keyword;//模糊查询关键字
    /**
     * 关键字只有员工工号、姓名
     */
    private String keywordCodeName;
    @Builder.Default
    private Boolean fuzzy = false;//模糊查询
    @Builder.Default
    private Boolean leaving;//是否待离职
    private Boolean leaved;//是否已离职
    @Builder.Default
    private Boolean inverseContact=false;//反选员工(not in contactIds)
    private Boolean inverseUser=false;//反选用户(not in userIds or userOids)
    @Builder.Default
    private ZonedDateTime now=ZonedDateTime.now();
    private ZonedDateTime leavedDate;
    @Builder.Default
    private Boolean hasCompany=true;
    @Builder.Default
    private Boolean hasDepartment=false;
    @Builder.Default
    private Boolean hasLegal=false;
    @Builder.Default
    private Boolean hasTenant=false;
    private Boolean orderByFullName;
    private Boolean orderByEmployeeId;
    //直属领导人查询时,需排除当前员工
    @Builder.Default
    private Long currentContactId = null;
    @Builder.Default
    private UUID currentUserOid = null;
    /**
     * 数据权限
     */
    private String dataAuthLabel;
}
