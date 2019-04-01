package com.hand.hcf.app.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hand.hcf.app.common.co.WorkflowMessageCO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/10.
 * 工作流发布事件工具类
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type") //序列化时使用子类的名称作为type
@JsonIgnoreProperties("source") //序列化时，忽略 source
@Data
@NoArgsConstructor
public class WorkflowCustomRemoteEvent extends RemoteApplicationEvent {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");
    //封装工作流消息对象
    private WorkflowMessageCO workflowMessage;
    // 构造方法
    public WorkflowCustomRemoteEvent(Object source,String originService, String destinationService, WorkflowMessageCO workflowMessage) {
        super(source, originService, destinationService);
        this.workflowMessage = workflowMessage;
    }
    @Override
    public String toString() {
        return "WorkflowCustomRemoteEvent{" +"WorkflowMessageCO=" + workflowMessage
                +",eventId:" + super.getId()
                +",originService:"+super.getOriginService()
                +",destinationService:"+ super.getDestinationService()
                +",time:"+ simpleDateFormat.format(new Date(super.getTimestamp()))
                +'}';

    }
}
