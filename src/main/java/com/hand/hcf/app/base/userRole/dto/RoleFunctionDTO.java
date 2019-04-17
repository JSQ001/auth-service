package com.hand.hcf.app.base.userRole.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.core.serializer.CollectionToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleFunctionDTO {
    //目录关联功能数据
    List<ContentFunctionDTO> contentFunctionDTOList;

    //角色分配的功能id
    @JsonSerialize(using = CollectionToStringSerializer.class)
    List<Long> functionIdList;

    //功能分配的页面数据
    List<FunctionPageDTO> functionPageDTOList;
}
