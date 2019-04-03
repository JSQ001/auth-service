package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统响应
 */
@Data
@TableName("sys_interface_response")
public class InterfaceResponse extends DomainLogicEnable {

    @TableField("name")
    private String name; //响应名称

    @TableField("resp_type")
    private String respType; // 响应协议

    @TableField("key_code")
    private String keyCode; // 响应代码


    @TableField("parent_id")
    private Long parentId; // 上级ID

    @TableField("remark")
    private String remark; // 备注说明

    @TableField("interface_id")

    private Long interfaceId;  // 接口ID


}
