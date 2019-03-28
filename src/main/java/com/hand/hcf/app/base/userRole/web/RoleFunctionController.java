package com.hand.hcf.app.base.userRole.web;

import com.hand.hcf.app.base.userRole.domain.RoleFunction;
import com.hand.hcf.app.base.userRole.dto.RoleFunctionDTO;
import com.hand.hcf.app.base.userRole.service.RoleFunctionService;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@RestController
@RequestMapping("/api/role/function")
public class RoleFunctionController {
    private final RoleFunctionService roleFunctionService;

    public RoleFunctionController(RoleFunctionService roleFunctionService){
        this.roleFunctionService = roleFunctionService;
    }

    /**
     * 不分页查询 角色可以分配的功能和已分配的功能
     * @param id 角色ID
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleFunctionDTO> getRoleFunction(@PathVariable Long id){
        return ResponseEntity.ok(roleFunctionService.getRoleFunction(id));
    }

    /**
     * 批量新增、删除 角色分配功能
     * @param list
     * @return
     */
    @PostMapping
    public ResponseEntity insertOrDeleteRoleFunction(@RequestBody List<RoleFunction> list){
        roleFunctionService.insertOrDeleteRoleFunction(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取导航栏数据
     * @return
     */
    @GetMapping
    public ResponseEntity<RoleFunctionDTO> getNavigationNar(){
        Long userId = LoginInformationUtil.getCurrentUserId();
        return ResponseEntity.ok(roleFunctionService.getNavigationNar(userId));
    }
}
