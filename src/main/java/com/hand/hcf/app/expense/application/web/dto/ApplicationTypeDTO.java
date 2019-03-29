package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignUser;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@Data
public class ApplicationTypeDTO implements Serializable {

    /**
     * 申请单类型
     */
    @Valid
    private ApplicationType applicationType;

    /**
     * 适用人员
     */
    private List<ApplicationTypeAssignUser> userInfos;

    /**
     * 关联申请类型
     */
    private List<ApplicationTypeAssignType> expenseTypeInfos;
}
