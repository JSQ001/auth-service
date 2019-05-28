package com.hand.hcf.app.mdata.area.dto;

import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.mdata.area.domain.Area;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by chenliangqin on 16/11/15.
 */
@Data
public class LevelDTO extends Domain {


    private Long id;

    private UUID levelOid;

    private UUID companyOid;

    private String levelName;

    private boolean deleted = false;

    private List<Area> areas;

    private String[] areaCodeArray;

    private List<InternationalAreaDTO> internationalAreaDTOS;


    private Long tenantId;


    private Long source;

    private String code;

    private String comment;
}
