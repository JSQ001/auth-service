
package com.hand.hcf.app.mdata.department.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("sys_department_user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentUser extends DomainEnable {
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "department_id")
    private Long departmentId;
}
