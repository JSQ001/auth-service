package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.Application;
import com.hand.hcf.app.base.system.domain.Interface;
import com.hand.hcf.app.base.system.dto.InterfaceTreeDTO;
import com.hand.hcf.app.base.system.persistence.InterfaceMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口Service
 */
@Service
@AllArgsConstructor
public class InterfaceService extends BaseService<InterfaceMapper, Interface> {

    private final InterfaceMapper interfaceMapper;

    private final ApplicationService applicationService;


    /**
     * 创建接口
     *
     * @param anInterface
     * @return
     */
    @Transactional
    public Interface createInterface(Interface anInterface) {
        //校验
        if (anInterface == null || anInterface.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        interfaceMapper.insert(anInterface);
        return anInterface;
    }

    /**
     * 更新接口
     *
     * @param anInterface
     * @return
     */
    @Transactional
    public Interface updateInterface(Interface anInterface) {
        //校验
        if (anInterface == null || anInterface.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //校验ID是否在数据库中存在
        Interface rr = interfaceMapper.selectById(anInterface.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (anInterface.getEnabled() == null || "".equals(anInterface.getEnabled())) {
            anInterface.setEnabled(rr.getEnabled());
        }
        if (anInterface.getDeleted() == null || "".equals(anInterface.getDeleted())) {
            anInterface.setDeleted(rr.getDeleted());
        }
        anInterface.setCreatedBy(rr.getCreatedBy());
        anInterface.setCreatedDate(rr.getCreatedDate());
        this.updateById(anInterface);
        return anInterface;
    }

    /**
     * @param id 删除接口（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteInterface(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除接口（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchInterface(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }

    /**
     * 根据模块Id,取所有接口 分页
     *
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Interface> pageInterfacesByAppId(Long appId, Boolean enabled, Page page) {
        return interfaceMapper.selectPage(page, new EntityWrapper<Interface>()
                .eq(enabled != null, "enabled", enabled)
                .eq("app_id", appId)
                .orderBy("id"));
    }

    /**
     * 根据模块Id,取所有接口 不分页
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Interface> listInterfacesByAppId(Long appId, Boolean enabled) {
        return interfaceMapper.selectList(new EntityWrapper<Interface>()
                .eq(enabled != null, "enabled", enabled)
                .eq("app_id", appId)
                .orderBy("id"));
    }

    /**
     * 前端查询接口 模糊查询 按 module_id,req_url排序
     * @param appId 不传则不控，传了则按其控制
     * @param keyword 模糊匹配 interfaceName或reqUrl字段
     * @return
     */
    public List<InterfaceTreeDTO> getInterfacesByKeyword(String appId,String keyword){
        List<InterfaceTreeDTO> treeDTOS = new ArrayList<>();
        //查询出符合条件的数据
        List<Interface> interfaceList = interfaceMapper.getInterfacesByKeyword(appId,keyword);
        Map<Long,List<Interface>> map = new HashMap<>();
        if(interfaceList != null && interfaceList.size() > 0){
            //抽取重复的getAppId，并以getAppId为key,Interface的集合为value，存放map,相同的getAppId放入一个集合
            interfaceList.forEach(e -> {
                if(map.get(e.getAppId()) == null || map.get(e.getAppId()).size() == 0){
                    List<Interface> tempList = new ArrayList<>();
                    tempList.add(e);
                    map.put(e.getAppId(),tempList);
                }else{
                    List<Interface> tempList = map.get(e.getAppId());
                    tempList.add(e);
                    map.put(e.getAppId(),tempList);
                }
            });
        }
        if(map.size() > 0){
            map.forEach((k,v) -> {
                Application module = applicationService.selectById(k);
                InterfaceTreeDTO treeDTO = new InterfaceTreeDTO();
                treeDTO.setAppId(k+"");
                treeDTO.setAppName(module.getAppName());
                treeDTO.getListInterface().addAll(v);
                treeDTOS.add(treeDTO);
            });
        }
        return treeDTOS;
    }
    /**
     * 根据ID，获取对应的接口信息
     *
     * @param id
     * @return
     */
    public Interface getInterfaceById(Long id) {
        return interfaceMapper.getById(id);
    }

    /**
     * 查所有接口，且接模块分组进行返回
     * 接口 模糊查询 查所有未删除的数据，按 module_id,interface排序
     */
    public List<InterfaceTreeDTO> getAllInterfaces(){
        return interfaceMapper.getAllInterfaces();
    }

}
