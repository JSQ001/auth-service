package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单据授权查询CO
 * @author shouting.cheng
 * @date 2019/4/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeQueryCO {

    /**
     * 单据大类
     */
    String documentCategory;

    /**
     * 单据类型id
     */
    Long formTypeId;

    /**
     * 关联公司id
     */
    List<Long> companyIdList;

    /**
     * 关联部门id
     */
    List<Long> departmentIdList;

    /**
     * 关联人员组id
     */
    List<Long> userGroupIdList;

    /**
     *  当前用户id
     */
    Long currentUserId;
}
