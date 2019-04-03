package com.hand.hcf.app.base.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocaleDTO {

    //界面key值
    private String keyCode;


    //源语言ID
    private Long sourceId;

    //源语言key描述
    private String sourceKeyDescription;

    //源语言类型
    private String sourceCategory;


    //目标语言ID
    private Long targetId;

    //目标语言key描述
    private String targetKeyDescription;

    //目标语言版本号
    private Integer targetVersionNumber;

}
