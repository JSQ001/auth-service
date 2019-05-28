package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.LocalizationDTO;
import com.hand.hcf.app.common.dto.LocalizationStateDTO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.location.service.LocalizationDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zhaowei.zhang
 * @Date: Create in 12:13 2019/4/17
 * =======================================
 **/
@RestController
public class LocalizationControllerImpl {

    @Autowired
    private LocalizationDTOService localizationDTOService;

    /**
     * 根据code获取国家信息
     *
     * @param language
     * @param includeList
     * @param page
     * @param size
     * @return
     */
    //@Override
    public ResponseEntity<List<LocalizationDTO>> getLocalizationCountryByCode(
            String language,
            List<String> includeList,
            int page,
            int size) {
        language = language.toLowerCase();
        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationCountryByCode(null, null, language, includeList, mybatisPage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/localization/query/country");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    //@Override
    public ResponseEntity<List<LocalizationDTO>> getLocalizationStateByCode(String code, String vendorType, List<String> includeList, int page, int size) {
        return null;
    }

    //@Override
    public ResponseEntity<List<LocalizationDTO>> getLocalizationCityByCode(String code, String vendorType, List<String> includeList, int page, int size) {
        return null;
    }

    //@Override
    public ResponseEntity<List<LocalizationDTO>> getLocalizationDistrictByCode(String code, String vendorType, List<String> includeList, int page, int size) {
        return null;
    }

    //@Override
    public ResponseEntity<List<LocalizationStateDTO>> getLocalizationStateAndCityByCode(String code, String vendorType, List<String> includeList, int page, int size) {
        return null;
    }
}
