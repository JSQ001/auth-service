package com.hand.hcf.app.mdata.department.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DepartmentMapper extends BaseMapper<Department> {
    List<DepartmentDTO> selectRootDepartment(DepartmentQueryDTO departmentDTO);

    List<DepartmentDTO> selectRootDepartment(Pagination page, DepartmentQueryDTO departmentDTO);

    /**
     * 查询财务角色下的部门
     *
     * @param userOid
     * @param tenantId
     * @param keyword
     * @return
     */
    List<DepartmentTreeDTO> selectFinanceRoleAvailableDepartments(@Param(value = "userOid") UUID userOid, @Param(value = "tenantId") Long tenantId, @Param(value = "keyword") String keyword, @Param(value = "language") String language);

    List<Department> findDepartmentsByKeyword(@Param(value = "tenantId") Long tenantId,
                                              @Param("keyword") String keyword,
                                              @Param("departmentStatus") Integer departmentStatus);

    List<DepartmentTreeDTO> findTenantAllDepartment(@Param(value = "code") String code,
                                                    @Param(value = "name") String name,
                                                    @Param(value = "tenantId") Long tenantId,
                                                    @Param("status") Integer status,
                                                    @Param(value = "language") String language);

    Department selectByDepartmentOidAndStatus(@Param("departmentOid") UUID departmentOid, @Param("status") Integer status);

    Department selectByDepartmentOidAndStatusNot(@Param("departmentOid") UUID departmentOid, @Param("status") Integer status);

    List<Department> selectByDepartmentOidInAndStatus(@Param("departmentOids") List<UUID> departmentOids, @Param("status") Integer status);

    List<Department> selectByDepartmentOidInAndStatusSimple(@Param("departmentOids") List<UUID> departmentOids, @Param("status") Integer status);

    List<Department> selectByDepartmentOidInAndStatusNot(@Param("departmentOids") List<UUID> departmentOids, @Param("status") Integer status);

    List<Department> selectByTenantIdAndPathLikeAndStatus(@Param(value = "tenantId") Long tenantId,
                                                          @Param("path") String path,
                                                          @Param("status") Integer status);

    Department selectByPathAndTenantIdAndStatus(@Param("path") String path,
                                                @Param(value = "tenantId") Long tenantId,
                                                @Param("status") Integer status);

    Department selectByPathAndTenantIdAndStatusNot(@Param("path") String path,
                                                   @Param(value = "tenantId") Long tenantId,
                                                   @Param("status") Integer status);

    List<Department> selectByParentDepartmentOidAndStatus(@Param("parentDepartmentOid") UUID parentDepartmentOid,
                                                          @Param("status") Integer status);

    List<Department> selectByParentDepartmentOidAndStatusNot(@Param("parentDepartmentOid") UUID parentDepartmentOid,
                                                             @Param("status") Integer status);

    Department selectByTenantIdAndDepartmentCode(@Param(value = "tenantId") Long tenantId,
                                                 @Param(value = "departmentCode") String departmentCode);

    Long selectTenantDepartmentCount(Long tenantId);

    List<Department> selectByTenantIdAndStatusNotPage(@Param(value = "tenantId") Long tenantId,
                                                      @Param("status") Integer status,
                                                      Page myBatisPage);

    List<Department> selectByTenantIdAndStatus(@Param(value = "tenantId") Long tenantId,
                                               @Param("status") Integer status);

    List<Department> selectByTenantIdAndStatusNot(@Param(value = "tenantId") Long tenantId,
                                                  @Param("status") Integer status);

    Department selectOneSimpleById(@Param("departmentId") Long departmentId);

    List<DepartmentDTO> selectDepartmentCode(Long currentTenantId);

    void updateDepartmentCode(@Param("id") Long id, @Param("departmentCode") String departmentCode);

    Department findByDepartmentCodeAndTenantId(@Param("departmentCode") String departmentCode,
                                               @Param("tenantId") Long tenantId,
                                               @Param("status") Integer status);

    /**
     * 查询指定人员负责的部门
     *
     * @param managerId
     * @param status
     * @return
     */
    List<Department> selectDepartmentByManageId(@Param("managerId") Long managerId,
                                                @Param("status") Integer status);

    List<DepartmentInfo> getAllDepartment(Pagination page);

    List<Department> listByUserOid(@Param("userOid") UUID userOid);

    void updateChildrenDepartmentPath(@Param("newPath") String newPath,
                                      @Param("oldPath") String oldPath,
                                      @Param("oldPathLength") Integer oldPathLength,
                                      @Param("tenantId") Long tenantId);

    List<UUID> findOidByDepartmentOids(@Param("companyId") Long companyId,
                                       @Param("departmentOids") List<UUID> departmentOids);

    /**
     * 根据部门ID获取下属部门ID
     * @param unitIds
     * @return
     */
    Set<Long> getUnitChildrenIdByUnitIds(@Param(value = "unitIds") Set<Long> unitIds);

    List<DepartmentUserSummaryDTO> getDepartmentUsers(@Param("tenantId") Long tenantId,
                                                      @Param("departmentOid") UUID departmentOid);
}
