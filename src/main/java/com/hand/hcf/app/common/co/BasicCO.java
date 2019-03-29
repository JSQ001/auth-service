package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/1/5 16:20
 * @remark 设置类基础数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicCO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Object id;

    private String code;

    private String name;

    private Boolean enabled;
}
