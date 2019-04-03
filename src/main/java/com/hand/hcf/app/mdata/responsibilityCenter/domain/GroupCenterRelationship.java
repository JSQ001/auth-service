package com.hand.hcf.app.mdata.responsibilityCenter.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

@Data
@TableName("sys_res_group_center_relation")
public class GroupCenterRelationship extends Domain{

    private Long groupId;

    private Long responsibilityCenterId;
}
