package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Response;
import com.helioscloud.atlantis.persistence.ResponseMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 响应ervice
 */
@Service
public class ResponseService extends BaseService<ResponseMapper, Response> {

    private final ResponseMapper responseMapper;

    public ResponseService(ResponseMapper moduleMapper) {
        this.responseMapper = moduleMapper;
    }

    /**
     * 创建响应
     *
     * @param response
     * @return
     */
    @Transactional
    public Response createResponse(Response response) {
        //校验
        if (response == null || response.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (response.getName() == null || "".equals(response.getName())) {
            throw new BizException(RespCode.RESPONSE_NAME_NULL);
        }
        if (response.getInterfaceId() == null || "".equals(response.getInterfaceId())) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }
        if(response.getParentId() == null || "".equals(response.getParentId())){
            response.setParentId(0L);
        }
        responseMapper.insert(response);
        return response;
    }

    /**
     * 更新响应
     *
     * @param response
     * @return
     */
    @Transactional
    public Response updateResponse(Response response) {
        //校验
        if (response == null || response.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (response.getName() == null || "".equals(response.getName())) {
            throw new BizException(RespCode.RESPONSE_NAME_NULL);
        }
        if (response.getInterfaceId() == null || "".equals(response.getInterfaceId())) {
            throw new BizException(RespCode.RESPONSE_INTERFACE_NULL);
        }
        //校验ID是否在数据库中存在
        Response rr = responseMapper.selectById(response.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (response.getIsEnabled() == null || "".equals(response.getIsEnabled())) {
            response.setIsEnabled(rr.getIsEnabled());
        }
        if (response.getIsDeleted() == null || "".equals(response.getIsDeleted())) {
            response.setIsDeleted(rr.getIsDeleted());
        }
        if (response.getInterfaceId() == null || "".equals(response.getInterfaceId())) {
            response.setInterfaceId(rr.getInterfaceId());
        }
        if(response.getParentId() == null || "".equals(response.getParentId())){
            response.setParentId(rr.getParentId());
        }
        response.setCreatedBy(rr.getCreatedBy());
        response.setCreatedDate(rr.getCreatedDate());
        this.updateById(response);
        return response;
    }

    /**
     * @param id 删除响应（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteResponse(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除响应（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchResponse(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据接口ID，取所有接口的响应 分页
     *
     * @param interfaceId
     * @param page
     * @param isEnabled   如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Response> getResponsesByInterfaceId(Long interfaceId, Boolean isEnabled, Page page) {
        return responseMapper.selectPage(page, new EntityWrapper<Response>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("interface_id", interfaceId)
                .orderBy("id"));
    }

    /**
     * 根据parentId，取所有子响应 分页
     *
     * @param parentId
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Response> getResponsesByParentId(Long parentId, Boolean isEnabled, Page page) {
        return responseMapper.selectPage(page, new EntityWrapper<Response>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("parent_id", parentId)
                .orderBy("id"));
    }


    /**
     * 根据ID，获取对应的响应信息
     *
     * @param id
     * @return
     */
    public Response getResponseById(Long id) {
        return responseMapper.selectById(id);
    }
}
