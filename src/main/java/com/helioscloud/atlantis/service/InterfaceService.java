package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Interface;
import com.helioscloud.atlantis.domain.Module;
import com.helioscloud.atlantis.dto.InterfaceTreeDTO;
import com.helioscloud.atlantis.persistence.InterfaceMapper;
import com.helioscloud.atlantis.persistence.ModuleMapper;
import com.helioscloud.atlantis.util.RespCode;
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
public class InterfaceService extends BaseService<InterfaceMapper, Interface> {

    private final InterfaceMapper interfaceMapper;

    private final ModuleMapper moduleMapper;

    public InterfaceService(InterfaceMapper moduleMapper, ModuleMapper moduleMapper1) {
        this.interfaceMapper = moduleMapper;
        this.moduleMapper = moduleMapper1;
    }

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
            throw new BizException(RespCode.ID_NOT_NULL);
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
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        Interface rr = interfaceMapper.selectById(anInterface.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (anInterface.getIsEnabled() == null || "".equals(anInterface.getIsEnabled())) {
            anInterface.setIsEnabled(rr.getIsEnabled());
        }
        if (anInterface.getIsDeleted() == null || "".equals(anInterface.getIsDeleted())) {
            anInterface.setIsDeleted(rr.getIsDeleted());
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
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Interface> getInterfacesByModuleId(Long moduleId, Boolean isEnabled, Page page) {
        return interfaceMapper.selectPage(page, new EntityWrapper<Interface>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("module_id", moduleId)
                .orderBy("id"));
    }

    /**
     * 根据模块Id,取所有接口 不分页
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Interface> getInterfacesByModuleId(Long moduleId, Boolean isEnabled) {
        return interfaceMapper.selectList(new EntityWrapper<Interface>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("module_id", moduleId)
                .orderBy("id"));
    }

    /**
     * 前端查询接口 模糊查询 按 module_id,req_url排序
     * @param moduleId 不传则不控，传了则按其控制
     * @param keyword 模糊匹配 interfaceName或reqUrl字段
     * @return
     */
    public List<InterfaceTreeDTO> getInterfacesByKeyword(String moduleId,String keyword){
        List<InterfaceTreeDTO> treeDTOS = new ArrayList<>();
        //查询出符合条件的数据
        List<Interface> interfaceList = interfaceMapper.getInterfacesByKeyword(moduleId,keyword);
        Map<Long,List<Interface>> map = new HashMap<>();
        if(interfaceList != null && interfaceList.size() > 0){
            //抽取重复的moduleId，并以moduleId为key,Interface的集合为value，存放map,相同的moduleId放入一个集合
            interfaceList.forEach(e -> {
                if(map.get(e.getModuleId()) == null || map.get(e.getModuleId()).size() == 0){
                    List<Interface> tempList = new ArrayList<>();
                    tempList.add(e);
                    map.put(e.getModuleId(),tempList);
                }else{
                    List<Interface> tempList = map.get(e.getModuleId());
                    tempList.add(e);
                    map.put(e.getModuleId(),tempList);
                }
            });
        }
        if(map.size() > 0){
            map.forEach((k,v) -> {
                Module module = moduleMapper.selectById(k);
                InterfaceTreeDTO treeDTO = new InterfaceTreeDTO();
                treeDTO.setModuleId(k+"");
                treeDTO.setModuleName(module.getModuleName());
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
        return interfaceMapper.selectById(id);
    }
}
