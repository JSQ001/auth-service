package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import com.hand.hcf.app.expense.travel.dto.TravelApplicationTypeDTO;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeService;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
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
@Api(tags = "差旅申请单")
@RestController
@RequestMapping("/api/travel/application/type")
public class TravelApplicationTypeController {

    @Autowired
    private TravelApplicationTypeService travelApplicationTypeService;


    @PostMapping
    @ApiOperation(value = "【差旅申请单】创建", notes = "【差旅申请单】创建 开发:shouting")
    public TravelApplicationType createTravelApplicationType(@ApiParam(value = "差旅申请单类型") @RequestBody TravelApplicationTypeDTO typeDTO){
        return travelApplicationTypeService.createTravelApplicationType(typeDTO);
    }


    @PutMapping
    @ApiOperation(value = "【差旅申请单】更新", notes = "【差旅申请单】更新 开发:shouting")
    public TravelApplicationType updateTravelApplicationType(@ApiParam(value = "差旅申请单类型") @RequestBody TravelApplicationTypeDTO typeDTO){
        return travelApplicationTypeService.updateTravelApplicationType(typeDTO);
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "【差旅申请单】ID查询", notes = "【差旅申请单】ID查询 开发:shouting")
    public TravelApplicationTypeDTO getTravelApplicationTypeById(@PathVariable("id") Long id){
        return travelApplicationTypeService.getTravelApplicationTypeById(id);
    }


    @GetMapping("/pageByCondition")
    @ApiOperation(value = "【差旅申请单】条件查询", notes = "【差旅申请单】条件查询 开发:shouting")
    public ResponseEntity<List<TravelApplicationTypeDTO>> pageTravelApplicationTypeByCondition(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                                               @ApiParam(value = "差旅申请单代码") @RequestParam(value = "travelCode",required = false) String travelCode,
                                                                                               @ApiParam(value = "差旅申请单名称") @RequestParam(value = "travelName",required = false) String travelName,
                                                                                               @ApiParam(value = "是否启用") @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                               @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                               @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeDTO> result = travelApplicationTypeService.pageTravelApplicationTypeByCondition(setOfBooksId, travelCode, travelName, enabled, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/query/created")
    @ApiOperation(value = "【差旅申请单】查询已创建申请单类型", notes = "【差旅申请单】查询已创建申请单类型 开发:shouting")
    public ResponseEntity<List<TravelApplicationType>> queryCreatedType(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                        @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(travelApplicationTypeService.queryCreatedType(setOfBooksId,enabled));
    }


    @GetMapping("/query/header/{id}")
    @ApiOperation(value = "【差旅申请单】头创建时查询", notes = "【差旅申请单】头创建时查询 开发:shouting")
    public ResponseEntity<TravelApplicationTypeDimensionDTO> queryByHeaderCreated(@PathVariable("id") Long id){

        return ResponseEntity.ok(travelApplicationTypeService.queryTypeAndDimensionById(id, true));
    }


    @GetMapping("/query/expense/type")
    @ApiOperation(value = "【差旅申请单】查询申请类型", notes = "【差旅申请单】查询申请类型 开发:shouting")
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseType(@ApiParam(value = "申请类型ID") @RequestParam("applicationTypeId") Long applicationTypeId,
                                                                    @ApiParam(value = "大类ID") @RequestParam(value = "categoryId",required = false) Long categoryId,
                                                                    @ApiParam(value = "费用类型名称") @RequestParam(value = "expenseTypeName", required = false) String expenseTypeName,
                                                                    @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                    @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        List<ExpenseTypeWebDTO> result = travelApplicationTypeService.queryExpenseTypeByApplicationTypeId(applicationTypeId, categoryId, expenseTypeName, mybatisPage);
        HttpHeaders headers = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }


    @GetMapping("/query/all")
    @ApiOperation(value = "【差旅申请单】所有查询", notes = "【差旅申请单】所有查询 开发:shouting")
    public ResponseEntity<List<TravelApplicationType>> queryAllType(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                    @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(travelApplicationTypeService.queryAllType(setOfBooksId,enabled));
    }
}
