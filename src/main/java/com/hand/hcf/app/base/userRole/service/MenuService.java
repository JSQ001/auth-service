package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.ComponentButton;
import com.hand.hcf.app.base.system.service.ComponentButtonService;
import com.hand.hcf.app.base.userRole.domain.Menu;
import com.hand.hcf.app.base.userRole.domain.MenuButton;
import com.hand.hcf.app.base.userRole.enums.MenuTypeEnum;
import com.hand.hcf.app.base.userRole.persistence.MenuMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单 Service
 */
@Service
@AllArgsConstructor
public class MenuService extends BaseService<MenuMapper, Menu> {


    private final MenuButtonService menuButtonService;

    private final ComponentButtonService componentButtonService;

    /**
     * 设置menuFullouter
     */
    public void setMenuFullouter(Menu menu) {
        if (menu.getParentMenuId() != 0 && menu.getParentMenuId() != null) {
            Menu parentMenu = getMenuById(menu.getParentMenuId());
            menu.setMenuFullRouter(parentMenu.getMenuFullRouter() + menu.getMenuRouter());
        } else {
            menu.setMenuFullRouter(menu.getMenuRouter());
        }
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
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
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
            if (MenuTypeEnum.FUNCTION.getId().equals(menu.getMenuTypeEnum())) {
                throw new BizException(RespCode.ROOT_CATALOG_MUST_BE_CATALOG);
            }
        } else {
            //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
            Menu mm = null;

            mm = baseMapper.selectById(menu.getParentMenuId());
            //如果上级是功能，则不允许再添加子功能
            if (MenuTypeEnum.FUNCTION.getId().intValue() == mm.getMenuTypeEnum()) {
                throw new BizException(RespCode.MENU_FUNCTION_PARENT_MUST_BE_CATALOG);
            }
            // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
            if (MenuTypeEnum.DIRECTORY.getId().intValue() == menu.getMenuTypeEnum() && mm != null) {
                //if (mm.getHasChildCatalog() == null || !mm.getHasChildCatalog()) {
                mm.setHasChildCatalog(true);
                this.updateById(mm);
                // 更新到ES中去
                //}
            } else if (MenuTypeEnum.FUNCTION.getId().intValue() == menu.getMenuTypeEnum() && mm != null) {
                // 当前为功能时，校验其上级是否有下级目录，如果有，则不允许功能，功能只能添加到最底级的目录、
                Integer childCatalogCount = 0;

                childCatalogCount = baseMapper.selectCount(new EntityWrapper<Menu>().eq("parent_menu_id", mm.getId()).eq("menu_type", 1001));
                if (childCatalogCount != null && childCatalogCount > 0) {
                    throw new BizException(RespCode.MENU_PARENT_CATALOG_ERROR);
                }
            }
        }
        setMenuFullouter(menu);
        baseMapper.insert(menu);
        List<MenuButton> buttonList = new ArrayList<>();
        if (menu.getComponentId() != null) {
            List<ComponentButton> componentButtons = componentButtonService.getComponentButtonsByComponentId(menu.getComponentId());
            if (componentButtons != null && componentButtons.size() > 0) {
                componentButtons.stream().forEach(b -> {
                    MenuButton mb = new MenuButton();
                    mb.setMenuId(menu.getId());
                    mb.setButtonCode(b.getButtonCode());
                    mb.setButtonName(b.getButtonName());
                    mb.setEnabled(true);
                    mb.setDeleted(false);
                    mb.setVersionNumber(1);
                    mb.setFlag("1001");
                    mb.setId(b.getId()); // 保持两个表的ID值一致
                    buttonList.add(mb);
                });
            }
        }
        //如果菜单有按钮，则将按钮一起保存
        if (buttonList != null && buttonList.size() > 0) {
            menuButtonService.batchSaveAndUpdateMenuButton(buttonList);
            menu.setButtonList(buttonList);
        }
        // 更新到ES中去
        return menu;
    }

    /**
     * 更新菜单
     *
     * @param menu
     * @return
     */
    @Transactional
    public Menu updateMenu(Menu menu) {
        boolean updateMenuType = false;
        //校验
        if (menu == null || menu.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (menu.getMenuName() == null || "".equals(menu.getMenuName())) {
            throw new BizException(RespCode.MENU_CODE_NULL);
        }
        //校验ID是否在数据库中存在
        Menu mm = null;

        mm = baseMapper.selectById(menu.getId());
        if (mm == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (menu.getEnabled() == null || "".equals(menu.getEnabled())) {
            menu.setEnabled(mm.getEnabled());
        }
        if (menu.getDeleted() == null || "".equals(menu.getDeleted())) {
            menu.setDeleted(mm.getDeleted());
        }
        if (menu.getParentMenuId() == null || "".equals(menu.getParentMenuId())) {
            menu.setParentMenuId(mm.getParentMenuId());//如果没有上级，则默认为0
        }
        if (menu.getMenuTypeEnum() == null || "".equals(menu.getMenuTypeEnum())) {
            menu.setMenuTypeEnum(mm.getMenuTypeEnum());
        } else {
            //判断是否是 目录 修改为 功能  1000：功能，1001：目录
            if (MenuTypeEnum.FUNCTION.getId().intValue() == menu.getMenuTypeEnum().intValue() && MenuTypeEnum.DIRECTORY.getId().intValue() == mm.getMenuTypeEnum().intValue()) {
                updateMenuType = true;
            }
        }
        //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
        Menu mmm = null;
        if (menu.getParentMenuId().intValue() > 0) {

            mmm = baseMapper.selectById(menu.getParentMenuId());
        }
        // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
        if (MenuTypeEnum.DIRECTORY.getId().intValue() == menu.getMenuTypeEnum() && mmm != null) {
            if (mmm.getHasChildCatalog() == null || !mmm.getHasChildCatalog()) {
                mmm.setHasChildCatalog(true);
                this.updateById(mmm);
            }
        } else {
            menu.setHasChildCatalog(false);
            // 由目录 修改为 功能 时，判断是否还有除了当前菜单之后的目录，如果不存在，则需要修改hasChildCatalog的值为false
            if (updateMenuType) {
                //如果有子目录，则不允许由目录修改为功能
                List<Menu> childCatalog = baseMapper.selectList(new EntityWrapper<Menu>()
                        .eq("parent_menu_id", menu.getId())
                        .eq("menu_type", MenuTypeEnum.DIRECTORY.getId()));
                if (childCatalog != null) {
                    if (childCatalog.size() > 0) {
                        throw new BizException(RespCode.HAS_CHILD_CATALOG_CAN_NOT_BE_FUNCTION);
                    }
                    mmm.setHasChildCatalog(true);
                    this.updateById(mmm);
                }
            }
        }
        menu.setCreatedBy(mm.getCreatedBy());
        menu.setCreatedDate(mm.getCreatedDate());
        menu.setMenuCode(mm.getMenuCode());
        setMenuFullouter(menu);
        this.updateById(menu);

        //如果菜单更新了关联的组件
        if (menu.getComponentId() != null && menu.getComponentId().longValue() > 0) {
            //所有已经保存的按钮
            List<MenuButton> buttonListDb = menuButtonService.getMenuButtonsByMenuId(menu.getId());
            List<ComponentButton> componentButtons = componentButtonService.getComponentButtonsByComponentId(menu.getComponentId());
            List<Long> compButtonList = new ArrayList<>();
            List<MenuButton> saveButtonList = new ArrayList<>();
            if (buttonListDb != null && buttonListDb.size() > 0) {
                if (componentButtons != null && componentButtons.size() > 0) {
                    componentButtons.stream().forEach(b -> {
                        compButtonList.add(b.getId());
                        MenuButton mb = new MenuButton();
                        mb.setButtonCode(b.getButtonCode());
                        mb.setButtonName(b.getButtonName());
                        mb.setMenuId(menu.getId());
                        mb.setEnabled(true);
                        mb.setId(b.getId());
                        mb.setDeleted(false);
                        mb.setVersionNumber(1);
                        mb.setFlag("1001");// 需要插入
                        saveButtonList.add(mb);
                    });
                    buttonListDb.stream().forEach(b -> {
                        if (!compButtonList.contains(b.getId())) {
                            b.setFlag("1002");// 需要删除
                            saveButtonList.add(b);
                        }
                    });
                } else {
                    buttonListDb.stream().forEach(b -> {
                        b.setFlag("1002");// 需要删除
                        saveButtonList.add(b);
                    });
                }
                menuButtonService.batchSaveAndUpdateMenuButton(saveButtonList);
            }
        }
        //20180823 增加校验 禁用上级菜单的时候，把其所有子菜单都禁用掉，但启用的时候，只启用它自己，不递归处理子菜单
        if (!menu.getEnabled().booleanValue() && mm.getEnabled().booleanValue()) {
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
        List<Menu> list = null;
        //先判断是否有 启用的子菜单

            list = baseMapper.selectList(new EntityWrapper<Menu>()
                    .eq("enabled", true)
                    .eq("parent_menu_id", menu.getId()));
        if (list != null && list.size() > 0) {
            //则去禁用子菜单
            list.stream().forEach(e -> {
                e.setEnabled(false);
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
        //是否启用了ES，启用了则从ES里查询

            return baseMapper.selectCount(new EntityWrapper<Menu>()
                    .eq("menu_code", menuCode));
    }

    /**
     * @param id 删除菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteMenu(Long id) throws Exception {
        if (id != null) {
            Integer childCount = 0;
            //如果有启用的子菜单，则不允许删除

                childCount = baseMapper.selectCount(new EntityWrapper<Menu>()
                        .eq("enabled", true)
                        .eq("parent_menu_id", id));
            if (childCount != null && childCount > 0) {
                throw new BizException(RespCode.HAVING_CHILD_MENU);
            }
            this.deleteById(id);
            //删除菜单时，将菜单对应的按钮也删除掉
            menuButtonService.deleteMenuButtonByMenuId(id);
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
            //删除菜单的按钮
            menuButtonService.deleteMenuButtonByMenuIds(ids);

        }
    }

    /**
     * 所有菜单 分页
     *
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenus(Boolean enabled, Page page) {

            return baseMapper.selectPage(page, new EntityWrapper<Menu>()
                    .eq(enabled != null && !"".equals(enabled), "enabled", enabled)
                    .orderBy("seq_number")
                    .orderBy("menu_code"));
    }

    /**
     * 所有子菜单 分页
     *
     * @param page
     * @param parentMenuId 父菜单ID
     * @param enabled      如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenusByParentMenuId(Long parentMenuId, Boolean enabled, Page page) {

            return baseMapper.selectPage(page, new EntityWrapper<Menu>()
                    .eq(enabled != null && !"".equals(enabled), "enabled", enabled)
                    .eq("parent_menu_id", parentMenuId));
    }

    /**
     * 根据ID，获取对应的菜单信息
     *
     * @param id
     * @return
     */
    public Menu getMenuById(Long id) {

        return baseMapper.selectById(id);
    }
}
