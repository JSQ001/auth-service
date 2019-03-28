package com.hand.hcf.app.base.system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/22.
 *
 */
public class InterfaceRequestDTO implements Serializable {
    private Long id;
    private String name; //请求名称
    private String reqType; // 参数类型
    private String position; // 位置
    private String keyCode; // 请求代码
    private Long parentId; // 上级ID
    private String remark; // 备注说明
    private Long interfaceId;  // 接口ID
    private List<InterfaceRequestDTO> children = new ArrayList<>();
    @JsonIgnore
    private InterfaceRequestDTO parent;

}
