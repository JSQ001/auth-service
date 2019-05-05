package com.hand.hcf.app.workflow.web;

import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.service.WorkFlowEventPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/11.
 * Demo测试使用
 */
@RestController
@RequestMapping("/api/workflow/event")
public class WorkFlowEventController {

    @Autowired
    private WorkFlowEventPublishService workflowRabbitMQPublish;

    @RequestMapping(value = "/publish",method = RequestMethod.GET)
    public ResponseEntity publishRabbit(@RequestParam String destinationService, @RequestParam UUID documentOid, @RequestParam Integer documentCategory){
        WorkFlowDocumentRef workFlowDocumentRef = WorkFlowDocumentRef.builder().build();
        workFlowDocumentRef.setDestinationService(destinationService);
        workFlowDocumentRef.setDocumentCategory(documentCategory);
        workFlowDocumentRef.setDocumentOid(documentOid);
        //workflowRabbitMQPublish.publishEvent(workFlowDocumentRef);

        return ResponseEntity.ok("WORKFLOW EVENT PUBLISH SUCCESS");
    }
}
