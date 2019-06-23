/**
 * <p>
 *     费用小类基类
 * </p>
 *
 * @Author: jsq
 * @Date: 2019/06/19
 */

package com.hand.hcf.app.ant.expenseCategory.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@ApiModel(description = "费用小类")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ant_exp_expense_category")
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategory extends Domain {
    @TableId
    @JsonSerialize(
            using = ToStringSerializer.class   //用字符串类型序列化id
    )
    private Long id;
    /**
     * 费用代码
     */
    @ApiModelProperty(value = "代码")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "code", condition = SqlConditionExpanse.LIKE)
    private String code;
    /**
     * 费用小类类别
     */
    @ApiModelProperty(value = "小类类别")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "category_type")
    private String categoryType;
    /**
     * 费用名称
     */
    @ApiModelProperty(value = "名称")
    @I18nField
    @TableField(value = "name", condition = SqlConditionExpanse.LIKE)
    private String name;


    /**
     * 账套
     */
    @ApiModelProperty(value = "帐套id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_book_id")
    private Long setOfBooksId;

    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称")
    private String setOfBooksName;

    /*
     * 状态
     * */
    @ApiModelProperty(value = "状态")
    @TableField(value = "is_enabled")
    private boolean enabledFlag;

    /*
     * 是否删除
     * */
    @ApiModelProperty(value = "状态")
    @TableField(value = "is_deleted")
    private boolean delete;


    /*
     * 是否采购
     * */
    @ApiModelProperty(value = "是否采购")
    @TableField(value = "is_purchase")
    private boolean purchase;

    /*
     * 所属模块
     * */
    @ApiModelProperty(value = "模块")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "module")
    private String module;

    /*
     * 税率
     * */
    @ApiModelProperty(value = "税率")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "tax")
    private String tax;

    /*
     * 公司
     * */
    @ApiModelProperty(value = "公司")
    @TableField(value = "company_id")
    private String companyId;
    /*
     * 预算部门
     * */
    @ApiModelProperty(value = "预算部门")
    @TableField(value = "budget_dept_id")
    private String budgetDeptId;

    /*
     * 受益部门
     * */
    @ApiModelProperty(value = "受益部门")
    @TableField(value = "benefit_dept_id")
    private String benefitDeptId;

    /*
     * 区域
     * */
    @ApiModelProperty(value = "区域")
    @TableField(value = "adress")
    private String adress;

    /*
     * 明细
     * */
    @ApiModelProperty(value = "明细")
    @TableField(value = "detail")
    private String detail;

    /*
     * 来往
     * */
    @ApiModelProperty(value = "来往")
    @TableField(value = "from")
    private String from;

    /*
     * 项目
     * */
    @ApiModelProperty(value = "项目")
    @TableField(value = "project")
    private String project;
    /*
     * 产品
     * */
    @ApiModelProperty(value = "产品")
    @TableField(value = "product")
    private String product;

    /*
     * 行业
     * */
    @ApiModelProperty(value = "行业")
    @TableField(value = "industry")
    private String industry;

    /*
     * 转账
     * */
    @ApiModelProperty(value = "转账")
    @TableField(value = "transfer")
    private String transfer;
    /*
     * 备用
     * */
    @ApiModelProperty(value = "备用")
    @TableField(value = "standby")
    private String standby;

    @TableField(exist = false)
    private CompanyOrDeptAuthority companyOrDeptAuthority;
}
