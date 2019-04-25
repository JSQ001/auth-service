package com.hand.hcf.app.mdata.company.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.company.domain.CompanyAssociateUnit;
import com.hand.hcf.app.mdata.company.dto.CompanyAssociateUnitDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovQueryParams;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovQueryParams;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/15
 */
public interface CompanyAssociateUnitMapper extends BaseMapper<CompanyAssociateUnit> {
    /**
     *  查询公司关联的部门信息
     * @param rowBounds 分页大小
     * @param companyId 公司id
     * @return List<CompanyAssociateUnitDTO>
     */
    List<CompanyAssociateUnitDTO> queryByCompanyId(RowBounds rowBounds,
                                                   @Param("companyId") Long companyId);

    /**
     * 校验公司和部门下是否存在员工
     * @param domain 查询参数
     * @return Integer
     */
    Integer checkEmployee(CompanyAssociateUnit domain);

    /**
     * 分页查询公司可关联的部门
     * @param rowBounds 分页参数
     * @param companyId 公司id
     * @param tenantId  租户id
     * @param codeName 代码/名称
     * @param codeFrom 代码从
     * @param codeTo 代码至
     * @return List<CompanyAssociateUnitDTO>
     */
    List<CompanyAssociateUnitDTO> queryCanAssociate(RowBounds rowBounds,
                                                    @Param("companyId") Long companyId,
                                                    @Param("tenantId") Long tenantId,
                                                    @Param("codeName") String codeName,
                                                    @Param("codeFrom") String codeFrom,
                                                    @Param("codeTo") String codeTo);

    /**
     * 查询公司部门下的员工
     * @param rowBounds 分页参数
     * @param companyId 公司id
     * @param departmentId 部门id
     * @param codeName 员工代码/名称
     * @param dutyCode 职务
     * @param status 状态
     * @return List<ContactDTO>
     */
    List<ContactDTO> queryContact(RowBounds rowBounds,
                                  @Param("companyId") Long companyId,
                                  @Param("departmentId") Long departmentId,
                                  @Param("codeName") String codeName,
                                  @Param("dutyCode") String dutyCode,
                                  @Param("status") Integer status);

    /**
     * 部门lov查询
     * @param rowBounds  分页参数
     * @param queryParams 查询条件
     * @return List<DepartmentLovDTO>
     */
    List<DepartmentLovDTO> queryDepartmentLov(RowBounds rowBounds,
                                              DepartmentLovQueryParams queryParams);
    List<DepartmentLovDTO> queryDepartmentLov(DepartmentLovQueryParams queryParams);
    /**
     * 公司lov查询
     * @param rowBounds  分页参数
     * @param queryParams 查询条件
     * @return List<DepartmentLovDTO>
     */
    List<CompanyLovDTO> queryCompanyLov(RowBounds rowBounds,
                                        @Param("ew") CompanyLovQueryParams queryParams);
    /**
     * 根据部门id 查询启用的账套
     * @param departmentId 部门id
     * @return List<SetOfBooks>
     */
    List<SetOfBooks> listSetOfBooksByDepartmentId(@Param("departmentId") Long departmentId);
}
