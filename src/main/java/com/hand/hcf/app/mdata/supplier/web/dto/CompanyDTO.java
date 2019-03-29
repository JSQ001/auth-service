package com.hand.hcf.app.mdata.supplier.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/14 16:30
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 公司编码
     */
    private String companyCode;

    private String name;

    /**
     * 公司类型名称
     */
    private String companyTypeName;

    /**
     * 账套名称
     */
    private String setOfBooksName;

    /**
     * 是否启用
     */
    private Boolean enabled;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 供应商分配公司主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long infoAssignCompanyId;
}
