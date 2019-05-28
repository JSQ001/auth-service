package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.system.domain.ServeLocale;
import com.hand.hcf.app.base.system.service.ServeLocaleService;
import com.hand.hcf.app.core.web.dto.MessageDTO;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 18:11
 * @remark 第三方接口
 */
//@RequestMapping(value = "/api/implement")
//// @PreAuthorize("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')")
@RestController
public class MessageControllerImpl {

    @Autowired
    private  ServeLocaleService serveLocaleService;

    @Autowired
    private  MapperFacade mapper;



//    @GetMapping("/serve/locale/byKeyAndLanguage")
    public MessageDTO getServeLocaleByKeyAndLanguage(@RequestParam("keyCode") String keyCode,
                                                      @RequestParam("lang") String language) {
        ServeLocale serveLocale = serveLocaleService.getServeLocaleByKeyAndLanguage(keyCode, language);
        if(serveLocale != null){
            return mapper.map(serveLocale,MessageDTO.class);
        }
        return null;
    }

//    @GetMapping("/serve/locale/byKey")
    public List<MessageDTO> listServeLocaleByKey(@RequestParam("keyCode") String keyCode) {
        List<ServeLocale> serveLocaleList = serveLocaleService.listServeLocaleByKey(keyCode);

        return mapper.mapAsList(serveLocaleList,MessageDTO.class);
    }
}
