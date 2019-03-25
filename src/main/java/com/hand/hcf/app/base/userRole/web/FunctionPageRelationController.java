package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.service.FunctionPageRelationService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@RestController
@RequestMapping("/api/function/page/relation")
public class FunctionPageRelationController {
    private final FunctionPageRelationService functionPageRelationService;

    public FunctionPageRelationController(FunctionPageRelationService functionPageRelationService){
        this.functionPageRelationService = functionPageRelationService;
    }

    /**
     * 批量新增 功能页面关联
     * @param list
     * @return
     */
    @PostMapping
    public ResponseEntity<List<FunctionPageRelation>> createFunctionPageRelationBatch(@RequestBody List<FunctionPageRelation> list){
        return ResponseEntity.ok(functionPageRelationService.createFunctionPageRelationBatch(list));
    }

    /**
     * 批量逻辑删除 功能页面关联
     * @param idList
     * @return
     */
    @PostMapping("/deleted/by/ids")
    public ResponseEntity deleteFunctionPageRelationBatch(@RequestBody List<Long> idList){
        functionPageRelationService.deleteFunctionPageRelationBatch(idList);
        return ResponseEntity.ok().build();
    }

    /**
     * 条件分页查询 功能页面关联
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<FunctionPageRelation>> geFunctionPageRelationByCond(
            Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<FunctionPageRelation> result = functionPageRelationService.geFunctionPageRelationByCond(page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/function/page/relation/query/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
