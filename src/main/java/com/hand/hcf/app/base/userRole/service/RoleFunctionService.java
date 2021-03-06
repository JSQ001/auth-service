package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.userRole.domain.ContentList;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.domain.RoleFunction;
import com.hand.hcf.app.base.userRole.dto.ContentFunctionDTO;
import com.hand.hcf.app.base.userRole.dto.FunctionPageDTO;
import com.hand.hcf.app.base.userRole.dto.RoleFunctionDTO;
import com.hand.hcf.app.base.userRole.persistence.RoleFunctionMapper;
import com.hand.hcf.app.base.userRole.persistence.UserRoleMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@Service
@Transactional
public class RoleFunctionService extends BaseService<RoleFunctionMapper,RoleFunction>{
    @Autowired
    private  RoleFunctionMapper roleFunctionMapper;
    @Autowired
    private  ContentFunctionRelationService contentFunctionRelationService;
    @Autowired
    private  FunctionListService functionListService;
    @Autowired
    private  ContentListService contentListService;

    @Autowired
    private  UserRoleMapper userRoleMapper;
    @Autowired
    private  PageListService pageListService;

    /**
     * 不分页查询 角色可以分配的功能和已分配的功能
     * @param id
     * @return
     */
    public RoleFunctionDTO getRoleFunction(Long id){
        RoleFunctionDTO result = new RoleFunctionDTO();
        List<ContentFunctionDTO> contentFunctionDTOS = contentFunctionRelationService.listCanAssignFunction(id);
        Map<Long, ContentFunctionDTO> collect = contentFunctionDTOS
                .stream()
                .filter(e -> e.getContentId() != null)
                .collect(Collectors.toMap(ContentFunctionDTO::getContentId, e -> e, (k1, k2) -> k1));
        collect.forEach((key, value) -> {
            ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                    .contentId(value.getContentId())
                    .contentName(value.getContentName())
                    .parentId(value.getParentId())
                    .build();
            contentFunctionDTOS.add(contentFunctionDTO);
        });
        List<Long> functionIdList = roleFunctionMapper.selectList(
                new EntityWrapper<RoleFunction>()
                        .eq("role_id",id)
        ).stream().map(RoleFunction::getFunctionId).collect(Collectors.toList());

        result.setContentFunctionDTOList(contentFunctionDTOS);
        result.setFunctionIdList(functionIdList);

        return result;
    }

    /**
     * 批量新增、删除 角色分配功能
     * @param list
     */
    public void insertOrDeleteRoleFunction(List<RoleFunction> list){
        list.stream().forEach(roleFunction -> {
            //flag为true，表示新增，false表示删除
            if (roleFunction.getFlag()){
                if (roleFunctionMapper.selectList(
                        new EntityWrapper<RoleFunction>()
                                .eq("role_id",roleFunction.getRoleId())
                                .eq("function_id",roleFunction.getFunctionId())
                ).size() > 0 ){
                    throw new BizException(RespCode.ROLE_FUNCTION_EXIST);
                }
                roleFunctionMapper.insert(roleFunction);
            }else {
                List<Long> roleFunctionIds = roleFunctionMapper.selectList(
                        new EntityWrapper<RoleFunction>()
                                .eq("role_id",roleFunction.getRoleId())
                                .eq("function_id",roleFunction.getFunctionId())
                ).stream().map(RoleFunction::getId).collect(Collectors.toList());
                if (roleFunctionIds.size() < 0 ){
                    throw new BizException(RespCode.ROLE_FUNCTION_NOT_EXIST);
                }
                roleFunctionMapper.deleteBatchIds(roleFunctionIds);
            }
        });
    }

    /**
     * 获取导航栏数据
     * @param userId
     * @return
     */
    public RoleFunctionDTO getNavigationNar(Long userId){
        RoleFunctionDTO result = new RoleFunctionDTO();
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        if (userId != null) {
            //目录关联功能数据
            List<ContentFunctionDTO> contentFunctionDTOList = new ArrayList<>();
            //功能分配的页面数据
            List<FunctionPageDTO> functionPageDTOList;

            //拿到当前用户的所有角色id
            ZonedDateTime now = ZonedDateTime.now();
            List<Long> roleIdList = userRoleMapper.getRoleIdByUserIdAndTime(userId,now);
            if (CollectionUtils.isEmpty(roleIdList)){
                return result;
            }
            // 查角色分配的功能
            List<RoleFunction> roleFunctions = roleFunctionMapper.selectList(new EntityWrapper<RoleFunction>().in("role_id", roleIdList));
            if (CollectionUtils.isEmpty(roleFunctions)){
                return result;
            }
            Set<Long> functionIdList = roleFunctions.stream().map(RoleFunction::getFunctionId).collect(Collectors.toSet());
            functionPageDTOList = pageListService.listPageByRoleIds(roleIdList,tenantId);
            List<ContentFunctionDTO> contentFunctionDTOS = contentFunctionRelationService.listContentFunctions(roleIdList);
            List<ContentList> contentLists = contentListService.selectList(new EntityWrapper<ContentList>().eq("tenant_id", tenantId));
            List<ContentFunctionDTO> allContentDto = contentLists.stream().map(e -> {
                ContentFunctionDTO dto = ContentFunctionDTO.builder()
                        .contentId(e.getId())
                        .contentName(e.getContentName())
                        .contentRouter(e.getContentRouter())
                        .icon(e.getIcon())
                        .parentId(e.getParentId())
                        .contentSequenceNumber(e.getSequenceNumber())
                        .hasSonContent(e.getHasSonContent())
                        .build();
                return dto;
            }).collect(Collectors.toList());
            contentFunctionDTOList.addAll(contentFunctionDTOS);
            contentFunctionDTOList.addAll(allContentDto);

            result.setContentFunctionDTOList(contentFunctionDTOList);
            result.setFunctionIdList(new ArrayList<>(functionIdList));
            result.setFunctionPageDTOList(functionPageDTOList);
        }

        return result;
    }

    /**
     * 初始化租户时默认管理员的角色菜单
     * @param role
     */
    @Transactional(rollbackFor = Exception.class)
    public void initRoleFunctionByTenant(Role role) {
        //获得当前角色对应的租户
        Long tenantId = role.getTenantId();
        // 先查询系统管理员分配的菜单
        List<RoleFunction> roleFunctions = roleFunctionMapper.getRoleFunctionByAdminRoleIdAndTenantId(1L,tenantId, role.getId());
        if (!CollectionUtils.isEmpty(roleFunctions)){
            this.insertBatch(roleFunctions);
        }
    }

    /**
     * 初始化租户时关联更新角色功能id
     * @param tenantId
     */
    @Transactional(rollbackFor = Exception.class)
    public void initRoleFunction(Long tenantId) {
        functionListService.initTenantFunction(tenantId);
        contentListService.initTenantContent(tenantId);
        baseMapper.updateRoleFunction(tenantId);
    }



}
