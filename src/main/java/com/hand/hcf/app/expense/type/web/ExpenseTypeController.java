package com.hand.hcf.app.expense.type.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeIcon;
import com.hand.hcf.app.expense.type.service.ExpenseTypeIconService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeAssignInfoDTO;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.expense.type.web.dto.SortBySequenceDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
@Api(tags = "费用类型")
@RestController
@RequestMapping("/api/expense/types")
public class ExpenseTypeController {
    @Autowired
    private ExpenseTypeService service;
    @Autowired
    private ExpenseTypeIconService expenseTypeIconService;


    @PostMapping
    @ApiOperation(value = "创建一个费用或申请类别", notes = "创建一个费用或申请类别 开发:bin.xie")
    public ResponseEntity createType(@ApiParam(value = "费用类型") @RequestBody @Validated ExpenseType dto){

        return ResponseEntity.ok(service.createType(dto));
    }


    @PutMapping
    @ApiOperation(value = "修改一个费用或申请类别", notes = "修改一个费用或申请类别 开发:bin.xie")
    public ResponseEntity updateType(@ApiParam(value = "费用类型") @RequestBody @Validated ExpenseType dto){
        return ResponseEntity.ok(service.updateType(dto));
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除一个费用或申请类别", notes = "删除一个费用或申请类别 开发:bin.xie")
    public ResponseEntity deleteType(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.deleteTypeById(id));
    }


    @GetMapping("/select/{id}")
    @ApiOperation(value = "根据ID查询一个费用或申请类别", notes = "根据ID查询一个费用或申请类别 开发:bin.xie")
    public ResponseEntity<ExpenseType> queryById(@PathVariable("id") Long id){

        return ResponseEntity.ok(service.getTypeById(id));
    }


    @GetMapping("/icon")
    @ApiOperation(value = "查询所有的费用图标", notes = "查询所有的费用图标 开发:bin.xie")
    public ResponseEntity<List<ExpenseTypeIcon>> queryIcon(){
        return ResponseEntity.ok(expenseTypeIconService.selectList(new EntityWrapper<ExpenseTypeIcon>().eq("enabled",true)));
    }



    @PostMapping(value = "/{expenseTypeId}/fields")
    @ApiOperation(value = "保存控件", notes = "保存控件 开发:bin.xie")
    public ResponseEntity<Boolean> saveExpenseTypeFields(@PathVariable(value = "expenseTypeId")Long expenseTypeId,
                                                         @ApiParam(value = "账套ID") @RequestBody List<ExpenseFieldDTO> fieldDTOS){
        service.saveExpenseTypeFields(expenseTypeId, fieldDTOS);
        return ResponseEntity.ok(true);
    }



    @DeleteMapping("/{expenseTypeId}/{fieldOid}")
    @ApiOperation(value = "根据控件OID删除控件", notes = "根据控件OID删除控件 开发:bin.xie")
    public ResponseEntity<Boolean> deleteExpenseTypeField(@PathVariable(value = "expenseTypeId")Long expenseTypeId,
                                                          @PathVariable(value = "fieldOid") UUID fieldOid){
        service.deleteFieldByOid(expenseTypeId, fieldOid);
        return ResponseEntity.ok(true);
    }


    @GetMapping("/{expenseTypeId}/fields")
    @ApiOperation(value = "查询控件", notes = "查询控件 开发:bin.xie")
    public ResponseEntity<List<ExpenseFieldDTO>> queryFields(@PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.queryFields(expenseTypeId));
    }


    @PostMapping("/{expenseTypeId}/assign")
    @ApiOperation(value = "保存权限", notes = "保存权限 开发:bin.xie")
    public ResponseEntity<ExpenseTypeAssignInfoDTO> saveAssignInfo(@ApiParam(value = "费用类型分配信息") @RequestBody ExpenseTypeAssignInfoDTO infoDTO,
                                                                   @PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.saveAssignInfo(infoDTO, expenseTypeId));
    }


    @GetMapping("/{expenseTypeId}/assign/query")
    @ApiOperation(value = "查询权限", notes = "查询权限 开发:bin.xie")
    public ResponseEntity<ExpenseTypeAssignInfoDTO> queryAssign(@PathVariable("expenseTypeId") Long expenseTypeId){

        return ResponseEntity.ok(service.queryAssign(expenseTypeId));

    }


    @GetMapping("/{setOfBooksId}/query")
    @ApiOperation(value = "条件查询类别", notes = "条件查询类别 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseType>> queryByCondition(@PathVariable("setOfBooksId") Long setOfBooksId,
                                                              @ApiParam(value = "编码") @RequestParam(value = "code", required = false) String code,
                                                              @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                                              @ApiParam(value = "类型标识") @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag,
                                                              @ApiParam(value = "大类ID") @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                              @ApiParam(value = "是否启用") @RequestParam(value ="enabled",required = false,defaultValue = "true") Boolean enabled,
                                                              @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> expenseTypes = service.queryByCondition(page, setOfBooksId, code, name, typeCategoryId, typeFlag,enabled);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/" + setOfBooksId + "/query");
        return new ResponseEntity<>(expenseTypes, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/query/by/category")
    @ApiOperation(value = "根据大类查询该大类下的申请类型", notes = "根据大类查询该大类下的申请类型 开发:bin.xie")
    public ResponseEntity queryByCategoryId(@ApiParam(value = "大类ID") @RequestParam("typeCategoryId") Long typeCategoryId){

        return ResponseEntity.ok(service.queryByCategoryId(typeCategoryId, 0));
    }


    @PostMapping("/sort")
    @ApiOperation(value = "申请类型或者费用类型排序", notes = "申请类型或者费用类型排序 开发:bin.xie")
    public ResponseEntity sort(@ApiParam(value = "排序") @RequestBody List<SortBySequenceDTO> dtos){

        return ResponseEntity.ok(service.sort(dtos));
    }


    @GetMapping("/query/by/document/assign")
    @ApiOperation(value = "单据类型关联费用类型LOV查询", notes = "单据类型关联费用类型LOV查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity queryLovByDocumentTypeAssign(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                              @ApiParam(value = "范围 all-全部 selected-已选 其他为未选") @RequestParam("range") String range,
                                              @ApiParam(value = "单据大类") @RequestParam("documentType") Integer documentType,
                                              @ApiParam(value = "单据大类ID") @RequestParam(value = "id", required = false) Long documentTypeId,
                                              @ApiParam(value = "代码") @RequestParam(value = "code",required = false) String code,
                                              @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                              @ApiParam(value = "费用大类ID") @RequestParam(value = "typeCategoryId",required = false) Long typeCategoryId,
                                              @ApiParam(value = "0-申请类型 1-费用类型") @RequestParam("typeFlag") Integer typeFlag,
                                                       @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> list = service.queryLovByDocumentTypeAssign(setOfBooksId, range, documentType, documentTypeId, code, name,typeCategoryId,typeFlag, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/query/by/adjust/assign");
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/chooser/query")
    @ApiOperation(value = "条件查询类别", notes = "条件查询类别 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseType>> queryLovByCondition(@ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                              @ApiParam(value = "编码") @RequestParam(value = "code", required = false) String code,
                                                              @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                                              @ApiParam(value = "0-申请类型 1-费用类型") @RequestParam(value = "typeFlag", required = false, defaultValue = "0") Integer typeFlag,
                                                              @ApiParam(value = "费用大类ID") @RequestParam(value = "typeCategoryId", required = false) Long typeCategoryId,
                                                              @ApiParam(value = "是否启用") @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                 @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> expenseTypes = service.queryByCondition(page, setOfBooksId, code, name, typeCategoryId, typeFlag, enabled);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/types/chooser/query");
        return new ResponseEntity<>(expenseTypes, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询所有费用申请单类型
     * @author sq.l
     * @date 2019/04/22
     *
     * @param code
     * @param name
     * @param categoryName
     * @Param setOfBooksId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/chooser/query/by/code")
    @ApiOperation(value = "分页查询费用类型", notes = "分页查询费用类型 开发:罗书强")
    public ResponseEntity selectAllExpenseType(@ApiParam(value = "费用类型代码") @RequestParam(value = "code",required = false) String code,
                                               @ApiParam(value = "费用类型名称") @RequestParam(value = "name",required = false) String name,
                                               @ApiParam(value = "大类名称") @RequestParam(value = "categoryName",required = false) String categoryName,
                                               @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                               @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                               @ApiParam(value = "页数") @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page myPage = PageUtil.getPage(page,size);
        List<ExpenseType> result = service.selectExpenseByCode(code, name, categoryName, setOfBooksId, myPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(myPage);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据登录人信息获取有权限使用的费用类型
     * @param categoryId
     * @param expenseTypeName
     * @param existsExpenseTypeIds
     * @param pageable
     * @return
     */
    @PostMapping("/chooser/query/by/login")
    @ApiOperation(value = "根据登录信息获取费用类型", notes = "根据登录人信息获取有权限使用的费用类型 开发:kai.zhang")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseTypeWebDTO>> getExpenseTypeByLoginUser(@ApiParam(value = "单据大类ID") @RequestParam(value = "categoryId",required = false) Long categoryId,
                                                                             @ApiParam(value = "费用类型名称") @RequestParam(value = "expenseTypeName",required = false) String expenseTypeName,
                                                                             @ApiParam(value = "费用类型范围") @RequestBody(required = false) List<Long> existsExpenseTypeIds,
                                                                             @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> expenseTypeByLoginUser = service.getExpenseTypeByLoginUser(categoryId, expenseTypeName, existsExpenseTypeIds, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expenseTypeByLoginUser, httpHeaders, HttpStatus.OK);
    }
}
