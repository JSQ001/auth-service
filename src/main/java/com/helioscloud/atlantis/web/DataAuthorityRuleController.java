package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.service.DataAuthorityRuleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:49
 * @remark
 */
@RestController
@RequestMapping("/api/data/authority/rule")
@AllArgsConstructor
public class DataAuthorityRuleController {

    private final DataAuthorityRuleService dataAuthorityRuleService;
}
