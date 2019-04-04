package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * @description: 快捷回复表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/13
 */
@TableName("sys_quick_reply")
@Data
public class QuickReply extends Domain {
    //快速回复Oid
    @TableField("quick_reply_oid")
    private UUID quickReplyOid;

    //用户Oid
    @TableField("user_oid")
    private UUID userOid;

    //回复信息
    @TableField("reply")
    private String reply;

    //状态
    @TableField("status")
    private Integer status;

    //序号
    @TableField("sequence_number")
    private Integer sequenceNumber;
}
