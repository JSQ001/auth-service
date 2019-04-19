package com.hand.hcf.app.mdata.announcement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.announcement.dto.CarouselTemplateDTO;
import com.hand.hcf.app.mdata.announcement.service.CarouselTemplateService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: 魏胜
 * @Description: 公告图片维护
 * @Date: 2018/7/17 10:00
 */
@RestController
@RequestMapping("/api/carousel/template")
public class CarouselTemplateController {

    @Autowired
    private CarouselTemplateService carouselTemplateService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarouselTemplateDTO> createCarouselTemplate(@RequestBody CarouselTemplateDTO carouselTemplateDTO) throws URISyntaxException {
        return ResponseEntity.ok(carouselTemplateService.createCarouselTemplate(carouselTemplateDTO));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarouselTemplateDTO> updateCarouselTemplate(@RequestBody CarouselTemplateDTO carouselTemplateDTO) throws URISyntaxException {
        return ResponseEntity.ok(carouselTemplateService.updateCarouselTemplate(carouselTemplateDTO));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CarouselTemplateDTO>> selectCarouselTemplates(@RequestParam(value = "type", required = false) String type,
                                                                             Pageable pageable) throws URISyntaxException{
        Page<CarouselTemplateDTO> page = carouselTemplateService.selectCarouselTemplates(type,  LoginInformationUtil.getCurrentLanguage(), pageable);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/carousel/template");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }
}
