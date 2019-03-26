package com.hand.hcf.app.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/4/14 13:20
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationVendorCompanyCO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long vendorInfoId;

    /**
     * @JsonSerialize(using = ToStringSerializer.class)
     * artemis 服务需要强转Integer,序列化String 再强转 Integer报错 java.lang.ClassCastException
     */
    private Long companyId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private Boolean enabled;

    private Set<String> companyIDs;

    private Set<String> infoIDs;
}
