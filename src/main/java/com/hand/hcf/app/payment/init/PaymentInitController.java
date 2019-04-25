package com.hand.hcf.app.payment.init;


import com.hand.hcf.app.payment.service.CompanyBankService;
import com.hand.hcf.app.payment.web.dto.CompanyBankImportDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author zhongyan.zhao
 */
@RestController
@RequestMapping("/api/init")
@Api(tags = "初始化数据控制器")
public class PaymentInitController {

    @Autowired
    private CompanyBankService companyBankService;


    /**
     * 公司银行账户导入
     * @param companyBankImportDTOS
     * @return
     */
    @PostMapping(value = "/company/bank/import",produces = "application/json")
    @ApiOperation(value = "通过模板批量导入公司银行账户", notes = "通过模板批量导入公司银行账户 开发：赵忠岩")
    public ResponseEntity importDepartment(@ApiParam(value = "银行Json数据") @RequestBody List<CompanyBankImportDTO> companyBankImportDTOS){
        return ResponseEntity.ok(companyBankService.importCompanyBank(companyBankImportDTOS));
    }
}
