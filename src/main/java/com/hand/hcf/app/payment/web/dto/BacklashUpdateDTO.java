package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by 刘亮 on 2018/4/4.
 */

@ApiModel(description = "反冲更新dto")
@Data
public class BacklashUpdateDTO {
    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "备注")
    private String remarks;
    @ApiModelProperty(value = "附加id")
    private List<String> attachmentOidS;
}
