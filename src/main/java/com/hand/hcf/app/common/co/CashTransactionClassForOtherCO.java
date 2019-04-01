package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by 韩雪 on 2017/12/22.
 */
@Data
public class CashTransactionClassForOtherCO {
    //账套id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBookId;

    //范围
    //全部：all、已选：selected、未选：notChoose
    @NotNull
    private String range;

    //现金事物分类代码
    private String classCode;

    //现金事物分类名称
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sobPayReqTypeId;//预付款单类型id(新增时不用传，更新时需要传)

    //现金事物分类id集合
    private List<Long> transactionClassIdList;
}
