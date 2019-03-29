package com.hand.hcf.app.mdata.responsibilityCenter.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.DepartmentResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.DepartmentSobResponsibility;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.DepartmentSobResponsibilityMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentSobResponsibilityService extends BaseService<DepartmentSobResponsibilityMapper,DepartmentSobResponsibility> {

    @Autowired
    private DepartmentSobResponsibilityMapper departmentSobResMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private ResponsibilityCenterService responsibilityCenterService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private DepartmentResponsibilityCenterService departmentResCenterService;

    @Autowired
    private MessageService messageService;

    /**
     * 新增或修改 部门账套责任中心
     * @param departmentSobResDTO
     * @return
     */
    @Transactional
    public DepartmentSobResponsibility insertOrUpdateDepartmentSobResponsibility(DepartmentSobResponsibilityDTO departmentSobResDTO) {
        Long companyId = departmentSobResDTO.getCompanyId();
        DepartmentSobResponsibility departmentSobRes = mapperFacade.map(departmentSobResDTO,DepartmentSobResponsibility.class);
        Wrapper wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                .eq("set_of_books_id", departmentSobRes.getSetOfBooksId())
                .eq(companyId != null,"company_id",companyId)
                .eq("department_id", departmentSobRes.getDepartmentId());
        if(companyId == null){
            wrapper.isNull("company_id");
        }
        //更新
        if(departmentSobResDTO.getId() != null){
            DepartmentSobResponsibility oldDepartmentSobRes = departmentSobResMapper.selectById(departmentSobResDTO.getId());
            if(oldDepartmentSobRes == null){
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_DEPARTMENTSOB_NOT_EXIST);
            }
            //判断是否修改了公司
            if(companyId != null) {
                if (!companyId.equals(oldDepartmentSobRes.getCompanyId())) {
                    if (departmentSobResMapper.selectCount(wrapper) > 0) {
                        throw new BizException(RespCode.RESPONSIBILITY_CENTER_CONFIGURE_REPEAT);
                    }
                }
            }
            departmentSobResMapper.updateById(departmentSobRes);
            //删除之前的关联
            departmentResCenterService.delete(
                    new EntityWrapper<DepartmentResponsibilityCenter>()
                            .eq("department_id",departmentSobRes.getDepartmentId())
                            .eq("set_of_books_id",departmentSobRes.getSetOfBooksId())
                            .eq("relation_id",oldDepartmentSobRes.getId()));
        }else{
            //校验现有配置数据的账套+公司组合与侧滑框中账套+公司组合不重复
            if(departmentSobResMapper.selectCount(wrapper) > 0){
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_CONFIGURE_REPEAT);
            }
            departmentSobResMapper.insert(departmentSobRes);
        }
        //插入责任中心关联
        if("N".equals(departmentSobResDTO.getAllResponsibilityCenter())){
            List<Long> ids = departmentSobResDTO.getIds();
            if(CollectionUtils.isNotEmpty(ids)){
                ids.stream().forEach(id ->{
                    departmentResCenterService.insert(DepartmentResponsibilityCenter
                            .builder()
                            .responsibilityCenterId(id)
                            .departmentId(departmentSobRes.getDepartmentId())
                            .tenantId(OrgInformationUtil.getCurrentTenantId())
                            .setOfBooksId(departmentSobRes.getSetOfBooksId())
                            .relationId(departmentSobRes.getId())
                            .build());
                });
            }
        }
        return departmentSobRes;
    }


    /**
     * 分页查询获取部门责任中心
     * @param departmentId 部门id
     * @param keyword 账套名称或者代码
     * @param page
     * @return
     */
    public Page<DepartmentSobResponsibilityDTO> pageDepartmentSobResponsibilityByDepartmentId(Long departmentId, String keyword, Page page) {
        List<Long> setOfBooksIdList = null;
        if(!StringUtils.isEmpty(keyword)){
            Wrapper<SetOfBooks> setOfBooksWrapper = new EntityWrapper<SetOfBooks>()
                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                    .eq("deleted", false)
                    .orderBy("set_of_books_code");
            setOfBooksWrapper.andNew()
                    .like("set_of_books_code", keyword)
                    .or()
                    .like("set_of_books_name", keyword);
            //根据账套代码或者名称获取账套Id
             setOfBooksIdList = setOfBooksService.selectList(setOfBooksWrapper)
                    .stream()
                    .map(SetOfBooks::getId)
                    .collect(Collectors.toList());
             if(setOfBooksIdList.size() == 0){
                 return page;
             }
        }
        List<DepartmentSobResponsibility> departmentSobResponsibilityList = departmentSobResMapper.selectPage(page,
                new EntityWrapper<DepartmentSobResponsibility>()
                        .eq("department_id",departmentId)
                        .in(!StringUtils.isEmpty(keyword),"set_of_books_id",setOfBooksIdList)
                        .orderBy("set_of_books_id")
                        .orderBy("company_id"));
        List<DepartmentSobResponsibilityDTO> result = mapperFacade.mapAsList(departmentSobResponsibilityList,DepartmentSobResponsibilityDTO.class);
        result.stream().forEach(e-> toDTO(e));
        page.setRecords(result);
        return page;
    }

    /**
     * 部门责任中心转化数据
     * @param departmentSobRes
     * @return
     */
    private void toDTO(DepartmentSobResponsibilityDTO departmentSobRes){
        List<ResponsibilityCenter> responsibilityCenterList = null;
        Long setOfBooksId = departmentSobRes.getSetOfBooksId();
        if(departmentSobRes.getCompanyId() != null){
            Company company = companyService.getCompanyById(departmentSobRes.getCompanyId());
            if(company != null) {
                departmentSobRes.setCompanyName(company.getName());
                departmentSobRes.setCompanyCode(company.getCompanyCode());
            }
        }
        if(setOfBooksId != null){
            SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(setOfBooksId);
            if(setOfBooks != null) {
                departmentSobRes.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
                departmentSobRes.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
            //全部
            if(departmentSobRes.getAllResponsibilityCenter().equals("Y")){
                //获取当前所选账套下所有启用的责任中心
                responsibilityCenterList = responsibilityCenterService.selectList(
                        new EntityWrapper<ResponsibilityCenter>()
                                .eq("set_of_books_id",setOfBooksId)
                                .eq("enabled",true));
            }else {
                //选择部分 获取当前部门所选的责任中心
                List<Long> responsibilityCenterIdList = departmentResCenterService.selectList(
                        new EntityWrapper<DepartmentResponsibilityCenter>()
                                .eq("set_of_books_id",setOfBooksId)
                                .eq("department_id",departmentSobRes.getDepartmentId())
                                .eq("relation_id",departmentSobRes.getId()))
                        .stream()
                        .map(DepartmentResponsibilityCenter::getResponsibilityCenterId)
                        .collect(Collectors.toList());
                responsibilityCenterList= responsibilityCenterService.listByResponsibilityCenterConditionByIds(setOfBooksId,responsibilityCenterIdList,true);
                //可用责任中心 全部或者已选个数
                departmentSobRes.setAllResponsibilityCenterCount(Long.valueOf(responsibilityCenterList.size()));
            }
            departmentSobRes.setResponsibilityCentersList(responsibilityCenterList);
        }
        if(departmentSobRes.getDefaultResponsibilityCenter() != null){
            ResponsibilityCenter center = responsibilityCenterService.selectById(departmentSobRes.getDefaultResponsibilityCenter());
            if(center != null){
                departmentSobRes.setDefaultResponsibilityCenterName(center.getResponsibilityCenterName());
                departmentSobRes.setDefaultResponsibilityCenterCode(center.getResponsibilityCenterCode());
            }
        }
    }

    public List<ResponsibilityCenter> pageDepartmentSobResponsibilityByCond(Long companyId,
                                                                            Long setOfBooksId,
                                                                            Long deparmentId,
                                                                            String info,
                                                                            String codeFrom,
                                                                            String codeTo,
                                                                            Boolean enabled,
                                                                            Page mybatisPage) {
        List<Long> responsibilityCenterIdList = new ArrayList<>();
        List<ResponsibilityCenter> responsibilityCenterList = new ArrayList<>();
        DepartmentSobResponsibility departmentSobRes = this.selectOne(
               new EntityWrapper<DepartmentSobResponsibility>()
                       .eq("department_id",deparmentId)
                       .eq("company_id",companyId));
        if(departmentSobRes == null){
            return  mybatisPage.getRecords();
        }
        //获取关联
        if("Y".equals(departmentSobRes.getAllResponsibilityCenter())){
            //获取当前所选账套下所有启用的责任中心
            responsibilityCenterList = responsibilityCenterService.selectPage(mybatisPage,
                    new EntityWrapper<ResponsibilityCenter>()
                            .eq("set_of_books_id",departmentSobRes.getSetOfBooksId())
                            .eq("enabled",enabled))
                    .getRecords();
            return responsibilityCenterList;
        }
        responsibilityCenterIdList = departmentResCenterService.selectList(
                new EntityWrapper<DepartmentResponsibilityCenter>()
                        .eq("department_id",deparmentId)
                        .eq("relation_id",departmentSobRes.getId()))
                .stream()
                .map(DepartmentResponsibilityCenter::getResponsibilityCenterId)
                .collect(Collectors.toList());
       return responsibilityCenterService.pageByResponsibilityCenterByIds(setOfBooksId, info, codeFrom, codeTo, responsibilityCenterIdList, enabled, mybatisPage);
    }

    /**
     * 根据公司、部门，获取部门默认责任中心
     * @param departmentId
     * @param companyId
     * @return
     */
    public ResponsibilityCenter getDefaultResponsibilityCenter(Long departmentId, Long companyId){
        Wrapper wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                .eq("company_id",companyId)
                .eq("department_id", departmentId);
        DepartmentSobResponsibility departmentSobResponsibility = selectOne(wrapper);
        if(departmentSobResponsibility == null){
            Company company = companyService.selectById(companyId);
            wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                    .eq("set_of_books_id",company.getSetOfBooksId())
                    .eq("department_id", departmentId)
                    .isNull("company_id");
            departmentSobResponsibility = selectOne(wrapper);
        }
        if(departmentSobResponsibility.getDefaultResponsibilityCenter() != null){
            return responsibilityCenterService.selectById(departmentSobResponsibility.getDefaultResponsibilityCenter());
        }
        return null;
    }

    /**
     * 根据部门责任中心Id获取详细信息
     * @param id
     * @return
     */
    public DepartmentSobResponsibilityDTO getDepartmentSobResponsibilityById(Long id) {
        DepartmentSobResponsibilityDTO departmentSobResDTO = null;
        DepartmentSobResponsibility departmentSobResponsibility = baseMapper.selectById(id);
        if(departmentSobResponsibility == null){
            throw  new BizException(messageService.getMessageDetailByCode("RESPONSIBILITY_CENTER_DEPARTMENTSOB_NOT_EXIST"));
        }
        departmentSobResDTO = mapperFacade.map(departmentSobResponsibility,DepartmentSobResponsibilityDTO.class);
        toDTO(departmentSobResDTO);
        return departmentSobResDTO;

    }
}
