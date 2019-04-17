package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import com.hand.hcf.app.expense.travel.dto.TravelApplicationTypeDTO;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeService;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationTypeDimensionDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.core.util.PageUtil;
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
@RequestMapping("/api/travel/application/type")
public class TravelApplicationTypeController {
    /**
     * @apiDefine Travel 差旅申请单
     */
    @Autowired
    private TravelApplicationTypeService travelApplicationTypeService;

    /**
     * @api {POST} /api/travel/application/type 【差旅申请单】创建
     * @apiGroup Travel
     */
    @PostMapping
    public TravelApplicationType createTravelApplicationType(@RequestBody TravelApplicationTypeDTO typeDTO){
        return travelApplicationTypeService.createTravelApplicationType(typeDTO);
    }

    /**
     * @api {PUT} /api/travel/application/type 【差旅申请单】更新
     * @apiGroup Travel
     */
    @PutMapping
    public TravelApplicationType updateTravelApplicationType(@RequestBody TravelApplicationTypeDTO typeDTO){
        return travelApplicationTypeService.updateTravelApplicationType(typeDTO);
    }

    /**
     * @api {GET} /api/travel/application/type/{id} 【差旅申请单】ID查询
     * @apiGroup Travel
     */
    @GetMapping("/{id}")
    public TravelApplicationTypeDTO getTravelApplicationTypeById(@PathVariable("id") Long id){
        return travelApplicationTypeService.getTravelApplicationTypeById(id);
    }

    /**
     * @api {GET} /api/travel/application/type/pageByCondition 【差旅申请单】条件查询
     * @apiGroup Travel
     * @apiParam {Long} setOfBooksId  账套ID
     * @apiParam {String} [travelCode]  差旅申请单代码
     * @apiParam {String} [travelName]  差旅申请单名称
     * @apiParam {Boolean} [enabled]  是否启用
     * @apiParam {int} [page]
     * @apiParam {int} [size]
     */
    @GetMapping("/pageByCondition")
    public ResponseEntity<List<TravelApplicationTypeDTO>> pageTravelApplicationTypeByCondition(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                                               @RequestParam(value = "travelCode",required = false) String travelCode,
                                                                                               @RequestParam(value = "travelName",required = false) String travelName,
                                                                                               @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                               @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                               @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeDTO> result = travelApplicationTypeService.pageTravelApplicationTypeByCondition(setOfBooksId, travelCode, travelName, enabled, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/travel/application/type/query/created 【差旅申请单】查询已创建申请单类型
     * @apiDescription  查询账套下的所有已创建的申请单类型（前端查询条件下拉框)
     * @apiGroup Travel
     */
    @GetMapping("/query/created")
    public ResponseEntity<List<TravelApplicationType>> queryCreatedType(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                        @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(travelApplicationTypeService.queryCreatedType(setOfBooksId,enabled));
    }

    /**
     * @api {GET} /api/travel/application/type/query/header/{id} 【差旅申请单】头创建时查询
     * @apiDescription  申请单头创建时，根据类型ID查询分配的维度，以便动态生成表单
     * @apiGroup Travel
     */
    @GetMapping("/query/header/{id}")
    public ResponseEntity<TravelApplicationTypeDimensionDTO> queryByHeaderCreated(@PathVariable("id") Long id){

        return ResponseEntity.ok(travelApplicationTypeService.queryTypeAndDimensionById(id, true));
    }

    /**
     * @api {GET} /api/travel/application/type/query/expense/type 【差旅申请单类型】查询申请类型
     * @apiDescription  创建单据行时，查询该类型分配的申请类型详细信息
     * @apiGroup Travel
     */
    @GetMapping("/query/expense/type")
    public ResponseEntity<List<ExpenseTypeWebDTO>> queryExpenseType(@RequestParam("applicationTypeId") Long applicationTypeId,
                                                                    @RequestParam(value = "categoryId",required = false) Long categoryId,
                                                                    @RequestParam(value = "expenseTypeName", required = false) String expenseTypeName,
                                                                    @RequestParam(value = "page",defaultValue = "0") int page,
                                                                    @RequestParam(value = "size",defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        List<ExpenseTypeWebDTO> result = travelApplicationTypeService.queryExpenseTypeByApplicationTypeId(applicationTypeId, categoryId, expenseTypeName, mybatisPage);
        HttpHeaders headers = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/travel/application/type/query/all 【差旅申请单类型】所有查询
     * @apiDescription  查询账套下的所有的差旅申请单类型（前端查询条件下拉框)
     * @apiGroup Travel
     */
    @GetMapping("/query/all")
    public ResponseEntity<List<TravelApplicationType>> queryAllType(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                    @RequestParam(value = "enabled", required = false) Boolean enabled){

        return ResponseEntity.ok(travelApplicationTypeService.queryAllType(setOfBooksId,enabled));
    }
}
