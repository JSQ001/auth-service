package com.hand.hcf.app.workflow.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by 韩雪 on 2018/3/8.
 */
@Data
public class ApprovalFormForOtherRequestDTO {
    //账套id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBookId;


    //范围
    //全部：all、已选：selected、未选：notChoose
    @NotNull
    private String range;

    //申请单类型代码
    private String formCode;

    //申请单类型名称
    private String formName;

    //申请单类型id集合
    private List<Long> idList;


    //公司id
    private Long companyId;

    //公司name
    private String companyName;
}
