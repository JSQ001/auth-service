package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.client.contact.ContactCO;
import com.hand.hcf.app.prepayment.domain.PrePaymentLog;
import com.hand.hcf.app.prepayment.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.prepayment.persistence.PrepaymentLogMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.prepayment.web.dto.PrePaymentLogDTO;
import com.hand.hcf.core.exception.BizException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘亮 on 2018/1/26.
 */
@Service
@AllArgsConstructor
public class PrepaymentLogService {
    private final PrepaymentLogMapper prepaymentLogMapper;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    public void insertLog( PrePaymentLog prepaymentLog){
        prepaymentLogMapper.insert(prepaymentLog);
        return ;
    }

    public List<PrePaymentLogDTO> getAll(Long id){
        List<PrePaymentLogDTO> logs = new ArrayList<>();
        List<PrePaymentLog> prePaymentLogs = prepaymentLogMapper.selectList(
                new EntityWrapper<PrePaymentLog>()
                        .eq("header_id", id)
        );
        prePaymentLogs.forEach(
                prePaymentLog -> {
                    PrePaymentLogDTO dto = new PrePaymentLogDTO();
                    BeanUtils.copyProperties(prePaymentLog,dto);
                    dto.setOperation(prePaymentLog.getOperationType());
                    dto.setLastUpdatedDate(prePaymentLog.getOperationTime());
                    dto.setOperationType(1000);
                    try{
                        dto.setOperationTypeName(hcfOrganizationInterface.getSysCodeValue("2028", String.valueOf(dto.getOperation()),
                                RespCode.SYS_CODE_TYPE_NOT_EXIT).get(dto.getOperation()));
                    }catch (Exception e){
                        throw new BizException(RespCode.PREPAY_HEAD_STATUS_ERROR);
                    }
                    try{
                        ContactCO userInfoCO = hcfOrganizationInterface.getUserById(dto.getUserId());
                        dto.setEmployeeId(userInfoCO.getEmployeeCode());
                        dto.setEmployeeName(userInfoCO.getFullName());
                    }catch (Exception e){
                        throw new BizException(RespCode.SYS_EMPLOYEE_INFO_NOT_EXISTS);
                    }
                    logs.add(dto);
                }
        );
        return logs;
    }

}
