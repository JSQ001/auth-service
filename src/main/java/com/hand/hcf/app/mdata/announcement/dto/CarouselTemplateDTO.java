package com.hand.hcf.app.mdata.announcement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.mdata.announcement.domain.CarouselTemplate;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: 魏胜
 * @Description: 公告图片实体；分 内置图片和模板图片
 * @Date: 2018/7/16 11:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CarouselTemplateDTO extends CarouselTemplate {

    private Map<String, List<Map<String, String>>> i18n;
    /**
     * 异常信息
     * code: 异常编码
     * msg: 异常信息
     */
    private Map<String, String> exceptionMap;
}
