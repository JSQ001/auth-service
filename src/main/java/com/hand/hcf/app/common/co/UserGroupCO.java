package com.hand.hcf.app.common.co;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by cbc on 2018/3/15.
 */
@Data
public class UserGroupCO {

    private Long id;
    private String code;
    private String name;
    private UUID userGroupOid;
    private String comment;
    private String type;
    private List<Long> userIds;
}