package com.hand.hcf.app.mdata.parameter.web;

import com.hand.hcf.app.mdata.parameter.dto.ParameterModuleDTO;
import com.hand.hcf.app.mdata.parameter.service.ParameterModuleService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 19:55
 */
@RestController
@RequestMapping("/api/parameter/module")
public class ParameterModuleController {

    @Autowired
    private ParameterModuleService parameterModuleService;


    /**
     * @api {GET} /api/parameter/module 【参数模块】查询租户下启用的模块
     * @apiGroup ParameterModule
     * @apiSuccessExample {json} 成功返回值:
     * [
     *      {
     *          "moduleCode":"ACCOUNT",
     *          "moduleName":"核算模块"
     *      },
     * ]
     */
    @GetMapping
    public List<ParameterModuleDTO> listParameterModuleByTenantId(){
        return parameterModuleService.listParameterModuleByTenantId(LoginInformationUtil.getCurrentTenantId());
    }
}
