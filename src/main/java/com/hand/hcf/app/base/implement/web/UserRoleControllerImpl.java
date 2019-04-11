package com.hand.hcf.app.base.implement.web;


import com.hand.hcf.app.base.userRole.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 18:11
 * @remark 第三方接口
 */
@AllArgsConstructor
@RestController
public class UserRoleControllerImpl {

    private final UserRoleService userRoleService;

    public List<Long> listDataAuthByFunctionId(@RequestParam("functionId") Long functionId) {
        return userRoleService.listDataAuthIdByFunctionId(functionId);
    }

    //@GetMapping(value = "/user/has/role")
    public Boolean userHasRole(@RequestParam("userId") Long userId) {
        return userRoleService.userHasRole(userId);
    }

    // @GetMapping("/api/implement/user/role/get/dataAuth/has/isUsed")
    public Boolean dataAuthHasUsed(Long id) {
        return userRoleService.dataAuthHasUsed(id);
    }
}
