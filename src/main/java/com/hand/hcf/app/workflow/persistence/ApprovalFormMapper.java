package com.hand.hcf.app.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormDepartmentVO;
import com.hand.hcf.app.workflow.dto.ApprovalFormQO;
import com.hand.hcf.app.workflow.dto.ApprovalFormUserGroupVO;

import java.util.List;
import java.util.UUID;

public interface ApprovalFormMapper extends BaseMapper<ApprovalForm> {


    List<ApprovalFormDTO> listDTOByQO(ApprovalFormQO approvalFormQO);

    List<ApprovalFormUserGroupVO> listUserGroupByFormOid(UUID formOID);

    List<ApprovalFormDepartmentVO> listDepartmentsByFormOid(UUID formOID);

}
