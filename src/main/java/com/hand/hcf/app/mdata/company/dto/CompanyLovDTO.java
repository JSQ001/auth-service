package com.hand.hcf.app.mdata.company.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/16
 */
@Data
@ApiModel("公司Lov对象")
@AllArgsConstructor
@NoArgsConstructor
public class CompanyLovDTO implements Serializable {

    @ApiModelProperty("账套id")
    private Long setOfBooksId;
    @ApiModelProperty("账套名称")
    private String setOfBooksName;
    @ApiModelProperty("法人实体id")
    private Long legalEntityId;
    @ApiModelProperty("法人实体名称")
    private String legalEntityName;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("公司代码")
    private String companyCode;
    @ApiModelProperty("公司id")
    private Long id;
    @ApiModelProperty("公司类型")
    private String companyTypeName;
    @ApiModelProperty("是否启用")
    private Boolean enabled;
    @ApiModelProperty("公司oid")
    private String companyOid;
    @ApiModelProperty("租户id")
    private Long tenantId;
    @ApiModelProperty("公司业务类型代码")
    private String companyTypeCode;

}
