package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.DetailLog;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.DetailLogMapper;
import com.hand.hcf.app.payment.web.dto.DetailLogDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 刘亮 on 2018/4/4.
 */
@Service
public class DetailLogService extends BaseService<DetailLogMapper, DetailLog> {


    private final DetailLogMapper detailLogMapper;
    private final PaymentOrganizationService organizationService;

    public DetailLogService(DetailLogMapper detailLogMapper, PaymentOrganizationService organizationService) {
        this.detailLogMapper = detailLogMapper;
        this.organizationService = organizationService;
    }



    public void insertLog(Long detailId,Long userId,Integer operationType,String remark){

        DetailLog log = DetailLog.builder()
                .detailId(detailId)
                .operationMessage(remark)
                .operationTime(ZonedDateTime.now())
                .operationType(operationType)
                .userId(userId)
                .build();
        detailLogMapper.insert(log);
    }


    public List<DetailLogDTO>  getLogsByDetailId(Long detailId){
        List<DetailLog> logs = detailLogMapper.selectList(
                new EntityWrapper<DetailLog>()
                        .eq("detail_id", detailId)
                        .orderBy("id",false)
        );

        List<DetailLogDTO> detailLogDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(logs)){
            List<Long> userIds = logs.stream().map(DetailLog::getUserId).collect(Collectors.toList());
            List<ContactCO> userInfoCOs = organizationService.listByUserIds(userIds);
            Map<Long, ContactCO> userInfoDTOMap = userInfoCOs
                    .stream()
                    .collect(Collectors.toMap(ContactCO::getId, e -> e, (k1, k2) -> k1));
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for(DetailLog log:logs){
                String operationRemark = "";
                switch (log.getOperationType()){
                    case 1001: operationRemark="新建";
                        break;
                    case 1002: operationRemark="提交";
                        break;
                    case 1003: operationRemark="撤回";
                        break;
                    case 1004: operationRemark="审批通过";
                        break;
                    case 1005: operationRemark="驳回";
                        break;
                }

                detailLogDTOS.add(DetailLogDTO.builder()
                        .employeeID(userInfoDTOMap.get(log.getUserId()) == null ? null : userInfoDTOMap.get(log.getUserId()).getEmployeeCode())
                        .employeeName(userInfoDTOMap.get(log.getUserId()) == null ? null : userInfoDTOMap.get(log.getUserId()).getFullName())
                        .operationDetail(log.getOperationMessage())
                        .operation(log.getOperationType())
                        .operationType(1000)
                        .operationRemark(operationRemark)
                        .lastUpdatedDate(log.getOperationTime().format(df)).build());
            }
        }

        return detailLogDTOS;
    }


}
