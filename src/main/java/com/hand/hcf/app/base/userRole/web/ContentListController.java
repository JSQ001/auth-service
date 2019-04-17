package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.ContentList;
import com.hand.hcf.app.base.userRole.service.ContentListService;
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
@RequestMapping("/api/content/list")
public class ContentListController {
    private final ContentListService contentListService;

    public ContentListController(ContentListService contentListService){
        this.contentListService = contentListService;
    }

    /**
     * 新增 目录
     * @param contentList
     * @return
     */
    @PostMapping
    public ResponseEntity<ContentList> createContentList(@RequestBody ContentList contentList){
        return ResponseEntity.ok(contentListService.createContentList(contentList));
    }

    /**
     * 逻辑删除 目录
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteContentListById(@PathVariable Long id){
        contentListService.deleteContentListById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改 目录
     * @param contentList
     * @return
     */
    @PutMapping
    public ResponseEntity<ContentList> updateContentList(@RequestBody ContentList contentList){
        return ResponseEntity.ok(contentListService.updateContentList(contentList));
    }

    /**
     * 根据id查询 目录
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentList> getContentListById(@PathVariable Long id){
        return ResponseEntity.ok(contentListService.getContentListById(id));
    }

    /**
     * 条件分页查询 目录
     * @param contentName
     * @param contentRouter
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<ContentList>> getContentListByCond(
            @RequestParam(value = "contentName",required = false)String contentName,
            @RequestParam(value = "contentRouter",required = false)String contentRouter,
            Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<ContentList> result = contentListService.getContentListByCond(contentName,contentRouter,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/content/list/query/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 查询某个目录的子目录
     * @param id
     * @return
     */
    @GetMapping("/query/son/content/{id}")
    public ResponseEntity<List<ContentList>> getSonContent (@PathVariable Long id){
        return ResponseEntity.ok(contentListService.getSonContent(id));
    }
}
