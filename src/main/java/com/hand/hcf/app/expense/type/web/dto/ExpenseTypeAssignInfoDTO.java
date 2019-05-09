package com.hand.hcf.app.expense.type.web.dto;

import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@ApiModel(description = "�������ͷ�����Ϣ")
@Data
public class ExpenseTypeAssignInfoDTO implements Serializable {
    @ApiModelProperty(value = "�����û�")
    private List<ExpenseTypeAssignUser> assignUsers;
    @ApiModelProperty(value = "���乫˾")
    private List<ExpenseTypeAssignCompany> assignCompanies;
    @ApiModelProperty(value = "ȫ��˾��־")
    private Boolean allCompanyFlag;
    @ApiModelProperty(value = "��������")
    private Integer applyType;
}
