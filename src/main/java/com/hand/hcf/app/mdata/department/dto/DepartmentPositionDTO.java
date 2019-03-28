package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
* Created by Transy on 2017-10-27.
*/
@Data
public class DepartmentPositionDTO implements Serializable {

    private Long id;

    private Long tenantId;
    private String positionCode;
    private String positionName;
    private UUID userOid;
    private String userName;
    private Map<String, List<Map<String, String>>> i18n;
}
