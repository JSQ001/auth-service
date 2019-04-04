package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.domain.WorkFlowEventLogs;
import com.hand.hcf.app.workflow.persistence.WorkFlowEventLogsMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by dick on 2018/12/14.
 */
@Service
@Transactional
public class WorkFlowEventLogsService extends BaseService<WorkFlowEventLogsMapper, WorkFlowEventLogs> {

    @Autowired
    private WorkFlowEventLogsMapper workflowEventLogsMapper;
    /**
     * @param workflowEventLogs
     * @return
     */
    @Transactional
    public WorkFlowEventLogs createSysWorkflowEventLogs(WorkFlowEventLogs workflowEventLogs) {
        //校验
        if (workflowEventLogs == null || workflowEventLogs.getId() != null) {
            throw new BizException("数据不存在");
        }
        workflowEventLogs.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        ZonedDateTime now = ZonedDateTime.now();
        workflowEventLogs.setCreatedDate(now);
        workflowEventLogs.setLastUpdatedDate(now);
        workflowEventLogs.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        workflowEventLogs.setVersionNumber(1);
        workflowEventLogsMapper.insert(workflowEventLogs);
        return workflowEventLogs;
    }
    /**
     * 更新workFlowDocumentRef
     * @param workflowEventLogs
     * @return
     */
    @Transactional
    public WorkFlowEventLogs updateSysWorkflowEventLogs(WorkFlowEventLogs workflowEventLogs) {
        //校验
        if (workflowEventLogs == null || workflowEventLogs.getId() == null) {
            throw new BizException("ID 不允许为空");
        }
        //校验ID是否在数据库中存在
        WorkFlowEventLogs rr = workflowEventLogsMapper.selectById(workflowEventLogs.getId());
        if (rr == null) {
            throw new BizException("数据不存在");
        }
        workflowEventLogs.setCreatedBy(rr.getCreatedBy());
        workflowEventLogs.setCreatedDate(rr.getCreatedDate());
        ZonedDateTime now = ZonedDateTime.now();
        workflowEventLogs.setLastUpdatedDate(now);
        workflowEventLogs.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        workflowEventLogs.setVersionNumber(workflowEventLogs.getVersionNumber() + 1);
        this.updateById(workflowEventLogs);
        return workflowEventLogs;
    }
    /**
     * @param id 删除
     * @return
     */
    @Transactional
    public void deleteSysWorkflowEventLogs(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }
    /**
     * @param eventId 消息事件ID
     * @return
     */
    @Transactional
    public Integer deleteByEventId(String eventId) {
        return workflowEventLogsMapper.delete(new EntityWrapper<WorkFlowEventLogs>()
                .eq("event_id", eventId));
    }

    /**
     * 根据事件消息ID，取得消息日志信息
     * @return
     */
    public WorkFlowEventLogs getSysWorkflowEventLogsByEventId(String eventId) {
        List<WorkFlowEventLogs> eventLogs = workflowEventLogsMapper.selectList(new EntityWrapper<WorkFlowEventLogs>()
                .eq("event_id", eventId));
        if (eventLogs != null && eventLogs.size() > 0) {
            return eventLogs.get(0);
        }
        return null;
    }
}
