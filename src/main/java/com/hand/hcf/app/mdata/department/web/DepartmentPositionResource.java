package com.hand.hcf.app.mdata.department.web;

import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import com.hand.hcf.app.mdata.department.service.DepartmentPositionService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departmentposition")
public class DepartmentPositionResource {

    @Autowired
    private DepartmentPositionService departmentPositionService;
    @Autowired
    private BaseI18nService baseI18nService;

    @RequestMapping(value = "/init", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> initTenant() {
        departmentPositionService.init();
        return ResponseEntity.ok().build();
    }

    /**
     * 查询当前租户的角色列表
     */
    @Timed
    @RequestMapping(method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentPosition>> getPostionList(@RequestParam(value = "isEnabled",required = false) boolean isEnabled){
        long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<DepartmentPosition> result = null;
        if (isEnabled){
            result = departmentPositionService.listByTenantIdAndEnabled(tenantId,isEnabled);
        }else{
            result = departmentPositionService.listByTenantId(tenantId);
        }
        List<Long> ids = result.stream().map(departmentPosition -> departmentPosition.getId()).collect(Collectors.toList());
        result = baseI18nService.selectListBaseTableInfoWithI18n(ids, DepartmentPosition.class);
        result = baseI18nService.convertListByLocale(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Timed
    @RequestMapping(method= RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentPosition>> save(@RequestBody List<DepartmentPosition> departmentPositionList){
        departmentPositionList = departmentPositionService.saveOrUpdate(departmentPositionList);
        return new ResponseEntity<>(departmentPositionList, HttpStatus.OK);
    }
    @Timed
    @RequestMapping(method= RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentPosition>> update(@RequestBody List<DepartmentPosition> departmentPositionList){
        departmentPositionList = departmentPositionService.saveOrUpdate(departmentPositionList);
        return new ResponseEntity<>(departmentPositionList, HttpStatus.OK);
    }

    /**
     * 查询当前租户的角色列表
     */
    @Timed
    @RequestMapping(value = "/page",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DepartmentPosition>> getPagePostionList(@RequestParam(value = "enabled",required = false) boolean enabled, Pageable pageable){
        com.baomidou.mybatisplus.plugins.Page mybatisPage = new com.baomidou.mybatisplus.plugins.Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        long tenantId = OrgInformationUtil.getCurrentTenantId();
        Page<DepartmentPosition> result = null;
        if (enabled){
            mybatisPage = departmentPositionService.pageByTenantId(tenantId,enabled,mybatisPage);
        }else{
            mybatisPage = departmentPositionService.pageByTenantId(tenantId,false,mybatisPage);
        }
        result = new PageImpl<>(mybatisPage.getRecords(), pageable, mybatisPage.getTotal());
        result.getContent().stream().forEach(departmentPosition -> {
            //多语言bug修改
            DepartmentPosition i18nDepartmentPosition = baseI18nService.selectOneTranslatedTableInfoWithI18n(departmentPosition.getId(),DepartmentPosition.class);
            departmentPosition.setI18n(i18nDepartmentPosition.getI18n());
        });


        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
