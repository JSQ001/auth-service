package com.hand.hcf.app.mdata.responsibilityCenter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityDefaultDTO;

import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLovDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResponsibilityCenterMapper extends BaseMapper<ResponsibilityCenter> {
    /**
     * 获取当前所选账套下所有启用的责任中心，如果选择了公司，则只能选到分配给此公司的责任中心
     * @param setOfBooksId 账套id
     * @param info
     * @param codeFrom
     * @param codeTo
     * @param page
     * @return
     */
    List<ResponsibilityCenter> pageByResponsibilityCenterConditionByIds(@Param("setOfBooksId") Long setOfBooksId,
                                                                        @Param("info") String info,
                                                                        @Param("codeFrom") String codeFrom,
                                                                        @Param("codeTo") String codeTo,
                                                                        @Param("resCenterIdList") List<Long> resCenterIdList,
                                                                        @Param("enabled") Boolean enabled,
                                                                        Page page);
    /**
     * 分页查询责任中心
     *
     * @param keyword 责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param setOfBooksId 账套Id
     * @param responsibilityCenterCode 责任中心代码
     * @param responsibilityCenterName 责任中心名称
     * @param enabled 启用禁用
     * @param page 分页
     * @return
     */
    List<ResponsibilityCenter> pageResponsibilityCenterBySetOfBooksId(@Param("keyword") String keyword,
                                                                      @Param("codeFrom") String codeFrom,
                                                                      @Param("codeTo") String codeTo,
                                                                      @Param("setOfBooksId") Long setOfBooksId,
                                                                      @Param("responsibilityCenterCode") String responsibilityCenterCode,
                                                                      @Param("responsibilityCenterName") String responsibilityCenterName,
                                                                      @Param("enabled") Boolean enabled,
                                                                      @Param("dataAuthLabel") String dataAuthLabel,
                                                                      Page page);

    /**
     * 根据部门和公司查询可用的责任中心
     * @param rowBounds 分页参数
     * @param companyId 公司id
     * @param departmentId 部门id
     * @param code 代码
     * @param name 名称
     * @param id id
     * @param enabled 是否启用
     * @param codeName 代码/名称
     * @return List<ResponsibilityLov>
     */
    List<ResponsibilityLovDTO> pageByCompanyAndDepartment(RowBounds rowBounds,
                                                          @Param("companyId") Long companyId,
                                                          @Param("departmentId")Long departmentId,
                                                          @Param("code") String code,
                                                          @Param("name") String name,
                                                          @Param("enabled") Boolean enabled,
                                                          @Param("codeName") String codeName,
                                                          @Param("id") Long id);

    /**
     * 获取公司部门默认的责任中心
     * @param companyId 公司id
     * @param departmentIds
     * @return List<ResponsibilityDefaultDTO>
     */
    List<ResponsibilityDefaultDTO> listCompanyDepartmentDefaultCenter(@Param("companyId") Long companyId,
                                                                      @Param("departmentIds") List<Long> departmentIds);

    /**
     * 查询账套下已启用或者分配给指定公司的责任中心
     * @param setOfBooksId
     * @param companyId
     * @param centerIds
     * @return
     */
    List<ResponsibilityCenter> listEnabledBySobIdAndCompanyId(@Param("setOfBooksId") Long setOfBooksId,
                                                              @Param("companyId") Long companyId,
                                                              @Param("centerIds") List<Long> centerIds);
}
