package com.helioscloud.atlantis.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.DataAuthority;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:45
 * @remark
 */
public interface DataAuthorityMapper extends BaseMapper<DataAuthority>{

    /**
     * 根据公司ID获取下属公司ID
     * @param companyIds
     * @return
     */
    Set<Long> getCompanyChildrenIdByCompanyIds(@Param(value = "companyIds") Set<Long> companyIds);

    /**
     * 获取员工所在部门
     * @param userId
     * @return
     */
    Long getUnitIdByUserId(@Param(value = "userId") Long userId);

    /**
     * 根据部门ID获取下属部门ID
     * @param unitIds
     * @return
     */
    Set<Long> getUnitChildrenIdByUnitIds(@Param(value = "unitIds") Set<Long> unitIds);
}
