package com.hand.hcf.app.mdata.dimension.dto;

import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DimensionItemRequestDTO {
    /*维值*/
    @NotNull
    private DimensionItem dimensionItem;

    /*关联部门或人员组id集合*/
    private List<Long> departmentOrUserGroupIdList;

    /*关联部门或人员组的对象集合*/
    private List<DepartmentOrUserGroupReturnDTO> departmentOrUserGroupList;
}
