package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDetail;
import com.hand.hcf.app.mdata.department.persistence.DepartmentGroupDetailMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DepartmentGroupDetailService extends ServiceImpl<DepartmentGroupDetailMapper,DepartmentGroupDetail> {
    @Autowired
    private DepartmentGroupDetailMapper departmentGroupDetailMapper;
    @Autowired
    private DepartmentGroupService departmentGroupService;


    //新增或修改
    public DepartmentGroupDetail insertOrUpdateDepartmnetGroupDetail(DepartmentGroupDetail departmentGroupDetail, UUID userId){
        //首先校验部门组id对应的数据是否被删除
        try{
            if(departmentGroupService.selectById(departmentGroupDetail.getDepartmentGroupId()).getDeleted()){
                throw new BizException(RespCode.DEPARTMENT_GROUP_DELETED_23002);
            }
        }catch (NullPointerException e){
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        departmentGroupDetail.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if(departmentGroupDetail.getId() == null){//新增
            //插入前，为保证不重复，需要保证部门组和部门id在表中没有，禁止重复
            EntityWrapper<DepartmentGroupDetail> wrapper = new EntityWrapper<>();
            wrapper.eq("department_group_id",departmentGroupDetail.getDepartmentGroupId());
            wrapper.eq("department_id",departmentGroupDetail.getDepartmentId());
            wrapper.eq("tenant_id",departmentGroupDetail.getTenantId());
            wrapper.eq("deleted",false);
            if(departmentGroupDetailMapper.selectList(wrapper).size()!=0){
                throw new BizException(RespCode.DEPARTMENT_GROUP_DETAIL_EXIT_21002);
            }

            departmentGroupDetailMapper.insert(departmentGroupDetail);
            return  departmentGroupDetailMapper.selectById(departmentGroupDetail.getId());
        }
        //修改

        departmentGroupDetailMapper.updateById(departmentGroupDetail);
        return departmentGroupDetailMapper.selectById(departmentGroupDetail.getId());
    }


    //根据明细id,部门组明细删除
    public boolean deleteDepartmentGroupById(Long id){
        DepartmentGroupDetail departmentGroupDetail = departmentGroupDetailMapper.selectById(id);
        if(departmentGroupDetail == null){
            throw new BizException(RespCode.DEPARTMENT_GROUP_DETAIL_NULL_21001);
        }
        departmentGroupDetail.setDeleted(true);
        int i = departmentGroupDetailMapper.updateById(departmentGroupDetail);
        if(i != 0){
            return true;
        }
        return false;
    }


    //根据部门组id,删除部门组id对应的部门组明细
    public boolean deleteDepartmentGroupDetailByGroupId(Long groupId){

        //首先根据groupId查询，是否能得到明细，不能得到则不用删除
        if(selectByDepartmentGourpId(groupId,new Page()).getRecords()==null){
            return true;
        };

        DepartmentGroupDetail departmentGroupDetail = new DepartmentGroupDetail();
        departmentGroupDetail.setDeleted(true);
        departmentGroupDetail.setId(groupId);
        int i = departmentGroupDetailMapper.updateById(departmentGroupDetail);
        return i!=0?true:false;
    }


    //根据部门组id查询部门组明细
    public Page<DepartmentGroupDetail> selectByDepartmentGourpId(Long departmentGroupId, Page<DepartmentGroupDetail> page){
        if(departmentGroupService.selectById(departmentGroupId).getDeleted()){
            throw new BizException(RespCode.DEPARTMENT_GROUP_NOT_FOUND_23001);
        }
        EntityWrapper<DepartmentGroupDetail> wrapper = new EntityWrapper<>();
        wrapper.eq("department_group_id",departmentGroupId);
        wrapper.eq("deleted",false);
        //条件查询当前租户下的部门组明细
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        List<DepartmentGroupDetail> list = departmentGroupDetailMapper.selectPage(page,wrapper);
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }



    //当前租户下，部门组明细条件查询
    public Page<DepartmentGroupDetail> selectByInput(String departmentGroupCode, String departmentGroupDescription, Page<DepartmentGroupDetail> page, Long DepartmentGroupId){

        page.getRecords();
        List<DepartmentGroupDetail> list = departmentGroupDetailMapper.selectByInput(OrgInformationUtil.getCurrentTenantId(),departmentGroupCode,departmentGroupDescription,DepartmentGroupId,page);
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }


    //批量删除部门组明细
    public Boolean deleteDepartmentGroupBatch(List<Long> ids){
        boolean flag = true;
        for(int i=0;i<ids.size();i++){
            flag = flag && deleteDepartmentGroupById(ids.get(i));
        }
        return flag;

    }

    //批量新增部门组明细
    public Boolean addDepartmentDetailBatch(List<DepartmentGroupDetail> list){

        for(int i = 0;i<list.size();i++){
           insertOrUpdateDepartmnetGroupDetail(list.get(i),OrgInformationUtil.getCurrentUserOid());
        }
        return true;
    }


}
