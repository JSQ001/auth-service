package com.hand.hcf.app.expense.adjust.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustHeaderService;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustHeaderWebDTO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;

import com.hand.hcf.app.core.util.TypeConversionUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *  费用调整单单据头信息controller
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5
 */
@Api(tags = "费用调整单单据头信息controller")
@RestController
@RequestMapping("/api/expense/adjust/headers")
public class ExpenseAdjustHeaderController {

    @Autowired
    private ExpenseAdjustHeaderService headerService;

    @Autowired
    private ExcelExportService excelService;


    @ApiOperation(value = "提交工作流", notes = "提交工作流 开发:bin.xie")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity<Boolean> submit(@ApiParam(value = "提交工作流") @RequestBody WorkFlowDocumentRefCO workFlowDocumentRef) {
        return ResponseEntity.ok(headerService.submit(workFlowDocumentRef));
    }

    /**api/expense/application/header
     * 创建费用调整单单据头
     * @param dto
     * @return
     */
    @ApiOperation(value = "创建费用调整单单据头", notes = "创建费用调整单单据头 开发:bin.xie")
    @PostMapping
    public ResponseEntity<ExpenseAdjustHeader> createHeaders(@ApiParam(value = "创建费用调整单单据头") @RequestBody ExpenseAdjustHeaderWebDTO dto) {
        return ResponseEntity.ok( headerService.createHeader(dto) );
    }

    /**
     * 更新费用调整单
     * @param dto
     * @return
     */
    @ApiOperation(value = "更新费用调整单", notes = "更新费用调整单 开发:bin.xie")
    @PutMapping
    public ResponseEntity<ExpenseAdjustHeader> updateHeaders(@ApiParam(value = "更新费用调整单") @RequestBody ExpenseAdjustHeaderWebDTO dto) {

        return ResponseEntity.ok( headerService.updateHeaders(dto));
    }
    @ApiOperation(value = "查找费用调整单", notes = "查找费用调整单 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query/dto")
    public ResponseEntity<List<ExpenseAdjustHeaderWebDTO>> findExpenseAdjustHeaderDTO(@ApiParam(value = "费用调整单编号") @RequestParam(required = false) String documentNumber,
                                                                                      @ApiParam(value = "费用调整类型ID") @RequestParam(required = false,value = "expAdjustTypeId") Long expAdjustTypeId,
                                                                                      @ApiParam(value = "状态") @RequestParam(required = false,value = "status") String status,
                                                                                      @ApiParam(value = "日期时间从") @RequestParam(required = false,value ="applyDateFrom" ) String dateTimeFrom,
                                                                                      @ApiParam(value = "日期时间到") @RequestParam(required = false,value = "applyDateTo") String dateTimeTo,
                                                                                      @ApiParam(value = "最小金额") @RequestParam(required = false,value = "amountFrom") BigDecimal amountMin,
                                                                                      @ApiParam(value = "最大金额") @RequestParam(required = false,value = "amountTo") BigDecimal amountMax,
                                                                                      @ApiParam(value = "申请人id") @RequestParam(required = false,value = "applyId") Long employeeId,
                                                                                      @ApiParam(value = "描述") @RequestParam(required = false,value = "description") String description,
                                                                                      @ApiParam(value = "调整类型") @RequestParam(required = false,value = "adjustTypeCategory") String adjustTypeCategory,
                                                                                      @ApiParam(value = "币种") @RequestParam(required = false,value = "currency") String currencyCode,
                                                                                      @ApiParam(value = "部门ID") @RequestParam(required = false,value = "unitId") Long unitId,
                                                                                      @ApiParam(value = "公司ID") @RequestParam(required = false,value = "companyId") Long companyId,
                                                                                      @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateTimeFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTimeTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        List<ExpenseAdjustHeaderWebDTO> result = headerService.listHeaderWebDTOByCondition(
                documentNumber, expAdjustTypeId, status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax,
                employeeId, description, adjustTypeCategory, currencyCode,unitId,companyId,false, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotal());
        headers.add("Link", "/api/expense/adjust/headers/query/dto");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "查找费用调整单", notes = "查找费用调整单 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query/dto/enable/dataAuth")
    public ResponseEntity<List<ExpenseAdjustHeaderWebDTO>> findExpenseAdjustHeaderDTOEnableDataAuth(@ApiParam(value = "费用调整单编号") @RequestParam(required = false) String documentNumber,
                                                                                      @ApiParam(value = "费用调整类型ID") @RequestParam(required = false,value = "expAdjustTypeId") Long expAdjustTypeId,
                                                                                      @ApiParam(value = "状态") @RequestParam(required = false,value = "status") String status,
                                                                                      @ApiParam(value = "日期时间从") @RequestParam(required = false,value ="applyDateFrom" ) String dateTimeFrom,
                                                                                      @ApiParam(value = "日期时间到") @RequestParam(required = false,value = "applyDateTo") String dateTimeTo,
                                                                                      @ApiParam(value = "最小金额") @RequestParam(required = false,value = "amountFrom") BigDecimal amountMin,
                                                                                      @ApiParam(value = "最大金额") @RequestParam(required = false,value = "amountTo") BigDecimal amountMax,
                                                                                      @ApiParam(value = "申请人id") @RequestParam(required = false,value = "applyId") Long employeeId,
                                                                                      @ApiParam(value = "描述") @RequestParam(required = false,value = "description") String description,
                                                                                      @ApiParam(value = "调整类型") @RequestParam(required = false,value = "adjustTypeCategory") String adjustTypeCategory,
                                                                                      @ApiParam(value = "币种") @RequestParam(required = false,value = "currency") String currencyCode,
                                                                                      @ApiParam(value = "部门ID") @RequestParam(required = false,value = "unitId") Long unitId,
                                                                                      @ApiParam(value = "公司ID") @RequestParam(required = false,value = "companyId") Long companyId,
                                                                                                    @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateTimeFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTimeTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        List<ExpenseAdjustHeaderWebDTO> result = headerService.listHeaderWebDTOByCondition(
                documentNumber, expAdjustTypeId, status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax,
                employeeId, description, adjustTypeCategory, currencyCode,unitId,companyId,true, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotal());
        headers.add("Link", "/api/expense/adjust/headers/query/dto/enable/dataAuth");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @GetMapping("/query/id")
    @ApiOperation(value = "根据ID查询费用调整单头", notes = "根据ID查询费用调整单头 开发:bin.xie")
    public ResponseEntity queryHeaderById(@ApiParam(value = "费用调整单头ID") @RequestParam("expAdjustHeaderId") Long expAdjustHeaderId){

        return ResponseEntity.ok(headerService.getHeaderDTOById(expAdjustHeaderId));
    }

    @GetMapping("/query/dimension/dto")
    @ApiOperation(value = "根据类型ID查询维度", notes = "根据类型ID查询维度 开发:bin.xie")
    public ResponseEntity queryDimensionDTOByTypeId(@ApiParam(value = "头ID") @RequestParam("headerId") Long headerId){
        return ResponseEntity.ok(headerService.queryDimensionDTOByTypeId(headerId));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据ID删除费用调整单头", notes = "根据类型ID查询维度 开发:bin.xie")
    public ResponseEntity<Boolean> deleteHeaderById(@PathVariable("id") Long id){

        return ResponseEntity.ok(headerService.deleteHeaderById(id));
    }

    /**
     * @api {GET} /api/expense/adjust/headers/query/created 查询已创建调整单的申请人
     */
    @GetMapping("/query/created")
    @ApiOperation(value = "查询已创建调整单的申请人", notes = "查询已创建调整单的申请人 开发:bin.xie")
    public List<ContactCO> listUsersByCreatedAdjustHeaders(){

        return headerService.listUsersByCreatedAdjustHeaders();
    }

    /**
     * {GET}    /api/expense/adjust/headers/approvals/filter
     * @param finished
     * @param expAdjustTypeId
     * @param adjustTypeCategory
     * @param fullName
     * @param beginDate
     * @param endDate
     * @param currencyCode
     * @param amountMin
     * @param amountMax
     * @param description
     * @param pageable
     * @return
     */
    @GetMapping("/approvals/filters")
    @ApiOperation(value = "费用调整单许可列表", notes = "费用调整单许可列表 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listExpenseAdjustApprovals(@ApiParam(value = "完成状态") @RequestParam(value = "finished", required = false) boolean finished,
                                                    @ApiParam(value = "费用调整单编号") @RequestParam(required = false) String documentNumber,
                                                    @ApiParam(value = "费用调整类型ID") @RequestParam(required = false) Long expAdjustTypeId,
                                                    @ApiParam(value = "调整类型") @RequestParam(required = false) String adjustTypeCategory,
                                                    @ApiParam(value = "全称") @RequestParam(required = false) String fullName,
                                                    @ApiParam(value = "申请人id") @RequestParam(required = false) Long employeeId,
                                                    @ApiParam(value = "开始日期") @RequestParam(required = false) String beginDate,
                                                    @ApiParam(value = "结束日期") @RequestParam(required = false) String endDate,
                                                    @ApiParam(value = "币种") @RequestParam(required = false) String currencyCode,
                                                    @ApiParam(value = "最小数量") @RequestParam(required = false) BigDecimal amountMin,
                                                    @ApiParam(value = "最大数量") @RequestParam(required = false) BigDecimal amountMax,
                                                    @ApiParam(value = "描述") @RequestParam(required = false) String description,
                                                    @ApiIgnore Pageable pageable){
        Page mybatisPage = PageUtil.getPage(pageable);

        beginDate = StringUtils.isEmpty(beginDate) ? null : beginDate;
        endDate = StringUtils.isEmpty(endDate) ? null : endDate;

        List<ExpenseAdjustHeaderWebDTO> result = headerService.listExpenseAdjustApprovals(finished,documentNumber,expAdjustTypeId, adjustTypeCategory, fullName, employeeId, beginDate, endDate, currencyCode, amountMin, amountMax, description, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }
/*************
  * 导出费用调整单
  * ***************/
    @RequestMapping(value = "export/query/dto")
    @ApiOperation(value = "导出费用调整单", notes = "导出费用调整单 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public void exportExpenseAdjustHeader(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @ApiParam(value = "费用调整单编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                          @ApiParam(value = "费用调整类型ID") @RequestParam(value = "expAdjustTypeId",required = false) Long expAdjustTypeId,
                                          @ApiParam(value = "状态") @RequestParam(value = "status",required = false) String status,
                                          @ApiParam(value = "日期时间从") @RequestParam(value = "applyDateFrom",required = false) String dateTimeFrom,
                                          @ApiParam(value = "日期时间到") @RequestParam(value = "applyDateTo",required = false) String dateTimeTo,
                                          @ApiParam(value = "最小金额") @RequestParam(value = "amountFrom",required = false) BigDecimal amountMin,
                                          @ApiParam(value = "最大金额") @RequestParam(value = "amountTo",required = false) BigDecimal amountMax,
                                          @ApiParam(value = "申请人id") @RequestParam(value = "applyId",required = false) Long employeeId,
                                          @ApiParam(value = "描述") @RequestParam(value = "description",required = false) String description,
                                          @ApiParam(value = "调整类型") @RequestParam(value = "adjustTypeCategory",required = false) String adjustTypeCategory,
                                          @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                          @ApiParam(value = "部门ID") @RequestParam(value = "unitId",required = false) Long unitId,
                                          @ApiParam(value = "公司ID") @RequestParam(value = "companyId",required = false) Long companyId,
                                          @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                                          @ApiIgnore Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateTimeFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTimeTo);
        Page<ExpenseAdjustHeaderWebDTO> ExpenseAdjustHeaderWebDTOPage = headerService.getExpenseAdjustHeaderWebDTOByCond(
                documentNumber, expAdjustTypeId, status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax,
                employeeId, description, adjustTypeCategory, currencyCode,unitId,companyId,page);
        Integer total = TypeConversionUtils.parseInt(page.getTotal());
        int thredNumber = total > 100000 ? 8 : 2;
        excelService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpenseAdjustHeaderWebDTO, ExpenseAdjustHeaderWebDTO>(){
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpenseAdjustHeaderWebDTO> queryDataByPage(Page page) {

                Page<ExpenseAdjustHeaderWebDTO> ExpenseAdjustHeaderWebDTOPage = headerService.getExpenseAdjustHeaderWebDTOByCond(
                        documentNumber, expAdjustTypeId, status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax,
                        employeeId, description, adjustTypeCategory, currencyCode,unitId,companyId,page);
                return ExpenseAdjustHeaderWebDTOPage.getRecords();
            }

            @Override
            public ExpenseAdjustHeaderWebDTO toDTO(ExpenseAdjustHeaderWebDTO t) {
                return t;
            }

            @Override
            public Class<ExpenseAdjustHeaderWebDTO> getEntityClass() {
                return ExpenseAdjustHeaderWebDTO.class;
            }
        },thredNumber,request,response);
    }
}
