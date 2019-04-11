package com.hand.hcf.app.mdata.responsibilityCenter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ResponsibilityCenterGroupCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.GroupCenterRelationship;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenterGroup;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.ResponsibilityCenterGroupMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsibilityCenterGroupService extends BaseService<ResponsibilityCenterGroupMapper,ResponsibilityCenterGroup>{
    @Autowired
    private ResponsibilityCenterGroupMapper resCenterGroupMapper;

    @Autowired
    private GroupCenterRelationshipService groupCenterRelationshipService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ResponsibilityCenterService resCenterService;
    /**
     * 新增或修改责任中心组
     * @param responsibilityCenterGroup
     * @return
     */
    @Transactional
    public ResponsibilityCenterGroup insertOrUpdateResponsibilityCenterGroup(ResponsibilityCenterGroup responsibilityCenterGroup) {
        Long resCenterGroupId = responsibilityCenterGroup.getId();
        if(resCenterGroupId == null) {
            //插入
            if(this.selectCount(
                    new EntityWrapper<ResponsibilityCenterGroup>()
                    .eq("set_of_books_id", responsibilityCenterGroup.getSetOfBooksId())
                    .eq("group_code", responsibilityCenterGroup.getGroupCode()))
                            != 0) {
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_GROUP_CODE_REPEAT);
            }
            resCenterGroupMapper.insert(responsibilityCenterGroup);
        }else {
            //更新
            int count = resCenterGroupMapper.selectCount(
                    new EntityWrapper<ResponsibilityCenterGroup>()
                            .eq("id",resCenterGroupId));
            if(count == 0){
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_GROUP_NOT_EXIST);
            }
            resCenterGroupMapper.updateById(responsibilityCenterGroup);
        }
        return responsibilityCenterGroup;
    }

    /**
     * 添加责任中心关联关系
     * @param centerGroupId 责任中心组id
     * @param ids 责任中心ids
     * @return
     */
    @Transactional
    public Boolean insertResponsibilityCenterGroupByRelationship(Long centerGroupId, List<Long> ids) {
        //删除旧关联
        if(resCenterGroupMapper.selectCount(
                new EntityWrapper<ResponsibilityCenterGroup>()
                        .eq("id", centerGroupId))
                == 0) {
            throw new BizException(RespCode.RESPONSIBILITY_CENTER_GROUP_NOT_EXIST);
        }
        groupCenterRelationshipService.delete(
                new EntityWrapper<GroupCenterRelationship>()
                        .eq("group_id",centerGroupId));
        if(CollectionUtils.isNotEmpty(ids)){
            ids.stream().forEach(id ->{
                groupCenterRelationshipService.insertGroupCenterRelationship(centerGroupId,id);
            });
        }
        return true;
    }

    /**
     * 根据账套获取责任中心组
     * @param setOfBooksId 账套Id
     * @param groupCode 责任中心组code
     * @param groupName 责任中心组名称
     * @param enabled 启用禁用
     * @param page 分页
     * @return
     */
    public Page<ResponsibilityCenterGroup> pageResponsibilityCenterGroupBySetOfBooksId(Long setOfBooksId,
                                                                                       String groupCode,
                                                                                       String groupName,
                                                                                       Boolean enabled,
                                                                                       Page page) {
        Wrapper<ResponsibilityCenterGroup> wrapper = new EntityWrapper<ResponsibilityCenterGroup>()
                .eq("set_of_books_id", setOfBooksId)
                .like(groupCode != null, "group_code", groupCode)
                .like(groupName != null, "group_name", groupName)
                .eq(enabled != null,"enabled", enabled)
                .orderBy("enabled",false)
                .orderBy("group_code");
        List<ResponsibilityCenterGroup> centerGroupList = resCenterGroupMapper.selectPage(page,wrapper);
        //获取当前责任组下责任中心
        centerGroupList.stream().forEach(e->{
            List<Long> ids = groupCenterRelationshipService.selectList(
                    new EntityWrapper<GroupCenterRelationship>()
                            .eq("group_id",e.getId()))
                    .stream().map(GroupCenterRelationship::getResponsibilityCenterId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                e.setRelation(true);
            } else {
                e.setRelation(false);
            }
            e.setResponsibilityCenterIdList(ids);
        });
        page.setRecords(centerGroupList);
        return page;
    }

    /**
     * 删除责任中心组
     * @param id 删除Id
     * @return
     */
    @Transactional
    public Boolean delecteResponsibilityCenterGroupById(Long id) {
        ResponsibilityCenterGroup centerGroup = resCenterGroupMapper.selectById(id);
        if(centerGroup == null){
            throw new BizException(RespCode.RESPONSIBILITY_CENTER_GROUP_NOT_EXIST);
        }
        resCenterGroupMapper.deleteById(id);
        //删除责任中心组关联责任中心代码
        groupCenterRelationshipService.delete(
                new EntityWrapper<GroupCenterRelationship>()
                        .eq("group_id",id));
        return true;
    }
    /**
     * 根据责任中心获取责任中心组 （预算模块）
     * @param responsibilityCenterId 责任中心
     * @return
     */
    public List<ResponsibilityCenterGroup> listResponsibilityCenterGroupByResCenterId(Long responsibilityCenterId) {
       List<GroupCenterRelationship> groupCenterRelationships = groupCenterRelationshipService.selectList(
               new EntityWrapper<GroupCenterRelationship>()
                       .eq("responsibility_center_id",responsibilityCenterId));
        if(CollectionUtils.isEmpty(groupCenterRelationships)){
            return new ArrayList<>();
        }
        List<Long> groupId  = groupCenterRelationships
                .stream()
                .map(GroupCenterRelationship::getGroupId)
                .collect(Collectors.toList());
        return baseMapper.selectList(
                new EntityWrapper<ResponsibilityCenterGroup>()
                        .in("id",groupId));
    }

    /**
     * 根据责任中心组Id获取其责任中心
     * @param groupId 责任中心组Id
     * @return
     */
    public List<ResponsibilityCenter> listResponsibilityCenterByGroupId(Long groupId) {
        ResponsibilityCenterGroup responsibilityCenterGroup = this.selectById(groupId);
        if(responsibilityCenterGroup == null){
            String Message = messageService.getMessageDetailByCode("RESPONSIBILITY_CENTER_GROUP_NOT_EXIST");
            throw new BizException(Message);
        }
        return resCenterService.selectList(
                new EntityWrapper<ResponsibilityCenter>()
                        .in("id",groupCenterRelationshipService.listGroupCenterRelByGroupId(groupId))
                        .eq("enabled",true));
    }

    /**
     * 根据当前账套Id获取账套下所有责任中心组
     * @return
     */
    public List<ResponsibilityCenterGroup> listResCenterGroupBySetOfBooksId() {
        return baseMapper.selectList(
                new EntityWrapper<ResponsibilityCenterGroup>()
                        .eq("set_Of_books_id", OrgInformationUtil.getCurrentSetOfBookId()));
    }

    /**
     * ResponsibilityCenterGroup转化ResponsibilityCenterGroupCO
     * @param resCenterGroup
     * @return
     */
    public ResponsibilityCenterGroupCO toCO(ResponsibilityCenterGroup resCenterGroup){
        List<Long> resIdList = groupCenterRelationshipService.listGroupCenterRelByGroupId(resCenterGroup.getId());
        return ResponsibilityCenterGroupCO
                .builder()
                .id(resCenterGroup.getId())
                .groupName(resCenterGroup.getGroupName())
                .groupCode(resCenterGroup.getGroupCode())
                .resCenterIds(resIdList)
                .build();
    }
}
