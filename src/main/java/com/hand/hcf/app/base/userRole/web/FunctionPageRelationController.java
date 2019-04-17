package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionPageRelation;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.service.FunctionPageRelationService;
import com.hand.hcf.app.core.util.PageUtil;
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
     * 批量物理删除 功能页面关联
     * @param idList
     * @return
     */
    @PostMapping("/deleted/by/ids")
    public ResponseEntity deleteFunctionPageRelationBatch(@RequestBody List<Long> idList){
        functionPageRelationService.deleteFunctionPageRelationBatch(idList);
        return ResponseEntity.ok().build();
    }

    /**
     * 条件查询 功能页面关联
     *
     * @param functionId
     * @return
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<FunctionPageRelation>> getFunctionPageRelationByCond(
            @RequestParam(value = "functionId") Long functionId){
        List<FunctionPageRelation> result = functionPageRelationService.getFunctionPageRelationByCond(functionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 过滤查询 功能页面关联
     *
     * @param pageName
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/filter")
    public ResponseEntity<List<PageList>> filterFunctionPageRelationByCond(
            @RequestParam(value = "pageName",required = false) String pageName,
            Pageable pageable)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<PageList> result = functionPageRelationService.filterFunctionPageRelationByCond(pageName,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<List<PageList>>(result,httpHeaders,HttpStatus.OK);
    }
}
