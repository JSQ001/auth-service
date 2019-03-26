package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserGroupMappingDTO {
    private List<UUID> userOids;
    private UUID userGroupOid;

}
