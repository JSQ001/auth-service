package com.hand.hcf.app.mdata.department.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by yangqi on 2016/11/23.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DepartmentRoleDTO {

    private UUID departmentOid;

    private UUID managerOid;

    private String managerName;

    private UUID chargeManagerOid;

    private String chargeManagerName;

    private UUID hrbpOid;

    private String hrbpName;

    private UUID financialBPOid;

    private String financialBPName;

    private UUID financialAPOid;

    private String financialAPName;

    private UUID legalReviewOid;

    private String legalReviewName;

    private UUID administrativeReviewOid;

    private String administrativeReviewName;

    private UUID financialDirectorOid;

    private String financialDirectorName;

    private UUID viceManagerOid;

    private String viceManagerName;

    private UUID departmentManagerOid;

    private String departmentManagerName;

    private UUID vicePresidentOid;

    private String vicePresidentName;

    private UUID presidentOid;

    private String presidentName;

    private UUID financialManagerOid;

    private String financialManagerName;

    private ZonedDateTime createdDate;


}
