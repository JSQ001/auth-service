package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignDimension;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeAssignDimensionService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "差旅申请单维度设置")
@RestController
@RequestMapping("/api/travel/application/type/dimension")
public class TravelApplicationTypeAssignDimensionController {

    @Autowired
    private TravelApplicationTypeAssignDimensionService assignDimensionService;


    @PostMapping
    @ApiOperation(value = "【差旅申请单维度设置】创建", notes = "【差旅申请单维度设置】创建 开发:shouting.cheng")
    public TravelApplicationTypeAssignDimension createAssignDimension(@ApiParam(value = "差旅申请单类型关联维度") @RequestBody TravelApplicationTypeAssignDimension assignDimension){
        return assignDimensionService.createAssignDimension(assignDimension);
    }


    @PutMapping
    @ApiOperation(value = "【差旅申请单维度设置】更新", notes = "【差旅申请单维度设置】更新 开发:shouting.cheng")
    public TravelApplicationTypeAssignDimension updateAssignDimension(@ApiParam(value = "差旅申请单类型关联维度") @RequestBody TravelApplicationTypeAssignDimension assignDimension){
        return assignDimensionService.updateAssignDimension(assignDimension);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "【差旅申请单维度设置】ID删除", notes = "【差旅申请单维度设置】ID删除 开发:shouting.cheng")
    public Boolean deleteAssignDimensionById(@PathVariable("id") Long id){
        return assignDimensionService.deleteAssignDimensionById(id);
    }


    @GetMapping("/pageAssignDimension")
    @ApiOperation(value = "【差旅申请单维度设置】ID删除", notes = "【差旅申请单维度设置】ID删除 开发:shouting.cheng")
    public ResponseEntity<List<TravelApplicationTypeAssignDimension>> pageAssignDimension(@ApiParam(value = "差旅申请单ID") @RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                                          @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                          @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeAssignDimension> result = assignDimensionService.pageAssignDimension(travelTypeId, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/{travelTypeId}/query/filter")
    @ApiOperation(value = "【差旅申请单维度设置】未分配维度查询", notes = "【差旅申请单维度设置】未分配维度查询 开发:shouting.cheng")
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("travelTypeId") Long travelTypeId,
                                                            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @ApiParam(value = "维度代码") @RequestParam(value = "dimensionCode",required = false) String dimensionCode,
                                                            @ApiParam(value = "维度名称") @RequestParam(value = "dimensionName",required = false) String dimensionName,
                                                            @ApiParam(value = "是否启用") @RequestParam(value = "enabled",required = false) Boolean enabled) {
        List<DimensionCO> result = assignDimensionService.listDimensionByConditionFilter(travelTypeId,setOfBooksId,dimensionCode,dimensionName,enabled);
        return result;
    }
}
