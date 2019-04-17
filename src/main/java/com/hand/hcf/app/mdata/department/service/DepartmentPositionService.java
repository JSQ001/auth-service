package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.DepartmentPositionCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentPositionCode;
import com.hand.hcf.app.mdata.department.persistence.DepartmentPositionMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.DEPARTMENT_POSITION})
public class DepartmentPositionService extends ServiceImpl<DepartmentPositionMapper, DepartmentPosition> {

    private final Logger log = LoggerFactory.getLogger(DepartmentPositionService.class);

    @Autowired
    private DepartmentPositionMapper departmentPositionMapper;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString()")
    public List<DepartmentPosition> listByTenantId(long tenantId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tenant_id", tenantId);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositionList = departmentPositionMapper.selectByMap(map);
        departmentPositionList = baseI18nService.convertListByLocale(departmentPositionList);
        return departmentPositionList;
    }

    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString().concat(#enabled)")
    public List<DepartmentPosition> listByTenantIdAndEnabled(long tenantId, boolean enabled) {
        Map<String, Object> map = new HashMap<>();
        map.put("tenant_id", tenantId);
        map.put("enabled", enabled);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositionList = departmentPositionMapper.selectByMap(map);
        departmentPositionList = baseI18nService.convertListByLocale(departmentPositionList);
        return departmentPositionList;

    }

    public long getPostionId(long tenantId, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("tenant_id", tenantId);
        map.put("position_code", code);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositions = departmentPositionMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(departmentPositions)) {
            return departmentPositions.get(0).getId();
        } else {
            return -1;
        }
    }

    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString().concat(#name)")
    public DepartmentPosition getPostionByName(long tenantId, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("position_name", name);
        map.put("tenant_id", tenantId);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositions = departmentPositionMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(departmentPositions)) {
            return departmentPositions.get(0);
        } else {
            return null;
        }
    }

    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString().concat(#code)")
    public DepartmentPosition getPostionByCode(long tenantId, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("position_code", code);
        map.put("tenant_id", tenantId);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositions = departmentPositionMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(departmentPositions)) {
            return departmentPositions.get(0);
        } else {
            return null;
        }
    }

    public void init() {
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            this.initDepartmentPosition();
            return null;
        });
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);
    }

    @CacheEvict(allEntries = true)
    public void initDepartmentPosition() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DepartmentPositionCode.codes.length; i++) {
            DepartmentPosition departmentPosition = new DepartmentPosition();
            departmentPosition.setTenantId(OrgInformationUtil.getCurrentTenantId());
            departmentPosition.setPositionCode(DepartmentPositionCode.codes[i]);
            departmentPosition.setPositionName(DepartmentPositionCode.names[i]);

            DepartmentPosition queryDepartmentPosition = departmentPositionMapper.selectOne(departmentPosition);
            if (queryDepartmentPosition == null) {
                departmentPositionMapper.insert(departmentPosition);
            }
        }
        log.info("修复角色结束，共耗时:{}秒", (System.currentTimeMillis() - start) / 1000);
    }

    public DepartmentPosition save(DepartmentPosition departmentPosition, boolean sysCodeCheck) {
        if (departmentPosition.getTenantId() == null || departmentPosition.getTenantId() <= 0) {
            departmentPosition.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }

        List<DepartmentPosition> departmentPositionList = listByTenantId(departmentPosition.getTenantId());
//        if(departmentPositionList != null && departmentPositionList.size() >= 13){
//            throw new BizException(RespCode.DEPARTMENT_POSITION_32007);
//        }
        DepartmentPosition existDepartmentPosition = getPostionByName(departmentPosition.getTenantId(), departmentPosition.getPositionName());
        if (existDepartmentPosition != null) {
            throw new BizException(RespCode.DEPARTMENT_ROLE_NAME_EXISTS);
        }

        String code = departmentPosition.getPositionCode();
        checkPositionCode(code);
        if (sysCodeCheck) {//除系统创建外其他都需要进行系统保留编码校验
            checkSysCode(code);
        }
        String name = departmentPosition.getPositionName();
        checkPositionName(name);
        existDepartmentPosition = getPostionByCode(departmentPosition.getTenantId(), code);
        if (existDepartmentPosition != null) {
            throw new BizException(RespCode.DEPARTMENT_ROLE_CODE_REPEAT);
        }

        departmentPositionMapper.insert(departmentPosition);
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), departmentPosition, messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.ADD_DEPARTMENT_POSITION, departmentPosition.getPositionCode()), OperationEntityTypeEnum.DEPARTMENT_POSITION.getKey(), OperationTypeEnum.ADD.getKey(), departmentPosition.getTenantId());
        return departmentPosition;
    }

    public DepartmentPosition update(DepartmentPosition departmentPosition) {
        if (departmentPosition.getTenantId() == null || departmentPosition.getTenantId() <= 0) {
            departmentPosition.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        DepartmentPosition oldPosition = new DepartmentPosition();
        DepartmentPosition newPosition = new DepartmentPosition();
        DepartmentPosition existDepartmentPosition = departmentPositionMapper.selectById(departmentPosition.getId());
        if (existDepartmentPosition == null) {
            throw new BizException(RespCode.DEPARTMENT_ROLE_NAME_NOT_EXISTS);
        }
        if (!existDepartmentPosition.getPositionName().equals(departmentPosition.getPositionName())) {
            DepartmentPosition queryEntity = getPostionByName(departmentPosition.getTenantId(), departmentPosition.getPositionName());
            if (queryEntity != null) {
                throw new BizException(RespCode.DEPARTMENT_ROLE_NAME_EXISTS);
            }
        }
        BeanUtils.copyProperties(existDepartmentPosition, oldPosition);

        String code = departmentPosition.getPositionCode();
        checkPositionCode(code);
        if (!existDepartmentPosition.getPositionCode().equals(code)) {
            checkSysCode(code);

        }
        if (!existDepartmentPosition.getPositionCode().equals(code)) {
            existDepartmentPosition = getPostionByCode(departmentPosition.getTenantId(), code);
            if (existDepartmentPosition != null) {
                throw new BizException(RespCode.DEPARTMENT_ROLE_CODE_REPEAT);
            }
        }

        departmentPositionMapper.updateById(departmentPosition);
        BeanUtils.copyProperties(departmentPosition, newPosition);
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), oldPosition, newPosition, OperationEntityTypeEnum.DEPARTMENT_POSITION.getKey(), OperationTypeEnum.UPDATE.getKey(), departmentPosition.getTenantId(), newPosition.getPositionName());
        return departmentPosition;
    }

    //jiu.zhao redis
    //@Cacheable(key = "#companyOid.toString()")
    public List<DepartmentPosition> listByCompanyOid(UUID companyOid) {
        Company company = companyService.getByCompanyOidCache(companyOid);
        if (company == null) {
            throw new RuntimeException("公司不存在,companyOid:" + companyOid);
        }
        long tenantId = company.getTenantId();
        Map<String, Object> map = new HashMap<>();
        map.put("tenant_id", tenantId);
        map.put("deleted", false);
        List<DepartmentPosition> departmentPositionList = departmentPositionMapper.selectByMap(map);
        departmentPositionList = baseI18nService.convertListByLocale(departmentPositionList);
        return departmentPositionList;

    }

    @CacheEvict(allEntries = true)
    public List<DepartmentPosition> saveOrUpdate(List<DepartmentPosition> departmentPositionList) {
        long tenantId = OrgInformationUtil.getCurrentTenantId();
        if (CollectionUtils.isNotEmpty(departmentPositionList)) {
            departmentPositionList.stream().forEach(departmentPosition -> {
                departmentPosition.setTenantId(tenantId);
                if (departmentPosition.getId() == null) {
                    save(departmentPosition, true);
                } else {
                    update(departmentPosition);
                }
            });
        }
        return departmentPositionList;
    }

    public void checkPositionCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new BizException(RespCode.DEPARTMENT_ROLE_CODE_EMPTY);
        }
        String reg2 = "[0-9]{4,9}";
        //判断部门组code长度是否超过限制
        if (!code.matches(reg2)) {//36=50-14(删除的时候需要加占14个字符)
            throw new BizException(RespCode.DEPARTMENT_ROLE_CODE_LENGTH_EXCEEDS_LIMIT_OR_ILLEGAL);
        }
    }

    private void checkPositionName(String name) {
        if (StringUtils.isBlank(name)) {
            //名称不能为空
            throw new BizException(RespCode.DEPARTMENT_ROLE_NAME_EMPTY);
        }
    }

    public void checkSysCode(String code) {
        if (code.startsWith("0") || code.startsWith("1") || code.startsWith("6")) {
            throw new BizException(RespCode.WRONG_START_CODE);
        }
    }

    public List<DepartmentPosition> listByUserAndDepartment(Long departmentId, UUID userOid) {
        return departmentPositionMapper.selectDepartmentPositionByUserAndDepartment(departmentId, userOid);
    }

    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString().concat(#enabled.toString()).concat(#mybatisPage.getCurrent()).concat(':').concat(#mybatisPage.getSize())")
    public com.baomidou.mybatisplus.plugins.Page<DepartmentPosition> pageByTenantId(long tenantId, boolean enabled, com.baomidou.mybatisplus.plugins.Page mybatisPage) {
        List<DepartmentPosition> departmentPositionList = departmentPositionMapper.getDepartmentPositionList(mybatisPage, tenantId, enabled);
        mybatisPage.setRecords(departmentPositionList);
        return mybatisPage;
    }

    public DepartmentPositionCO toCO(DepartmentPosition departmentPosition) {
        DepartmentPositionCO departmentPositionCO = new DepartmentPositionCO();
        departmentPositionCO.setId(departmentPosition.getId());
        departmentPositionCO.setPositionCode(departmentPosition.getPositionCode());
        departmentPositionCO.setPositionName(departmentPosition.getPositionName());
        return departmentPositionCO;
    }
}
