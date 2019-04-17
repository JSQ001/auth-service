package com.hand.hcf.app.mdata.announcement.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.announcement.cover.CarouselTemplateMapping;
import com.hand.hcf.app.mdata.announcement.domain.CarouselTemplate;
import com.hand.hcf.app.mdata.announcement.dto.CarouselTemplateDTO;
import com.hand.hcf.app.mdata.announcement.enums.CarouselTemplateTypeEnum;
import com.hand.hcf.app.mdata.announcement.persistence.CarouselTemplateMapper;
import com.hand.hcf.app.core.util.PageUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: zhu.zhao
 * @Description:
 * @Date: 2019/1/14 10:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CarouselTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarouselTemplateService.class);

    @Autowired
    private CarouselTemplateMapper carouselTemplateMapper;

    public CarouselTemplateDTO createCarouselTemplate(CarouselTemplateDTO carouselTemplateDTO) {
        // 内置图片仅能维护一张
        if (CarouselTemplateTypeEnum.DEFAULT.toString().equals(carouselTemplateDTO.getType())) {
            List<CarouselTemplate> carouselTemplates = carouselTemplateMapper.selectByType(carouselTemplateDTO.getType(), new Page<>(1, 1));
            if (!CollectionUtils.isEmpty(carouselTemplates)) {
                return packagingExceptionMap(carouselTemplateDTO, "3008001", null);
            }
        }
        CarouselTemplate carouselTemplate = CarouselTemplateMapping.carouselTemplateDTOToCarouselTemplate(carouselTemplateDTO);
        carouselTemplateMapper.insert(carouselTemplate);
        return CarouselTemplateMapping.carouselTemplateToCarouselTemplateDTO(carouselTemplate);
    }

    public CarouselTemplateDTO updateCarouselTemplate(CarouselTemplateDTO carouselTemplateDTO) {
        CarouselTemplate carouselTemplate = carouselTemplateMapper.selectById(carouselTemplateDTO.getId());
        if (carouselTemplate == null) {
            return packagingExceptionMap(carouselTemplateDTO, "3008002", null);
        }
        carouselTemplate.setDeleted(Boolean.TRUE);
        carouselTemplate.setLastUpdatedBy(carouselTemplateDTO.getCreatedBy());
        carouselTemplate.setLastUpdatedDate(ZonedDateTime.now());
        carouselTemplateMapper.updateById(carouselTemplate);
        CarouselTemplate carouselTemplateNew = null;
        if (!carouselTemplateDTO.getDeleted()) {
            carouselTemplateNew = CarouselTemplateMapping.carouselTemplateDTOToCarouselTemplate(carouselTemplateDTO);
            carouselTemplateNew.setId(null);
            carouselTemplateMapper.insert(carouselTemplateNew);
        }
        if (carouselTemplateNew == null) {
           return carouselTemplateDTO;
        } else {
            return CarouselTemplateMapping.carouselTemplateToCarouselTemplateDTO(carouselTemplateNew);
        }
    }

    /**
     * 封装异常信息，与上游平台交互
     * @param carouselTemplateDTO
     * @param code
     * @param msg
     * @return
     */
    private CarouselTemplateDTO packagingExceptionMap(CarouselTemplateDTO carouselTemplateDTO, String code, String msg) {
        Map<String, String> exceptionMap = new HashedMap();
        exceptionMap.put("code", code);
        exceptionMap.put("msg", msg);
        carouselTemplateDTO.setExceptionMap(exceptionMap);
        return carouselTemplateDTO;
    }

    public Page<CarouselTemplateDTO> selectCarouselTemplates(String type, String language, Pageable pageable) {
        Page<CarouselTemplate> page = PageUtil.getPage(pageable);
        page.setRecords(carouselTemplateMapper.selectByType(type, page));
        List<CarouselTemplateDTO> carouselTemplateDTOs = page.getRecords().stream().map(carouselTemplate -> {
            CarouselTemplateDTO carouselTemplateDTO = CarouselTemplateMapping.carouselTemplateToCarouselTemplateDTO(carouselTemplate);
            //languageConvertService.convertToOneByLanguage(carouselTemplateDTO, CarouselTemplate.class, carouselTemplate.getId(), language);
            //carouselTemplateDTO.setAttachmentOid(attachmentService.getAttachmentId(carouselTemplate.getAttachmentId()).getAttachmentOID().toString());
            return carouselTemplateDTO;
        }).collect(Collectors.toList());
        Page<CarouselTemplateDTO> result = PageUtil.getPage(pageable);
        result.setRecords(carouselTemplateDTOs);
        result.setTotal(page.getTotal());
        return result;
    }
}
