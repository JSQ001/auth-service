package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ResponsibilityCenterCO;
import com.hand.hcf.app.common.co.ResponsibilityCenterGroupCO;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenterGroup;
import com.hand.hcf.app.mdata.responsibilityCenter.service.DepartmentSobResponsibilityService;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterGroupService;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterService;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/3/5 9:53
 * @version: 1.0.0
 */
@RestController
public class ResponsibilityCenterControllerImpl {

    @Autowired
    private ResponsibilityCenterService responsibilityCenterService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private DepartmentSobResponsibilityService departmentSobResponsibilityService;

    @Autowired
    private ResponsibilityCenterGroupService resCenterGroupService;
    /**
     * 根据责任中心id列表查询责任中心信息
     * @param setOfBooksId 账套id
     * @param info  责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param ids 责任中心Id列表
     * @param enabled 启用禁用
     * @return
     */
    public Page<ResponsibilityCenterCO> pageByResponsibilityCenterByIds(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                        @RequestParam(value = "info", required = false) String info,
                                                                        @RequestParam(value = "codeFrom", required = false) String codeFrom,
                                                                        @RequestParam(value = "codeTo", required = false) String codeTo,
                                                                        @RequestBody(required = false) List<Long> ids,
                                                                        @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page mybatisPage = PageUtil.getPage(page, size);
        List<ResponsibilityCenter> respCenterList = responsibilityCenterService.pageByResponsibilityCenterByIds(setOfBooksId,info,codeFrom,codeTo,ids,enabled,mybatisPage);
        mybatisPage.setRecords(mapperFacade.mapAsList(respCenterList,ResponsibilityCenterCO.class));
        return mybatisPage;
    }

    /**
     * 根据id获取责任中心信息
     * @param id 责任中心id
     * @return
     */
    public ResponsibilityCenterCO getResponsibilityCenterById(@PathVariable("id") Long id) {
        if(id != null){
            ResponsibilityCenter responsibilityCenter = responsibilityCenterService.getResponsibilityCenterById(id);
            return mapperFacade.map(responsibilityCenter,ResponsibilityCenterCO.class);
        }
        return null;
    }
    /**
     * 根据公司Id 部门Id获取部门下责任中心
     * @param companyId 公司Id
     * @param deparmentId 部门Id
     * @param info 责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param enabled 启用禁用
     * @param page
     * @return
     */
    public Page<ResponsibilityCenterCO> pageDepartmentSobResponsibilityByCond(Long companyId,
                                                                              Long deparmentId,
                                                                              String info,
                                                                              String codeFrom,
                                                                              String codeTo,
                                                                              Boolean enabled,
                                                                              int page,
                                                                              int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        List<ResponsibilityCenter> responsibilityCenters = departmentSobResponsibilityService.pageDepartmentSobResponsibilityByCond(
                companyId,
                null,
                deparmentId,
                info,
                codeFrom,
                codeTo,
                enabled,
                mybatisPage);
        mybatisPage.setRecords(mapperFacade.mapAsList(responsibilityCenters,ResponsibilityCenterCO.class));
        return mybatisPage;
    }
    /**
     * 查询责任中心信息 - 分页
     * @param setOfBooksId
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param keyWord
     * @param ids
     * @param enabled
     * @param page
     * @param size
     * @return
     */
    public Page<ResponsibilityCenterCO> pageByResponsibilityCenterByCond(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                         @RequestParam(value = "code", required = false) String code,
                                                                         @RequestParam(value = "codeFrom", required = false) String codeFrom,
                                                                         @RequestParam(value = "codeTo", required = false) String codeTo,
                                                                         @RequestParam(value = "name", required = false) String name,
                                                                         @RequestParam(value = "keyWord", required = false) String keyWord,
                                                                         @RequestBody(required = false) List<Long> ids,
                                                                         @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<ResponsibilityCenterCO> mybatisPage = PageUtil.getPage(page, size);
        return responsibilityCenterService.pageByResponsibilityCenterByCond(setOfBooksId, code, codeFrom, codeTo, name, keyWord, ids, enabled, mybatisPage);
    }

    /**
     * 查询责任中心信息
     * @param setOfBooksId
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param keyWord
     * @param ids
     * @param enabled
     * @return
     */
    public List<ResponsibilityCenterCO> listByResponsibilityCenterByCond(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                         @RequestParam(value = "code", required = false) String code,
                                                                         @RequestParam(value = "codeFrom", required = false) String codeFrom,
                                                                         @RequestParam(value = "codeTo", required = false) String codeTo,
                                                                         @RequestParam(value = "name", required = false) String name,
                                                                         @RequestParam(value = "keyWord", required = false) String keyWord,
                                                                         @RequestBody(required = false) List<Long> ids,
                                                                         @RequestParam(value = "enabled", required = false) Boolean enabled) {
        return responsibilityCenterService.listByResponsibilityCenterByCond(setOfBooksId, code, codeFrom, codeTo, name, keyWord, ids, enabled);
    }

    /**
     * 根据公司部门获取默认的成本中心
     * @param companyId
     * @param departmentId
     * @return
     */
    public ResponsibilityCenterCO getDefaultResponsibilityCenter(@RequestParam(value = "companyId") Long companyId,
                                                                 @RequestParam(value = "departmentId") Long departmentId) {
        ResponsibilityCenter responsibilityCenter = departmentSobResponsibilityService.getDefaultResponsibilityCenter(departmentId, companyId);
        return mapperFacade.map(responsibilityCenter,ResponsibilityCenterCO.class);
    }

    /**
     * 根据责任中心id集合获取责任中集合
     * @param idList
     * @return
     */
    public List<ResponsibilityCenterCO> getResponsibilityCenterByIdList(List<Long> idList) {
        return responsibilityCenterService.getResponsibilityCenterByIdList(idList);
    }

    /**
     * 根据责任中心组Id获取其责任中心
     * @param groupId 责任中心组Id
     * @return
     */
    public List<ResponsibilityCenterCO> listResponsibilityCenterByGroupId(@PathVariable(value = "groupId")  Long groupId) {
        List<ResponsibilityCenter> res = resCenterGroupService.listResponsibilityCenterByGroupId(groupId);
        return mapperFacade.mapAsList(res,ResponsibilityCenterCO.class);
    }
    /**
     * 根据责任中心获取责任中心组 （预算模块）
     * @param responsibilityCenterId 责任中心
     * @return
     */
    //@Override
    public List<ResponsibilityCenterGroupCO> listResponsibilityCenterGroupByResCenterId(@RequestParam(value = "responsibilityCenterId") Long responsibilityCenterId) {
        List<ResponsibilityCenterGroup> responsibilityCenters =  resCenterGroupService.listResponsibilityCenterGroupByResCenterId(responsibilityCenterId);
        return mapperFacade.mapAsList(responsibilityCenters,ResponsibilityCenterGroupCO.class);
    }

    /**
     * 根据账套Id获取责任中心组及其关联责任中心信息
     * @return
     */
    //@Override
    public List<ResponsibilityCenterGroupCO> listResCenterGroupBySetOfBooksId() {
        List<ResponsibilityCenterGroupCO> resCenterGroupCOS = new ArrayList<>();
        List<ResponsibilityCenterGroup> resCenterGroups = resCenterGroupService.listResCenterGroupBySetOfBooksId();
        resCenterGroups
                .stream()
                .forEach(resCenterGroup -> {
           resCenterGroupCOS.add(resCenterGroupService.toCO(resCenterGroup));
        });
        return resCenterGroupCOS;
    }

    /**
     * 根据责任中心组Ids责任中心组及其关联责任中心信息
     * @param groupIds 责任中心组Ids
     * @return
     */
    //@Override
    public List<ResponsibilityCenterGroupCO> listResCenterGroupByIds(List<Long> groupIds) {
        List<ResponsibilityCenterGroupCO> resCenterGroupCOS = new ArrayList<>();
       List<ResponsibilityCenterGroup> resCenterGroups = resCenterGroupService.selectList(
               new EntityWrapper<ResponsibilityCenterGroup>()
                       .in("id",groupIds));
       resCenterGroups
               .stream()
               .forEach(resCenterGroup -> {
           resCenterGroupCOS.add(resCenterGroupService.toCO(resCenterGroup));
       });
        return resCenterGroupCOS;
    }

    /**
     * 公司为空情况下获取默认责任中心
     * @param tenantId 租户
     * @param setOfBooksId 账套
     * @param unitId 部门
     * @param companyId 公司
     * @return 默认责任中心
     */
    //@Override
    public ResponsibilityCenterCO getDepartmentDefaultResponsibility(@RequestParam("tenantId") Long tenantId,
                                                                     @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                     @RequestParam("unitId") Long unitId,
                                                                     @RequestParam(value = "companyId", required = false) Long companyId) {
        return departmentSobResponsibilityService.getDefaultResponsibilityCenterByUnit(tenantId, setOfBooksId, unitId, companyId);
    }

}
