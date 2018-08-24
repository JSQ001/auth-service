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
     * hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
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
        if (menu.getParentMenuId() == null || "".equals(menu.getParentMenuId()) || 0 == menu.getParentMenuId()) {
            menu.setParentMenuId(0L);//如果没有上级，则默认为0
            if (1000 == menu.getMenuTypeEnum()) {
                throw new BizException(RespCode.ROOT_CATALOG_MUST_BE_CATALOG);
            }
        } else {
            //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
            Menu mm = menuMapper.selectById(menu.getParentMenuId());
            //如果上级是功能，则不允许再添加子功能
            if (1000 == mm.getMenuTypeEnum()) {
                throw new BizException(RespCode.MENU_FUNCTION_PARENT_MUST_BE_CATALOG);
            }
            // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
            if (1001 == menu.getMenuTypeEnum()) {
                if (mm.getHasChildCatalog() == null || !mm.getHasChildCatalog()) {
                    mm.setHasChildCatalog(true);
                    this.updateById(mm);
                }
            } else if (1000 == menu.getMenuTypeEnum()) {
                // 当前为功能时，校验其上级是否有下级目录，如果有，则不允许功能，功能只能添加到最底级的目录、
                Integer childCatalogCount = menuMapper.selectCount(new EntityWrapper<Menu>().eq("parent_menu_id", mm.getId()).eq("menu_type", 1001));
                if (childCatalogCount != null && childCatalogCount > 0) {
                    throw new BizException(RespCode.MENU_PARENT_CATALOG_ERROR);
                }
            }
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
        boolean updateMenuType = false;
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
        if (menu.getIsEnabled() == null || "".equals(menu.getIsEnabled())) {
            menu.setIsEnabled(mm.getIsEnabled());
        }
        if (menu.getIsDeleted() == null || "".equals(menu.getIsDeleted())) {
            menu.setIsDeleted(mm.getIsDeleted());
        }
        if (menu.getParentMenuId() == null || "".equals(menu.getParentMenuId())) {
            menu.setParentMenuId(mm.getParentMenuId());//如果没有上级，则默认为0
        }
        if (menu.getMenuTypeEnum() == null || "".equals(menu.getMenuTypeEnum())) {
            menu.setMenuTypeEnum(mm.getMenuTypeEnum());
        } else {
            //判断是否是 目录 修改为 功能  1000：功能，1001：目录
            if (1000 == menu.getMenuTypeEnum() && 1001 == mm.getMenuTypeEnum()) {
                updateMenuType = true;
            }
        }
        //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
        Menu mmm = menuMapper.selectById(menu.getParentMenuId());
        // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
        if (1001 == menu.getMenuTypeEnum()) {
            if (mmm.getHasChildCatalog() == null || !mmm.getHasChildCatalog()) {
                mmm.setHasChildCatalog(true);
                this.updateById(mmm);
            }
        } else {
            // 由目录 修改为 功能 时，判断是否还有除了当前菜单之后的目录，如果不存在，则需要修改hasChildCatalog的值为false
            if (updateMenuType) {
                List<Menu> childCatalog = menuMapper.selectList(new EntityWrapper<Menu>()
                        .eq("parent_menu_id", menu.getParentMenuId())
                        .eq("menu_type", 1001));
                if (childCatalog != null && childCatalog.size() == 1) {
                    if (mmm.getHasChildCatalog() == null || !mmm.getHasChildCatalog()) {
                        mmm.setHasChildCatalog(true);
                        this.updateById(mmm);
                    }
                }
            }
        }
        menu.setCreatedBy(mm.getCreatedBy());
        menu.setCreatedDate(mm.getCreatedDate());
        menu.setMenuCode(mm.getMenuCode());
        this.updateById(menu);
        //20180823 增加校验 禁用上级菜单的时候，把其所有子菜单都禁用掉，但启用的时候，只启用它自己，不递归处理子菜单
        if (!menu.getIsEnabled().booleanValue() && mm.getIsEnabled().booleanValue()) {
            updateChildrenMenu(menu);
        }
        return menu;
    }

    /**
     * 禁用父菜单时，递归禁用其所有子菜单
     * 递归 处理 菜单的子菜单
     *
     * @param menu
     */
    private void updateChildrenMenu(Menu menu) {
        //先判断是否有 启用的子菜单
        List<Menu> list = menuMapper.selectList(new EntityWrapper<Menu>()
                .eq("is_enabled", true)
                .eq("parent_menu_id", menu.getId()));
        if (list != null && list.size() > 0) {
            //则去禁用子菜单
            list.stream().forEach(e -> {
                e.setIsEnabled(false);
            });
            //批量保存
            this.updateBatchById(list);
            //递归再查子菜单
            list.stream().forEach(e -> {
                updateChildrenMenu(e);
            });
        }
        return;
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
            //如果有启用的子菜单，则不允许删除
            Integer childCount = menuMapper.selectCount(new EntityWrapper<Menu>()
                    .eq("is_enabled", true)
                    .eq("parent_menu_id", id));
            if (childCount != null && childCount > 0) {
                throw new BizException(RespCode.HAVING_CHILD_MENU);
            }
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchMenu(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
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
                .eq(isEnabled != null && !"".equals(isEnabled), "is_enabled", isEnabled)
                .orderBy("seq_number")
                .orderBy("menu_code"));
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
