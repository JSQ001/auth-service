package com.hand.hcf.app.mdata.parameter.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.service.ParameterValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 13:07
 */
@RestController
@RequestMapping("/api/parameter/values")
public class ParameterValuesController {

    @Autowired
    private ParameterValuesService parameterValuesService;


    /**
     * @api {GET} /api/parameter/values/valuaList/by/parameterValueType 【参数值列表】查询模块代码下的参数
     * @apiGroup ParameterValues
     * @apiParam {String} parameterValueType  模块代码
     * @apiParamExample {json} 请求参数:
     * {
     *     "moduleCode":"VALUE_LIST"
     * }
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077205443387875329",
     *         "parameterValue": "Y",
     *         "parameterValueDesc": "启用预算模块",
     *     }
     * ]
     */
    @GetMapping("/valuaList/by/parameterValueType")
    public List<BasicCO> listParameterValuesByPVType(@RequestParam(value = "parameterCode",required = false)String parameterCode){

        return parameterValuesService.listParameterValuesByPVType(parameterCode);
    }
    @GetMapping("/api/by/parameterValueType")
    public ResponseEntity<List<BasicCO>> pageParameterValuesByCond(@RequestParam(value = "parameterCode",required = false)String parameterCode,
                                                                   @RequestParam(value = "parameterLevel")ParameterLevel parameterLevel,
                                                                   @RequestParam(value = "setOfBooksId",required = false)Long setOfBooksId,
                                                                   @RequestParam(value = "companyId",required = false)Long companyId,
                                                                   @RequestParam(value = "code",required = false)String code,
                                                                   @RequestParam(value = "name",required = false)String name,
                                                                   @RequestParam(value = "page",defaultValue = "0") int page,
                                                                   @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<BasicCO> result = parameterValuesService.pageParameterValuesByCond(parameterCode,null,parameterLevel,setOfBooksId,companyId,code,name,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

}
