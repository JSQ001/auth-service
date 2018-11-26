package com.hand.hcf.app.base.web;

import com.hand.hcf.app.base.service.DataAuthorityRuleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
