package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.system.service.OrderNumberService;
import com.hand.hcf.app.base.org.OrderNumberCO;
import com.hand.hcf.app.base.org.SysCodeValueCO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
public class CommonControllerImpl {


    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private MessageSource exceptionSource;

    @Autowired
    private OrderNumberService orderNumberService;


    /**
     * 根据编码规则生成编号
     *
     * @param documentTypeCode
     * @param companyCode
     * @param operationDate
     * @return
     */
    public ResponseEntity<OrderNumberCO> getOrderNumber(@RequestParam("documentTypeCode") String documentTypeCode,
                                                        @RequestParam("companyCode") String companyCode,
                                                        @RequestParam(value = "operationDate", required = false) String operationDate) {
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        OrderNumberCO orderNumberDTO = new OrderNumberCO();
        orderNumberDTO.setCode("0000");
        String orderNumber = "";
        try {
            orderNumber = orderNumberService.getOderNumber(documentTypeCode, companyCode, operationDate, tenantId);
        } catch (BizException b) {
            Locale zh_cn = new Locale("zh_cn");
            Locale en_us = Locale.ENGLISH;
            orderNumberDTO.setCode(b.getCode());
            OrderNumberCO.Message message_zh = new OrderNumberCO.Message();
            OrderNumberCO.Message message_en = new OrderNumberCO.Message();
            message_zh.setLanguage("zh_cn");
            message_en.setLanguage("en_us");
            message_zh.setContent(exceptionSource.getMessage(b.getCode(), null, zh_cn));
            message_en.setContent(exceptionSource.getMessage(b.getCode(), null, en_us));
            List<OrderNumberCO.Message> list = new ArrayList();
            list.add(message_en);
            list.add(message_zh);
            orderNumberDTO.setMessage(list);
        }
        orderNumberDTO.setOrderNumber(orderNumber);
        return ResponseEntity.ok(orderNumberDTO);
    }




    /**
     * 根据系统代码获取其下所有的值
     * @param code
     * @return
     */
    public List<SysCodeValueCO> listSysValueByCodeConditionByEnabled(@RequestParam("code") String code,
                                                              @RequestParam(value = "enabled",required = false) Boolean enabled){
        return sysCodeService.listSysValueByCodeConditionByEnabled(code,enabled);
    }

    /**
     * 根据系统代码和值获取系统代码的具体值
     * @param code
     * @param value
     * @return
     */
    public SysCodeValueCO getSysCodeValueByCodeAndValue(@RequestParam("code") String code,
                                                 @RequestParam("value") String value){
        SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue(code, value);
        if (null == sysCodeValue){
            return null;
        }
        SysCodeValueCO sysCodeValueCO = new SysCodeValueCO();
        sysCodeValueCO.setCodeId(sysCodeValue.getCodeId());
        sysCodeValueCO.setEnabled(sysCodeValue.getEnabled());
        sysCodeValueCO.setId(sysCodeValue.getId());
        sysCodeValueCO.setName(sysCodeValue.getName());
        sysCodeValueCO.setValue(sysCodeValue.getValue());
        return sysCodeValueCO;
    }

    /**
     * 根据系统代码Oid获取其下所有的值
     * @param codeOid
     * @return
     */
    public List<SysCodeValueCO> listSysValueByCodeOidConditionByEnabled(@RequestParam("codeOid") String codeOid,
                                                                 @RequestParam(value = "enabled",required = false) Boolean enabled){
        return sysCodeService.listSysValueByCodeOidConditionByEnabled(codeOid,enabled);
    }

    /**
     * 根据系统代码Oid和值获取系统代码的具体值
     * @param codeOid
     * @param value
     * @return
     */
    public SysCodeValueCO getSysCodeValueByCodeOidAndValue(@RequestParam("codeOid") String codeOid,
                                                    @RequestParam("value") String value){
        SysCodeValue sysCodeValue = sysCodeService.getSysCodeValueByCodeOidAndValue(codeOid, value);
        if (null == sysCodeValue){
            return null;
        }
        SysCodeValueCO sysCodeValueCO = new SysCodeValueCO();
        sysCodeValueCO.setCodeId(sysCodeValue.getCodeId());
        sysCodeValueCO.setEnabled(sysCodeValue.getEnabled());
        sysCodeValueCO.setId(sysCodeValue.getId());
        sysCodeValueCO.setName(sysCodeValue.getName());
        sysCodeValueCO.setValue(sysCodeValue.getValue());
        return sysCodeValueCO;
    }


}

