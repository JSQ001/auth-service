package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/20
 */
@Data
public class OrganizationUserCO implements Serializable {
    private Long setOfBookId;//账套ID
    private Long userId;//用户ID
    private UUID userOid;//用户OID
    private Long companyId;//公司ID
    private UUID companyOid;//公司OID
    private Long departmentId;//部门ID
    private UUID departmentOid;//部门OID
    private List<Long> userGroupIds;//员工组ID
}
