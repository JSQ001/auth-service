package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import com.hand.hcf.app.core.annotation.ExcelDomainField;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * <p>
 * 差旅申请单明细行表
 * </p>
 *
 * @author zhu.zhao
 * @since 2019-03-12
 */
@Data
@TableName("exp_travel_app_line_d")
public class TravelApplicationLineDetail extends DimensionDomain {
    /**
     * 差旅申请单行ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long requisitionLineId;

    /**
     * 公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;

    /**
     * 人员id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long comPeopleId;

    /**
     * 订票人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bookerId;

    /**
     * 备注
     */
    @TableField(value = "description",strategy = FieldStrategy.IGNORED)
    private String description;

    /**
     * 使用状态（y表示预订票；n表示未订票;p表示中间状态）
     */
    private String useFlag;

    /**
     * 申请日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime requisitionDate;

    /**
     * 申请单关闭标志 true 关闭，false 不关闭 默认false
     */
    private ClosedTypeEnum closedFlag;

    /**
     * 申请类型id(即费用类型)
     */
    private Long requisitonTypeId;
}
