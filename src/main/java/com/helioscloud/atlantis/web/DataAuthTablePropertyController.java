package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.service.DataAuthTablePropertyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 17:33
 * @remark
 */
@RestController
@RequestMapping(value = "/api/data/auth/table/properties")
@AllArgsConstructor
public class DataAuthTablePropertyController {

    private final DataAuthTablePropertyService dataAuthTablePropertyService;
}
