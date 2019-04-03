package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.userRole.domain.*;
import com.hand.hcf.app.base.userRole.dto.FunctionPageDTO;
import com.hand.hcf.app.base.userRole.dto.RoleFunctionDTO;
import com.hand.hcf.app.base.userRole.dto.ContentFunctionDTO;
import com.hand.hcf.app.base.userRole.persistence.RoleFunctionMapper;
import com.hand.hcf.app.base.userRole.persistence.UserRoleMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@Service
@Transactional
@AllArgsConstructor
public class RoleFunctionService extends BaseService<RoleFunctionMapper,RoleFunction>{
    private final RoleFunctionMapper roleFunctionMapper;

    private final ContentFunctionRelationService contentFunctionRelationService;

    private final FunctionPageRelationService functionPageRelationService;

    private final ContentListService contentListService;

    private final FunctionListService functionListService;

    private final UserRoleMapper userRoleMapper;

    private final PageListService pageListService;

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
        /*
        //获取所有的目录数据
        List<ContentList> contentLists = contentListService.selectList(null);
        contentLists.forEach(contentList -> {
            ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                    .contentId(contentList.getId())
                    .contentName(contentList.getContentName())
                    .parentId(contentList.getParentId())
                    .build();
            contentFunctionList.add(contentFunctionDTO);
        });
        //获取目录功能关联数据
        List<ContentFunctionRelation> contentFunctionRelationList = contentFunctionRelationService.selectList(new EntityWrapper<ContentFunctionRelation>());
        contentFunctionRelationList.forEach(contentFunctionRelation -> {
            ContentList contentList = contentListService.selectById(contentFunctionRelation.getContentId());
            FunctionList functionList = functionListService.selectById(contentFunctionRelation.getFunctionId());
            ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                    .contentId(contentList.getId())
                    .contentName(contentList.getContentName())
                    .parentId(contentList.getParentId())
                    .functionId(functionList.getId())
                    .functionName(functionList.getFunctionName())
                    .functionIcon(functionList.getFunctionIcon())
                    .applicationId(functionList.getApplicationId())
                    .build();
            contentFunctionDTOList.add(contentFunctionDTO);
        });

        //获取没被目录分配的功能
        List<FunctionList> functionLists = functionListService.selectList(
                new EntityWrapper<FunctionList>()
                        .eq("deleted",false)
                        .notIn("id",contentFunctionRelationList.stream().map(ContentFunctionRelation::getFunctionId).collect(Collectors.toList()))
        );
        functionLists.stream().forEach(functionList -> {
            ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                    .functionId(functionList.getId())
                    .functionName(functionList.getFunctionName())
                    .functionRouter(functionList.getFunctionRouter())
                    .param(functionList.getParam())
                    .functionSequenceNumber(functionList.getSequenceNumber())
                    .pageId(functionList.getPageId())
                    .functionIcon(functionList.getFunctionIcon())
                    .applicationId(functionList.getApplicationId())
                    .build();
            contentFunctionDTOList.add(contentFunctionDTO);
        });*/

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

        if (userId != null) {
            //目录关联功能数据
            List<ContentFunctionDTO> contentFunctionDTOList = new ArrayList<>();
            //角色分配的功能id
            List<Long> functionIdList;
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
            functionIdList = roleFunctions.stream().map(RoleFunction::getFunctionId).distinct().collect(Collectors.toList());
            functionPageDTOList = pageListService.listPageByFunctionIds(functionIdList);
            List<ContentFunctionDTO> contentFunctionDTOS = contentFunctionRelationService.listContentFunctions(functionIdList);
            List<ContentList> contentLists = contentListService.selectList(null);
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

            /*//获取所有的目录数据
            List<ContentList> contentLists = contentListService.selectList(
                    new EntityWrapper<ContentList>()
                            .eq("deleted",false)
            );
            contentLists.stream().forEach(contentList -> {
                ContentFunctionDTO tempContentFunctionDTO = ContentFunctionDTO.builder().contentId(contentList.getId()).functionId(null).build();
                if (!contentFunctionDTOList.contains(tempContentFunctionDTO)) {
                    ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                            .contentId(contentList.getId())
                            .contentName(contentList.getContentName())
                            .contentRouter(contentList.getContentRouter())
                            .icon(contentList.getIcon())
                            .parentId(contentList.getParentId())
                            .contentSequenceNumber(contentList.getSequenceNumber())
                            .hasSonContent(contentList.getHasSonContent())
                            .build();
                    contentFunctionDTOList.add(contentFunctionDTO);
                }
            });

            //遍历角色id
            if (roleIdList != null){
                for (Long roleId : roleIdList){
                    //拿到每个角色分配的功能id
                    List<Long> functionIds = roleFunctionMapper.selectList(
                            new EntityWrapper<RoleFunction>()
                                    .eq("role_id",roleId)
                    ).stream().map(RoleFunction::getFunctionId).collect(Collectors.toList());
                    for (Long functionId : functionIds){
                        if (!functionIdList.contains(functionId)){
                            //保存角色分配的功能id
                            functionIdList.add(functionId);
                            //保存目录关联功能数据
                            List<ContentFunctionRelation> contentFunctionRelationList = contentFunctionRelationService.selectList(
                                    new EntityWrapper<ContentFunctionRelation>()
                                            .eq("function_id",functionId)
                            );

                            contentFunctionRelationList.stream().forEach(contentFunctionRelation -> {
                                ContentFunctionDTO contentFunctionDTO = ContentFunctionDTO.builder()
                                        .contentId(contentFunctionRelation.getContentId()).functionId(contentFunctionRelation.getFunctionId()).build();
                                if ( !contentFunctionDTOList.contains(contentFunctionDTO)){
                                    ContentList contentList = contentListService.selectById(contentFunctionRelation.getContentId());
                                    FunctionList functionList = functionListService.selectById(contentFunctionRelation.getFunctionId());

                                    ContentFunctionDTO contentFunctionDTO1 = ContentFunctionDTO.builder()
                                            .contentId(contentList.getId())
                                            .contentName(contentList.getContentName())
                                            .contentRouter(contentList.getContentRouter())
                                            .icon(contentList.getIcon())
                                            .parentId(contentList.getParentId())
                                            .contentSequenceNumber(contentList.getSequenceNumber())
                                            .hasSonContent(contentList.getHasSonContent())
                                            .functionId(functionList.getId())
                                            .functionName(functionList.getFunctionName())
                                            .functionRouter(functionList.getFunctionRouter())
                                            .param(functionList.getParam())
                                            .functionSequenceNumber(functionList.getSequenceNumber())
                                            .pageId(functionList.getPageId())
                                            .build();
                                    contentFunctionDTOList.add(contentFunctionDTO1);
                                    //保存功能分配的页面数据
                                    List<Long> pageIds = functionPageRelationService.selectList(
                                            new EntityWrapper<FunctionPageRelation>()
                                                    .eq("function_id",functionId)
                                    ).stream().map(FunctionPageRelation::getPageId).collect(Collectors.toList());
                                    for (Long pageId : pageIds){
                                        FunctionPageDTO functionPageDTO = FunctionPageDTO.builder().functionId(functionId).pageId(pageId).build();
                                        if (!functionPageDTOList.contains(functionPageDTO)){
                                            PageList pageList = pageListService.selectById(pageId);
                                            FunctionPageDTO functionPageDTO1 = FunctionPageDTO.builder()
                                                    .functionId(functionId)
                                                    .pageId(pageList.getId())
                                                    .pageName(pageList.getPageName())
                                                    .filePath(pageList.getFilePath())
                                                    .contentRouter(pageList.getContentRouter())
                                                    .functionRouter(pageList.getFunctionRouter())
                                                    .pageRouter(pageList.getPageRouter())
                                                    .pageUrl(pageList.getPageUrl())
                                                    .build();
                                            functionPageDTOList.add(functionPageDTO1);
                                        }
                                    }
                                }
                            });



                        }
                    }
                }
            }*/
            result.setContentFunctionDTOList(contentFunctionDTOList);
            result.setFunctionIdList(functionIdList);
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
        // 先查询系统管理员分配的菜单
        List<RoleFunction> roleFunctions = this.selectList(new EntityWrapper<RoleFunction>().eq("role_id", 1L));
        if (!CollectionUtils.isEmpty(roleFunctions)){
            roleFunctions.forEach(e -> {
                e.setRoleId(role.getId());
                e.setId(null);
            });
            this.insertBatch(roleFunctions);
        }
    }
}
