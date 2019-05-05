package com.hand.hcf.app.workflow.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.domain.WorkflowTrace;
import com.hand.hcf.app.workflow.persistence.WorkflowTraceMapper;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @version 1.0
 * @author mh.z
 * @date 2019/05/04
 */
@Service
public class WorkflowTraceService extends BaseService<WorkflowTraceMapper, WorkflowTrace> {

    /**
     * 记录工作流轨迹
     * @version 1.0
     * @author mh.z
     * @date 2019/05/04
     *
     * @param entityType 实体类型
     * @param entityOid 实体oid
     * @param message 信息
     * @param detail 详情
     */
    public void saveTrace(Integer entityType, UUID entityOid, String message, String detail) {
        CheckUtil.notNull(entityType, "entityType null");
        CheckUtil.notNull(entityOid, "entityOid null");

        ZonedDateTime now = ZonedDateTime.now();
        Long userId = OrgInformationUtil.getCurrentUserId();

        WorkflowTrace trace = new WorkflowTrace();
        trace.setEntityType(entityType);
        trace.setEntityOid(entityOid);
        trace.setMessage(message);
        trace.setDetail(detail);
        trace.setCreatedDate(now);
        trace.setLastUpdatedDate(now);
        trace.setCreatedBy(userId);
        trace.setLastUpdatedBy(userId);

        insert(trace);
    }

}
