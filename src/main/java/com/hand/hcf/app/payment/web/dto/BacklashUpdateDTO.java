package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * Created by 刘亮 on 2018/4/4.
 */
@Data
public class BacklashUpdateDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String remarks;
    private List<String> attachmentOidS;
}
