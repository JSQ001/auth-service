package com.hand.hcf.app.mdata.contact.dto;

import com.hand.hcf.app.core.domain.BaseI18nDomain;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserGroupDTO extends BaseI18nDomain {


    private Long id ;
    UUID userGroupOid;
    UUID companyOid;
    @NotEmpty
    String name;
    Boolean enabled;

    private String comment;

    private List<UserDTO> userSummaryDTOs;

    private String code;

    private String type;

    private List<ConditionViewDTO> conditionViewDTOS = new ArrayList<>();


    private Long tenantId;

    private String employeeId;

}
