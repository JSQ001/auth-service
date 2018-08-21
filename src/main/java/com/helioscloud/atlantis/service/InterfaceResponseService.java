package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.InterfaceResponse;
import com.helioscloud.atlantis.persistence.InterfaceResponseMapper;
import com.helioscloud.atlantis.util.RespCode;
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
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (interfaceResponse.getName() == null || "".equals(interfaceResponse.getName())) {
            throw new BizException(RespCode.RESPONSE_NAME_NULL);
        }
        if (interfaceResponse.getInterfaceId() == null || "".equals(interfaceResponse.getInterfaceId())) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }
        if(interfaceResponse.getParentId() == null || "".equals(interfaceResponse.getParentId())){
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
            throw new BizException(RespCode.ID_NULL);
        }
        if (interfaceResponse.getName() == null || "".equals(interfaceResponse.getName())) {
            throw new BizException(RespCode.RESPONSE_NAME_NULL);
        }
        if (interfaceResponse.getInterfaceId() == null || "".equals(interfaceResponse.getInterfaceId())) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }
        //校验ID是否在数据库中存在
        InterfaceResponse rr = interfaceResponseMapper.selectById(interfaceResponse.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (interfaceResponse.getIsEnabled() == null || "".equals(interfaceResponse.getIsEnabled())) {
            interfaceResponse.setIsEnabled(rr.getIsEnabled());
        }
        if (interfaceResponse.getIsDeleted() == null || "".equals(interfaceResponse.getIsDeleted())) {
            interfaceResponse.setIsDeleted(rr.getIsDeleted());
        }
        if (interfaceResponse.getInterfaceId() == null || "".equals(interfaceResponse.getInterfaceId())) {
            interfaceResponse.setInterfaceId(rr.getInterfaceId());
        }
        if(interfaceResponse.getParentId() == null || "".equals(interfaceResponse.getParentId())){
            interfaceResponse.setParentId(rr.getParentId());
        }
        interfaceResponse.setCreatedBy(rr.getCreatedBy());
        interfaceResponse.setCreatedDate(rr.getCreatedDate());
        this.updateById(interfaceResponse);
        return interfaceResponse;
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
     * @param isEnabled   如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<InterfaceResponse> getInterfaceResponsesByInterfaceId(Long interfaceId, Boolean isEnabled, Page page) {
        return interfaceResponseMapper.selectPage(page, new EntityWrapper<InterfaceResponse>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("interface_id", interfaceId)
                .orderBy("id"));
    }

    /**
     * 根据parentId，取所有子接口响应 分页
     *
     * @param parentId
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<InterfaceResponse> getInterfaceResponsesByParentId(Long parentId, Boolean isEnabled, Page page) {
        return interfaceResponseMapper.selectPage(page, new EntityWrapper<InterfaceResponse>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
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
