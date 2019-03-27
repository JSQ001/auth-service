package com.hand.hcf.app.workflow.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalFormDepartmentVO;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalFormQO;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalFormUserGroupVO;

import java.util.List;
import java.util.UUID;

public interface ApprovalFormMapper extends BaseMapper<ApprovalForm> {


    List<ApprovalFormDTO> listDTOByQO(ApprovalFormQO approvalFormQO);

    List<ApprovalFormUserGroupVO> listUserGroupByFormOid(UUID formOID);

    List<ApprovalFormDepartmentVO> listDepartmentsByFormOid(UUID formOID);

}
