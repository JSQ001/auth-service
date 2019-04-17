package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.DepartmentGroupCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyBatchQueryDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.UserInfoDTO;
import com.hand.hcf.app.mdata.contact.service.ContactBankAccountService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroup;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDepartmentCO;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDetail;
import com.hand.hcf.app.mdata.department.dto.DepartmentDTO;
import com.hand.hcf.app.mdata.department.persistence.DepartmentGroupDetailMapper;
import com.hand.hcf.app.mdata.department.persistence.DepartmentGroupMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/*import com.hand.hcf.app.client.org.SysCodeValueCO;*/
/*
import com.hand.hcf.app.mdata.client.department.DepartmentGroupDepartmentCO;
*/

@Service
public class DepartmentGroupService extends ServiceImpl<DepartmentGroupMapper, DepartmentGroup> {

    @Autowired
    private DepartmentGroupMapper departmentGroupMapper;
    @Autowired
    private DepartmentGroupDetailService departmentGroupDetailService;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private DepartmentRoleService departmentRoleService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ContactBankAccountService contactBankAccountService;
    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private DepartmentGroupDetailMapper departmentGroupDetailMapper;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    //部门组新增或修改
    public DepartmentGroup insertOrUpdateDepartmentGroup(DepartmentGroup departmentGroup, UUID userId) {

        departmentGroup.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (departmentGroup.getId() == null) {//新增
            //校验部门组code
            checkDepartmentGroupCode(departmentGroup.getDeptGroupCode());
            //  Code过滤单引号
//            String result = StringEscapeUtils.escapeSql(departmentGroup.getDeptGroupCode());
            //  Code过滤特殊字符
//            String codeResult = StringUtil.filterSpecialCharacters(result);
            //  过滤后重新set
//            departmentGroup.setDeptGroupCode(codeResult);

            departmentGroup.setCreatedDate(ZonedDateTime.now());

            if (StringUtils.isEmpty(departmentGroup.getDescription())) {
                throw new BizException(RespCode.DEPARTMENT_GROUP_DESCRIPTION_IS_NULL_23008);
            }


            departmentGroupMapper.insert(departmentGroup);
            return baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentGroup.getId(), DepartmentGroup.class);
        }
        //修改
        DepartmentGroup oldDepartment = departmentGroupMapper.selectById(departmentGroup.getId());
        if (!oldDepartment.getDeptGroupCode().equals(departmentGroup.getDeptGroupCode())) {
            //  如果不等于，说明修改了code,需要校验
            checkDepartmentGroupCode(departmentGroup.getDeptGroupCode());
            //  Code过滤单引号
//            String result = StringEscapeUtils.escapeSql(departmentGroup.getDeptGroupCode());
            //  Code过滤特殊字符
//            String codeResult = StringUtil.filterSpecialCharacters(result);
            //  过滤后重新set
//            departmentGroup.setDeptGroupCode(codeResult);
        }
        //修改了多语言
        if (!oldDepartment.getDescription().equals(departmentGroup.getDescription())) {
            List<Map<String, String>> nameList = new ArrayList<>();
            nameList.add(new HashMap<String, String>() {{
                put("language", OrgInformationUtil.getCurrentLanguage());
                put("value", departmentGroup.getDescription());
            }});
            departmentGroup.setI18n(new HashMap<String, List<Map<String, String>>>() {{
                put("description", nameList);
            }});
            baseI18nService.insertOrUpdateI18n(departmentGroup.getI18n(), departmentGroup.getClass(), departmentGroup.getId());
        }

        if (null == departmentGroup.getDescription() || "".equals(departmentGroup.getDescription())) {
            throw new BizException(RespCode.ILLEGAL_DEPARTMENT_NAME);
        }

        departmentGroupMapper.updateById(departmentGroup);
//        return baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentGroup.getId(), DepartmentGroup.class);
        return departmentGroup;
    }

    //部门组删除
    @Transactional
    public boolean deleteDepartmentGroupById(Long id) {
        DepartmentGroup departmentGroup = departmentGroupMapper.selectById(id);
        if (departmentGroup == null) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        //删除部门组前，会对应删除部门组明细
        boolean flag = departmentGroupDetailService.deleteDepartmentGroupDetailByGroupId(id);
        departmentGroup.setDeleted(true);
        departmentGroup.setDeptGroupCode(departmentGroup.getDeptGroupCode() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
        int i = departmentGroupMapper.updateById(departmentGroup);
        if (i != 0 && flag) {
            return true;
        }
        return false;
    }


    //根据部门组代码和描述，条件查询
    public Page<DepartmentGroup> selectDepartmentGroupByInput(Page<DepartmentGroup> page, String deptGroupCode, String description, boolean enabled) {

        //在当前租户下，条件查询
//        List<DepartmentGroup> list = departmentGroupMapper.selectPage(page,new EntityWrapper<DepartmentGroup>()
//                                                    .like(!StringUtils.isEmpty(deptGroupCode),"dept_group_code",deptGroupCode)
//                                                    .like(!StringUtils.isEmpty(description),"description",description)
//                                                    .eq("deleted",false)
//                                                    .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
//
//        );
        List<DepartmentGroup> list = departmentGroupMapper.selectGroupByInput(OrgInformationUtil.getCurrentTenantId(), deptGroupCode, description, page);
        List<DepartmentGroup> i18ns = new ArrayList<>();
        list.stream().forEach((DepartmentGroup departmentGroup) -> {
            i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentGroup.getId(), DepartmentGroup.class));
        });

        if (CollectionUtils.isNotEmpty(i18ns)) {
            page.setRecords(i18ns);
        }
        return page;
    }


    //根据部门组代码和描述，条件查询
    public Page<DepartmentGroup> selectDepartmentGroupByInputAndEnabled(Page<DepartmentGroup> page, String deptGroupCode, String description) {

        //在当前租户下，条件查询
//        List<DepartmentGroup> list = departmentGroupMapper.selectPage(page,new EntityWrapper<DepartmentGroup>()
//                                                    .like(!StringUtils.isEmpty(deptGroupCode),"dept_group_code",deptGroupCode)
//                                                    .like(!StringUtils.isEmpty(description),"description",description)
//                                                    .eq("deleted",false)
//                                                    .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
//
//        );
        List<DepartmentGroup> list = departmentGroupMapper.selectGroupByInputAndEnabled(OrgInformationUtil.getCurrentTenantId(), deptGroupCode, description, page);
        List<DepartmentGroup> i18ns = new ArrayList<>();
        list.stream().forEach((DepartmentGroup departmentGroup) -> {
            i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentGroup.getId(), DepartmentGroup.class));
        });

        if (CollectionUtils.isNotEmpty(i18ns)) {
            page.setRecords(i18ns);
        }
        return page;
    }


    //根据部门组代码和描述，条件查询
    public Page<DepartmentGroup> selectDepartmentGroupByInput(Page<DepartmentGroup> page, String deptGroupCode, String description, Boolean enable) {

        //在当前租户下，条件查询
//        List<DepartmentGroup> list = departmentGroupMapper.selectPage(page,new EntityWrapper<DepartmentGroup>()
//                                                    .like(!StringUtils.isEmpty(deptGroupCode),"dept_group_code",deptGroupCode)
//                                                    .like(!StringUtils.isEmpty(description),"description",description)
//                                                    .eq("deleted",false)
//                                                    .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
//
//        );
        List<DepartmentGroup> list = departmentGroupMapper.selectDeptGroupByInput(enable, OrgInformationUtil.getCurrentTenantId(), deptGroupCode, description, page);
        List<DepartmentGroup> i18ns = new ArrayList<>();
        list.stream().forEach((DepartmentGroup departmentGroup) -> {
            i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentGroup.getId(), DepartmentGroup.class));
        });

        if (CollectionUtils.isNotEmpty(i18ns)) {
            page.setRecords(i18ns);
        }
        return page;
    }


    public void checkDepartmentGroupCode(String departmentGroupCode) {
        if (StringUtils.isEmpty(departmentGroupCode)) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_CODE_NULL_23003);
        }

//        String reg2 = "[a-zA-Z0-9_]{1,35}";
        //判断部门组code长度是否超过限制
//        if(!departmentGroupCode.matches(reg2)){//36=50-14(删除的时候需要加占14个字符)
//            throw new BizException(RespCode.DEPARTMENT_GROUP_CODE_LENGTH_MORE_THEN_LIMIT_OR_NOT_INNEGAL_23005);
//        }
        if (departmentGroupCode.length() > 36) {
            throw new BizException("6057003");
            // 验证部门组编码是否包含中文
        } else if (PatternMatcherUtil.isChineseCharacterRegex(departmentGroupCode)) {
            throw new BizException("6057001");
        } else if (!PatternMatcherUtil.validateCodeRegex(departmentGroupCode)) {
            throw new BizException("6057002");
        }
        //如果部门组代码重复，抛异常
        EntityWrapper<DepartmentGroup> wrapper = new EntityWrapper<>();
        wrapper.eq("dept_group_code", departmentGroupCode);
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.eq("deleted", false);
        if (departmentGroupMapper.selectList(wrapper).size() != 0) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_CODE_REPEAT_23004);
        }

    }

    //跟据部门编码departmentCode查询部门组
    public Page<DepartmentGroup> selectDepartmentGroupByDepartmentCode(String departmentCode, Page<DepartmentGroup> page) {
        if (StringUtils.isEmpty(departmentCode)) {
            throw new BizException(RespCode.DEPARTMENT_CODE_NULL_23006);
        }
        page.getRecords();
        List<DepartmentGroup> list = departmentGroupMapper.selectDepartmentGroupByDepartmentCode(departmentCode, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;

    }

    //根据部门组编码和描述,描述查询所有部门信息
    public Page<DepartmentGroupDepartmentCO> selectDepartmentByGroupCode(String deptGroupCode, String description, Long departmentGroupId, Page<DepartmentGroupDepartmentCO> page) {
        if (departmentGroupId == null) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        page.getRecords();
//        if (StringUtils.isEmpty(deptGroupCode) && StringUtils.isEmpty(description)) {
//            List<DepartmentGroupDepartmentDTO> list = departmentGroupMapper.selectNotExit(departmentGroupId, OrgInformationUtil.getCurrentTenantId(), page);
//            if (CollectionUtils.isNotEmpty(list)) {
//                page.setRecords(list);
//            }
//            return page;
//        }
        //条件查询
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectDepartmentByDepartmentGroupCode(deptGroupCode, description, departmentGroupId, OrgInformationUtil.getCurrentTenantId(), page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;


    }

    //查询当前部门组下的部门信息
    public Page<DepartmentGroupDepartmentCO> selectCurrentGroupDepartment(Long departmentGroupId, Page<DepartmentGroupDepartmentCO> page) {
        if (departmentGroupMapper.selectById(departmentGroupId) == null) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        page.getRecords();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectCurrentDepartmentGroupDepartment(departmentGroupId, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }

    //查看当前部门组下的部门---不分页，预算用
    public List<DepartmentGroupDepartmentCO> selectCurrentDepartment(Long departmentGroupId) {
        if (departmentGroupMapper.selectById(departmentGroupId) == null) {
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectCurrentDepartment(departmentGroupId);
        return list;
    }


    //查看当前部门属于哪些部门组下---不分页，预算用
    public List<DepartmentGroup> selectDepartmentGroupByDepartmentId(Long departmentId) {
        List<DepartmentGroup> list = departmentGroupMapper.selectDepartmentGroupByDepartmentId(departmentId);
        return list;
    }


    //根据id查看当前部门组
    public DepartmentGroup selectById(Long id) {
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, DepartmentGroup.class);
    }

    //根据部门组id查看翻译后的信息以及i18n信息
    public DepartmentGroup selectOneTranslatedTableInfoWithI18nById(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, DepartmentGroup.class);
    }


    //过滤当前部门组存在的部门，返回部门列表
    public Page<DepartmentGroupDepartmentCO> selectNotExit(Long departmentGroupId, Page<DepartmentGroupDepartmentCO> page) {
        page.getRecords();
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectNotExit(departmentGroupId, tenantId, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    //根据部门id获取部门明细---外供接口
    public DepartmentGroupDepartmentCO selectBydepartmentId(Long departmentId) {
        return departmentGroupMapper.selectByDepartmentId(departmentId);
    }


    //查看公司和租户，查询部门列表--外供接口
    public Page<DepartmentGroupDepartmentCO> selectByCompanyId(Long companyId, Long tenantId, String deptCode, String deptDescription, Page<DepartmentGroupDepartmentCO> page) {
        page.getRecords();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectByCompanyIdAndTenantId(companyId, tenantId, deptCode, deptDescription, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    //查看公司和租户，查询部门列表--外供接口
    public Page<DepartmentGroupDepartmentCO> selectDepartments(Integer status, Long companyId, Long tenantId, String deptCode, String deptDescription, Page<DepartmentGroupDepartmentCO> page) {
        page.getRecords();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectDepartmentByCompanyIdAndTenantId(status, companyId, tenantId, deptCode, deptDescription, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    @Transactional
    //条件查询公司list
    public Page<CompanyDTO> selectCompanyByInput(String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, List<Long> companyIds, Long setOfBooks, Page page) {
        page.getRecords();

        List<CompanyDTO> list = new ArrayList<>();

        List<Long> listCompanyIds = departmentGroupMapper.selectCompanyByInput(companyCode, companyName, companyCodeFrom, companyCodeTo, companyIds, setOfBooks, page);
        List<CompanyDTO> companys = new ArrayList<>();
        for (Long id : listCompanyIds) {
            CompanyDTO companyDTO = companyService.getByCompanyOid(companyService.findOne(id).getCompanyOid());
            companys.add(companyDTO);
        }

        for (CompanyDTO companyDTO : companys) {
            if (null != companyDTO.getCompanyTypeCode()) {
                SysCodeValueCO sysCodeValue =
                        hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString(),companyDTO.getCompanyTypeCode());
                if (null != sysCodeValue) {
                    companyDTO.setCompanyTypeName(sysCodeValue.getName());
                }
            }
            list.add(companyDTO);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    //根据员工oid查员工所属部门
    public DepartmentGroupDepartmentCO selectByEmpOid(String empOid) {
        if (StringUtils.isEmpty(empOid)) {
            throw new BizException(RespCode.EMP_OID_IS_NULL);
        }
        DepartmentGroupDepartmentCO dto = departmentGroupMapper.selectDepartmentByEmpOid(empOid);
        return dto;
    }

    //根据员工oid查员工所属部门
    public DepartmentGroupDepartmentCO selectByEmployeeId(Long empId) {
        DepartmentGroupDepartmentCO dto = departmentGroupMapper.selectDepartmentByEmployeeId(empId);
        return dto;
    }


    //租户下的所有部门组
    public List<DepartmentGroup> selectByTenantId(Page page) {
        List<DepartmentGroup> list = departmentGroupMapper.selectPage(
                page, new EntityWrapper<DepartmentGroup>()
                        .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                        .eq("deleted", false)
        );
        return list;
    }

    //获取当前租户下的所有部门
    public Page<DepartmentGroupDepartmentCO> selectDepartmentByTenantId(Page<DepartmentGroupDepartmentCO> page) {
        page.getRecords();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectByTenantId(OrgInformationUtil.getCurrentTenantId(), page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    //根据租户，公司，部门code查询部门详细--预算日记账行校验使用
    public List<DepartmentGroupDepartmentCO> selectDepartmentByTenantIdAndCompanyIdAndCode(Long tenantId, Long companyId, String deptCode) {
        return departmentGroupMapper.selectByTenantAndCompanyAndDeptCode(tenantId, companyId, deptCode);
    }

//    //根据成本中心id,成本中心项code,成本中心项name,查询成本中心项详细信息---预算日记账导入校验用
//    public List<CostCenterItem> selectCostCenterItemByInput(Long id,String code,String name){
//        return  costCenterItemService.selectList(
//            new EntityWrapper<CostCenterItem>()
//                .eq("cost_center_id",id)
//                .eq("code",code)
//                .eq("name",name)
//                .eq("enabled",true)
//        );
//    }


    //预算日记账导入校验用
    public List<Company> selectCompanyByInput(Long setOfBooksId, String code, String name) {
        return departmentGroupMapper.selectCostCompanyByInput(setOfBooksId, code, name);
    }

    //预算日记账导入校验用--批量
    public List<Company> selectCompanyByInputBatch(CompanyBatchQueryDTO dto) {
        List<Company> companies = new ArrayList<>();
        dto.getCodes().stream().forEach(str -> {
            companies.add(departmentGroupMapper.selectCostCompanyByIdAndCode(dto.getSetOfBooksId(), str));
        });
        return companies;
    }

    public List<Long> selectUserGroupBySetOfBooksIdAndEnable(Long setOfBooksId, Boolean enabled) {
        return departmentGroupMapper.selectUserGroupBySetOfBooksIdAndEnable(setOfBooksId, enabled);
    }


    //条件查询租户下已经启用的部门，code排序
    public Page<DepartmentGroupDepartmentCO> selectDepartmentByTenantIdAndEnabled(Long tenantId, String deptCode, String name, Boolean leafEnable, Page<DepartmentGroupDepartmentCO> page) {
        page.getRecords();
        List<DepartmentGroupDepartmentCO> list = departmentGroupMapper.selectDepartmentByTenantIdAndEnabled(tenantId, deptCode, name, leafEnable, page);
        list.stream().map(u -> {
            Department one = departmentService.selectOnebyId(u.getDepartmentId());
            if (one != null) {
                u.setName(one.getName());
            }
            return u;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }


    //条件查询租户下已经启用的部门，code排序
    public Page<DepartmentDTO> selectDepartmentsByTenantIdAndEnabled(Long tenantId, String deptCode, String name, Page<DepartmentDTO> page) {
        page.getRecords();
        List<DepartmentDTO> list = departmentGroupMapper.selectDepartmentsByTenantIdAndEnabled(tenantId, deptCode, name, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }

    //根据租户和部门code查询部门信息
    public DepartmentGroupDepartmentCO selectByDepartmentCodeAndTenantId(String code, Long tenantId) {
        return departmentGroupMapper.selectDepartmentByCodeAndTenantId(code, tenantId);
    }


    //根据公司id和部门id获取这个交集下的员工
    public Page<UserInfoDTO> selectUsersByCompanyAndDepartment1(Long companyId, Long departmentId, String userCode, String userName, String companyName, Page<UserInfoDTO> page) {
        page.getRecords();
        List<UserInfoDTO> list = departmentGroupMapper.selectUserIdsByCompanyAndDepartmentId(companyId, departmentId, OrgInformationUtil.getCurrentTenantId(), userCode, userName, companyName, page);
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }
    /*
     * 根据公司id和用户name,查询这个公司的员工银行信息
     * */
    public List<ContactBankAccountDTO> selectContactBankAccountByCompanyId(Long companyId, String name) {
        List<ContactBankAccount> list = departmentGroupMapper.selectContactBankAccountByUserName(name, companyId);
        List<ContactBankAccountDTO> contactBankAccountDTOS = new ArrayList<>();
        list.forEach(
                contactBankAccount -> {
                    contactBankAccountDTOS.add(contactBankAccountService.ContactBankAccountToContactBankAccountDTO(contactBankAccount));
                }
        );
        return contactBankAccountDTOS;
    }


    /*根据员工id查询员工可用的银行账户*/
    public List<ContactBankAccountDTO> selectContactBankAccountDTOByUserOid(String userOid) {
        List<ContactBankAccount> list = departmentGroupMapper.selectContactBankAccountDTOByUserOid(userOid);
        List<ContactBankAccountDTO> contactBankAccountDTOS = new ArrayList<>();
        list.forEach(
                contactBankAccount -> {
                    contactBankAccountDTOS.add(contactBankAccountService.ContactBankAccountToContactBankAccountDTO(contactBankAccount));
                }
        );
        return contactBankAccountDTOS;
    }

    /*根据员工id查询员工可用的银行账户*/
    public List<ContactBankAccountDTO> selectContactBankAccountDTOByUserId(Long userId) {
        List<ContactBankAccount> list = departmentGroupMapper.selectContactBankAccountDTOByUserId(userId);
        List<ContactBankAccountDTO> contactBankAccountDTOS = new ArrayList<>();
        list.forEach(
                contactBankAccount -> {
                    contactBankAccountDTOS.add(contactBankAccountService.ContactBankAccountToContactBankAccountDTO(contactBankAccount));
                }
        );
        return contactBankAccountDTOS;
    }

    @Transactional
    public List<DepartmentDTO> findByParentDepartmentOidAndStatusNotAndNameLike(Long id, String name, Integer status) {
        // List<Department> list = departmentRepository.findByParentIdAndStatusNotAndNameLike(id,1001,"%"+name+"%");
        // art_department.status 101表示启用, 102表示禁用, 这里sql执行为 where status <> ?
        List<Department> list;
        if (status == null) {
            list = departmentService.findByParentIdAndStatusNotAndNameLike(id, 101, name);
        }
        list = departmentService.findByParentIdAndStatusNotAndNameLike(id, status, name);
        List<DepartmentDTO> departmentDTOS = new ArrayList<>();
        for (Department department : list) {
            DepartmentDTO departmentDTO = departmentService.departmentToDepartmentDTO(department);
            departmentDTO.setHasChildrenDepartments(department.getChildren().size() > 0);
            departmentDTO.setDepartmentRole(departmentRoleService.getDepartmentRoleDTOByDepartmentId(department.getId()));
            departmentDTO.setI18n(department.getI18n());
            departmentDTOS.add(departmentDTO);
        }
        return departmentDTOS;
    }

    public List<Long> selectUserGroupBySetOfBooksIdAndEnableAndName(Long setOfBooksId, Boolean enabled, String name) {
        return departmentGroupMapper.selectUserGroupBySetOfBooksIdAndEnableAndName(setOfBooksId, enabled, name);
    }


    //根据条件获取账套下的公司
    @Transactional
    public Page<CompanyDTO> getCompanyByCond(Long setOfBooksId, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, Page page) {
        List<CompanyDTO> list = new ArrayList<>();
        page.getRecords();

        List<Long> companyIdList = departmentGroupMapper.getCompanyByCond(setOfBooksId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);

        for (Long id : companyIdList) {
            CompanyDTO companyDTO = companyService.getByCompanyOid(companyService.findOne(id).getCompanyOid());
            list.add(companyDTO);
        }

        for (CompanyDTO companyDTO : list) {
            if (null != companyDTO.getCompanyTypeCode()) {
                SysCodeValueCO sysCodeValue =
                        hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString(),companyDTO.getCompanyTypeCode());
                if (null != sysCodeValue) {
                    companyDTO.setCompanyTypeName(sysCodeValue.getName());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 根据用户ID，获取用户的名称和代码信息
     *
     * @param userIds
     * @return
     */
    public List<UserInfoDTO> getUserInfoListByIds(@Param("userIds") List<Long> userIds) {
        return departmentGroupMapper.getUserInfoListByIds(userIds);
    }

    @Transactional
    //条件查询公司list
    public Page<CompanyDTO> selectAccCompanyByInput(String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, List<Long> companyIds, Long setOfBooks, Page page) {
        page.getRecords();

        List<CompanyDTO> list = new ArrayList<>();

        List<Long> listCompanyIds = departmentGroupMapper.selectAccCompanyByInput(companyCode, companyName, companyCodeFrom, companyCodeTo, companyIds, setOfBooks, page);
        List<CompanyDTO> companys = new ArrayList<>();
        for (Long id : listCompanyIds) {
            CompanyDTO companyDTO = companyService.getByCompanyOid(companyService.findOne(id).getCompanyOid());
            companys.add(companyDTO);
        }

        for (CompanyDTO companyDTO : companys) {
            if (null != companyDTO.getCompanyTypeCode()) {
                SysCodeValueCO sysCodeValue =
                        hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString(),companyDTO.getCompanyTypeCode());
                if (null != sysCodeValue) {
                    companyDTO.setCompanyTypeName(sysCodeValue.getName());
                }
            }
            list.add(companyDTO);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }

    /******************************* 以下为对外接口 **********************************/
    public List<DepartmentGroupCO> listByDepartmentIdAndGroupStatus(Long departmentId) {
        List<DepartmentGroup> departmentGroupList = departmentGroupMapper.selectDepartmentGroupByDepartmentId(departmentId);
        return mapperFacade.mapAsList(departmentGroupList, DepartmentGroupCO.class);
    }

    public List<DepartmentGroupDepartmentCO> listDepartmentBydepartmentGroupId(Long departmentGroupId) {
        List<DepartmentGroupDepartmentCO> departmentGroupDepartmentCOList = departmentGroupMapper.selectCurrentDepartment(departmentGroupId);
        return departmentGroupDepartmentCOList;
    }

    public List<DepartmentGroupCO> listDepartmentsByTenantId() {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<DepartmentGroup> departmentGroups = departmentGroupMapper.selectList(
                new EntityWrapper<DepartmentGroup>()
                        .eq("tenant_id", tenantId)
                        .eq("deleted", false)
                        .eq("enabled", true)
        );
        List<DepartmentGroupCO> departmentGroupCOList = new ArrayList<>();
        departmentGroups.forEach(
                departmentGroup -> {
                    DepartmentGroupCO departmentGroupCO = new DepartmentGroupCO();
                    departmentGroupCO.setId(departmentGroup.getId());
                    departmentGroupCO.setDescription(departmentGroup.getDescription());
                    departmentGroupCO.setDepartmentIdList(
                            departmentGroupDetailMapper.selectList(
                                    new EntityWrapper<DepartmentGroupDetail>()
                                            .eq("department_group_id", departmentGroup.getId())
                                            .eq("deleted", false)
                                            .eq("enabled", true)
                            ).stream().map(DepartmentGroupDetail::getDepartmentId).collect(Collectors.toList())
                    );

                    departmentGroupCOList.add(departmentGroupCO);
                }
        );
        return departmentGroupCOList;
    }

    public List<DepartmentGroupCO> listDepartmentsByGroupIds(List<Long> groupIds) {
        List<DepartmentGroupCO> list = new ArrayList<>();
        for (int i = 0; i < groupIds.size(); i++) {
            DepartmentGroupCO departmentGroupCO = new DepartmentGroupCO();
            departmentGroupCO.setId(groupIds.get(i));
            departmentGroupCO.setDescription(this.selectById(groupIds.get(i)).getDescription());
            departmentGroupCO.setDepartmentIdList(selectCurrentDepartment(groupIds.get(i)).stream().map(DepartmentGroupDepartmentCO::getDepartmentId).collect(Collectors.toList()));
            list.add(departmentGroupCO);
        }
        return list;
    }

}
