package com.hand.hcf.app.mdata.contact.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserGroupQO {
    private Long userId;
    private UUID userOid;
    private List<UUID> userOids;
    private UUID companyOid;
    private Boolean enabled;


}
