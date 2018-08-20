package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Request;
import com.helioscloud.atlantis.persistence.RequestMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 请求ervice
 */
@Service
public class RequestService extends BaseService<RequestMapper, Request> {

    private final RequestMapper requestMapper;

    public RequestService(RequestMapper moduleMapper) {
        this.requestMapper = moduleMapper;
    }

    /**
     * 创建请求
     *
     * @param request
     * @return
     */
    @Transactional
    public Request createRequest(Request request) {
        //校验
        if (request == null || request.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (request.getName() == null || "".equals(request.getName())) {
            throw new BizException(RespCode.REQUEST_NAME_NULL);
        }
        if (request.getInterfaceId() == null || "".equals(request.getInterfaceId())) {
            throw new BizException(RespCode.REQUEST_INTERFACE_NULL);
        }
        if(request.getParentId() == null || "".equals(request.getParentId())){
            request.setParentId(0L);//如果没有上级，则默认为0
        }
        requestMapper.insert(request);
        return request;
    }

    /**
     * 更新请求
     *
     * @param request
     * @return
     */
    @Transactional
    public Request updateRequest(Request request) {
        //校验
        if (request == null || request.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (request.getName() == null || "".equals(request.getName())) {
            throw new BizException(RespCode.REQUEST_NAME_NULL);
        }
        if (request.getInterfaceId() == null || "".equals(request.getInterfaceId())) {
            throw new BizException(RespCode.REQUEST_INTERFACE_NULL);
        }
        //校验ID是否在数据库中存在
        Request rr = requestMapper.selectById(request.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (request.getIsEnabled() == null || "".equals(request.getIsEnabled())) {
            request.setIsEnabled(rr.getIsEnabled());
        }
        if (request.getIsDeleted() == null || "".equals(request.getIsDeleted())) {
            request.setIsDeleted(rr.getIsDeleted());
        }
        if (request.getInterfaceId() == null || "".equals(request.getInterfaceId())) {
            request.setInterfaceId(rr.getInterfaceId());
        }
        request.setCreatedBy(rr.getCreatedBy());
        request.setCreatedDate(rr.getCreatedDate());
        this.updateById(request);
        return request;
    }

    /**
     * @param id 删除请求（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteRequest(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除请求（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchRequest(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据接口ID，取所有接口的请求 分页
     *
     * @param interfaceId
     * @param page
     * @param isEnabled   如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Request> getRequestsByInterfaceId(Long interfaceId, Boolean isEnabled, Page page) {
        return requestMapper.selectPage(page, new EntityWrapper<Request>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("interface_id", interfaceId)
                .orderBy("id"));
    }

    /**
     * 根据parent，取所有子请求 分页
     *
     * @param parentId
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Request> getRequestsByParentId(Long parentId, Boolean isEnabled, Page page) {
        return requestMapper.selectPage(page, new EntityWrapper<Request>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("parent_id", parentId)
                .orderBy("id"));
    }


    /**
     * 根据ID，获取对应的请求信息
     *
     * @param id
     * @return
     */
    public Request getRequestById(Long id) {
        return requestMapper.selectById(id);
    }
}
