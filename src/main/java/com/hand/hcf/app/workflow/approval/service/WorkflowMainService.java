package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowMainService {
    public static final Logger logger = LoggerFactory.getLogger(WorkflowMainService.class);

    /**
     * 运行工作流
     *
     * @param action
     * @return
     */
    public String runWorkflow(WorkflowAction action) {
        Assert.notNull(action, "action null");

        StringBuffer message = new StringBuffer("workflow log\n");
        WorkflowAction current = action;
        WorkflowResult result = null;
        Object entity = null;
        int loopCount = 0;
        int loopMax = 100;

        try {
            while (current != null) {
                // 防止死循环
                loopCount = loopCount + 1;
                if (loopCount > loopMax) {
                    throw new RuntimeException("loop fail");
                }

                message.append(current.getName());

                // 下个动作
                result = current.execute();
                current = result.getNext();
                entity = result.getEntity();
                // END 下个动作

                message.append("[");
                message.append(getId(entity));
                message.append("] -> ");
                message.append(result.getStatus());
                message.append(" -> ");

                if (current != null) {
                    message.append(current.getName());
                } else {
                    message.append("null");
                }

                message.append("\n");
            }
        } finally {
            logger.info(message.toString());
        }

        String status = result.getStatus();
        return status;
    }

    private Object getId(Object entity) {
        Object id = null;

        if (entity instanceof WorkflowTask) {
            id = ((WorkflowTask) entity).getId();
        } else if (entity instanceof WorkflowNode) {
            id = ((WorkflowNode) entity).getId();
        } else if (entity instanceof WorkflowInstance) {
            id = ((WorkflowInstance) entity).getId();
        }

        return id;
    }

}
