package com.hand.hcf.app.base.dataAuthority.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.dataAuthority.dto.DataAuthRuleDetailValueDTO;
import com.hand.hcf.app.base.dataAuthority.service.DataAuthorityRuleService;
import com.hand.hcf.core.util.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

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

    /**
     * @api {DELETE} /api/data/authority/rule/{id} 【数据权限】删除数据权限规则
     * @apiDescription 删除数据权限规则，并删除明细数据
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} id ID
     *
     * @apiParamExample {json} 请求报文:
     *  /api/data/authority/rule/1
     *
     */
    @DeleteMapping(value = "/{id}")
    public void deleteDataAuthRule(@PathVariable(value = "id") Long id){
        dataAuthorityRuleService.deleteDataAuthRuleAndDetail(id);
    }

    /**
     * @api {GET} /api/data/authority/rule/detail/values 【数据权限】获取数据权限规则明细配置数据
     * @apiDescription 根据数据权限规则配置，实时查询明细数据
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} ruleId 数据权限规则ID
     * @apiParam (请求参数) {String} dataType 数据权限规则明细数据类型
     * @apiParam (请求参数) {Integer} [page] 页数
     * @apiParam (请求参数) {Integer} [size] 每页大小
     *
     * @apiParamExample {json} 请求报文:
     *  /api/data/authority/rule/detail/values?ruleId=1066705440423739393&dataType=EMPLOYEE
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *  {
     *  "valueKey": "1059",
     *  "valueKeyCode": "8188",
     *  "valueKeyDesc": "小汤圆",
     *  "filtrateMethodDesc": "包含"
     *  }
     *  ]
     */
    @GetMapping(value = "/detail/values")
    public ResponseEntity<List<DataAuthRuleDetailValueDTO>> getDataAuthRuleDetailValuesByDataType(@RequestParam(value = "ruleId") Long ruleId,
                                                                                                 @RequestParam(value = "dataType") String dataType,
                                                                                                 @RequestParam(value = "keyWord",required = false) String keyWord,
                                                                                                 Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DataAuthRuleDetailValueDTO> dataAuthRuleDetailValuesByDataType = dataAuthorityRuleService.getDataAuthRuleDetailValuesByDataType(ruleId, dataType, keyWord, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/data/authority/rule/detail/values");
        return new ResponseEntity(dataAuthRuleDetailValuesByDataType,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/data/authority/rule/detail/values/select 【数据权限】获取数据权限规则明细值选择列表
     * @apiDescription 账套、员工选择列表
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} [ruleId] 数据权限规则ID
     * @apiParam (请求参数) {String} dataType 数据权限规则明细数据类型
     * @apiParam (请求参数) {String} scope  数据范围：all->全部；selected->已选择；notChoose->未选择
     * @apiParam (请求参数) {Integer} [code] 代码
     * @apiParam (请求参数) {Integer} [name] 名称
     * @apiParam (请求参数) {Integer} [page] 页数
     * @apiParam (请求参数) {Integer} [size] 每页大小
     *
     * @apiParamExample {json} 请求报文:
     *  /api/data/authority/rule/detail/values/select?ruleId=1066705440423739393&dataType=EMPLOYEE&scope=all
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *  {
     *  "valueKey": "1059",
     *  "valueKeyCode": "8188",
     *  "valueKeyDesc": "小汤圆",
     *  "filtrateMethodDesc": null
     *  }
     *  ]
     */
    @GetMapping(value = "/detail/values/select")
    public ResponseEntity<List<DataAuthRuleDetailValueDTO>> getDataAuthRuleDetailSelectValuesByDataType(@RequestParam(value = "ruleId",required = false) Long ruleId,
                                                                                                  @RequestParam(value = "dataType") String dataType,
                                                                                                  @RequestParam(value = "scope") String scope,
                                                                                                  @RequestParam(value = "code",required = false) String code,
                                                                                                  @RequestParam(value = "name",required = false) String name,
                                                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DataAuthRuleDetailValueDTO> dataAuthRuleDetailValuesByDataType =
                dataAuthorityRuleService.getDataAuthRuleDetailSelectValuesByDataType(ruleId, dataType, scope, code, name, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/data/authority/rule/detail/values/select");
        return new ResponseEntity(dataAuthRuleDetailValuesByDataType,httpHeaders, HttpStatus.OK);
    }


}
