package com.hand.hcf.app.mdata.parameter.web;

import com.hand.hcf.app.mdata.parameter.domain.Parameter;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 19:40
 */
@RestController
@RequestMapping("/api/parameter")
public class ParameterController {

    @Autowired
    private ParameterService parameterService;

    /**
     * @apiDefine ParameterDefinition 参数定义
     */

    /**
     * @api {GET} /api/parameter/by/moduleCode 【参数】查询模块代码下的参数
     * @apiGroup Parameter
     * @apiParam {String} moduleCode  模块代码
     * @apiParamExample {json} 请求参数:
     * {
     *     "moduleCode":"ACCOUNT"
     * }
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077205443387875329",
     *         "parameterCode": "ACCOUNT_ENABLE",
     *         "parameterName": "核算模块启用标志",
     *         "moduleCode": "ACCOUNT",
     *         "sobParameter": true,
     *         "companyParameter": false,
     *         "parameterValueType": "VALUE_LIST",
     *         "apiSourceModule": null,
     *         "api": null,
     *         "remark": null,
     *         "createdDate": "2018-12-27T16:37:45.892+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-27T16:37:45.895+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *     }
     * ]
     */
    @GetMapping("/by/moduleCode")
    public List<Parameter> listParameterByModuleCode(@RequestParam(value = "moduleCode",required = false) String moduleCode,
                                                     @RequestParam(value = "parameterLevel",required = false)ParameterLevel parameterLevel){
        return parameterService.listParameterByModuleCode(moduleCode,parameterLevel);
    }

}
