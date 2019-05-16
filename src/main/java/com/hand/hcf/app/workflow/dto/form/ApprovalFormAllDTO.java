package com.hand.hcf.app.workflow.dto.form;

import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import lombok.*;

/**
 * Created by caixiang on 2018/3/21.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApprovalFormAllDTO {

    //表单相关
    private ApprovalForm approvalFormDTO;


}
