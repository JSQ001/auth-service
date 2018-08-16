package com.helioscloud.atlantis.dto;

import com.helioscloud.atlantis.domain.Menu;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/15.
 */
@Data
public class RoleMenuDTO {
    private Long id;

    private Long menuId;

    private Long roleId;

    private Menu menu;

}
