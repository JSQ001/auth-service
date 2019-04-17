package com.hand.hcf.app.base.codingrule.web;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleObject;
import com.hand.hcf.app.base.codingrule.domain.enums.DocumentTypeEnum;
import com.hand.hcf.app.base.codingrule.service.CodingRuleObjectService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dong.liu on 2017-08-23
 */
@RestController
@RequestMapping("/api/budget/coding/rule/objects")
public class CodingRuleObjectResource {

    @Autowired
    private CodingRuleObjectService codingRuleObjectService;

    /**
     * 新增一个编码规则定义
     *
     * @param codingRuleObject
     * @return ResponseEntity<CodingRuleObject>
     */
    @PostMapping
    public ResponseEntity<CodingRuleObject> insertCodingRuleObject(@RequestBody CodingRuleObject codingRuleObject) {
        return ResponseEntity.ok(codingRuleObjectService.insertCodingRuleObject(codingRuleObject));
    }

    @PutMapping
    /**
     * 更新一个编码规则定义
     *
     * @param codingRuleObject
     * @return ResponseEntity<CodingRuleObject>
     */
    public ResponseEntity<CodingRuleObject> updateCodingRuleObject(@RequestBody CodingRuleObject codingRuleObject) {
        return ResponseEntity.ok(codingRuleObjectService.updateCodingRuleObject(codingRuleObject));
    }

    /**
     * 删除一个编码规则定义
     *
     * @param id
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCodingRuleObject(@PathVariable Long id) {
        CodingRuleObject codingRuleObject = codingRuleObjectService.selectById(id);
        if (codingRuleObject != null) {
            codingRuleObjectService.deleteCodingRuleObject(codingRuleObject);
        } else {
            throw new BizException(RespCode.BUDGET_CODING_NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }


    /**
     * 根据id获取编码规则定义
     *
     * @param id
     * @return ResponseEntity<CodingRuleObject>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CodingRuleObject> getCodingRuleObjectById(@PathVariable Long id) {
        CodingRuleObject codingRuleObject = codingRuleObjectService.selectById(id);
        if (codingRuleObject != null) {
            codingRuleObjectService.setCodingRuleObject(codingRuleObject);
        }
        return ResponseEntity.ok(codingRuleObject);
    }

    /**
     * 通用查询-分页
     *
     * @param documentTypeCode 单据类型代码
     * @param companyCode      公司代码
     * @param pageable         页码
     * @return ResponseEntity<List       <       CodingRuleObject>>
     */
    @GetMapping("/query")
    public ResponseEntity<List<CodingRuleObject>> getCodingRuleObjectByCond(
        @RequestParam(required = false) String documentTypeCode,
        @RequestParam(required = false) String companyCode,
        @RequestParam(required = false, value = "enabled") Boolean enabled,
        Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page<CodingRuleObject> result = codingRuleObjectService.getCodingRuleObjectByCond(documentTypeCode, companyCode, enabled, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/budget/coding/rule/objects/query");
        return new ResponseEntity(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 初始化一个租户级编码规则数据
     *
     * @param tenantId
     * @return
     */
    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestParam Long tenantId) {
        boolean flag = codingRuleObjectService.initDefaultCodingRule(tenantId);
        return ResponseEntity.ok(flag);
    }

    @GetMapping("/enable")
    public ResponseEntity<Boolean> isEnabledCodingRuleObject(@RequestParam String documentTypeCode) {
        boolean flag = codingRuleObjectService.isEnabledCodingRuleObject(null, documentTypeCode);
        return ResponseEntity.ok(flag);
    }

    /**
     * 校验供应商是否支持自动编码。
     * 租户级供应商仅校验租户级编码规则；
     * 公司级供应商需优先检验公司级编码规则，如没有配置，再校验租户级编码规则；
     * @param roleType
     * @return {"result":true} : 自动编码； {"result":false} : 手动编码
     */
    @GetMapping(value = "/vendor/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> validateVendorAutoCode(@RequestParam(value = "roleType", required = false) String roleType) {
        return ResponseEntity.ok(codingRuleObjectService.validateVendorAutoCode(null, DocumentTypeEnum.VENDER.toString(), roleType));
    }
}
