package com.hand.hcf.app.mdata.announcement.cover;

import com.hand.hcf.app.mdata.announcement.domain.CarouselTemplate;
import com.hand.hcf.app.mdata.announcement.dto.CarouselTemplateDTO;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/7/16 11:58
 */
public class CarouselTemplateMapping {

    @Autowired
    static private MapperFacade mapperFacade;

    public static CarouselTemplate carouselTemplateDTOToCarouselTemplate(CarouselTemplateDTO carouselTemplateDTO) {
        if (carouselTemplateDTO == null) {
            return null;
        }
        CarouselTemplate carouselTemplate = new CarouselTemplate();
        return mapperFacade.map(carouselTemplateDTO,carouselTemplate.getClass());
    }

    public static CarouselTemplateDTO carouselTemplateToCarouselTemplateDTO(CarouselTemplate carouselTemplate) {
        if (carouselTemplate == null) {
            return null;
        }
        CarouselTemplateDTO carouselTemplateDTO = new CarouselTemplateDTO();
        return mapperFacade.map(carouselTemplate,carouselTemplateDTO.getClass());
    }
}
