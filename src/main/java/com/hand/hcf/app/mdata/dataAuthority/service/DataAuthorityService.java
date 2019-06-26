package com.hand.hcf.app.mdata.dataAuthority.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.implement.web.UserRoleControllerImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.HttpServletUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.DataAuthValuePropertyDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthority;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRule;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.app.mdata.dataAuthority.persistence.DataAuthorityMapper;
import com.hand.hcf.app.mdata.department.service.DepartmentGroupService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:48
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthorityService extends BaseService<DataAuthorityMapper,DataAuthority> {

    private final DataAuthorityMapper dataAuthorityMapper;
    private final DataAuthorityRuleService dataAuthorityRuleService;
    private final DataAuthorityRuleDetailService dataAuthorityRuleDetailService;
    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;
    private final BaseI18nService baseI18nService;
    private final DepartmentGroupService departmentGroupService;
    private final DepartmentService departmentService;
    private final CompanyService companyService;

    @Autowired
    private UserRoleControllerImpl userRoleClient;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    /**
     * 保存数据权限
     */
    @Transactional
    public DataAuthority saveDataAuthority(DataAuthority entity) {
        if (entity.getTenantId() == null) {
            entity.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        Integer integer = dataAuthorityMapper.selectCount(new EntityWrapper<DataAuthority>()
                .eq("tenant_id", entity.getTenantId())
                .eq("data_authority_code", entity.getDataAuthorityCode())
                .ne(entity.getId() != null, "id", entity.getId()));
        if(integer > 0){
            throw new BizException(RespCode.AUTH_DATA_AUTHORITY_EXISTS);
        }
        if(entity.getId() !=null){
            dataAuthorityMapper.updateAllColumnById(entity);
        }else {
            dataAuthorityMapper.insert(entity);
        }
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRules())){
            dataAuthorityRuleService.saveDataAuthorityRuleBatch(entity.getDataAuthorityRules(),entity.getId());
        }
        return entity;
    }

    /**
     * 删除数据权限
     * @param id
     */
    @Transactional
    public void deleteDataAuthorityById(Long id) {
        //校验数据权限是否被引用
        boolean isCited = hcfOrganizationInterface.dataAuthHasUsed(id);

            if(isCited){
            throw new BizException(RespCode.AUTH_DATA_AUTHORITY_CITED);
        }
        DataAuthority dataAuthority = dataAuthorityMapper.selectById(id);
        dataAuthority.setDeleted(true);
        dataAuthority.setDataAuthorityCode(dataAuthority.getDataAuthorityCode() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        dataAuthorityRuleService.deleteDataAuthRuleByAuthId(id);
        dataAuthorityRuleDetailService.delete(new EntityWrapper<DataAuthorityRuleDetail>().eq("data_authority_id",id));
        dataAuthorityRuleDetailValueService.delete(new EntityWrapper<DataAuthorityRuleDetailValue>().eq("data_authority_id",id));
        dataAuthorityMapper.updateById(dataAuthority);
    }

    /**
     * 批量删除数据权限
     * @param ids
     */
    @Transactional
    public void deleteDataAuthorityByIds(List<Long> ids){
        ids.forEach(id -> deleteDataAuthorityById(id));
    }

    /**
     * 数据权限查询
     * @param dataAuthorityCode
     * @param dataAuthorityName
     * @param page
     * @return
     */
    public List<DataAuthority> getDataAuthorityByCond(Long id,
                                                      String dataAuthorityCode,
                                                      String dataAuthorityName,
                                                      Page pageable) {
        return dataAuthorityMapper.selectPage(pageable, new EntityWrapper<DataAuthority>()
                .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
                .eq(TypeConversionUtils.isNotEmpty(id), "id", id)
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityCode), "data_authority_code", dataAuthorityCode)
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityName), "data_authority_name", dataAuthorityName)
                .eq("deleted", false)
                .orderBy("enabled desc,data_authority_code asc"));
    }

    /**
     * 获取数据权限，并获取明细信息
     * @param id
     * @return
     */
    public DataAuthority getDataAuthorityById(Long id,Long ruleId){
        DataAuthority dataAuthority = baseI18nService.selectOneTranslatedTableInfoWithI18n(id,DataAuthority.class);
        List<DataAuthorityRule> dataAuthorityRules = dataAuthorityRuleService.queryDataAuthorityRules(id,ruleId);
        dataAuthority.setDataAuthorityRules(dataAuthorityRules);
        return dataAuthority;
    }

    /**
     * 获取数据权限，并获取明细信息
     * @param id
     * @return
     */
    public DataAuthority getDataAuthorityById(Long id){
        return getDataAuthorityById(id,null);
    }

    /**
     * 获取数据权限，并获取明细信息并过滤为启用的数据权限
     * @param ids 权限id
     * @return
     */
    public List<DataAuthority> getDataAuthorityByIds(Set<Long> ids){
        return ids.stream().map(id -> getDataAuthorityById(id)).filter(t-> t.getEnabled()).collect(Collectors.toList());
    }

    /**
     * 根绝登录人信息获取相关数据权限
     * @return
     */
    public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValuePropertiesByRequest() {
        //需要根据登录人信息获取 关联的数据权限
        List<Map<String, List<DataAuthValuePropertyDTO>>> list = new ArrayList<>();
        String headerMessage = HttpServletUtil.getHeaderMessage("X-Menu-Id");
        if (headerMessage == null) {
            return list;
        }
        Long functionId = Long.valueOf(headerMessage);
        List<Long> listDataAuth = userRoleClient.listDataAuthByFunctionId(functionId);
        Set<Long> dataAuthIds = new HashSet(listDataAuth);
        if(CollectionUtils.isNotEmpty(dataAuthIds)) {
            List<DataAuthority> dataAuthorityByIds = getDataAuthorityByIds(dataAuthIds);
            // 由于规则与规则之间为or关系，且多个数据结构之间也为or管线，所以只要有一个规则所有取值范围为全部，则表示其他规则全部无效，直接取全部值
            // 考虑到数据权限配置的数据量不会很大，在获取明细值之前判断，减少查询时间
//            boolean all = dataAuthorityByIds.stream().anyMatch(dataAuthority -> {
//                List<DataAuthorityRule> dataAuthorityRules = dataAuthority.getDataAuthorityRules();
//                if (CollectionUtils.isNotEmpty(dataAuthorityRules)) {
//                    return dataAuthorityRules.stream().anyMatch(dataAuthorityRule -> {
//                        return dataAuthorityRule.getDataAuthorityRuleDetails().stream().allMatch(dataAuthorityRuleDetail -> "1001".equals(dataAuthorityRuleDetail.getDataScope()));
//                    });
//                }
//                return false;
//            });
//            if (all) {
//                return list;
//            }
            dataAuthIds.stream().forEach(dataAuthId -> {
                DataAuthority dataAuthorityById = getDataAuthorityById(dataAuthId);
                if (dataAuthorityById != null && dataAuthorityById.getEnabled()) {
                    Map<String, List<DataAuthValuePropertyDTO>> dataAuthRulePropertiesMap = new HashMap<>();
                    List<DataAuthorityRule> dataAuthorityRules = dataAuthorityById.getDataAuthorityRules();
                    if (CollectionUtils.isNotEmpty(dataAuthorityRules)) {
                        dataAuthorityRules.stream().forEach(dataAuthorityRule -> {
                            List<DataAuthorityRuleDetail> dataAuthorityRuleDetails = dataAuthorityRule.getDataAuthorityRuleDetails();
                                List<DataAuthValuePropertyDTO> collect = dataAuthorityRuleDetails.stream().map(dataAuthorityRuleDetail -> {
                                DataAuthValuePropertyDTO dataAuthValuePropertyDTO = new DataAuthValuePropertyDTO();
                                dataAuthValuePropertyDTO.setDataType(dataAuthorityRuleDetail.getDataType());
                                dataAuthValuePropertyDTO.setFiltrateMethod(StringUtils.isNotEmpty(dataAuthorityRuleDetail.getFiltrateMethod()) ? dataAuthorityRuleDetail.getFiltrateMethod(): "INCLUDE");
                                // 全部
                                if ("1001".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setAllFlag(true);
                                    return dataAuthValuePropertyDTO;
                                }
                                dataAuthValuePropertyDTO.setAllFlag(false);
                                // 当前 当前及下属
                                if ("1002".equals(dataAuthorityRuleDetail.getDataScope()) || "1003".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setValueKeyList(
                                            getDataValueKeyToStringByDataTypeAndDataScope(dataAuthorityRuleDetail.getDataType(), dataAuthorityRuleDetail.getDataScope()));
                                    // 手工选择
                                } else if ("1004".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setValueKeyList(dataAuthorityRuleDetail.getDataAuthorityRuleDetailValues());
                                }
                                return dataAuthValuePropertyDTO;
                            }).collect(Collectors.toList());
                            dataAuthRulePropertiesMap.put(dataAuthorityRule.getDataAuthorityRuleName(), collect);
                        });
                    }
                    list.add(dataAuthRulePropertiesMap);
                }
            });
        }
        return list;
    }

    private List<String> getDataValueKeyToStringByDataTypeAndDataScope(String dataType,String dataScope){
        return getDataValueKeyByDataTypeAndDataScope(dataType,dataScope).stream().map(e -> e.toString()).collect(Collectors.toList());
    }

    private List<Long> getDataValueKeyByDataTypeAndDataScope(String dataType,String dataScope){
        List<Long> list = new ArrayList<>();
        // 账套
        if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
            list.add(OrgInformationUtil.getCurrentSetOfBookId());
        // 公司
        }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
            Long currentCompanyId = OrgInformationUtil.getCurrentCompanyId();
            // 当前
            if("1002".equals(dataScope)) {
                list.add(currentCompanyId);
            // 当前及下属
            }else if("1003".equals(dataScope)){
                Set<Long> companyChildrenIdByCompanyIds = companyService.getCompanyChildrenIdByCompanyId(currentCompanyId);
                list.add(currentCompanyId);
                list.addAll(companyChildrenIdByCompanyIds);
            }
        // 部门
        }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
            Long unitIdByUserId = departmentGroupService.selectByEmpOid(LoginInformationUtil.getCurrentUserOid().toString()).getDepartmentId();
            // 当前
            if("1002".equals(dataScope)) {
                list.add(unitIdByUserId);
            // 当前及下属
            }else if("1003".equals(dataScope)){
                Set<Long> unitChildrenIdByUnitIds = departmentService.listDepartmentChildrenIdById(unitIdByUserId);
                list.add(unitIdByUserId);
                list.addAll(unitChildrenIdByUnitIds);
            }
        // 员工
        }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
            list.add(LoginInformationUtil.getCurrentUserId());
        }
        return list;
    }

    /**
     * 数据权限查询 不分页
     * @param dataAuthorityCode
     * @param dataAuthorityName
     * @return
     */
    public List<DataAuthority> listAllDataAuthorityByCond(String dataAuthorityCode,
                                                      String dataAuthorityName){
        List<DataAuthority> list = dataAuthorityMapper.selectList(new EntityWrapper<DataAuthority>()
                .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityCode), "data_authority_code", dataAuthorityCode)
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityName), "data_authority_name", dataAuthorityName)
                .eq("deleted", false)
                .orderBy("enabled desc,data_authority_code asc"));
        list.stream().map(e -> {
            e.setDataAuthorityName(e.getDataAuthorityCode() + "-" + e.getDataAuthorityName());
            return e;
        }).collect(Collectors.toList());
        return list;
    }
}
