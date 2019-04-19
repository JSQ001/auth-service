package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.system.service.OrderNumberService;
import com.hand.hcf.app.common.co.OrderNumberCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class CommonControllerImpl {


    @Autowired
    private SysCodeService sysCodeService;
	
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
        String orderNumber = orderNumberService.getOderNumber(documentTypeCode, companyCode, operationDate, tenantId);
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

    //jiu.zhao 修改三方接口 20190403
    public List<SysCodeValueCO> listEnabledSysCodeValueByCodeOid(UUID codeOid) {
        List<SysCodeValueCO> sysCodeValueCOS = this.listSysValueByCodeOidConditionByEnabled(codeOid.toString(), true);
        return (List)(null == sysCodeValueCOS ? new ArrayList() : sysCodeValueCOS);
    }

    public OrderNumberCO getOrderNumberCO(String documentTypeCode, String companyCode, String operationDate) {
        String language = LoginInformationUtil.getCurrentLanguage();
        return (OrderNumberCO)getOrderNumber(documentTypeCode, companyCode, operationDate).getBody();
    }

    public List<SysCodeValueCO> listAllSysCodeValueByCode(String code) {
        List<SysCodeValueCO> sysCodeValueCOS = listSysValueByCodeConditionByEnabled(code, (Boolean)null);
        return (List)(null == sysCodeValueCOS ? new ArrayList() : sysCodeValueCOS);
    }

    public List<SysCodeValueCO> listEnabledSysCodeValueByCode(String code) {
        List<SysCodeValueCO> sysCodeValueCOS = listSysValueByCodeConditionByEnabled(code, true);
        return (List)(null == sysCodeValueCOS ? new ArrayList() : sysCodeValueCOS);
    }

    public Map<String, String> mapAllSysCodeValueByCode(String code) {
        Map<String, String> map = new HashMap(16);
        List<SysCodeValueCO> sysCodeValueCOS = listSysValueByCodeConditionByEnabled(code, (Boolean)null);
        if (null == sysCodeValueCOS) {
            return map;
        } else {
            map = (Map) sysCodeValueCOS.stream().collect(Collectors.toMap(SysCodeValueCO::getValue, SysCodeValueCO::getName));
            return map;
        }
    }



}

