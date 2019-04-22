package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignDimension;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeAssignDimensionService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@RestController
@RequestMapping("/api/travel/application/type/dimension")
public class TravelApplicationTypeAssignDimensionController {

    @Autowired
    private TravelApplicationTypeAssignDimensionService assignDimensionService;

    /**
     * @api {POST} /api/travel/application/type/dimension 【差旅申请单维度设置】创建
     * @apiGroup Travel
     */
    @PostMapping
    public TravelApplicationTypeAssignDimension createAssignDimension(@RequestBody TravelApplicationTypeAssignDimension assignDimension){
        return assignDimensionService.createAssignDimension(assignDimension);
    }

    /**
     * @api {PUT} /api/travel/application/type/dimension 【差旅申请单维度设置】更新
     * @apiGroup Travel
     */
    @PutMapping
    public TravelApplicationTypeAssignDimension updateAssignDimension(@RequestBody TravelApplicationTypeAssignDimension assignDimension){
        return assignDimensionService.updateAssignDimension(assignDimension);
    }

    /**
     * @api {DELETE} /api/travel/application/type/dimension/{id} 【差旅申请单维度设置】ID删除
     * @apiGroup Travel
     */
    @DeleteMapping("/{id}")
    public Boolean deleteAssignDimensionById(@PathVariable("id") Long id){
        return assignDimensionService.deleteAssignDimensionById(id);
    }

    /**
     * @api {GET} /api/travel/application/type/dimension/pageAssignDimension 【差旅申请单维度设置】条件查询
     * @apiGroup Travel
     * @apiParam {Long} travelTypeId  差旅申请单ID
     * @apiParam {int} [page]
     * @apiParam {int} [size]
     */
    @GetMapping("/pageAssignDimension")
    public ResponseEntity<List<TravelApplicationTypeAssignDimension>> pageAssignDimension(@RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                                          @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                          @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeAssignDimension> result = assignDimensionService.pageAssignDimension(travelTypeId, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/travel/application/type/dimension/{travelTypeId}/query/filter 【差旅申请单维度设置】未分配维度查询
     * @apiGroup Travel
     * @apiParam {Long} travelTypeId  差旅申请单ID
     * @apiParam {Long} setOfBooksId  账套ID
     * @apiParam {String} [dimensionCode]  维度代码
     * @apiParam {String} [dimensionName]  维度名称
     * @apiParam {Boolean} [enabled]  是否启用
     */
    @GetMapping("/{travelTypeId}/query/filter")
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("travelTypeId") Long travelTypeId,
                                                            @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                            @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                            @RequestParam(value = "enabled",required = false) Boolean enabled) {
        List<DimensionCO> result = assignDimensionService.listDimensionByConditionFilter(travelTypeId,setOfBooksId,dimensionCode,dimensionName,enabled);
        return result;
    }
}
