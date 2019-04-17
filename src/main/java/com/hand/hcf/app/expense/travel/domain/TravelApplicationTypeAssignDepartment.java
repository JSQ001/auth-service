package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 差旅申请单类型关联部门
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("exp_travel_app_type_ass_dep")
public class TravelApplicationTypeAssignDepartment extends Domain {
    /**
     * 差旅申请单类型ID
     */
    private Long typeId;
    /**
     * 部门ID
     */
    private Long departmentId;
}
