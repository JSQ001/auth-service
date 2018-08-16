package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.persistence.MenuMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单 Service
 */
@Service
public class MenuService extends BaseService<MenuMapper, Menu> {

    private final MenuMapper menuMapper;

    public MenuService(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */
    @Transactional
    public Menu createMenu(Menu menu) {
        //校验
        if (menu == null || menu.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (menu.getMenuCode() == null || "".equals(menu.getMenuCode())) {
            throw new BizException(RespCode.MENU_CODE_NULL);
        }
        if (menu.getMenuName() == null || "".equals(menu.getMenuName())) {
            throw new BizException(RespCode.MENU_NAME_NULL);
        }
        //检查菜单代码是否唯一
        Integer count = getMenuCountByMenuCode(menu.getMenuCode());
        if (count != null && count > 0) {
            throw new BizException(RespCode.CODE_NOT_UNION);
        }
        menuMapper.insert(menu);
        return menu;
    }

    /**
     * 更新角色
     *
     * @param menu
     * @return
     */
    @Transactional
    public Menu updateMenu(Menu menu) {
        //校验
        if (menu == null || menu.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (menu.getMenuName() == null || "".equals(menu.getMenuName())) {
            throw new BizException(RespCode.MENU_CODE_NULL);
        }
        //校验ID是否在数据库中存在
        Menu mm = menuMapper.selectById(menu.getId());
        if (mm == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (menu.getIsEnabled() == null) {
            menu.setIsEnabled(mm.getIsEnabled());
        }
        if (menu.getIsDeleted() == null) {
            menu.setIsDeleted(mm.getIsDeleted());
        }
        menu.setCreatedBy(mm.getCreatedBy());
        menu.setCreatedDate(mm.getCreatedDate());
        menu.setMenuCode(mm.getMenuCode());
        this.updateById(menu);
        return menu;
    }

    /**
     * 菜单代码，检查是否存在相同的菜单代码
     *
     * @param menuCode
     * @return
     */
    public Integer getMenuCountByMenuCode(String menuCode) {
        return menuMapper.selectCount(new EntityWrapper<Menu>()
                .eq("menu_code", menuCode));
    }

    /**
     * @param id 删除菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteMenu(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
        /*Menu menu = menuMapper.selectById(id);
        menu.setIsDeleted(true);
        menuMapper.updateById(menu);*/
    }

    /**
     * @param ids 批量删除菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchMenu(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
            /*List<Menu> result = null;
            List<Menu> list = menuMapper.selectBatchIds(ids);
            if (list != null && list.size() > 0) {
                result = list.stream().map(menu -> {
                    menu.setIsDeleted(true);
                    return menu;
                }).collect(Collectors.toList());
            }
            if(result != null){
                this.updateBatchById(result);
            }*/
        }
    }

    /**
     * 所有菜单 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenus(Boolean isEnabled, Page page) {
        return menuMapper.selectPage(page, new EntityWrapper<Menu>()
                .eq(isEnabled != null && !"".equals(isEnabled), "is_enabled", isEnabled));
       /* if(isDeleted == null || "".equals(isDeleted)){
            return menuMapper.selectPage(page,new EntityWrapper<Menu>()
                    .eq("is_deleted",false)
                    .eq(isEnabled != null && !"".equals(isEnabled),"is_enabled",isEnabled));
        }else{
            return menuMapper.selectPage(page,new EntityWrapper<Menu>()
                    .eq("is_deleted",isDeleted)
                    .eq(isEnabled != null && !"".equals(isEnabled),"is_enabled",isEnabled));
        }*/
    }

    /**
     * 所有子菜单 分页
     *
     * @param page
     * @param parentMenuId 父菜单ID
     * @param isEnabled    如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenusByParentMenuId(Long parentMenuId, Boolean isEnabled, Page page) {
        return menuMapper.selectPage(page, new EntityWrapper<Menu>()
                .eq(isEnabled != null && !"".equals(isEnabled), "is_enabled", isEnabled)
                .eq("parent_menu_id", parentMenuId));
        /*if(isDeleted == null || "".equals(isDeleted)){
            return menuMapper.selectPage(page,new EntityWrapper<Menu>()
                    .eq("is_deleted",false)
                    .eq(isEnabled != null && !"".equals(isEnabled),"is_enabled",isEnabled)
                    .eq("parent_menu_id",parentMenuId));
        }else{
            return menuMapper.selectPage(page,new EntityWrapper<Menu>()
                    .eq("is_deleted",isDeleted)
                    .eq(isEnabled != null && !"".equals(isEnabled),"is_enabled",isEnabled)
                    .eq("parent_menu_id",parentMenuId));
        }*/
    }

    /**
     * 根据ID，获取对应的菜单信息
     *
     * @param id
     * @return
     */
    public Menu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }
}
