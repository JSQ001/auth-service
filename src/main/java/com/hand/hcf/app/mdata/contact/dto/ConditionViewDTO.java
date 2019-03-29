package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Ray Ma on 2017/11/1.
 */
@Data
public class ConditionViewDTO {
    private Integer conditionSeq;
    private List<UserGroupConditionDTO> conditionDetails;

}
