package com.hand.hcf.app.base.system.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.base.system.domain.Language;
import com.hand.hcf.app.base.system.dto.Lov;
import com.hand.hcf.app.base.system.service.LanguageService;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
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
public class LovController {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private LanguageService languageService;
    private ObjectMapper objectMapper=new ObjectMapper();


    @RequestMapping(value = "/lov/language/{code}", method = RequestMethod.POST)
    public ResponseEntity<List<Lov>> getSystemLanguageList(@PathVariable String code) {
        List<Lov> list = new ArrayList<Lov>();
        List<String> language = this.getSysEnableLanguage();

        List<Lov> listLang = new ArrayList<Lov>();
        listLang.add(new Lov(LanguageEnum.ZH_CN.getKey(), "简体中文", "language", "简体中文"));
        listLang.add(new Lov(LanguageEnum.EN_US.getKey(), "英文", "language", "English"));
        listLang.add(new Lov(LanguageEnum.MS.getKey(), "马来文", "language", "Bahasa Melayu"));
        listLang.add(new Lov(LanguageEnum.JA.getKey(), "日语", "language", "日本语"));



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
    public ResponseEntity<List<Lov>> getReportLineStatusList(@PathVariable String code) {
        List<Lov> list = new ArrayList<Lov>();
        switch (code) {
            case "zh_cn":
                list.add(new Lov("1", "全集团", "report_line_status", "简体中文"));
                list.add(new Lov("3", "该人员所在部门（包含下级部门）", "report_line_status", "简体中文"));
                list.add(new Lov("2", "该人员所在部门（不包含下级部门）", "report_line_status", "简体中文"));
                list.add(new Lov("7", "该人员所在法人", "report_line_status", "简体中文"));
                list.add(new Lov("6", "指定部门（包含下级部门）", "report_line_status", "简体中文"));
                list.add(new Lov("4", "指定部门（不包含下级部门）", "report_line_status", "简体中文"));
                list.add(new Lov("5", "指定法人", "report_line_status", "简体中文"));
                list.add(new Lov("8", "指定账套", "report_line_status", "简体中文"));
                list.add(new Lov("9", "指定公司", "report_line_status", "简体中文"));
                break;
            case "en_us":
                list.add(new Lov("1", "All Group", "report_line_status", "English"));
                list.add(new Lov("3", "The Department in which the person belongs (including the subordinate departments)", "report_line_status", "English"));
                list.add(new Lov("2", "The Department (excluding subordinate departments) of the person", "report_line_status", "English"));
                list.add(new Lov("7", "The legal person of the company", "report_line_status", "English"));
                list.add(new Lov("6", "Designated department (including subordinate departments)", "report_line_status", "English"));
                list.add(new Lov("4", "Designated department (excluding subordinate departments)", "report_line_status", "English"));
                list.add(new Lov("5", "Designated Corporation", "report_line_status", "English"));
                list.add(new Lov("8", "Designated Set of Books", "report_line_status", "English"));
                list.add(new Lov("9", "Designated Company", "report_line_status", "English"));
                break;
            case "ms":
                list.add(new Lov("1", "All Group", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("3", "The Department in which the person belongs (including the subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("2", "The Department (excluding subordinate departments) of the person", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("7", "The legal person of the company", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("6", "Designated department (including subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("4", "Designated department (excluding subordinate departments)", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("5", "Designated Corporation", "report_line_status", "Bahasa Melayu"));
                list.add(new Lov("8", "Designated Set of Books", "report_line_status", "English"));
                list.add(new Lov("9", "Designated Company", "report_line_status", "English"));
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

    private List<Lov> getEnableLanguage(List<Lov> allLanguageList, List<String> enableLanguageList) {
        List<Lov> list = new ArrayList<Lov>();
        for (Lov lov : allLanguageList) {
            for (String enable : enableLanguageList) {
                if (lov.getCode().equalsIgnoreCase(enable)) {
                    list.add(lov);
                }
            }
        }
        return list;
    }

}
