package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.payment.domain.CashPaymentMethod;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankPayment;
import com.hand.hcf.app.payment.service.CashPaymentMethodService;
import com.hand.hcf.app.payment.service.CompanyBankPaymentService;
import com.hand.hcf.app.payment.service.CompanyBankService;
import com.hand.hcf.app.payment.utils.MyBatisPageUtil;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.CompanyBankPaymentDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@RestController
@RequestMapping("/api/comapnyBankPayment")
public class CompanyBankPaymentController {
    @Autowired
    private CompanyBankPaymentService companyBankPaymentService;
    @Autowired
    private CompanyBankService companyBankService;
    @Autowired
    private CashPaymentMethodService cashPaymentMethodService;


    /***
     * 新增或修改公司银行的付款方式
     * @param companyBankPayments
     * @return
     */
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> insertOrUpdate(@RequestBody List<CompanyBankPayment> companyBankPayments) {
        return ResponseEntity.ok(companyBankPaymentService.insertOrUpdateCompanyBankPayment(companyBankPayments));
    }


    /***
     * 根据id逻辑删除
     * @param id
     * @return
     */

    @RequestMapping(value = "/deleteById", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@RequestParam Long id) {
        return ResponseEntity.ok(companyBankPaymentService.deleteById(id));
    }


    @RequestMapping(value = "/deleteByIds", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(companyBankPaymentService.deleteByIds(ids));
    }


    /**
     * 根据银行账户id查询对应的付款方式dto
     */


    @RequestMapping(value = "/getByBankAccountId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankPaymentDTO>> selectByBankAccountId(@RequestParam Long id, Pageable pageable) {

        Page page = MyBatisPageUtil.getPage(pageable);
        Page<CompanyBankPaymentDTO> result = companyBankPaymentService.selectByBankId(id, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/comapnyBankPayment/getByBankAccountId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/getByBankAccountIdAndCode", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyBankPaymentDTO>> getByBankAccountIdAndCode(
            @RequestParam Long id,
            @RequestParam String code) {
        List<CompanyBankPaymentDTO> companyBankPaymentDTOS = companyBankPaymentService.selectByBankIdAndCode(id, code);
        return ResponseEntity.ok(companyBankPaymentDTOS);
    }


    /*  根据银行账户id查询此银行账户下面的付款方式
     * */
    @RequestMapping(value = "/get/companyBankPayment/by/bankAccountId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankPaymentDTO>> selectByBankAccountIdNoPage(@RequestParam Long id) {
        List<CompanyBankPaymentDTO> list = new ArrayList<>();
        List<CompanyBankPayment> result = companyBankPaymentService.selectCompanyBankPaymentByBankId(id);
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach(
                    companyBankPayment -> {
                        CompanyBankPaymentDTO dto = new CompanyBankPaymentDTO();
                        CashPaymentMethod cashPaymentMethod = cashPaymentMethodService.selectPaymentMethodById(companyBankPayment.getPaymentMethodId(), "");
                        if (cashPaymentMethod == null) {
                            throw new BizException(RespCode.PAYMENT_METHOD_NOT_FOUNT);
                        }
                        BeanUtils.copyProperties(cashPaymentMethod, dto, "id");
                        dto.setId(companyBankPayment.getId());
                        dto.setPaymentMethodId(cashPaymentMethod.getId());
                        dto.setPaymentMethodCategoryName(cashPaymentMethod.getPaymentMethodCategoryName());
                        list.add(dto);
                    }
            );
        }
        return ResponseEntity.ok(list);
    }


    /*
     * 根据银行账户账号查询此账户下的付款方式
     * */
    @RequestMapping(value = "/get/company/bank/payment/by/bank/account/number", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyBankPaymentDTO>> getByBankAccountNumber(
            @RequestParam String number,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod) {
        List<CompanyBankPaymentDTO> list = new ArrayList<>();
        List<CompanyBank> companyBanks = companyBankService.selectList(
                new EntityWrapper<CompanyBank>()
                        .eq("bank_account_number", number)
                        .eq("deleted", false)
                        .eq("enabled", true)
        );
        if (CollectionUtils.isEmpty(companyBanks)) {
            return ResponseEntity.ok(new ArrayList<CompanyBankPaymentDTO>());
        }
        List<CompanyBankPayment> result = companyBankPaymentService.selectCompanyBankPaymentByBankId(companyBanks.get(0).getId());
        if (!CollectionUtils.isEmpty(result)) {
            for (CompanyBankPayment companyBankPayment : result) {
                CompanyBankPaymentDTO dto = new CompanyBankPaymentDTO();
                CashPaymentMethod cashPaymentMethod = cashPaymentMethodService.selectPaymentMethodById(companyBankPayment.getPaymentMethodId(), paymentMethod);
                if (cashPaymentMethod == null) {
                    continue;
                }
                BeanUtils.copyProperties(cashPaymentMethod, dto, "id");
                dto.setId(companyBankPayment.getId());
                dto.setPaymentMethodId(cashPaymentMethod.getId());
                dto.setPaymentMethodCategoryName(cashPaymentMethod.getPaymentMethodCategoryName());
                if (cashPaymentMethod.getPaymentMethodCategory().equals(paymentMethod)) {
                    list.add(dto);
                }
            }
        }
        return ResponseEntity.ok(list);
    }


}
