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

import java.util.Collection;
import java.util.Stack;

/**
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowMainService {
    public static final Logger logger = LoggerFactory.getLogger(WorkflowMainService.class);

    /**
     * 运行工作流
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param start
     */
    public boolean runWorkflow(WorkflowAction start) {
        Assert.notNull(start, "start null");

        StringBuffer buffer = new StringBuffer("workflow trace\n");
        Stack<WorkflowAction> stack = new Stack<WorkflowAction>();
        int loopCount = 0;
        int loopMax = 100;

        Object current = start;
        WorkflowAction action = null;
        WorkflowResult result = null;

        // 通过这个循环实现动作链，循环里在调用动作的执行方法后会获取到下一个动作（null则没有下一个动作），
        // 比如有这样一条动作链WorkflowSubmitInstance -> WorkflowNextNodeAction -> null，
        // 循环里会调用WorkflowSubmitInstance.execute执行动作并获取到下一个动作WorkflowNextNodeAction，
        // 接着调用WorkflowNextNodeAction.execute执行动作并获取到下一个动作null（循环结束）
        try {
            while (current != null) {
                // 防止死循环
                loopCount = loopCount + 1;
                if (loopCount > loopMax) {
                    throw new RuntimeException("loop fail");
                }

                if (current instanceof Collection) {
                    Collection<WorkflowAction> actionList = (Collection<WorkflowAction>) current;
                    pushStack(stack, actionList);
                    buffer.append(String.format("@queue +%s -> %s\n", actionList.size(), stack.size()));
                    action = stack.pop();
                    buffer.append(String.format("@queue -1 -> %s\n", stack.size()));
                } else {
                    action = (WorkflowAction) current;
                }

                result = action.execute();
                traceMessage(buffer, action, result);
                current = result.getNext();

                if (current == null && stack.size() > 0) {
                    current = stack.pop();
                    buffer.append(String.format("@queue -1 -> %s\n", stack.size()));
                }
            }
        } finally {
            logger.info(buffer.toString());
        }

        return true;
    }

    /**
     * @param stack
     * @param actions
     */
    protected void pushStack(Stack<WorkflowAction> stack, Collection<WorkflowAction> actions) {
        WorkflowAction[] array = actions.toArray(new WorkflowAction[0]);

        for (int i = array.length - 1; i >= 0; i--) {
            stack.push(array[i]);
        }
    }

    /**
     * @param buffer
     * @param action
     * @param result
     */
    protected void traceMessage(StringBuffer buffer, WorkflowAction action, WorkflowResult result) {
        buffer.append("action:");
        buffer.append(action.getName());
        buffer.append('[');

        Object entity = result.getEntity();
        if (entity instanceof WorkflowTask) {
            buffer.append(((WorkflowTask) entity).getId());
        } else if (entity instanceof WorkflowNode) {
            buffer.append(((WorkflowNode) entity).getId());
        } else if (entity instanceof WorkflowInstance) {
            buffer.append(((WorkflowInstance) entity).getId());
        }

        buffer.append("] -> status:");
        buffer.append(result.getStatus());
        buffer.append(" -> next:");

        Object next = result.getNext();
        if (next == null) {
            buffer.append("null");
        } else if (next instanceof WorkflowAction) {
            buffer.append(((WorkflowAction) next).getName());
        } else {
            buffer.append("action batch");
        }

        buffer.append('\n');
    }

}
