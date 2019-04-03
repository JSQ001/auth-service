package com.hand.hcf.app.workflow.web;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.domain.QuickReply;
import com.hand.hcf.app.workflow.service.QuickReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/13
 */
@RestController
@RequestMapping("/api/quick/reply")
public class QuickReplyController {
    @Autowired
    private QuickReplyService quickReplyService;

    @RequestMapping(method = RequestMethod.POST)
    public QuickReply createOrUpdateQuickReply(@RequestBody QuickReply quickReply) {
        return quickReplyService.createOrUpdateQuickReply(quickReply, OrgInformationUtil.getCurrentUserOid());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<QuickReply> findByUserOid() {
        return quickReplyService.findByUserOid(OrgInformationUtil.getCurrentUserOid());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteByQuickReply(@RequestParam("quickReplyOids") List<UUID> quickReplyOids) {
        quickReplyService.deleteByQuickReplyOids(quickReplyOids);
    }
}
