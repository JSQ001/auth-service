package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Ray Ma on 2017/11/1.
 */

@Data
public class UserGroupConditionDetailDTO implements Serializable {

    private Long id ;

    private Long conditionId;
    private String conditionValue;
    private String description;
}
