package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

/**
 * <p>
 *     现金事务分类DTO
 * </p>
 *
 * @Author: bin.xie
 * @Date: Created in 16:09 2018/7/16
 */
@Data
public class CashTransactionClassCO extends DomainObjectDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBookId; // 账套ID

    private String typeCode; // 现金事务类型code(取自syscode)(现金交易事务类型)

    private String typeName; // 现金事务类型name

    private String classCode; // 现金事务分类代码

    private String description; // 现金事务分类名称

    private String setOfBookCode; // 账套code

    private String setOfBookName; // 账套name


    private Boolean assigned; // 是否被分配
}
