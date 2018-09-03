package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.DomainLogicEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统接口管理
 */
@Data
@TableName("sys_interface")
public class Interface extends DomainLogicEnable {

    @TableField("interface_name")
    private String interfaceName; //接口名称

    @TableField("request_protocol")
    private String requestProtocol; // 请求协议

    @TableField("request_method")
    private String requestMethod; // 请求方法

    @TableField("request_format")
    private String requestFormat; // 请求格式

    @TableField("req_url")
    private String reqUrl; // 请求URL

    @TableField("response_format")
    private String responseFormat; // 响应格式

    @TableField("remark")
    private String remark; // 备注说明

    @TableField("module_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID

}
