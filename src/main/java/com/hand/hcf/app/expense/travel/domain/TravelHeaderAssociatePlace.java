package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.BaseObject;
import lombok.Data;

/**
 * <p>
 * 差旅申请单头关联地点表
 * </p>
 *
 * @author zhu.zhao
 * @since 2019-03-12
 */
@Data
@TableName("exp_travel_h_asso_place")
public class TravelHeaderAssociatePlace extends BaseObject {
    /**
     * 单据头表主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long requisitionHeaderId;

    /**
     * 地点id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long placeId;

    /**
     * 地点类型(F代表出发地，T代表目的地)
     */
    private String placeType;





}
