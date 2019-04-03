package com.hand.hcf.app.base.codingrule.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleDetail;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleObject;
import com.hand.hcf.app.base.codingrule.service.CodingRuleDetailService;
import com.hand.hcf.app.base.codingrule.service.CodingRuleObjectService;
import com.hand.hcf.app.base.codingrule.service.CodingRuleService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.PageUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author dong.liu on 2017-08-23
 */
@RestController
@RequestMapping("/api/budget/coding/rule/details")
public class CodingRuleDetailResource {

    @Autowired
    private CodingRuleDetailService codingRuleDetailService;

    @Autowired
    private CodingRuleService codingRuleService;

    @Autowired
    private CodingRuleObjectService codingRuleObjectService;

    /**
     * 新增一个编码规则明细
     *
     * @param codingRuleDetail
     * @return ResponseEntity<CodingRuleDetail>
     */
    @PostMapping
    public ResponseEntity<CodingRuleDetail> insertCodingRuleDetail(@Valid @RequestBody CodingRuleDetail codingRuleDetail) {
        CodingRuleDetail codingRuleDetail1 = codingRuleDetailService.insertCodingRuleDetail(codingRuleDetail);
        return ResponseEntity.ok(codingRuleDetail1);
    }

    /**
     * 更新一个编码规则明细
     *
     * @param codingRuleDetail
     * @return ResponseEntity<CodingRuleDetail>
     */
    @PutMapping
    public ResponseEntity<CodingRuleDetail> updateCodingRuleDetail(@Valid @RequestBody CodingRuleDetail codingRuleDetail) {
        CodingRuleDetail codingRuleDetail1 = codingRuleDetailService.updateCodingRuleDetail(codingRuleDetail);
        return ResponseEntity.ok(codingRuleDetail1);
    }

    /**
     * 根据id删除编码规则明细
     *
     * @param id
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCodingRuleDetail(@PathVariable Long id) {
        CodingRuleDetail codingRuleDetail = codingRuleDetailService.selectById(id);
        if (codingRuleDetail == null) {
            throw new BizException(RespCode.BUDGET_CODING_NOT_FOUND);
        }
        codingRuleDetailService.deleteCodingRuleDetail(codingRuleDetail);
        return ResponseEntity.ok().build();
    }


    /**
     * 通过id获取编码规则明细
     *
     * @param id
     * @return ResponseEntity<CodingRuleDetail>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CodingRuleDetail> getCodingRuleDetailById(@PathVariable Long id) {
        CodingRuleDetail codingRuleDetail = codingRuleDetailService.selectById(id);
        if (codingRuleDetail != null) {
            codingRuleDetailService.setCodingRuleDetail(codingRuleDetail);
        }
        return ResponseEntity.ok(codingRuleDetail);
    }

    /**
     * 通用查询-分页
     *
     * @param codingRuleId 编码规则id
     * @param isEnabled    是否启用
     * @param pageable     页码
     * @return ResponseEntity<List       <       CodingRuleDetail>>
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    public ResponseEntity<List<CodingRuleDetail>> getCodingRuleDetailByCond(
        @RequestParam Long codingRuleId,
        @RequestParam(required = false, value = "enabled") Boolean isEnabled,
        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CodingRuleDetail> result = codingRuleDetailService.getCodingRuleDetailByCond(codingRuleId, isEnabled, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/budget/coding/rule/details/query");
        return new ResponseEntity(result.getRecords(), headers, HttpStatus.OK);
    }

}
