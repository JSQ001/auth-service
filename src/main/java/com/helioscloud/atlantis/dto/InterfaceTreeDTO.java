package com.helioscloud.atlantis.dto;

import com.helioscloud.atlantis.domain.Interface;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/28.
 * 接口查询时，返回树结构
 */
@Data
public class InterfaceTreeDTO {
     private String moduleName;
     private String moduleId;
     private List<Interface> listInterface = new ArrayList<Interface>();

}
