package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationType;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *     申请单类型和分配的人员组
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/20
 */
@Data
public class ApplicationTypeAndUserGroupDTO extends ApplicationType {
    private List<Long> userGroupIds;
}
