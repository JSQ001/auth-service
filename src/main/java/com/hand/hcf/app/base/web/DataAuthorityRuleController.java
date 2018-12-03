package com.hand.hcf.app.base.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.dto.DataAuthRuleDetailValueDTO;
import com.hand.hcf.app.base.service.DataAuthorityRuleService;
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
     * @api {DELETE} /api/data/authority/rule/{id} 【数据权限】删除数据权限规则
     * @apiDescription 删除数据权限规则，并删除明细数据
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} id ID
     *
     * @apiParamExample {json} 请求报文:
     *  /api/data/authority/rule/1
     *
     */
    @GetMapping(value = "/detail/values")
    public ResponseEntity<List<DataAuthRuleDetailValueDTO>> getDataAuthRuleDetailValuesByDataType(@RequestParam(value = "ruleId") Long ruleId,
                                                                                                 @RequestParam(value = "dataType") String dataType,
                                                                                                 Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DataAuthRuleDetailValueDTO> dataAuthRuleDetailValuesByDataType = dataAuthorityRuleService.getDataAuthRuleDetailValuesByDataType(ruleId, dataType, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/data/authority/rule/detail/values");
        return new ResponseEntity(dataAuthRuleDetailValuesByDataType,httpHeaders, HttpStatus.OK);
    }
}
