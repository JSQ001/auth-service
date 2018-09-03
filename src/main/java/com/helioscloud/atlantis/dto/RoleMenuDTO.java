package com.helioscloud.atlantis.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.helioscloud.atlantis.domain.Menu;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/15.
 */
@Data
public class RoleMenuDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    private Menu menu;
}
