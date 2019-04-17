package com.hand.hcf.app.base.system.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.base.system.domain.Language;
import com.hand.hcf.app.base.system.dto.LovDTO;
import com.hand.hcf.app.base.system.service.LanguageService;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LovLangController {

    @Autowired
    private LanguageService languageService;
    private ObjectMapper objectMapper=new ObjectMapper();


    @RequestMapping(value = "/lov/language/{code}", method = RequestMethod.POST)
    public ResponseEntity<List<LovDTO>> getSystemLanguageList(@PathVariable String code) {
        List<LovDTO> list = new ArrayList<LovDTO>();
        List<String> language = this.getSysEnableLanguage();

        List<LovDTO> listLang = new ArrayList<LovDTO>();
        listLang.add(new LovDTO(LanguageEnum.ZH_CN.getKey(), "简体中文", "language", "简体中文"));
        listLang.add(new LovDTO(LanguageEnum.EN_US.getKey(), "英文", "language", "English"));
        listLang.add(new LovDTO(LanguageEnum.MS.getKey(), "马来文", "language", "Bahasa Melayu"));
        listLang.add(new LovDTO(LanguageEnum.JA.getKey(), "日语", "language", "日本语"));



        switch (code) {
            case "zh_cn":
                list = this.getEnableLanguage(listLang, language);
                break;
            case "en_us":
                list = this.getEnableLanguage(listLang, language);
                break;
            case "ms":
                list = this.getEnableLanguage(listLang, language);
                break;
            case "jp":
                list = this.getEnableLanguage(listLang, language);
                break;
            default:
                break;
        }
        return ResponseEntity.ok(list);
    }


    @RequestMapping(value = "/lov/reportLine/{code}", method = RequestMethod.GET)
    public ResponseEntity<List<LovDTO>> getReportLineStatusList(@PathVariable String code) {
        List<LovDTO> list = new ArrayList<LovDTO>();
        switch (code) {
            case "zh_cn":
                list.add(new LovDTO("1", "全集团", "report_line_status", "简体中文"));
                list.add(new LovDTO("3", "该人员所在部门（包含下级部门）", "report_line_status", "简体中文"));
                list.add(new LovDTO("2", "该人员所在部门（不包含下级部门）", "report_line_status", "简体中文"));
                list.add(new LovDTO("7", "该人员所在法人", "report_line_status", "简体中文"));
                list.add(new LovDTO("6", "指定部门（包含下级部门）", "report_line_status", "简体中文"));
                list.add(new LovDTO("4", "指定部门（不包含下级部门）", "report_line_status", "简体中文"));
                list.add(new LovDTO("5", "指定法人", "report_line_status", "简体中文"));
                list.add(new LovDTO("8", "指定账套", "report_line_status", "简体中文"));
                list.add(new LovDTO("9", "指定公司", "report_line_status", "简体中文"));
                break;
            case "en_us":
                list.add(new LovDTO("1", "All Group", "report_line_status", "English"));
                list.add(new LovDTO("3", "The Department in which the person belongs (including the subordinate departments)", "report_line_status", "English"));
                list.add(new LovDTO("2", "The Department (excluding subordinate departments) of the person", "report_line_status", "English"));
                list.add(new LovDTO("7", "The legal person of the company", "report_line_status", "English"));
                list.add(new LovDTO("6", "Designated department (including subordinate departments)", "report_line_status", "English"));
                list.add(new LovDTO("4", "Designated department (excluding subordinate departments)", "report_line_status", "English"));
                list.add(new LovDTO("5", "Designated Corporation", "report_line_status", "English"));
                list.add(new LovDTO("8", "Designated Set of Books", "report_line_status", "English"));
                list.add(new LovDTO("9", "Designated Company", "report_line_status", "English"));
                break;
            case "ms":
                list.add(new LovDTO("1", "All Group", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("3", "The Department in which the person belongs (including the subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("2", "The Department (excluding subordinate departments) of the person", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("7", "The legal person of the company", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("6", "Designated department (including subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("4", "Designated department (excluding subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("5", "Designated Corporation", "report_line_status", "Bahasa Melayu"));
                list.add(new LovDTO("8", "Designated Set of Books", "report_line_status", "English"));
                list.add(new LovDTO("9", "Designated Company", "report_line_status", "English"));
                break;
            default:
                break;
        }

        return ResponseEntity.ok(list);
    }

    private List<String> getSysEnableLanguage() {
        String languageArray = "";
//        List<String> language = new ArrayList<>();
//        language.add("zh_cn");
//        language.add("en");
        List<String> language = languageService.listAll().stream().map(Language::getLanguage).collect(Collectors.toList());
        /*FunctionProfile functionProfile = functionProfileService.getFunctionProfileByCompanyOid(LoginInformationUtil.getCurrentCompanyOid());
        JSONObject profile = functionProfile.getProfileDetail();
        if (profile == null) {
            return language;
        }
        if (profile.containsKey("sys.language.enable")) {
            try {
                languageArray = profile.getString("sys.language.enable");
                if (StringUtils.isEmpty(languageArray) || languageArray.equalsIgnoreCase("null")) {
                    return language;
                }
                language = objectMapper.readValue(languageArray,new TypeReference<List<String>>(){});
            } catch (Exception e) {
                return language;
            }
        } else {
            return language;
        }*/
        return language;
    }

    private List<LovDTO> getEnableLanguage(List<LovDTO> allLanguageList, List<String> enableLanguageList) {
        List<LovDTO> list = new ArrayList<LovDTO>();
        for (LovDTO lovDTO : allLanguageList) {
            for (String enable : enableLanguageList) {
                if (lovDTO.getCode().equalsIgnoreCase(enable)) {
                    list.add(lovDTO);
                }
            }
        }
        return list;
    }

}
