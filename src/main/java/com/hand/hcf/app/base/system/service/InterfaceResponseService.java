package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.InterfaceResponse;
import com.hand.hcf.app.base.system.persistence.InterfaceResponseMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口响应ervice
 */
@Service
public class InterfaceResponseService extends BaseService<InterfaceResponseMapper, InterfaceResponse> {

    private final InterfaceResponseMapper interfaceResponseMapper;

    public InterfaceResponseService(InterfaceResponseMapper moduleMapper) {
        this.interfaceResponseMapper = moduleMapper;
    }

    /**
     * 创建接口响应
     *
     * @param interfaceResponse
     * @return
     */
    @Transactional
    public InterfaceResponse createInterfaceResponse(InterfaceResponse interfaceResponse) {
        //校验
        if (interfaceResponse == null || interfaceResponse.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (interfaceResponse.getKeyCode() == null || "".equals(interfaceResponse.getKeyCode())) {
            throw new BizException(RespCode.RESPONSE_CODE_NULL);
        }
        if (interfaceResponse.getInterfaceId() == null ) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }

        if(interfaceResponse.getParentId() == null ){
            interfaceResponse.setParentId(0L);
        }
        interfaceResponseMapper.insert(interfaceResponse);
        return interfaceResponse;
    }

    /**
     * 更新接口响应
     *
     * @param interfaceResponse
     * @return
     */
    @Transactional
    public InterfaceResponse updateInterfaceResponse(InterfaceResponse interfaceResponse) {
        //校验
        if (interfaceResponse == null || interfaceResponse.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (interfaceResponse.getKeyCode() == null || "".equals(interfaceResponse.getKeyCode())) {
            throw new BizException(RespCode.RESPONSE_CODE_NULL);
        }
        if (interfaceResponse.getInterfaceId() == null ) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }
        //校验ID是否在数据库中存在
        InterfaceResponse rr = interfaceResponseMapper.selectById(interfaceResponse.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (interfaceResponse.getEnabled() == null ) {
            interfaceResponse.setEnabled(rr.getEnabled());
        }
        if (interfaceResponse.getDeleted() == null ) {
            interfaceResponse.setDeleted(rr.getDeleted());
        }
        if (interfaceResponse.getInterfaceId() == null ) {
            interfaceResponse.setInterfaceId(rr.getInterfaceId());
        }
        if(interfaceResponse.getParentId() == null ){
            interfaceResponse.setParentId(rr.getParentId());
        }

        interfaceResponse.setKeyCode(rr.getKeyCode());
        interfaceResponse.setCreatedBy(rr.getCreatedBy());
        interfaceResponse.setCreatedDate(rr.getCreatedDate());
        this.updateById(interfaceResponse);
        return interfaceResponse;
    }

    /**
     * 批量、保存或更新接口响应
     * @param responseList
     * @return
     */
    @Transactional
    public List<InterfaceResponse> batchSaveOrUpdateInterfaceResponse(List<InterfaceResponse> responseList) {
        //校验新增的
        if(CollectionUtils.isNotEmpty(responseList)){
            responseList.stream().forEach(interfaceResponse ->{
                if(interfaceResponse.getId() == null){
                    //校验新增的
                    if (interfaceResponse.getKeyCode() == null || "".equals(interfaceResponse.getKeyCode())) {
                        throw new BizException(RespCode.RESPONSE_CODE_NULL);
                    }
                    if (interfaceResponse.getInterfaceId() == null ) {
                        throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
                    }

                    if(interfaceResponse.getParentId() == null ){
                        interfaceResponse.setParentId(0L);
                    }
                }else{
                    //校验更新的
                    InterfaceResponse rr = interfaceResponseMapper.selectById(interfaceResponse.getId());
                    if (rr == null) {
                        throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
                    }
                    if (interfaceResponse.getEnabled() == null ) {
                        interfaceResponse.setEnabled(rr.getEnabled());
                    }
                    if (interfaceResponse.getDeleted() == null ) {
                        interfaceResponse.setDeleted(rr.getDeleted());
                    }
                    if (interfaceResponse.getInterfaceId() == null ) {
                        interfaceResponse.setInterfaceId(rr.getInterfaceId());
                    }
                    if(interfaceResponse.getParentId() == null ){
                        interfaceResponse.setParentId(rr.getParentId());
                    }
                    interfaceResponse.setKeyCode(rr.getKeyCode());
                    interfaceResponse.setCreatedBy(rr.getCreatedBy());
                    interfaceResponse.setCreatedDate(rr.getCreatedDate());
                }
            });
        }
        this.insertOrUpdateBatch(responseList,100);
        return responseList;
    }



    /**
     * @param id 删除接口响应（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteInterfaceResponse(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除接口响应（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchInterfaceResponse(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据接口ID，取所有接口的接口响应 分页
     *
     * @param interfaceId
     * @param page
     * @param enabled   如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<InterfaceResponse> getInterfaceResponsesByInterfaceId(Long interfaceId, Boolean enabled, Page page) {
        return interfaceResponseMapper.selectPage(page, new EntityWrapper<InterfaceResponse>()
                .eq(enabled != null, "enabled", enabled)
                .eq("interface_id", interfaceId)
                .orderBy("id"));
    }

    /**
     * 根据parentId，取所有子接口响应 分页
     *
     * @param parentId
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<InterfaceResponse> getInterfaceResponsesByParentId(Long parentId, Boolean enabled, Page page) {
        return interfaceResponseMapper.selectPage(page, new EntityWrapper<InterfaceResponse>()
                .eq(enabled != null, "enabled", enabled)
                .eq("parent_id", parentId)
                .orderBy("id"));
    }


    /**
     * 根据ID，获取对应的接口响应信息
     *
     * @param id
     * @return
     */
    public InterfaceResponse getInterfaceResponseById(Long id) {
        return interfaceResponseMapper.selectById(id);
    }
}
