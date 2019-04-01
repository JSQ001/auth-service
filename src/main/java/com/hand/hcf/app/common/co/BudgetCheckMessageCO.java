package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by kai.zhang on 2017-09-27.
 */
@Data
public class BudgetCheckMessageCO {
    @Valid
    @NotNull
    private List<BudgetReserveCO> budgetReserveDtoList;      //预算保留行数据
    private String ignoreWarningFlag;      //是否忽略警告
    private String includeReleaseFlag;       //是否包含释放信息
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;  //租户id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;  //账套id

    private String ignoreTransactionFlag;       //是否忽略事务：Y表示忽略事务，即只校验，不生成占用数据
}
