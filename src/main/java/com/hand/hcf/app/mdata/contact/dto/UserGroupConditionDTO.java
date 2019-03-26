package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Ray Ma on 2017/10/24.
 */
@Data
public class UserGroupConditionDTO {

    private Long id ;

    private Long userGroupId;
    private Integer conditionSeq;
    private String conditionLogic;
    private String conditionProperty;
    private List<UserGroupConditionDetailDTO> conditionValues;
    private Boolean enabled;
}
