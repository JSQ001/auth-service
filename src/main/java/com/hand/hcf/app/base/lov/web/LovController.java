package com.hand.hcf.app.base.lov.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.lov.domain.Lov;
import com.hand.hcf.app.base.lov.service.LovService;
import com.hand.hcf.app.core.util.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by weishan on 2019/3/13.
 * 应用控制类
 */
@RestController
@RequestMapping("/api/lov")
@AllArgsConstructor
public class LovController {
    private final LovService lovService;

    @PostMapping
    public ResponseEntity<Lov> createApplication(@RequestBody Lov lov) {
        return ResponseEntity.ok(lovService.createLov(lov));
    }


    @PutMapping
    public ResponseEntity<Lov> updateApplication(@RequestBody Lov lov) {
        return ResponseEntity.ok(lovService.updateLov(lov));
    }

    /**
     * @api {DELETE} /api/component/delete/{id} 【系统框架】组件删除
     * @apiDescription 删除组件
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/component/delete/1031480700163096577
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        lovService.delete(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Lov> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lovService.getById(id));
    }


    @GetMapping("/page")
    public ResponseEntity<List<Lov>> pageAll(
            Pageable pageable,
            @RequestParam(required = false) String lovCode,
            @RequestParam(required = false) String lovName,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) Long appId
            ) {
        Page page = PageUtil.getPage(pageable);
        List<Lov> list = lovService.pageAll(page, lovCode, lovName,appId, remarks);
        return new ResponseEntity<>(list, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Lov>> listAll(
            @RequestParam(required = false) String lovCode,
            @RequestParam(required = false) String lovName) {
        List<Lov> list = lovService.listAll(lovCode,lovName);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/detail/{code}")
    public ResponseEntity getDetailByCode(@PathVariable("code") String code){
        return ResponseEntity.ok(lovService.getByCode(code));
    }
}
