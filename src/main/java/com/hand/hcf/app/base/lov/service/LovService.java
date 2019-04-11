package com.hand.hcf.app.base.lov.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.lov.domain.Lov;
import com.hand.hcf.app.base.lov.persistence.LovMapper;
import com.hand.hcf.app.base.lov.web.dto.ColumnDTO;
import com.hand.hcf.app.base.lov.web.dto.LovColumnInfoDTO;
import com.hand.hcf.app.base.lov.web.dto.LovInfoDTO;
import com.hand.hcf.app.base.lov.web.dto.SearchColumnDTO;
import com.hand.hcf.app.base.system.domain.Interface;
import com.hand.hcf.app.base.system.domain.InterfaceRequest;
import com.hand.hcf.app.base.system.domain.InterfaceResponse;
import com.hand.hcf.app.base.system.service.InterfaceRequestService;
import com.hand.hcf.app.base.system.service.InterfaceResponseService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by weishan on 2019/3/5.
 * lov Service
 */
@Service
public class LovService extends BaseService<LovMapper, Lov> {
    @Autowired
    private InterfaceRequestService requestService;
    @Autowired
    private InterfaceResponseService responseService;
    /*@Autowired
    private FeignDynamicClient feignDynamicClient;*/

    /**
     * 创建LOV
     *
     * @param lov
     * @return
     */
    @Transactional
    public Lov createLov(Lov lov) {
        //校验
        if (lov == null || lov.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        validateEntity(lov);

        insert(lov);
        return lov;
    }

    /**
     * 更新LOV
     *
     * @param lov
     * @return
     */
    @Transactional
    public Lov updateLov(Lov lov) {
        //校验
        if (lov == null || lov.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }

        validateEntity(lov);

        updateById(lov);
        return lov;
    }


    public void validateEntity(Lov lov) {

        if (StringUtils.isEmpty(lov.getLovCode())) {
            throw new BizException(RespCode.LOV_CODE_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Lov>()
                .eq("lov_code", lov.getLovCode())
                .ne(lov.getId() != null, "id", lov.getId())) > 0) {
            throw new BizException(RespCode.LOV_CODE_EXISTS);
        }


        if (StringUtils.isEmpty(lov.getLovName())) {
            throw new BizException(RespCode.LOV_NAME_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Lov>()
                .eq("lov_name", lov.getLovName())
                .ne(lov.getId() != null, "id", lov.getId())) > 0) {
            throw new BizException(RespCode.LOV_NAME_EXISTS);
        }
    }

    /**
     * @param id 删除（逻辑删除）
     * @return
     */
    @Transactional
    public void delete(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }


    public List<Lov> pageAll(Page page, String lovCode, String lovName,Long appId, String remarks) {
        return baseMapper.pageAll(lovCode,lovName,appId,page, remarks);
    }

    public List<Lov> listAll(String lovCode, String lovName) {
        return selectList(new EntityWrapper<Lov>()
                .like(StringUtils.isEmpty(lovCode), "lov_code", lovCode)
                .like(StringUtils.isEmpty(lovName), "lov_name", lovName)
                .orderBy("lov_code"));
    }

    public Lov getById(Long id) {
        Lov lov = baseMapper.getById(id);
        if (null != lov){
            if (!StringUtils.isEmpty(lov.getRequestColumn())){
                String requestColumn = lov.getRequestColumn();
                String[] requestColumnIds = requestColumn.split(",");
                List<InterfaceRequest> interfaceRequests = requestService.selectBatchIds(Arrays.asList(requestColumnIds));
                List<LovColumnInfoDTO> collect = interfaceRequests.stream().map(e -> {
                    LovColumnInfoDTO lovColumnInfoDTO = new LovColumnInfoDTO();
                    lovColumnInfoDTO.setId(e.getId());
                    lovColumnInfoDTO.setName(e.getName());
                    return lovColumnInfoDTO;
                }).collect(Collectors.toList());
                lov.setRequestColumnInfo(collect);
            }else{
                lov.setRequestColumnInfo(new ArrayList<>());
            }
            if (!StringUtils.isEmpty(lov.getResponseColumn())){
                String responseColumn = lov.getResponseColumn();
                String[] responseColumnIds = responseColumn.split(",");
                List<InterfaceResponse> interfaceResponses = responseService.selectBatchIds(Arrays.asList(responseColumnIds));
                List<LovColumnInfoDTO> collect = interfaceResponses.stream().map(e -> {
                    LovColumnInfoDTO lovColumnInfoDTO = new LovColumnInfoDTO();
                    lovColumnInfoDTO.setId(e.getId());
                    lovColumnInfoDTO.setName(e.getName());
                    return lovColumnInfoDTO;
                }).collect(Collectors.toList());
                lov.setResponseColumnInfo(collect);
            }else{
                lov.setResponseColumnInfo(new ArrayList<>());
            }
        }
        return lov;
    }


    public LovInfoDTO getByCode(String code) {
        LovInfoDTO lov = baseMapper.getDetailInfoByCode(code);
        if (null != lov){
            if (!StringUtils.isEmpty(lov.getRequestColumn())){
                String requestColumn = lov.getRequestColumn();
                String[] requestColumnIds = requestColumn.split(",");
                List<InterfaceRequest> interfaceRequests = requestService.selectBatchIds(Arrays.asList(requestColumnIds));
                List<SearchColumnDTO> collect = interfaceRequests.stream().map(e -> {
                    SearchColumnDTO searchColumnDTO = new SearchColumnDTO();
                    searchColumnDTO.setId(e.getKeyCode());
                    searchColumnDTO.setLabel(e.getName());
                    searchColumnDTO.setType("input");
                    return searchColumnDTO;
                }).collect(Collectors.toList());
                lov.setSearchForm(collect);
            }else{
                lov.setSearchForm(new ArrayList<>());
            }
            if (!StringUtils.isEmpty(lov.getResponseColumn())){
                String responseColumn = lov.getResponseColumn();
                String[] responseColumnIds = responseColumn.split(",");
                List<InterfaceResponse> interfaceResponses = responseService.selectBatchIds(Arrays.asList(responseColumnIds));
                List<ColumnDTO> collect = interfaceResponses.stream().map(e -> {
                    ColumnDTO columnDTO = new ColumnDTO();
                    columnDTO.setDataIndex(e.getKeyCode());
                    columnDTO.setTitle(e.getName());
                    return columnDTO;
                }).collect(Collectors.toList());
                lov.setColumns(collect);
            }else{
                lov.setColumns(new ArrayList<>());
            }
        }else {
            throw new BizException(RespCode.SYS_LOV_NOT_EXISTS);
        }
        return lov;
    }

    //jiu.zhao TODO
    /*public Object getObjectByLovCode(String code, String id) {
        List<Interface> apiInfos = baseMapper.getLovApiInfoByCode(code);
        if (CollectionUtils.isEmpty(apiInfos)) {
            return null;
        }
        Interface api = apiInfos.get(0);
        String url = api.getReqUrl() + "?id=" + id;

        FeignDynamicInterface target = feignDynamicClient.target(api.getAppCode(), url);
        Object forObject;
        if (HttpMethod.GET.toString().equalsIgnoreCase(api.getRequestMethod())){
            forObject = target.getForObject();
        } else {
            forObject = target.postForObject(null);
        }
        if (forObject == null){
            return null;
        }
        if (forObject instanceof List){
            if (CollectionUtils.isEmpty(apiInfos)) {
                return null;
            } else {
                return ((List) forObject).get(0);
            }
        } else {
            return forObject;
        }
    }*/
}
