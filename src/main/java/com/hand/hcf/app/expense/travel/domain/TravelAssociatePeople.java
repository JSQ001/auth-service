package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.BaseObject;
import lombok.Data;

/**
 * <p>
 * 差旅申请单关联人员表
 * </p>
 *
 * @author zhu.zhao
 * @since 2019-03-12
 */
@Data
@TableName("exp_travel_asso_people")
public class TravelAssociatePeople extends BaseObject{
    /**
     * 差旅申请单据头/行表主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assoPkId;

    /**
     * 人员id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long comPeopleId;

    /**
     * 关联类型(H代表差旅申请单头关联出行人表，L代表行程行关联人员表)
     */
    private String assoType;


}
