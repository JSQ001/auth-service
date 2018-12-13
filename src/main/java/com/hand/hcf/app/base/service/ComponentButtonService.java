package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.ComponentButton;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.persistence.ComponentButtonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/20.
 * 组件按钮Service
 */
@Service
public class ComponentButtonService extends BaseService<ComponentButtonMapper, ComponentButton> {

    private final ComponentButtonMapper componentButtonMapper;

    public ComponentButtonService(ComponentButtonMapper componentButtonMapper) {
        this.componentButtonMapper = componentButtonMapper;
    }

    /**
     * 创建组件按钮
     *
     * @param componentButton
     * @return
     */
    @Transactional
    public ComponentButton createComponentButton(ComponentButton componentButton) {
        //校验
        if (componentButton == null || componentButton.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        componentButtonMapper.insert(componentButton);
        return componentButton;
    }

    /**
     * 更新组件按钮
     *
     * @param componentButton
     * @return
     */
    @Transactional
    public ComponentButton updateComponentButton(ComponentButton componentButton) {
        //校验
        if (componentButton == null || componentButton.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //校验ID是否在数据库中存在
        ComponentButton rr = componentButtonMapper.selectById(componentButton.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DB_NOT_EXISTS);
        }
        componentButton.setCreatedBy(rr.getCreatedBy());
        componentButton.setCreatedDate(rr.getCreatedDate());
        this.updateById(componentButton);
        return componentButton;
    }
    /**
     * 批量组件按钮保存或更新
     * @param buttonList
     * @return
     */
    @Transactional
    public List<ComponentButton> batchSaveAndUpdateMenuButton(List<ComponentButton> buttonList) {
        //校验
        if (buttonList == null || buttonList.size() == 0) {
            return null;
        }
        //需要保存和更新
        List<ComponentButton> toSaveList = new ArrayList<>();
        //需要删除
        List<Long> toDeleteListIds = new ArrayList<>();

        //flag;创建:1001，删除:1002
        buttonList.stream().filter(b -> "1001".equals(b.getFlag())).forEach(button -> {
            toSaveList.add(button);
        });
        buttonList.stream().filter(b -> "1002".equals(b.getFlag())).forEach(button -> {
            toDeleteListIds.add(button.getId());
        });
        //批量删除
        this.deleteBatchComponentButton(toDeleteListIds);
        if(toSaveList.size() > 0){
            //处理保存
            toSaveList.forEach(button -> {
                if(button.getId() != null && button.getId() > 0){
                    button = this.updateComponentButton(button);
                }else{
                    button = this.createComponentButton(button);
                }
            });
        }
        return toSaveList;
    }
    /**
     * @param id 删除组件按钮
     * @return
     */
    @Transactional
    public void deleteComponentButton(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除组件按钮
     * @return
     */
    @Transactional
    public void deleteBatchComponentButton(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据组件ID，获取其所有组件按钮不分页
     *
     * @return
     */
    public List<ComponentButton> getComponentButtonsByComponentId(Long componentId) {
        return componentButtonMapper.selectList(new EntityWrapper<ComponentButton>()
                .eq("component_id", componentId)
                .orderBy("id"));
    }

    /**
     * 根据ID，获取对应的组件按钮信息
     *
     * @param id
     * @return
     */
    public ComponentButton getComponentButtonById(Long id) {
        return componentButtonMapper.selectById(id);
    }

}
