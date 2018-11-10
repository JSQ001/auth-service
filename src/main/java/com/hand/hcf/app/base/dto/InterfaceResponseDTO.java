package com.hand.hcf.app.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/22.
 *
 */
public class InterfaceResponseDTO implements Serializable {

    private Long id;
    private String name; //响应名称
    private String respType; // 参数类型
    private String keyCode; // 请求代码
    private Long parentId; // 上级ID
    private String remark; // 备注说明
    private Long interfaceId;  // 接口ID
    private Boolean enabledSearch;//是否启用搜索
    private List<InterfaceResponseDTO> children = new ArrayList<>();
    @JsonIgnore
    private InterfaceResponseDTO parent;

}
