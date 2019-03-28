package com.hand.hcf.app.mdata.department.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDepartmentCO;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.dto.UserInfoDTO;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroup;
import com.hand.hcf.app.mdata.department.dto.DepartmentDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface DepartmentGroupMapper extends BaseMapper<DepartmentGroup> {
    //跟据部门编码departmentCode查询部门组
    List<DepartmentGroup> selectDepartmentGroupByDepartmentCode(
            @Param("departmentCode") String departmentCode,
            Pagination page
    );
    //根据部门id查询部门所属的部门组  ---预算用，不分页
    List<DepartmentGroup> selectDepartmentGroupByDepartmentId(
            @Param("departmentId") Long departmentId
    );

    //跟据部门组编码查询所有部门DTO
    List<DepartmentGroupDepartmentCO> selectDepartmentByDepartmentGroupCode(@Param("deptGroupCode") String deptGroupCode,
                                                                            @Param("description") String description,
                                                                            @Param("departmentGroupId") Long departmentGroupId,
                                                                            @Param("tenantId") Long tenantId,
                                                                            Pagination page);

    //根据部门组id分页查询当前部门组的部门
    List<DepartmentGroupDepartmentCO> selectCurrentDepartmentGroupDepartment(@Param("departmentGroupId") Long departmentGroupId,
                                                                             Pagination page);

    //根据部门组id查询部门组下的部门---不分页，预算用
    List<DepartmentGroupDepartmentCO> selectCurrentDepartment(@Param("departmentGroupId") Long departmentGroupId);



    //根据部门组code分页查询部门
    List<DepartmentGroupDepartmentCO> selectDepartmentByGroupCode(@Param("deptGroupCode") String deptGroupCode,
                                                                  @Param("tenantId") Long tenantId,
                                                                  Pagination page);
    //根据部门组id查询不在当前部门组下的部门
    List<DepartmentGroupDepartmentCO> selectNotExit(@Param("departmentGroupId") Long departmentGroupId,
                                                    @Param("tenantId") Long tenantId,
                                                    Pagination page);

    //根据部门id查询部门明细
    DepartmentGroupDepartmentCO selectByDepartmentId(@Param("departmentId") Long departmentId);

    //根据公司id和租户id查询部门列表
    List<DepartmentGroupDepartmentCO> selectByCompanyIdAndTenantId(@Param("companyId") Long companyId,
                                                                   @Param("tenantId") Long tenantId,
                                                                   @Param("deptCode") String deptCode,
                                                                   @Param("deptDescription") String description,
                                                                   Pagination page
    );
    //根据公司id和租户id查询部门列表
    List<DepartmentGroupDepartmentCO> selectDepartmentByCompanyIdAndTenantId(
            @Param("status") Integer status,
            @Param("companyId") Long companyId,
            @Param("tenantId") Long tenantId,
            @Param("deptCode") String deptCode,
            @Param("deptDescription") String description,
            Pagination page
    );


    //根据公司code,公司名称等查询公司list
    List<Long> selectCompanyByInput(
            @Param("companyCode") String companyCode,
            @Param("companyName") String companyName,
            @Param("companyCodeFrom") String companyCodeFrom,
            @Param("companyCodeTo") String companyCodeTo,
            @Param("companyIds") List<Long> companyIds,
            @Param("setOfBooks") Long setOfBooks,
            Pagination pagination
    );


    //根据员工Oid查询部门信息
    DepartmentGroupDepartmentCO selectDepartmentByEmpOid(@Param("empOid") String empOid);


    //查询当前租户下启用的所有部门（分页）
    List<DepartmentGroupDepartmentCO> selectByTenantId(@Param("tenantId") Long tenantId, Pagination pagination);

    List<DepartmentGroup> selectGroupByInput(@Param("tenantId") Long tenantId,
                                             @Param("deptGroupCode") String deptGroupCode,
                                             @Param("description") String description,
                                             Pagination page
    );

    List<DepartmentGroup> selectGroupByInputAndEnabled(@Param("tenantId") Long tenantId,
                                                       @Param("deptGroupCode") String deptGroupCode,
                                                       @Param("description") String description,
                                                       Pagination page
    );
    List<DepartmentGroup> selectDeptGroupByInput(
            @Param("enable") Boolean enable,
            @Param("tenantId") Long tenantId,
            @Param("deptGroupCode") String deptGroupCode,
            @Param("description") String description,
            Pagination page
    );


    //根据条件查询部门
    List<DepartmentGroupDepartmentCO> selectByTenantAndCompanyAndDeptCode(
            @Param("tenantId") Long tenantId,
            @Param("companyId") Long companyId,
            @Param("deptCode") String deptCode
    );


    List<Company> selectCostCompanyByInput(@Param("setOfBooksId") Long setOfBooksId,
                                           @Param("companyCode") String code,
                                           @Param("companyName") String name);

    Company selectCostCompanyByIdAndCode(@Param("setOfBooksId") Long setOfBooksId,
                                         @Param("companyCode") String code);

    /*条件查询租户下所有已经启用的部门*/
    List<DepartmentGroupDepartmentCO> selectDepartmentByTenantIdAndEnabled(
            @Param("tenantId") Long tenantId,
            @Param("deptCode") String deptCode,
            @Param("name") String name,
            @Param("leafEnable") Boolean leafEnable,
            Pagination page
    );

    /*条件查询租户下所有已经启用的部门*/
    List<DepartmentDTO> selectDepartmentsByTenantIdAndEnabled(
            @Param("tenantId") Long tenantId,
            @Param("deptCode") String deptCode,
            @Param("name") String name,
            Pagination page
    );

    DepartmentGroupDepartmentCO selectDepartmentByCodeAndTenantId(
            @Param("code") String code,
            @Param("tenantId") Long tenantId
    );

    List<UserInfoDTO> selectUserIdsByCompanyAndDepartmentId(
            @Param("companyId") Long companyId,
            @Param("departmentId") Long departmentId,
            @Param("tenantId") Long tenantId,
            @Param("userCode") String userCode,
            @Param("userName") String userName,
            @Param("companyName") String companyName,
            Pagination page
    );


    List<UserInfoDTO> selectUserByCompanyAndDepartmentIdAndEmpId(
            @Param("companyId") Long companyId,
            @Param("departmentId") Long departmentId,
            @Param("empId") String empId
    );


    List<ContactBankAccount> selectContactBankAccountByUserName(
            @Param("userName") String userName,
            @Param("companyId") Long companyId
    );

    List<ContactBankAccount> selectContactBankAccountDTOByUserOid(@Param("userOid") String userOid);

    List<ContactBankAccount> selectContactBankAccountDTOByUserId(@Param("userId") Long userId);

    List<Long> selectUserGroupBySetOfBooksIdAndEnableAndName(
            @Param("setOfBooksId") Long setOfBooksId,
            @Param("enable") Boolean enable,
            @Param("name") String name

    );


    List<UUID> selectUserByKeyAndCompanyIdPage(
            @Param("tenantId") Long tenantId,
            @Param("key") String key,
            @Param("companyId") Long companyId,
            Pagination page
    );

    //根据租户和用户姓名或工号查询userOid
    List<UUID> selectUserByKey(
            @Param("tenantId") Long tenantId,
            @Param("key") String key
    );


    List<Long> selectUserGroupBySetOfBooksIdAndEnable(
            @Param("setOfBooksId") Long setOfBooksId,
            @Param("enable") Boolean enable

    );

    List<UUID> selectUserByKeyPage(
            @Param("tenantId") Long tenantId,
            @Param("key") String key,
            @Param("setOfBooksId") Long setOfBooksId,
            Pagination page
    );

    List<UUID> selectUserByKeyOrNamePage(
            @Param("tenantId") Long tenantId,
            @Param("key") String key,
            @Param("name") String name,
            @Param("setOfBooksId") Long setOfBooksId,
            Pagination page
    );


    //根据条件获取账套下的公司
    List<Long> getCompanyByCond(
            @Param("setOfBooksId") Long setOfBooksId,
            @Param("companyCode") String companyCode,
            @Param("companyName") String companyName,
            @Param("companyCodeFrom") String companyCodeFrom,
            @Param("companyCodeTo") String companyCodeTo,
            Pagination pagination
    );
    List<UserInfoDTO> getUserInfoListByIds(@Param("userIds") List<Long> userIds);

    List<Long> selectAccCompanyByInput(@Param("companyCode") String companyCode,
                                       @Param("companyName") String companyName,
                                       @Param("companyCodeFrom") String companyCodeFrom,
                                       @Param("companyCodeTo") String companyCodeTo,
                                       @Param("companyIds") List<Long> companyIds,
                                       @Param("setOfBooks") Long setOfBooks,
                                       Pagination pagination);

    /**
     * 根据员工id查询部门信息
     * @param empId
     * @return
     */
    DepartmentGroupDepartmentCO selectDepartmentByEmployeeId(@Param("empId") Long empId);

}
