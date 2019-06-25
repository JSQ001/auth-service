package com.hand.hcf.app.ant.withholdingReimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import com.hand.hcf.app.ant.withholdingReimburse.service.WithholdingReimburseService;
import com.hand.hcf.app.ant.withholdingReimburse.service.WithholdingReimburseService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "费用小类")
@RestController
@RequestMapping("/api/expense/category")
public class WithholdingReimburseController {

    @Autowired
    private WithholdingReimburseService withholdingReimburseService;

    @GetMapping("/query/page")
    @ApiOperation(value = "费用小类分页查询", notes = "费用小类分页查询（当前帐套下） 开发:jsq")
    ResponseEntity<List<WithholdingReimburse>> queryPages(@RequestParam String categoryType, Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<WithholdingReimburse> list = withholdingReimburseService.queryPages(categoryType,page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/category/query/page");
        return new ResponseEntity<List<WithholdingReimburse>>(list, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新建费用小类", notes = "新建费用小类 开发:jsq")
    ResponseEntity<WithholdingReimburse> create(@ApiParam(value = "费用类小类信息") @RequestBody WithholdingReimburse expenseCategory){
        return ResponseEntity.ok(withholdingReimburseService.insertOrUpdateWithholdingReimburse(expenseCategory));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询", notes = "根据ID查询 开发:jsq")
    ResponseEntity<WithholdingReimburse> getWithholdingReimburseById(@PathVariable Long id){
        return ResponseEntity.ok(withholdingReimburseService.selectById(id));
    }


    @GetMapping("/delete/{id}")
    @ApiOperation(value = "根据ID查询", notes = "根据ID查询 开发:jsq")
    ResponseEntity<Boolean> deleteWithholdingReimburse(@PathVariable Long id){
        return ResponseEntity.ok(withholdingReimburseService.deleteById(id));
    }
/**/
}
