package com.hand.hcf.app.expense.application.web.dto;

import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "申请单类型")
@Data
public class ApplicationTypeDTO implements Serializable {

    /**
     * 申请单类型
     */
    @Valid
    @ApiModelProperty(value = "申请单类型")
    private ApplicationType applicationType;

    /**
     * 适用人员
     */
    @ApiModelProperty(value = "适用人员")
    private List<ApplicationTypeAssignUser> userInfos;

    /**
     * 关联申请类型
     */
    @ApiModelProperty(value = "关联申请类型")
    private List<ApplicationTypeAssignType> expenseTypeInfos;
}
