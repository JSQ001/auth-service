package com.hand.hcf.app.mdata.period.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.period.domain.PeriodRules;
import com.hand.hcf.app.mdata.period.service.PeriodRuleService;
import com.hand.hcf.core.util.PageUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PeriodRulesResource {
    private final Logger log = LoggerFactory.getLogger(PeriodRulesResource.class);
    @Autowired
    private PeriodRuleService periodRuleService;

    /**
     * @api {get} /api/periodrule/query 根据会计期id 分页查询规则数据
     * @apiGroup PeriodRule
     * @apiParam {Long} periodSetId   会计期id
     * @apiSuccess {Object[]} PeriodRule  会计期规则实体集合
     * @apiSuccess {Long} id   会计期规则id
     * @apiSuccess {Long} periodSetId   会计期id
     * @apiSuccess {String} periodNum   序号
     * @apiSuccess {String} periodAdditionalName   期间名称附加
     * @apiSuccess {Integer} monthFrom   月份从
     * @apiSuccess {Integer} monthTo  月份到
     * @apiSuccess {Integer} dateFrom  日期从
     * @apiSuccess {Integer} dateTo  日期到
     * @apiSuccess {Integer} quarterNum  季度
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    [ {
    "id": "911872001642643458",
    "periodSetId": 907805405383053313,
    "periodNum": 2,
    "periodAdditionalName": "02",
    "monthFrom": 2,
    "monthTo": 2,
    "dateFrom": 1,
    "dateTo": 28,
    "quarterNum": 1,
    "enabled": true,
    "deleted": false
    }]
     */
    @RequestMapping(value = "/periodrule/query",method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PeriodRules>> findPeriodRulesByPeriodSetId(@RequestParam(name = "periodSetId") Long periodSetId, Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<PeriodRules> result = periodRuleService.findPeriodRulesByPeriodSetId(page,periodSetId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/periodset");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }



    /**
     * @api {post} /api/periodrule/batch 批量创建多个规则
     * @apiGroup PeriodRule
     * @apiSuccess {Object[]} PeriodRule  会计期规则实体集合
     * @apiSuccess {Long} id   会计期规则id
     * @apiSuccess {Long} periodSetId   会计期id
     * @apiSuccess {String} periodNum   序号
     * @apiSuccess {String} periodAdditionalName   期间名称附加
     * @apiSuccess {Integer} monthFrom   月份从
     * @apiSuccess {Integer} monthTo  月份到
     * @apiSuccess {Integer} dateFrom  日期从
     * @apiSuccess {Integer} dateTo  日期到
     * @apiSuccess {Integer} quarterNum  季度
     * @apiSuccess {Long}  tenantId   租户id
     * @apiSuccessExample {json} Success-Result
    [ {
    "id": "911872001642643458",
    "periodSetId": 907805405383053313,
    "periodNum": 2,
    "periodAdditionalName": "02",
    "monthFrom": 2,
    "monthTo": 2,
    "dateFrom": 1,
    "dateTo": 28,
    "quarterNum": 1,
    "enabled": true,
    "deleted": false
    }]
     */
    @RequestMapping(value = "/periodrule/batch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PeriodRules>> createPeriodRulesBatch(@RequestBody List<PeriodRules> list) throws Exception {
        if (list.stream().anyMatch(u->u.getId() != null)) {
            return ResponseEntity.badRequest().body(null);
        }
        periodRuleService.addPeriodRulesBatch(list);
        return ResponseEntity.ok(list);
    }
}
