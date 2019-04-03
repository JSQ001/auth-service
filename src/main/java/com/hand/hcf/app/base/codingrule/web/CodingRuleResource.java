package com.hand.hcf.app.base.codingrule.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import com.hand.hcf.app.base.codingrule.service.CodingRuleService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author dong.liu on 2017-08-23
 */
@RestController
@RequestMapping("/api/budget/coding/rules")
public class CodingRuleResource {

    @Autowired
    private CodingRuleService codingRuleService;

    /**
     * 新增一个编码规则
     *
     * @param codingRule
     * @return ResponseEntity<CodingRule>
     */
    @PostMapping
    public ResponseEntity<CodingRule> insertCodingRule(@RequestBody @Valid CodingRule codingRule) {
        return ResponseEntity.ok(codingRuleService.insetCodingRule(codingRule));
    }

    /**
     * 更新一个编码规则
     *
     * @param codingRule
     * @return ResponseEntity<CodingRule>
     */
    @PutMapping
    public ResponseEntity<CodingRule> updateCodingRule(@RequestBody @Valid CodingRule codingRule) {
        return ResponseEntity.ok(codingRuleService.updateCodingRule(codingRule));
    }

    /**
     * 删除一个编码规则
     *
     * @param id
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCodingRule(@PathVariable Long id) {
        CodingRule codingRule = codingRuleService.selectById(id);
        codingRuleService.deleteCodingRule(codingRule);
        return ResponseEntity.ok().build();
    }


    /**
     * 通过id获取编码规则
     *
     * @param id
     * @return ResponseEntity<CodingRule>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CodingRule> getCodingRuleById(@PathVariable Long id) {
        CodingRule codingRule = codingRuleService.selectById(id);
        if (codingRule != null) {
            codingRuleService.setCodingRuleById(codingRule);
        }
        return ResponseEntity.ok(codingRule);
    }

    /**
     * 通用查询-分页
     *
     * @param codingRuleObjectId 编码规则定义id
     * @param codingRuleCode     编码规则代码
     * @param codingRuleName     编码规则名称
     * @param isEnabled          是否启用
     * @param pageable           页码
     * @return ResponseEntity<List       <       CodingRule>>
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    public ResponseEntity<List<CodingRule>> getCodingRuleByCond(
        @RequestParam Long codingRuleObjectId,
        @RequestParam(required = false) String codingRuleCode,
        @RequestParam(required = false) String codingRuleName,
        @RequestParam(required = false, value = "enabled") Boolean isEnabled,
        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CodingRule> result = codingRuleService.getCodingRuleByCond(codingRuleObjectId, codingRuleCode, codingRuleName, isEnabled, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/budget/coding/rules/query");
        return new ResponseEntity(result.getRecords(), headers, HttpStatus.OK);
    }
}
