package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 韩雪 on 2017/9/7.
 */
@ApiModel(description = "现金事务分类实体类")
@Data
@TableName("csh_transaction_class")
public class CashTransactionClass extends DomainLogicEnable {
    @ApiModelProperty(value = "账套ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_book_id")
    private Long setOfBookId;//账套ID

    @ApiModelProperty(value = "现金事务类型code")
    @TableField(value = "type_code")
    private String typeCode;//现金事务类型code(取自syscode)(现金交易事务类型)

    @ApiModelProperty(value = "现金事务类型name")
    @TableField(exist = false)
    private String typeName;//现金事务类型name

    @ApiModelProperty(value = "现金事务分类代码")
    @TableField(value = "class_code")
    private String classCode;//现金事务分类代码

    @ApiModelProperty(value = "现金事务分类名称")
    @TableField(value = "description")
    private String description;//现金事务分类名称

    @ApiModelProperty(value = "账套code")
    @TableField(exist = false)
    private String setOfBookCode;//账套code

    @ApiModelProperty(value = "账套name")
    @TableField(exist = false)
    private String setOfBookName;//账套name

    //是否被分配
    @ApiModelProperty(value = "是否被分配")
    @TableField(exist = false)
    private Boolean assigned;
}
