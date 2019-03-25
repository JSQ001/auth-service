package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.ContentFunctionRelation;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.service.ContentFunctionRelationService;
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
@RequestMapping("/api/content/function/relation")
public class ContentFunctionRelationController {
    private final ContentFunctionRelationService contentFunctionRelationService;

    public ContentFunctionRelationController(ContentFunctionRelationService contentFunctionRelationService){
        this.contentFunctionRelationService = contentFunctionRelationService;
    }

    /**
     * 批量新增 目录功能关联
     * @param list
     * @return
     */
    @PostMapping
    public ResponseEntity<List<ContentFunctionRelation>> createContentFunctionRelationBatch(@RequestBody List<ContentFunctionRelation> list){
        return ResponseEntity.ok(contentFunctionRelationService.createContentFunctionRelationBatch(list));
    }

    /**
     * 批量物理删除 目录功能关联
     * @param idList
     * @return
     */
    @PostMapping("/deleted/by/ids")
    public ResponseEntity deleteContentFunctionRelationBatch(@RequestBody List<Long> idList){
        contentFunctionRelationService.deleteContentFunctionRelationBatch(idList);
        return ResponseEntity.ok().build();
    }

    /**
     * 条件查询 目录功能关联
     *
     * @param contentId
     * @return
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<ContentFunctionRelation>> getContentFunctionRelationByCond(
            @RequestParam("contentId") Long contentId){
        List<ContentFunctionRelation> result = contentFunctionRelationService.getContentFunctionRelationByCond(contentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 过滤查询 目录功能关联
     *
     * @param functionName
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/filter")
    public ResponseEntity<List<FunctionList>> filterContentFunctionRelationByCond(
            @RequestParam(value = "functionName",required = false) String functionName,
            Pageable pageable)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FunctionList> result = contentFunctionRelationService.filterContentFunctionRelationByCond(functionName,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders,HttpStatus.OK);
    }
}
