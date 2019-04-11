package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.PageList;
import com.hand.hcf.app.base.userRole.service.PageListService;
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
 * @date: 2019/1/28
 */
@RestController
@RequestMapping("/api/page/list")
public class PageListController {
    private final PageListService pageListService;

    public PageListController(PageListService pageListService){
        this.pageListService = pageListService;
    }

    /**
     * 新增 页面
     * @param pageList
     * @return
     */
    @PostMapping
    public ResponseEntity<PageList> createPageList(@RequestBody PageList pageList){
        return ResponseEntity.ok(pageListService.createPageList(pageList));
    }

    /**
     * 逻辑删除 页面
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deletePageListById(@PathVariable Long id){
        pageListService.deletePageListById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改 页面
     * @param pageList
     * @return
     */
    @PutMapping
    public ResponseEntity<PageList> updatePageList(@RequestBody PageList pageList){
        return ResponseEntity.ok(pageListService.updatePageList(pageList));
    }

    /**
     * 根据id查询 页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<PageList> getPageListById(@PathVariable Long id){
        return ResponseEntity.ok(pageListService.getPageListById(id));
    }

    /**
     * 条件分页查询 页面
     * @param pageName
     * @param pageRouter
     * @param pageable
     * @return
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<PageList>> getPageListByCond(
            @RequestParam(value = "pageName",required = false)String pageName,
            @RequestParam(value = "pageRouter",required = false)String pageRouter,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<PageList> result = pageListService.getPageListByCond(pageName,pageRouter,page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
