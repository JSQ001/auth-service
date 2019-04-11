package com.hand.hcf.app.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormQO;

import java.util.List;

public interface ApprovalFormMapper extends BaseMapper<ApprovalForm> {


    List<ApprovalFormDTO> listDTOByQO(ApprovalFormQO approvalFormQO);

}
