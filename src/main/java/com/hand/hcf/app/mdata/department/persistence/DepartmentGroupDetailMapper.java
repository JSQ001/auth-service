package com.hand.hcf.app.mdata.department.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentGroupDetailMapper extends BaseMapper<DepartmentGroupDetail> {
    //输入条件查询
    List<DepartmentGroupDetail> selectByInput(
            @Param("tenantId") Long tenantId,
            @Param("departmentGroupCode") String departmentGroupCode,
            @Param("departmentGroupDescription") String departmentGroupDescription,
            @Param("departmentGroupId") Long departmentGroupId,
            Pagination page
    );

}
