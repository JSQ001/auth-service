package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.service.FunctionListService;
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
@RequestMapping("/api/function/list")
public class FunctionListController {
    private final FunctionListService functionListService;

    public FunctionListController(FunctionListService functionListService){
        this.functionListService = functionListService;
    }

    /**
     * 新增 功能
     * @param functionList
     * @return
     */
    @PostMapping
    public ResponseEntity<FunctionList> createFunctionList(@RequestBody FunctionList functionList){
        return ResponseEntity.ok(functionListService.createFunctionList(functionList));
    }

    /**
     * 逻辑删除 功能
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFunctionListById(@PathVariable Long id){
        functionListService.deleteFunctionListById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改 功能
     * @param functionList
     * @return
     */
    @PutMapping
    public ResponseEntity<FunctionList> updateFunctionList(@RequestBody FunctionList functionList){
        return ResponseEntity.ok(functionListService.updateFunctionList(functionList));
    }

    /**
     * 根据id查询 功能
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<FunctionList> getFunctionListById(@PathVariable Long id){
        return ResponseEntity.ok(functionListService.getFunctionListById(id));
    }

    /**
     * 条件分页查询 功能
     * @param functionName
     * @param functionRouter
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<FunctionList>> getFunctionListByCond(
            @RequestParam(value = "functionName",required = false)String functionName,
            @RequestParam(value = "functionRouter",required = false)String functionRouter,
            Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<FunctionList> result = functionListService.getFunctionListByCond(functionName,functionRouter,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/function/list/query/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
