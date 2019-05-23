package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeService;
import com.hand.hcf.app.prepayment.web.dto.CashPayRequisitionTypeDTO;
import com.hand.hcf.app.prepayment.web.dto.TypeDTO;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/10/24.
 */
@Api(tags = "预付款单类型定义")
@RestController
@RequestMapping("/api/cash/pay/requisition/types")
public class CashPayRequisitionTypeController {
    private final CashPayRequisitionTypeService cashSobPayReqTypeService;

    public CashPayRequisitionTypeController(CashPayRequisitionTypeService cashSobPayReqTypeService){
        this.cashSobPayReqTypeService = cashSobPayReqTypeService;
    }

    /**
     * 新增 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增 预付款单类型定义", notes = "新增 预付款单类型定义 开发:韩雪")
    public ResponseEntity<CashPayRequisitionType> createCashPayRequisitionType(@ApiParam(value = "预付款单类型定义") @RequestBody @NotNull CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        return ResponseEntity.ok(cashSobPayReqTypeService.createCashPayRequisitionType(cashPayRequisitionTypeDTO));
    }

    /**
     * 修改 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改 预付款单类型定义", notes = "修改 预付款单类型定义 开发:韩雪")
    public ResponseEntity<CashPayRequisitionType> updateCashPayRequisitionType(@ApiParam(value = "预付款单类型定义") @RequestBody CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        return ResponseEntity.ok(cashSobPayReqTypeService.updateCashPayRequisitionType(cashPayRequisitionTypeDTO));
    }

    /**
     * 根据ID查询 预付款单类型定义
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询 预付款单类型定义", notes = "根据ID查询 预付款单类型定义 开发:韩雪")
    public ResponseEntity<CashPayRequisitionTypeDTO> getCashPayRequisitionType(@PathVariable Long id){
        return ResponseEntity.ok(cashSobPayReqTypeService.getCashPayRequisitionType(id));
    }

    /**
     * 自定义条件查询 预付款单类型定义(分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param paymentMethodCategory
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    @ApiOperation(value = "自定义条件查询 预付款单类型定义(分页)", notes = "自定义条件查询 预付款单类型定义(分页) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @ApiParam(value = "预付款单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "预付款单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
            @ApiParam(value = "付款方式类型") @RequestParam(value = "paymentMethodCategory",required = false) String paymentMethodCategory,
            @ApiParam(value = "付款方式") @RequestParam(value = "paymentType",required = false) String paymentType,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeByCond(setOfBookId,typeCode,typeName,paymentMethodCategory,paymentType,isEnabled,page,false);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/types/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 预付款单类型定义(分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param paymentMethodCategory
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/enable/dataAuth")
    @ApiOperation(value = "自定义条件查询 预付款单类型定义,分页", notes = "自定义条件查询 预付款单类型定义,分页 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeByCondEnableDataAuth(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @ApiParam(value = "预付款单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "预付款单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
            @ApiParam(value = "付款方式类型") @RequestParam(value = "paymentMethodCategory",required = false) String paymentMethodCategory,
            @ApiParam(value = "付款方式") @RequestParam(value = "paymentType",required = false) String paymentType,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeByCond(setOfBookId,typeCode,typeName,paymentMethodCategory,paymentType,isEnabled,page,true);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/types/query/enable/dataAuth");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 预付款单类型定义(不分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @return
     */
    @GetMapping("/queryAll")
    @ApiOperation(value = "自定义条件查询 预付款单类型定义,不分页", notes = "自定义条件查询 预付款单类型定义,不分页 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeAllByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @ApiParam(value = "预付款单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "预付款单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
            @ApiParam(value = "公司code") @RequestParam(value = "companyCode",required = false) String companyCode,
            @ApiParam(value = "公司名称") @RequestParam(value = "companyName",required = false) String companyName,
            @ApiParam(value = "公司ID") @RequestParam(value = "companyId",required = false)Long companyId,
            @ApiParam(value = "是否启用")@RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiParam(value = "分配权限") @RequestParam(value = "assginEnable",required = false) Boolean assginEnable
            ){
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeAllByCond(setOfBookId,typeCode,typeName,isEnabled,companyCode,companyName,companyId,assginEnable);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据预付款单类型id，获取其下已分配的现金事务分类
     * 为预付款单提供
     *
     * @param typeId
     * @return
     */
    @GetMapping("/queryTransactionClassByTypeId/{typeId}")
    @ApiOperation(value = "根据预付款单类型id，获取其下已分配的现金事务分类", notes = "根据预付款单类型id，获取其下已分配的现金事务分类 开发:韩雪")
    public ResponseEntity<List<CashTransactionClassCO>> getTransactionClassByTypeId(@PathVariable Long typeId){
        return ResponseEntity.ok(cashSobPayReqTypeService.getTransactionClassByTypeId(typeId));
    }

    /**
     * 给新建预付款时选择预付款单类型提供
     * 根据想要新建预付款的人来筛选预付款单类型
     *
     * @param userId
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param isEnabled
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "获取用户有权限创建的预付款单类型", notes = "获取用户有权限创建的预付款单类型 修改： 赵旭东")
    @GetMapping("/queryByEmployeeId")
    public ResponseEntity<List<CashPayRequisitionType>> getCashPayRequisitionTypeByEmployeeId(
            @ApiParam(value = "当前用户ID") @RequestParam Long userId,
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId", required = false) Long setOfBookId,
            @ApiParam(value = "预付款单类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "预付款单类型名称") @RequestParam(value = "typeName", required = false) String typeName,
            @ApiParam(value = "是否启用") @RequestParam(value = "isEnabled", required = false) Boolean isEnabled,
            @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag) throws URISyntaxException {
//            Pageable pageable) throws URISyntaxException {
//        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionType> list = cashSobPayReqTypeService.getCashPayRequisitionTypeByEmployeeId(userId,setOfBookId,typeCode,typeName,isEnabled, authFlag);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/types/queryByEmployeeId");
//        return new ResponseEntity(list.getRecords(),headers, HttpStatus.OK);
        return ResponseEntity.ok(list);
    }


    /**
     * @apiDescription 通过id查询预付款单类型名称和code
     * @api {get} /api/cash/pay/requisition/types/get/by/id?id=969399702304866306 【预付款单类型】 查名称和code
     * @apiGroup PrepaymentService
     * @apiParam {Long} id 预付款单类型ID
     *@apiSuccessExample {json} 成功返回值
    {
    "id": "969399702304866306",
    "code": "YFK001",
    "name": "预付款单走工作流"
    }
     */
    @GetMapping("/get/by/id")
    @ApiOperation(value = "通过id查询预付款单类型名称和code", notes = "通过id查询预付款单类型名称和code 开发:韩雪")
    public ResponseEntity<TypeDTO> getTypeById(@ApiParam(value = "预付款单类型ID") @RequestParam(value = "id") Long id){
        CashPayRequisitionType type = cashSobPayReqTypeService.selectById(id);
        if(type==null){
            return null;
        }
        TypeDTO dto = new TypeDTO();
        dto.setId(type.getId());
        dto.setCode(type.getTypeCode());
        dto.setName(type.getTypeName());
        return ResponseEntity.ok(dto);
    }

    /**
     * @api {GET} /api/cash/pay/requisition/types/users 【预付款单类型】根据单据id查询有该单据权限的用户
     */
    @GetMapping("/users")
    @ApiOperation(value = "【预付款单类型】根据单据id查询有该单据权限的用户", notes = "【预付款单类型】根据单据id查询有该单据权限的用户 开发:韩雪")
    public ResponseEntity listUsersByCashSobPayReqTypeId(@ApiParam(value = "预付款单类型ID") @RequestParam(value = "payReqTypeId") Long payReqTypeId,
                                                         @ApiParam(value = "用户code") @RequestParam(required = false) String userCode,
                                                         @ApiParam(value = "用户名称") @RequestParam(required = false) String userName,
                                                         @ApiParam(value = "当前页") @RequestParam(defaultValue = "0") int page,
                                                         @ApiParam(value = "每页多少条") @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = cashSobPayReqTypeService.listUsersByCashSobPayReqTypeId(payReqTypeId,
                userCode, userName, queryPage);

        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
