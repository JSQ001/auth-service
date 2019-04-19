package com.hand.hcf.app.mdata.responsibilityCenter.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.ResponsibilityCenterCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.DepartmentResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.DepartmentSobResponsibility;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityImportDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.DepartmentSobResponsibilityMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
    @Autowired
    private ExcelImportService excelImportService;
    @Autowired
    private DepartmentService departmentService;

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
        DepartmentSobResponsibility departmentSobResponsibility = null;
        List<DepartmentSobResponsibility> list = this.selectList(wrapper);
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(list)){
            departmentSobResponsibility = list.get(0);
        }
        if(departmentSobResponsibility == null){
            Company company = companyService.selectById(companyId);
            wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                    .eq("set_of_books_id",company.getSetOfBooksId())
                    .eq("department_id", departmentId)
                    .isNull("company_id");
            list = this.selectList(wrapper);
            if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(list)){
                departmentSobResponsibility = list.get(0);
            }
        }
        if(departmentSobResponsibility != null){
            if(departmentSobResponsibility.getDefaultResponsibilityCenter() != null){
                return responsibilityCenterService.selectById(departmentSobResponsibility.getDefaultResponsibilityCenter());
            }
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

    /**
     * 公司为空情况下获取部门默认责任中心
     *
     * @param tenantId 租户id
     * @param setOfBooksId 账套id
     * @param unitId 部门id
     * @param companyId 公司id
     * @return 默认责任中心
     */
    public ResponsibilityCenterCO getDefaultResponsibilityCenterByUnit(Long tenantId, Long setOfBooksId, Long unitId, Long companyId) {
        List<DepartmentSobResponsibility> list = baseMapper.selectList(
                new EntityWrapper<DepartmentSobResponsibility>().eq("tenant_id", tenantId)
                        .eq("set_of_books_id", setOfBooksId)
                        .eq("department_id", unitId)
                        .eq(TypeConversionUtils.isNotEmpty(companyId), "company_id", companyId)
        );
        if (com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(list)) {
            DepartmentSobResponsibility departmentSobResponsibility = list.get(0);
            if (departmentSobResponsibility.getDefaultResponsibilityCenter() != null) {
                return mapperFacade.map(responsibilityCenterService.selectById(departmentSobResponsibility.getDefaultResponsibilityCenter()), ResponsibilityCenterCO.class);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 根据公司、部门，获取部门可用责任中心
     * @param departmentId
     * @param companyId
     * @return
     */
    public List<ResponsibilityCenter> listDepartmentAvailableResCenterByCond(Long departmentId, Long companyId){
        List<ResponsibilityCenter> responsibilityCenterList = new ArrayList<>();
        Wrapper wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                .eq("company_id",companyId)
                .eq("department_id", departmentId);
        DepartmentSobResponsibility departmentSobResponsibility = null;
        List<DepartmentSobResponsibility> list = this.selectList(wrapper);
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(list)){
            departmentSobResponsibility = list.get(0);
        }
        if(departmentSobResponsibility == null){
            Company company = companyService.selectById(companyId);
            wrapper = new EntityWrapper<DepartmentSobResponsibility>()
                    .eq("set_of_books_id",company.getSetOfBooksId())
                    .eq("department_id", departmentId)
                    .isNull("company_id");
            list = this.selectList(wrapper);
            if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(list)){
                departmentSobResponsibility = list.get(0);
            }
        }
        if(departmentSobResponsibility != null){
            //获取当前部门责任中心下可用责任中心
            responsibilityCenterList = listDepartmentResCenter(departmentSobResponsibility);
        }
        return responsibilityCenterList;
    }


    public List<ResponsibilityCenter> listDepartmentResCenter(DepartmentSobResponsibility departmentSobResponsibility){
        List<ResponsibilityCenter> responsibilityCenterList = new ArrayList<>();
        Long setOfBooksId = departmentSobResponsibility.getSetOfBooksId();
        if(departmentSobResponsibility.getAllResponsibilityCenter().equals("Y")){
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
                            .eq("department_id",departmentSobResponsibility.getDepartmentId())
                            .eq("relation_id",departmentSobResponsibility.getId()))
                    .stream()
                    .map(DepartmentResponsibilityCenter::getResponsibilityCenterId)
                    .collect(Collectors.toList());
            responsibilityCenterList= responsibilityCenterService.listByResponsibilityCenterConditionByIds(setOfBooksId,
                    responsibilityCenterIdList,true);
        }
        return  responsibilityCenterList;
    }

    public String importDepartmentPositionUser(List<DepartmentSobResponsibilityImportDTO> list) {
        StringBuilder message = new StringBuilder();
        list.forEach(item -> {
            String errorMessage = "";
            if (TypeConversionUtils.isEmpty(item.getTenantId())
                    || TypeConversionUtils.isEmpty(item.getSetOfBooksCode())
                    || TypeConversionUtils.isEmpty(item.getDepartmentCode())
                    || TypeConversionUtils.isEmpty(item.getResponsibilityCenterCode())) {
                errorMessage += "必输字段为空";
                message.append(errorMessage);
            } else {
                Long tenantId = TypeConversionUtils.parseLong(item.getTenantId());
                SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksByTenantId(tenantId, item.getSetOfBooksCode());
                if (ObjectUtils.isEmpty(setOfBooks)) {
                    errorMessage += "当前租户不存在该账套" + item.getSetOfBooksCode();
                }
                CompanyCO companyCO = null;
                if (TypeConversionUtils.isNotEmpty(item.getCompanyCode())) {
                    companyCO = companyService.getByCompanyCode(item.getCompanyCode());
                    if (ObjectUtils.isEmpty(companyCO)) {
                        errorMessage += "当前账套不存在该公司" + item.getCompanyCode();
                    }
                }
                DepartmentCO department = departmentService.getDepartmentByCodeAndTenantId(item.getDepartmentCode());
                if (ObjectUtils.isEmpty(department)) {
                    errorMessage += "当前租户下不存在该部门" + item.getDepartmentCode();
                }
                ResponsibilityCenter responsibilityCenter = responsibilityCenterService.selectOne(
                        new EntityWrapper<ResponsibilityCenter>()
                        .eq("responsibility_center_code", item.getResponsibilityCenterCode())
                );
                if (ObjectUtils.isEmpty(responsibilityCenter)) {
                    errorMessage += "当前租户下不存在该责任中心" + item.getResponsibilityCenterCode();
                }
                ResponsibilityCenter defaultResponsibilityCenter = null;
                if (TypeConversionUtils.isNotEmpty(item.getDefaultResponsibilityCenter())) {
                    defaultResponsibilityCenter = responsibilityCenterService.selectOne(
                            new EntityWrapper<ResponsibilityCenter>()
                            .eq("responsibility_center_code", item.getDefaultResponsibilityCenter())
                    );
                    if (ObjectUtils.isEmpty(defaultResponsibilityCenter)) {
                        errorMessage += "当前租户下不存在该责任中心" + item.getDefaultResponsibilityCenter();
                    }
                }
                if ("".equals(errorMessage)) {
                    DepartmentSobResponsibility departmentSobResponsibility = null;
                    if (!ObjectUtils.isEmpty(defaultResponsibilityCenter)) {
                        departmentSobResponsibility = getDepartmentSobResponsibility(tenantId, setOfBooks,
                                companyCO, department, defaultResponsibilityCenter);
                    }
                    List<DepartmentResponsibilityCenter> departmentCenters = departmentResCenterService.selectList(
                            new EntityWrapper<DepartmentResponsibilityCenter>()
                            .eq("tenant_id", tenantId)
                            .eq("set_of_books_id", setOfBooks.getId())
                            .eq("department_id", department.getId())
                    );
                    if (departmentCenters.size() == 0) {
                        DepartmentResponsibilityCenter departmentCenter = new DepartmentResponsibilityCenter();
                        departmentCenter.setTenantId(tenantId);
                        departmentCenter.setSetOfBooksId(setOfBooks.getId());
                        departmentCenter.setDepartmentId(department.getId());
                        if (departmentSobResponsibility != null) {
                            departmentCenter.setRelationId(departmentSobResponsibility.getId());
                        }
                        departmentCenter.setResponsibilityCenterId(responsibilityCenter.getId());
                        departmentResCenterService.insert(departmentCenter);
                    }
                } else {
                    message.append(errorMessage);
                }
            }
        });
        if ("".equals(message.toString())) {
            return "导入成功";
        } else {
            return "导入失败："+message.toString();
        }
    }

    /**
     *
     * @param tenantId 账套id
     * @param setOfBooks 租户
     * @param companyCO 机构
     * @param department 部门
     * @param defaultResponsibilityCenter 默认责任中心
     * @return 部门账套责任中心数据
     */
    private DepartmentSobResponsibility getDepartmentSobResponsibility(
            Long tenantId,
            SetOfBooks setOfBooks,
            CompanyCO companyCO,
            DepartmentCO department,
            ResponsibilityCenter defaultResponsibilityCenter) {
        DepartmentSobResponsibility departmentSobResponsibility = null;
        int count = baseMapper.selectCount(
                new EntityWrapper<DepartmentSobResponsibility>()
                        .eq("tenant_id", tenantId)
                        .eq("set_of_books_id", setOfBooks.getId())
                        .eq("department_id", department.getId())
                        .eq(TypeConversionUtils.isNotEmpty(companyCO), "company_id", companyCO.getId()));
        // 尚未分配默认责任中心，则插入一条数据
        if (count == 0) {
            departmentSobResponsibility = new DepartmentSobResponsibility();
            departmentSobResponsibility.setTenantId(tenantId);
            departmentSobResponsibility.setSetOfBooksId(setOfBooks.getId());
            departmentSobResponsibility.setDepartmentId(department.getId());
            if (TypeConversionUtils.isNotEmpty(companyCO)) {
                departmentSobResponsibility.setCompanyId(companyCO.getId());
            }
            departmentSobResponsibility.setDefaultResponsibilityCenter(defaultResponsibilityCenter.getId());
            departmentSobResponsibility.setAllResponsibilityCenter("N");
            baseMapper.insert(departmentSobResponsibility);
        } else if (count == 1) {
            // 已经分配了默认责任中心，则更新该默认责任中心
            departmentSobResponsibility = baseMapper.selectList(
                    new EntityWrapper<DepartmentSobResponsibility>()
                            .eq("tenant_id", tenantId)
                            .eq("set_of_books_id", setOfBooks.getId())
                            .eq("department_id", department.getId())
                            .eq(TypeConversionUtils.isNotEmpty(companyCO), "company_id", companyCO.getId())
            ).get(0);
            departmentSobResponsibility.setDefaultResponsibilityCenter(defaultResponsibilityCenter.getId());
            this.updateById(departmentSobResponsibility);
        }
        return departmentSobResponsibility;
    }
}
