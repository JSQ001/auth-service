package com.hand.hcf.app.workflow.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.ApprovalFormPropertyConstants;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.workflow.domain.CountersignDetail;
import com.hand.hcf.app.workflow.workflow.enums.CounterSignOperationTypeEnum;
import com.hand.hcf.app.workflow.workflow.persistence.CountersignDetailMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by 魏建 on 2017/7/3.
 */
@Service
@Transactional
public class CountersignDetailService extends BaseService<CountersignDetailMapper, CountersignDetail> {

    @Autowired
    private ApprovalFormPropertyService approvalFormPropertyService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private BrmsService brmsService;

    //最长加签人数量
    private static final Integer maxApproversNum = 26;

    public CountersignDetail getTop1ByEntityTypeAndEntityOidAndCreatedByOrderByLastUpdatedDateDesc(Integer entityType, UUID entityOid, Long applicantId) {
        return selectOne(new EntityWrapper<CountersignDetail>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("created_by", applicantId)
                .orderBy("last_updated_date", false));
    }

    public CountersignDetail getTop1ByEntityTypeAndEntityOidAndCreatedByAndOperationType(Integer entityType, UUID entityOid, Long applicantId, Integer operationType) {
        return selectOne(new EntityWrapper<CountersignDetail>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("created_by", applicantId)
                .eq("operation_type", operationType)
        );
    }

    /**
     * 根据实体类型和Oid查询会签详情
     *
     * @param entityType
     * @param entityOid
     * @param applicantId
     * @return
     */
    public List<UUID> getTop1ByEntityTypeAndEntityOidAndCreatedBy(Integer entityType, UUID entityOid, Long applicantId) {
        CountersignDetail countersignDetail = getTop1ByEntityTypeAndEntityOidAndCreatedByOrderByLastUpdatedDateDesc(entityType, entityOid, applicantId);
        if (countersignDetail != null && countersignDetail.getCountersignApprovalOids() != null) {
            List<UUID> countersignApproverOids = new ArrayList<>();
            Arrays.asList(countersignDetail.getCountersignApprovalOids().split(", ")).stream().forEach(approverOid ->
                    countersignApproverOids.add(UUID.fromString(approverOid))
            );
            return countersignApproverOids;
        }
        return null;
    }

    /**
     * 多参考一个operationType维度
     * 同时失效此记录
     *
     * @param entityType
     * @param entityOid
     * @param applicantId
     * @param operationType
     * @return
     */
    public List<UUID> listAndInvalidateByEntityTypeAndEntityOidAndOperationType(Integer entityType, UUID entityOid, Long applicantId, Integer operationType) {
        CountersignDetail countersignDetail = getTop1ByEntityTypeAndEntityOidAndCreatedByAndOperationType(entityType, entityOid, applicantId, operationType);
        if (countersignDetail != null && countersignDetail.getCountersignApprovalOids() != null) {
            countersignDetail.setOperationType(CounterSignOperationTypeEnum.COUNTER_SIGN_OPERATION_TYPE_USED_BY_APPROVE.getValue());
            insertOrUpdate(countersignDetail);
            List<UUID> countersignApproverOids = new ArrayList<>();
            Arrays.asList(countersignDetail.getCountersignApprovalOids().split(", ")).stream().forEach(approverOid ->
                    countersignApproverOids.add(UUID.fromString(approverOid))
            );
            return countersignApproverOids;
        }
        return null;
    }

    /**
     * 保存会签信息
     *
     * @param entityType
     * @param entityOid
     * @param applicantId
     * @param countersignApproverOids
     * @param operationType           加签操作类型
     */
    public void saveCountersignDetailByEntityTypeAndEntityOidAndApplicantOidAndCountersignApproverOids(Integer entityType, UUID entityOid, Long applicantId, List<UUID> countersignApproverOids, Integer operationType, UUID formOid) {
        //查询实体对应的表单
        String countersignType = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER);
        CountersignDetail countersignDetail = getTop1ByEntityTypeAndEntityOidAndCreatedByOrderByLastUpdatedDateDesc(entityType, entityOid, applicantId);
        if (countersignDetail == null) {
            countersignDetail = new CountersignDetail();
        }
        countersignDetail.setEntityOid(entityOid);
        countersignDetail.setEntityType(entityType);
        countersignDetail.setCountersignType(StringUtils.isNotBlank(countersignType) ? Integer.valueOf(countersignType) : RuleConstants.RULE_SEQUENCE);
        // 目前代理提交时会存值:1,其他情况暂时为null
        if (operationType != null && operationType.equals(CounterSignOperationTypeEnum.COUNTER_SIGN_OPERATION_TYPE_CREATED_BY_SUBMIT.getValue())) {
            countersignDetail.setOperationType(operationType);
        }
        if (CollectionUtils.isNotEmpty(countersignApproverOids)) {
            if (countersignApproverOids.size() > maxApproversNum) {
                throw new BizException(RespCode.APPROVER_MUST_LT_26, "已选审批人超过26个，无法提交");
            }
            StringBuffer approverOidString = new StringBuffer();
            countersignApproverOids.stream().forEach(approverOid -> {
                if (StringUtils.isEmpty(approverOidString.toString())) {
                    approverOidString.append(approverOid);
                } else {
                    approverOidString.append(", " + approverOid);
                }
            });
            countersignDetail.setCountersignApprovalOids(approverOidString.toString());
        } else {
            countersignDetail.setCountersignApprovalOids(null);
        }
        save(countersignDetail);
    }

    public void save(CountersignDetail countersignDetail) {
        insertOrUpdate(countersignDetail);
    }


    /**
     * 判断是否开启加签
     *
     * @param companyOid
     * @param formOid
     * @param counterSignType
     * @return
     */
    public boolean addSignIsEnable(UUID companyOid, UUID formOid, String counterSignType) {
        String value = "";
        if (StringUtils.isEmpty(counterSignType) || counterSignType.equals(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN)) {
            value = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.ENABEL_ADD_SIGN);
            if (StringUtils.isEmpty(value)) {
                return false;
            }
        } else if (counterSignType.equals(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN_FOR_SUBMITTER)) {
            value = approvalFormPropertyService.getPropertyValueByFormOidAndPropertyName(formOid, ApprovalFormPropertyConstants.ENABEL_ADD_SIGN_FOR_SUBMITTER);
            if (StringUtils.isEmpty(value)) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(value) && Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return false;
        }
        return brmsService.isEnableCustomeApprovalMode(companyOid, formOid);
    }

}
