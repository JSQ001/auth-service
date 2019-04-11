package com.hand.hcf.app.mdata.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/31 21:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationLevelAssignDTO implements Serializable {
    /**
     * 地点级别id
     */
    private Long levelId;
    /**
     * 地点id列表
     */
    private List<Long> locationIds;
}
