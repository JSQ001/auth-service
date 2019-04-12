package com.hand.hcf.app.workflow.brms.persistence;

import com.hand.hcf.app.workflow.brms.dto.FormApprovalModeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 */
@Mapper
public interface ApprovalFormApprovalModeMapper {

    /**
     * 查询表单的审批模式
     * @return
     */
    List<FormApprovalModeDTO> getCustomFormApproverMode();
}
