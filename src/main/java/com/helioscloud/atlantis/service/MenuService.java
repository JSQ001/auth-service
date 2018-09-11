package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.MenuButton;
import com.helioscloud.atlantis.domain.enumeration.MenuTypeEnum;
import com.helioscloud.atlantis.persistence.MenuMapper;
import com.helioscloud.atlantis.service.es.EsMenuInfoSerivce;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
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
    private final EsMenuInfoSerivce esMenuInfoSerivce;

    private final MenuButtonService menuButtonService;

    public MenuService(MenuMapper menuMapper, EsMenuInfoSerivce esMenuInfoSerivce, MenuButtonService menuButtonService) {
        this.menuMapper = menuMapper;
        this.esMenuInfoSerivce = esMenuInfoSerivce;
        this.menuButtonService = menuButtonService;
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
            if (MenuTypeEnum.FUNCTION.getID() == menu.getMenuTypeEnum()) {
                throw new BizException(RespCode.ROOT_CATALOG_MUST_BE_CATALOG);
            }
        } else {
            //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
            Menu mm = null;
            if (esMenuInfoSerivce.isElasticSearchEnable()) {
                mm = esMenuInfoSerivce.getMenuByIdFromES(menu.getParentMenuId());
            } else {
                mm = menuMapper.selectById(menu.getParentMenuId());
            }
            //如果上级是功能，则不允许再添加子功能
            if (MenuTypeEnum.FUNCTION.getID().intValue() == mm.getMenuTypeEnum()) {
                throw new BizException(RespCode.MENU_FUNCTION_PARENT_MUST_BE_CATALOG);
            }
            // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
            if (MenuTypeEnum.DIRECTORY.getID().intValue() == menu.getMenuTypeEnum() && mm != null) {

                //if (mm.getHasChildCatalog() == null || !mm.getHasChildCatalog()) {
                    mm.setHasChildCatalog(true);
                    this.updateById(mm);
                    // 更新到ES中去
                    esMenuInfoSerivce.saveEsMenuIndex(mm);
                //}
            } else if (MenuTypeEnum.FUNCTION.getID().intValue() == menu.getMenuTypeEnum() && mm != null) {
                // 当前为功能时，校验其上级是否有下级目录，如果有，则不允许功能，功能只能添加到最底级的目录、
                Integer childCatalogCount = 0;
                if (esMenuInfoSerivce.isElasticSearchEnable()) {
                    childCatalogCount = esMenuInfoSerivce.getMenuCountByParentIdAndTypeFromES(mm.getId(), 1001).intValue();
                } else {
                    childCatalogCount = menuMapper.selectCount(new EntityWrapper<Menu>().eq("parent_menu_id", mm.getId()).eq("menu_type", 1001));
                }
                if (childCatalogCount != null && childCatalogCount > 0) {
                    throw new BizException(RespCode.MENU_PARENT_CATALOG_ERROR);
                }
            }
        }
        menuMapper.insert(menu);
        // 更新到ES中去
        esMenuInfoSerivce.saveEsMenuIndex(menu);
        //如果菜单有按钮，则将按钮一起保存
        List<MenuButton> buttonList = menu.getButtonList();
        if(buttonList != null && buttonList.size() > 0){
            buttonList.forEach(b->{
                b.setMenuId(menu.getId());
            });
            menuButtonService.batchSaveAndUpdateMenuButton(menu.getButtonList());
            menu.setButtonList(buttonList);
        }
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
        Menu mm = null;
        if (esMenuInfoSerivce.isElasticSearchEnable()) {
            mm = esMenuInfoSerivce.getMenuByIdFromES(menu.getId());
        } else {
            mm = menuMapper.selectById(menu.getId());
        }
        if (mm == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
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
            if (MenuTypeEnum.FUNCTION.getID() == menu.getMenuTypeEnum() && MenuTypeEnum.DIRECTORY.getID() == mm.getMenuTypeEnum()) {
                updateMenuType = true;
            }
        }
        //hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
        Menu mmm = null;
        if(menu.getParentMenuId().intValue() > 0){
            if (esMenuInfoSerivce.isElasticSearchEnable()) {
                mmm = esMenuInfoSerivce.getMenuByIdFromES(menu.getParentMenuId());
            } else {
                mmm = menuMapper.selectById(menu.getParentMenuId());
            }
        }
        // 如果 当前为目录时，需要更新上级目录的hasChildCatalog字段的值
        if (MenuTypeEnum.DIRECTORY.getID().intValue() == menu.getMenuTypeEnum() && mmm != null) {
            if (mmm.getHasChildCatalog() == null || !mmm.getHasChildCatalog()) {
                mmm.setHasChildCatalog(true);
                this.updateById(mmm);
                esMenuInfoSerivce.saveEsMenuIndex(mmm);
            }
        } else {
            // 由目录 修改为 功能 时，判断是否还有除了当前菜单之后的目录，如果不存在，则需要修改hasChildCatalog的值为false
            if (updateMenuType) {
                List<Menu> childCatalog = menuMapper.selectList(new EntityWrapper<Menu>()
                        .eq("parent_menu_id", menu.getParentMenuId())
                        .eq("menu_type", MenuTypeEnum.DIRECTORY.getID()));
                if (childCatalog != null && childCatalog.size() == 1 && mmm != null) {
                    if (mmm.getHasChildCatalog() == null || !mmm.getHasChildCatalog()) {
                        mmm.setHasChildCatalog(true);
                        this.updateById(mmm);
                        esMenuInfoSerivce.saveEsMenuIndex(mmm);
                    }
                }
            }
        }
        menu.setCreatedBy(mm.getCreatedBy());
        menu.setCreatedDate(mm.getCreatedDate());
        menu.setMenuCode(mm.getMenuCode());
        this.updateById(menu);
        esMenuInfoSerivce.saveEsMenuIndex(menu);
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
        if (esMenuInfoSerivce.isElasticSearchEnable()) {
            list = esMenuInfoSerivce.getMenuListByParentMenuIdFromES(menu.getId());
        } else {
            list = menuMapper.selectList(new EntityWrapper<Menu>()
                    .eq("enabled", true)
                    .eq("parent_menu_id", menu.getId()));
        }
        if (list != null && list.size() > 0) {
            //则去禁用子菜单
            list.stream().forEach(e -> {
                e.setEnabled(false);
            });
            //批量保存
            this.updateBatchById(list);
            esMenuInfoSerivce.batchIndex(list);
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
        if (esMenuInfoSerivce.isElasticSearchEnable()) {
            Long count = esMenuInfoSerivce.getMenuCountByMenuCodeFromES(menuCode);
            return count.intValue();
        } else {
            return menuMapper.selectCount(new EntityWrapper<Menu>()
                    .eq("menu_code", menuCode));
        }
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
            if(esMenuInfoSerivce.isElasticSearchEnable()){
                childCount= esMenuInfoSerivce.getMenuListByParentMenuIdFromES(id).size();
            }else{
                childCount= menuMapper.selectCount(new EntityWrapper<Menu>()
                        .eq("enabled", true)
                        .eq("parent_menu_id", id));
            }
            if (childCount != null && childCount > 0) {
                throw new BizException(RespCode.HAVING_CHILD_MENU);
            }
            this.deleteById(id);
            //删除菜单时，将菜单对应的按钮也删除掉
            menuButtonService.deleteMenuButtonByMenuId(id);
            if(esMenuInfoSerivce.isElasticSearchEnable()){
                esMenuInfoSerivce.deleteEsMenuIndex(id);
            }
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
            if(esMenuInfoSerivce.isElasticSearchEnable()){
                ids.forEach(id -> {
                    try {
                        esMenuInfoSerivce.deleteEsMenuIndex(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * 所有菜单 分页
     * @param pageable
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenus(Boolean enabled, Pageable pageable) {
        if(esMenuInfoSerivce.isElasticSearchEnable()){
            return esMenuInfoSerivce.getMenuPagesFromES(enabled,pageable);
        }else{
            Page page = PageUtil.getPage(pageable);
            return menuMapper.selectPage(page, new EntityWrapper<Menu>()
                    .eq(enabled != null && !"".equals(enabled), "enabled", enabled)
                    .orderBy("seq_number")
                    .orderBy("menu_code"));
        }
    }

    /**
     * 所有子菜单 分页
     * @param pageable
     * @param parentMenuId 父菜单ID
     * @param enabled    如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenusByParentMenuId(Long parentMenuId, Boolean enabled, Pageable pageable) {
        if(esMenuInfoSerivce.isElasticSearchEnable()){
            return esMenuInfoSerivce.getMenuPageByParentMenuIdFromES(parentMenuId,enabled,pageable);
        }else{
            Page page = PageUtil.getPage(pageable);
            return menuMapper.selectPage(page, new EntityWrapper<Menu>()
                    .eq(enabled != null && !"".equals(enabled), "enabled", enabled)
                    .eq("parent_menu_id", parentMenuId));
        }
    }

    /**
     * 根据ID，获取对应的菜单信息
     *
     * @param id
     * @return
     */
    public Menu getMenuById(Long id) {
        if(esMenuInfoSerivce.isElasticSearchEnable()){
            return esMenuInfoSerivce.getMenuByIdFromES(id);
        }
        return menuMapper.selectById(id);
    }
}
