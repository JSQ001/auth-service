package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import com.hand.hcf.app.expense.travel.service.TravelApplicationHeaderService;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationHeaderWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineWebDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;


/**
 * 差旅申请单头前端控制器
 * @author zhu.zhao
 * @date 2019/3/11
 */
@Api(tags = "差旅申请单头前端控制器")
@RestController
@RequestMapping("/api/travel/application")
public class TravelApplicationDocumentController {
    @Autowired
    private TravelApplicationHeaderService service;


    @PostMapping("/header")
    @ApiOperation(value = "分页查询地点级别", notes = "分页查询地点级别信息 开发:zhu.zhao")
    public ResponseEntity createHeader(@ApiParam(value = "差旅申请单头") @RequestBody @Validated TravelApplicationHeaderWebDTO dto) {

        return ResponseEntity.ok(service.createHeader(dto));
    }

    /**
     * 根据ID查询申请单头信息，编辑时用
     *
     * @param id
     * @return
     */
    @GetMapping("/header/query")
    @ApiOperation(value = "根据ID查询申请单头信息，编辑时用", notes = "根据ID查询申请单头信息，编辑时用 开发:zhu.zhao")
    public ResponseEntity<TravelApplicationHeaderWebDTO> getHeaderInfoById(@ApiParam(value = "id") @RequestParam("id") Long id) {
        return ResponseEntity.ok(service.getHeaderInfoById(id));
    }

    /**
     * 我的申请单条件查询
     *
     * @param documentNumber
     * @param typeId
     * @param dateFrom
     * @param dateTo
     * @param amountFrom
     * @param amountTo
     * @param status
     * @param currencyCode
     * @param remarks
     * @param employeeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/header/query/condition")
    @ApiOperation(value = "我的申请单条件查询", notes = "我的申请单条件查询 开发:zhu.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listByCondition(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                          @ApiParam(value = "类型ID") @RequestParam(value = "typeId", required = false) Long typeId,
                                          @ApiParam(value = "日期从") @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @ApiParam(value = "日期至") @RequestParam(value = "dateTo", required = false) String dateTo,
                                          @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                          @ApiParam(value = "金额至") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                          @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
                                          @ApiParam(value = "币种") @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                          @ApiParam(value = "备注") @RequestParam(value = "remarks", required = false) String remarks,
                                          @ApiParam(value = "员工id") @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          @ApiIgnore Pageable pageable) throws URISyntaxException {

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<TravelApplicationHeaderWebDTO> result = service.listHeaderDTOsByCondition(page,
                documentNumber,
                typeId,
                requisitionDateFrom,
                requisitionDateTo,
                amountFrom,
                amountTo,
                status,
                currencyCode,
                remarks,
                employeeId);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/header")
    @ApiOperation(value = "更新差旅申请单头", notes = "更新差旅申请单头 开发:zhu.zhao")
    public ResponseEntity updateHeader(@ApiParam(value = "差旅申请单头") @RequestBody TravelApplicationHeaderWebDTO dto) {
        return ResponseEntity.ok(service.updateHeader(dto));
    }

    @DeleteMapping("/header/{id}")
    @ApiOperation(value = "删除差旅申请单头", notes = "删除差旅申请单头 开发:zhu.zhao")
    public ResponseEntity deleteHeader(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteHeader(id));
    }

    @GetMapping("/line/query/info")
    @ApiOperation(value = "查询其他信息", notes = "查询其他信息 开发:zhu.zhao")
    public ResponseEntity queryOtherInfo(@ApiParam(value = "单头ID") @RequestParam("headerId") Long headerId,
                                         @ApiParam(value = "ID") @RequestParam(value = "lineId", required = false) Long id,
                                         @ApiParam(value = "是否新建") @RequestParam(value = "isNew", defaultValue = "true") Boolean isNew) {
        return ResponseEntity.ok(service.queryLineInfo(headerId, id, isNew));
    }

    /**
     * 创建行
     *
     * @param dto
     * @return
     */
    @PostMapping("/line")
    @ApiOperation(value = "创建行", notes = "创建行 开发:zhu.zhao")
    public ResponseEntity createLine(@ApiParam(value = "差旅申请单行") @RequestBody TravelApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.createLine(dto));
    }

    /**
     * 更新行
     *
     * @param dto
     * @return
     */
    @PutMapping("/line")
    @ApiOperation(value = "更新行", notes = "更新行 开发:zhu.zhao")
    public ResponseEntity updateLine(@ApiParam(value = "差旅申请单行") @RequestBody TravelApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.updateLine(dto));
    }

    /**
     * 删除行
     *
     * @param id
     * @return
     */
    @DeleteMapping("/line/{id}")
    @ApiOperation(value = "删除行", notes = "删除行 开发:zhu.zhao")
    public ResponseEntity deleteLine(@PathVariable("id") Long id) {

        return ResponseEntity.ok(service.deleteLineByLineId(id));
    }

    /**
     * 点击详情查询单据头信息
     *
     * @param id
     * @return
     */
    @GetMapping("/header/{id}")
    @ApiOperation(value = "点击详情查询单据头信息", notes = "点击详情查询单据头信息 开发:zhu.zhao")
    public ResponseEntity getHeaderDetail(@PathVariable("id") Long id) {

        return ResponseEntity.ok(service.getHeaderDetailInfo(id));
    }

    @GetMapping("/line/query/{id}")
    @ApiOperation(value = "根据头ID获取行", notes = "根据头ID获取行 开发:zhu.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<TravelApplicationLineWebDTO>> getLinesByHeaderId(@PathVariable("id") Long id,
                                                                                @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<TravelApplicationLineWebDTO> result = service.getLinesByHeaderId(id, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询动态维度列信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "查询动态维度列信息", notes = "查询动态维度列信息 开发:zhu.zhao")
    @GetMapping("/line/column/{id}")
    public ResponseEntity queryDimensionColumn(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.queryDimensionColumn(id));
    }


    @ApiOperation(value = "提交工作流", notes = "提交工作流 开发:zhu.zhao")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity submit(@ApiParam(value = "工作流单据参数") @RequestBody WorkFlowDocumentRefCO workFlowDocumentRef) {
        return ResponseEntity.ok(service.submit(workFlowDocumentRef));
    }

    /**
     * 更新行明细
     *
     * @param lineDetail
     * @return
     */
    @PutMapping("/line/detail")
    @ApiOperation(value = "更新行明细", notes = "更新行明细 开发:zhu.zhao")
    public ResponseEntity updateLineDetail(@ApiParam(value = "差旅申请单明细行表") @RequestBody TravelApplicationLineDetail lineDetail) {

        return ResponseEntity.ok(service.updateLineDetail(lineDetail));
    }
}
