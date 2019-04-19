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

import com.hand.hcf.app.core.util.TypeConversionUtils;;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping("/api/expense/adjust/headers")
public class ExpenseAdjustHeaderController {

    @Autowired
    private ExpenseAdjustHeaderService headerService;

    @Autowired
    private ExcelExportService excelService;

    /**
     * @apiDescription 提交工作流
     * @api {POST} /api/expense/adjust/headers/submit
     * @apiGroup ExpenseService
     *
     * @apiParam {UUID} applicantOid 申请人OID
     * @apiParam {UUID} userOid 用户OID
     * @apiParam {UUID} formOid 表单OID
     * @apiParam {UUID} documentOid 单据OID
     * @apiParam {Integer} documentCategory 单据大类 （如801003)
     * @apiParam {List} countersignApproverOIDs 加签审批人OID
     * @apiParam {String} documentNumber 单据编号
     * @apiParam {String} remark 描述说明
     * @apiParam {Long} companyId 公司ID
     * @apiParam {UUID} unitOid 部门OID
     * @apiParam {String} remark 描述说明
     * @apiParam {Bigdecimal} amount 金额
     * @apiParam {String} currencyCode 币种
     * @apiParam {Long} documentTypeId 单据类型ID
     * @apiSuccessExample {json} 成功返回值:
     * [true]
     *
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity<Boolean> submit(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRef) {
        return ResponseEntity.ok(headerService.submit(workFlowDocumentRef));
    }

    /**
     * 创建费用调整单单据头
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<ExpenseAdjustHeader> createHeaders(@RequestBody ExpenseAdjustHeaderWebDTO dto) {
        return ResponseEntity.ok( headerService.createHeader(dto) );
    }

    /**
     * 更新费用调整单
     * @param dto
     * @return
     */
    @PutMapping
    public ResponseEntity<ExpenseAdjustHeader> updateHeaders(@RequestBody ExpenseAdjustHeaderWebDTO dto) {

        return ResponseEntity.ok( headerService.updateHeaders(dto));
    }
    @GetMapping("/query/dto")
    public ResponseEntity<List<ExpenseAdjustHeaderWebDTO>> findExpenseAdjustHeaderDTO(@RequestParam(required = false) String documentNumber,
                                                                                       @RequestParam(required = false,value = "expAdjustTypeId") Long expAdjustTypeId,
                                                                                       @RequestParam(required = false,value = "status") String status,
                                                                                       @RequestParam(required = false,value ="applyDateFrom" ) String dateTimeFrom,
                                                                                       @RequestParam(required = false,value = "applyDateTo") String dateTimeTo,
                                                                                       @RequestParam(required = false,value = "amountFrom") BigDecimal amountMin,
                                                                                       @RequestParam(required = false,value = "amountTo") BigDecimal amountMax,
                                                                                       @RequestParam(required = false,value = "applyId") Long employeeId,
                                                                                       @RequestParam(required = false,value = "description") String description,
                                                                                       @RequestParam(required = false,value = "adjustTypeCategory") String adjustTypeCategory,
                                                                                       @RequestParam(required = false,value = "currency") String currencyCode,
                                                                                       @RequestParam(required = false,value = "unitId") Long unitId,
                                                                                       @RequestParam(required = false,value = "companyId") Long companyId,
                                                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateTimeFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTimeTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        List<ExpenseAdjustHeaderWebDTO> result = headerService.listHeaderWebDTOByCondition(
                documentNumber, expAdjustTypeId, status, requisitionDateFrom, requisitionDateTo, amountMin, amountMax,
                employeeId, description, adjustTypeCategory, currencyCode,unitId,companyId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotal());
        headers.add("Link", "/api/expense/adjust/headers/query/dto");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @GetMapping("/query/id")
    public ResponseEntity queryHeaderById(@RequestParam("expAdjustHeaderId") Long expAdjustHeaderId){

        return ResponseEntity.ok(headerService.getHeaderDTOById(expAdjustHeaderId));
    }

    @GetMapping("/query/dimension/dto")
    public ResponseEntity queryDimensionDTOByTypeId(@RequestParam("headerId") Long headerId){
        return ResponseEntity.ok(headerService.queryDimensionDTOByTypeId(headerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteHeaderById(@PathVariable("id") Long id){

        return ResponseEntity.ok(headerService.deleteHeaderById(id));
    }

    /**
     * @api {GET} /api/expense/adjust/headers/query/created 查询已创建调整单的申请人
     */
    @GetMapping("/query/created")
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
    public ResponseEntity listExpenseAdjustApprovals(@RequestParam(value = "finished", required = false) boolean finished,
                                                     @RequestParam(required = false) String documentNumber,
                                                     @RequestParam(required = false) Long expAdjustTypeId,
                                                     @RequestParam(required = false) String adjustTypeCategory,
                                                     @RequestParam(required = false) String fullName,
                                                     @RequestParam(required = false) Long employeeId,
                                                     @RequestParam(required = false) String beginDate,
                                                     @RequestParam(required = false) String endDate,
                                                     @RequestParam(required = false) String currencyCode,
                                                     @RequestParam(required = false) BigDecimal amountMin,
                                                     @RequestParam(required = false) BigDecimal amountMax,
                                                     @RequestParam(required = false) String description,
                                                     Pageable pageable){
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
    public void exportExpenseAdjustHeader(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                          @RequestParam(value = "expAdjustTypeId",required = false) Long expAdjustTypeId,
                                          @RequestParam(value = "status",required = false) String status,
                                          @RequestParam(value = "applyDateFrom",required = false) String dateTimeFrom,
                                          @RequestParam(value = "applyDateTo",required = false) String dateTimeTo,
                                          @RequestParam(value = "amountFrom",required = false) BigDecimal amountMin,
                                          @RequestParam(value = "amountTo",required = false) BigDecimal amountMax,
                                          @RequestParam(value = "applyId",required = false) Long employeeId,
                                          @RequestParam(value = "description",required = false) String description,
                                          @RequestParam(value = "adjustTypeCategory",required = false) String adjustTypeCategory,
                                          @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                          @RequestParam(value = "unitId",required = false) Long unitId,
                                          @RequestParam(value = "companyId",required = false) Long companyId,
                                          @RequestBody ExportConfig exportConfig,
                                           Pageable pageable) throws IOException {
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
