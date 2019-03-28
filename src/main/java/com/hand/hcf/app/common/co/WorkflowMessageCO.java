package com.hand.hcf.app.common.co;

import com.hand.hcf.core.security.domain.PrincipalLite;
import lombok.Data;

import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/10.
 * 封装工作流事件更新单据时的消息信息
 */
@Data
public class WorkflowMessageCO {
    private UUID entityOid;//单据OID
    private String entityType; // 单据类型 801003:表示预付款单,801004: 表示合同单据...
    private Integer status; // 需要修改的单据状态  如：1001
    private Long userId;//当前用户ID
    private String remark; //  备注说明
    private PrincipalLite userBean;//必传参数  取 LoginInformationUtil.getUser()
    private Long documentId;  // 单据ID
    private String approvalText;// 审批意见
    @Override
    public String toString() {
        return "WorkflowMessageCO{" +
                "entityOid='" + entityOid + '\'' +
                ", entityType='" + entityType + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", remark='" + remark + '\'' +
                ", userBean='" + userBean + '\'' +
                ", approvalText='" + approvalText + '\'' +
                '}';
    }
}
