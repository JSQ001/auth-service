package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ray Ma on 2018/4/5.
 */
@Data
public class UserMoveDTO {

    private UUID companyOidFrom;
    private UUID companyOidTo;
    private List<UUID> userOids;
    private String selectMode = "default";
}
