package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.system.domain.FrontKey;
import com.hand.hcf.app.base.system.service.FrontKeyService;
import com.hand.hcf.core.web.dto.MessageDTO;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
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
@AllArgsConstructor
@RestController
public class MessageControllerImpl {
    private final FrontKeyService frontKeyService;
    private final MapperFacade mapper;

//    @GetMapping("/front/key/byModuleAndKeyAndLang")
    public MessageDTO getFrontKeyByModuleAndKeyAndLang(@RequestParam("moduleCode") String moduleCode,
                                                       @RequestParam("keyCode") String keyCode,
                                                       @RequestParam("lang") String lang) {
        FrontKey frontKeyByModuleAndKeyAndLang = frontKeyService.getFrontKeyByModuleAndKeyAndLang(moduleCode, keyCode, lang);
        if(frontKeyByModuleAndKeyAndLang != null){
            return mapper.map(frontKeyByModuleAndKeyAndLang,MessageDTO.class);
        }
        return null;
    }

//    @GetMapping("/front/key/byModuleAndKey")
    public List<MessageDTO> getFrontKeyByModuleAndKey(@RequestParam("moduleCode") String moduleCode,
                                                      @RequestParam("keyCode") String keyCode) {
        return mapper.mapAsList(frontKeyService.getFrontKeysByModuleAndKey(moduleCode,keyCode),MessageDTO.class);
    }
}
